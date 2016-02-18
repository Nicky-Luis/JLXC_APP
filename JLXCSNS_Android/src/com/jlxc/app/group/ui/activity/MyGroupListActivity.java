package com.jlxc.app.group.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
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
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.GroupTopicModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 圈子列表
 * */
public class MyGroupListActivity extends BaseActivityWithTopBar {

	// 圈子的相关信息
	// private final static String GROUP_TYPE = "group_type";
	// private final static String GROUP_NAME = "group_name";
	// private final static String GROUP_MEMBER = "group_member";
	// private final static String GROUP_COVER_IMG = "group_cover_image";
	// private final static String GROUP_UNREAD_COUNT = "group_unread_msg";
	// 圈子类别
	// private final static String GROUP_TYPE_SCHOOL = "school";
	// private final static String GROUP_TYPE_ATTENTION = "attention";
	// private final static String GROUP_TYPE_CREATE = "create";

	// 动态listview
	@ViewInject(R.id.listview_my_group)
	private PullToRefreshListView groupListView;
	// 提示信息
	@ViewInject(R.id.txt_my_group_prompt)
	private TextView myGroupPrompt;
	// 动态列表适配器
	private HelloHaAdapter<GroupTopicModel> groupAdapter = null;
	// 用户关注的圈子信息数据
	private List<GroupTopicModel> groupList = new ArrayList<GroupTopicModel>();
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	@Override
	public int setLayoutId() {
		return R.layout.activity_my_group_list;
	}

	@Override
	protected void setUpView() {
		// 设置标头
		setBarText("我的频道");
		// 初始化
		initialize();
		newsListViewSet();
		// 获取我的圈子列表
		getMyTopicList();
	}

	/**
	 * 数据的初始化
	 * */
	private void initialize() {

		itemViewClickListener = new ItemViewClick();
		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.image_load_fail)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * listView 的设置
	 * */
	private void newsListViewSet() {
		// 设置刷新模式
		groupListView.setMode(Mode.DISABLED);
		/**
		 * 刷新监听
		 * */
		groupListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {

			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {

			}
		});

		/**
		 * adapter的设置
		 * */
		groupAdapter = new HelloHaAdapter<GroupTopicModel>(
				MyGroupListActivity.this, R.layout.listitem_my_group_layout,
				groupList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					GroupTopicModel item) {
				// 关注的圈子
				imgLoader.displayImage(
						JLXCConst.ATTACHMENT_ADDR
								+ item.getTopic_cover_sub_image(),
						(ImageView) helper.getView(R.id.img_my_group_icon),
						options);
				helper.setText(R.id.txt_my_group_name, item.getTopic_name());
				helper.setText(R.id.txt_group_member_count,
						item.getMember_count() + "人关注");

				int unread = item.getUnread_news_count();
				if (unread > 0) {
					helper.setVisible(R.id.txt_my_group_unread_news_count, true);
					helper.setText(R.id.txt_my_group_unread_news_count, unread
							+ "");
				} else {
					helper.setVisible(R.id.txt_my_group_unread_news_count,
							false);
				}

				// 设置事件监听
				final int postion = helper.getPosition();
				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View view) {
						itemViewClickListener.onClick(view, postion,
								view.getId());
					}
				};
				helper.setOnClickListener(R.id.layout_group_Listitem_rootview,
						listener);
			}
		};

		// 设置不可点击
		groupAdapter.setItemsClickEnable(false);
		groupListView.setAdapter(groupAdapter);
	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.layout_group_Listitem_rootview:
				// 跳转至圈子内容部分
				Intent intentToGroupNews = new Intent();
				intentToGroupNews.setClass(MyGroupListActivity.this,
						GroupNewsActivity.class);
				// 传递名称
				intentToGroupNews.putExtra(
						GroupNewsActivity.INTENT_KEY_TOPIC_NAME, groupAdapter
								.getItem(postion).getTopic_name());
				// 传递ID
				intentToGroupNews.putExtra(
						GroupNewsActivity.INTENT_KEY_TOPIC_ID, groupAdapter
								.getItem(postion).getTopic_id());
				// 设置为0
				GroupTopicModel model = groupList.get(postion);
				model.setUnread_news_count(0);
				groupAdapter.replaceAll(groupList);

				startActivityWithRight(intentToGroupNews);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * listview点击事件接口,用于区分不同view的点击事件
	 * 
	 * @author Alan
	 */
	private interface ListItemClickHelp {
		void onClick(View view, int postion, int viewID);
	}

	// 获取我的话题
	private void getMyTopicList() {
		// 获取群组详情
		String path = JLXCConst.GET_MY_TOPIC_LIST + "?user_id="
				+ UserManager.getInstance().getUser().getUid();
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
							jsonToGroupData(jResult
									.getJSONArray(JLXCConst.HTTP_LIST));
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(MyGroupListActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(MyGroupListActivity.this, "获取失败。。");
					}

				}, null));
	}

	/**
	 * 数据处理
	 */
	private void jsonToGroupData(JSONArray dataList) {
		groupList.clear();
		for (int i = 0; i < dataList.size(); i++) {
			JSONObject object = dataList.getJSONObject(i);
			GroupTopicModel groupTopicModel = new GroupTopicModel();
			groupTopicModel.setTopic_id(object.getIntValue("topic_id"));
			groupTopicModel.setTopic_name(object.getString("topic_name"));
			groupTopicModel.setMember_count(object.getIntValue("member_count"));
			groupTopicModel.setTopic_cover_sub_image(object
					.getString("topic_cover_sub_image"));
			groupTopicModel.setUnread_news_count(object
					.getIntValue("unread_news_count"));
			groupList.add(groupTopicModel);
		}
		groupAdapter.replaceAll(groupList);
		//显示提示信息
		if (groupAdapter.getCount() <= 0) {
			myGroupPrompt.setVisibility(View.VISIBLE);
		} else {
			myGroupPrompt.setVisibility(View.GONE);
		}
	}
}
