package com.jlxc.app.personal.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.news.model.ImageModel;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.news.ui.activity.NewsDetailActivity;
import com.jlxc.app.news.ui.view.CommentButton;
import com.jlxc.app.news.ui.view.LikeButton;
import com.jlxc.app.news.ui.view.MultiImageView;
import com.jlxc.app.news.ui.view.MultiImageView.JumpCallBack;
import com.jlxc.app.news.utils.NewsOperate;
import com.jlxc.app.news.utils.NewsOperate.LikeCallBack;
import com.jlxc.app.news.utils.NewsOperate.OperateCallBack;
import com.jlxc.app.personal.model.MyNewsListItemModel;
import com.jlxc.app.personal.model.MyNewsListItemModel.MyNewsBodyItem;
import com.jlxc.app.personal.model.MyNewsListItemModel.MyNewsOperateItem;
import com.jlxc.app.personal.model.MyNewsListItemModel.MyNewsTitleItem;
import com.jlxc.app.personal.utils.NewsToItemData;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;

//我的动态列表
public class MyNewsListActivity extends BaseActivityWithTopBar {

	// 其他Activity传递进入的被查看的用户id
	public final static String INTNET_KEY_UID = "user_id";
	// 动态listview
	@ViewInject(R.id.listview_my_news_list)
	private PullToRefreshListView newsListView;
	// 动态listview
	@ViewInject(R.id.tv_my_news_prompt)
	private TextView prompTextView;
	// 时光轴
	@ViewInject(R.id.iv_time_line_backgroung)
	private View timeLine;
	// 原始数据源
	private List<NewsModel> newsList = new ArrayList<NewsModel>();
	// item类型数据
	private List<MyNewsListItemModel> itemDataList = null;
	// 动态列表适配器
	private HelloHaAdapter<MyNewsListItemModel> newsAdapter = null;
	// 使支持多种item
	private MultiItemTypeSupport<MyNewsListItemModel> multiItemTypeSupport = null;
	// 当前的数据页
	private int currentPage = 1;
	// 是否是最后一页数据
	private boolean islastPage = false;
	// 是否下拉
	private boolean isPullDowm = true;
	// 是否正在请求数据
	private boolean isRequestData = false;
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 当前操作的动态id
	private String currentNewsId = "";
	// 对动态的操作
	private NewsOperate newsOPerate;
	// 被查看者的用户ID
	private String currentUid = "";

	@Override
	public int setLayoutId() {
		return R.layout.activity_my_news_list;
	}

	@Override
	protected void setUpView() {
		init();
		initBoradcastReceiver();
		multiItemTypeSet();
		newsListViewSet();

		/******** 首次获取数据 ********/
		showLoading("加载中...", true);
		getMyNewsData(currentUid, String.valueOf(currentPage));
		/*************************/
	}

