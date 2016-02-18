package com.jlxc.app.news.utils;

import java.util.List;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.news.model.CommentModel;
import com.jlxc.app.news.model.ItemModel;
import com.jlxc.app.news.model.ItemModel.LikeListItem;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.news.model.SubCommentModel;
import com.jlxc.app.news.ui.view.LikeImageListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

public class NewsOperate<T> {
	// 操作类型
	public final static int OP_Type_Delete_News = 0;
	public final static int OP_Type_Add_Comment = 1;
	public final static int OP_Type_Delete_Comment = 2;
	public final static int OP_Type_Add_Sub_Comment = 3;
	public final static int OP_Type_Delete_Sub_Comment = 4;
	public final static int OP_Type_Like = 5;
	public final static int OP_Type_Like_cancel = 6;
	public final static int OP_Type_News_Like = 7;
	public final static int OP_Type_News_Like_cancel = 8;

	// 记录上次操作
	private int lastOperateType = -1;
	// 上下文
	private Context mContext;
	// 回调接口
	private OperateCallBack callInterface;
	// 点赞回调接口
	private LikeCallBack likeCallInterface;
	// 上次点赞操作的listview
	private LikeImageListView likeListView;
	// 点赞操作对应的动态adapter
	private HelloHaAdapter<T> newsAdapter;
	// 记录动态点赞的操作位置
	private int lastPostion = 0;
	// 是否正在传输数据
	private boolean isUploadData = false;

	public NewsOperate(Context context) {
		this.mContext = context;
	}

	// 设置回调
	public void setOperateListener(OperateCallBack callInterface) {
		this.callInterface = callInterface;
	}

	// 设置点赞回调
	public void setLikeListener(LikeCallBack callInterface) {
		this.likeCallInterface = callInterface;
	}

