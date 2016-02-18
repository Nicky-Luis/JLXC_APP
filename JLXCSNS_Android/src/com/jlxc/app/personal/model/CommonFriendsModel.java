package com.jlxc.app.personal.model;

/**
 * 共同好友model
 * @author lixiaohang
 */
public class CommonFriendsModel {
	
	private int friend_id;//用户id
	private String head_sub_image;//用户头像缩略图
	
	public int getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}
	public String getHead_sub_image() {
		return head_sub_image;
	}
	public void setHead_sub_image(String head_sub_image) {
		this.head_sub_image = head_sub_image;
	}
}
