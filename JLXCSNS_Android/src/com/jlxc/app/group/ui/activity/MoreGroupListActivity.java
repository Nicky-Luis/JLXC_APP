package com.jlxc.app.group.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.GroupTopicModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 圈子list
 * */
public class MoreGroupListActivity extends BaseActivityWithTopBar {

	// id
	public final static String INTENT_CATEGORY_ID_KEY = "categoryID";
	// 名字
	public final static String INTENT_CATEGORY_NAME_KEY = "categoryName";
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 标头
	// @ViewInject(R.id.tv_discovey_group_title)
	private TextView titleTextView;
	// category标题
	private TextView categoryNameTextView;
	// category描述
	private TextView categoryDescTextView;
	// category封面
	private ImageView categoryImageView;

	// 推荐的圈子
	@ViewInject(R.id.listview_discovey_group)
	private PullToRefreshListView discoveryGroupListView;
	// 适配器
	private HelloHaAdapter<GroupTopicModel> discoveryGroupAdapter = null;
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 是否下拉刷新
	private boolean isPullDowm = false;
	// 是否是最后一页
	private boolean isLast = false;
	// 当前的页数
	private int currentPage = 1;
	// 当前的类别
	private int categoryId = 0;

	@Override
	public int setLayoutId() {
		return R.layout.group_list_layout;
	}

	@Override
	protected void setUpView() {
		init();
		listHeadSet();
		newsListViewSet();
		// 更新数据
		getRecommentData();
	}

