package com.jlxc.app.personal.model;

import java.util.ArrayList;
import java.util.List;

import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.ImageModel;

public class MyNewsListItemModel {

	// 动态item的种类数
	public static final int NEWS_ITEM_TYPE_COUNT = 3;
	// 表示动态各item
	public static final int NEWS_TITLE = 0;
	public static final int NEWS_BODY = 1;
	public static final int NEWS_OPERATE = 2;

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
		case NEWS_TITLE:
		case NEWS_BODY:
		case NEWS_OPERATE:
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
	public static class MyNewsTitleItem extends MyNewsListItemModel {

		// 动态发布者的名字
		private String userName;
		// 发布的时间
		private String sendTime;
		// 显示的标签
		private String userTag;
		// 显示用户的id
		private String userID;
		// 显示是否已赞
		private boolean isLike;
		// 头像
		private String userHeadImage;
		// 头像s缩略
		private String userSubHeadImage;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userNameStr) {
			this.userName = userNameStr;
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public String getUserTag() {
			return userTag;
		}

		public void setUserTag(String userTag) {
			this.userTag = userTag;
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

		public String getUserHeadImage() {
			return userHeadImage;
		}

		public void setUserHeadImage(String userHeadImage) {
			this.userHeadImage = userHeadImage;
		}

		public String getUserSubHeadImage() {
			return userSubHeadImage;
		}

		public void setUserSubHeadImage(String userSubHeadImage) {
			this.userSubHeadImage = userSubHeadImage;
		}

	}

	/**
	 * 动态的主体
	 * */
	public static class MyNewsBodyItem extends MyNewsListItemModel {

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
	public static class MyNewsOperateItem extends MyNewsListItemModel {

		// 评论数
		private int replyCount;
		// 点赞数
		private int likeCount;
		// 是否已赞
		private boolean isLike = false;

		public int getReplyCount() {
			return replyCount;
		}

		public void setReplyCount(String replyCount) {
			try {
				this.replyCount = JLXCUtils.stringToInt(replyCount);
			} catch (Exception e) {
				LogUtils.e("评论数据格式错误.");
			}
		}

		public int getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(String likeCount) {
			try {
				this.likeCount = JLXCUtils.stringToInt(likeCount);
			} catch (Exception e) {
				LogUtils.e("点赞数据格式错误.");
			}
		}

		public boolean getIsLike() {
			return isLike;
		}

		public void setIsLike(String isLike) {
			if (isLike.equals("1")) {
				this.isLike = true;
			} else {
				this.isLike = false;
			}
		}
	}
}
