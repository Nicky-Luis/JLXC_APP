package com.jlxc.app.login.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jlxc.app.R;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.MainTabActivity;
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.login.ui.activity.LaunchActivity;
import com.jlxc.app.login.ui.activity.LoginActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class LaunchCircleFragment3 extends BaseFragment{

	@ViewInject(R.id.launch_image_view)
	private ImageView launchImageView;
	@ViewInject(R.id.enter_button)
	private ImageButton enterButton;
	
	@OnClick({R.id.enter_button})
	private void methodClick(View view){
		switch (view.getId()) {
		case R.id.enter_button:
			UserModel userModel = UserManager.getInstance().getUser();
			if (null != userModel.getUsername() && null != userModel.getLogin_token()) {
				startActivity(new Intent(getActivity(), MainTabActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			getActivity().finish();
			break;
		default:
			break;
		}
		
	}
	
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
		enterButton.setVisibility(View.VISIBLE);
		launchImageView.setImageResource(R.drawable.guide_page3);
	}
	
}
