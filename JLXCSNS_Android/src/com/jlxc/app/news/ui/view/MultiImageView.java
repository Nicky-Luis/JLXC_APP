package com.jlxc.app.news.ui.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jlxc.app.R;
import com.jlxc.app.base.ui.activity.BigImgLookActivity;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.ImageModel;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class MultiImageView extends RelativeLayout {

	// 手机宽度
	private final static int SMALL_PIX = 480;
	private final static int MIDDLE_PIX = 720;
	private final static int LARGE_PIX = 1280;
	// 根布局
	private RelativeLayout rootView;
	// 多图
	private List<ImageView> imageViewsList = new ArrayList<ImageView>();
	// 单图
	private ImageView singleImageView;
	//
	private Context mContext;
	// 屏幕宽度
	private static int screenWidth = 0;
	// 图片源
	private List<ImageModel> dataList = new ArrayList<ImageModel>();
	// 点击事件回调接口
	private JumpCallBack jumpInterface;
	// 是否是大图
	private boolean isLargeSize = true;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	public MultiImageView(Context context) {
		super(context);
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// 获取要显示的尺寸
		TypedArray type = context.obtainStyledAttributes(attrs,
				R.styleable.MultiImageView);
		isLargeSize = type.getBoolean(R.styleable.MultiImageView_islargesize,
				true);
		// 为了保持以后使用该属性一致性,返回一个绑定资源结束的信号给资源
		type.recycle();
		init();
		getWidget();
	}

	/**
	 * 初始化
	 * */
	private void init() {
		// 获取屏幕尺寸
		DisplayMetrics displayMet = getResources().getDisplayMetrics();
		screenWidth = displayMet.widthPixels;
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.image_load_fail)
				.cacheInMemory(true).cacheOnDisk(true)
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
		View view = null;
		// 进行适配
		if (isLargeSize) {
			if (screenWidth <= SMALL_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_small, this);
			} else if (screenWidth > SMALL_PIX && screenWidth <= MIDDLE_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_medium, this);
			} else if (screenWidth > MIDDLE_PIX && screenWidth <= LARGE_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_large, this);
			} else if (screenWidth > LARGE_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_major, this);
			}
		} else {
			if (screenWidth <= SMALL_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_small, this);
			} else if (screenWidth > SMALL_PIX && screenWidth <= MIDDLE_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_small, this);
			} else if (screenWidth > MIDDLE_PIX && screenWidth <= LARGE_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_medium, this);
			} else if (screenWidth > LARGE_PIX) {
				view = View.inflate(mContext,
						R.layout.custom_multi_image_large, this);
			}
		}

		rootView = (RelativeLayout) view
				.findViewById(R.id.layout_multi_pic_root_view);
		singleImageView = (ImageView) view.findViewById(R.id.iv_custom_big_pic);
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
		setClickListener();
	}

	/**
	 * 事件监听
	 * */
	private void setClickListener() {
		singleImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				jumpToBigImage(BigImgLookActivity.INTENT_KEY, dataList.get(0)
						.getURL(), 0);
			}
		});
		// 图片点击事件
		for (int index = 0; index < imageViewsList.size(); index++) {
			imageViewsList.get(index).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					for (int postion = 0; postion < imageViewsList.size(); postion++) {
						if (view.getId() == imageViewsList.get(postion).getId()) {
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
		if (pictureList.size() == 0) {
			// 没有图片的情况
			rootView.setVisibility(View.GONE);
		} else if (pictureList.size() == 1) {
			// 只有一张图片的情况
			rootView.setVisibility(View.VISIBLE);
			singleImageView.setVisibility(View.VISIBLE);
			ImageModel imageModel = pictureList.get(0);

			// 图片尺寸
			int imagHeight = 100;
			int imageWidth = 100;
			if (imageModel.getImageHheight() >= imageModel.getImageWidth()) {
				imagHeight = screenWidth * 3 / 5;
				imageWidth = (int) ((imageModel.getImageWidth() * screenWidth * 3) / (5.0 * imageModel
						.getImageHheight()));
			} else {
				imagHeight = (int) ((imageModel.getImageHheight() * screenWidth * 3) / (5.0 * imageModel
						.getImageWidth()));
				imageWidth = screenWidth * 3 / 5;
			}
			singleImageView.setLayoutParams(new LayoutParams(imageWidth,
					imagHeight));

			imgLoader.displayImage(pictureList.get(0).getSubURL(),
					singleImageView, options);
			// 隐藏其余的imageview
			for (int index = 0; index < imageViewsList.size(); index++) {
				imageViewsList.get(index).setVisibility(View.GONE);
			}
		} else {
			//多张图片
			rootView.setVisibility(View.VISIBLE);
			singleImageView.setVisibility(View.GONE);
			for (int index = 0; index < imageViewsList.size(); index++) {
				if (index < pictureList.size()) {
					imageViewsList.get(index).setVisibility(View.VISIBLE);
					imgLoader.displayImage(pictureList.get(index).getSubURL(),
							imageViewsList.get(index), options);
				} else {
					imageViewsList.get(index).setVisibility(View.GONE);
				}
			}
		}
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
