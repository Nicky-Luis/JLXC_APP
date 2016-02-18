package com.jlxc.app.group.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
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
import com.jlxc.app.base.adapter.MultiItemTypeSupport;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.GroupNewsItemModel;
import com.jlxc.app.group.model.GroupNewsItemModel.GroupNewsBodyItem;
import com.jlxc.app.group.model.GroupNewsItemModel.GroupNewsOperateItem;
import com.jlxc.app.group.model.GroupNewsItemModel.GroupNewsTitleItem;
import com.jlxc.app.group.utils.NewsToGroupItem;
import com.jlxc.app.news.model.ImageModel;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.news.ui.activity.CampusHomeActivity;
import com.jlxc.app.news.ui.activity.NewsDetailActivity;
import com.jlxc.app.news.ui.activity.PublishNewsActivity;
import com.jlxc.app.news.ui.view.CommentButton;
import com.jlxc.app.news.ui.view.LikeButton;
import com.jlxc.app.news.ui.view.MultiImageMetroView;
import com.jlxc.app.news.ui.view.MultiImageMetroView.JumpCallBack;
import com.jlxc.app.news.ui.view.TextViewHandel;
import com.jlxc.app.news.utils.NewsOperate;
import com.jlxc.app.news.utils.NewsOperate.LikeCallBack;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupNewsActivity extends BaseActivityWithTopBar {

	// 圈子名
	public static final String INTENT_KEY_TOPIC_NAME = "group_name";
	// 圈子ID
	public static final String INTENT_KEY_TOPIC_ID = "group_id";
	// 发布按钮
	@ViewInject(R.id.img_group_publish_btn)
	private ImageView publishBtn;
	// 圈子/校园的名称
	private String topicName;
	// 圈子id
	private int topicID;
	// 添加圈子信息按钮
	private ImageView groupInfo;
	// //////////////

	// 动态listview
	@ViewInject(R.id.listview_group_news)
	private PullToRefreshListView newsListView;
	// 顶部Layout
	private View header;
	// 顶部描述tv
	private TextView topicDescTextView;
	// 顶部关注按钮tv
	private TextView topicBtnTextView;
	// 提示信息
	@ViewInject(R.id.txt_group_news_prompt)
	private TextView groupNewsPrompt;
	// 原始数据源
	private List<NewsModel> newsList = new ArrayList<NewsModel>();
	// item数据源
	private List<GroupNewsItemModel> itemDataList = null;
	// 动态列表适配器
	private HelloHaAdapter<GroupNewsItemModel> newsAdapter = null;
	// 使支持多种item
	private MultiItemTypeSupport<GroupNewsItemModel> multiItemTypeSupport = null;
	// 当前数据的页
	private int pageIndex = 1;
	// 是否是最后一页数据
	private boolean lastPage = false;
	// 时间戳
	private String latestTimesTamp = "";
	// 是否下拉
	private boolean isPullDowm = true;
	// 是否正在请求数据
	private boolean isRequestingData = false;
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 对动态的操作
	private NewsOperate<GroupNewsItemModel> newsOPerate;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 是否为文字长按事件
	private boolean isLongClick = false;

	@Override
	public int setLayoutId() {
		return R.layout.activity_group_news;
	}

	@Override
	protected void setUpView() {
		init();
		widgetInit();
		// /////////////
		headViewInit();
		initBoradcastReceiver();
		multiItemTypeSet();
		newsListViewSet();
		// 从服务器加载数据
		getNewsData(UserManager.getInstance().getUser().getUid(), pageIndex, "");

	}

	/**
	 * 初始化
	 * */
	private void init() {
		Intent intent = this.getIntent();
		// 获取圈子名称
		if (intent.hasExtra(INTENT_KEY_TOPIC_NAME)) {
			topicName = intent.getStringExtra(INTENT_KEY_TOPIC_NAME);
		}
		// 获取圈子ID
		if (intent.hasExtra(INTENT_KEY_TOPIC_ID)) {
			topicID = intent.getIntExtra(INTENT_KEY_TOPIC_ID, 0);
		}

		itemViewClickListener = new ItemViewClick();
		newsOPerate = new NewsOperate<GroupNewsItemModel>(
				GroupNewsActivity.this);
		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 控件的一些初始化
	 * */
	private void widgetInit() {
		// 设置标头
		setBarText(topicName);
		// 添加圈子信息按钮
		groupInfo = addRightImgBtn(R.layout.right_image_button,
				R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		groupInfo.setImageResource(R.drawable.group_info_icon);

		// 添加点事件跳转至圈子资料
		groupInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转至圈子主页
				Intent intentGroupInfo = new Intent(GroupNewsActivity.this,
						GroupInfoActivity.class);
				intentGroupInfo.putExtra(GroupInfoActivity.INTENT_KEY, topicID);
				startActivityWithRight(intentGroupInfo);
			}
		});
		// 点击发布按钮事件
		publishBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intentPublish = new Intent(GroupNewsActivity.this,
						PublishNewsActivity.class);
				intentPublish.putExtra(PublishNewsActivity.INTENT_TOPIC_ID,
						topicID);
				intentPublish.putExtra(PublishNewsActivity.INTENT_TOPIC_NAME,
						topicName);
				startActivityWithRight(intentPublish);
			}
		});
	}

	/**
	 * 控件的一些初始化
	 * */
	private void headViewInit() {
		// 添加顶部布局，提示，通知部分
		header = View.inflate(GroupNewsActivity.this,
				R.layout.group_news_item_head_layout, null);
		// newsListView.getRefreshableView().addHeaderView(header);
		// 顶部描述tv
		topicDescTextView = (TextView) header
				.findViewById(R.id.topic_desc_text_view);
		// 顶部关注按钮tv
		topicBtnTextView = (TextView) header
				.findViewById(R.id.topic_top_btn_text_view);
		topicBtnTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关注点击
				joinTopic();
			}
		});
	}

	/**
	 * 初始化广播信息
	 * */
	private void initBoradcastReceiver() {
		LocalBroadcastManager mLocalBroadcastManager;
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(GroupNewsActivity.this);
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(JLXCConst.BROADCAST_NEWS_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				myIntentFilter);
	}

	/**
	 * listView 支持多种item的设置
	 * */
	private void multiItemTypeSet() {
		multiItemTypeSupport = new MultiItemTypeSupport<GroupNewsItemModel>() {

			@Override
			public int getLayoutId(int position, GroupNewsItemModel itemData) {
				int layoutId = 0;
				switch (itemData.getItemType()) {
				case GroupNewsItemModel.GROUP_TITLE:
					layoutId = R.layout.group_news_item_title_layout;
					break;
				case GroupNewsItemModel.GROUP_BODY:
					layoutId = R.layout.group_news_item_body_layout;
					break;
				case GroupNewsItemModel.GROUP_OPERATE:
					layoutId = R.layout.group_news_item_operate_layout;
					break;
				default:
					break;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return GroupNewsItemModel.NEWS_ITEM_TYPE_COUNT;
			}

			@Override
			public int getItemViewType(int postion, GroupNewsItemModel itemData) {
				int itemtype = 0;
				switch (itemData.getItemType()) {
				case GroupNewsItemModel.GROUP_TITLE:
					itemtype = GroupNewsItemModel.GROUP_TITLE;
					break;
				case GroupNewsItemModel.GROUP_BODY:
					itemtype = GroupNewsItemModel.GROUP_BODY;
					break;
				case GroupNewsItemModel.GROUP_OPERATE:
					itemtype = GroupNewsItemModel.GROUP_OPERATE;
					break;
				default:
					break;
				}
				return itemtype;
			}
		};
	}

	/**
	 * listView 的设置
	 * */
	private void newsListViewSet() {
		// 设置刷新模式
		newsListView.setMode(Mode.BOTH);
		/**
		 * 刷新监听
		 * */
		newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isRequestingData) {
					isRequestingData = true;
					pageIndex = 1;
					isPullDowm = true;
					getNewsData(UserManager.getInstance().getUser().getUid(),
							pageIndex, "");
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!lastPage && !isRequestingData) {
					isRequestingData = true;
					isPullDowm = false;
					getNewsData(UserManager.getInstance().getUser().getUid(),
							pageIndex, latestTimesTamp);
				}
			}
		});

		/**
		 * 设置底部自动刷新
		 * */
		newsListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!lastPage) {
							newsListView.setMode(Mode.PULL_FROM_END);
							newsListView.setRefreshing(true);
						}
					}
				});

		/**
		 * adapter的设置
		 * */
		newsAdapter = new HelloHaAdapter<GroupNewsItemModel>(
				GroupNewsActivity.this, itemDataList, multiItemTypeSupport) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					GroupNewsItemModel item) {

				switch (helper.layoutId) {
				case R.layout.group_news_item_title_layout:
					setTitleItemView(helper, item);
					break;
				case R.layout.group_news_item_body_layout:
					setBodyItemView(helper, item);
					break;
				case R.layout.group_news_item_operate_layout:
					setOperateItemView(helper, item);
					break;

				default:
					break;
				}
			}
		};

		// 设置不可点击
		newsAdapter.setItemsClickEnable(false);
		newsListView.setAdapter(newsAdapter);
	}

	/**
	 * titleItem的数据绑定与设置
	 * */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper,
			GroupNewsItemModel item) {
		GroupNewsTitleItem titleData = (GroupNewsTitleItem) item;
		// 显示头像
		if (null != titleData.getUserSubHeadImage()
				&& titleData.getUserSubHeadImage().length() > 0) {
			imgLoader.displayImage(titleData.getUserSubHeadImage(),
					(ImageView) helper.getView(R.id.img_group_news_user_head),
					options);
		} else {
			((ImageView) helper.getView(R.id.img_group_news_user_head))
					.setImageResource(R.drawable.default_avatar);
		}

		// 设置用户名，学校，标签
		helper.setText(R.id.txt_group_news_user_name, titleData.getUserName());
		helper.setText(R.id.txt_group_news_user_school, titleData.getSchool());

		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.txt_group_news_user_school, listener);
		helper.setOnClickListener(R.id.img_group_news_user_head, listener);
		helper.setOnClickListener(R.id.txt_group_news_user_name, listener);
		helper.setOnClickListener(R.id.layout_group_news_title_rootview,
				listener);
	}

	/**
	 * 设置新闻主体item
	 * */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper,
			GroupNewsItemModel item) {
		final GroupNewsBodyItem bodyData = (GroupNewsBodyItem) item;
		List<ImageModel> pictureList = bodyData.getNewsImageListList();
		// MultiImageView bodyImages =
		// helper.getView(R.id.miv_main_news_images);
		MultiImageMetroView bodyImages = helper
				.getView(R.id.miv_group_news_images);
		bodyImages.imageDataSet(pictureList);
		// 快速滑动时不加载图片
		bodyImages.loadImageOnFastSlide(newsListView, true);

		bodyImages.setJumpListener(new JumpCallBack() {

			@Override
			public void onImageClick(Intent intentToimageoBig) {
				startActivity(intentToimageoBig);
			}
		});
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!isLongClick) {
					itemViewClickListener.onClick(view, postion, view.getId());
				}
			}
		};

		// 设置 文字内容
		if (bodyData.getNewsContent().equals("")) {
			helper.setVisible(R.id.txt_group_news_content, false);
		} else {
			//
			TextViewHandel customTvHandel = new TextViewHandel(
					GroupNewsActivity.this, bodyData.getNewsContent());
			helper.setVisible(R.id.txt_group_news_content, true);
			TextView contentView = helper.getView(R.id.txt_group_news_content);
			contentView.setText(bodyData.getNewsContent());
			// customTvHandel.setTextContent(contentView);
			// 长按复制
			contentView.setOnLongClickListener(TextViewHandel
					.getLongClickListener(GroupNewsActivity.this,
							bodyData.getNewsContent()));
			// 点击
			helper.setOnClickListener(R.id.txt_group_news_content, listener);

		}
		// 设置地理位置
		if (bodyData.getLocation().equals("")) {
			helper.setVisible(R.id.txt_group_news_location, false);
		} else {
			helper.setVisible(R.id.txt_group_news_location, true);
			helper.setText(R.id.txt_group_news_location, bodyData.getLocation());
		}
		// 父布局监听
		helper.setOnClickListener(R.id.miv_group_news_images, listener);
		helper.setOnClickListener(R.id.layout_group_news_body_rootview,
				listener);
	}

	/**
	 * 设置操作部分item
	 * */
	private void setOperateItemView(HelloHaBaseAdapterHelper helper,
			GroupNewsItemModel item) {
		GroupNewsOperateItem opData = (GroupNewsOperateItem) item;
		// 点赞按钮
		LikeButton likeBtn = helper.getView(R.id.btn_group_like);
		if (opData.getIsLike()) {
			if (opData.getLikeCount() > 0) {
				// 绑定点赞的数量
				likeBtn.setStatue(true, opData.getLikeCount());
			} else {
				likeBtn.setStatue(true);
			}
		} else {
			if (opData.getLikeCount() > 0) {
				// 绑定点赞的数量
				likeBtn.setStatue(false, opData.getLikeCount());
			} else {
				likeBtn.setStatue(false);
			}
		}

		// 评论按钮
		CommentButton commentBtn = helper.getView(R.id.btn_group_reply);
		if (opData.getReplyCount() > 0) {
			commentBtn.setContent(opData.getReplyCount());
		}
		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		// 绑定时间
		helper.setText(R.id.txt_group_news_publish_time,
				TimeHandle.getShowTimeFormat(opData.getTime()));
		// 事件监听绑定
		helper.setOnClickListener(R.id.btn_group_reply, listener);
		helper.setOnClickListener(R.id.btn_group_like, listener);
		helper.setOnClickListener(R.id.layout_group_news_operate_rootview,
				listener);
	}

	/**
	 * 获取动态数据
	 * */
	private void getNewsData(int userID, int desPage, String lastTime) {
		if (topicID < 1) {
			ToastUtil.show(GroupNewsActivity.this, "圈子不存在");
		}
		String path = JLXCConst.GET_TOPIC_NEWS_LIST + "?" + "user_id=" + userID
				+ "&topic_id=" + topicID + "&page=" + desPage + "&frist_time="
				+ lastTime;

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

							// 是否已经关注
							int isAttent = jResult.getIntValue("is_attent");
							if (isAttent == 0) {
								topicDescTextView.setText(jResult
										.getString("topic_detail"));
								if (newsListView.getRefreshableView()
										.getHeaderViewsCount() < 2) {
									newsListView.getRefreshableView()
											.addHeaderView(header);
								}
							} else {
								newsListView.getRefreshableView()
										.removeHeaderView(header);
							}

							// 获取动态列表
							List<JSONObject> JSONList = (List<JSONObject>) jResult
									.get("list");
							JsonToNewsModel(JSONList);
							newsListView.onRefreshComplete();
							if (jResult.getString("is_last").equals("0")) {
								lastPage = false;
								pageIndex++;
								newsListView.setMode(Mode.BOTH);
							} else {
								lastPage = true;
								newsListView.setMode(Mode.PULL_FROM_START);
							}
							isRequestingData = false;
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(GroupNewsActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
							newsListView.onRefreshComplete();
							newsListView.setMode(Mode.BOTH);
							isRequestingData = false;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil
								.show(GroupNewsActivity.this, "网络抽筋了，请检查(→_→)");
						newsListView.onRefreshComplete();
						newsListView.setMode(Mode.BOTH);
						isRequestingData = false;
					}

				}, null));
	}

	/**
	 * 数据处理
	 */
	private void JsonToNewsModel(List<JSONObject> dataList) {
		if (null != dataList && dataList.size() >= 1) {
			List<NewsModel> newDatas = new ArrayList<NewsModel>();
			for (JSONObject newsObj : dataList) {
				NewsModel tempNews = new NewsModel();
				tempNews.setContentWithJson(newsObj);
				newDatas.add(tempNews);
			}
			if (isPullDowm) {
				// 更新时间戳
				latestTimesTamp = newDatas.get(0).getTimesTamp();
				newsList.clear();
				newsList.addAll(newDatas);
				newsAdapter.replaceAll(NewsToGroupItem.newsToItem(newDatas));
			} else {
				newsList.addAll(newDatas);
				newsAdapter.addAll(NewsToGroupItem.newsToItem(newDatas));
			}
			dataList.clear();
		}

		// 显示提示信息
		if (newsAdapter.getCount() <= 0) {
			groupNewsPrompt.setVisibility(View.VISIBLE);
		} else {
			groupNewsPrompt.setVisibility(View.GONE);
		}
	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.layout_group_news_title_rootview:
			case R.id.img_group_news_user_head:
			case R.id.txt_group_news_user_name:
			case R.id.txt_group_news_user_school:
				GroupNewsTitleItem titleData = (GroupNewsTitleItem) newsAdapter
						.getItem(postion);
				if (R.id.layout_group_news_title_rootview == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(titleData, NewsConstants.KEY_BOARD_CLOSE,
							null);
				} else if (R.id.txt_group_news_user_school == viewID) {
					// 跳转至校园主页
					Intent intentCampusInfo = new Intent(
							GroupNewsActivity.this, CampusHomeActivity.class);
					intentCampusInfo.putExtra(
							CampusHomeActivity.INTENT_SCHOOL_CODE_KEY,
							titleData.getSchoolCode());
					startActivityWithRight(intentCampusInfo);
				} else {
					// 跳转至用户主页
					jumpToHomepage(JLXCUtils.stringToInt(titleData.getUserID()));
				}
				break;

			case R.id.layout_news_body_rootview:
			case R.id.txt_group_news_content:
			case R.id.miv_group_news_images:
				GroupNewsBodyItem bodyData = (GroupNewsBodyItem) newsAdapter
						.getItem(postion);
				// 跳转到动态详情
				jumpToNewsDetail(bodyData, NewsConstants.KEY_BOARD_CLOSE, null);
				break;

			case R.id.btn_group_reply:
			case R.id.btn_group_like:
			case R.id.layout_group_news_operate_rootview:

				final GroupNewsOperateItem operateData = (GroupNewsOperateItem) newsAdapter
						.getItem(postion);
				if (R.id.layout_group_news_operate_rootview == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				} else if (R.id.btn_group_reply == viewID) {
					// 跳转至评论页面并评论框
					// jumpToNewsDetail(operateData,
					// NewsConstants.KEY_BOARD_COMMENT, null);
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				} else {
					// 点赞操作
					likeOperate(postion, view, operateData);
				}
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

	/**
	 * 点赞操作
	 * */
	private void likeOperate(int postion, View view,
			final GroupNewsOperateItem operateData) {

		final LikeButton oprtView = (LikeButton) view;
		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onOperateStart(boolean isLike) {
				if (isLike) {
					operateData.setIsLike("1");
					// 设置点赞的数量
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() + 1));
					oprtView.setStatue(true, operateData.getLikeCount());
				} else {
					operateData.setIsLike("0");
					// 设置点赞的数量
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() - 1));
					if (operateData.getLikeCount() > 1) {
						oprtView.setStatue(false, operateData.getLikeCount());
					} else {
						oprtView.setStatue(false);
					}
				}
			}

			@Override
			public void onOperateFail(boolean isLike) {
				// 撤销上次
				newsOPerate.operateRevoked();
				if (isLike) {
					operateData.setIsLike("0");
					// 设置点赞的数量
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() - 1));
					if (operateData.getLikeCount() > 1) {
						oprtView.setStatue(false, operateData.getLikeCount());
					} else {
						oprtView.setStatue(false);
					}
				} else {
					operateData.setIsLike("1");
					// 设置点赞的数量
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() + 1));
					oprtView.setStatue(true, operateData.getLikeCount());
				}
			}
		});
		if (operateData.getIsLike()) {
			newsOPerate.uploadLikeOperate(operateData.getNewsID(), false);
		} else {
			newsOPerate.uploadLikeOperate(operateData.getNewsID(), true);
		}
	}

	/**
	 * 跳转至用户的主页
	 */
	private void jumpToHomepage(int userID) {
		Intent intentUsrMain = new Intent(GroupNewsActivity.this,
				OtherPersonalActivity.class);
		intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentUsrMain);
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(GroupNewsItemModel itemModel,
			int keyBoardMode, String commentId) {
		// 跳转到动态详情
		Intent intentToNewsDetail = new Intent(GroupNewsActivity.this,
				NewsDetailActivity.class);
		switch (keyBoardMode) {
		// 键盘关闭
		case NewsConstants.KEY_BOARD_CLOSE:
			intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_COMMENT_STATE,
					NewsConstants.KEY_BOARD_CLOSE);
			break;
		// 键盘打开等待评论
		case NewsConstants.KEY_BOARD_COMMENT:
			intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_COMMENT_STATE,
					NewsConstants.KEY_BOARD_COMMENT);
			break;
		// 键盘打开等待回复
		case NewsConstants.KEY_BOARD_REPLY:
			intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_COMMENT_STATE,
					NewsConstants.KEY_BOARD_REPLY);
			if (null != commentId) {
				intentToNewsDetail.putExtra(
						NewsConstants.INTENT_KEY_COMMENT_ID, commentId);
			} else {
				LogUtils.e("回复别人时必须要传递被评论的id.");
			}
			break;

		default:
			break;
		}
		// 当前操作的动态id
		intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_ID,
				itemModel.getNewsID());

		// 找到当前的动态对象
		for (int index = 0; index < newsList.size(); ++index) {
			if (newsList.get(index).getNewsID().equals(itemModel.getNewsID())) {
				intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_OBJ,
						newsList.get(index));
				break;
			}
		}

		// 带有返回参数的跳转至动态详情
		startActivityForResult(intentToNewsDetail, 1);
		GroupNewsActivity.this.overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
	}

	// 加入或者退出
	private void joinTopic() {
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("topic_id", topicID + "");
		showLoading("关注中..", true);

		// 路径
		String path = JLXCConst.JOIN_TOPIC;
		HttpManager.post(path, params, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {

							ToastUtil.show(GroupNewsActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));

							newsListView.getRefreshableView().removeHeaderView(
									header);

						}
						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(GroupNewsActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(GroupNewsActivity.this, "网络异常");
					}
				}, null));
	}

	/**
	 * 广播接收处理
	 * */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent resultIntent) {
			String action = resultIntent.getAction();
			if (action.equals(JLXCConst.BROADCAST_NEWS_LIST_REFRESH)) {
				if (resultIntent.hasExtra(NewsConstants.OPERATE_UPDATE)) {
					// 更新动态列表
					NewsModel resultNews = (NewsModel) resultIntent
							.getSerializableExtra(NewsConstants.OPERATE_UPDATE);

					for (int index = 0; index < newsList.size(); index++) {
						if (resultNews.getNewsID().equals(
								newsList.get(index).getNewsID())) {
							newsList.set(index, resultNews);
							newsAdapter.replaceAll(NewsToGroupItem
									.newsToItem(newsList));
							break;
						}
					}
				} else if (resultIntent.hasExtra(NewsConstants.OPERATE_DELETET)) {
					String resultID = resultIntent
							.getStringExtra(NewsConstants.OPERATE_DELETET);
					// 删除该动态
					for (int index = 0; index < newsList.size(); index++) {
						if (resultID.equals(newsList.get(index).getNewsID())) {
							newsList.remove(index);
							newsAdapter.replaceAll(NewsToGroupItem
									.newsToItem(newsList));
							break;
						}
					}
				} else if (resultIntent
						.hasExtra(NewsConstants.OPERATE_NO_ACTION)) {
					// 无改变
				} else if (resultIntent.hasExtra(NewsConstants.PUBLISH_FINISH)) {
					// 发布了动态,进行刷新
					if (!isRequestingData) {
						isRequestingData = true;
						pageIndex = 1;
						isPullDowm = true;
						getNewsData(UserManager.getInstance().getUser()
								.getUid(), pageIndex, "");
					}
				}
			}
		}
	};

	// // 平滑滚动到顶
	// private void smoothToTop() {
	// int firstVisiblePosition = newsListView.getRefreshableView()
	// .getFirstVisiblePosition();
	// if (0 == firstVisiblePosition) {
	// // 已经在顶部
	// newsListView.setMode(Mode.PULL_FROM_START);
	// newsListView.setRefreshing();
	// } else {
	// if (firstVisiblePosition < 20) {
	// newsListView.getRefreshableView().smoothScrollToPosition(0);
	// } else {
	// newsListView.getRefreshableView().setSelection(20);
	// newsListView.getRefreshableView().smoothScrollToPosition(0);
	// }
	// newsListView.getRefreshableView().clearFocus();
	// }
	// }

}
