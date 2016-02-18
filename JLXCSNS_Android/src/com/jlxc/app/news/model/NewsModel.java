package com.jlxc.app.news.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;

public class NewsModel implements Serializable {

	/**
	 * 序列号（activity之间传递的时候使用）
	 */
	private static final long serialVersionUID = 1994327813985826490L;

	// 用户id
	private String uid;
	// 用户的名字
	private String userName;
	// 用户的学校
	private String userSchool;
	// 学校id
	private String schoolCode;
	// 用户的头像
	private String userHeadImage;
	// 用户的头像缩略图
	private String userHeadSubImage;
	// 动态的ID
	private String newsID;
	// 动态内容
	private String newsContent;
	// 发布的位置
	private String location;
	// 动态的评论量
	private String commentQuantity;
	// 动态的浏览量
	private String browseQuantity;
	// 动态的点赞量
	private String likeQuantity;
	// 发布的时间
	private String sendTime;
	// 发布到的圈子（为0时不存在）
	private int topicID;	
	// 发布到的圈子名
	private String topicName;
	// 添加的图片列表
	private List<ImageModel> imageNewsList = new ArrayList<ImageModel>();
	// 用户是否点赞
	private String isLike;
	// 评论列表
	private List<CommentModel> commentList = new ArrayList<CommentModel>();
	// 点赞的人列表
	private List<LikeModel> likeHeadListimage = new ArrayList<LikeModel>();
	// 发布动态的时间戳
	private String timesTamp;
	// 关系类型
	private Map<String, String> TYPE = new HashMap<String, String>();

