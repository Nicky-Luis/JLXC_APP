package com.jlxc.app.base.manager;

import java.util.List;

import org.apache.http.NameValuePair;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.utils.JLXCUtils;

public class HttpManager {

	private static HttpUtils http = new HttpUtils();

	private HttpManager() {
	}

//	/**
//	 * 请求（Get方式）
//	 * 
//	 * @param <T>
//	 * @param <T>
//	 * 
//	 * @param url
//	 * @param params
//	 * @param callBack
//	 */
//	public static <T> void get(String url, RequestParams params, JsonRequestCallBack<T> callBack, boolean isEncrypt) {
//		http.configCurrentHttpCacheExpiry(0);
//		http.configDefaultHttpCacheExpiry(0);
////		User user = CacheManager.getInstance().getCurrLoginUser();
////		if (user != null && params != null) {
////			params.addQueryStringParameter("ubaby_user_id", user.getName());
////			params.addQueryStringParameter("device", "android");
////		}
//
//		if (isEncrypt) {
//			List<NameValuePair> paramsList = params.getQueryStringParams();
//			RequestParams paramNew = new RequestParams();
//			if (null != paramsList && paramsList.size() > 0) {
//				String paramStr = "";
//				for (int i = 0; i < paramsList.size(); i++) {
//					if (i == 0) {
//						paramStr += paramsList.get(i);
//					} else {
//						paramStr += "&" + paramsList.get(i);
//					}
//				}
//				paramStr = JLXCUtils.urlEncoding(paramStr);
//				paramNew.addQueryStringParameter("params", paramStr);
//			}
//			if (null != callBack) {
//				callBack.setAbsoluteUrl(getAbsoluteUrl(url, paramNew));
//			}
//			http.send(HttpMethod.GET, url, paramNew, callBack);
//		} else {
//			if (null != callBack) {
//				callBack.setAbsoluteUrl(getAbsoluteUrl(url, params));
//			}
//			http.send(HttpMethod.GET, url, params, callBack);
//		}
//	}

	/**
	 * 请求（Get方式）
	 * 
	 * @param <T>
	 * @param <T>
	 * 
	 * @param url
	 * @param callBack
	 */
	public static <T> void get(String url, JsonRequestCallBack<T> callBack) {
		http.configCurrentHttpCacheExpiry(0);
		http.configDefaultHttpCacheExpiry(0);
		if (null != callBack) {
			callBack.setAbsoluteUrl(url);
		}
		http.send(HttpMethod.GET, url, callBack);
	}

	/**
	 * 上传（Post方式）
	 * 
	 * @param <T>
	 * @param <T>
	 * 
	 * @param url
	 * @param params
	 * @param callBack
	 */
	public static <T> void post(String url, RequestParams params, JsonRequestCallBack<T> callBack) {
//		User user = CacheManager.getInstance().getCurrLoginUser();
//		if (user != null && params != null) {
//			params.addBodyParameter("ubaby_user_id", user.getName());
//			params.addBodyParameter("device", "android");
//		}

		http.configCurrentHttpCacheExpiry(0);
		http.configDefaultHttpCacheExpiry(0);
		http.send(HttpMethod.POST, url, params, callBack);
	}

	public static HttpUtils getHttpUtils() {
		return http;
	}

	/**
	 * 拼装Url
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private static String getAbsoluteUrl(String url, RequestParams params) {
		if (params == null) {
			return "";
		}

		List<NameValuePair> paramsList = params.getQueryStringParams();
		if (null != paramsList && paramsList.size() > 0) {
			String paramStr = "";
			for (int i = 0; i < paramsList.size(); i++) {
				paramStr += "&" + paramsList.get(i);
			}
			return url + paramStr;
		}

		return url;
	}
}
