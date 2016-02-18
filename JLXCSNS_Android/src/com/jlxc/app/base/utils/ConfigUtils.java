package com.jlxc.app.base.utils;

import com.jlxc.app.base.app.JLXCApplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//配置存储
public class ConfigUtils {
	
	//最后一次校园主页新闻刷新
	public static String LAST_REFRESH__SCHOOL_HOME_NEWS_DATE = "lastSchoolHomeNewsRefreshDate";
	
	//存储配置
	public static void saveConfig(String key,String value){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			Editor editor = httpPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}
	//存储配置
	public static void saveConfig(String key,int value){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			Editor editor = httpPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}
	
	//存储配置
	public static void saveConfig(String key,boolean value){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			Editor editor = httpPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}	
	
	//获取配置
	public static String getStringConfig(String key){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			return httpPreferences.getString(key, "");
		} catch (Exception e) {
			return null;
		}
	}
	//获取配置
	public static int getIntConfig(String key){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			return httpPreferences.getInt(key, 0);
		} catch (Exception e) {
			return 0;
		}
	}
	//获取配置
	public static boolean getBooleanConfig(String key){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			return httpPreferences.getBoolean(key, false);
		} catch (Exception e) {
			return false;
		}
	}	
	
	
	//清空缓存
	public static void clearConfig(){
		try {
			SharedPreferences httpPreferences = JLXCApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			httpPreferences.edit().clear();
		} catch (Exception e) {
		}
	}
	
}
