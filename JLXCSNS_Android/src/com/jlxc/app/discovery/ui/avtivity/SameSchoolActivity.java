package com.jlxc.app.discovery.ui.avtivity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
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
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.discovery.model.SameSchoolModel;
import com.jlxc.app.message.helper.MessageAddFriendHelper;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
//同校的人
public class SameSchoolActivity extends BaseActivityWithTopBar {

	//下拉列表
	@ViewInject(R.id.same_school_refresh_list)
	private PullToRefreshListView sameListView;
	//adapter
	HelloHaAdapter<SameSchoolModel> sameAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = true;
	//是否是最后一页
	private boolean isLast = false; 
//	private BitmapUtils bitmapUtils;
	//新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	//当前的页数
	private int currentPage = 1;
	//自己
	private UserModel userModel;
	//source list
	private List<SameSchoolModel> sameSchoolModels;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_same_school;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		setBarText("同校的好友~");
		sameSchoolModels = new ArrayList<SameSchoolModel>();
		userModel = UserManager.getInstance().getUser();
//		bitmapUtils = BitmapManager.getInstance().getHeadPicBitmapUtils(this, R.drawable.default_avatar, true, true);
		
		headImageOptions = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.default_avatar)  
        .showImageOnFail(R.drawable.default_avatar)  
        .cacheInMemory(false)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		initListViewSet();
		getSameData();
	}
	
	////////////////////////////////////private method //////////////////////////////////////
	/***
	 * listview的设置
	 */
	private void initListViewSet() {
		
		//设置内容
		sameAdapter = new HelloHaAdapter<SameSchoolModel>(
				SameSchoolActivity.this, R.layout.same_school_adapter) {
			@SuppressLint("ResourceAsColor") @Override
			protected void convert(final HelloHaBaseAdapterHelper helper,
					final SameSchoolModel item) {
				
				helper.setText(R.id.name_text_view, item.getName());
				helper.setText(R.id.sign_text_view, item.getSign());
				ImageView headImageView = helper.getView(R.id.head_image_view);
//				bitmapUtils.display(headImageView, JLXCConst.ATTACHMENT_ADDR+item.getHead_sub_image());
				if (null != item.getHead_sub_image() && item.getHead_sub_image().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + item.getHead_sub_image(), headImageView, headImageOptions);					
				}else {
					headImageView.setImageResource(R.drawable.default_avatar);
				}
				
				//男的
				if (item.getSex() == UserModel.SexBoy) {
					helper.setImageResource(R.id.sex_image_view, R.drawable.sex_boy);
				}else {
					helper.setImageResource(R.id.sex_image_view, R.drawable.sex_girl);
				}
				
				//添加好友tv
				Button addImageBtn = helper.getView(R.id.add_image_button);
				//点击添加
				addImageBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						IMModel imModel = new IMModel();
						imModel.setTargetId(JLXCConst.JLXC + item.getUid());
						imModel.setAvatarPath(item.getHead_image());
						imModel.setTitle(item.getName());
						addFriend(imModel, helper.getPosition());
					}
				});
				//是否是好友
				if (item.getIs_friend() == 1) {
					addImageBtn.setText("已关注");
					addImageBtn.setBackgroundResource(R.color.main_gary);
					addImageBtn.setTextColor(getResources().getColorStateList(R.color.main_white));
					addImageBtn.setEnabled(false);
				}else {
					addImageBtn.setText("关注");
					addImageBtn.setBackgroundResource(R.color.main_yellow);
					addImageBtn.setTextColor(getResources().getColorStateList(R.color.main_brown));
					addImageBtn.setEnabled(true);
				}
				
				LinearLayout linearLayout = (LinearLayout) helper.getView();
				//点击事件
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//跳转到其他人页面
						Intent intent = new Intent(SameSchoolActivity.this, OtherPersonalActivity.class);
						intent.putExtra(OtherPersonalActivity.INTENT_KEY, item.getUid());
						startActivityWithRight(intent);
					}
				});
				
			}
		};

		// 适配器绑定
		sameListView.setAdapter(sameAdapter);
		sameListView.setMode(Mode.PULL_FROM_START);
		sameListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		sameListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				currentPage = 1;
				getSameData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
