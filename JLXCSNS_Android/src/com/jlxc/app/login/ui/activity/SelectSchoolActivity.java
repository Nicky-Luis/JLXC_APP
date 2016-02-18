package com.jlxc.app.login.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.s;
import com.handmark.pulltorefresh.library.ILoadingLayout;
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
import com.jlxc.app.news.model.SchoolModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class SelectSchoolActivity extends BaseActivityWithTopBar {

	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 学校数据列表
	private List<SchoolModel> mDatas = new ArrayList<SchoolModel>();
	// 学校listview的适配器
	private HelloHaAdapter<SchoolModel> schoolAdapter;
	// 父layout
	@ViewInject(R.id.root_layout)
	private LinearLayout rootLayout;
	// 搜索框
	@ViewInject(R.id.search_edittext)
	private EditText searchEditText;
	// 学校列表标头
	@ViewInject(R.id.school_list_title_textview)
	private TextView listTitleTextView;
	// 学校列表listview
	@ViewInject(R.id.school_listview)
	private PullToRefreshListView schoolListView;
	// 提示信息
	@ViewInject(R.id.tv_school_prompt)
	private TextView promptTextView;
	// 用户当前所在的区域编码
	private String districtCode = "110101";
	// 需要搜索的学校名字
	private String schoolName = "";
	// 查询学校时的page值
	private int pageIndex = 1;
	// 定位对象
	private Location districtLocation;
	// 是否下拉刷新
	private boolean isPullDowm = false;
	// 是否是最后一页
	private boolean isLastPage = false;
	// 是否正在请求数据
	private boolean isRequestingData = false;

	// 注册的时候使用或者修改信息的时候使用
	private boolean notRegister;

	@OnClick({ R.id.root_layout })
	public void viewCickListener(View view) {
		switch (view.getId()) {
		case R.id.root_layout:
			// 隐藏输入键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			break;
		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_select_school_layout;
	}

	@Override
	protected void setUpView() {

		setBarText("选择学校");
		// 设置是否是注册进入的
		Intent intent = getIntent();
		setNotRegister(intent.getBooleanExtra("notRegister", false));
		// 初始化listView
		initListViewSet();

		// 设置搜索框内容改变的监听事件
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int start, int before,
					int count) {
				isPullDowm = true;
				schoolName = str.toString();
				if (!schoolName.equals("")) {
					listTitleTextView.setText("搜索到的学校");
				} else {
					listTitleTextView.setText("猜你在这些学校");
				}
				pageIndex = 1;
				getSchoolList(String.valueOf(pageIndex), districtCode,
						schoolName);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 开始定位，每0.2s通知一次，距离变化10通知一次，超时时间为5秒
		districtLocation = new Location(SelectSchoolActivity.this);
		districtLocation.locateInit(200, 10, 5000);
		showLoading("定位中..", true);

		TextView backBtn = (TextView) findViewById(R.id.base_tv_back);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				back();
			}
		});

	}

	/***
	 * 
	 * listview的设置
	 */
	private void initListViewSet() {
		// 设置为底部刷新模式
		schoolListView.setMode(Mode.BOTH);
		schoolAdapter = new HelloHaAdapter<SchoolModel>(
				SelectSchoolActivity.this, R.layout.school_listitem_layout,
				mDatas) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					SchoolModel item) {
				// 学校绑定
				helper.setText(R.id.school_name_textView, item.getSchoolName());
				// 位置绑定
				if (item.getSchoolType().equals(
						SchoolModel.JUNIOR_MIDDLE_SCHOOL)) {
					helper.setText(R.id.school_location_textView,
							item.getCityName() + item.getDistrictName()
									+ " ▪ 初中");
				} else if (item.getSchoolType().equals(
						SchoolModel.SENIOR_MIDDLE_SCHOOL)) {
					helper.setText(R.id.school_location_textView,
							item.getCityName() + item.getDistrictName()
									+ " ▪ 高中");
				} else {
					// 其他
				}
			}
		};

		// 适配器绑定
		schoolListView.setAdapter(schoolAdapter);
		schoolListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		schoolListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				pageIndex = 1;
				getSchoolList(String.valueOf(pageIndex), districtCode,
						schoolName);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isLastPage && !isRequestingData) {
					// 上拉刷新
					isRequestingData = true;
					isPullDowm = false;
					getSchoolList(String.valueOf(pageIndex), districtCode,
							schoolName);
					isRequestingData = false;
				}
			}
		});

		/**
		 * 设置底部自动刷新
		 * */
		schoolListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!isLastPage) {
							// 底部自动加载
							schoolListView.setMode(Mode.PULL_FROM_END);
							schoolListView.setRefreshing(true);
						}
					}
				});
		/**
		 * 设置点击item到事件
		 * */
		schoolListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 隐藏输入键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

				// 更新数据
				updateUserSchool(schoolAdapter.getItem(position - 1)
						.getSchoolName(), schoolAdapter.getItem(position - 1)
						.getSchoolCode());
			}
		});

	}

	/**
	 * 数据转化
	 * */
	private void jsonToSchoolData(List<JSONObject> dataList) {
		List<SchoolModel> newDatas = new ArrayList<SchoolModel>();
		for (JSONObject schoolobj : dataList) {
			SchoolModel tempModel = new SchoolModel();
			tempModel.setContentWithJson(schoolobj);
			newDatas.add(tempModel);
		}
		if (isPullDowm) {
			schoolAdapter.replaceAll(newDatas);
		} else {
			schoolAdapter.addAll(newDatas);
		}
		if (null != dataList) {
			dataList.clear();
		}

		if (schoolAdapter.getCount() == 0) {
			promptTextView.setVisibility(View.VISIBLE);
			promptTextView.setText("然而并没有任何学校  ヽ(.◕ฺˇд ˇ◕ฺ;)ﾉ");
		} else {
			promptTextView.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取学校列表
	 * */
	private void getSchoolList(String page, String districtCode,
			String schoolName) {
		if (districtCode.length() == 0) {
			districtCode = "110101";
		}
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("page", page);
		params.addBodyParameter("district_code", districtCode);
		params.addBodyParameter("school_name", schoolName);

		HttpManager.post(JLXCConst.GET_SCHOOL_LIST, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 获取学校列表
							schoolListView.onRefreshComplete();
							List<JSONObject> objList = (List<JSONObject>) jResult
									.get("list");
							jsonToSchoolData(objList);
							if (jResult.getString("is_last").equals("1")) {
								isLastPage = true;
								schoolListView.setMode(Mode.PULL_FROM_START);
							} else {
								isLastPage = false;
								pageIndex++;
								schoolListView.setMode(Mode.BOTH);
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(SelectSchoolActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
							if (!isLastPage) {
								schoolListView.setMode(Mode.BOTH);
							}
							schoolListView.onRefreshComplete();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(SelectSchoolActivity.this,
								"卧槽，竟然查询失败，检查下网络");
						if (!isLastPage) {
							schoolListView.setMode(Mode.BOTH);
						}
						schoolListView.onRefreshComplete();
					}
				}, null));
	}

	/**
	 * 上传学校数据
	 * */
	private void updateUserSchool(final String schoolName,
			final String schoolCode) {
		final UserModel userModel = UserManager.getInstance().getUser();
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid() + "");
		params.addBodyParameter("school", schoolName);
		params.addBodyParameter("school_code", schoolCode);

		HttpManager.post(JLXCConst.CHANGE_SCHOOL, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							// 设置数据
							userModel.setSchool(schoolName);
							userModel.setSchool_code(schoolCode);
							// 数据持久化
							UserManager.getInstance().saveAndUpdate();

							if (isNotRegister()) {
								finishWithRight();
							} else {
								// 注册进来的 跳转到下一页面
								Intent intent = new Intent(
										SelectSchoolActivity.this,
										RegisterInformationActivity.class);
								startActivityWithRight(intent);
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(SelectSchoolActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
							LogUtils.e("学校上传失败");
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(SelectSchoolActivity.this,
								"卧槽，操作失败，检查下网络");
					}

				}, null));

	}

	/**
	 * 定位获取区域代码
	 * */
	private class Location implements AMapLocationListener {

		private Context mContext;
		private LocationManagerProxy aMapLocManager = null;

		public Location(Context context) {
			mContext = context;
		}

		/**
		 * 初始化定位,通知时间（毫秒），通知距离（米），超时值（毫秒）
		 */
		public void locateInit(long minTime, float minDistance, int timeOutValue) {
			aMapLocManager = LocationManagerProxy.getInstance(mContext);
			aMapLocManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, minTime, minDistance,
					this);
			LogUtils.i("Locate init successed.");
		}

		/**
		 * 停止定位并销毁对象
		 */
		private void stopLocation() {
			if (aMapLocManager != null) {
				aMapLocManager.removeUpdates(this);
				aMapLocManager.destroy();
			}
			aMapLocManager = null;
			LogUtils.i("Locate stop successed.");
		}

		@Override
		public void onLocationChanged(android.location.Location location) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(AMapLocation location) {
			// 隐藏HUD
			hideLoading();
			if (location != null) {
				districtCode = location.getAdCode();
				LogUtils.i("get district Code successed.");
				// 查询区域代码成功后开始查找学校数据
				isPullDowm = false;
				getSchoolList(String.valueOf(pageIndex), districtCode,
						schoolName);
				stopLocation();
			}
		}
	}

	@Override
	protected void onStop() {
		// 停止定位
		districtLocation.stopLocation();
		super.onStop();
	}

	/**
	 * 返回操作
	 * */
	private void back() {
		// 停止定位
		districtLocation.stopLocation();
		// 返回首页
		if (notRegister) {
			finishWithRight();
		} else {
			Intent intent = new Intent(SelectSchoolActivity.this,
					LoginActivity.class);
			startActivityWithRight(intent);
		}
	}

	/**
	 * 重写返回操作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	public boolean isNotRegister() {
		return notRegister;
	}

	public void setNotRegister(boolean notRegister) {
		this.notRegister = notRegister;
	}
}