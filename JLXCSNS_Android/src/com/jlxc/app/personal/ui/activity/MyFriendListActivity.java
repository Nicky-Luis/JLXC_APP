package com.jlxc.app.personal.ui.activity;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.personal.model.FriendModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//和MyFriendsActivity 共用一套layout
public class MyFriendListActivity extends BaseActivityWithTopBar {

	// 每页加载好友数
	private final int pageCount = 20;
	// 点赞的人的listview
	@ViewInject(R.id.listview_my_friend_list)
	private PullToRefreshListView friendListView;
	// 数据源
	private List<FriendModel> dataList = new ArrayList<FriendModel>();
	// 适配器
	private HelloHaAdapter<FriendModel> friendAdapter;
	// bitmap的处理
//	private static BitmapUtils bitmapUtils;
	// 当前的刷新模式
	private boolean isPullDown = false;
	// 屏幕的尺寸
	private int screenWidth = 0, screenHeight = 0;
	// 当前的数据页
	private int currentPage = 1;
	// 是否是最后一页数据
	private String lastPage = "0";
	//新图片缓存工具 头像
	DisplayImageOptions headImageOptions;

	@Override
	public int setLayoutId() {
		return R.layout.activity_my_friend_list;
	}

	@Override
	protected void setUpView() {
		
        //显示头像的配置  
		headImageOptions = new DisplayImageOptions.Builder()  
                .showImageOnLoading(R.drawable.loading_default)  
                .showImageOnFail(R.drawable.default_avatar)  
                .cacheInMemory(true)  
                .cacheOnDisk(true)  
                .bitmapConfig(Bitmap.Config.RGB_565)  
                .build();
		
		setBarText("我关注的人 ");
		
		init();
		listviewSet();

		isPullDown = true;

		// 同步好友到本地
		syncFriends();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//每次进入页面都更新
		getFriends(String.valueOf(currentPage), String.valueOf(pageCount));
	}

	/**
	 * 初始化
	 * */
	private void init() {

		// 获取屏幕尺寸
		DisplayMetrics displayMet = getResources().getDisplayMetrics();
		screenWidth = displayMet.widthPixels;
		screenHeight = displayMet.heightPixels;
	}

	/**
	 * 初始化BitmapUtils
	 * */
//	private void initBitmapUtils() {
//		bitmapUtils = new BitmapUtils(MyFriendListActivity.this);
//		bitmapUtils.configDefaultBitmapMaxSize(screenWidth, screenHeight);
//		bitmapUtils.configDefaultLoadingImage(R.drawable.default_avatar);
//		bitmapUtils.configDefaultLoadFailedImage(R.drawable.default_avatar);
//		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//	}

