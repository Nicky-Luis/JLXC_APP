package com.jlxc.app.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.ConfigUtils;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.news.model.CampusPersonModel;
import com.jlxc.app.news.ui.activity.CampusAllPersonActivity;
import com.jlxc.app.news.ui.activity.CampusNewsActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CampusHomeActivity extends BaseActivityWithTopBar {
	
	//学校ID
	public static final String INTENT_SCHOOL_CODE_KEY = "schoolCode";
	// 学校的人
	private List<CampusPersonModel> personList;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 学校位置
	@ViewInject(R.id.txt_campus_home_school_local)
	private TextView schoolLocationTextView;
	// 学校名字
	@ViewInject(R.id.txt_campus_home_school_name)
	private TextView schoolNameTextView;
	// 学校学生数量
	@ViewInject(R.id.txt_campus_home_mumber_count)
	private TextView studentCountTextView;
	// 未读的消息数量
	@ViewInject(R.id.tv_campus_home_news_count)
	private TextView unreadNewsTextView;
	// 校园的人布局
	@ViewInject(R.id.layout_campus_home_member_rootview)
	private LinearLayout campusMumberLayout;
	// 校园的动态
	@ViewInject(R.id.layout_campus_home_news_rootview)
	private LinearLayout campusNewsLayout;
	// 展示在外边的第一个人
	@ViewInject(R.id.img_campus_home_mumber_A)
	private ImageView OutsidePersonA;
	// 展示在外面的第二个人
	@ViewInject(R.id.img_campus_home_mumber_B)
	private ImageView OutsidePersonB;
	// 展示在外面的第三个人
	@ViewInject(R.id.img_campus_home_mumber_C)
	private ImageView OutsidePersonC;
	// 学校代码
	private String schoolCode;

	@Override
	public int setLayoutId() {
		return R.layout.activity_campus_home_layout;
	}

	@Override
	protected void setUpView() {
		init();
		setWidgetListener();
		// 进入本页面时请求数据
		getCampusHomeData();
	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		setBarText("校园主页");
		// 图片加载初始化
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		// 获取是否是本校学生浏览
		Intent intent = this.getIntent();
		if (intent.hasExtra(INTENT_SCHOOL_CODE_KEY)) {
			schoolCode = intent.getStringExtra(INTENT_SCHOOL_CODE_KEY);
		} else {
			LogUtils.e("未传递查看类型");
		}
		//处理学校
		if (null == schoolCode || schoolCode.length() < 1) {
			schoolCode = UserManager.getInstance().getUser().getSchool_code();
		}
		personList = new ArrayList<CampusPersonModel>();
	}

	/**
	 * 数据绑定
	 * */
	private void schoolHomeData(JSONObject homeJsonObject) {

		// 学校默认自己学校
		schoolNameTextView.setText(UserManager.getInstance().getUser().getSchool());
		
		// 学校位置
		if (homeJsonObject.containsKey("school")) {
			//位置
			JSONObject schoolObject = homeJsonObject.getJSONObject("school");
			String locationString = schoolObject.getString("city_name") + " ▪ "+ schoolObject.getString("district_name");
			schoolLocationTextView.setText(locationString);
			//名字
			if (schoolObject.containsKey("name")) {
				schoolNameTextView.setText(schoolObject.getString("name"));	
			}
		}
		if (homeJsonObject.containsKey("student_count")) {
			// 学校人数
			studentCountTextView.setText(JLXCUtils.stringToInt(homeJsonObject
					.getString("student_count")) + "人");
		}
		if (homeJsonObject.containsKey("unread_news_count")) {
			// 新闻未读tv
			int unreadCount = JLXCUtils.stringToInt(homeJsonObject
					.getString("unread_news_count"));
			if (unreadCount > 0) {
				if (unreadCount > 99) {
					unreadCount = 99;
				}
				unreadNewsTextView.setVisibility(View.VISIBLE);
				unreadNewsTextView.setText(unreadCount + "");
			} else {
				unreadNewsTextView.setVisibility(View.GONE);
			}
			// 非本校学生查看则把动态模块隐藏
			if (null != schoolCode && !schoolCode.equals(UserManager.getInstance().getUser().getSchool_code())) {
				unreadNewsTextView.setVisibility(View.GONE);
			}
		}

		// 解析学校的人
		if (homeJsonObject.containsKey("info")) {
			@SuppressWarnings("unchecked")
			List<JSONObject> JPersonList = (List<JSONObject>) homeJsonObject
					.get("info");
			// 清空
			personList.clear();
			// 解析校园的人
			for (JSONObject personObj : JPersonList) {
				CampusPersonModel tempPerson = new CampusPersonModel();
				tempPerson.setContentWithJson(personObj);
				personList.add(tempPerson);
			}
		}
		// 将头像绑定到imageview上
		if (personList.size() >= 3) {
			OutsidePersonA.setVisibility(View.VISIBLE);
			OutsidePersonB.setVisibility(View.VISIBLE);
			OutsidePersonC.setVisibility(View.VISIBLE);
			imgLoader.displayImage(personList.get(0).getHeadSubImage(),
					OutsidePersonA, options);
			imgLoader.displayImage(personList.get(1).getHeadSubImage(),
					OutsidePersonB, options);
			imgLoader.displayImage(personList.get(2).getHeadSubImage(),
					OutsidePersonC, options);
		} else if (personList.size() >= 2) {
			OutsidePersonA.setVisibility(View.VISIBLE);
			OutsidePersonB.setVisibility(View.VISIBLE);
			OutsidePersonC.setVisibility(View.GONE);
			imgLoader.displayImage(personList.get(0).getHeadSubImage(),
					OutsidePersonA, options);
			imgLoader.displayImage(personList.get(1).getHeadSubImage(),
					OutsidePersonB, options);
		} else if (personList.size() >= 1) {
			OutsidePersonA.setVisibility(View.VISIBLE);
			OutsidePersonB.setVisibility(View.GONE);
			OutsidePersonC.setVisibility(View.GONE);
			imgLoader.displayImage(personList.get(0).getHeadSubImage(),
					OutsidePersonA, options);
		} else {
			OutsidePersonA.setVisibility(View.GONE);
			OutsidePersonB.setVisibility(View.GONE);
			OutsidePersonC.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置事件监听
	 * */
	private void setWidgetListener() {
		campusMumberLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转到所有校友列表页面
				Intent personIntent = new Intent(CampusHomeActivity.this,
						CampusAllPersonActivity.class);
				personIntent.putExtra(CampusAllPersonActivity.INTENT_SCHOOL_CODE_KEY, schoolCode);
				startActivityWithRight(personIntent);
			}
		});

		campusNewsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				unreadNewsTextView.setVisibility(View.GONE);
				// 跳转至校园动态页面
				Intent intentToGroupNews = new Intent();
				intentToGroupNews.setClass(CampusHomeActivity.this,CampusNewsActivity.class);
				intentToGroupNews.putExtra(CampusNewsActivity.INTENT_SCHOOL_CODE_KEY, schoolCode);
				startActivityWithRight(intentToGroupNews);
			}
		});
	}

	/**
	 * 获取学校动态的数据
	 * */
	private void getCampusHomeData() {
		// 上一次查询时间处理
		String lastRefeshTime = ConfigUtils
				.getStringConfig(ConfigUtils.LAST_REFRESH__SCHOOL_HOME_NEWS_DATE);
		if (null == lastRefeshTime || lastRefeshTime.length() < 1) {
			lastRefeshTime = "";
			ConfigUtils.saveConfig(
					ConfigUtils.LAST_REFRESH__SCHOOL_HOME_NEWS_DATE,
					System.currentTimeMillis() / 1000 + "");
		}
		// 1441074913
		String path = JLXCConst.SCHOOL_HOME_DATA + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid()
				+ "&school_code="
				+ schoolCode
				+ "&last_time=" + lastRefeshTime;
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
							// 处理数据
							schoolHomeData(jResult);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(CampusHomeActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(CampusHomeActivity.this,
								"网络抽筋了，请检查(→_→)");
					}

				}, null));
	}
}
