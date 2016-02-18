package com.jlxc.app.discovery.ui.avtivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.jlxc.app.discovery.model.FindUserModel;
import com.jlxc.app.message.helper.MessageAddFriendHelper;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//同校的人
public class SearchUserActivity extends BaseActivityWithTopBar {

	// 下拉列表
	@ViewInject(R.id.search_user_refresh_list)
	private PullToRefreshListView searchListView;
	// 搜索et
	@ViewInject(R.id.search_edit_text)
	private EditText searchEditText;
	// 显示helloHa号tv
	@ViewInject(R.id.search_top_text_view)
	private TextView searchTopTextView;
	// adapter
	HelloHaAdapter<FindUserModel> searchAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = true;
	// 是否是最后一页
	private boolean isLast = false;
//	private BitmapUtils bitmapUtils;
	//新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	// 当前的页数
	private int currentPage = 1;
	// 自己
	private UserModel userModel;
	// source list
	private List<FindUserModel> findUserModels;
	// 当前的helloHaID
	private String currentHelloHaID;

	@OnClick({ R.id.search_top_layout })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.search_top_layout:
			// 查找该helloHaId
			searchHaHaId();
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_search_user;
	}

	@Override
	protected void setUpView() {

		headImageOptions = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.default_avatar)  
        .showImageOnFail(R.drawable.default_avatar)  
        .cacheInMemory(false)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		setBarText("查找");
		findUserModels = new ArrayList<FindUserModel>();
		userModel = UserManager.getInstance().getUser();
