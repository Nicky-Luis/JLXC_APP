package com.jlxc.app.personal.ui.activity;

import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TextView;

import com.jlxc.app.R;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AboutUsActivity extends BaseActivityWithTopBar {

	//版本tv
	@ViewInject(R.id.version_text_view)
	private TextView versionTextView;
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_about;
	}

	@Override
	protected void setUpView() {
		setBarText("关于");
		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			versionTextView.setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

}
