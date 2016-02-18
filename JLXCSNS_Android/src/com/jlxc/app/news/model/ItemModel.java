package com.jlxc.app.news.model;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import com.jlxc.app.base.utils.LogUtils;

/**
 * 将每条动态拆分成不同的部分
 * */
public class ItemModel {

	// 动态item的种类数
	public static final int NEWS_ITEM_TYPE_COUNT = 5;
	// 校园item的种类数
	public static final int CAMPUS_ITEM_TYPE_COUNT = 5;
	// 动态详情item的种类数
	public static final int NEWS_DETAIL_ITEM_TYPE_COUNT = 5;
	// 圈子item的种类数
	public static final int GROUP_NEWS_ITEM_TYPE_COUNT = 3;
	// 表示动态各item类型
	public static final int NEWS_TITLE = 0;
	public static final int NEWS_BODY = 1;
	public static final int NEWS_LIKELIST = 2;
	public static final int NEWS_COMMENT = 3;
	public static final int NEWS_OPERATE = 4;
	// 表示校园各item类型
	public static final int CAMPUS_TITLE = 0;
	public static final int CAMPUS_BODY = 1;
	public static final int CAMPUS_OPERATE = 2;
	public static final int CAMPUS_LIKELIST = 3;
	public static final int CAMPUS_HEAD = 4;
	// 表示动态详情各item类型
	public static final int NEWS_DETAIL_TITLE = 0;
	public static final int NEWS_DETAIL_BODY = 1;
	public static final int NEWS_DETAIL_LIKELIST = 2;
	public static final int NEWS_DETAIL_COMMENT = 3;
	public static final int NEWS_DETAIL_SUB_COMMENT = 4;
	
	// 动态的id
	private String newsID = "";
	// 当前的item类型
	private int itemType;

	public int getItemType() {
		return itemType;
	}

