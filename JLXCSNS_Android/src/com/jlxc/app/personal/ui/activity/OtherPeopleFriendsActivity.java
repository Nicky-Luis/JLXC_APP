package com.jlxc.app.personal.ui.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.personal.model.OtherPeopleFriendModel;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
//废弃 暂时不用了 使用OtherAttentOrFansActivity代替
public class OtherPeopleFriendsActivity extends BaseActivityWithTopBar {

	public final static String INTENT_KEY = "uid";
	//下拉列表
	@ViewInject(R.id.other_friends_refresh_list)
	private PullToRefreshListView friendListView;
	//adapter
	HelloHaAdapter<OtherPeopleFriendModel> friendAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = false;
	//是否是最后一页
	private boolean isLast = false; 
//	private BitmapUtils bitmapUtils;
	//新图片缓存工具 头像
	private DisplayImageOptions headImageOptions;
	//uid
	private int uid;
	//当前的页数
	private int currentPage = 1;
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_other_people_friends_list;
	}

	@Override
	protected void setUpView() {
		
        //显示头像的配置  
		headImageOptions = new DisplayImageOptions.Builder()  
                .showImageOnLoading(R.drawable.default_avatar)  
                .showImageOnFail(R.drawable.default_avatar)  
                .cacheInMemory(true)  
                .cacheOnDisk(true)  
                .bitmapConfig(Bitmap.Config.RGB_565)  
                .build();
		
		// TODO Auto-generated method stub
		setBarText("TA的朋友们 (｡・`ω´･)");
		Intent intent = getIntent();
		uid = intent.getIntExtra(INTENT_KEY, 0);
		
//		bitmapUtils = new BitmapUtils(this);
//		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.ARGB_8888);
//		bitmapUtils.configMemoryCacheEnabled(true);
//		bitmapUtils.configDiskCacheEnabled(true);
//		bitmapUtils.configDefaultLoadFailedImage(R.drawable.default_avatar);
		initListViewSet();
		getFriendsData();
	}

	
	////////////////////////////////////private method //////////////////////////////////////
	/***
	 * 
	 * listview的设置
	 */
	private void initListViewSet() {
		
		//设置内容
		friendAdapter = new HelloHaAdapter<OtherPeopleFriendModel>(
				OtherPeopleFriendsActivity.this, R.layout.other_friends_listitem_adapter) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					final OtherPeopleFriendModel item) {
				
				helper.setText(R.id.name_text_view, item.getName());
				helper.setText(R.id.school_text_view, item.getSchool());
				ImageView headImageView = helper.getView(R.id.head_image_view);
				
				if (null != item.getHead_sub_image() && item.getHead_sub_image().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + item.getHead_sub_image(), headImageView, headImageOptions);					
				}else {
					headImageView.setImageResource(R.drawable.default_avatar);
				}
				
				LinearLayout linearLayout = (LinearLayout) helper.getView();
				//点击事件
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//跳转到其他人页面
						Intent intent = new Intent(OtherPeopleFriendsActivity.this, OtherPersonalActivity.class);
						intent.putExtra(OtherPersonalActivity.INTENT_KEY, item.getUid());
						startActivityWithRight(intent);
					}
				});
			}
		};

		// 适配器绑定
		friendListView.setAdapter(friendAdapter);
		friendListView.setMode(Mode.PULL_FROM_START);
		friendListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		friendListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				currentPage = 1;
				getFriendsData();
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
//							friendListView.onRefreshComplete();
//						}
//					};
//					// 开始倒计时
//					countdownTimer.start();
//					return;
//				}
//				currentPage++;
//				// 上拉刷新
//				isPullDowm = false;
//				getFriendsData();
			}

		});

		// 设置底部自动刷新
		friendListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (isLast) {
							friendListView.onRefreshComplete();
							return;
						}
						currentPage++;
						// 底部自动加载
						friendListView.setMode(Mode.PULL_FROM_END);
						friendListView.setRefreshing(true);
						isPullDowm = false;
						getFriendsData();
					}
				});
		
		// 快宿滑动时不加载图片
		friendListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),
				false, true));
	}
	
	
	/**
	 * 获取动态数据
	 * */
	private void getFriendsData() {

		String path = JLXCConst.GET_OTHER_FRIENDS_LIST + "?" + "uid=" + uid
				+ "&page="+currentPage;
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
							List<OtherPeopleFriendModel> list = JSON.parseArray(jsonArrayStr, OtherPeopleFriendModel.class);
							
							//如果是下拉刷新
							if (isPullDowm) {
								friendAdapter.replaceAll(list);
							}else {
								friendAdapter.addAll(list);
							}
							friendListView.onRefreshComplete();
							
							//是否是最后一页
							if (isLast) {
								friendListView.setMode(Mode.PULL_FROM_START);
							}else {
								friendListView.setMode(Mode.BOTH);
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(OtherPeopleFriendsActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
							friendListView.onRefreshComplete();
							//是否是最后一页
							if (isLast) {
								friendListView.setMode(Mode.PULL_FROM_START);
							}else {
								friendListView.setMode(Mode.BOTH);
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(OtherPeopleFriendsActivity.this, "网络有毒=_=");
						friendListView.onRefreshComplete();
						//是否是最后一页
						if (isLast) {
							friendListView.setMode(Mode.PULL_FROM_START);
						}else {
							friendListView.setMode(Mode.BOTH);
						}
					}

				}, null));
	}
	 
}
