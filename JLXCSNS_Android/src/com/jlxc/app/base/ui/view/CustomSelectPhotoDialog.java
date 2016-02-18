package com.jlxc.app.base.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jlxc.app.R;

public class CustomSelectPhotoDialog extends Dialog {

	private Context context;
	// 回调接口
	private ClickListenerInterface clickListenerInterface;

	// 事件接口
	public interface ClickListenerInterface {

		public void onSelectGallery();

		public void onSelectCamera();
	}

	public CustomSelectPhotoDialog(Context context) {
		super(context, R.style.alert_dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
	}

	/***
	 * 初始化
	 * */
	public void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(
				R.layout.dialog_custom_select_photos_layout, null);
		setContentView(view);

		LinearLayout btnCamera = (LinearLayout) view
				.findViewById(R.id.tv_btn_camera);
		LinearLayout btnGallery = (LinearLayout) view
				.findViewById(R.id.tv_btn_galley);

		btnCamera.setOnClickListener(new clickListener());
		btnGallery.setOnClickListener(new clickListener());

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// 获取屏幕宽、高用
		DisplayMetrics d = context.getResources().getDisplayMetrics();
		// k度设置为屏幕的0.8
		lp.width = (int) (d.widthPixels * 0.8);
		dialogWindow.setAttributes(lp);
	}

	public void setClicklistener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}

	/**
	 * 回调
	 * */
	private class clickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.tv_btn_galley:
				clickListenerInterface.onSelectGallery();
				break;
			case R.id.tv_btn_camera:
				clickListenerInterface.onSelectCamera();
				break;
			}
		}
	};
}
