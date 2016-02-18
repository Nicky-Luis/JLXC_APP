package com.jlxc.app.news.model;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.utils.LogUtils;

public class SchoolModel {

	// 初中
	public static final String JUNIOR_MIDDLE_SCHOOL = "1";
	// 高中
	public static final String SENIOR_MIDDLE_SCHOOL = "2";
	// 学校代码
	private String schoolCode;
	// 学校的名字
	private String schoolName;
	// 区域名字
	private String districtName;
	// 所在城市的名字
	private String cityName;
	// 学校的类型
	private String schoolType;

	// 内容注入
	public void setContentWithJson(JSONObject object) {

		setSchoolCode(object.getString("code"));
		setSchoolName(object.getString("name"));
		setDistrictName(object.getString("district_name"));
		setCityName(object.getString("city_name"));
		setSchoolType(object.getString("level"));

	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchoolCode() {
		return schoolCode;
	}

	public void setSchoolCode(String schoolCode) {
		this.schoolCode = schoolCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(String schoolType) {
		this.schoolType = schoolType;
	}

}
