package com.jlxc.app.base.helper;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;

/**
 * 
 * @author Michael.liu
 * 
 */
public class LoadDataHandler<T> {

	public void onStart(T flag){
		
	}

	public void onFailure(HttpException arg0, String arg1,T flag){
		
	}

	public void onSuccess(JSONObject jsonResponse,T flag){
		
	}

	public void onCancelled(T flag) {
	}

	public void onLoading(long total, long current, boolean isUploading,T flag) {
	}
}
