package com.jlxc.app.group.model;

import java.io.Serializable;

//话题模型
public class GroupTopicModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//话题ID
	private int topic_id;
	//话题名
	private String topic_name;
	//话题描述
	private String topic_detail;
	//话题封面原图
	private String topic_cover_image;
	//话题封面缩略图
	private String topic_cover_sub_image;
	//成员数量
	private int member_count;
	//新闻数量
	private int news_count;
	//未读数量
	private int unread_news_count;
	//最后一次刷新时间
	private String last_refresh_date;
	//是否有内容
	private boolean has_news;
	
	public int getTopic_id() {
		return topic_id;
	}
	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	public String getTopic_name() {
		return topic_name;
	}
	public void setTopic_name(String topic_name) {
		this.topic_name = topic_name;
	}
	public String getTopic_cover_sub_image() {
		return topic_cover_sub_image;
	}
	public void setTopic_cover_sub_image(String topic_cover_sub_image) {
		this.topic_cover_sub_image = topic_cover_sub_image;
	}
	public int getMember_count() {
		return member_count;
	}
	public void setMember_count(int member_count) {
		this.member_count = member_count;
	}
	public int getUnread_news_count() {
		return unread_news_count;
	}
	public void setUnread_news_count(int unread_news_count) {
		this.unread_news_count = unread_news_count;
	}
	public String getLast_refresh_date() {
		return last_refresh_date;
	}
	public void setLast_refresh_date(String last_refresh_date) {
		this.last_refresh_date = last_refresh_date;
	}
	public int getNews_count() {
		return news_count;
	}
	public void setNews_count(int news_count) {
		this.news_count = news_count;
	}
	public String getTopic_detail() {
		return topic_detail;
	}
	public void setTopic_detail(String topic_detail) {
		this.topic_detail = topic_detail;
	}
	public String getTopic_cover_image() {
		return topic_cover_image;
	}
	public void setTopic_cover_image(String topic_cover_image) {
		this.topic_cover_image = topic_cover_image;
	}
	public boolean isHas_news() {
		return has_news;
	}
	public void setHas_news(boolean has_news) {
		this.has_news = has_news;
	}
	
}
