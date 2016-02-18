package com.jlxc.app.discovery.model;

import java.util.List;

import com.jlxc.app.base.utils.LogUtils;

public class RecommendItemData {

	// item的种类数
	public static final int RECOMMEND_ITEM_TYPE_COUNT = 3;
	// 表示推荐的人列表item
	public static final int RECOMMEND_TITLE = 0;
	public static final int RECOMMEND_INFO = 1;
	public static final int RECOMMEND_PHOTOS = 2;
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
		case RECOMMEND_TITLE:
		case RECOMMEND_INFO:
		case RECOMMEND_PHOTOS:
			itemType = type;
			break;

		default:
			LogUtils.e("items type error");
			break;
		}
	}

	/**
	 * 动态的头部
	 * */
	public static class RecommendTitleItem extends RecommendItemData {
	}

	/**
	 * 信息
	 * */
	public static class RecommendInfoItem extends RecommendItemData {

		// 推荐的人头像缩略图
		private String headSubImage;
		// 推荐的人的头像
		private String headImage;
		// 推荐的人的名字
		private String userName;
		// 推荐的人学校
		private String userSchool;
		// 关系的标签
		private String relationTag;
		// 推荐的人的id
		private String userID;
		// 是否添加了
		private boolean isAdd;
		//当前原始对象索引
		private int originIndex;

		public String getHeadSubImage() {
			return headSubImage;
		}

		public void setHeadSubImage(String headSubImage) {
			this.headSubImage = headSubImage;
		}

		public String getHeadImage() {
			return headImage;
		}

		public void setHeadImage(String headImage) {
			this.headImage = headImage;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserSchool() {
			return userSchool;
		}

		public void setUserSchool(String userSchool) {
			this.userSchool = userSchool;
		}

		public String getRelationTag() {
			return relationTag;
		}

		public void setRelationTag(String relationTag) {
			this.relationTag = relationTag;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public boolean isAdd() {
			return isAdd;
		}

		public void setAdd(String isadd) {
			if (isadd.equals("1")) {
				this.isAdd = true;
			} else {
				this.isAdd = false;
			}
		}

		public int getOriginIndex() {
			return originIndex;
		}

		public void setOriginIndex(int originIndex) {
			this.originIndex = originIndex;
		}
		

	}

	/**
	 * 照片部分
	 * */
	public static class RecommendPhotoItem extends RecommendItemData {
		// 用户id
		private String userId;
		// 缩略照片列表
		private List<String> photoSubUrl;
		// 照片列表
		private List<String> photoUrl;

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public List<String> getPhotoSubUrl() {
			return photoSubUrl;
		}

		public void setPhotoSubUrl(List<String> photoSubUrl) {
			this.photoSubUrl = photoSubUrl;
		}

		public List<String> getPhotoUrl() {
			return photoUrl;
		}

		public void setPhotoUrl(List<String> photoUrl) {
			this.photoUrl = photoUrl;
		}
	}
}
