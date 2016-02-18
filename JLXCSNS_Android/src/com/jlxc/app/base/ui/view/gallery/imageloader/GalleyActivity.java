package com.jlxc.app.base.ui.view.gallery.imageloader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jlxc.app.R;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.gallery.bean.ImageFloder;
import com.jlxc.app.base.ui.view.gallery.imageloader.GalleyAdapter.OnItemClickClass;
import com.jlxc.app.base.ui.view.gallery.imageloader.ListImageDirPopupWindow.OnImageDirSelected;
import com.jlxc.app.base.utils.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class GalleyActivity extends BaseActivityWithTopBar implements
		OnImageDirSelected {

	public static final String INTENT_KEY_PHOTO_LIST = "select_result";
	public static final String INTENT_KEY_SELECTED_COUNT = "selected_count";
	public static final String INTENT_KEY_ONE = "one_pic";
	// 所有的图片
	private List<String> mImgs;
	@ViewInject(R.id.layout_gallery_root_view)
	private LinearLayout rootView;
	// 其余相册
	@ViewInject(R.id.layout_galley_bottom_ly)
	private RelativeLayout mBottomLy;
	// 当前的相册
	@ViewInject(R.id.tv_galley_choose_dir)
	private TextView mChooseDir;
	// 照片列表
	@ViewInject(R.id.gridView_galley)
	private GridView mGirdView;
	// 适配器
	private GalleyAdapter mAdapter;
	// 临时的辅助类，用于防止同一个文件夹的多次扫描
	private HashSet<String> mDirPaths = new HashSet<String>();
	// 扫描拿到所有的图片文件夹
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	int totalCount = 0;
	// 存储文件夹中的图片数量
	private int mPicsSize;
	// 默认显示的文件夹
	private File defaultImgDir;
	// 图片最多的文件夹
	private File mImgDir;
	// 屏幕高
	private int mScreenHeight;
	// 弹出框
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	// 已选照片的数量
	private int selectedCount = 0;
	// 是否是只选单张
	private boolean isSingle = false;
	// 完成按钮
	@ViewInject(R.id.base_ll_right_btns)
	private LinearLayout finishBtn;

	@Override
	public int setLayoutId() {
		return R.layout.gallery_activity_main;
	}

	@Override
	protected void setUpView() {
		// 添加完成按钮
		addRightBtn("完成");

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		// 获取已选的数量
		Intent intent = this.getIntent();
		if (intent.hasExtra(INTENT_KEY_SELECTED_COUNT)) {
			selectedCount = intent.getIntExtra(INTENT_KEY_SELECTED_COUNT, 0);
		}
		isSingle = intent.getBooleanExtra(INTENT_KEY_ONE, false);
		if (isSingle) {
			setBarText("选取照片");
		} else {
			setBarText("选取照片 (" + selectedCount + "/9)");
		}

		GalleyAdapter.clearSelectedImageList();
		getImages();
		initEvent();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			hideLoading();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (null != defaultImgDir.list()) {
			mImgs = Arrays.asList(defaultImgDir.list());
		}

		Collections.reverse(mImgs);
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new GalleyAdapter(getApplicationContext(), mImgs,
				R.layout.gallery_grid_item, defaultImgDir.getAbsolutePath(),
				selectedCount);
		// 设置最多
		if (isSingle) {
			mAdapter.setMAX_SELECT_COUNT(1);
		} else {
			mAdapter.setMAX_SELECT_COUNT(9);
		}

		mGirdView.setAdapter(mAdapter);
		mAdapter.setClickCallBack(onItemClickClass);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.gallery_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		showLoading("正在加载...", true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String firstImage = null;
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = GalleyActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}
					FilenameFilter tpFilenameFilter = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					};
					int picSize = 0;
					if (null != parentFile.list(tpFilenameFilter)) {
						picSize = parentFile.list(tpFilenameFilter).length;
					}

					if (picSize > 0) {
						totalCount += picSize;
						imageFloder.setCount(picSize);
						mImageFloders.add(imageFloder);
					} else {
						continue;
					}

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
					// 默认显示的文件夹
					if (imageFloder.getDir().contains("/Camera")) {
						if (imageFloder.getCount() > 0) {
							defaultImgDir = parentFile;
						}
					}
				}
				mCursor.close();
				if (null == defaultImgDir) {
					defaultImgDir = mImgDir;
				}
				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;
				// 通知Handler扫描图片完成x
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
	}

	/**
	 * 事件监听
	 * */
	private void initEvent() {

		// 为底部的布局设置点击事件，弹出popupWindow
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mListImageDirPopupWindow
							.setAnimationStyle(R.style.anim_popup_dir);
					mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0,
							-mBottomLy.getHeight());

					// 设置背景颜色变暗
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = .3f;
					getWindow().setAttributes(lp);
				} catch (Exception e) {
					LogUtils.e("选择相册出错");
				}
			}
		});

		// 完成按钮
		finishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mAdapter) {
					// 点击完成时
					List<String> list = mAdapter.getSelectedImageList();
					if (list.size() > 0) {
						Intent intentFinish = new Intent();
						intentFinish.putExtra(INTENT_KEY_PHOTO_LIST,
								(Serializable) list);
						setResult(Activity.RESULT_OK, intentFinish);
						finish();

						finishBtn.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * 切换文件目录
	 * */
	@Override
	public void selected(ImageFloder floder) {

		defaultImgDir = new File(floder.getDir());
		FilenameFilter tpfFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		};
		if (null != defaultImgDir.list(tpfFilter)) {
			mImgs = Arrays.asList(defaultImgDir.list(tpfFilter));
		}

		Collections.reverse(mImgs);
		// 文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		// 设置最多
		if (isSingle) {
			// 清空
			GalleyAdapter.clearSelectedImageList();
			mAdapter = new GalleyAdapter(getApplicationContext(), mImgs,
					R.layout.gallery_grid_item,
					defaultImgDir.getAbsolutePath(), 0);
			mAdapter.setMAX_SELECT_COUNT(1);
		} else {
			mAdapter = new GalleyAdapter(getApplicationContext(), mImgs,
					R.layout.gallery_grid_item,
					defaultImgDir.getAbsolutePath(), selectedCount);
			mAdapter.setMAX_SELECT_COUNT(9);
		}
		mGirdView.setAdapter(mAdapter);
		mAdapter.setClickCallBack(onItemClickClass);
		mChooseDir.setText(floder.getName().replace("/", ""));
		mListImageDirPopupWindow.dismiss();
	}

	/**
	 * 点击事件
	 * */
	GalleyAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {

		@Override
		public void OnItemClick(int count) {
			if (isSingle) {
				setBarText("选取照片poi~");
			} else {
				setBarText("选取照片 (" + count + "/9)");
			}
		}
	};

	/**
	 * key点击
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
