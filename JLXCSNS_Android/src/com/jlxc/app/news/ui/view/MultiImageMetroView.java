package com.jlxc.app.news.ui.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jlxc.app.R;
import com.jlxc.app.base.ui.activity.BigImgLookActivity;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.ImageModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class MultiImageMetroView extends RelativeLayout {

	// 根布局
	private LinearLayout rootView;
	// 底部布局
	private LinearLayout bottomLayout;
	// 左边布局
	private LinearLayout leftLayout;
	// 右边布局
	private LinearLayout rightLayout;
	// 中间布局
	private LinearLayout middleLayout;
	// 多图
	private List<ImageView> imageViewsList = new ArrayList<ImageView>();
	//
	private Context mContext;
	// 图片源
	private List<ImageModel> dataList = new ArrayList<ImageModel>();
	// 点击事件回调接口
	private JumpCallBack jumpInterface;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	public MultiImageMetroView(Context context) {
		super(context);
	}

	public MultiImageMetroView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
		getWidget();
	}

	/**
	 * 初始化
	 * */
	private void init() {
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.image_load_fail)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 快速滑动时是否加载图片
	 * */
	public void loadImageOnFastSlide(PullToRefreshListView listView,
			boolean isLoad) {
		listView.setOnScrollListener(new PauseOnScrollListener(imgLoader, true,
				isLoad));
	}

	/**
	 * 获取控件
	 * */
	private void getWidget() {
		View view = View.inflate(mContext,
				R.layout.custom_multi_metro_imageview, this);

		// 获取布局
		rootView = (LinearLayout) view
				.findViewById(R.id.layout_multi_image_root_view);
		bottomLayout = (LinearLayout) view
				.findViewById(R.id.layout_multi_pic_bottom);
		leftLayout = (LinearLayout) view
				.findViewById(R.id.layout_multi_pic_left);
		middleLayout = (LinearLayout) view
				.findViewById(R.id.layout_multi_pic_middle);
		rightLayout = (LinearLayout) view
				.findViewById(R.id.layout_multi_pic_right);
		// 获取控件
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_A));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_B));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_C));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_D));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_E));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_F));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_G));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_H));
		imageViewsList.add((ImageView) view
				.findViewById(R.id.iv_custom_picture_I));
	}

	/**
	 * 事件监听
	 * */
	private void setClickListener(final List<ImageView> tempimageViewsList) {
		// 图片点击事件
		for (int index = 0; index < tempimageViewsList.size(); index++) {
			tempimageViewsList.get(index).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View view) {
							for (int postion = 0; postion < tempimageViewsList
									.size(); postion++) {
								if (view.getId() == tempimageViewsList.get(
										postion).getId()) {
									jumpToBigImage(
											BigImgLookActivity.INTENT_KEY_IMG_MODEl_LIST,
											dataList, postion);
									break;
								}
							}
						}
					});
		}
	}

	/**
	 * 绑定数据
	 */
	public void imageDataSet(final List<ImageModel> pictureList) {
		dataList = pictureList;
		// 用于存放将要显示的imageview
		List<ImageView> tempimageViewsList = new ArrayList<ImageView>();
		// 先隐藏
		for (int index = 0; index < imageViewsList.size(); index++) {
			imageViewsList.get(index).setVisibility(View.GONE);
		}
		leftLayout.setVisibility(View.GONE);
		rightLayout.setVisibility(View.GONE);
		bottomLayout.setVisibility(View.GONE);
		middleLayout.setVisibility(View.GONE);
		rootView.setVisibility(View.GONE);

		switch (pictureList.size()) {
		// 只有一张图片
		case 1:
			leftLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			break;
		// 2张图片
		case 2:
			leftLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			tempimageViewsList.add(imageViewsList.get(1));
			break;
		// 3张图片
		case 3:
			rightLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(1));
			tempimageViewsList.add(imageViewsList.get(2));
			tempimageViewsList.add(imageViewsList.get(5));
			break;
		// 4张图片
		case 4:
			leftLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			tempimageViewsList.add(imageViewsList.get(1));
			tempimageViewsList.add(imageViewsList.get(3));
			tempimageViewsList.add(imageViewsList.get(4));
			break;
		// 5张图片
		case 5:
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			tempimageViewsList.add(imageViewsList.get(1));
			tempimageViewsList.add(imageViewsList.get(2));
			tempimageViewsList.add(imageViewsList.get(4));
			tempimageViewsList.add(imageViewsList.get(5));
			break;
		// 6张图片
		case 6:
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			tempimageViewsList.add(imageViewsList.get(1));
			tempimageViewsList.add(imageViewsList.get(2));
			tempimageViewsList.add(imageViewsList.get(3));
			tempimageViewsList.add(imageViewsList.get(4));
			tempimageViewsList.add(imageViewsList.get(5));
			break;
		// 7张图片
		case 7:
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.VISIBLE);
			bottomLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			tempimageViewsList.add(imageViewsList.get(1));
			tempimageViewsList.add(imageViewsList.get(2));
			tempimageViewsList.add(imageViewsList.get(4));
			tempimageViewsList.add(imageViewsList.get(5));
			tempimageViewsList.add(imageViewsList.get(6));
			tempimageViewsList.add(imageViewsList.get(8));
			break;
		// 8张图片
		case 8:
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.VISIBLE);
			bottomLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList.add(imageViewsList.get(0));
			tempimageViewsList.add(imageViewsList.get(1));
			tempimageViewsList.add(imageViewsList.get(2));
			tempimageViewsList.add(imageViewsList.get(3));
			tempimageViewsList.add(imageViewsList.get(4));
			tempimageViewsList.add(imageViewsList.get(5));
			tempimageViewsList.add(imageViewsList.get(7));
			tempimageViewsList.add(imageViewsList.get(8));
			break;
		// 9张图片
		case 9:
			leftLayout.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.VISIBLE);
			bottomLayout.setVisibility(View.VISIBLE);
			middleLayout.setVisibility(View.VISIBLE);
			tempimageViewsList = imageViewsList;

			break;

		default:
			break;
		}

		// 绑定图片
		for (int index = 0; index < tempimageViewsList.size(); index++) {
			tempimageViewsList.get(index).setColorFilter(Color.parseColor("#05000000"));
			if (rootView.getVisibility() == View.GONE) {
				rootView.setVisibility(View.VISIBLE);
			}
			tempimageViewsList.get(index).setVisibility(View.VISIBLE);
			if (tempimageViewsList.size() == 1) {
				// 只有一张图片时显示大图
				imgLoader.displayImage(pictureList.get(index).getURL(),
						tempimageViewsList.get(index), options);
			} else if (tempimageViewsList.size() == 2) {
				// 有两张图片时，第一张显示大图
				if (index == 0) {
					imgLoader.displayImage(pictureList.get(index).getURL(),
							tempimageViewsList.get(index), options);
				} else {
					imgLoader.displayImage(pictureList.get(index).getSubURL(),
							tempimageViewsList.get(index), options);
				}
			} else {
				// 显示缩略图
				imgLoader.displayImage(pictureList.get(index).getSubURL(),
						tempimageViewsList.get(index), options);
			}
		}
		setClickListener(tempimageViewsList);
	}

	/**
	 * 设置跳转回调
	 * 
	 * @param callInterface
	 */
	public void setJumpListener(JumpCallBack callInterface) {
		this.jumpInterface = callInterface;
	}

	/**
	 * 跳转查看大图
	 */
	private void jumpToBigImage(String intentKey, Object path, int index) {

		if (intentKey.equals(BigImgLookActivity.INTENT_KEY)) {
			// 单张图片跳转
			String pathUrl = (String) path;
			Intent intentPicDetail = new Intent(mContext,
					BigImgLookActivity.class);
			intentPicDetail.putExtra(BigImgLookActivity.INTENT_KEY, pathUrl);
			jumpInterface.onImageClick(intentPicDetail);
		} else if (intentKey
				.equals(BigImgLookActivity.INTENT_KEY_IMG_MODEl_LIST)) {
			// 传递model列表
			@SuppressWarnings("unchecked")
			List<ImageModel> mdPath = (List<ImageModel>) path;
			Intent intent = new Intent(mContext, BigImgLookActivity.class);
			intent.putExtra(BigImgLookActivity.INTENT_KEY_IMG_MODEl_LIST,
					(Serializable) mdPath);
			intent.putExtra(BigImgLookActivity.INTENT_KEY_INDEX, index);
			jumpInterface.onImageClick(intent);
		} else if (intentKey.equals(BigImgLookActivity.INTENT_KEY_IMG_LIST)) {
			// 传递String列表
			@SuppressWarnings("unchecked")
			List<String> mdPath = (List<String>) path;
			Intent intent = new Intent(mContext, BigImgLookActivity.class);
			intent.putExtra(BigImgLookActivity.INTENT_KEY_IMG_LIST,
					(Serializable) mdPath);
			intent.putExtra(BigImgLookActivity.INTENT_KEY_INDEX, index);
			jumpInterface.onImageClick(intent);
		} else {
			LogUtils.e("未传递图片地址");
		}

	}

	/**
	 * 点击事件回调接口
	 * */
	public interface JumpCallBack {
		public void onImageClick(Intent intentToimageoBig);
	}
}
