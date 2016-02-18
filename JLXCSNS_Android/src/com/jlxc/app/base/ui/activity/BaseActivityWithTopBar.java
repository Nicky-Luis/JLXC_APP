package com.jlxc.app.base.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jlxc.app.R;
import com.jlxc.app.demo.ui.activity.MainActivity;

/**
 * 基类Activity，含顶部栏
 * 
 * @author Michael.liu
 * 
 */
public abstract class BaseActivityWithTopBar extends BaseActivity {
	private TextView barTitle;
	private RelativeLayout rlBar;
	private LinearLayout llRightView;
	private TextView backBtn;

	@Override
	protected void loadLayout(View v) {
		barTitle = (TextView) v.findViewById(R.id.base_tv_title);
		rlBar = (RelativeLayout) v.findViewById(R.id.layout_base_title);
		llRightView = (LinearLayout) v.findViewById(R.id.base_ll_right_btns);
		backBtn = (TextView) v.findViewById(R.id.base_tv_back);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finishWithRight();
			}
		});
	}

	protected void hideBackBtn() {
		backBtn.setVisibility(View.GONE);
	}

	protected void setBarText(String title) {
		barTitle.setText(title);
	}

	protected void setBarText(String title, float size, ColorStateList color) {
		barTitle.setText(title);
		barTitle.setTextSize(size);
		barTitle.setTextColor(color);
	}

	protected void setBarColor(int color) {
		rlBar.setBackgroundColor(color);
	}

	/**
	 * 添加文字按钮
	 * */
	protected TextView addRightBtn(String text) {
		View layout = View.inflate(BaseActivityWithTopBar.this,
				R.layout.right_button, null);
		TextView rightBtn = (TextView) layout.findViewById(R.id.btn_right_top);
		rightBtn.setText(text);
		LinearLayout llLayout = (LinearLayout) layout
				.findViewById(R.id.ll_layout);
		LinearLayout.LayoutParams linlLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linlLayoutParams.setMargins(0, 0, 10, 0);
		llLayout.removeAllViews();
		llRightView.removeAllViews();
		llRightView.addView(rightBtn, linlLayoutParams);
		return rightBtn;
	}

	/** 添加图片按钮 */
	protected ImageView addRightImgBtn(int layoutId, int rootLayout, int btnId) {
		View layout = View.inflate(BaseActivityWithTopBar.this, layoutId, null);
		ImageView rightBtn = (ImageView) layout.findViewById(btnId);
		LinearLayout llLayout = (LinearLayout) layout.findViewById(rootLayout);
		llLayout.removeAllViews();
		llRightView.removeAllViews();
		llRightView.addView(rightBtn);
		return rightBtn;
	}

	/**
	 * 跳转到首页
	 */
	public void gotoMainPage() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