	// 内容注入
	@SuppressWarnings("unchecked")
	public void setContentWithJson(JSONObject object) {

		if (object.containsKey("uid")) {
			setUid(object.getString("uid"));
		}
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
		if (object.containsKey("school")) {
			setUserSchool(object.getString("school"));
		}
		if (object.containsKey("school_code")) {
			setSchoolCode(object.getString("school_code"));
		}
		if (object.containsKey("head_image")) {
			setUserHeadImage(JLXCConst.ATTACHMENT_ADDR
					+ object.getString("head_image"));
		}
		if (object.containsKey("head_sub_image")) {
			setUserHeadSubImage(JLXCConst.ATTACHMENT_ADDR
					+ object.getString("head_sub_image"));
		}
		if (object.containsKey("id")) {
			setNewsID(object.getString("id"));
		}
		if (object.containsKey("content_text")) {
			setNewsContent(object.getString("content_text"));
		}
		if (object.containsKey("location")) {
			setLocation(object.getString("location"));
		}
		if (object.containsKey("comment_quantity")) {
			setCommentQuantity(object.getString("comment_quantity"));
		}
		if (object.containsKey("browse_quantity")) {
			setBrowseQuantity(object.getString("browse_quantity"));
		}
		if (object.containsKey("like_quantity")) {
			setLikeQuantity(object.getString("like_quantity"));
		}
		if (object.containsKey("add_date")) {
			setSendTime(object.getString("add_date"));
		}
		if (object.containsKey("is_like")) {
			setIsLike(object.getString("is_like"));
		}
		if (object.containsKey("topic_id")) {
			setTopicID(object.getIntValue("topic_id"));
		}
		if (object.containsKey("topic_name")) {
			setTopicName(object.getString("topic_name"));
		}
		
		// 图片的转换
		if (object.containsKey("images")) {
			List<JSONObject> JImageObj = (List<JSONObject>) object
					.get("images");
			List<ImageModel> imgList = new ArrayList<ImageModel>();
			for (JSONObject imgObject : JImageObj) {
				ImageModel imgTemp = new ImageModel();
				imgTemp.setContentWithJson(imgObject);
				imgList.add(imgTemp);
			}
			setImageNewsList(imgList);
		}

		// 评论的转换
		if (object.containsKey("comments")) {
			List<JSONObject> JCommentObj = (List<JSONObject>) object
					.get("comments");
			List<CommentModel> cmtList = new ArrayList<CommentModel>();
			for (JSONObject cmtObject : JCommentObj) {
				CommentModel cmtTemp = new CommentModel();
				cmtTemp.setContentWithJson(cmtObject);
				cmtList.add(cmtTemp);
			}
			setCommentList(cmtList);
		}
		// 点赞
		if (object.containsKey("likes")) {
			List<JSONObject> JLikeObj = (List<JSONObject>) object.get("likes");
			List<LikeModel> likeList = new ArrayList<LikeModel>();
			for (JSONObject lkObject : JLikeObj) {
				LikeModel lkTemp = new LikeModel();
				lkTemp.setContentWithJson(lkObject);
				likeList.add(lkTemp);
			}
			setLikeHeadListimage(likeList);
		}
		if (object.containsKey("add_time")) {
			setTimesTamp(object.getString("add_time"));
		}

		// 类别
		Map<String, String> tempMap = new HashMap<String, String>();
		JSONObject typeObject = (JSONObject) object.get("type");
		if (null != typeObject) {
			if (typeObject.containsKey("type")) {
				tempMap.put("type", typeObject.getString("type"));
			} else {
				tempMap.put("type", "");
			}
			if (typeObject.containsKey("fid")) {
				tempMap.put("fid", typeObject.getString("fid"));
			} else {
				tempMap.put("fid", "");
			}
			if (typeObject.containsKey("content")) {
				tempMap.put("content", typeObject.getString("content"));
			} else {
				tempMap.put("content", "");
			}
		} else {
			tempMap.put("type", "");
			tempMap.put("fid", "");
			tempMap.put("content", "");
		}
		setTYPE(tempMap);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserSchool() {
		return userSchool;
	}

	public void setUserSchool(String userSchool) {
		this.userSchool = userSchool;
	}
	
	public String getSchoolCode() {
		return schoolCode;
	}

	public void setSchoolCode(String schoolCode) {
		this.schoolCode = schoolCode;
	}

	public String getUserHeadImage() {
		return userHeadImage;
	}

	public void setUserHeadImage(String userHeadImage) {
		this.userHeadImage = userHeadImage;
	}

	public String getUserHeadSubImage() {
		return userHeadSubImage;
	}

	public void setUserHeadSubImage(String userHeadSubImage) {
		this.userHeadSubImage = userHeadSubImage;
	}

	public String getNewsID() {
		return newsID;
	}

	public void setNewsID(String newsID) {
		this.newsID = newsID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNewsContent() {
		return newsContent;
	}

	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}

	public String getCommentQuantity() {
		return commentQuantity;
	}

	public void setCommentQuantity(String commentQuantity) {
		this.commentQuantity = commentQuantity;
	}

	public String getBrowseQuantity() {
		return browseQuantity;
	}

	public void setBrowseQuantity(String browseQuantity) {
		this.browseQuantity = browseQuantity;
	}

	public String getLikeQuantity() {
		return likeQuantity;
	}

	public void setLikeQuantity(String likeQuantity) {
		this.likeQuantity = likeQuantity;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public List<ImageModel> getImageNewsList() {
		return imageNewsList;
	}

	public void setImageNewsList(List<ImageModel> imageNewsList) {
		this.imageNewsList = imageNewsList;
	}

	public String getIsLike() {
		return isLike;
	}

	public void setIsLike(String isLike) {
		this.isLike = isLike;
	}

	public List<LikeModel> getLikeHeadListimage() {
		return likeHeadListimage;
	}

	public void setLikeHeadListimage(List<LikeModel> likeHeadListimage) {
		this.likeHeadListimage = likeHeadListimage;
	}

	public List<CommentModel> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<CommentModel> replyList) {
		this.commentList = replyList;
	}

	public String getTimesTamp() {
		return timesTamp;
	}

	public void setTimesTamp(String timesTamp) {
		this.timesTamp = timesTamp;
	}

	public Map<String, String> getTYPE() {
		return TYPE;
	}

	public void setTYPE(Map<String, String> tYPE) {
		TYPE = tYPE;
	}

	public String getTypeContent() {
		return TYPE.get("content");
	}

	public int getTopicID() {
		return topicID;
	}

	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
}