	/**
	 * 数据绑定初始化
	 * */
	private void listviewSet() {
		// 设置刷新模式
		friendListView.setMode(Mode.BOTH);

		friendAdapter = new HelloHaAdapter<FriendModel>(
				MyFriendListActivity.this, R.layout.my_friend_list_item_layout,
				dataList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					FriendModel item) {
//				ImageView imgView = helper.getView(R.id.iv_my_friend_list_head);
//				// 设置头像尺寸
//				LayoutParams laParams = (LayoutParams) imgView
//						.getLayoutParams();
//				laParams.width = laParams.height = (screenWidth) / 7;
//				imgView.setLayoutParams(laParams);
//				imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				bitmapUtils
//						.configDefaultBitmapMaxSize(screenWidth, screenWidth);

//				helper.setImageUrl(R.id.iv_my_friend_list_head, bitmapUtils,
//						JLXCConst.ATTACHMENT_ADDR + item.get("head_sub_image"),
//						new HeadBitmapLoadCallBack());
				
				ImageView headImageView = helper.getView(R.id.iv_my_friend_list_head);
				if (null != item.getHead_sub_image() && item.getHead_sub_image().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + item.getHead_sub_image(), headImageView, headImageOptions);					
				}else {
					headImageView.setImageResource(R.drawable.default_avatar);
				}
				
				// 绑定昵称与学校已经关注状态
				helper.setText(R.id.tv_my_friend_name, item.getName());
				helper.setText(R.id.tv_my_friend_school, item.getSchool());
				if (item.isOrHasAttent()) {
					helper.setText(R.id.attent_state_btn, "互相关注");					
				}else {
					helper.setText(R.id.attent_state_btn, "已关注");
				}
				
				final int index = helper.getPosition();
				//点击提示取消关注
				helper.setOnClickListener(R.id.attent_state_btn, new OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteFriendsAlert(index);
					}
				});
			}
		};

		/**
		 * 刷新监听
		 * */
		friendListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉
				currentPage = 1;
				isPullDown = true;
				getFriends(String.valueOf(currentPage),
						String.valueOf(pageCount));
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
//				if (lastPage.equals("0")) {
//					isPullDown = false;
//					getFriends(String.valueOf(currentPage),
//							String.valueOf(pageCount));
//				}
			}
		});

		/**
		 * 设置底部自动刷新
		 * */
		friendListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (lastPage.equals("0")) {
							isPullDown = false;
							friendListView.setMode(Mode.PULL_FROM_END);
							friendListView.setRefreshing(true);
							getFriends(String.valueOf(currentPage),
									String.valueOf(pageCount));
						}
					}
				});

		friendListView.setAdapter(friendAdapter);

		// 单击
		friendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 跳转至个人主页
				Intent intentUsrMain = new Intent(MyFriendListActivity.this,
						OtherPersonalActivity.class);
				intentUsrMain.putExtra(
						OtherPersonalActivity.INTENT_KEY,
						friendAdapter.getItem(position - 1).getUid());
				startActivityWithRight(intentUsrMain);
			}
		});
	}
	
	//删除关注提示
	public void deleteFriendsAlert(final int index) {
		
		FriendModel friend = dataList.get(index);
		final CustomAlertDialog confirmDialog = new CustomAlertDialog(
				this, "确定要取消关注“"+friend.getName()+"”吗？", "确定", "取消");
		confirmDialog.show();
		confirmDialog.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						deleteFriendConfirm(index);
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});	
	}
	
	private void deleteFriendConfirm(int index){
		final int location = index;
		final FriendModel friend = dataList.get(index);
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser().getUid()+"");
		params.addBodyParameter("friend_id", friend.getUid()+"");
		
		showLoading("删除中..", false);
		HttpManager.post(JLXCConst.DELETE_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
						ToastUtil.show(MyFriendListActivity.this,jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
						
						if (status == JLXCConst.STATUS_SUCCESS) {
							RongIMClient.getInstance().removeConversation(ConversationType.PRIVATE, JLXCConst.JLXC+friend.getUid(), null);
							//UI变化
							dataList.remove(location);
							friendAdapter.replaceAll(dataList);
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

	/**
	 * 获取朋友信息
	 * */
	public void getFriends(String page, String size) {
		String path = JLXCConst.GET_ATTENT_LIST + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid() + "&page="
				+ page + "&size=" + size;

		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							friendListView.onRefreshComplete();
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							JSONArray jsonArray = jResult
									.getJSONArray(JLXCConst.HTTP_LIST);
							lastPage = jResult.getString("is_last");
							//模型注入
							jsonToPersonData(jsonArray);
							
							if (lastPage.equals("0")) {
								friendListView.setMode(Mode.BOTH);
								currentPage++;
							} else {
								friendListView.setMode(Mode.PULL_FROM_START);
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							if (lastPage.equals("0")) {
								friendListView.setMode(Mode.BOTH);
							}
							friendListView.onRefreshComplete();
							ToastUtil.show(MyFriendListActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						if (lastPage.equals("0")) {
							friendListView.setMode(Mode.BOTH);
						}
						friendListView.onRefreshComplete();
						ToastUtil.show(MyFriendListActivity.this, "网络好像有点问题");
					}

				}, null));
	}
	//数据模型转换
	private void jsonToPersonData(JSONArray jPersonList) {
		List<FriendModel> list = new ArrayList<FriendModel>();
		for (int index = 0; index < jPersonList.size(); index++) {
			JSONObject jsonObject = jPersonList.getJSONObject(index);
			FriendModel friend = new FriendModel();
			friend.setUid(JLXCUtils.stringToInt(jsonObject.getString("uid")));
			friend.setName(jsonObject.getString("name"));
			friend.setHead_image(jsonObject.getString("head_image"));
			friend.setHead_sub_image(jsonObject.getString("head_sub_image"));
			friend.setSchool(jsonObject.getString("school"));
			if ("0".equals(jsonObject.getString("isAttent"))) {
				friend.setOrHasAttent(false);
			}else {
				friend.setOrHasAttent(true);
			}
			list.add(friend);
		}
		if (isPullDown) {
			dataList = list;
			friendAdapter.replaceAll(dataList);
		} else {
			dataList.addAll(list);
			friendAdapter.replaceAll(dataList);
		}

		if (null != jPersonList) {
			jPersonList.clear();
		}
	}
	
	
	// 同步全部好友到本地
	public void syncFriends() {
		// 判断是否需要同步
		String path = JLXCConst.NEED_SYNC_FRIENDS + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid()
				+ "&friends_count=" + IMModel.findHasAddAll().size();
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
							// 是否需要更新
							int needUpdate = jResult.getIntValue("needUpdate");
							if (needUpdate > 0) {
								// 需要更新好友列表
								isPullDown = true;
								getAllFriends();
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

	// 更新全部
	public void getAllFriends() {

		// 同步
		String path = JLXCConst.GET_ALL_FRIENDS_LIST + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
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
							JSONArray jsonArray = jResult
									.getJSONArray(JLXCConst.HTTP_LIST);
							// 建立模型数组
							for (int i = 0; i < jsonArray.size(); i++) {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);

								String jlxcUid = JLXCConst.JLXC
										+ jsonObject.getIntValue("uid");
								IMModel model = IMModel.findByGroupId(jlxcUid);
								if (null != model) {
									model.setTitle(jsonObject.getString("name"));
									model.setAvatarPath(jsonObject
											.getString("head_image"));
									model.setRemark(jsonObject
											.getString("friend_remark"));
									model.setCurrentState(IMModel.GroupHasAdd);
									model.update();
								} else {
									model = new IMModel();
									model.setType(IMModel.ConversationType_PRIVATE);
									model.setTargetId(jlxcUid);
									model.setTitle(jsonObject.getString("name"));
									model.setAvatarPath(jsonObject
											.getString("head_image"));
									model.setRemark(jsonObject
											.getString("friend_remark"));
									model.setOwner(UserManager.getInstance()
											.getUser().getUid());
									model.setIsNew(0);
									model.setIsRead(1);
									model.setCurrentState(IMModel.GroupHasAdd);
									model.save();
								}

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
}
