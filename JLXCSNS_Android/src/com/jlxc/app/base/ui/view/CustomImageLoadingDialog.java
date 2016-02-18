package com.jlxc.app.base.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jlxc.app.R;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CustomImageLoadingDialog {

	/**
	 * 得到自定义的加载大图的Dialog
	 * 
	 * @param context
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_loading_image, null);
		// 加载布局
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		// main.xml中的ImageView
		ImageView loadingImage = (ImageView) v.findViewById(R.id.img_loading);
		// 加载动画
		Animation loadingAnimation = AnimationUtils.loadAnimation(context,
				R.anim.loading_image);
		// 使用ImageView显示动画
		loadingImage.startAnimation(loadingAnimation);
		Dialog loadingDialog = new Dialog(context, R.style.loading_image_dialog);
		// 返回键可以取消
		loadingDialog.setCancelable(true);
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;
	}

}
