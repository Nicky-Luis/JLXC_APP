package com.jlxc.app.group.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jlxc.app.R;

/**
 * 下拉操作菜单
 * */
public class OperatePopupWindow extends PopupWindow {

	// 布局
	private View conentView;
	// onclick
	private OperateListener listener;

	@SuppressLint("NewApi")
	public OperatePopupWindow(final Context context) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater
				.inflate(R.layout.popup_window_group_operate, null);
		// 创建部分的设置
		LinearLayout createNewLayout = (LinearLayout) conentView
				.findViewById(R.id.layout_create_new_group);
		createNewLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.createClick();
				OperatePopupWindow.this.dismiss();
			}
		});

		// 更多频道部分的设置
		LinearLayout moreGroupLayout = (LinearLayout) conentView
				.findViewById(R.id.layout_group_list);
		moreGroupLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.lookMoreGroup();
				OperatePopupWindow.this.dismiss();
			}
		});

		// 设置PopupWindow的View
		this.setContentView(conentView);
		// 设置窗体的尺寸
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 背景变暗
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.anim_group_operate);
	}

	/**
	 * 显示窗体
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}

	// 监听器
	public interface OperateListener {
		// 创建
		public void createClick();

		// 更多
		public void lookMoreGroup();
		// 类别
	}

	public OperateListener getListener() {
		return listener;
	}

	public void setListener(OperateListener listener) {
		this.listener = listener;
	}
}