	/**
	 * listView 支持多种item的设置
	 * */
	private void multiItemTypeSet() {
		multiItemTypeSupport = new MultiItemTypeSupport<MyNewsListItemModel>() {

			@Override
			public int getLayoutId(int position, MyNewsListItemModel itemData) {
				int layoutId = 0;
				switch (itemData.getItemType()) {
				case MyNewsListItemModel.NEWS_TITLE:
					layoutId = R.layout.my_newslist_item_title;
					break;
				case MyNewsListItemModel.NEWS_BODY:
					layoutId = R.layout.my_newslist_item_body;
					break;
				case MyNewsListItemModel.NEWS_OPERATE:
					layoutId = R.layout.my_newslist_item_operate;
					break;
				default:
					break;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return MyNewsListItemModel.NEWS_ITEM_TYPE_COUNT;
			}

			@Override
			public int getItemViewType(int postion, MyNewsListItemModel itemData) {
				int itemtype = 0;
				switch (itemData.getItemType()) {
				case MyNewsListItemModel.NEWS_TITLE:
					itemtype = MyNewsListItemModel.NEWS_TITLE;
					break;
				case MyNewsListItemModel.NEWS_BODY:
					itemtype = MyNewsListItemModel.NEWS_BODY;
					break;
				case MyNewsListItemModel.NEWS_OPERATE:
					itemtype = MyNewsListItemModel.NEWS_OPERATE;
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
				if (!isRequestData) {
					isRequestData = true;
					currentPage = 1;
					isPullDowm = true;
					getMyNewsData(currentUid, String.valueOf(currentPage));
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!islastPage && !isRequestData) {
					isRequestData = true;
					isPullDowm = false;
					getMyNewsData(currentUid, String.valueOf(currentPage));
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
						if (!islastPage) {
							newsListView.setMode(Mode.PULL_FROM_END);
							newsListView.setRefreshing(true);
						}
					}
				});

		/**
		 * adapter的设置
		 * */
		newsAdapter = new HelloHaAdapter<MyNewsListItemModel>(
				MyNewsListActivity.this, itemDataList, multiItemTypeSupport) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					MyNewsListItemModel item) {

				switch (helper.layoutId) {
				case R.layout.my_newslist_item_title:
					setTitleItemView(helper, item);
					break;
				case R.layout.my_newslist_item_body:
					setBodyItemView(helper, item);
					break;
				case R.layout.my_newslist_item_operate:
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
	 * 数据的初始化
	 * */
	private void init() {
		setBarText("个人动态");
		itemViewClickListener = new ItemViewClick();
		newsOperateSet();

		Intent intent = this.getIntent();
		if (null != intent && intent.hasExtra(INTNET_KEY_UID)) {
			currentUid = intent.getStringExtra(INTNET_KEY_UID);
		} else {
			LogUtils.e("用户id传输错误，用户id为：" + currentUid);
		}
	}

	/**
	 * 初始化广播信息
	 * */
	private void initBoradcastReceiver() {
		LocalBroadcastManager mLocalBroadcastManager;
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(MyNewsListActivity.this);
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(JLXCConst.BROADCAST_NEWS_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				myIntentFilter);
	}

	/**
	 * 动态操作类的初始化
	 * */
	private void newsOperateSet() {
		newsOPerate = new NewsOperate(MyNewsListActivity.this);
		newsOPerate.setOperateListener(new OperateCallBack() {

			@Override
			public void onStart(int operateType) {
				// 操作开始
			}

			@Override
			public void onFinish(int operateType, boolean isSucceed,
					Object resultValue) {
				switch (operateType) {
				case NewsOperate.OP_Type_Delete_News:
					if (isSucceed) {
						for (int index = 0; index < newsAdapter.getCount(); index++) {
							if (newsAdapter.getItem(index).getNewsID()
									.equals(currentNewsId)) {
								newsAdapter.remove(index);
								index--;
							}
						}
						ToastUtil.show(MyNewsListActivity.this, "删除成功");
					}
					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * titleItem的数据绑定与设置
	 * */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper,
			MyNewsListItemModel item) {
		MyNewsTitleItem titleData = (MyNewsTitleItem) item;

		// 设置用户名,发布的时间
		helper.setText(R.id.txt_my_news_list_name, titleData.getUserName());
		helper.setText(R.id.txt_my_news_list_tiem,
				TimeHandle.getShowTimeFormat(titleData.getSendTime()));

	}

	/**
	 * 设置新闻主体item
	 * */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper,
			MyNewsListItemModel item) {
		MyNewsBodyItem bodyData = (MyNewsBodyItem) item;
		List<ImageModel> pictureList = bodyData.getNewsImageListList();
		MultiImageView bodyImages = helper.getView(R.id.miv_my_newslist_images);
		bodyImages.imageDataSet(pictureList);
		// 快速滑动时不加载
		bodyImages.loadImageOnFastSlide(newsListView, true);

		bodyImages.setJumpListener(new JumpCallBack() {

			@Override
			public void onImageClick(Intent intentToimageoBig) {
				startActivity(intentToimageoBig);
			}
		});

		// 创建点击事件监听对象
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};

		// 设置 文字内容
		if (bodyData.getNewsContent().equals("")) {
			helper.setVisible(R.id.txt_my_news_list_content, false);
		} else {
			helper.setVisible(R.id.txt_my_news_list_content, true);
			helper.setText(R.id.txt_my_news_list_content,
					bodyData.getNewsContent());
			helper.setOnClickListener(R.id.txt_my_news_list_content, listener);
		}
		// 设置地理位置
		if (bodyData.getLocation().equals("")) {
			helper.setVisible(R.id.txt_my_news_list_location, false);
		} else {
			helper.setVisible(R.id.txt_my_news_list_location, true);
			helper.setText(R.id.txt_my_news_list_location,
					bodyData.getLocation());
		}
		// 父布局监听
		helper.setOnClickListener(R.id.layout_my_news_list_body_rootview,
				listener);
	}

	/**
	 * 设置操作部分item
	 * */
	private void setOperateItemView(HelloHaBaseAdapterHelper helper,
			MyNewsListItemModel item) {

		MyNewsOperateItem opData = (MyNewsOperateItem) item;

		// 点赞按钮
		LikeButton likeBtn = helper.getView(R.id.btn_my_news_list_like);
		if (opData.getIsLike()) {
			likeBtn.setStatue(true, opData.getLikeCount());
		} else {
			likeBtn.setStatue(false, opData.getLikeCount());
		}

		// 评论按钮
		CommentButton commentBtn = helper.getView(R.id.btn_my_news_list_reply);
		commentBtn.setContent(opData.getReplyCount());
		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};

		//
		helper.setOnClickListener(R.id.btn_my_news_list_reply, listener);
		helper.setOnClickListener(R.id.btn_my_news_list_like, listener);
		helper.setOnClickListener(R.id.layout_my_news_list_operate_rootview,
				listener);
		/********隐藏操作按钮，改为显示数量********/
		likeBtn.setVisibility(View.GONE);
		commentBtn.setVisibility(View.GONE);
		
		TextView likeCount = helper.getView(R.id.tv_like_count);
		TextView commentCount = helper.getView(R.id.tv_comment_count);
		likeCount.setText(opData.getLikeCount()+"点赞");
		commentCount.setText(opData.getReplyCount()+"评论");
	}

	/**
	 * 获取动态数据
	 * */
	private void getMyNewsData(String userID, String page) {
		String path = JLXCConst.USER_NEWS_LIST + "?" + "user_id=" + userID
				+ "&page=" + page + "&size=" + "";
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							hideLoading();
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 获取动态列表
							List<JSONObject> JSONList = (List<JSONObject>) jResult
									.get("list");
							JsonToNewsModel(JSONList);
							newsListView.onRefreshComplete();
							if (jResult.getString("is_last").equals("0")) {
								islastPage = false;
								currentPage++;
								newsListView.setMode(Mode.BOTH);
							} else {
								islastPage = true;
								newsListView.setMode(Mode.PULL_FROM_START);
							}
							isRequestData = false;
						}

						if (status == JLXCConst.STATUS_FAIL) {
							hideLoading();
							ToastUtil.show(MyNewsListActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
							newsListView.onRefreshComplete();
							if (!islastPage) {
								newsListView.setMode(Mode.BOTH);
							}
							isRequestData = false;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(MyNewsListActivity.this,
								"网络 太差，请检查 =_=||");
						newsListView.onRefreshComplete();
						if (!islastPage) {
							newsListView.setMode(Mode.BOTH);
						}
						isRequestData = false;
					}

				}, null));
	}

	/**
	 * 点赞操作
	 * */
	private void likeOperate(int postion, View view,
			final MyNewsOperateItem operateData) {

		final View oprtView = view;

		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onOperateStart(boolean isLike) {
				if (isLike) {
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() + 1));
					((LikeButton) oprtView).setStatue(true,
							operateData.getLikeCount());
					operateData.setIsLike("1");
				} else {
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() - 1));
					((LikeButton) oprtView).setStatue(false,
							operateData.getLikeCount());
					operateData.setIsLike("0");
				}
			}

			@Override
			public void onOperateFail(boolean isLike) {
				if (isLike) {
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() - 1));
					((LikeButton) oprtView).setStatue(false,
							operateData.getLikeCount());
					operateData.setIsLike("0");
				} else {
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() + 1));
					((LikeButton) oprtView).setStatue(true,
							operateData.getLikeCount());
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
	 * 数据处理
	 */
	private void JsonToNewsModel(List<JSONObject> dataList) {
		List<NewsModel> newDatas = new ArrayList<NewsModel>();
		for (JSONObject newsObj : dataList) {
			NewsModel tempNews = new NewsModel();
			tempNews.setContentWithJson(newsObj);
			newDatas.add(tempNews);
		}
		if (isPullDowm) {
			newsList.clear();
			newsList.addAll(newDatas);
			itemDataList = NewsToItemData.newsToItem(newDatas);
			newsAdapter.replaceAll(itemDataList);
		} else {
			newsList.addAll(newDatas);
			newsAdapter.addAll(NewsToItemData.newsToItem(newDatas));
		}
		if (isPullDowm) {
			dataList.clear();
		}
		if (newsAdapter.getCount() <= 0) {
			prompTextView.setVisibility(View.VISIBLE);
			timeLine.setVisibility(View.GONE);
		} else {
			prompTextView.setVisibility(View.GONE);
			timeLine.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.layout_my_news_list_body_rootview:
			case R.id.txt_my_news_list_content:
			case R.id.miv_my_newslist_images:
				MyNewsBodyItem bodyData = (MyNewsBodyItem) newsAdapter
						.getItem(postion);
				// 跳转至动态详情
				jumpToNewsDetail(bodyData, NewsConstants.KEY_BOARD_CLOSE, null);
				break;

			case R.id.btn_my_news_list_reply:
			case R.id.btn_my_news_list_like:
			case R.id.layout_my_news_list_operate_rootview:

				final MyNewsOperateItem operateData = (MyNewsOperateItem) newsAdapter
						.getItem(postion);
				if (R.id.layout_my_news_list_operate_rootview == viewID) {
					// 跳转至动态详情
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				} else if (R.id.btn_my_news_list_reply == viewID) {
					// 跳转至评论页面并打开评论框
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_COMMENT, null);
				} else {
					// 进行点赞操作
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
	 * 
	 */
	private interface ListItemClickHelp {
		void onClick(View view, int postion, int viewID);
	}

	/**
	 * 点赞gridview监听
	 */
	public class LikeGridViewItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LikeModel likeUser = (LikeModel) parent.getAdapter().getItem(
					position);
			jumpToHomepage(JLXCUtils.stringToInt(likeUser.getUserID()));
		}
	}

	/**
	 * 跳转至用户的主页
	 */
	private void jumpToHomepage(int userID) {
		Intent intentUsrMain = new Intent(MyNewsListActivity.this,
				OtherPersonalActivity.class);
		intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentUsrMain);
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(MyNewsListItemModel itemModel,
			int keyBoardMode, String commentId) {
		// 跳转到动态详情
		Intent intentToNewsDetail = new Intent(MyNewsListActivity.this,
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

		// 找到当前的操作对象
		for (int index = 0; index < newsList.size(); ++index) {
			if (newsList.get(index).getNewsID().equals(itemModel.getNewsID())) {
				intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_OBJ,
						newsList.get(index));
				break;
			}
		}

		// 带有返回参数的跳转至动态详情
		startActivityForResult(intentToNewsDetail, 0);
		this.overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
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
							newsAdapter.replaceAll(NewsToItemData
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
							newsAdapter.replaceAll(NewsToItemData
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
						isRequestData = true;
						currentPage = 1;
						isPullDowm = true;
						getMyNewsData(currentUid, String.valueOf(currentPage));
					}
				}
			}
		}
	};

}
