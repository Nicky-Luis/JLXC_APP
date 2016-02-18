package com.jlxc.app.news.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.utils.JLXCConst;

public class ImageModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1628730232453437535L;
	// 图片id
	private String imageId;
	// 图片的类型
	private String imageType;
	// 缩略图地址
	private String subURL;
	// 地址
	private String URL;
	// 尺寸
	private String imageStize;
	// 发布的时间
	private String addDate;
	// 宽度
	private int imageWidth;
	// 高度
	private int imageHheight;

	// 内容注入
	public void setContentWithJson(JSONObject object) {
		setImageId(object.getString("id"));
		setImageType(object.getString("type"));
		setSubURL(JLXCConst.ATTACHMENT_ADDR + object.getString("sub_url"));
		setURL(JLXCConst.ATTACHMENT_ADDR + object.getString("url"));
		setImageStize(object.getString("size"));
		setAddDate(object.getString("add_date"));
		setImageWidth(object.getString("width"));
		setImageHheight(object.getString("height"));
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getSubURL() {
		return subURL;
	}

	public void setSubURL(String subURL) {
		this.subURL = subURL;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getImageStize() {
		return imageStize;
	}

	public void setImageStize(String imageStize) {
		this.imageStize = imageStize;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(String imageWidth) {
		this.imageWidth = Integer.parseInt(imageWidth);
	}

	public int getImageHheight() {
		return imageHheight;
	}

	public void setImageHheight(String imageHheight) {
		this.imageHheight = Integer.parseInt(imageHheight);
	}

}
