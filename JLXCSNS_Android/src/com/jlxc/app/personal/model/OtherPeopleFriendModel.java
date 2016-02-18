package com.jlxc.app.personal.model;

//最近来访模型
/**
 * @author lixiaohang
 *
 */
public class OtherPeopleFriendModel {

	private int uid;//uid
	private String name;//姓名
	private String head_sub_image;//头像
	private String school;//学校

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
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}

}
