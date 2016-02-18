package com.jlxc.app.group.model;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.ImageModel;
import com.jlxc.app.news.model.LikeModel;

/**
 * 将每条动态拆分成不同的部分
 * */
public class SchoolItemModel {

	// 校园item的种类数
	public static final int SCHOOL_NEWS_ITEM_TYPE_COUNT = 4;
	// 表示校园各item类型
	public static final int SCHOOL_NEWS_TITLE = 0;
	public static final int SCHOOL_NEWS_BODY = 1;
	public static final int SCHOOL_NEWS_OPERATE = 2;
	public static final int SCHOOL_NEWS_LIKELIST = 3;

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
	public static class SchoolNewsTitleItem extends SchoolItemModel {

		// 动态发布者的头像缩略图
		private String headSubImage;
		// 动态发布者的头像
		private String headImage;
		// 动态发布者的名字
		private String userName;
		// 显示用户的id
		private String userID;
		// 发布的时间
		private String sendTime;

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

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}
	}

	/**
	 * 动态的主体
	 * */
	public static class SchoolNewsBodyItem extends SchoolItemModel {

		// 动态的文字内容
		private String newsContent;
		// 动态的图片列表
		private List<ImageModel> newsImageList = new ArrayList<ImageModel>();
		// 发布的位置
		private String location;

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
	}

	/**
	 * 动态的操作部分
	 * */
	public static class SchoolNewsOperateItem extends SchoolItemModel {

		// 是否已赞
		private boolean isLike = false;
		// 点赞数
		private int likeCount;
		// 评论数
		private int commentCount;

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

		public int getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(int commentCount) {
			this.commentCount = commentCount;
		}
	}

	/**
	 * 已赞的人的头像部分
	 * */
	public static class SchoolNewsLikeListItem extends SchoolItemModel {

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

}
