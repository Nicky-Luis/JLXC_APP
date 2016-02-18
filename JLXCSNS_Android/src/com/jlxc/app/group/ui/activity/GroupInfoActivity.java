package com.jlxc.app.group.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupInfoActivity extends BaseActivityWithTopBar {

	public static String INTENT_KEY = "topicId";

	// 图片
	@ViewInject(R.id.group_image_view)
	private ImageView topicImageView;
	// 圈子名
	@ViewInject(R.id.group_name_text_view)
	private TextView topicNameTextView;
	// 圈子内容数量
	@ViewInject(R.id.group_news_count_text_view)
	private TextView topicCountTextView;
	// 活跃程度
	@ViewInject(R.id.active_text_view)
	private TextView activeTextView;
	// 活跃图
	@ViewInject(R.id.active_image_view)
	private ImageView activeImageView;
	// 创建者姓名
	@ViewInject(R.id.create_name_text_view)
	private TextView createNameTextView;
	// 类别名
	@ViewInject(R.id.category_name)
	private TextView categoryNametTextView;
	// 创建者头像
	@ViewInject(R.id.create_head_image_view)
	private ImageView createImageView;
	// 成员数量
	@ViewInject(R.id.member_count_text_view)
	private TextView memberCountTextView;
	// 话题详情
	@ViewInject(R.id.group_description_text_view)
	private TextView topicDescTextView;
	// 操作按钮 关注或者取消关注
	@ViewInject(R.id.btn_group_operate)
	private Button groupOperateButton;
	// 成员A
	@ViewInject(R.id.img_number_A)
	private ImageView memberAImageView;
	// 成员B
	@ViewInject(R.id.img_number_B)
	private ImageView memberBImageView;
	// 成员C
	@ViewInject(R.id.img_number_C)
	private ImageView memberCImageView;
	// 成员D
	@ViewInject(R.id.img_number_D)
	private ImageView memberDImageView;

	// 加载图片
	@SuppressWarnings("unused")
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 话题id
	private int topicId;
	// 是否已经关注了
	private boolean isJoin;
	// 创建人ID
	private int creatorId;
	//类别ID
	private int categoryId;
	//类别名字
	private String categoryName;
	// 成员数组
	private List<TopicMember> topicMembers;

	@OnClick({ R.id.layout_group_info_create_creator,
			R.id.layout_group_info_member, R.id.btn_group_operate, R.id.category_layout})
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 点击创建人item
		case R.id.layout_group_info_create_creator:
			if (creatorId != 0) {
				Intent creatorIntent = new Intent(this, OtherPersonalActivity.class);
				creatorIntent.putExtra(OtherPersonalActivity.INTENT_KEY, creatorId);
				startActivityWithRight(creatorIntent);				
			}
			break;
		// 查看所有所有成员
		case R.id.layout_group_info_member:
			if (topicId != 0) {
				Intent allIntent = new Intent(this, GroupAllPersonActivity.class);
				allIntent.putExtra(GroupAllPersonActivity.INTENT_KEY, topicId);
				startActivityWithRight(allIntent);				
			}
			break;
		// 操作按钮事件
		case R.id.btn_group_operate:
			if (topicId != 0) {
				joinOrExit();				
			}
			break;
		// 类别布局
		case R.id.category_layout:
			if (categoryId != 0) {
				// 跳转至更多圈子列表
				Intent intentToGroupList = new Intent();
				intentToGroupList.setClass(this,MoreGroupListActivity.class);
				intentToGroupList.putExtra(
						MoreGroupListActivity.INTENT_CATEGORY_ID_KEY,
						categoryId);
				intentToGroupList.putExtra(
						MoreGroupListActivity.INTENT_CATEGORY_NAME_KEY,
						categoryName);
				startActivityWithRight(intentToGroupList);				
			}
			break;			
		default:
			break;
		}

	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_group_info;
	}

	@Override
	protected void setUpView() {
		// 获取intent
		Intent intent = getIntent();
		topicId = intent.getIntExtra(INTENT_KEY, 0);

		initImageLoader();
		initWidget();
		// 数据获取
		getGroupInfoData();
	}

	/**
	 * 初始化图片加载工具
	 * */
	private void initImageLoader() {
		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.image_load_fail).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 控件初始化
	 * */
	private void initWidget() {
		setBarText("频道信息");
		// 添加圈子设置按钮
		ImageView groupSetting = addRightImgBtn(R.layout.right_image_button,
				R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		groupSetting.setImageResource(R.drawable.setting_btn);
		groupSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intentGroupSet = new Intent(GroupInfoActivity.this,
						GroupManageActivity.class);
				startActivityWithRight(intentGroupSet);

			}
		});
		// 隐藏设置按钮
		groupSetting.setVisibility(View.GONE);
	}

	/**
	 * 获取群组数据
	 * */
	private void getGroupInfoData() {
		// 获取群组详情

		String path = JLXCConst.GET_TOPIC_DETAIL + "?topic_id=" + topicId
				+ "&user_id=" + UserManager.getInstance().getUser().getUid();
		LogUtils.i(path, 1);
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
							handleResult(jResult);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(GroupInfoActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(GroupInfoActivity.this, "oh no 获取失败...");
					}

				}, null));

	}

	// 处理结果
	private void handleResult(JSONObject result) {
		JSONObject content = result.getJSONObject("content");
		// 名字
		topicNameTextView.setText(content.getString("topic_name"));
		// 内容
		int newsCount = result.getIntValue("news_count");
		topicCountTextView.setText(newsCount + "条内容");
		if (newsCount > 100) {
			activeTextView.setText("热门");
			activeImageView.setImageResource(R.drawable.group_statues_hot);
		} else if (newsCount > 50) {
			activeTextView.setText("中等");
			activeImageView.setImageResource(R.drawable.group_statues_moderate);
		} else {
			activeTextView.setText("冷清");
			activeImageView.setImageResource(R.drawable.group_statues_cold);
		}
		// 图片
		ImageLoader.getInstance().displayImage(
				JLXCConst.ATTACHMENT_ADDR
						+ content.getString("topic_cover_image"),
				topicImageView, options);
		// 创建者
		createNameTextView.setText(content.getString("name"));
		//类别ID
		categoryId = content.getIntValue("category_id");
		categoryName = content.getString("category_name");
		// 类别名
		categoryNametTextView.setText(categoryName);
		creatorId = content.getIntValue("user_id");
		// 创建者头像
		ImageLoader.getInstance()
				.displayImage(
						JLXCConst.ATTACHMENT_ADDR
								+ content.getString("head_sub_image"),
						createImageView, options);
		// 成员数量
		memberCountTextView.setText(result.getString("member_count"));
		// 圈子介绍
		topicDescTextView.setText(content.getString("topic_detail"));
		// 加入状态
		int joinState = result.getInteger("join_state");
		groupOperateButton.setVisibility(View.VISIBLE);
		if (joinState == 1) {
			isJoin = true;
			groupOperateButton.setText("取消关注");
			groupOperateButton.setBackgroundResource(R.drawable.logout_btn);
		} else {
			isJoin = false;
			groupOperateButton.setText("关注");
			groupOperateButton
					.setBackgroundResource(R.drawable.alert_dialog_confirm_btn_normal);
		}
		
		//如果是创建者隐藏
		if (creatorId == UserManager.getInstance().getUser().getUid()) {
			groupOperateButton.setVisibility(View.GONE);
		}

		topicMembers = new ArrayList<GroupInfoActivity.TopicMember>();
		List<ImageView> memberImageViews = new ArrayList<ImageView>();
		memberImageViews.add(memberAImageView);
		memberImageViews.add(memberBImageView);
		memberImageViews.add(memberCImageView);
		memberImageViews.add(memberDImageView);
		// 成员们 最多4个
		JSONArray members = result.getJSONArray("members");
		for (int i = 0; i < members.size(); i++) {
			JSONObject object = members.getJSONObject(i);
			ImageView memberImageView = memberImageViews.get(i);
			// 显示出来
			memberImageView.setVisibility(View.VISIBLE);
			TopicMember member = new TopicMember();
			member.setUser_id(object.getIntValue("user_id"));
			member.setName(object.getString("name"));
			member.setHead_sub_image(object.getString("head_sub_image"));
			topicMembers.add(member);
			ImageLoader.getInstance().displayImage(
					JLXCConst.ATTACHMENT_ADDR + member.getHead_sub_image(),
					memberImageView, options);
		}

	}

	// 加入或者退出
	private void joinOrExit() {
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("topic_id", topicId + "");
		// 路径
		String path = JLXCConst.JOIN_TOPIC;
		if (isJoin) {
			path = JLXCConst.QUIT_TOPIC;
			showLoading("取消关注中..", false);
		} else {
			showLoading("关注中..", false);
		}
		HttpManager.post(path, params, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							ToastUtil.show(GroupInfoActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
							// 更新UI及其界面状态
							if (isJoin) {
								isJoin = false;
								groupOperateButton.setText("关注");
								groupOperateButton
										.setBackgroundResource(R.drawable.alert_dialog_confirm_btn_normal);
							} else {
								isJoin = true;
								groupOperateButton.setText("取消关注");
								groupOperateButton
										.setBackgroundResource(R.drawable.logout_btn);
							}
						}
						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(GroupInfoActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(GroupInfoActivity.this, "网络异常");
					}
				}, null));
	}

	@SuppressWarnings("unused")
	private class TopicMember {
		private int user_id;
		private String name;
		private String head_sub_image;

		public int getUser_id() {
			return user_id;
		}

		public void setUser_id(int user_id) {
			this.user_id = user_id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHead_sub_image() {
			return head_sub_image;
		}

		public void setHead_sub_image(String head_sub_image) {
			this.head_sub_image = head_sub_image;
		}

	}
}
