package com.jlxc.app.message.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.NewsPushModel;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//新好友
public class NewFriendsActivity extends BaseActivityWithTopBar {

	@ViewInject(R.id.new_friend_list_view)
	ListView newFriendListView;
	//adapter
	HelloHaAdapter<IMModel> newFriendAdapter;
//	BitmapUtils bitmapUtils;
	//新图片缓存工具 头像
	DisplayImageOptions headImageOptions;	
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_new_friend_list;
	}

	@Override
	protected void setUpView() {
		
		headImageOptions = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.default_avatar)  
        .showImageOnFail(R.drawable.default_avatar)  
        .cacheInMemory(true)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		setBarText("新的朋友");
//		bitmapUtils = BitmapManager.getInstance().getHeadPicBitmapUtils(this, R.drawable.default_avatar, true, true);
		initListView();
		refreshListView();
		//发送通知
		sendNotify();
	}
	//////////////////////////////private method////////////////////////////////
	private void initListView() {
		//初始化adapter
		newFriendAdapter = new HelloHaAdapter<IMModel>(this, R.layout.new_friend_adapter) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, final IMModel item) {
				//头像
				ImageView imageView = helper.getView(R.id.head_image_view);
				if (null != item.getAvatarPath() && item.getAvatarPath().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + item.getAvatarPath(), imageView, headImageOptions);					
				}else {
					imageView.setImageResource(R.drawable.default_avatar);
				}
				
				//姓名
				helper.setText(R.id.name_text_view, item.getTitle());
//				ImageView unreadImageView = helper.getView(R.id.unread_image_view);
				//是否是新的
//				if (item.getIsRead() == 0) {
//					unreadImageView.setVisibility(View.VISIBLE);
//				}else {
//					unreadImageView.setVisibility(View.GONE);
//				}

				if (null != item.getAddDate() && item.getAddDate().length()>4) {
					//时间
					helper.setText(R.id.time_text_view, TimeHandle.getShowTimeFormat(item.getAddDate()));
				}else {
					helper.setText(R.id.time_text_view, "");
				}
			}
		};
		newFriendListView.setAdapter(newFriendAdapter);
		
		//点击进入详情
		newFriendListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				IMModel imModel = newFriendAdapter.getItem(position);
				Intent intent = new Intent(NewFriendsActivity.this, OtherPersonalActivity.class);
				intent.putExtra(OtherPersonalActivity.INTENT_KEY, JLXCUtils.stringToInt(imModel.getTargetId().replace(JLXCConst.JLXC, "")));
				startActivityWithRight(intent);
			}
		});
		
		//长按删除
		newFriendListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				List<String> menuList = new ArrayList<String>();
				menuList.add("删除内容");
				final CustomListViewDialog confirmDialog = new CustomListViewDialog(
						NewFriendsActivity.this, menuList);
				confirmDialog.setClickCallBack(new ClickCallBack() {

					@Override
					public void Onclick(View view, int which) {
						IMModel imModel = newFriendAdapter.getItem(position);
						imModel.setIsNew(0);
						imModel.update();
						refreshListView(); 
						confirmDialog.dismiss();
					}
				});
				if (null != confirmDialog && !confirmDialog.isShowing()) {
					confirmDialog.show();
				}
				return true;
			}
		});
	}
	
	private void refreshListView() {

		List<IMModel> newFriendList = IMModel.findAllNewFriends();
		newFriendAdapter.replaceAll(newFriendList);
		
		//设置已读
		IMModel.hasRead();
		//通知
		Intent notifyIntent = new Intent(JLXCConst.BROADCAST_TAB_BADGE);
		sendBroadcast(notifyIntent);
	}

//	//添加好友
//	private void addFriend(final IMModel imModel) {
//
//		// 参数设置
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("user_id", UserManager.getInstance().getUser().getUid()+"");
//		params.addBodyParameter("friend_id", imModel.getTargetId().replace(JLXCConst.JLXC, "")+"");
//		
//		showLoading("添加中^_^", false);
//		HttpManager.post(JLXCConst.Add_FRIEND, params,
//				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
//
//					@Override
//					public void onSuccess(JSONObject jsonResponse, String flag) {
//						super.onSuccess(jsonResponse, flag);
//						
//						hideLoading();
//						int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
//						ToastUtil.show(NewFriendsActivity.this,jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
//						
//						if (status == JLXCConst.STATUS_SUCCESS) {
//							//本地数据持久化
//							IMModel newModel = IMModel.findByGroupId(imModel.getTargetId());
//							//如果存在更新
//							if (null != newModel) {
//								newModel.setTitle(imModel.getTitle());
//								newModel.setAvatarPath(imModel.getAvatarPath());
//								newModel.setIsNew(1);
//								newModel.setIsRead(1);
//								newModel.setCurrentState(IMModel.GroupHasAdd);
//								newModel.update();
//							}else {
//								newModel = new IMModel();
//								newModel.setType(IMModel.ConversationType_PRIVATE);
//								newModel.setTargetId(imModel.getTargetId());
//								newModel.setTitle(imModel.getTitle());
//								newModel.setAvatarPath(imModel.getAvatarPath());
//								newModel.setIsNew(1);
//								newModel.setIsRead(1);
//								newModel.setCurrentState(IMModel.GroupHasAdd);
//								newModel.setOwner(UserManager.getInstance().getUser().getUid());
//								newModel.save();
//							}
//							
//							refreshListView();
//						}
//					}
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1,
//							String flag) {
//						super.onFailure(arg0, arg1, flag);
//						hideLoading();
//						ToastUtil.show(NewFriendsActivity.this,
//								"网络异常");
//					}
//				}, null));
//	}
	
	//发送通知
	private void sendNotify() {
		//通知刷新
		Intent tabIntent = new Intent(JLXCConst.BROADCAST_TAB_BADGE);
		sendBroadcast(tabIntent);
		//通知页面刷新
		Intent messageIntent = new Intent(JLXCConst.BROADCAST_NEW_MESSAGE_PUSH);
		sendBroadcast(messageIntent);
		//顶部刷新
		Intent messageTopIntent = new Intent(JLXCConst.BROADCAST_MESSAGE_REFRESH);
		sendBroadcast(messageTopIntent);
	}
	
}
