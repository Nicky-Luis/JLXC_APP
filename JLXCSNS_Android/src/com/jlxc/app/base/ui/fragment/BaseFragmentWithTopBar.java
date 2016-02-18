package com.jlxc.app.base.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jlxc.app.R;

public abstract class BaseFragmentWithTopBar extends BaseFragment {
	private TextView barTitle;
	private RelativeLayout rlBar;
	private LinearLayout llRightView;
	private TextView backBtn;

	@Override
	public void loadLayout(View rootView) {
		barTitle = (TextView) rootView.findViewById(R.id.base_tv_title);
		rlBar = (RelativeLayout) rootView.findViewById(R.id.layout_base_title);
		llRightView = (LinearLayout) rootView.findViewById(R.id.base_ll_right_btns);
		backBtn = (TextView) rootView.findViewById(R.id.base_tv_back);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finishWithRight();
			}
		});
	}

	public interface RightBtnClickListener {
		public abstract void onRightBtnClick(View v);
	}

	protected void hideToolBar() {
		rlBar.setVisibility(View.GONE);
	}

	public void hideBackBtn() {
		backBtn.setVisibility(View.GONE);
	}

	public void setBarText(String title) {
		barTitle.setText(title);
	}

	protected void setBarColor(int color) {
		rlBar.setBackgroundColor(color);
	}

	protected void addRightBtn(String text, final RightBtnClickListener rightBtnOnClickListener) {
		View layout = View.inflate(getActivity(), R.layout.right_button, null);
		TextView rightBtn = (TextView) layout.findViewById(R.id.btn_right_top);
		rightBtn.setText(text);
		LinearLayout llLayout = (LinearLayout) layout.findViewById(R.id.ll_layout);
		llLayout.removeAllViews();
		llRightView.removeAllViews();
		llRightView.addView(rightBtn);
		rightBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rightBtnOnClickListener.onRightBtnClick(v);
			}
		});
	}
}
