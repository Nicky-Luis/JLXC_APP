package com.jlxc.app.group.model;

import java.util.ArrayList;
import java.util.List;

import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.news.model.ImageModel;

public class GroupNewsItemModel {

	// 动态item的种类数
	public static final int NEWS_ITEM_TYPE_COUNT = 3;
	// 表示圈子各item类型
	public static final int GROUP_TITLE = 0;
	public static final int GROUP_BODY = 1;
	public static final int GROUP_OPERATE = 2;

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
		case GROUP_TITLE:
		case GROUP_BODY:
		case GROUP_OPERATE:
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
	public static class GroupNewsTitleItem extends GroupNewsItemModel {

		// 动态发布者的名字
		private String userName;
		// 显示用户的id
		private String userID;
		// 所在学校
		private String school;
		// 所在学校代码
		private String schoolCode;		
		// 头像图片
		private String userHeadImage;
		// 头像缩略
		private String userSubHeadImage;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userNameStr) {
			this.userName = userNameStr;
		}

		public String getSchool() {
			return school;
		}

		public void setSchool(String userSchool) {
			this.school = userSchool;
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
	public static class GroupNewsBodyItem extends GroupNewsItemModel {

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
	public static class GroupNewsOperateItem extends GroupNewsItemModel {

		// 评论数
		private int replyCount;
		// 点赞数
		private int likeCount;
		// 是否已赞
		private boolean isLike = false;
		// 发布的时间
		private String time;

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

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

	}

}