	/**
	 * 设置item的类型
	 * */
	public void setItemType(int type) {
		switch (type) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			itemType = type;
			break;

		default:
			LogUtils.e("items type error");
			break;
		}
	}

	public String getNewsID() {
		return newsID;
	}

	public void setNewsID(String newsID) {
		this.newsID = newsID;
	}

	/**
	 * 动态的头部
	 * */
	public static class TitleItem extends ItemModel {

		// 动态发布者的头像缩略图
		private String headSubImage;
		// 动态发布者的头像
		private String headImage;
		// 动态发布者的名字
		private String userName;
		// 学校
		private String userSchool;
		// 学校id
		private String schoolCode;
		// 显示用户的id
		private String userID;
		// 显示是否已赞
		private boolean isLike;
		// 所有点赞的人
		private int likeCount;
		// 发布的时间
		private String sendTime;
		// 标签来源
		private String tagContent = "";

		public String getHeadSubImage() {
			return headSubImage;
		}

		public void setHeadSubImage(String headSubImage) {
			this.headSubImage = headSubImage;
		}

		public String getHeadImage() {
			return headImage;
		}

		public void setHeadImage(String userHeadImage) {
			this.headImage = userHeadImage;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userNameStr) {
			this.userName = userNameStr;
		}

		public String getUserSchool() {
			return userSchool;
		}

		public void setUserSchool(String userSchool) {
			this.userSchool = userSchool;
		}

		public boolean getIsLike() {
			return isLike;
		}

		public void setIsLike(String isLike) {
			if (isLike.equals("0")) {
				this.isLike = false;
			} else {
				this.isLike = true;
			}
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public int getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(String likeCount) {
			try {
				this.likeCount = Integer.parseInt(likeCount);
			} catch (Exception e) {
				LogUtils.e("点赞数据格式错误.");
			}
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public String getTagContent() {
			return tagContent;
		}

		public void setTagContent(String tagContent) {
			this.tagContent = tagContent;
		}
		public String getSchoolCode() {
			return schoolCode;
		}
		public void setSchoolCode(String schoolCode) {
			this.schoolCode = schoolCode;
		}

		
	}

	/**
	 * 动态的主体
	 * */
	public static class BodyItem extends ItemModel {

		// 动态的文字内容
		private String newsContent;
		// 动态的图片列表
		private List<ImageModel> newsImageList = new ArrayList<ImageModel>();
		// 发布的位置
		private String location;
		// 发布的时间
		private String sendTime;
		// 发布到的圈子（为0时不存在）
		private int topicID;	
		// 发布到的圈子名
		private String topicName;

		public String getNewsContent() {
			return newsContent;
		}

		public void setNewsContent(String newsContent) {
			this.newsContent = newsContent;
		}

		public List<ImageModel> getNewsImageListList() {
			return newsImageList;
		}

		public void setImageNewsList(List<ImageModel> imageList) {
			this.newsImageList = imageList;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public int getTopicID() {
			return topicID;
		}

		public void setTopicID(int topicID) {
			this.topicID = topicID;
		}

		public String getTopicName() {
			return topicName;
		}

		public void setTopicName(String topicName) {
			this.topicName = topicName;
		}
		
	}

	/**
	 * 动态的操作部分
	 * */
	public static class OperateItem extends ItemModel {

		// 是否已赞
		private boolean isLike = false;
		// 点赞数
		private int likeCount;
		// 发布的时间
		private String sendTime;
		// 发布到的圈子（为0时不存在）
		private int topicID;	
		// 发布到的圈子名
		private String topicName;
		
		public boolean getIsLike() {
			return isLike;
		}

		public int getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(String likeCount) {
			try {
				this.likeCount = Integer.parseInt(likeCount);
			} catch (Exception e) {
				LogUtils.e("点赞数据格式错误.");
			}
		}

		public void setIsLike(String isLike) {
			if (isLike.equals("1")) {
				this.isLike = true;
			} else {
				this.isLike = false;
			}
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public int getTopicID() {
			return topicID;
		}

		public void setTopicID(int topicID) {
			this.topicID = topicID;
		}

		public String getTopicName() {
			return topicName;
		}

		public void setTopicName(String topicName) {
			this.topicName = topicName;
		}
		
	}

	/**
	 * 已赞的人的头像部分
	 * */
	public static class LikeListItem extends ItemModel {

		// 点赞数
		private int likeCount;
		// 点赞的人头像
		private List<LikeModel> likeList = new ArrayList<LikeModel>();

		public int getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(String likeCount) {
			try {
				this.likeCount = Integer.parseInt(likeCount);
			} catch (Exception e) {
				LogUtils.e("点赞数据格式错误.");
			}
		}

		public List<LikeModel> getLikeHeadListimage() {
			return likeList;
		}

		public void setLikeHeadListimage(List<LikeModel> list) {
			this.likeList = list;
		}
	}

	/**
	 * 评论列表部分
	 * */
	public static class CommentListItem extends ItemModel {
		// 评论数
		private int replyCount;
		// 评论列表
		private List<CommentModel> commentList = new ArrayList<CommentModel>();

		public int getReplyCount() {
			return replyCount;
		}

		public void setReplyCount(String replyCount) {
			try {
				this.replyCount = Integer.parseInt(replyCount);
			} catch (Exception e) {
				LogUtils.e("评论数据格式错误.");
			}
		}

		public List<CommentModel> getCommentList() {
			return commentList;
		}

		public void setCommentList(List<CommentModel> cmtList) {
			this.commentList = cmtList;
		}
	}

	/**
	 * 评论列表部分
	 * */
	public static class CommentItem extends ItemModel {

		// 评论列表
		private CommentModel commentData = new CommentModel();

		public CommentModel getCommentModel() {
			return commentData;
		}

		public void setComment(CommentModel cmt) {
			this.commentData = cmt;
		}
	}

	/**
	 * 校园的头部
	 * */
	public static class CampusHeadItem extends ItemModel {

		private String schoolName;
		// 学校的人列表
		private List<CampusPersonModel> personList = new ArrayList<CampusPersonModel>();

		public List<CampusPersonModel> getPersonList() {
			return personList;
		}

		public void setPersonList(List<CampusPersonModel> List) {
			this.personList = List;
		}

		public String getSchoolName() {
			return schoolName;
		}

		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}
	}

	/**
	 * 子评论item数据
	 * */
	public static class SubCommentItem extends ItemModel {

		// 子评论对象
		private SubCommentModel subCommentModel;

		public SubCommentModel getSubCommentModel() {
			return subCommentModel;
		}

		public void setSubCommentModel(SubCommentModel subCommentModel) {
			this.subCommentModel = subCommentModel;
		}
	}
}
