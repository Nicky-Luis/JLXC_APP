package com.jlxc.app.group.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.GroupCategoryModel;
import com.jlxc.app.group.model.GroupTopicModel;
import com.jlxc.app.group.ui.activity.CreateGroupActivity;
import com.jlxc.app.group.ui.activity.GroupNewsActivity;
import com.jlxc.app.group.ui.activity.MoreGroupListActivity;
import com.jlxc.app.group.view.GroupMenuPopWindow;
import com.jlxc.app.group.view.GroupMenuPopWindow.CategorySelectListener;
import com.jlxc.app.group.view.LoopViewPager;
import com.jlxc.app.group.view.OperatePopupWindow;
import com.jlxc.app.group.view.OperatePopupWindow.OperateListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupMainFragment extends BaseFragment {

	// 标题布局
	@ViewInject(R.id.layout_discovey_group_title)
	private LinearLayout titleLayout;
	// 类别名字
	@ViewInject(R.id.text_discovery_group_title)
	private TextView groupCategory;
	// 标头根布局
	@ViewInject(R.id.layout_title_rootview)
	private FrameLayout titleRootLayout;
	// 标题背景遮罩
	@ViewInject(R.id.view_title_background)
	private View titlebackView;
	// 标题图标
	@ViewInject(R.id.img_title_icon)
	private ImageView imgTitleIcon;
	// 创建新的圈子
	@ViewInject(R.id.btn_more_operate)
	private ImageButton moreOperate;
	// 可循环滑动的viewpage
	@ViewInject(R.id.loop_view_page_group)
	private LoopViewPager groupViewPage;
	// 上下文信息
	private Context mContext;
	// 加载图片
	@SuppressWarnings("unused")
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 菜单窗口
	private GroupMenuPopWindow menuPopWindow;
	// 操作窗口
	private OperatePopupWindow operatePopWindow;
	// 圈子信息数据
	private List<GroupTopicModel> groupList = new ArrayList<GroupTopicModel>();
	// 类型list
	private List<GroupCategoryModel> categoryModels = new ArrayList<GroupCategoryModel>();
	// 当前的categoryId
	private int categoryId = 0;
	// 当前的category名字
	private String categoryName = "话题频道";
	// 记录前一状态下title的颜色
	private int lastTitleBackColor = 0xffffc400;
	// 每个item的颜色
	private int[] colorList = new int[] { 0xFFFF9966, 0xFFFFcc66, 0xFFb6b6b6,
			0xFF99ccFF, 0xFF61a8dd, 0xFF43ba8f, 0xFFffcc99 };

	@Override
	public int setLayoutId() {
		return R.layout.fragment_discovery_group_layout;
	}

	@Override
	public void loadLayout(View rootView) {
	}

	@Override
	public void setUpViews(View rootView) {
		init();
		// 首次更新数据
		// getRecommentData("参数", "参数");
		// groupViewPage.setAdapter(new MyPagerAdapter());
		//初始化broadcast
		initBoradcastReceiver();
		getRecommendData(0);
	}

	/**
	 * 点击事件监听
	 * */
	@OnClick(value = { R.id.btn_more_operate, R.id.layout_discovey_group_title })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 话题菜单
		case R.id.layout_discovey_group_title:
			// 显示类别列表
			choiceCategory();
			// 设置背景颜色变暗
			// WindowManager.LayoutParams lp = getActivity().getWindow()
			// .getAttributes();
			// lp.alpha = .3f;
			// getActivity().getWindow().setAttributes(lp);
			break;

		case R.id.btn_more_operate:
			operatePopWindow.showPopupWindow(moreOperate);
			break;
		}
	}

	// 选择类别
	private void choiceCategory() {
		// 存在直接用
		if (categoryModels.size() > 0) {
			showCategoryList();
		} else {
			// showLoading(getActivity(), "获取中..", true);
			// 不存在获取一次
			String path = JLXCConst.GET_TOPIC_CATEGORY;
			HttpManager.get(path, new JsonRequestCallBack<String>(
					new LoadDataHandler<String>() {

						@Override
						public void onSuccess(JSONObject jsonResponse,
								String flag) {
							super.onSuccess(jsonResponse, flag);
							hideLoading();
							int status = jsonResponse
									.getInteger(JLXCConst.HTTP_STATUS);
							if (status == JLXCConst.STATUS_SUCCESS) {
								// 已经有了
								if (categoryModels.size() > 0) {
									showCategoryList();
									return;
								}
								JSONObject jResult = jsonResponse
										.getJSONObject(JLXCConst.HTTP_RESULT);
								JSONArray categoryArray = jResult
										.getJSONArray(JLXCConst.HTTP_LIST);
								// 第一条item的值
								GroupCategoryModel allCategoryModel = new GroupCategoryModel();
								allCategoryModel.setCategory_id(0);
								allCategoryModel
										.setCategory_desc("全宇宙的事情都在这里讨论");
								allCategoryModel.setCategory_name("话题频道");
								allCategoryModel.setCategory_cover("NULL");
								allCategoryModel
										.setBackgroundValue(colorList[0]);
								categoryModels.add(0, allCategoryModel);
								// 模型拼装
								for (int i = 0; i < categoryArray.size(); i++) {
									JSONObject object = categoryArray
											.getJSONObject(i);
									GroupCategoryModel model = new GroupCategoryModel();
									model.setCategory_id(object
											.getIntValue("category_id"));
									model.setCategory_name(object
											.getString("category_name"));
									model.setCategory_cover(object
											.getString("category_cover"));
									model.setCategory_desc(object
											.getString("category_desc"));
									model.setBackgroundValue(colorList[i + 1]);
									categoryModels.add(model);
								}

								showCategoryList();
							}

							if (status == JLXCConst.STATUS_FAIL) {
								ToastUtil.show(getActivity(), "获取失败,请重试");
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1,
								String flag) {
							hideLoading();
							super.onFailure(arg0, arg1, flag);
							ToastUtil.show(getActivity(), "获取失败,请重试");
						}

					}, null));
		}
	}

	private void showCategoryList() {
		menuPopWindow.setCategoryList(categoryModels);
		menuPopWindow.showPopupWindow(titleRootLayout);
		// 设置缩放动画
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 1.0f, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(200);
		animation.setFillAfter(true);
		imgTitleIcon.clearAnimation();
		imgTitleIcon.setAnimation(animation);
		animation.startNow();
		//隐藏操作按钮
		moreOperate.setVisibility(View.GONE);
	}
	
	/**
	 * 初始化广播信息
	 * */
	private void initBoradcastReceiver() {
		LocalBroadcastManager mLocalBroadcastManager;
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(getActivity());
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(JLXCConst.BROADCAST_NEW_TOPIC_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				myIntentFilter);
	}
	
	/**
	 * 广播接收处理
	 * */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent resultIntent) {
			String action = resultIntent.getAction();
			if (action.equals(JLXCConst.BROADCAST_NEW_TOPIC_REFRESH)) {
				if (resultIntent.hasExtra(CreateGroupActivity.NEW_TOPIC_OK)) {
					GroupTopicModel topicModel = (GroupTopicModel) resultIntent.getSerializableExtra(CreateGroupActivity.NEW_TOPIC_OK);
					groupList.add(0, topicModel);
					groupViewPage.setAdapter(new MyPagerAdapter());
				}
			}
		}
	};

	/**
	 * 频道菜单的初始化
	 * */
	private void initPopupWindow() {
		menuPopWindow = new GroupMenuPopWindow(mContext);
		menuPopWindow.setListener(new CategorySelectListener() {
			@Override
			public void select(GroupCategoryModel model) {
				categoryId = model.getCategory_id();
				categoryName = model.getCategory_name();
				getRecommendData(model.getCategory_id());
				menuPopWindow.dismiss();
				groupCategory.setText(model.getCategory_name());
			}

			@Override
			public void onFirstVisibleItemChange(int index) {
				titleRootLayout.setBackgroundColor(lastTitleBackColor);
				lastTitleBackColor = colorList[index];
				titleLayout.setBackgroundColor(0x00000000);
				titlebackView.setBackgroundColor(colorList[index]);
				// 设置颜色渐变
				Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
				alphaAnimation.setDuration(500);
				titlebackView.startAnimation(alphaAnimation);
			}
		});
		// 监听消失事件
		menuPopWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// 缩放动画
				ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f,
						-1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setDuration(200);
				imgTitleIcon.clearAnimation();
				imgTitleIcon.setAnimation(animation);
				animation.startNow();
				// 设置背景颜色变亮
				// WindowManager.LayoutParams lp = getActivity().getWindow()
				// .getAttributes();
				// lp.alpha = 1.0f;
				// getActivity().getWindow().setAttributes(lp);
				// 恢复背景颜色
				titleRootLayout.setBackgroundColor(0xFFffc400);
				titlebackView.setBackgroundColor(0x00000000);
				titleLayout
						.setBackgroundResource(R.drawable.selector_main_yellow_click);
				moreOperate.setVisibility(View.VISIBLE);
			}
		});

		// 操作菜单监听
		operatePopWindow = new OperatePopupWindow(mContext);
		operatePopWindow.setListener(new OperateListener() {

			@Override
			public void createClick() {
				// 创建页面
				Intent createGroupList = new Intent();
				createGroupList.setClass(mContext, CreateGroupActivity.class);
				startActivityWithRight(createGroupList);
			}

			@Override
			public void lookMoreGroup() {
				// 跳转至更多圈子列表
				Intent intentToGroupList = new Intent();
				intentToGroupList.setClass(mContext,
						MoreGroupListActivity.class);
				intentToGroupList.putExtra(
						MoreGroupListActivity.INTENT_CATEGORY_ID_KEY,
						categoryId);
				intentToGroupList.putExtra(
						MoreGroupListActivity.INTENT_CATEGORY_NAME_KEY,
						categoryName);
				startActivityWithRight(intentToGroupList);
			}
		});
	}

	/**
	 * 初始化
	 * */
	private void init() {
		mContext = this.getActivity().getApplicationContext();

		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		initPopupWindow();
	}

	/**
	 * 获取发现部分的数据 全看的时候topicId为0
	 * */
	private void getRecommendData(int categoryId) {

		String path = JLXCConst.GET_TOPIC_HOME_LIST + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid()
				+ "&category_id=" + categoryId;
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 获取数据列表
							List<JSONObject> JPersonList = (List<JSONObject>) jResult
									.get("list");
							JsonToItemData(JPersonList);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(mContext, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(mContext, "网络抽筋了,请检查 =_=");
					}

				}, null));
	}

	/**
	 * 数据解析
	 * */
	private void JsonToItemData(List<JSONObject> dataList) {
		if (dataList.size() < 1) {
			return;
		}
		groupList.clear();
		for (JSONObject jsonObject : dataList) {
			// 数据处理
			GroupTopicModel topicModel = new GroupTopicModel();
			topicModel.setTopic_id(jsonObject.getIntValue("topic_id"));
			topicModel.setTopic_cover_image(jsonObject
					.getString("topic_cover_image"));
			topicModel.setTopic_name(jsonObject.getString("topic_name"));
			topicModel.setTopic_detail(jsonObject.getString("topic_detail"));
			topicModel.setNews_count(jsonObject.getIntValue("news_count"));
			topicModel.setMember_count(jsonObject.getIntValue("member_count"));

			groupList.add(topicModel);
		}

		groupViewPage.setAdapter(new MyPagerAdapter());
		groupViewPage.setOnPageChangeListener(new ChangeColorListener());
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View groupPageView = View.inflate(mContext,
					R.layout.group_page_layout, null);
			// 数据模型
			final GroupTopicModel topicModel = groupList.get(position);

			// 名字
			TextView topicNameTextView = (TextView) groupPageView
					.findViewById(R.id.topic_name_text_view);
			topicNameTextView.setText(topicModel.getTopic_name());
			// 描述
			TextView topicDescTextView = (TextView) groupPageView
					.findViewById(R.id.topic_desc_text_view);
			topicDescTextView.setText(topicModel.getTopic_detail());
			// 背景图
			ImageView topicImageView = (ImageView) groupPageView
					.findViewById(R.id.topic_image);
			ImageLoader.getInstance().displayImage(
					JLXCConst.ATTACHMENT_ADDR
							+ topicModel.getTopic_cover_image(),
					topicImageView, options);
			// 成员数量
			TextView memberTextView = (TextView) groupPageView
					.findViewById(R.id.member_count_text_view);
			memberTextView.setText(topicModel.getMember_count() + "人关注");
			// 内容数量
			TextView newsTextView = (TextView) groupPageView
					.findViewById(R.id.news_count_text_view);
			newsTextView.setText(topicModel.getNews_count() + "条内容");
			container.addView(groupPageView);

			// 点击事件
			groupPageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 跳转至圈子内容部分
					Intent intentToGroupNews = new Intent();
					intentToGroupNews.setClass(getActivity(),
							GroupNewsActivity.class);
					// 传递名称
					intentToGroupNews.putExtra(
							GroupNewsActivity.INTENT_KEY_TOPIC_NAME,
							topicModel.getTopic_name());
					// 传递ID
					intentToGroupNews.putExtra(
							GroupNewsActivity.INTENT_KEY_TOPIC_ID,
							topicModel.getTopic_id());
					startActivityWithRight(intentToGroupNews);
				}
			});

			return groupPageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			// 返回page的数量
			return groupList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	// 滚动变色监听器
	@SuppressLint("NewApi")
	private class ChangeColorListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int index) {

		}

	}

	public int getRandomExcept(int RandMax, int exceptNums) {
		Random rand = new Random();
		int num = rand.nextInt(RandMax);
		while (true) {
			int have = 0;
			if (num == exceptNums) {
				have = 1;
			}
			if (have == 0) {
				return num;
			}
			num = rand.nextInt(RandMax);
		}
	}
}
