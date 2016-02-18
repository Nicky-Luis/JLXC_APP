package com.jlxc.app.personal.ui.activity;


import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.message.model.IMModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class FriendSettingActivity extends BaseActivityWithTopBar {
	
	public static final String INTENT_FUID = "fuid";
	public static final String INTENT_NAME = "name";
	//用户id
	private int friend_uid;
	//姓名
	private String name;
	
	@ViewInject(R.id.delete_friend_text_view)
	private TextView deleteFriendTextView;
	
	@OnClick({R.id.delete_friend_layout})
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.delete_friend_layout:
			deleteFriend();
			break;
		default:
			break;
		}
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_friend_setting;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		friend_uid = intent.getIntExtra(INTENT_FUID, 0);
		name = intent.getStringExtra(INTENT_NAME);
		
	}

	//////////////////////////////private method//////////////////////////////
	private void deleteFriend() {
		new AlertDialog.Builder(this).setTitle("确定要删除好友"+getName()+"吗").setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 参数设置
				RequestParams params = new RequestParams();
				params.addBodyParameter("user_id", UserManager.getInstance().getUser().getUid()+"");
				params.addBodyParameter("friend_id", getFriend_uid()+"");
				
				showLoading("删除中..", false);
				HttpManager.post(JLXCConst.DELETE_FRIEND, params,
						new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

							@Override
							public void onSuccess(JSONObject jsonResponse, String flag) {
								super.onSuccess(jsonResponse, flag);
								hideLoading();
								int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
								ToastUtil.show(FriendSettingActivity.this,jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
								
								if (status == JLXCConst.STATUS_SUCCESS) {
									//本地删除
									IMModel imModel = new IMModel();
									imModel.setTargetId(JLXCConst.JLXC+getFriend_uid());
									imModel.setOwner(UserManager.getInstance().getUser().getUid());
									imModel.remove();
									RongIMClient.getInstance().removeConversation(ConversationType.PRIVATE, imModel.getTargetId(), null);
								}
							}

							@Override
							public void onFailure(HttpException arg0, String arg1,
									String flag) {
								super.onFailure(arg0, arg1, flag);
								hideLoading();
							}
						}, null));
			}
		}).setNegativeButton("取消", null).show();
	}
	
	//////////////////////////////getter setter//////////////////////////////
	public int getFriend_uid() {
		return friend_uid;
	}

	public void setFriend_uid(int friend_uid) {
		this.friend_uid = friend_uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