//				if (isLast) {
//					CountDownTimer countdownTimer = new CountDownTimer(500, 1000) {
//						@Override
//						public void onTick(long millisUntilFinished) {
//						}
//						@Override
//						public void onFinish() {
//							sameListView.onRefreshComplete();
//						}
//					};
//					// 开始倒计时
//					countdownTimer.start();
//					return;
//				}
//				currentPage++;
//				// 上拉刷新
//				isPullDowm = false;
//				getSameData();
			}

		});

		// 设置底部自动刷新
		sameListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (isLast) {
							sameListView.onRefreshComplete();
							return;
						}
						currentPage++;
						// 底部自动加载
						sameListView.setMode(Mode.PULL_FROM_END);
						sameListView.setRefreshing(true);
						isPullDowm = false;
						getSameData();
					}
				});
		
		// 快宿滑动时不加载图片
		sameListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),
				false, true));
	}
	
	
	/**
	 * 获取动态数据
	 * */
	private void getSameData() {

		String path = JLXCConst.GET_SAME_SCHOOL_LIST + "?" + "user_id=" + userModel.getUid() + 
				"&school_code=" + userModel.getSchool_code() + "&page="+currentPage;
		LogUtils.i(path, 1); 
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
							//最后一页
							if (0 < jResult.getIntValue("is_last")) {
								isLast = true;								
							}else {
								isLast = false;
							}
							
							// 获取动态列表							
							String jsonArrayStr = jResult.getString(JLXCConst.HTTP_LIST);
							List<SameSchoolModel> list = JSON.parseArray(jsonArrayStr, SameSchoolModel.class);
							//如果是下拉刷新
							if (isPullDowm) {
								sameSchoolModels.clear();
								sameSchoolModels.addAll(list);
								sameAdapter.replaceAll(sameSchoolModels);
							}else {
								sameSchoolModels.addAll(list);
								sameAdapter.replaceAll(sameSchoolModels);
							}
							sameListView.onRefreshComplete();
							//是否是最后一页
							if (isLast) {
								sameListView.setMode(Mode.PULL_FROM_START);
							}else {
								sameListView.setMode(Mode.BOTH);
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(SameSchoolActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
							sameListView.onRefreshComplete();
							//是否是最后一页
							if (isLast) {
								sameListView.setMode(Mode.PULL_FROM_START);
							}else {
								sameListView.setMode(Mode.BOTH);
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(SameSchoolActivity.this, "网络有毒=_=");
						sameListView.onRefreshComplete();
						//是否是最后一页
						if (isLast) {
							sameListView.setMode(Mode.PULL_FROM_START);
						}else {
							sameListView.setMode(Mode.BOTH);
						}
					}

				}, null));
	}
	
	
	//添加好友
	private void addFriend(final IMModel imModel, final int index) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser().getUid()+"");
		params.addBodyParameter("friend_id", imModel.getTargetId().replace(JLXCConst.JLXC, "")+"");
		
		showLoading("添加中^_^", false);
		HttpManager.post(JLXCConst.Add_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						
						hideLoading();
						int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
						ToastUtil.show(SameSchoolActivity.this,jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
						
						if (status == JLXCConst.STATUS_SUCCESS) {
							//添加好友
							MessageAddFriendHelper.addFriend(imModel);
							//更新
							SameSchoolModel sameSchoolModel = sameSchoolModels.get(index);
							sameSchoolModel.setIs_friend(1);
							sameAdapter.replaceAll(sameSchoolModels);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(SameSchoolActivity.this,
								"网络异常");
					}
				}, null));
	}
	
	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}	 
}
