package com.jlxc.app.personal.model;
/**
 * 好友模型
 * @author lixiaohang
 *
 */
public class FriendModel {
	//用户id
	private int uid;
	//用户姓名
	private String name;
	//头像缩略图
	private String head_sub_image;
	//头像大图
	private String head_image;
	//学校
	private String school;
	//是否被关注或已经关注
	private boolean isOrHasAttent;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHead_sub_image() {
		return head_sub_image;
	}
	public void setHead_sub_image(String head_sub_image) {
		this.head_sub_image = head_sub_image;
	}
	public String getHead_image() {
		return head_image;
	}
	public void setHead_image(String head_image) {
		this.head_image = head_image;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public boolean isOrHasAttent() {
		return isOrHasAttent;
	}
	public void setOrHasAttent(boolean isOrHasAttent) {
		this.isOrHasAttent = isOrHasAttent;
	}
	
	
}
