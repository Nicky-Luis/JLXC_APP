package com.jlxc.app.news.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.base.utils.HttpCacheUtils;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.ui.activity.GroupNewsActivity;
import com.jlxc.app.group.ui.activity.MyGroupListActivity;
import com.jlxc.app.news.model.CommentModel;
import com.jlxc.app.news.model.ImageModel;
import com.jlxc.app.news.model.ItemModel;
import com.jlxc.app.news.model.ItemModel.BodyItem;
import com.jlxc.app.news.model.ItemModel.CommentListItem;
import com.jlxc.app.news.model.ItemModel.LikeListItem;
import com.jlxc.app.news.model.ItemModel.OperateItem;
import com.jlxc.app.news.model.ItemModel.TitleItem;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.news.ui.activity.AllLikePersonActivity;
import com.jlxc.app.news.ui.activity.CampusHomeActivity;
import com.jlxc.app.news.ui.activity.NewsDetailActivity;
import com.jlxc.app.news.ui.activity.PublishNewsActivity;
import com.jlxc.app.news.ui.view.LikeButton;
import com.jlxc.app.news.ui.view.LikeImageListView;
import com.jlxc.app.news.ui.view.LikeImageListView.EventCallBack;
import com.jlxc.app.news.ui.view.MultiImageMetroView;
import com.jlxc.app.news.ui.view.MultiImageMetroView.JumpCallBack;
import com.jlxc.app.news.ui.view.TextViewHandel;
import com.jlxc.app.news.utils.DataToItem;
import com.jlxc.app.news.utils.NewsOperate;
import com.jlxc.app.news.utils.NewsOperate.LikeCallBack;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainNewsListFragment extends BaseFragment {

	// 动态显示的评论数量
	private int NEWS_COMMENT_NUM = 3;
	// 动态listview
	@ViewInject(R.id.news_listview)
	private PullToRefreshListView newsListView;
	// 发布按钮
	@ViewInject(R.id.img_main_publish_btn)
	private ImageView publishBtn;
	// 学校主页按钮
	@ViewInject(R.id.image_school_home)
	private ImageView schoolBtn;
	 //标头部分
//	 @ViewInject(R.id.layout_main_head_rootview)
//	 private LinearLayout headLayout;
	//顶部
//	@ViewInject(R.id.home_top_layout)
//	private LinearLayout topBarLayout;
	// 原始数据源
	private List<NewsModel> newsList = new ArrayList<NewsModel>();
	// item数据源
	private List<ItemModel> itemDataList = null;
	// 动态列表适配器
	private HelloHaAdapter<ItemModel> newsAdapter = null;
	// 使支持多种item
	private MultiItemTypeSupport<ItemModel> multiItemTypeSupport = null;
	// 上下文信息
	private Context mContext;
	// 评论部分的控件
	private List<Map<String, Integer>> commentViewList;
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
	private NewsOperate<ItemModel> newsOPerate;
	// 当前点赞对应的gridview的adpter
	private LikeImageListView currentLikeListControl;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 是否为文字长按事件
	private boolean isLongClick = false;
	//
	private View header;
//	//最后一条
//	private int lastItem;
//	private boolean isAnimation;

	@Override
	public int setLayoutId() {
		return R.layout.fragment_mian_news_layout;
	}

	@Override
	public void loadLayout(View rootView) {

	}

	@Override
	public void setUpViews(View rootView) {
		init();
		initBoradcastReceiver();
		multiItemTypeSet();
		newsListViewSet();
		getCommentWidget();
		// 获取上次缓存的数据
		setLastData(UserManager.getInstance().getUser().getUid());
		// 从服务器加载数据
		getNewsData(UserManager.getInstance().getUser().getUid(), pageIndex, "");

		// //点击发布按钮
		publishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentUsrMain = new Intent(mContext,
						PublishNewsActivity.class);
				startActivityWithRight(intentUsrMain);
			}
		});
		schoolBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转至校园主页
				Intent intentCampusInfo = new Intent(mContext,
						CampusHomeActivity.class);
				startActivityWithRight(intentCampusInfo);
			}
		});
	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		mContext = this.getActivity();

		itemViewClickListener = new ItemViewClick();
		newsOPerate = new NewsOperate<ItemModel>(mContext);
		// 获取显示图片的实例
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
				.getInstance(getActivity());
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
		multiItemTypeSupport = new MultiItemTypeSupport<ItemModel>() {

			@Override
			public int getLayoutId(int position, ItemModel itemData) {
				int layoutId = 0;
				switch (itemData.getItemType()) {
				case ItemModel.NEWS_TITLE:
					layoutId = R.layout.mian_news_item_title_layout;
					break;
				case ItemModel.NEWS_BODY:
					layoutId = R.layout.main_news_item_body_layout;
					break;
				case ItemModel.NEWS_OPERATE:
					layoutId = R.layout.mian_news_item_operate_layout;
					break;
				case ItemModel.NEWS_LIKELIST:
					layoutId = R.layout.mian_news_item_likelist_layout;
					break;
				case ItemModel.NEWS_COMMENT:
					layoutId = R.layout.mian_news_item_comment_layout;
					break;
				default:
					break;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return ItemModel.NEWS_ITEM_TYPE_COUNT;
			}

			@Override
			public int getItemViewType(int postion, ItemModel itemData) {
				int itemtype = 0;
				switch (itemData.getItemType()) {
				case ItemModel.NEWS_TITLE:
					itemtype = ItemModel.NEWS_TITLE;
					break;
				case ItemModel.NEWS_BODY:
					itemtype = ItemModel.NEWS_BODY;
					break;
				case ItemModel.NEWS_OPERATE:
					itemtype = ItemModel.NEWS_OPERATE;
					break;
				case ItemModel.NEWS_LIKELIST:
					itemtype = ItemModel.NEWS_LIKELIST;
					break;
				case ItemModel.NEWS_COMMENT:
					itemtype = ItemModel.NEWS_COMMENT;
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

		// 添加顶部布局与初始化事件
		header = View.inflate(mContext, R.layout.main_news_item_head_layout,
				null);// 头部内容
		newsListView.getRefreshableView().addHeaderView(header);
		LinearLayout headView = (LinearLayout) header
				.findViewById(R.id.layout_main_news_head_rootview);
		headView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转至圈子内容部分
				Intent intentToGroupNews = new Intent(mContext,
						MyGroupListActivity.class);
				startActivityWithRight(intentToGroupNews);
			}
		});

//		// 滑动刷新
//		newsListView.getRefreshableView().setOnScrollListener(
//				new OnScrollListener() {
//
//					@Override
//					public void onScrollStateChanged(AbsListView arg0, int arg1) {
//
//					}
//
//					@Override
//					public void onScroll(AbsListView absListView,
//							int firstVisibleItem, int visibleItemCount,
//							int totalItemCount) {
//
//						if (isAnimation) {
//							return;
//						}
//						
//						final int[] location = new int[2];  
//	                	topBarLayout.getLocationOnScreen(location);
//	                	
//						if(firstVisibleItem!=lastItem)
//			            {
//			                if(firstVisibleItem>lastItem)
//			                {
//			                	if (location[1] >= 0) {
//			                		AnimationSet animationSet = new AnimationSet(true);
//				                    TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f,0.0f,-topBarLayout.getHeight());
//				                    translateAnimation.setDuration(300);
//				                    animationSet.addAnimation(translateAnimation);
//				                    animationSet.setAnimationListener(new AnimationListener() {
//										@Override
//										public void onAnimationStart(Animation animation) {
//											isAnimation = true;
//										}
//										@Override
//										public void onAnimationRepeat(Animation animation) {
//										}
//										@Override
//										public void onAnimationEnd(Animation animation) {
//											RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) topBarLayout.getLayoutParams();
//											params.setMargins(0, -topBarLayout.getHeight(), 0, 0);
//											topBarLayout.setLayoutParams(params);
//											
//											RelativeLayout.LayoutParams listParams = (android.widget.RelativeLayout.LayoutParams) newsListView.getLayoutParams();
//											marginTop = listParams.topMargin;
//											listParams.setMargins(0, 0, 0, 0);
//											newsListView.setLayoutParams(listParams);
//											
//											isAnimation = false;
//										}
//									});
//				                    topBarLayout.startAnimation(animationSet);	
//								}
//		                		
//			                }else{
//			                	if (location[1] < 0) {
//			                		AnimationSet animationSet = new AnimationSet(true);
//				                    TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.f,-topBarLayout.getHeight(),0.0f);
//				                    translateAnimation.setDuration(300);
//				                    animationSet.addAnimation(translateAnimation);
//				                    animationSet.setAnimationListener(new AnimationListener() {
//										@Override
//										public void onAnimationStart(Animation animation) {
//											isAnimation = true;
//										}
//										@Override
//										public void onAnimationRepeat(Animation animation) {
//										}
//										@Override
//										public void onAnimationEnd(Animation animation) {
//											RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) topBarLayout.getLayoutParams();
//											params.setMargins(0, 0, 0, 0);
//											topBarLayout.setLayoutParams(params);
//											
//											RelativeLayout.LayoutParams listParams = (android.widget.RelativeLayout.LayoutParams) newsListView.getLayoutParams();
//											listParams.setMargins(0, (int) marginTop, 0, 0);
//											newsListView.setLayoutParams(listParams);
//											
//											isAnimation = false;
//										}
//									});
//				                    topBarLayout.startAnimation(animationSet);									
//			                	}
//			                }
//			                
//			                lastItem = firstVisibleItem;
////			                mScreenY = location[1];
//			            }else{
////			                if(mScreenY>location[1])
////			                {
////			                    Log.i("--->", "->向上滑动");
////			                }
////			                else if(mScreenY<location[1])
////			                {
////			                    Log.i("--->", "->向下滑动");
////			                }
////			                mScreenY = location[1];
//			            }
//					}
//					
//				});

		/**
		 * adapter的设置
		 * */
		newsAdapter = new HelloHaAdapter<ItemModel>(mContext, itemDataList,
				multiItemTypeSupport) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					ItemModel item) {

				switch (helper.layoutId) {
				case R.layout.mian_news_item_title_layout:
					setTitleItemView(helper, item);
					break;
				case R.layout.main_news_item_body_layout:
					setBodyItemView(helper, item);
					break;
				case R.layout.mian_news_item_operate_layout:
					setOperateItemView(helper, item);
					break;
				case R.layout.mian_news_item_likelist_layout:
					setLikeListItemView(helper, item);
					break;
				case R.layout.mian_news_item_comment_layout:
					setComentItemView(helper, item);
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
	 * 获取评论控件, 将评论view存储在map
	 * */
	private void getCommentWidget() {
		commentViewList = new ArrayList<Map<String, Integer>>();
		Map<String, Integer> commentMap = new HashMap<String, Integer>();
		commentMap.put("NAME", R.id.txt_comment_nameA);
		commentMap.put("CONTENT", R.id.txt_comment_contentA);
		commentMap.put("LAYOUT", R.id.layout_comment_A);
		commentViewList.add(commentMap);

		commentMap = new HashMap<String, Integer>();
		commentMap.put("NAME", R.id.txt_comment_nameB);
		commentMap.put("CONTENT", R.id.txt_comment_contentB);
		commentMap.put("LAYOUT", R.id.layout_comment_B);
		commentViewList.add(commentMap);

		commentMap = new HashMap<String, Integer>();
		commentMap.put("NAME", R.id.txt_comment_nameC);
		commentMap.put("CONTENT", R.id.txt_comment_contentC);
		commentMap.put("LAYOUT", R.id.layout_comment_C);
		commentViewList.add(commentMap);
	}

	/***
	 * 上次缓存的数据
	 * */
	@SuppressWarnings("unchecked")
	private void setLastData(int userID) {
		String path = JLXCConst.NEWS_LIST + "?" + "user_id=" + userID
				+ "&page=" + 1 + "&frist_time=";
		try {
			JSONObject JObject = HttpCacheUtils.getHttpCache(path);
			if (null != JObject) {
				JSONObject jResult = JObject
						.getJSONObject(JLXCConst.HTTP_RESULT);
				if (null != jResult) {
					List<JSONObject> JSONList = (List<JSONObject>) jResult
							.get(JLXCConst.HTTP_LIST);
					if (null != JSONList) {
						JsonToNewsModel(JSONList);
					}
				}
			}
		} catch (Exception e) {
			LogUtils.e("解析本地缓存错误.");
		}

	}

	/**
	 * titleItem的数据绑定与设置
	 * */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		TitleItem titleData = (TitleItem) item;
		// 显示头像
		if (null != titleData.getHeadSubImage()
				&& titleData.getHeadSubImage().length() > 0) {
			imgLoader.displayImage(titleData.getHeadSubImage(),
					(ImageView) helper.getView(R.id.img_mian_news_user_head),
					options);
		} else {
			((ImageView) helper.getView(R.id.img_mian_news_user_head))
					.setImageResource(R.drawable.default_avatar);
		}

		// 设置用户名，学校，标签
		helper.setText(R.id.txt_main_news_user_name, titleData.getUserName());
		helper.setText(R.id.txt_main_news_user_school,
				titleData.getUserSchool());
		if (titleData.getTagContent().equals("")) {
			helper.setVisible(R.id.txt_main_news_user_tag, false);
		} else {
			helper.setVisible(R.id.txt_main_news_user_tag, true);
			helper.setText(R.id.txt_main_news_user_tag,
					" ▪ " + titleData.getTagContent());
		}

		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.img_mian_news_user_head, listener);
		helper.setOnClickListener(R.id.txt_main_news_user_name, listener);
		helper.setOnClickListener(R.id.layout_news_title_rootview, listener);
		helper.setOnClickListener(R.id.txt_main_news_user_school, listener);
	}

	/**
	 * 设置新闻主体item
	 * */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper, ItemModel item) {
		final BodyItem bodyData = (BodyItem) item;
		List<ImageModel> pictureList = bodyData.getNewsImageListList();
		// MultiImageView bodyImages =
		// helper.getView(R.id.miv_main_news_images);
		MultiImageMetroView bodyImages = helper
				.getView(R.id.miv_main_news_images);
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
			helper.setVisible(R.id.txt_main_news_content, false);
		} else {
			//
			helper.setVisible(R.id.txt_main_news_content, true);
			TextView contentView = helper.getView(R.id.txt_main_news_content);
			contentView.setText(bodyData.getNewsContent());
			// customTvHandel.setTextContent(contentView);
			// 长按复制
			contentView.setOnLongClickListener(TextViewHandel
					.getLongClickListener(getActivity(),
							bodyData.getNewsContent()));
			// 点击
			helper.setOnClickListener(R.id.txt_main_news_content, listener);

		}
		// 设置地理位置
		if (bodyData.getLocation().equals("")) {
			helper.setVisible(R.id.txt_main_news_location, false);
		} else {
			helper.setVisible(R.id.txt_main_news_location, true);
			helper.setText(R.id.txt_main_news_location, bodyData.getLocation());
		}
		// 父布局监听
		helper.setOnClickListener(R.id.miv_main_news_images, listener);
		helper.setOnClickListener(R.id.layout_news_body_rootview, listener);
	}

	/**
	 * 设置操作部分item
	 * */
	private void setOperateItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		OperateItem opData = (OperateItem) item;
		// 点赞按钮
		LikeButton likeBtn = helper.getView(R.id.btn_news_like);
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
		// 绑定时间
		helper.setText(R.id.txt_main_news_publish_time,
				TimeHandle.getShowTimeFormat(opData.getSendTime()));
		// 是发到圈子里的东西
		if (opData.getTopicID() > 0) {
			helper.setVisible(R.id.txt_topic_name, true);
			// 显示修改
			helper.setText(R.id.txt_main_news_publish_time,
					TimeHandle.getShowTimeFormat(opData.getSendTime()));
			helper.setText(R.id.txt_topic_name, opData.getTopicName());
		} else {
			helper.setVisible(R.id.txt_topic_name, false);
		}
		// 事件监听绑定
		helper.setOnClickListener(R.id.btn_mian_reply, listener);
		helper.setOnClickListener(R.id.btn_news_like, listener);
		helper.setOnClickListener(R.id.layout_news_operate_rootview, listener);
		helper.setOnClickListener(R.id.txt_topic_name, listener);
	}

	/**
	 * 设置点赞部分item
	 * */
	private void setLikeListItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		LikeListItem lkData = (LikeListItem) item;
		List<LikeModel> lkImageList = lkData.getLikeHeadListimage();

		LikeImageListView likeControl = helper
				.getView(R.id.control_news_like_listview);
		int allCount = lkData.getLikeCount();
		String newsID = lkData.getNewsID();

		likeControl.dataInit(allCount, newsID);
		likeControl.listDataBindSet(lkImageList);
		likeControl.setEventListener(new EventCallBack() {

			@Override
			public void onItemClick(int userId) {
				jumpToHomepage(userId);
			}

			@Override
			public void onAllPersonBtnClick(String newsId) {
				// 跳转到点赞的人
				Intent intentToALLPerson = new Intent(mContext,
						AllLikePersonActivity.class);
				intentToALLPerson.putExtra(
						AllLikePersonActivity.INTENT_KEY_NEWS_ID, newsId);
				startActivityWithRight(intentToALLPerson);
			}
		});
	}

	/**
	 * 设置回复评论item
	 * */
	private void setComentItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		CommentListItem itemData = (CommentListItem) item;
		// 所有的评论数据
		List<CommentModel> commentList = itemData.getCommentList();

		// 显示三条评论
		for (int iCount = 0; iCount < NEWS_COMMENT_NUM; ++iCount) {
			if (iCount < commentList.size()) {
				// 设为显示
				helper.setVisible(commentViewList.get(iCount).get("LAYOUT")
						.intValue(), true);
				// 绑定数据
				helper.setText(commentViewList.get(iCount).get("NAME")
						.intValue(), commentList.get(iCount).getPublishName()
						+ " : ");
				helper.setText(commentViewList.get(iCount).get("CONTENT")
						.intValue(), commentList.get(iCount)
						.getCommentContent());
			} else {
				// 设为隐藏
				helper.setVisible(commentViewList.get(iCount).get("LAYOUT")
						.intValue(), false);
			}

		}
		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};

		// 显示加载更多评论的按钮
		if (commentList.size() < NEWS_COMMENT_NUM) {
			helper.setVisible(R.id.tv_more_comment, false);
		} else {
			helper.setVisible(R.id.tv_more_comment, true);
			helper.setText(R.id.tv_more_comment,
					"查看更多评论(" + itemData.getReplyCount() + ")...");
			helper.setOnClickListener(R.id.tv_more_comment, listener);
		}

		for (int iCount = 0; iCount < NEWS_COMMENT_NUM; ++iCount) {
			helper.setOnClickListener(commentViewList.get(iCount).get("NAME")
					.intValue(), listener);
			helper.setOnClickListener(commentViewList.get(iCount).get("LAYOUT")
					.intValue(), listener);
		}

	}

	/**
	 * 获取动态数据
	 * */
	private void getNewsData(int userID, int desPage, String lastTime) {
		String path = JLXCConst.NEWS_LIST + "?" + "user_id=" + userID
				+ "&page=" + desPage + "&frist_time=" + lastTime;

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
							ToastUtil.show(mContext, jsonResponse
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
						ToastUtil.show(mContext, "网络抽筋了，请检查(→_→)");
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
			newsAdapter.replaceAll(DataToItem.newsDataToItems(newDatas));
		} else {
			newsList.addAll(newDatas);
			newsAdapter.addAll(DataToItem.newsDataToItems(newDatas));
		}
		dataList.clear();
	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.layout_news_title_rootview:
			case R.id.img_mian_news_user_head:
			case R.id.txt_main_news_user_name:
			case R.id.txt_main_news_user_school:
				TitleItem titleData = (TitleItem) newsAdapter.getItem(postion);
				if (R.id.layout_news_title_rootview == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(titleData, NewsConstants.KEY_BOARD_CLOSE,
							null);
				} else if (R.id.txt_main_news_user_school == viewID) {
					// 跳转至校园主页
					Intent intentCampusInfo = new Intent(mContext,
							CampusHomeActivity.class);
					intentCampusInfo.putExtra(
							CampusHomeActivity.INTENT_SCHOOL_CODE_KEY,
							titleData.getSchoolCode());
					startActivityWithRight(intentCampusInfo);
				} else {
					jumpToHomepage(JLXCUtils.stringToInt(titleData.getUserID()));
				}
				break;
			case R.id.layout_news_body_rootview:
			case R.id.txt_main_news_content:
			case R.id.miv_main_news_images:
				BodyItem bodyData = (BodyItem) newsAdapter.getItem(postion);
				// 跳转到动态详情
				jumpToNewsDetail(bodyData, NewsConstants.KEY_BOARD_CLOSE, null);
				break;
			case R.id.btn_mian_reply:
			case R.id.btn_news_like:
			case R.id.layout_news_operate_rootview:
			case R.id.txt_topic_name:

				final OperateItem operateData = (OperateItem) newsAdapter
						.getItem(postion);
				if (R.id.layout_news_operate_rootview == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				} else if (R.id.btn_mian_reply == viewID) {
					// 跳转至评论页面并打开评论框
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_COMMENT, null);
				} else if (R.id.btn_news_like == viewID) {
					// 点赞操作
					likeOperate(postion, view, operateData);
				} else if (R.id.txt_topic_name == viewID) {
					// 确认有圈子
					if (operateData.getTopicID() > 0) {
						// 跳转至圈子内容部分
						Intent intentToGroupNews = new Intent();
						intentToGroupNews.setClass(getActivity(),
								GroupNewsActivity.class);
						// 传递名称
						intentToGroupNews.putExtra(
								GroupNewsActivity.INTENT_KEY_TOPIC_NAME,
								operateData.getTopicName());
						// 传递ID
						intentToGroupNews.putExtra(
								GroupNewsActivity.INTENT_KEY_TOPIC_ID,
								operateData.getTopicID());
						startActivityWithRight(intentToGroupNews);
					}
				}
				break;
			case R.id.txt_comment_nameA:
			case R.id.txt_comment_nameC:
			case R.id.txt_comment_nameB:
			case R.id.layout_comment_A:
			case R.id.layout_comment_B:
			case R.id.layout_comment_C:
			case R.id.tv_more_comment:
				CommentListItem commentData = (CommentListItem) newsAdapter
						.getItem(postion);
				if (R.id.tv_more_comment == viewID) {
					// 查看全部评论
					jumpToNewsDetail(commentData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				} else {
					for (int iCount = 0; iCount < NEWS_COMMENT_NUM; ++iCount) {
						if (viewID == commentViewList.get(iCount).get("NAME")) {
							jumpToHomepage(JLXCUtils.stringToInt(commentData
									.getCommentList().get(iCount).getUserId()));
						} else if (viewID == commentViewList.get(iCount).get(
								"LAYOUT")) {
							if (!commentData
									.getCommentList()
									.get(iCount)
									.getUserId()
									.equals(String.valueOf(UserManager
											.getInstance().getUser().getUid()))) {
								// 跳转至评论页面并打开评论框,并变为回复某某的状态
								jumpToNewsDetail(commentData,
										NewsConstants.KEY_BOARD_REPLY,
										commentData.getCommentList()
												.get(iCount).getCommentID());
							} else {
								// 自己发布的评论跳转到动态详情
								jumpToNewsDetail(commentData,
										NewsConstants.KEY_BOARD_CLOSE, null);
							}
						}
					}
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
			final OperateItem operateData) {

		final LikeButton oprtView = (LikeButton) view;
		final int likeListPostion = postion + 2;
		try {
			ListView nListView = newsListView.getRefreshableView();
			View itemRootView = nListView.getChildAt(likeListPostion + 1
					- nListView.getFirstVisiblePosition());
			currentLikeListControl = null;
			if (null != itemRootView) {
				// 点赞头像列表可见的情况下
				currentLikeListControl = (LikeImageListView) itemRootView
						.findViewById(R.id.control_news_like_listview);
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
	 * 跳转至用户的主页
	 */
	private void jumpToHomepage(int userID) {
		Intent intentUsrMain = new Intent(mContext, OtherPersonalActivity.class);
		intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentUsrMain);
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(ItemModel itemModel, int keyBoardMode,
			String commentId) {
		// 跳转到动态详情
		Intent intentToNewsDetail = new Intent(mContext,
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
		getActivity().overridePendingTransition(R.anim.push_right_in,
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
							newsAdapter.replaceAll(DataToItem
									.newsDataToItems(newsList));
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
							newsAdapter.replaceAll(DataToItem
									.newsDataToItems(newsList));
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
		int firstVisiblePosition = newsListView.getRefreshableView()
				.getFirstVisiblePosition();
		if (0 == firstVisiblePosition) {
			// 已经在顶部
			newsListView.setMode(Mode.PULL_FROM_START);
			newsListView.setRefreshing();
		} else {
			if (firstVisiblePosition < 20) {
				newsListView.getRefreshableView().smoothScrollToPosition(0);
			} else {
				newsListView.getRefreshableView().setSelection(20);
				newsListView.getRefreshableView().smoothScrollToPosition(0);
			}
			newsListView.getRefreshableView().clearFocus();
		}
	}
}