//		bitmapUtils = BitmapManager.getInstance().getHeadPicBitmapUtils(this,
//				R.drawable.default_avatar, true, true);
		initListViewSet();

		// 设置搜索框内容改变的监听事件
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int start, int before,
					int count) {
				String inputStr = String.valueOf(str);
				// 如果是helloHa号格式
				if (inputStr.trim().matches("^[a-zA-Z0-9]{1,20}+$")) {
					searchTopTextView.setVisibility(View.VISIBLE);
					searchTopTextView.setText("查找HelloHa号："
							+ searchEditText.getText().toString().trim());
					currentHelloHaID = searchEditText.getText().toString()
							.trim();
				} else {
					searchTopTextView.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* 隐藏软键盘 */
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive()) {
				inputMethodManager.hideSoftInputFromWindow(
						SearchUserActivity.this.getCurrentFocus()
								.getWindowToken(), 0);
			}
			showLoading("查找中...", false);
			// 下拉刷新
			isPullDowm = true;
			currentPage = 1;
			// 点击查询
			getSearchData();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	// //////////////////////////////////private
	// method//////////////////////////////////////
	/***
	 * listview的设置
	 */
	private void initListViewSet() {

		searchEditText.setFocusable(true);
		searchEditText.setFocusableInTouchMode(true);
		searchEditText.requestFocus();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) searchEditText
						.getContext().getSystemService(INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(searchEditText, 0);
			}
		}, 500);

		// 设置内容
		searchAdapter = new HelloHaAdapter<FindUserModel>(
				SearchUserActivity.this, R.layout.search_user_adapter) {
			@SuppressLint("ResourceAsColor") @Override
			protected void convert(final HelloHaBaseAdapterHelper helper,
					final FindUserModel item) {

				if (helper.getPosition() == 0) {
					helper.setVisible(R.id.top_text_view, true);
				} else {
					helper.setVisible(R.id.top_text_view, false);
				}

				helper.setText(R.id.name_text_view, item.getName());
				ImageView headImageView = helper.getView(R.id.head_image_view);
//				bitmapUtils.display(headImageView, JLXCConst.ATTACHMENT_ADDR
//						+ item.getHead_sub_image());
				if (null != item.getHead_sub_image() && item.getHead_sub_image().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + item.getHead_sub_image(), headImageView, headImageOptions);					
				}else {
					headImageView.setImageResource(R.drawable.default_avatar);
				}
				// 添加好友tv
				Button addImageBtn = helper.getView(R.id.add_image_button);
				// 点击添加
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

				// 是否是好友
				if (item.getIs_friend() == 1) {
					addImageBtn.setEnabled(false);
					addImageBtn.setBackgroundResource(R.color.main_gary);
					addImageBtn.setTextColor(getResources().getColorStateList(R.color.main_white));
					addImageBtn.setText("已关注");
				} else {
					addImageBtn.setEnabled(true);
					addImageBtn.setBackgroundResource(R.color.main_yellow);
					addImageBtn.setTextColor(getResources().getColorStateList(R.color.main_brown));
					addImageBtn.setText("关注");
				}

				LinearLayout linearLayout = (LinearLayout) helper.getView();
				// 点击事件
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到其他人页面
						Intent intent = new Intent(SearchUserActivity.this,
								OtherPersonalActivity.class);
						intent.putExtra(OtherPersonalActivity.INTENT_KEY,
								item.getUid());
						startActivityWithRight(intent);
					}
				});

			}
		};

		// 适配器绑定
		searchListView.setAdapter(searchAdapter);
		searchListView.setMode(Mode.PULL_FROM_START);
		searchListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		searchListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				currentPage = 1;
				getSearchData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// if (isLast) {
				// CountDownTimer countdownTimer = new CountDownTimer(500, 1000)
				// {
				// @Override
				// public void onTick(long millisUntilFinished) {
				// }
				// @Override
				// public void onFinish() {
				// searchListView.onRefreshComplete();
				// }
				// };
				// // 开始倒计时
				// countdownTimer.start();
				// return;
				// }
				// currentPage++;
				// // 上拉刷新
				// isPullDowm = false;
				// getSearchData();
			}

		});

		// 设置底部自动刷新
		searchListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (isLast) {
							searchListView.onRefreshComplete();
							return;
						}
						currentPage++;
						// 底部自动加载
						searchListView.setMode(Mode.PULL_FROM_END);
						searchListView.setRefreshing(true);
						isPullDowm = false;
						getSearchData();
					}
				});
	}

	/**
	 * 获取动态数据
	 * */
	private void getSearchData() {

		String path = JLXCConst.FIND_USER_LIST + "?" + "user_id="
				+ userModel.getUid() + "&content="
				+ searchEditText.getText().toString().trim() + "&page="
				+ currentPage;
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 最后一页
							if (0 < jResult.getIntValue("is_last")) {
								isLast = true;
							} else {
								isLast = false;
							}

							// 获取动态列表
							String jsonArrayStr = jResult
									.getString(JLXCConst.HTTP_LIST);
							List<FindUserModel> list = JSON.parseArray(
									jsonArrayStr, FindUserModel.class);
							if (list.size() < 1) {
								ToastUtil.show(SearchUserActivity.this, "没这人");
							}

							// 如果是下拉刷新
							if (isPullDowm) {
								findUserModels.clear();
								findUserModels.addAll(list);
								searchAdapter.replaceAll(findUserModels);
							} else {
								findUserModels.addAll(list);
								searchAdapter.addAll(findUserModels);
							}
							searchListView.onRefreshComplete();
							// 是否是最后一页
							if (isLast) {
								searchListView.setMode(Mode.PULL_FROM_START);
							} else {
								searchListView.setMode(Mode.BOTH);
							}

							// 如果是helloHa号格式
							// if
							// (searchEditText.getText().toString().trim().matches(JLXCConst.USER_ACCOUNT_PATTERN))
							// {
							// searchTopTextView.setVisibility(View.VISIBLE);
							// searchTopTextView.setText("查找HelloHa号："+searchEditText.getText().toString().trim());
							// currentHelloHaID =
							// searchEditText.getText().toString().trim();
							// }else {
							// searchTopTextView.setVisibility(View.GONE);
							// }
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(SearchUserActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
							searchListView.onRefreshComplete();
							// 是否是最后一页
							if (isLast) {
								searchListView.setMode(Mode.PULL_FROM_START);
							} else {
								searchListView.setMode(Mode.BOTH);
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(SearchUserActivity.this, "网络有毒=_=");
						searchListView.onRefreshComplete();
						// 是否是最后一页
						if (isLast) {
							searchListView.setMode(Mode.PULL_FROM_START);
						} else {
							searchListView.setMode(Mode.BOTH);
						}
					}

				}, null));
	}

	// 添加好友
	private void addFriend(final IMModel imModel, final int index) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("friend_id",
				imModel.getTargetId().replace(JLXCConst.JLXC, "") + "");

		showLoading("添加中^_^", false);
		HttpManager.post(JLXCConst.Add_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);

						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						ToastUtil.show(SearchUserActivity.this,
								jsonResponse.getString(JLXCConst.HTTP_MESSAGE));

						if (status == JLXCConst.STATUS_SUCCESS) {
							// 添加好友
							MessageAddFriendHelper.addFriend(imModel);
							// 更新
							FindUserModel FindUserModel = findUserModels
									.get(index);
							FindUserModel.setIs_friend(1);
							searchAdapter.replaceAll(findUserModels);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(SearchUserActivity.this, "网络异常");
					}
				}, null));
	}

	// 查找HelloHa号
	private void searchHaHaId() {
		String path = JLXCConst.HELLOHA_ID_EXISTS + "?" + "helloha_id="
				+ currentHelloHaID;
		LogUtils.i(path, 1);
		showLoading("", false);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();

						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);

						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							int uid = jResult.getIntValue("uid");
							// 跳转到其他人页面
							Intent intent = new Intent(SearchUserActivity.this,
									OtherPersonalActivity.class);
							intent.putExtra(OtherPersonalActivity.INTENT_KEY,
									uid);
							startActivityWithRight(intent);
						} else {
							ToastUtil.show(SearchUserActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(SearchUserActivity.this, "网络有毒=_=");
					}

				}, null));

	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getCurrentHelloHaID() {
		return currentHelloHaID;
	}

	public void setCurrentHelloHaID(String currentHelloHaID) {
		this.currentHelloHaID = currentHelloHaID;
	}
}
