package com.jlxc.app.personal.ui.activity;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.message.model.IMModel;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
////////////////////////该类废弃 改用MyFriendListActivity////////////////////////////////////////////
public class FriendListActivity extends BaseActivityWithTopBar {

	@ViewInject(R.id.list_view)
	private ListView friendListView;
	private HelloHaAdapter<IMModel> friendsAdapter;
//	private BitmapUtils bitmapUtils;
	private List<IMModel> friendList;
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_friend_list;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
//		setBitmapUtils(BitmapManager.getInstance().getHeadPicBitmapUtils(this, R.drawable.default_avatar, true, true));
		initListView();
		//同步好友
		syncFriends();
	}
	
	///////////////////////////override////////////////////////////
	@Override
	public void onResume() {
		super.onResume();
		friendList = IMModel.findHasAddAll();
		friendsAdapter.replaceAll(friendList);
	}
	
	///////////////////////////private method////////////////////////////
	private void initListView() {
		//设置内容
		friendsAdapter = new HelloHaAdapter<IMModel>(
				FriendListActivity.this, R.layout.friend_listitem_adapter) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					final IMModel item) {
				helper.setText(R.id.name_text_view, item.getTitle());
				ImageView headImageView = helper.getView(R.id.head_image_view);
//				bitmapUtils.display(headImageView, JLXCConst.ATTACHMENT_ADDR+item.getAvatarPath());
			}
		};
		friendListView.setAdapter(friendsAdapter);
		friendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				IMModel imModel = friendList.get(position);
				String uid = imModel.getTargetId().replace(JLXCConst.JLXC, "");
				
				//跳转到其他人页面
				Intent intent = new Intent(FriendListActivity.this, OtherPersonalActivity.class);
				intent.putExtra(OtherPersonalActivity.INTENT_KEY, JLXCUtils.stringToInt(uid));
				startActivityWithRight(intent);
			}
		});
	}
	//同步好友
	public void syncFriends() {
		
		//判断是否需要同步
		String path = JLXCConst.NEED_SYNC_FRIENDS + "?" + "user_id=" + UserManager.getInstance().getUser().getUid()
				+ "&friends_count="+IMModel.findHasAddAll().size();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							//是否需要更新
							int needUpdate = jResult.getIntValue("needUpdate");
							if (needUpdate>0) {
								//需要更新好友列表
								getFriends();
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));
	}
	
	public void getFriends() {
		
		//同步
		String path = JLXCConst.GET_FRIENDS_LIST + "?" + "user_id=" + UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT);
							JSONArray jsonArray = jResult.getJSONArray(JLXCConst.HTTP_LIST);
							//建立模型数组
							for (int i = 0; i < jsonArray.size(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								
								String jlxcUid = JLXCConst.JLXC+jsonObject.getIntValue("uid");
								IMModel model = IMModel.findByGroupId(jlxcUid);
								if (null != model) {
									model.setTitle(jsonObject.getString("name"));
									model.setAvatarPath(jsonObject.getString("head_image"));
									model.setRemark(jsonObject.getString("friend_remark"));
									model.setCurrentState(IMModel.GroupHasAdd);
									model.update();
								}else {
									model = new IMModel();
									model.setType(IMModel.ConversationType_PRIVATE);
									model.setTargetId(jlxcUid);
									model.setTitle(jsonObject.getString("name"));
									model.setAvatarPath(jsonObject.getString("head_image"));
									model.setRemark(jsonObject.getString("friend_remark"));
									model.setOwner(UserManager.getInstance().getUser().getUid());
									model.setIsNew(0);
									model.setIsRead(1);
									model.setCurrentState(IMModel.GroupHasAdd);
									model.save();
								}
								
							}
							//刷新
							friendList = IMModel.findHasAddAll();
							friendsAdapter.replaceAll(friendList);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));
	}


}
