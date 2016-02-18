package com.jlxc.app.base.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.jlxc.app.base.manager.DBManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.utils.LogUtils;

//该模型对应数据表 jlxc_news_push 该表中不存储类型为'添加好友'的推送类型
public class NewsPushModel {
	//加好友
	public final static int PushAddFriend = 1;
	//回复消息
	public final static int PushNewsAnwser = 2;
	//回复二级评论
	public final static int PushSecondComment = 3;
	//点赞
	public final static int PushLikeNews = 4;
	
	//本条数据的id
	private int pid;
	//发送推送人的id
	private int uid;	
	//发送人的头像
	private String head_image;
	//姓名
	private String name;
	//评论内容
	private String comment_content;	
	//推送类型 Push
	private int type;
	//新闻id
	private int news_id;	
	//新闻内容
	private String news_content;	
	//新闻cover图片
	private String news_image;	
	//新闻用户名
	private String news_user_name;
	//是否已读
	private int is_read;
	//发布时间
	private String push_time;
	//所有者
	private int owner;
	
	/**
	 * 保存数据
	 */
	public void save(){
		
		String sql = "INSERT INTO jlxc_news_push values (null" +
					 ",'"+getUid()+"','"+getHead_image()+"','"+getName()+"' , '"+getComment_content()+"','"+getType()+"'" +
					 ",'"+getNews_id()+"','"+getNews_content()+"','"+getNews_image()+"','"+getNews_user_name()+"','"+getIs_read()+"', '"+getPush_time()+"', '"+getOwner()+"')";
		DBManager.getInstance().excute(sql);
	}
	
	/**
	 * 删除
	 */
	public void remove(){
		String sql = "DELETE FROM jlxc_news_push WHERE id="+getPid();
		DBManager.getInstance().excute(sql);
	}
	/**
	 * 存在
	 */
	public boolean isExist(){
		
		String sql = "SELECT * FROM jlxc_news_push WHERE news_id='"+getNews_id()+"' and uid='"+getUid()+"'"+" and push_time='"+getPush_time()+"'";
	    if (NewsPushModel.findBySql(sql).size() > 0) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * 删除全部
	 */
	public static void removeAll(){
		String sql = "DELETE FROM jlxc_news_push";
		DBManager.getInstance().excute(sql);
	}
	
	/**
	 * 设置已读
	 */
	public static void setIsRead(){
		String sql = "UPDATE jlxc_news_push SET is_read=1";
		DBManager.getInstance().excute(sql);
	}
	
	/**
	 * 从数据库中查出所有的通知
	 * //暂时不分页
	 * @param pageNumber 页数
	 * @param pageCount 每页数据数
	 */
	public static List<NewsPushModel> findAll(){
		String sql = "SELECT * FROM jlxc_news_push where owner="+UserManager.getInstance().getUser().getUid()
				+" ORDER BY id DESC;";
		return findBySql(sql);
	}
	
	/**
	 * 从数据库中分页查出通知
	 * @param pageNumber 页数
	 * @param pageCount 每页数据数
	 */
	public static List<NewsPushModel> findWithPage(int page, int size){
		
		int start = (page-1)*size;
        int end   = size;
		String sql = "SELECT * FROM jlxc_news_push where owner="+UserManager.getInstance().getUser().getUid()
				+" ORDER BY id DESC LIMIT "+start+","+end;
		return findBySql(sql);
	}
	
	/**
	 * 从数据库中查出所有某种类型未读的数据
	 */
	public static List<NewsPushModel> findUnreadCount(){
		String sql = "SELECT * FROM jlxc_news_push WHERE owner="+UserManager.getInstance().getUser().getUid()
				+" and is_read=0 ORDER BY id DESC;";
		return findBySql(sql);
	}
	
	//数组查询
	private static List<NewsPushModel> findBySql(String sql) {
		
		List<NewsPushModel> list = new ArrayList<NewsPushModel>();
		Cursor cursor = DBManager.getInstance().query(sql);
		while (cursor.moveToNext()) {
			NewsPushModel newsPushModel = new NewsPushModel();
			newsPushModel.setPid(cursor.getInt(0));
			newsPushModel.setUid(cursor.getInt(1));
			newsPushModel.setHead_image(cursor.getString(2));
			newsPushModel.setName(cursor.getString(3));
			newsPushModel.setComment_content(cursor.getString(4));
			newsPushModel.setType(cursor.getInt(5));
			newsPushModel.setNews_id(cursor.getInt(6));
			newsPushModel.setNews_content(cursor.getString(7));
			newsPushModel.setNews_image(cursor.getString(8));
			newsPushModel.setNews_user_name(cursor.getString(9));
			newsPushModel.setIs_read(cursor.getInt(10));
			newsPushModel.setPush_time(cursor.getString(11));
			newsPushModel.setOwner(cursor.getInt(12));
			list.add(newsPushModel);
		}
		cursor.close();
		return list;
	}
	
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getHead_image() {
		return head_image;
	}
	public void setHead_image(String head_image) {
		this.head_image = head_image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getNews_id() {
		return news_id;
	}
	public void setNews_id(int news_id) {
		this.news_id = news_id;
	}
	public String getNews_content() {
		return news_content;
	}
	public void setNews_content(String news_content) {
		this.news_content = news_content;
	}
	public String getNews_image() {
		return news_image;
	}
	public void setNews_image(String news_image) {
		this.news_image = news_image;
	}
	public String getNews_user_name() {
		return news_user_name;
	}
	public void setNews_user_name(String news_user_name) {
		this.news_user_name = news_user_name;
	}
	public int getIs_read() {
		return is_read;
	}
	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}
	public String getPush_time() {
		return push_time;
	}
	public void setPush_time(String push_time) {
		this.push_time = push_time;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
	
}
