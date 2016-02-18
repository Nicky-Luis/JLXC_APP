package com.jlxc.app.message.ui.activity;

import android.content.Intent;

import com.jlxc.app.R;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;


public class ConversationActivity extends BaseActivityWithTopBar {

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_conversation;
	}

	@Override
	protected void setUpView() {
		Intent intent = getIntent();
		setBarText(intent.getData().getQueryParameter("title"));
//		Log.i("MainTabActivity", intent.getData().getQueryParameter("targetId"));
//		Log.i("MainTabActivity", intent.getData().getQueryParameter("title"));
		
	}

	@Override
	public void finishWithRight() {
		// TODO Auto-generated method stub
		super.finishWithRight();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//顶部更新
		Intent messageIntent = new Intent(JLXCConst.BROADCAST_MESSAGE_REFRESH);
		sendBroadcast(messageIntent);
		//底部更新
		Intent tabIntent = new Intent(JLXCConst.BROADCAST_TAB_BADGE);
		sendBroadcast(tabIntent);
	}

}
