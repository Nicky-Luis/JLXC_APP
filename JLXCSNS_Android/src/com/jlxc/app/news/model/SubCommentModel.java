package com.jlxc.app.news.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class SubCommentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3721167041844355170L;
	// 子评论的id
	private String subID;
	// 评论者的名字
	private String publishName;
	// 顶部的评论id
	private String topCommentId;
	// 被回复者的id
	private String replyUid;
	// 被回复飞评论id
	private String replyCommentId;
	// 被回复者的名字
	private String replyName;
	// 添加的日期
	private String addData;
	// 发布者的名字
	private String publisId;
	// 发布的内容
	private String commentContent;

	// 内容注入
	public void setContentWithJson(JSONObject object) {
		if (object.containsKey("id")) {
			setSubID(object.getString("id"));
		}
		if (object.containsKey("name")) {
			setPublishName(object.getString("name"));
		}
		if (object.containsKey("top_comment_id")) {
			setTopCommentId(object.getString("top_comment_id"));
		}
		if (object.containsKey("reply_uid")) {
			setReplyUid(object.getString("reply_uid"));
		}
		if (object.containsKey("reply_comment_id")) {
			setReplyCommentId(object.getString("reply_comment_id"));
		}
		if (object.containsKey("reply_name")) {
			setReplyName(object.getString("reply_name"));
		}
		if (object.containsKey("add_date")) {
			setAddData(object.getString("add_date"));
		}
		if (object.containsKey("user_id")) {
			setPublishId(object.getString("user_id"));
		}
		if (object.containsKey("comment_content")) {
			setCommentContent(object.getString("comment_content"));
		}
	}

	public String getSubID() {
		return subID;
	}

	public void setSubID(String subID) {
		this.subID = subID;
	}

	public String getPublishName() {
		return publishName;
	}

	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}

	public String getTopCommentId() {
		return topCommentId;
	}

	public void setTopCommentId(String topCommentId) {
		this.topCommentId = topCommentId;
	}

	public String getReplyUid() {
		return replyUid;
	}

	public void setReplyUid(String replyUid) {
		this.replyUid = replyUid;
	}

	public String getReplyCommentId() {
		return replyCommentId;
	}

	public void setReplyCommentId(String replyCommentId) {
		this.replyCommentId = replyCommentId;
	}

	public String getReplyName() {
		return replyName;
	}

	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}

	public String getAddData() {
		return addData;
	}

	public void setAddData(String addData) {
		this.addData = addData;
	}

	public String getPublishId() {
		return publisId;
	}

	public void setPublishId(String userId) {
		publisId = userId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
}
