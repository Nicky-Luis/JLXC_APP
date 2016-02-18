package com.jlxc.app.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
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
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.jlxc.app.base.ui.view.KeyboardLayout;
import com.jlxc.app.base.ui.view.KeyboardLayout.onKeyboardsChangeListener;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.ui.activity.GroupNewsActivity;
import com.jlxc.app.news.model.CommentModel;
import com.jlxc.app.news.model.ImageModel;
import com.jlxc.app.news.model.ItemModel;
import com.jlxc.app.news.model.ItemModel.BodyItem;
import com.jlxc.app.news.model.ItemModel.CommentItem;
import com.jlxc.app.news.model.ItemModel.LikeListItem;
import com.jlxc.app.news.model.ItemModel.SubCommentItem;
import com.jlxc.app.news.model.ItemModel.TitleItem;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.model.NewsModel;
import com.jlxc.app.news.model.SubCommentModel;
import com.jlxc.app.news.ui.view.LikeButton;
import com.jlxc.app.news.ui.view.LikeImageListView;
import com.jlxc.app.news.ui.view.LikeImageListView.EventCallBack;
import com.jlxc.app.news.ui.view.MultiImageMetroView;
import com.jlxc.app.news.ui.view.MultiImageMetroView.JumpCallBack;
import com.jlxc.app.news.ui.view.TextViewHandel;
import com.jlxc.app.news.utils.DataToItem;
import com.jlxc.app.news.utils.NewsOperate;
import com.jlxc.app.news.utils.NewsOperate.LikeCallBack;
import com.jlxc.app.news.utils.NewsOperate.OperateCallBack;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsDetailActivity extends BaseActivityWithTopBar {

	// 记录对动态的操作
	private String actionType = NewsConstants.OPERATE_NO_ACTION;
	// 评论的类型
	private int commentType = NewsConstants.Input_Type_Comment;
	// 主listview
	@ViewInject(R.id.news_detail_listView)
	private PullToRefreshListView newsDetailListView;
	// 评论输入框
	@ViewInject(R.id.edt_comment_input)
	private EditText commentEditText;
	// 评论发送按钮
	@ViewInject(R.id.btn_comment_send)
	private Button btnSendComment;
	// 数据源
	private List<ItemModel> dataList;
	// 主适配器
	private HelloHaAdapter<ItemModel> detailAdapter;
	// 当前的动态对象
	private NewsModel currentNews;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 使支持多种item
	private MultiItemTypeSupport<ItemModel> multiItemTypeSupport = null;
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 对动态的操作
	private NewsOperate<ItemModel> newsOPerate;
	// 评论的内容
	private String commentContent = "";
	// 当前的操作的item
	private CommentModel currentCommentModel;
	// 当前的操作的子评论对象
	private SubCommentModel currentSubCmtModel;
	// 当前操作的位置
	private int currentOperateIndex = 0;
	// 是否是第一次请求数据
	private boolean firstRequstData = true;
	// 点赞按钮
	private LikeButton likeBtn;
	// 已赞头像部件
	private LikeImageListView likeControl;

	/**
	 * 事件监听函数
	 * */
	@OnClick(value = { R.id.base_ll_right_btns, R.id.btn_comment_send })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 删除动态
		case R.id.base_ll_right_btns:
			deleteCurrentNews();
			break;

		// 发布评论
		case R.id.btn_comment_send:
			publishComment();
			break;
		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_news_detail;
	}

	@Override
	protected void setUpView() {
		init();
		multiItemTypeSet();
		listViewSet();
		newsOperateSet();

		// 监听输入框文本的变化
		commentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int start, int before,
					int count) {
				commentContent = str.toString().trim();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 监听软键盘是否打开
		((KeyboardLayout) findViewById(R.id.news_detail_root_view))
				.setOnkeyboarddStateListener(new onKeyboardsChangeListener() {

					@Override
					public void onKeyBoardStateChange(int state) {
						if (KeyboardLayout.KEYBOARD_STATE_HIDE == state) {
							// 内容为空并且，软键盘隐藏时
							if (commentContent.length() <= 0) {
								commentType = NewsConstants.Input_Type_Comment;
								commentEditText.setHint("是时候来条神评论了...");
							}
						}
					}
				});

		Intent intent = this.getIntent();
		if (null != intent) {
			if (intent.hasExtra(NewsConstants.INTENT_KEY_NEWS_OBJ)) {
				currentNews = (NewsModel) intent
						.getSerializableExtra(NewsConstants.INTENT_KEY_NEWS_OBJ);
				// 获取传递过来的的数据
				detailAdapter.replaceAll(DataToItem
						.newsDetailToItems(currentNews));
			} else if (intent.hasExtra(NewsConstants.INTENT_KEY_NEWS_ID)) {
				currentNews = new NewsModel();
				currentNews.setNewsID(intent
						.getStringExtra(NewsConstants.INTENT_KEY_NEWS_ID));
			} else {
				LogUtils.e("未传递任何动态信息到详情页面.");
			}
		} else {
			LogUtils.e("跳转到详情页面时，意图null.");
		}

		// 更新数据
		getNewsDetailData(
				String.valueOf(UserManager.getInstance().getUser().getUid()),
				currentNews.getNewsID());
	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		setBarText("详情");
		dataList = new ArrayList<ItemModel>();
		itemViewClickListener = new ItemViewClick();
		// 图片加载初始化
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 初始状态处理
	 * */
	private void stateHandel() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(NewsConstants.INTENT_KEY_COMMENT_STATE)) {
		case NewsConstants.KEY_BOARD_CLOSE:
			btnSendComment.setFocusable(true);
			break;
		case NewsConstants.KEY_BOARD_COMMENT:
			commentEditText.setFocusable(true);
			commentEditText.setFocusableInTouchMode(true);
			commentEditText.requestFocus();
			commentType = NewsConstants.Input_Type_Comment;
			setKeyboardStatu(true);
			break;
		case NewsConstants.KEY_BOARD_REPLY:
			// 直接回复评论
			String cmtId = bundle
					.getString(NewsConstants.INTENT_KEY_COMMENT_ID);

			for (int index = 0; index < dataList.size(); ++index) {
				ItemModel tempItemModel = dataList.get(index);
				int itemType = tempItemModel.getItemType();
				if (ItemModel.NEWS_DETAIL_COMMENT == itemType) {
					// 回复评论
					currentCommentModel = ((CommentItem) tempItemModel)
							.getCommentModel();
					if (currentCommentModel.getCommentID().equals(cmtId)) {
						commentEditText.setHint("回复："
								+ currentCommentModel.getPublishName());
						commentType = NewsConstants.Input_Type_SubComment;
						break;
					}
				} else if (ItemModel.NEWS_DETAIL_SUB_COMMENT == itemType) {
					// 回复子评论
					currentSubCmtModel = ((SubCommentItem) tempItemModel)
							.getSubCommentModel();
					if (currentSubCmtModel.getSubID().equals(cmtId)) {
						commentEditText.setHint("回复："
								+ currentSubCmtModel.getPublishName());
						commentType = NewsConstants.Input_Type_SubReply;
						break;
					}
				}
				currentOperateIndex = index + 1;
			}
			commentEditText.setFocusable(true);
			commentEditText.setFocusableInTouchMode(true);
			commentEditText.requestFocus();
			setKeyboardStatu(true);

			break;

		default:
			break;
		}
	}

	/**
	 * 设置键盘状态
	 * */
	private void setKeyboardStatu(boolean state) {
		if (state) {
			InputMethodManager imm = (InputMethodManager) commentEditText
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		} else {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(NewsDetailActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 动态操作设置
	 * */
	private void newsOperateSet() {
		newsOPerate = new NewsOperate<ItemModel>(NewsDetailActivity.this);
		newsOPerate.setOperateListener(new OperateCallBack() {
			@Override
			public void onStart(int operateType) {
				switch (operateType) {
				case NewsOperate.OP_Type_Delete_News:
					// 删除动态
					break;

				case NewsOperate.OP_Type_Add_Comment:
					showLoading("努力发布中...", true);

				case NewsOperate.OP_Type_Delete_Comment:
					break;

				case NewsOperate.OP_Type_Add_Sub_Comment: {
					showLoading("努力发布中...", true);
				}
					break;

				case NewsOperate.OP_Type_Delete_Sub_Comment:

					break;
				default:
					break;
				}

			}

			@Override
			public void onFinish(int operateType, boolean isSucceed,
					Object resultValue) {
				actionType = NewsConstants.OPERATE_UPDATE;
				switch (operateType) {
				case NewsOperate.OP_Type_Delete_News:
					if (isSucceed) {
						ToastUtil.show(NewsDetailActivity.this, "删除成功");
						// 返回上一页
						actionType = NewsConstants.OPERATE_DELETET;
						finishWithRight();
					}
					break;

				case NewsOperate.OP_Type_Add_Comment:
					if (isSucceed) {
						// 发布成功则更新评论
						CommentModel resultmModel = (CommentModel) resultValue;
						detailAdapter.add(DataToItem.createComment(
								resultmModel, ItemModel.NEWS_DETAIL_COMMENT));
						// 滚动到底部
						newsDetailListView.getRefreshableView().setSelection(
								detailAdapter.getCount() - 1);
						hideLoading();
					} else {
						hideLoading();
					}
					break;

				case NewsOperate.OP_Type_Delete_Comment:
					if (isSucceed) {
						detailAdapter.remove(currentOperateIndex);
						String topID = currentCommentModel.getCommentID();
						// 删除所属的子评论
						while (currentOperateIndex < detailAdapter.getCount()) {
							if (ItemModel.NEWS_DETAIL_SUB_COMMENT == detailAdapter
									.getItem(currentOperateIndex).getItemType()) {
								if (((SubCommentItem) detailAdapter
										.getItem(currentOperateIndex))
										.getSubCommentModel().getTopCommentId()
										.equals(topID)) {
									detailAdapter.remove(currentOperateIndex);
								} else {
									break;
								}
							} else {
								break;
							}
						}
						ToastUtil.show(NewsDetailActivity.this, "删除成功");
					} else {
						ToastUtil.show(NewsDetailActivity.this, "竟然删除失败，检查网络");
					}
					break;

				case NewsOperate.OP_Type_Add_Sub_Comment:
					if (isSucceed) {
						// 发布成功则更新评论
						SubCommentModel resultmModel = (SubCommentModel) resultValue;
						// 找到需要插入的位置
						int index = currentOperateIndex + 1;
						while (index < detailAdapter.getCount()) {
							int itemType = detailAdapter.getItem(index)
									.getItemType();
							if (ItemModel.NEWS_DETAIL_COMMENT == itemType) {
								break;
							}
							index++;
						}
						// 插入子评论
						detailAdapter.insert(index, DataToItem
								.createSubComment(resultmModel,
										currentNews.getNewsID(),
										ItemModel.NEWS_DETAIL_SUB_COMMENT));
						// 滚动到添加的评论处
						newsDetailListView.getRefreshableView().setSelection(
								index);
						hideLoading();
					} else {
						hideLoading();
					}
					break;

				case NewsOperate.OP_Type_Delete_Sub_Comment:
					if (isSucceed) {
						detailAdapter.remove(currentOperateIndex);
						ToastUtil.show(NewsDetailActivity.this, "删除成功");
					} else {
						ToastUtil.show(NewsDetailActivity.this, "竟然删除失败");
					}
					break;
				default:
					break;
				}
			}
		});
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
				case ItemModel.NEWS_DETAIL_TITLE:
					layoutId = R.layout.news_detail_title_layout;
					break;
				case ItemModel.NEWS_DETAIL_BODY:
					layoutId = R.layout.news_detail_body_layout;
					break;
				case ItemModel.NEWS_DETAIL_LIKELIST:
					layoutId = R.layout.news_detail_likelist_layout;
					break;
				case ItemModel.NEWS_DETAIL_COMMENT:
					layoutId = R.layout.news_detail_comment_layout;
					break;
				case ItemModel.NEWS_DETAIL_SUB_COMMENT:
					layoutId = R.layout.news_detail_subcomment_layout;
					break;
				default:
					break;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return ItemModel.NEWS_DETAIL_ITEM_TYPE_COUNT;
			}

			@Override
			public int getItemViewType(int postion, ItemModel itemData) {
				int itemtype = 0;
				switch (itemData.getItemType()) {
				case ItemModel.NEWS_DETAIL_TITLE:
					itemtype = ItemModel.NEWS_DETAIL_TITLE;
					break;
				case ItemModel.NEWS_DETAIL_BODY:
					itemtype = ItemModel.NEWS_DETAIL_BODY;
					break;
				case ItemModel.NEWS_DETAIL_LIKELIST:
					itemtype = ItemModel.NEWS_DETAIL_LIKELIST;
					break;
				case ItemModel.NEWS_DETAIL_COMMENT:
					itemtype = ItemModel.NEWS_DETAIL_COMMENT;
					break;
				case ItemModel.NEWS_DETAIL_SUB_COMMENT:
					itemtype = ItemModel.NEWS_DETAIL_SUB_COMMENT;
					break;
				default:
					break;
				}
				return itemtype;

			}
		};
	}

	/**
	 * listview设置
	 * */
	private void listViewSet() {

		// 设置刷新模式
		newsDetailListView.setMode(Mode.PULL_FROM_START);
		/**
		 * 刷新监听
		 * */
		newsDetailListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						firstRequstData = false;
						getNewsDetailData(
								String.valueOf(UserManager.getInstance()
										.getUser().getUid()),
								currentNews.getNewsID());
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 上拉
						/*
						 * getNewsDetailData(String.valueOf(userModel.getUid()),
						 * currentNews.getNewsID());
						 */
					}
				});

		/**
		 * adapter的设置
		 * */
		detailAdapter = new HelloHaAdapter<ItemModel>(NewsDetailActivity.this,
				dataList, multiItemTypeSupport) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					ItemModel item) {

				switch (helper.layoutId) {
				case R.layout.news_detail_title_layout:
					setTitleItemView(helper, item);
					break;
				case R.layout.news_detail_body_layout:
					setBodyItemView(helper, item);
					break;
				case R.layout.news_detail_likelist_layout:
					setLikeListItemView(helper, item);
					break;
				case R.layout.news_detail_comment_layout:
					setComentItemView(helper, item);
					break;
				case R.layout.news_detail_subcomment_layout:
					setSubComentItemView(helper, item);
					break;

				default:
					break;
				}
			}
		};

		// 设置不可点击
		detailAdapter.setItemsClickEnable(false);
		newsDetailListView.setAdapter(detailAdapter);
	}

	/**
	 * titleItem的数据绑定与设置
	 * */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		TitleItem titleData = (TitleItem) item;

		// 显示头像
		imgLoader.displayImage(titleData.getHeadSubImage(),
				(ImageView) helper.getView(R.id.img_news_detail_user_head),
				options);

		// 设置用户名,发布的时间，标签
		helper.setText(R.id.txt_news_detail_user_name, titleData.getUserName());
		helper.setText(R.id.txt_news_detail_user_tag, titleData.getUserSchool());
		// 点赞按钮
		likeBtn = helper.getView(R.id.btn_news_detail_like);
		if (titleData.getIsLike()) {
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
		helper.setOnClickListener(R.id.img_news_detail_user_head, listener);
		helper.setOnClickListener(R.id.txt_news_detail_user_name, listener);
		helper.setOnClickListener(R.id.btn_news_detail_like, listener);
		helper.setOnClickListener(R.id.txt_news_detail_user_tag, listener);
	}

	/**
	 * 设置新闻主体item
	 * */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper, ItemModel item) {
		final BodyItem bodyData = (BodyItem) item;
		List<ImageModel> pictureList = bodyData.getNewsImageListList();

		// MultiImageView bodyImages =
		// helper.getView(R.id.miv_news_detail_images);
		MultiImageMetroView bodyImages = helper
				.getView(R.id.miv_news_detail_images);
		bodyImages.imageDataSet(pictureList);

		bodyImages.setJumpListener(new JumpCallBack() {

			@Override
			public void onImageClick(Intent intentToimageoBig) {
				startActivity(intentToimageoBig);
			}
		});

		// 设置 文字内容
		if (bodyData.getNewsContent().equals("")) {
			helper.setVisible(R.id.txt_news_detail_content, false);
		} else {
			helper.setVisible(R.id.txt_news_detail_content, true);
			TextView contentView = helper.getView(R.id.txt_news_detail_content);
			// customTvHandel.setTextContent(contentView);
			contentView.setText(bodyData.getNewsContent());
			// 长按复制
			contentView.setOnLongClickListener(TextViewHandel
					.getLongClickListener(NewsDetailActivity.this,
							bodyData.getNewsContent()));
		}
		// 设置地理位置
		if (bodyData.getLocation().equals("")) {
			helper.setVisible(R.id.txt_news_detail_location, false);
		} else {
			helper.setVisible(R.id.txt_news_detail_location, true);
			helper.setText(R.id.txt_news_detail_location,
					bodyData.getLocation());
		}
		// 发布时间
		helper.setText(R.id.txt_news_detail_publish_time,
				TimeHandle.getShowTimeFormat(bodyData.getSendTime()));
		// 是发到圈子里的东西
		if (bodyData.getTopicID() > 0) {
			helper.setVisible(R.id.txt_news_detial_topic_name, true);
			// 显示修改
			helper.setText(R.id.txt_news_detail_publish_time,
					TimeHandle.getShowTimeFormat(bodyData.getSendTime()));
			helper.setText(R.id.txt_news_detial_topic_name,
					bodyData.getTopicName());
		} else {
			helper.setVisible(R.id.txt_news_detial_topic_name, false);
		}
		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.txt_news_detial_topic_name, listener);
	}

	/**
	 * 设置点赞部分item
	 * */
	private void setLikeListItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		likeControl = helper.getView(R.id.control_like_listview);

		likeControl.dataInit(
				JLXCUtils.stringToInt(currentNews.getLikeQuantity()),
				currentNews.getNewsID());
		likeControl.listDataBindSet(currentNews.getLikeHeadListimage());
		likeControl.setEventListener(new EventCallBack() {

			@Override
			public void onItemClick(int userId) {
				JumpToHomepage(userId);
			}

			@Override
			public void onAllPersonBtnClick(String newsId) {
				// 跳转到点赞的人
				Intent intentToALLPerson = new Intent(NewsDetailActivity.this,
						AllLikePersonActivity.class);
				intentToALLPerson.putExtra(
						AllLikePersonActivity.INTENT_KEY_NEWS_ID, newsId);
				startActivityWithRight(intentToALLPerson);
			}
		});
	}

	/**
	 * 设置评论item
	 * */
	private void setComentItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		CommentModel comment = ((CommentItem) item).getCommentModel();

		// 显示评论头像
		imgLoader.displayImage(comment.getHeadSubImage(),
				(ImageView) helper.getView(R.id.iv_comment_head), options);
		// 设置评论的时间、学校与内容
		helper.setText(R.id.txt_news_detail_comment_time,
				TimeHandle.getShowTimeFormat(comment.getAddDate()));
		helper.setText(R.id.txt_news_detail_comment_name,
				comment.getPublishName());
		// 内容控件
		TextView contentView = helper
				.getView(R.id.txt_news_detail_comment_content);
		contentView.setText(comment.getCommentContent());
		// 设置长按复制
		helper.setOnLongClickListener(
				R.id.layout_news_detail_comment_root_view, TextViewHandel
						.getLongClickListener(NewsDetailActivity.this,
								comment.getCommentContent()));

		// 设置评论item的点击事件
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.iv_comment_head, listener);
		helper.setOnClickListener(R.id.layout_news_detail_comment_root_view,
				listener);
		helper.setOnClickListener(R.id.txt_news_detail_comment_name, listener);
	}

	/**
	 * 设置子评论item
	 * */
	private void setSubComentItemView(HelloHaBaseAdapterHelper helper,
			ItemModel item) {
		SubCommentModel subCmtModel = ((SubCommentItem) item)
				.getSubCommentModel();

		helper.setText(R.id.txt_sub_comment_by_name,
				subCmtModel.getPublishName());
		helper.setText(R.id.txt_by_sub_comment_name, subCmtModel.getReplyName());
		// 内容控件
		TextView contentView = helper.getView(R.id.txt_sub_comment_content);
		contentView.setText(subCmtModel.getCommentContent());
		// 设置长按复制
		helper.setOnLongClickListener(R.id.subcomment_root_view, TextViewHandel
				.getLongClickListener(NewsDetailActivity.this,
						subCmtModel.getCommentContent()));

		// 设置评论item的点击事件
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.subcomment_root_view, listener);
		helper.setOnClickListener(R.id.txt_sub_comment_by_name, listener);
		helper.setOnClickListener(R.id.txt_by_sub_comment_name, listener);
	}

	/**
	 * 数据处理
	 */
	private void JsonToNewsModel(JSONObject data) {
		currentNews.setContentWithJson(data);
		// 如果是自己发布的动态
		if (true/*currentNews.getUid().equals(
				String.valueOf(UserManager.getInstance().getUser().getUid()))*/) {
			addRightImgBtn(R.layout.right_image_button,
					R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		}
		dataList = DataToItem.newsDetailToItems(currentNews);
		detailAdapter.replaceAll(dataList);
	}

	/**
	 * 获取动态详情数据
	 * */
	private void getNewsDetailData(String uid, String newsId) {
		String path = JLXCConst.NEWS_DETAIL + "?" + "news_id=" + newsId
				+ "&user_id=" + uid;
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
							JsonToNewsModel(jResult);
							if (firstRequstData) {
								stateHandel();
							} else {
								newsDetailListView.onRefreshComplete();
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(NewsDetailActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));

							if (!firstRequstData) {
								newsDetailListView.onRefreshComplete();
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						if (!firstRequstData) {
							newsDetailListView.onRefreshComplete();
						}
						ToastUtil.show(NewsDetailActivity.this, "网络有毒=_=");
					}

				}, null));
	}

	/**
	 * 删除动态
	 * */
	private void deleteCurrentNews() {
		final CustomAlertDialog confirmDialog = new CustomAlertDialog(
				NewsDetailActivity.this, "真的狠心删除吗？", "狠心", "舍不得");
		confirmDialog.show();
		confirmDialog
				.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						newsOPerate.deleteNews(currentNews.getNewsID());
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});
	}

	// 点击回复发送按钮
	private void publishComment() {
		if (commentContent.length() > 0) {
			String tempContent = commentContent;
			// 清空输入内容
			commentEditText.setText("");
			commentEditText.setHint("来条神评论...");
			// 隐藏输入键盘
			setKeyboardStatu(false);

			switch (commentType) {
			case NewsConstants.Input_Type_Comment:
				// 发布评论
				CommentModel temMode = new CommentModel();
				temMode.setCommentContent(tempContent);
				temMode.setAddDate(TimeHandle.getCurrentDataStr());
				newsOPerate.publishComment(UserManager.getInstance().getUser(),
						currentNews.getNewsID(), tempContent);
				break;

			case NewsConstants.Input_Type_SubComment:
				// 发布二级评论
				SubCommentModel tempMd = new SubCommentModel();
				tempMd.setCommentContent(tempContent);
				tempMd.setReplyUid(currentCommentModel.getUserId());
				tempMd.setReplyName(currentCommentModel.getPublishName());
				tempMd.setReplyCommentId(currentCommentModel.getCommentID());
				tempMd.setTopCommentId(currentCommentModel.getCommentID());
				newsOPerate.publishSubComment(UserManager.getInstance()
						.getUser(), currentNews.getNewsID(), tempMd);
				break;

			case NewsConstants.Input_Type_SubReply:
				// 发布子回复
				SubCommentModel tpMold = new SubCommentModel();
				tpMold.setCommentContent(tempContent);
				tpMold.setReplyUid(currentSubCmtModel.getPublishId());
				tpMold.setReplyName(currentSubCmtModel.getPublishName());
				tpMold.setReplyCommentId(currentSubCmtModel.getSubID());
				tpMold.setTopCommentId(currentSubCmtModel.getTopCommentId());
				newsOPerate.publishSubComment(UserManager.getInstance()
						.getUser(), currentNews.getNewsID(), tpMold);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.img_news_detail_user_head:
			case R.id.txt_news_detail_user_name:
				TitleItem titleData = (TitleItem) detailAdapter
						.getItem(postion);
				JumpToHomepage(JLXCUtils.stringToInt(titleData.getUserID()));
				break;
			case R.id.txt_news_detail_user_tag:
				TitleItem schoolData = (TitleItem) detailAdapter
						.getItem(postion);
				// 跳转至校园主页
				Intent intentCampusInfo = new Intent(NewsDetailActivity.this,
						CampusHomeActivity.class);
				intentCampusInfo.putExtra(
						CampusHomeActivity.INTENT_SCHOOL_CODE_KEY,
						schoolData.getSchoolCode());
				startActivityWithRight(intentCampusInfo);
				break;
			case R.id.btn_news_detail_like:
				actionType = NewsConstants.OPERATE_UPDATE;
				likeOperate();
				break;
			case R.id.txt_news_detial_topic_name:
				// 圈子
				BodyItem bodyData = (BodyItem) detailAdapter.getItem(postion);
				// 确认有圈子
				if (bodyData.getTopicID() > 0) {
					// 跳转至圈子内容部分
					Intent intentToGroupNews = new Intent();
					intentToGroupNews.setClass(NewsDetailActivity.this,
							GroupNewsActivity.class);
					// 传递名称
					intentToGroupNews.putExtra(
							GroupNewsActivity.INTENT_KEY_TOPIC_NAME,
							bodyData.getTopicName());
					// 传递ID
					intentToGroupNews.putExtra(
							GroupNewsActivity.INTENT_KEY_TOPIC_ID,
							bodyData.getTopicID());
					startActivityWithRight(intentToGroupNews);
				}
				break;
			case R.id.layout_news_detail_comment_root_view:
			case R.id.iv_comment_head:
			case R.id.txt_news_detail_comment_name:
				currentCommentModel = ((CommentItem) detailAdapter
						.getItem(postion)).getCommentModel();
				if (viewID == R.id.layout_news_detail_comment_root_view) {
					currentOperateIndex = postion;
					if (currentCommentModel.getUserId().equals(
							String.valueOf(UserManager.getInstance().getUser()
									.getUid()))) {
						// 如果是自己发布的评论，则删除评论
						List<String> menuList = new ArrayList<String>();
						menuList.add("删除评论");
						final CustomListViewDialog downDialog = new CustomListViewDialog(
								NewsDetailActivity.this, menuList);
						downDialog.setClickCallBack(new ClickCallBack() {

							@Override
							public void Onclick(View view, int which) {
								newsOPerate.deleteComment(
										currentCommentModel.getCommentID(),
										currentNews.getNewsID());
								downDialog.cancel();
							}
						});
						downDialog.show();
					} else {
						// 发布回复别人的评论
						commentEditText.requestFocus();
						commentEditText.setHint("回复："
								+ currentCommentModel.getPublishName());
						commentType = NewsConstants.Input_Type_SubComment;
						// 显示键盘
						setKeyboardStatu(true);
					}
				} else {
					JumpToHomepage(JLXCUtils.stringToInt(currentCommentModel
							.getUserId()));
				}
				break;

			// 点击了子评论
			case R.id.subcomment_root_view:
			case R.id.txt_sub_comment_by_name:
			case R.id.txt_by_sub_comment_name:
				currentSubCmtModel = ((SubCommentItem) detailAdapter
						.getItem(postion)).getSubCommentModel();
				if (viewID == R.id.subcomment_root_view) {
					currentOperateIndex = postion;
					if (currentSubCmtModel.getPublishId().equals(
							String.valueOf(UserManager.getInstance().getUser()
									.getUid()))) {
						// 如果是自己发布的评论，则删除评论
						List<String> menuList = new ArrayList<String>();
						menuList.add("删除回复");
						final CustomListViewDialog downDialog = new CustomListViewDialog(
								NewsDetailActivity.this, menuList);
						downDialog.setClickCallBack(new ClickCallBack() {

							@Override
							public void Onclick(View view, int which) {
								newsOPerate.deleteSubComment(
										currentSubCmtModel.getSubID(),
										currentNews.getNewsID());
								downDialog.cancel();
							}
						});
						downDialog.show();
					} else {
						// 找到topComment
						int index = postion;
						while (index >= 0) {
							if (ItemModel.NEWS_DETAIL_COMMENT == detailAdapter
									.getItem(index).getItemType()) {
								break;
							}
							--index;
						}
						currentCommentModel = ((CommentItem) detailAdapter
								.getItem(index)).getCommentModel();

						commentEditText.requestFocus();
						commentEditText.setHint("回复："
								+ currentSubCmtModel.getPublishName());
						commentType = NewsConstants.Input_Type_SubReply;
						setKeyboardStatu(true);
					}
				} else if (viewID == R.id.txt_sub_comment_by_name) {
					JumpToHomepage(JLXCUtils.stringToInt(currentSubCmtModel
							.getPublishId()));
				} else {
					JumpToHomepage(JLXCUtils.stringToInt(currentSubCmtModel
							.getReplyUid()));
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
	private void likeOperate() {
		final TitleItem operateData = (TitleItem) detailAdapter.getItem(0);
		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onOperateStart(boolean isLike) {
				if (isLike) {
					// 点赞操作
					if (null != likeControl) {
						newsOPerate.addHeadToLikeList(likeControl);
					} else {
						newsOPerate.addDataToLikeList(detailAdapter, 2);
					}
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() + 1));
					likeBtn.setStatue(true);
					operateData.setIsLike("1");
				} else {
					// 取消点赞
					if (null != likeControl) {
						newsOPerate.removeHeadFromLikeList(likeControl);
					} else {
						newsOPerate.removeDataFromLikeList(detailAdapter, 2);
					}
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() - 1));
					likeBtn.setStatue(false);
					operateData.setIsLike("0");
				}
			}

			@Override
			public void onOperateFail(boolean isLike) {
				// 撤销上次
				newsOPerate.operateRevoked();
				if (isLike) {
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() - 1));
					likeBtn.setStatue(false);
					operateData.setIsLike("0");
				} else {
					operateData.setLikeCount(String.valueOf(operateData
							.getLikeCount() + 1));
					likeBtn.setStatue(true);
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
	 * 跳转至用户的主页
	 */
	private void JumpToHomepage(int userID) {
		Intent intentUsrMain = new Intent(NewsDetailActivity.this,
				OtherPersonalActivity.class);
		intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentUsrMain);
	}

	// 重写
	@Override
	public void finishWithRight() {
		updateResultData();
		super.finishWithRight();
	}

	// 监听返回事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finishWithRight();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 保存对动态的数据，并广播给上一个activity
	 * */
	private void updateResultData() {
		Intent mIntent = new Intent(JLXCConst.BROADCAST_NEWS_LIST_REFRESH);
		if (actionType.equals(NewsConstants.OPERATE_UPDATE)) {

			// 点赞数据
			LikeListItem likeData = (LikeListItem) detailAdapter.getItem(2);
			List<LikeModel> newlkList = likeData.getLikeHeadListimage();
			// 评论数据
			TitleItem titleData = (TitleItem) detailAdapter.getItem(0);
			String isLike = "0";
			if (titleData.getIsLike()) {
				isLike = "1";
			}
			// 最新的评论数据
			List<CommentModel> newCmtList = new ArrayList<CommentModel>();
			for (int index = 0; index < detailAdapter.getCount(); index++) {
				if (ItemModel.NEWS_DETAIL_COMMENT == detailAdapter.getItem(
						index).getItemType()) {
					newCmtList.add(((CommentItem) detailAdapter.getItem(index))
							.getCommentModel());
				}
			}

			currentNews.setIsLike(isLike);
			currentNews.setLikeQuantity(titleData.getLikeCount() + "");
			currentNews.setCommentQuantity(String.valueOf(detailAdapter
					.getCount() - 3));
			currentNews.setLikeHeadListimage(newlkList);
			currentNews.setCommentList(newCmtList);
			mIntent.putExtra(NewsConstants.OPERATE_UPDATE, currentNews);
		} else if (actionType.equals(NewsConstants.OPERATE_DELETET)) {
			// 删除操作
			mIntent.putExtra(NewsConstants.OPERATE_DELETET,
					currentNews.getNewsID());
		} else if (actionType.equals(NewsConstants.OPERATE_NO_ACTION)) {
			// 没有操作
			mIntent.putExtra(NewsConstants.OPERATE_NO_ACTION, "");
		}
		// 发送广播
		LocalBroadcastManager.getInstance(NewsDetailActivity.this)
				.sendBroadcast(mIntent);
	}
}
