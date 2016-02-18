package com.jlxc.app.base.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class UserModel implements Serializable{

	public static final int SexBoy = 0;
	public static final int SexGirl = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//用户id
	private int uid;
	//用户名
	private String username;
	//用户密码
	private String password;
	//helloha_id
	private String helloha_id;
	//姓名
	private String name;
	//电话号
	private String phone_num;
	//姓别 0男 1女 2不知道
	private int sex;	
	//学校
	private String school;	
	//学校编码
	private String school_code;	
	//头像地址
	private String head_image;
	//头像缩略图地址
	private String head_sub_image;	
	//年龄
	private int age;		
	//生日
	private String birthday;
	//城市
	private String city;
	//签名
	private String sign;
	//背景图片
	private String background_image;
	//登录token
	private String login_token;
	//融云im_token
	private String im_token;
	
	//Congestion
	//内容注入
	public void setContentWithJson(JSONObject object) {
		
		setUid(object.getIntValue("id"));
		setAge(object.getIntValue("age"));
		setHead_image(object.getString("head_image"));
		setHead_sub_image(object.getString("head_sub_image"));
		setName(object.getString("name"));
		setPassword(object.getString("password"));
		setUsername(object.getString("username"));
		setPhone_num(object.getString("phone_num"));
		setSchool(object.getString("school"));
		setSchool_code(object.getString("school_code"));
		setSex(object.getIntValue("sex"));
		setHelloha_id(object.getString("helloha_id"));
		setBirthday(object.getString("birthday"));
		setCity(object.getString("city"));
		setSign(object.getString("sign"));
		setBackground_image(object.getString("background_image"));
		setLogin_token(object.getString("login_token"));
		setIm_token(object.getString("im_token"));
	}
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHelloha_id() {
		return helloha_id;
	}
	public void setHelloha_id(String helloha_id) {
		this.helloha_id = helloha_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getSchool_code() {
		return school_code;
	}
	public void setSchool_code(String school_code) {
		this.school_code = school_code;
	}
	public String getHead_image() {
		return head_image;
	}
	public void setHead_image(String head_image) {
		this.head_image = head_image;
	}
	public String getHead_sub_image() {
		return head_sub_image;
	}
	public void setHead_sub_image(String head_sub_image) {
		this.head_sub_image = head_sub_image;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getBackground_image() {
		return background_image;
	}
	public void setBackground_image(String background_image) {
		this.background_image = background_image;
	}
	public String getLogin_token() {
		return login_token;
	}
	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}
	public String getIm_token() {
		return im_token;
	}
	public void setIm_token(String im_token) {
		this.im_token = im_token;
	}	
	
	
}
