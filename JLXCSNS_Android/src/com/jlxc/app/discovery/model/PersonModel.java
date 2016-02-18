package com.jlxc.app.discovery.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.utils.JLXCConst;

public class PersonModel {

	// 用户的id
	private String userId;
	// 用户名
	private String userName;
	// 用户所在的学校
	private String userSchool;
	// 学校代码
	private String schoolCode;
	// 用户的头像
	private String headImage;
	// 头像的缩略图
	private String headSubImage;
	// 照片列表
	private List<String> imageList;
	// 关系来源
	private String type;
	// 是否为朋友
	private String isFriend = "0";
	// 电话号码
	private String phoneNumber;
	// 性别
	private String sex;
	
	@SuppressWarnings("unchecked")
	public void setContentWithJson(JSONObject object) {

		if (object.containsKey("uid")) {
			setUerId(object.getString("uid"));
		}
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
		if (object.containsKey("school")) {
			setUserSchool(object.getString("school"));
		}
		if (object.containsKey("school_code")) {
			setSchoolCoed(object.getString("school_code"));
		}
		if (object.containsKey("head_image")) {
			setHeadImage(JLXCConst.ATTACHMENT_ADDR
					+ object.getString("head_image"));
		}
		if (object.containsKey("head_sub_image")) {
			setHeadSubImage(JLXCConst.ATTACHMENT_ADDR
					+ object.getString("head_sub_image"));
		}

		// 图片的转换
		if (object.containsKey("images")) {
			List<JSONObject> JImageObj = (List<JSONObject>) object
					.get("images");
			List<String> imgList = new ArrayList<String>();
			for (JSONObject imgObject : JImageObj) {
				imgList.add(JLXCConst.ATTACHMENT_ADDR
						+ imgObject.getString("sub_url"));
			}
			setImageList(imgList);
		}

		// 类型转换
		if (object.containsKey("type")) {
			JSONObject JTypeObj = (JSONObject) object.get("type");
			setType(JTypeObj.getString("content"));
		}
		if (object.containsKey("is_friend")) {
			setIsFriend(object.getString("is_friend"));
		}
		if (object.containsKey("phone")) {
			setPhoneNumber(object.getString("phone"));
		}
	}

	// 重写hashCode方法
	@Override
	public int hashCode() {
		return this.userId.hashCode();
	}

	// 重写equals方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null != obj && obj instanceof PersonModel) {
			PersonModel p = (PersonModel) obj;
			if (userId.equals(p.userId) && userName.equals(p.userName)
					&& userSchool.equals(p.userSchool)) {
				return true;
			}
		}
		return false;
	}

	public String getUerId() {
		return userId;
	}

	public void setUerId(String userId) {
		this.userId = userId;
	}

	public String getUserSchool() {
		return userSchool;
	}

	public void setUserSchool(String userSchool) {
		this.userSchool = userSchool;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSchoolCoed() {
		return schoolCode;
	}

	public void setSchoolCoed(String schoolCode) {
		this.schoolCode = schoolCode;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getHeadSubImage() {
		return headSubImage;
	}

	public void setHeadSubImage(String headSubImage) {
		this.headSubImage = headSubImage;
	}

	public List<String> getImageList() {
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(String isFriend) {
		this.isFriend = isFriend;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	
}
