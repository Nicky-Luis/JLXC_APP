package com.jlxc.app.group.model;

import com.alibaba.fastjson.JSONObject;

public class GroupPersonModel {

	// 头像缩略图
	private String headSubImage;
	// 名字
	private String userName;
	// 性别
	private String sex;
	// 用户id
	private String userId;

	public void setContentWithJson(JSONObject object) {

		if (object.containsKey("user_id")) {
			setUserId(object.getString("user_id"));
		}
		if (object.containsKey("sex")) {
			setSex(object.getString("sex"));
		}
		if (object.containsKey("head_sub_image")) {
			setHeadSubImage(object.getString("head_sub_image"));
		}
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
	}

	public String getHeadSubImage() {
		return headSubImage;
	}

	public void setHeadSubImage(String headSubImage) {
		this.headSubImage = headSubImage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
