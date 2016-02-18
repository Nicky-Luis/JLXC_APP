package com.jlxc.app.news.ui.activity;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.jlxc.app.base.utils.ConfigUtils;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.SchoolItemModel;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsBodyItem;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsLikeListItem;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsOperateItem;
import com.jlxc.app.group.model.SchoolItemModel.SchoolNewsTitleItem;
import com.jlxc.app.group.utils.NewsToSchoolItem;
import com.jlxc.app.news.model.CampusPersonModel;
import com.jlxc.app.news.model.ImageModel;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.news.ui.view.LikeButton;
import com.jlxc.app.news.ui.view.LikeImageListView;
import com.jlxc.app.news.ui.view.LikeImageListView.EventCallBack;
import com.jlxc.app.news.ui.view.MultiImageMetroView;
import com.jlxc.app.news.ui.view.MultiImageMetroView.JumpCallBack;
import com.jlxc.app.news.ui.view.TextViewHandel;
import com.jlxc.app.news.utils.NewsOperate;
import com.jlxc.app.news.utils.NewsOperate.LikeCallBack;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CampusNewsActivity extends BaseActivityWithTopBar {

	// 学校ID
	public static final String INTENT_SCHOOL_CODE_KEY = "schoolCode";
	// 动态listview
	@ViewInject(R.id.campus_listview)
	private PullToRefreshListView campusListView;
	// 原始数据源
	private List<NewsModel> newsList = new ArrayList<NewsModel>();
	// item数据源
	private List<SchoolItemModel> itemDataList = null;
	// 动态列表适配器
	private HelloHaAdapter<SchoolItemModel> newsAdapter = null;
	// 使支持多种item
	private MultiItemTypeSupport<SchoolItemModel> multiItemTypeCampus = null;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 当前的数据页
	private int pageIndex = 1;
	// 时间戳
	private String latestTimesTamp = "";
	// 是否下拉
	private boolean isPullDowm = true;
	// 是否正在请求数据
	private boolean isRequestData = false;
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 对动态的操作
	private NewsOperate<SchoolItemModel> newsOPerate;
	// 当前点赞对应的gridview的adpter
	private LikeImageListView currentLikeListControl;
	// 是否是最后一条数据
	private boolean isLast = false;
	// 学校代码
	private String schoolCode;

	@Override
	public int setLayoutId() {
		return R.layout.activity_campus_news_list;
	}

	@Override
	protected void setUpView() {
		setBarText("校园新鲜事");
//		Intent intent = getIntent();
//
//		if (intent.hasExtra(CampusNewsFragment.INTENT_SCHOOL_CODE_KEY)) {
//			String schoolCode = intent
//					.getStringExtra(CampusNewsFragment.INTENT_SCHOOL_CODE_KEY);
//			if (!schoolCode.equals(UserManager.getInstance().getUser()
//					.getSchool_code())) {
//				setBarText("别人学校中发生的事~");
//			}
//		}

		init();
		initBoradcastReceiver();
		multiItemTypeSet();
		newsListViewSet();
		// 进入本页面时请求数据
		getCampusData(
				String.valueOf(UserManager.getInstance().getUser().getUid()),
				String.valueOf(pageIndex), "");

	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		Intent intent = this.getIntent();
		if (intent.hasExtra(INTENT_SCHOOL_CODE_KEY)) {
			schoolCode = intent.getStringExtra(INTENT_SCHOOL_CODE_KEY);
		} else {
			LogUtils.e("未传递查看类型");
		}
		// 处理学校
		if (null == schoolCode || schoolCode.length() < 1) {
			schoolCode = UserManager.getInstance().getUser().getSchool_code();
		}

		itemViewClickListener = new ItemViewClick();
		newsOPerate = new NewsOperate<SchoolItemModel>(
				CampusNewsActivity.this);
		// 图片加载初始化
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 初始化广播信息
	 * */
	private void initBoradcastReceiver() {
		LocalBroadcastManager mLocalBroadcastManager;
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(CampusNewsActivity.this);
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(JLXCConst.BROADCAST_NEWS_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				myIntentFilter);
	}

	/***
	 * 上次缓存的数据
	 * */
	// @SuppressWarnings("unchecked")
	// private void setLastData(int userID, String schoolCode) {
	// String path = JLXCConst.SCHOOL_NEWS_LIST + "?" + "user_id=" + userID
	// + "&page=" + 1 + "&school_code=" + schoolCode + "&frist_time=";
	// try {
	// JSONObject JObject = HttpCacheUtils.getHttpCache(path);
	// if (null != JObject) {
	// int status = JObject.getInteger(JLXCConst.HTTP_STATUS);
	// if (status == JLXCConst.STATUS_SUCCESS) {
	// JSONObject jResult = JObject
	// .getJSONObject(JLXCConst.HTTP_RESULT);
	// if (null != jResult) {
	// // 获取数据列表
	// List<JSONObject> JNewsList = (List<JSONObject>) jResult
	// .get(JLXCConst.HTTP_LIST);
	// List<JSONObject> JPersonList = (List<JSONObject>) jResult
	// .get("info");
	//
	// if (null != JNewsList && null != JPersonList) {
	// JsonToItemData(JNewsList, JPersonList);
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// LogUtils.e("记载缓存出错");
	// }
	// }

	/**
	 * listView 支持多种item的设置
	 * */
	private void multiItemTypeSet() {
		multiItemTypeCampus = new MultiItemTypeSupport<SchoolItemModel>() {

			@Override
			public int getLayoutId(int position, SchoolItemModel itemData) {
				int layoutId = 0;
				switch (itemData.getItemType()) {
				case SchoolItemModel.SCHOOL_NEWS_TITLE:
					layoutId = R.layout.campus_news_item_title_layout;
					break;
				case SchoolItemModel.SCHOOL_NEWS_BODY:
					layoutId = R.layout.campus_news_item_body_layout;
					break;
				case SchoolItemModel.SCHOOL_NEWS_OPERATE:
					layoutId = R.layout.campus_news_item_operate_layout;
					break;
				case SchoolItemModel.SCHOOL_NEWS_LIKELIST:
					layoutId = R.layout.campus_news_item_likelist_layout;
					break;
				default:
					break;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return SchoolItemModel.SCHOOL_NEWS_ITEM_TYPE_COUNT;
			}

			@Override
			public int getItemViewType(int postion, SchoolItemModel itemData) {
				int itemtype = 0;
				switch (itemData.getItemType()) {
				case SchoolItemModel.SCHOOL_NEWS_TITLE:
					itemtype = SchoolItemModel.SCHOOL_NEWS_TITLE;
					break;
				case SchoolItemModel.SCHOOL_NEWS_BODY:
					itemtype = SchoolItemModel.SCHOOL_NEWS_BODY;
					break;
				case SchoolItemModel.SCHOOL_NEWS_OPERATE:
					itemtype = SchoolItemModel.SCHOOL_NEWS_OPERATE;
					break;
				case SchoolItemModel.SCHOOL_NEWS_LIKELIST:
					itemtype = SchoolItemModel.SCHOOL_NEWS_LIKELIST;
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
		campusListView.setMode(Mode.BOTH);
		/**
		 * 刷新监听
		 * */
		campusListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isRequestData) {
					isRequestData = true;
					pageIndex = 1;
					isPullDowm = true;
					getCampusData(
							String.valueOf(UserManager.getInstance().getUser()
									.getUid()), String.valueOf(pageIndex), "");
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isLast && !isRequestData) {
					isRequestData = true;
					isPullDowm = false;
					getCampusData(
							String.valueOf(UserManager.getInstance().getUser()
									.getUid()), String.valueOf(pageIndex),
							latestTimesTamp);
				}
			}
		});

		/**
		 * 设置底部自动刷新
		 * */
		campusListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!isLast) {
							campusListView.setMode(Mode.PULL_FROM_END);
							campusListView.setRefreshing(true);
						}
					}
				});

		/**
		 * adapter的设置
		 * */
		newsAdapter = new HelloHaAdapter<SchoolItemModel>(
				CampusNewsActivity.this, itemDataList, multiItemTypeCampus) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					SchoolItemModel item) {

				switch (helper.layoutId) {
				case R.layout.campus_news_item_title_layout:
					setTitleItemView(helper, item);
					break;
				case R.layout.campus_news_item_body_layout:
					setBodyItemView(helper, item);
					break;
				case R.layout.campus_news_item_operate_layout:
					setOperateItemView(helper, item);
					break;
				case R.layout.campus_news_item_likelist_layout:
					setLikeListItemView(helper, item);
					break;

				default:
					break;
				}
			}
		};

		// 设置不可点击
		newsAdapter.setItemsClickEnable(false);
		campusListView.setAdapter(newsAdapter);
	}

	/**
	 * titleItem的数据绑定与设置
	 * */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper,
			SchoolItemModel item) {
		SchoolNewsTitleItem titleData = (SchoolNewsTitleItem) item;

		// 显示头像
		imgLoader.displayImage(titleData.getHeadSubImage(),
				(ImageView) helper.getView(R.id.img_campus_user_head), options);
		// 设置用户名
		helper.setText(R.id.txt_campus_user_name, titleData.getUserName());
		helper.setText(R.id.txt_campus_publish_time,
				TimeHandle.getShowTimeFormat(titleData.getSendTime()));
		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.img_campus_user_head, listener);
		helper.setOnClickListener(R.id.txt_campus_user_name, listener);
		helper.setOnClickListener(R.id.layout_campus_title_rootview, listener);
	}

	/**
	 * 设置新闻主题item
	 * */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper,
			SchoolItemModel item) {
		final SchoolNewsBodyItem bodyData = (SchoolNewsBodyItem) item;
		List<ImageModel> pictureList = bodyData.getNewsImageListList();
		// MultiImageView bodyImages =
		// helper.getView(R.id.miv_campus_body_images);
		MultiImageMetroView bodyImages = helper
				.getView(R.id.miv_campus_body_images);
		bodyImages.imageDataSet(pictureList);
		// 快速滑动时不加载图片
		bodyImages.loadImageOnFastSlide(campusListView, true);
		bodyImages.setJumpListener(new JumpCallBack() {

			@Override
			public void onImageClick(Intent intentToimageoBig) {
				startActivity(intentToimageoBig);
			}
		});

		// 设置点击事件
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.layout_campus_body_root_view, listener);

		// 设置 文字内容
		if (bodyData.getNewsContent().equals("")) {
			helper.setVisible(R.id.txt_campus_news_content, false);
		} else {
			helper.setVisible(R.id.txt_campus_news_content, true);
			// 设置文字
			TextViewHandel customTvHandel = new TextViewHandel(
					CampusNewsActivity.this, bodyData.getNewsContent());
			helper.setVisible(R.id.txt_campus_news_content, true);
			TextView contentView = helper.getView(R.id.txt_campus_news_content);
			contentView.setText(bodyData.getNewsContent());
			// customTvHandel.setTextContent(contentView,
			// bodyData.getNewsContent());
			// // 长按复制
			contentView.setOnLongClickListener(TextViewHandel
					.getLongClickListener(CampusNewsActivity.this,
							bodyData.getNewsContent()));
			// 点击
			helper.setOnClickListener(R.id.txt_campus_news_content, listener);
		}
		// 设置地理位置
		if (bodyData.getLocation().equals("")) {
			helper.setVisible(R.id.txt_campus_news_location, false);
		} else {
			helper.setVisible(R.id.txt_campus_news_location, true);
			helper.setText(R.id.txt_campus_news_location,
					bodyData.getLocation());
		}
	}

	/**
	 * 设置操作部分item
	 * */
	private void setOperateItemView(HelloHaBaseAdapterHelper helper,
			SchoolItemModel item) {
		SchoolNewsOperateItem opData = (SchoolNewsOperateItem) item;
		// 点赞按钮
		LikeButton likeBtn = helper.getView(R.id.btn_campus_like);
		if (opData.getIsLike()) {
			likeBtn.setStatue(true);
		} else {
			likeBtn.setStatue(false);
		}

		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.btn_campus_reply, listener);
		helper.setOnClickListener(R.id.btn_campus_like, listener);
		helper.setOnClickListener(R.id.layout_campus_operate_root_view,
				listener);
	}

	/**
	 * 设置点赞部分item
	 * */
	private void setLikeListItemView(HelloHaBaseAdapterHelper helper,
			SchoolItemModel item) {
		SchoolNewsLikeListItem lkData = (SchoolNewsLikeListItem) item;
		List<LikeModel> lkImageList = lkData.getLikeHeadListimage();
		LikeImageListView likeControl = helper
				.getView(R.id.control_campus_like_listview);
		int allCount = lkData.getLikeCount();
		String newsID = lkData.getNewsID();

		likeControl.dataInit(allCount, newsID);
		likeControl.listDataBindSet(lkImageList);
		likeControl.setEventListener(new EventCallBack() {

			@Override
			public void onItemClick(int userId) {
				JumpToHomepage(userId);
			}

			@Override
			public void onAllPersonBtnClick(String newsId) {
				// 跳转到点赞的人
				Intent intentToALLPerson = new Intent(
						CampusNewsActivity.this,
						AllLikePersonActivity.class);
				intentToALLPerson.putExtra(
						AllLikePersonActivity.INTENT_KEY_NEWS_ID, newsId);
				startActivityWithRight(intentToALLPerson);
			}
		});
	}

	/**
	 * 获取学校动态的数据
	 * */
	private void getCampusData(String userID, String desPage, String lastTime) {
		String path = JLXCConst.SCHOOL_NEWS_LIST + "?" + "user_id=" + userID
				+ "&page=" + desPage + "&school_code=" + schoolCode
				+ "&frist_time=" + lastTime;

		LogUtils.i("校园请求数据：" + path);
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
							List<JSONObject> JNewsList = (List<JSONObject>) jResult
									.get("list");
							List<JSONObject> JPersonList = null;
							if (jResult.containsKey("info")) {
								JPersonList = (List<JSONObject>) jResult
										.get("info");
							}
							JsonToItemData(JNewsList, JPersonList);
							campusListView.onRefreshComplete();

							if (jResult.getString("is_last").equals("0")) {
								pageIndex++;
								campusListView.setMode(Mode.BOTH);
								isLast = false;
							} else {
								campusListView.setMode(Mode.PULL_FROM_START);
								isLast = true;
							}
							isRequestData = false;
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(CampusNewsActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
							campusListView.onRefreshComplete();
							campusListView.setMode(Mode.PULL_FROM_START);
							isRequestData = false;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(CampusNewsActivity.this,
								"网络抽筋了，请检查(→_→)");
						campusListView.onRefreshComplete();
						campusListView.setMode(Mode.BOTH);
						isRequestData = false;
					}

				}, null));
	}

	/**
	 * 数据处理
	 */
	private void JsonToItemData(List<JSONObject> JNewsList,
			List<JSONObject> JPersonList) {
		// 校园的动态
		List<NewsModel> newsList = new ArrayList<NewsModel>();
		for (JSONObject newsObj : JNewsList) {
			NewsModel tempNews = new NewsModel();
			tempNews.setContentWithJson(newsObj);
			newsList.add(tempNews);
		}

		if (isPullDowm) {
			this.newsList = newsList;
			if (newsList.size() > 0) {
				latestTimesTamp = newsList.get(0).getTimesTamp();
			}
			if (null != itemDataList) {
				itemDataList.clear();
			}
			itemDataList = NewsToSchoolItem.newsToItem(newsList);
			newsAdapter.replaceAll(itemDataList);

			// 如果有内容 取第一条消息的时间 作为本地对比时间 只有自己的学校才更新
			if (null != schoolCode
					&& schoolCode.equals(UserManager.getInstance().getUser()
							.getSchool_code())) {
				if (this.newsList.size() > 0) {
					NewsModel news = this.newsList.get(0);
					// 未读新消息布局
					// 重置未读消息最后一次时间
					ConfigUtils.saveConfig(
							ConfigUtils.LAST_REFRESH__SCHOOL_HOME_NEWS_DATE,
							news.getTimesTamp());
				}
			}
		} else {
			// 加载更多动态信息
			this.newsList.addAll(newsList);
			itemDataList.addAll(NewsToSchoolItem.newsToItem(newsList));
			newsAdapter.replaceAll(itemDataList);
		}

		if (null != JNewsList) {
			JNewsList.clear();
		}
		if (null != JPersonList) {
			JPersonList.clear();
		}
	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.img_campus_user_head:
			case R.id.txt_campus_user_name:
			case R.id.layout_campus_title_rootview:
				SchoolNewsTitleItem titleData = (SchoolNewsTitleItem) newsAdapter
						.getItem(postion);
				if (R.id.layout_campus_title_rootview == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(titleData, NewsConstants.KEY_BOARD_CLOSE,
							null);
				} else {
					// 跳转到用户的主页
					JumpToHomepage(JLXCUtils.stringToInt(titleData.getUserID()));
				}
				break;

			case R.id.miv_campus_body_images:
			case R.id.layout_campus_body_root_view:
			case R.id.txt_campus_news_content:
				// 跳转到动态详情
				SchoolNewsBodyItem bodyData = (SchoolNewsBodyItem) newsAdapter
						.getItem(postion);
				jumpToNewsDetail(bodyData, NewsConstants.KEY_BOARD_CLOSE, null);
				break;

			case R.id.btn_campus_reply:
			case R.id.btn_campus_like:
			case R.id.layout_campus_operate_root_view:
				SchoolNewsOperateItem operateData = (SchoolNewsOperateItem) newsAdapter
						.getItem(postion);
				if (R.id.layout_campus_operate_root_view == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				} else if (R.id.btn_campus_reply == viewID) {
					// 跳转至评论页面并打开评论框
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_COMMENT, null);
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
	 * 点赞操作
	 * */
	private void likeOperate(int postion, View view,
			final SchoolNewsOperateItem operateData) {

		final LikeButton oprtView = (LikeButton) view;
		final int likeListPostion = postion + 1;
		try {
			ListView nListView = campusListView.getRefreshableView();
			View itemRootView = nListView.getChildAt(likeListPostion + 1
					- nListView.getFirstVisiblePosition());
			currentLikeListControl = null;
			if (null != itemRootView) {
				// 点赞头像列表可见的情况下
				currentLikeListControl = (LikeImageListView) itemRootView
						.findViewById(R.id.control_campus_like_listview);
			}
		} catch (Exception e) {
			LogUtils.e("动态点赞部分发生异常.");
		}

		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onOperateStart(boolean isLike) {
				if (isLike) {
					// 点赞操作
					if (null != currentLikeListControl) {
						newsOPerate.addHeadToLikeList(currentLikeListControl);
					} else {
						newsOPerate.addDataToLikeList(newsAdapter,
								likeListPostion);
					}
					oprtView.setStatue(true);
					operateData.setIsLike("1");
				} else {
					// 取消点赞
					if (null != currentLikeListControl) {
						newsOPerate
								.removeHeadFromLikeList(currentLikeListControl);
					} else {
						newsOPerate.removeDataFromLikeList(newsAdapter,
								likeListPostion);
					}

					oprtView.setStatue(false);
					operateData.setIsLike("0");
				}
			}

			@Override
			public void onOperateFail(boolean isLike) {
				// 撤销上次
				newsOPerate.operateRevoked();
				if (isLike) {
					oprtView.setStatue(false);
					operateData.setIsLike("0");
				} else {
					oprtView.setStatue(true);
					operateData.setIsLike("1");
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
	 * listview点击事件接口,用于区分不同view的点击事件
	 * 
	 * @author Alan
	 * 
	 */
	private interface ListItemClickHelp {
		void onClick(View view, int postion, int viewID);
	}

	/**
	 * 学校的人gridview监听
	 */
	public class PersonGridViewItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CampusPersonModel personModel = (CampusPersonModel) parent
					.getAdapter().getItem(position);
			// 跳转到用户的主页
			JumpToHomepage(JLXCUtils.stringToInt(personModel.getUserId()));
		}
	}

	/**
	 * 跳转至用户的主页
	 */
	private void JumpToHomepage(int userID) {
		Intent intentUsrMain = new Intent(CampusNewsActivity.this,
				OtherPersonalActivity.class);
		intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentUsrMain);
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(SchoolItemModel SchoolItemModel,
			int keyBoardMode, String commentId) {
		// 跳转到动态详情
		Intent intentToNewsDetail = new Intent(CampusNewsActivity.this,
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
				SchoolItemModel.getNewsID());

		// 找到当前的操作对象
		for (int index = 0; index < newsList.size(); ++index) {
			if (newsList.get(index).getNewsID()
					.equals(SchoolItemModel.getNewsID())) {
				intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_OBJ,
						newsList.get(index));
				break;
			}
		}

		// 带有返回参数的跳转至动态详情
		startActivityForResult(intentToNewsDetail, 0);
		CampusNewsActivity.this.overridePendingTransition(
				R.anim.push_right_in, R.anim.push_right_out);
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
							newsAdapter.replaceAll(NewsToSchoolItem
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
							newsAdapter.replaceAll(NewsToSchoolItem
									.newsToItem(newsList));
							break;
						}
					}
				} else if (resultIntent
						.hasExtra(NewsConstants.OPERATE_NO_ACTION)) {
					// 无改变
				} else if (resultIntent.hasExtra(NewsConstants.PUBLISH_FINISH)) {
					if (!isRequestData) {
						// 发布了动态
						pageIndex = 1;
						isRequestData = true;
						isPullDowm = true;
						getCampusData(
								String.valueOf(UserManager.getInstance()
										.getUser().getUid()),
								String.valueOf(pageIndex), "");
					}
				} else if (resultIntent
						.hasExtra(NewsConstants.NEWS_LISTVIEW_REFRESH)) {
					// 点击table栏进行刷新
					smoothToTop();
				}
			}
		}
	};

	// 平滑滚动到顶
	private void smoothToTop() {
		int firstVisiblePosition = campusListView.getRefreshableView()
				.getFirstVisiblePosition();
		if (0 == firstVisiblePosition) {
			// 已经在顶部
			if (!campusListView.isRefreshing()) {
				campusListView.setMode(Mode.PULL_FROM_START);
				campusListView.setRefreshing(true);
			}
		} else {
			if (firstVisiblePosition < 20) {
				campusListView.getRefreshableView().smoothScrollToPosition(0);
			} else {
				campusListView.getRefreshableView().setSelection(20);
				campusListView.getRefreshableView().smoothScrollToPosition(0);
			}
			campusListView.getRefreshableView().clearFocus();
		}
	}
}
