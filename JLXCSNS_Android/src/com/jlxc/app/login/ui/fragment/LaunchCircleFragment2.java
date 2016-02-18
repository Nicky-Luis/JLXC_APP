package com.jlxc.app.login.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.jlxc.app.R;
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LaunchCircleFragment2 extends BaseFragment{

	@ViewInject(R.id.launch_image_view)
	private ImageView launchImageView;
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_launch_circle;
	}

	@Override
	public void loadLayout(View rootView) {
		
	}

	@Override
	public void setUpViews(View rootView) {
		launchImageView.setImageResource(R.drawable.guide_page2);
	}
	
}
