package com.jlxc.app.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/***
 * 一天以内按距离现在的时间是多少，eg:4分钟前，16小时前 超过一天，两天以内显示为昨天+时间，例如：昨天 12:22
 * 超过两天，三天以内显示为前天+时间，例如：昨天 12:22 超过三天，同一年以内显示为：月份+日期+时间，eg：5月12日 12:20
 * 超过一年的显示为：年份+月份+日期+时间，eg：2012年5月12日 12:20
 */
@SuppressLint("SimpleDateFormat")
public class TimeHandle {

	// 获取要显示的时间格式
	public static String getShowTimeFormat(String dateStr) {
		String decTime = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date dt = new Date();
		Date time = null;

		try {
			time = format.parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			//旧的时间
			Calendar oldCalendar = Calendar.getInstance();
			oldCalendar.setTime(dt);

			// 与当前时间差，毫秒ms
			long diff = dt.getTime() - time.getTime();

			// 将时间差转化成各类型
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			
			//当天晚上23:59:59
			Calendar todayEnd = Calendar.getInstance();  
	        todayEnd.set(Calendar.HOUR_OF_DAY, 23);  
	        todayEnd.set(Calendar.MINUTE, 59);  
	        todayEnd.set(Calendar.SECOND, 59);  
	        todayEnd.set(Calendar.MILLISECOND, 999);
	        
	        long dayDiff = todayEnd.getTime().getTime() - time.getTime();  
			long diffDays = dayDiff / (24 * 60 * 60 * 1000);
			
			int year = calendar.get(Calendar.YEAR); // 获取年;
			int month = calendar.get(Calendar.MONTH)+1; // 获取月;
			int day = calendar.get(Calendar.DATE); // 获取日;
			int hour = calendar.get(Calendar.HOUR_OF_DAY); // 获取小时;
			int min = calendar.get(Calendar.MINUTE); // 获取分钟;
			
			String hourStr = String.valueOf(hour);
			String minStr = String.valueOf(min);
			if (hour < 10) {
				hourStr = "0" + hour;
			}
			if (min < 10) {
				minStr = "0" + min;
			}
			calendar = Calendar.getInstance();
			int currentYear = calendar.get(Calendar.YEAR); // 获取当前年份

			if (diffSeconds < 60 && diffMinutes <= 0 && diffHours <= 0
					&& diffDays <= 0) { 
				decTime = "刚刚";
			} else if (diffDays <= 0 && diffHours <= 0) {
				decTime = diffMinutes + "分钟前";
			} else if (diffDays <= 0 && diffHours > 0) {
				decTime = diffHours + "小时前";
			} else if (1 == diffDays) {
				decTime = "昨天 " + hourStr + ":" + minStr;
			} else if (2 == diffDays) {
				decTime = "前天 " + hourStr + ":" + minStr;
			} else if (currentYear == year) {
				decTime = month + "月" + day + "日 " + hourStr + ":" + minStr;
			} else {
				decTime = year + "年" + month + "月" + day + "日 " + hourStr + ":"
						+ minStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decTime;
	}

	/**
	 * 当前系统的时间
	 * */

	public static String getCurrentDataStr() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sDateFormat.format(new java.util.Date());
	}
}
