package com.jlxc.app.discovery.ui.avtivity;

import com.jlxc.app.R;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;

public class DiscoveryHomeActivity extends BaseActivityWithTopBar {

	//intent key 
//	public static String SCHOOL_CODE = "schoolCode";
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_discovery_main;
	}

	@Override
	protected void setUpView() {
		setBarText("找同学");
	}

}