	/**
	 * 删除动态操作
	 * */
	public void deleteNews(String newsId) {
		lastOperateType = OP_Type_Delete_News;
		callInterface.onStart(OP_Type_Delete_News);
		RequestParams params = new RequestParams();
		params.addBodyParameter("news_id", newsId);

		HttpManager.post(JLXCConst.DELETE_NEWS, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							callInterface.onFinish(OP_Type_Delete_News, true,
									null);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							callInterface.onFinish(OP_Type_Delete_News, false,
									null);
							ToastUtil.show(mContext, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						callInterface
								.onFinish(OP_Type_Delete_News, false, null);
						ToastUtil.show(mContext, "竟然删除失败，请检查网络 （/TДT)/");
					}
				}, null));
	}

	/**
	 * 发送一级评论
	 * */
	public void publishComment(final UserModel user, String newsID,
			String content) {
		lastOperateType = OP_Type_Add_Comment;
		callInterface.onStart(OP_Type_Add_Comment);
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", String.valueOf(user.getUid()));
		params.addBodyParameter("news_id", newsID);
		params.addBodyParameter("comment_content", content);

		HttpManager.post(JLXCConst.SEND_COMMENT, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							// 获取评论成功的返回值
							JSONObject JResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							CommentModel temMode = new CommentModel();
							temMode.setContentWithJson(JResult);
							temMode.setPublishName(user.getName());
							temMode.setHeadSubImage(JLXCConst.ATTACHMENT_ADDR
									+ user.getHead_sub_image());
							callInterface.onFinish(OP_Type_Add_Comment, true,
									temMode);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							callInterface.onFinish(OP_Type_Add_Comment, false,
									null);
							ToastUtil.show(mContext, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						callInterface
								.onFinish(OP_Type_Add_Comment, false, null);
						ToastUtil.show(mContext, "竟然评论失败，请检查网络 （/TДT)/");
					}
				}, null));
	}

	/**
	 * 发送二级评论
	 * */
	public void publishSubComment(final UserModel user, String newsID,
			SubCommentModel subModle) {
		lastOperateType = OP_Type_Add_Sub_Comment;
		callInterface.onStart(OP_Type_Add_Sub_Comment);
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", String.valueOf(user.getUid()));
		params.addBodyParameter("news_id", newsID);
		params.addBodyParameter("comment_content", subModle.getCommentContent());
		params.addBodyParameter("reply_uid", subModle.getReplyUid());
		params.addBodyParameter("reply_comment_id",
				subModle.getReplyCommentId());
		params.addBodyParameter("top_comment_id", subModle.getTopCommentId());

		final String replyToName = subModle.getReplyName();
		HttpManager.post(JLXCConst.SEND_SECOND_COMMENT, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject JResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);

							SubCommentModel tempMd = new SubCommentModel();
							tempMd.setContentWithJson(JResult);
							tempMd.setReplyName(replyToName);
							tempMd.setPublishName(user.getName());
							tempMd.setPublishId(String.valueOf(user.getUid()));
							callInterface.onFinish(OP_Type_Add_Sub_Comment,
									true, tempMd);

						}

						if (status == JLXCConst.STATUS_FAIL) {
							callInterface.onFinish(OP_Type_Add_Sub_Comment,
									false, null);
							ToastUtil.show(mContext, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						callInterface.onFinish(OP_Type_Add_Sub_Comment, false,
								null);
						ToastUtil.show(mContext, "竟然回复失败，请检查网络 （/TДT)/");
					}
				}, null));
	}

	/**
	 * 删除一级评论
	 * */
	public void deleteComment(String CID, String newsID) {
		lastOperateType = OP_Type_Delete_Comment;
		callInterface.onStart(OP_Type_Delete_Comment);
		RequestParams params = new RequestParams();
		params.addBodyParameter("cid", CID);
		params.addBodyParameter("news_id", newsID);

		HttpManager.post(JLXCConst.DELETE_COMMENT, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							callInterface.onFinish(OP_Type_Delete_Comment,
									true, null);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							callInterface.onFinish(OP_Type_Delete_Comment,
									false, null);
							ToastUtil.show(mContext, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						callInterface.onFinish(OP_Type_Delete_Comment, false,
								null);
					}
				}, null));
	}

	/**
	 * 删除二级评论
	 * */
	public void deleteSubComment(String CID, String newsID) {
		lastOperateType = OP_Type_Delete_Sub_Comment;
		callInterface.onStart(OP_Type_Delete_Sub_Comment);
		RequestParams params = new RequestParams();
		params.addBodyParameter("cid", CID);
		params.addBodyParameter("news_id", newsID);

		HttpManager.post(JLXCConst.DELETE_SECOND_COMMENT, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							callInterface.onFinish(OP_Type_Delete_Sub_Comment,
									true, null);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							callInterface.onFinish(OP_Type_Delete_Sub_Comment,
									false, null);
							ToastUtil.show(mContext, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						callInterface.onFinish(OP_Type_Delete_Sub_Comment,
								false, null);
						super.onFailure(arg0, arg1, flag);
					}
				}, null));
	}

	/**
	 * 点赞操作网络请求
	 */
	public void uploadLikeOperate(String newsId, boolean isLike) {
		if (!isUploadData) {
			isUploadData = true;
			if (isLike) {
				likeCallInterface.onOperateStart(true);
			} else {
				likeCallInterface.onOperateStart(false);
			}
			// 参数设置
			RequestParams params = new RequestParams();
			params.addBodyParameter("news_id", newsId);
			if (isLike) {
				params.addBodyParameter("isLike", "1");
			} else {
				params.addBodyParameter("isLike", "0");
			}
			params.addBodyParameter("user_id", String.valueOf(UserManager
					.getInstance().getUser().getUid()));
			params.addBodyParameter("is_second", "0");

			HttpManager.post(JLXCConst.LIKE_OR_CANCEL, params,
					new JsonRequestCallBack<String>(
							new LoadDataHandler<String>() {

								@Override
								public void onSuccess(JSONObject jsonResponse,
										String flag) {
									super.onSuccess(jsonResponse, flag);
									int status = jsonResponse
											.getInteger(JLXCConst.HTTP_STATUS);
									if (status == JLXCConst.STATUS_SUCCESS) {
										// 点赞成功
										isUploadData = false;
									}

									if (status == JLXCConst.STATUS_FAIL) {
										if (OP_Type_Like == lastOperateType) {
											likeCallInterface
													.onOperateFail(true);
										} else {
											likeCallInterface
													.onOperateFail(false);
										}
										ToastUtil.show(
												mContext,
												jsonResponse
														.getString(JLXCConst.HTTP_MESSAGE));
										isUploadData = false;
									}
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1, String flag) {
									super.onFailure(arg0, arg1, flag);
									if (OP_Type_Like == lastOperateType) {
										likeCallInterface.onOperateFail(true);
									} else {
										likeCallInterface.onOperateFail(false);
									}
									isUploadData = false;
								}
							}, null));
		}
	}

	/**
	 * 添加头像到第一个位置
	 * */
	public void addHeadToLikeList(LikeImageListView likeView) {
		lastOperateType = OP_Type_Like;
		this.likeListView = likeView;
		LikeModel myModel = new LikeModel();
		myModel.setUserID(String.valueOf(UserManager.getInstance().getUser()
				.getUid()));
		myModel.setHeadImage(JLXCConst.ATTACHMENT_ADDR
				+ UserManager.getInstance().getUser().getHead_image());
		myModel.setHeadSubImage(JLXCConst.ATTACHMENT_ADDR
				+ UserManager.getInstance().getUser().getHead_sub_image());
		try {
			likeListView.insertToFirst(myModel);
		} catch (Exception e) {
			ToastUtil.show(mContext, "发生了点小故障 (・ˍ・*)");
		}
	}

	/**
	 * 更新点赞头像的数据
	 * */
	public void addDataToLikeList(HelloHaAdapter<T> adapter, int postion) {
		lastPostion = postion;
		newsAdapter = adapter;
		lastOperateType = OP_Type_News_Like;
		LikeModel myModel = new LikeModel();
		myModel.setUserID(String.valueOf(UserManager.getInstance().getUser()
				.getUid()));
		myModel.setHeadImage(JLXCConst.ATTACHMENT_ADDR
				+ UserManager.getInstance().getUser().getHead_image());
		myModel.setHeadSubImage(JLXCConst.ATTACHMENT_ADDR
				+ UserManager.getInstance().getUser().getHead_sub_image());

		LikeListItem likeData = (LikeListItem) newsAdapter.getItem(lastPostion);
		likeData.getLikeHeadListimage().add(0, myModel);
	}

	/**
	 * 移除自己的点赞头像
	 * */
	public void removeHeadFromLikeList(LikeImageListView likeView) {
		this.likeListView = likeView;
		lastOperateType = OP_Type_Like_cancel;
		likeListView.removeHeadImg();
	}

	/**
	 * 移除自己的点赞头像
	 * */
	public void removeDataFromLikeList(HelloHaAdapter<T> adapter,
			int postion) {
		lastPostion = postion;
		newsAdapter = adapter;
		lastOperateType = OP_Type_News_Like_cancel;
		// 移除头像
		List<LikeModel> likeData = ((LikeListItem) newsAdapter
				.getItem(lastPostion)).getLikeHeadListimage();
		for (int index = 0; index < likeData.size(); ++index) {
			if (likeData
					.get(index)
					.getUserID()
					.equals(String.valueOf(UserManager.getInstance().getUser()
							.getUid()))) {
				likeData.remove(index);
				break;
			} else if (index == likeData.size()) {
				LogUtils.e("点赞数据发生了错误.");
			}
		}
	}

	/**
	 * 撤销上次操作
	 * */
	public void operateRevoked() {
		switch (lastOperateType) {
		case OP_Type_Like:
			removeHeadFromLikeList(likeListView);
			break;

		case OP_Type_Like_cancel:
			addHeadToLikeList(likeListView);
			break;
		case OP_Type_News_Like:
			removeDataFromLikeList(newsAdapter, lastPostion);
			break;
		case OP_Type_News_Like_cancel:
			addDataToLikeList(newsAdapter, lastPostion);
			break;
		default:
			break;
		}
	}

	/**
	 * 回调类
	 **/
	public interface OperateCallBack {
		public void onStart(int operateType);

		public void onFinish(int operateType, boolean isSucceed,
				Object resultValue);
	}

	/**
	 * 点赞回调接口
	 * */
	public interface LikeCallBack {
		public void onOperateStart(boolean isLike);

		public void onOperateFail(boolean isLike);
	}
}
