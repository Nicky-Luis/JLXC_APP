package com.jlxc.app.news.model;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.utils.JLXCConst;

public class CampusPersonModel {

	// 发布的动态数量
	private String newsCount;
	// 头像缩略图
	private String headSubImage;
	// 名字
	private String userName;
	// 性别
	private String sex;
	// 用户id
	private String userId;

	public void setContentWithJson(JSONObject object) {

		setUserId(object.getString("uid"));
		
		if (object.containsKey("sex")) {
			setSex(object.getString("sex"));
		}
		
		setHeadSubImage(JLXCConst.ATTACHMENT_ADDR
				+ object.getString("head_sub_image"));
		
		setNewsCount(object.getString("count"));
		
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
	}

	public String getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(String newsCount) {
		this.newsCount = newsCount;
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
