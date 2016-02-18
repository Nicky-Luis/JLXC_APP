package com.jlxc.app.group.model;

import io.rong.imkit.R.integer;

//圈子类型模型
public class GroupCategoryModel {

	// 类型ID
	private int category_id;
	// 类型名字
	private String category_name;
	// 类型封面
	private String category_cover;
	// 类型描述
	private String category_desc;
	// 背景颜色值
	private int backgroundValue;

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory_cover() {
		return category_cover;
	}

	public void setCategory_cover(String category_cover) {
		this.category_cover = category_cover;
	}

	public String getCategory_desc() {
		return category_desc;
	}

	public void setCategory_desc(String category_desc) {
		this.category_desc = category_desc;
	}

	public int getBackgroundValue() {
		return backgroundValue;
	}

	public void setBackgroundValue(int backgroundValue) {
		this.backgroundValue = backgroundValue;
	}

}
