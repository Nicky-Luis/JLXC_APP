package com.jlxc.app.news.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jlxc.app.R;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.TouchImageView;
import com.jlxc.app.base.utils.FileUtil;
import com.jlxc.app.base.utils.LogUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublisPhotoHandelActivity extends BaseActivityWithTopBar {

	// 删除图片
	public static final String INTENT_KEY_DELETE_URL = "pic_url";
	// 传递过来的图片
	public static final String INTENT_KEY = "Single_Image";
	// 显示图片的imageview
	@ViewInject(R.id.iv_publish_imageview)
	private TouchImageView bigImageView;
	// 删除按钮
	private ImageView deleteBtn;
	// 图片地址
	private String imageURL;

	@Override
	public int setLayoutId() {
		return R.layout.activity_publis_photos_handel;
	}

	@Override
	protected void setUpView() {
		setBarText("");
		// 添加删除按钮
		deleteBtn = addRightImgBtn(R.layout.right_image_button,
				R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intentBack = new Intent();
				intentBack.putExtra(INTENT_KEY_DELETE_URL, imageURL);
				setResult(Activity.RESULT_OK, intentBack);
				finish();
			}
		});
		init();
	}

	/**
	 * 初始化
	 * */
	private void init() {
		// 获取穿过来的地址
		Intent intent = this.getIntent();
		// 传递的是单张图片url
		if (null != intent && intent.hasExtra(INTENT_KEY)) {
			imageURL = intent.getStringExtra(INTENT_KEY);
		} else {
			LogUtils.e("未传递图片地址");
		}

		// bitmap的处理
//		BitmapUtils bitmapUtils = BitmapManager.getInstance().getBitmapUtils(
//				PublisPhotoHandelActivity.this, true, true);
//		bitmapUtils.display(bigImageView, imageURL);
		
		DisplayImageOptions headImageOptions = new DisplayImageOptions.Builder()  
        .cacheInMemory(true)  
        .cacheOnDisk(false)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		ImageLoader.getInstance().displayImage("file://"+imageURL, bigImageView, headImageOptions);
		
	}
	@Override
	public void finishWithRight() {
		// TODO Auto-generated method stub
		finish();
	}

	/**
	 * 重写返回操作
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
