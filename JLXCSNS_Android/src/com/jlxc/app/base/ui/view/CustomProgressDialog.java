package com.jlxc.app.base.ui.view;

import com.jlxc.app.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomProgressDialog {

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg,
			boolean cancelble) {

		LayoutInflater inflater = LayoutInflater.from(context);
		// 得到加载view
		View rootView = inflater.inflate(R.layout.dialog_progress_layout, null);
		// 加载布局
		LinearLayout layout = (LinearLayout) rootView
				.findViewById(R.id.layout_progress_dialog_root_view);
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) rootView
				.findViewById(R.id.img_progress_bar);
		// 提示文字
		TextView tipTextView = (TextView) rootView
				.findViewById(R.id.tv_progress_tip);
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.progress_dialog);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		// 设置加载信息
		tipTextView.setText(msg);
		// 创建自定义样式dialog
		Dialog loadingDialog = new Dialog(context, R.style.progess_dialog);

		// 不可以用“返回键”取消
		loadingDialog.setCancelable(cancelble);
		// 设置布局
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;
	}
}
