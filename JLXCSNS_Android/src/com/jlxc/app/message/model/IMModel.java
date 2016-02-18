package com.jlxc.app.message.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.jlxc.app.base.manager.DBManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.utils.LogUtils;

//聊天模型
public class IMModel {
	
	//已经添加
	public static final int GroupHasAdd = 1;
	//未添加
	public static final int GroupNotAdd = 0;
	
	//单聊模式
	public static final int ConversationType_PRIVATE = 1;
	
	//目标id
	private String targetId;
	//标题
	private String title;
	//备注
	private String remark;
	//备注
	private int type;
	//当前状态
	private int currentState;
	//是否已读 1已经添加 0未添加
	private int isRead;
	//是否是新的
	private int isNew;
	//头像
	private String avatarPath;
	//拥有者
	private int owner;
	//添加时间
	private String addDate;
	
	//保存数据
	public void save() {
		//存在不插入
		if (null == findByGroupId(targetId)) {
			if (null == remark) {
				remark = "";
			}
			String sql = "INSERT INTO jlxc_group values (null,'"+targetId+"','"+title+"','"+type+
					"', '"+remark+"', '"+avatarPath+"', '"+currentState+"', '"+isRead+"', '"+isNew+"','"+owner+"','"+
					addDate+"')";
			
			DBManager.getInstance().excute(sql);
		}
	}
	//清空数据
	public void deleteData() {
		String sql = "DELETE FROM jlxc_group";
		DBManager.getInstance().excute(sql);
	}
	//更新数据
	public void update() {
		if (null == remark) {
			remark = "";
		}
		String sql = "UPDATE jlxc_group SET groupTitle = '"+title+"', type="+type+", " +
					 "avatarPath='"+avatarPath+"', groupRemark='"+remark+"', isRead='"+isRead+"'" +
					 " ,currentState='"+currentState+"', isNew='"+isNew+"', addDate='"+addDate+"' WHERE groupId='"+targetId+"'";
		DBManager.getInstance().excute(sql);
	}	
	//删除数据
	public void remove() {
		String sql = "DELETE FROM jlxc_group WHERE groupId='"+targetId+"' AND owner='"+owner+"'";
		Log.i("NewFriendsActivity", sql);
		DBManager.getInstance().excute(sql);
	}
//	//查询全部数据
//	public List<IMModel> findAll() {
//		String sql = "SELECT * FROM jlxc_group WHERE type=1 " +
//				     "AND owner='"+UserManager.getInstance().getUser().getUid()+"' ORDER BY id DESC";
//		return findBySql(sql);
//	}
//	//查出前三个好友
//	public List<IMModel> findCoverThree() {
//		String sql = "SELECT * FROM jlxc_group WHERE type=1 " +
//			     "AND owner='"+UserManager.getInstance().getUser().getUid()+"' ORDER BY id DESC  LIMIT 3";
//		return findBySql(sql);
//	}
	//查出所有已经添加的群组 (好友列表)
	public static List<IMModel> findHasAddAll() {
		String sql = "SELECT * FROM jlxc_group WHERE type=1 AND currentState=1" +
			     " AND owner='"+UserManager.getInstance().getUser().getUid()+"' ORDER BY id DESC";
		return findBySql(sql);
	}
	//从数据库中查出新的朋友群组 最近的三条没有被添加的人
	public static List<IMModel> findThreeNewFriends() {
		String sql = "SELECT * FROM jlxc_group WHERE type=1 and isNew=1 and isRead=0" +
			     " AND owner='"+UserManager.getInstance().getUser().getUid()+"' ORDER BY addDate DESC LIMIT 3";
		return findBySql(sql);
	}
	//从数据库中查出全部新的朋友群组
	public static List<IMModel> findAllNewFriends() {
		String sql = "SELECT * FROM jlxc_group WHERE type=1 and isNew=1" +
			     " AND owner='"+UserManager.getInstance().getUser().getUid()+"' ORDER BY addDate DESC";
		return findBySql(sql);
	}	
	//从数据库中查出全部未读的新朋友数量
	public static int unReadNewFriendsCount() {
		
		String sql = "SELECT * FROM jlxc_group WHERE type=1 and isNew=1 and isRead=0" +
			     " AND owner='"+UserManager.getInstance().getUser().getUid()+"'";
		return findBySql(sql).size();
	}
	//把所有未读设置为已读
	public static void hasRead() {
		String sql = "UPDATE jlxc_group SET isRead=1 WHERE owner='"+UserManager.getInstance().getUser().getUid()+"'";
		DBManager.getInstance().excute(sql);
	}
	
	//通过群组查找群组
	public static IMModel findByGroupId(String targetId) {
		
		String sql = "SELECT * FROM jlxc_group WHERE owner='"+UserManager.getInstance().getUser().getUid()+"' " +
					 "AND groupId='"+targetId+"' limit 1";
		List<IMModel> list = findBySql(sql);
		IMModel model = null;
		if (list.size() > 0) {
			model = list.get(0);
		}			 
		return model;
	}
	
	private static List<IMModel> findBySql(String sql) {
		
		List<IMModel> list = new ArrayList<IMModel>();
		Cursor cursor = DBManager.getInstance().query(sql);
		while (cursor.moveToNext()) {
			IMModel imModel = new IMModel();
			imModel.setTargetId(cursor.getString(1));
			imModel.setTitle(cursor.getString(2));
			imModel.setType(cursor.getInt(3));
			imModel.setRemark(cursor.getString(4));
			imModel.setAvatarPath(cursor.getString(5));
			imModel.setCurrentState(cursor.getInt(6));
			imModel.setIsRead(cursor.getInt(7));
			imModel.setIsNew(cursor.getInt(8));
			imModel.setOwner(cursor.getInt(9));
			imModel.setAddDate(cursor.getString(10));
			list.add(imModel);
		}
		cursor.close();
		return list;
	}
	
	
	
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCurrentState() {
		return currentState;
	}
	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	public int getIsNew() {
		return isNew;
	}
	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}
	public String getAvatarPath() {
		return avatarPath;
	}
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public String getAddDate() {
		return addDate;
	}
	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}	
	
	
}
