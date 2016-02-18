package com.jlxc.app.personal.model;

//最近来访模型
public class VisitModel {

	private int uid;
	private String name;
	private String head_sub_image;
	private String visit_time;
	private String sign;

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
	public String getVisit_time() {
		return visit_time;
	}
	public void setVisit_time(String visit_time) {
		this.visit_time = visit_time;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
}
