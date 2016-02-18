package com.jlxc.app.base.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.utils.HttpCacheUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class JsonRequestCallBack<T> extends RequestCallBack<T> {

	private LoadDataHandler<T> handler;
	private T flag;
	private String absoluteUrl;
//	private DBHelper dbHelper;

	public JsonRequestCallBack(LoadDataHandler<T> handler, T flag) {
		this.handler = handler;
		this.flag = flag;
//		this.dbHelper = DBHelper.getInstence(UBabyApplication.application.getApplicationContext());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		handler.onStart(flag);
	}

	@Override
	public void onFailure(HttpException arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
//			HttpLocalCache localCache = dbHelper.queryHttpLocalCache(absoluteUrl);
//			if (null != localCache) {
//				handler.onSuccess(JSON.parseObject(localCache.getJsonValue()), flag);
//			} else {
			handler.onFailure(arg0, arg1, flag);
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.onFailure(arg0, arg1, flag);
		}
	}

	@Override
	public void onSuccess(ResponseInfo<T> arg0) {
		// TODO Auto-generated method stub		
		try {
			JSONObject obj = JSON.parseObject(arg0.result.toString());
			//数据缓存
			if (obj != null) {
				HttpCacheUtils.saveHttpCache(absoluteUrl, obj.toJSONString());
			}
			handler.onSuccess(obj, flag);
//			HttpLocalCache localCache = dbHelper.queryHttpLocalCache(absoluteUrl);
//			if (null == localCache) {
//				localCache = new HttpLocalCache();
//				localCache.setKey(absoluteUrl);
//				localCache.setJsonValue(obj.toJSONString());
//				dbHelper.insertHttpLocalCache(localCache);
//			} else {
//				localCache.setJsonValue(obj.toJSONString());
//				dbHelper.updateHttpLocalCache(localCache);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.onFailure(null, "服务器返回数据格式错误！", flag);
		}
	}

	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		handler.onCancelled(flag);
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {
		// TODO Auto-generated method stub
		super.onLoading(total, current, isUploading);
		handler.onLoading(total, current, isUploading, flag);
	}

	public String getAbsoluteUrl() {
		return absoluteUrl;
	}

	public void setAbsoluteUrl(String absoluteUrl) {
		this.absoluteUrl = absoluteUrl;
	}
}