	/**
	 * 初始化
	 * */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			categoryId = intent.getIntExtra(INTENT_CATEGORY_ID_KEY, 0);
			if (intent.hasExtra(INTENT_CATEGORY_NAME_KEY)) {
				setBarText(intent.getStringExtra(INTENT_CATEGORY_NAME_KEY));
			}
		}
		itemViewClickListener = new ItemViewClick();
		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.school_home_background)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * listvew head的初始化
	 * */
	private void listHeadSet() {

		try {
			// 添加顶部布局与初始化事件
			View header = View.inflate(this, R.layout.discovery_group_head, null);
			discoveryGroupListView.getRefreshableView().addHeaderView(header);

			categoryNameTextView = (TextView) header
					.findViewById(R.id.category_name_text_view);
			categoryDescTextView = (TextView) header
					.findViewById(R.id.category_desc_text_view);
			categoryImageView = (ImageView) header
					.findViewById(R.id.category_image_view);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * listView 的设置
	 * */
	private void newsListViewSet() {
		// 设置刷新模式
		discoveryGroupListView.setMode(Mode.PULL_FROM_START);
		discoveryGroupListView.setPullToRefreshOverScrollEnabled(false);
		// 刷新监听
		discoveryGroupListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉
						isPullDowm = true;
						currentPage = 1;
						getRecommentData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 上拉
					}
				});
		// 设置底部自动刷新
		discoveryGroupListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (isLast) {
							discoveryGroupListView.onRefreshComplete();
							return;
						}
						currentPage++;
						// 底部自动加载
						discoveryGroupListView.setMode(Mode.PULL_FROM_END);
						discoveryGroupListView.setRefreshing(true);
						isPullDowm = false;
						getRecommentData();
					}
				});
		// 快宿滑动时不加载图片
		discoveryGroupListView.setOnScrollListener(new PauseOnScrollListener(
				ImageLoader.getInstance(), false, true));

		/**
		 * adapter的设置
		 * */
		discoveryGroupAdapter = new HelloHaAdapter<GroupTopicModel>(
				MoreGroupListActivity.this, R.layout.listitem_all_group_layout) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					GroupTopicModel item) {
				// 数据绑定
				imgLoader.displayImage(
						JLXCConst.ATTACHMENT_ADDR
								+ item.getTopic_cover_sub_image(),
						(ImageView) helper.getView(R.id.img_group_cover),
						options);
				helper.setText(R.id.text_group_name, item.getTopic_name());
				helper.setText(R.id.text_group_member_count,
						item.getMember_count() + "人关注");
				// 新闻数量
				int newsCount = item.getNews_count();
				if (newsCount < 2 && !item.isHas_news()) {
					newsCount = 0;
				}
				helper.setText(R.id.text_group_news_count, newsCount + "条内容");
				// 设置事件监听
				final int postion = helper.getPosition();
				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View view) {
						itemViewClickListener.onClick(view, postion,
								view.getId());
					}
				};
				helper.setOnClickListener(R.id.layout_group_item_rootview,
						listener);
			}
		};

		// 设置不可点击
		discoveryGroupAdapter.setItemsClickEnable(false);
		discoveryGroupListView.setAdapter(discoveryGroupAdapter);
	}

	/**
	 * 获取发现部分的数据 categoryId为0则全查
	 * */
	private void getRecommentData() {
		String path = JLXCConst.GET_CATEGORY_TOPIC_LIST + "?" + "category_id="
				+ categoryId + "&page=" + currentPage;

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
							// 最后一页
							if (0 < jResult.getIntValue("is_last")) {
								isLast = true;
							} else {
								isLast = false;
							}
							// 有类别
							if (jResult.containsKey("category")) {
								JSONObject object = jResult
										.getJSONObject("category");
								categoryNameTextView.setText(object
										.getString("category_name"));
								categoryDescTextView.setText(object
										.getString("category_desc"));
								String categoryImage = object
										.getString("category_cover");
								if (null != categoryImage
										&& categoryImage.length() > 1) {
									ImageLoader.getInstance()
											.displayImage(
													JLXCConst.ROOT_PATH
															+ categoryImage,
													categoryImageView, options);
								}
							}
							// 获取数据列表
							List<JSONObject> JPersonList = (List<JSONObject>) jResult
									.get(JLXCConst.HTTP_LIST);
							JsonToItemData(JPersonList);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(MoreGroupListActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
							discoveryGroupListView.onRefreshComplete();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(MoreGroupListActivity.this,
								"网络抽筋了,请检查 =_=");
						discoveryGroupListView.onRefreshComplete();
					}

				}, null));
	}

	/**
	 * 数据解析
	 * */
	private void JsonToItemData(List<JSONObject> dataList) {

		List<GroupTopicModel> list = new ArrayList<GroupTopicModel>();
		for (JSONObject jsonObject : dataList) {
			// 数据处理
			GroupTopicModel topicModel = new GroupTopicModel();
			topicModel.setTopic_id(jsonObject.getIntValue("topic_id"));
			topicModel.setTopic_cover_sub_image(jsonObject
					.getString("topic_cover_sub_image"));
			topicModel.setTopic_name(jsonObject.getString("topic_name"));
			topicModel.setTopic_detail(jsonObject.getString("topic_detail"));
			topicModel.setNews_count(jsonObject.getIntValue("news_count"));
			topicModel.setMember_count(jsonObject.getIntValue("member_count"));
			if (1 == jsonObject.getIntValue("has_news")) {
				topicModel.setHas_news(true);
			} else {
				topicModel.setHas_news(false);
			}
			list.add(topicModel);
		}
		// 如果是下拉刷新
		if (isPullDowm) {
			discoveryGroupAdapter.replaceAll(list);
		} else {
			discoveryGroupAdapter.addAll(list);
		}
		discoveryGroupListView.onRefreshComplete();
		// 是否是最后一页
		if (isLast) {
			discoveryGroupListView.setMode(Mode.PULL_FROM_START);
		} else {
			discoveryGroupListView.setMode(Mode.BOTH);
		}
	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int position, int viewID) {
			switch (viewID) {
			case R.id.layout_group_item_rootview:
				GroupTopicModel topicModel = discoveryGroupAdapter
						.getItem(position);
				// 跳转到圈子内容页面
				Intent groupNewsIntent = new Intent(MoreGroupListActivity.this,
						GroupNewsActivity.class);
				groupNewsIntent.putExtra(GroupNewsActivity.INTENT_KEY_TOPIC_ID,
						topicModel.getTopic_id());
				groupNewsIntent.putExtra(
						GroupNewsActivity.INTENT_KEY_TOPIC_NAME,
						topicModel.getTopic_name());
				startActivityWithRight(groupNewsIntent);
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

}
