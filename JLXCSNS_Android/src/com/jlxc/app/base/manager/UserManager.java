package com.jlxc.app.base.manager;

import android.database.Cursor;

import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.utils.LogUtils;

/**
 * 用户Manager
 */
public class UserManager {

	private UserModel user;
	
	private static UserManager userManager;

	public synchronized static UserManager getInstance() {
		if (userManager == null) {
			userManager = new UserManager();
			userManager.user = new UserModel();
		}
		return userManager;
	}

	private UserManager() {
	}

	public UserModel getUser() {
		
		if (null == user.getUsername() && null == user.getLogin_token()) {
			find(); 
		}
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}
	
	//本地持久化
	public void saveAndUpdate() {
		clear();
		String sql = "insert into jlxc_user (id,username,name,helloha_id,sex,phone_num,school,school_code,head_image,head_sub_image,age,birthday,city,sign,background_image,login_token,im_token,iosdevice_token) " +
				     "values ('"+user.getUid()+"', '"+user.getUsername()+"', '"+user.getName()+"', '"+user.getHelloha_id()+"'," +
				     " '"+user.getSex()+"', '"+user.getPhone_num()+"', '"+user.getSchool()+"', '"+user.getSchool_code()+"'" +
				     ", '"+user.getHead_image()+"', '"+user.getHead_sub_image()+"', '"+user.getAge()+"', '"+user.getBirthday()+"'" +
				     ", '"+user.getCity()+"', '"+user.getSign()+"', '"+user.getBackground_image()+"', '"+user.getLogin_token()+"', '"+user.getIm_token()+"', '')";
		DBManager.getInstance().excute(sql);
	}
	
	//获取本地数据
	public void find() {
		
		String sql = "select * from jlxc_user Limit 1";
		Cursor cursor = DBManager.getInstance().query(sql);
		if (cursor.moveToNext()) {
			UserModel userModel = new UserModel();
			userModel.setUid(cursor.getInt(0));
			userModel.setUsername(cursor.getString(1));
			userModel.setName(cursor.getString(2));
			userModel.setHelloha_id(cursor.getString(3));
			userModel.setSex(cursor.getInt(4));
			userModel.setPhone_num(cursor.getString(5));
			userModel.setSchool(cursor.getString(6));
			userModel.setSchool_code(cursor.getString(7));
			userModel.setHead_image(cursor.getString(8));
			userModel.setHead_sub_image(cursor.getString(9));
			userModel.setAge(cursor.getInt(10));
			userModel.setBirthday(cursor.getString(11));
			userModel.setCity(cursor.getString(12));
			userModel.setSign(cursor.getString(13));
			userModel.setBackground_image(cursor.getString(14));
			userModel.setLogin_token(cursor.getString(15));
			userModel.setIm_token(cursor.getString(16));
			setUser(userModel);
		}
	}
	
	//清除本地数据
	public void clear() {
		String sql = "delete from jlxc_user";
		DBManager.getInstance().excute(sql);
	}
	

//	public void setCurrLoginUser(Context context, User currLoginUser) {
//		DbUtils db = DBManager.getInstance().getDB(context);
//		User dbUser;
//		try {
//			if (null != currLoginUser) {
//				dbUser = db.findFirst(User.class);
//				if (null == dbUser) {
//					db.save(currLoginUser);
//				} else {
//					db.update(currLoginUser);
//				}
//			}
//		} catch (DbException e) {
//			e.printStackTrace();
//		}
//		this.currLoginUser = currLoginUser;
//	}
}
