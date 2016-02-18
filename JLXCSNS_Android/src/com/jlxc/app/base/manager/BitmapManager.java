package com.jlxc.app.base.manager;

import android.content.Context;
import android.graphics.Bitmap;

import com.lidroid.xutils.BitmapUtils;

public class BitmapManager {

//	private static BitmapManager bmpManager;
//	private BitmapUtils headPicBmpUtils;
//	private BitmapUtils bmpUtils;
//
//	private BitmapManager() {
//	};
//
//	public synchronized static BitmapManager getInstance() {
//		if (null == bmpManager) {
//			bmpManager = new BitmapManager();
//		}
//		return bmpManager;
//	}
//
//	public BitmapUtils getBitmapUtils(Context context,boolean isEnableMemoryCache, boolean isEnableDiskCache) {
////		if (null == bmpUtils) {
////			bmpUtils = new BitmapUtils(context);
////			// bmpUtils = new BitmapUtils(context, diskCachePath)
//////			bmpUtils.configDefaultLoadFailedImage(loadFailImage);
////			bmpUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
////			bmpUtils.configMemoryCacheEnabled(isEnableMemoryCache);
////			bmpUtils.configDiskCacheEnabled(isEnableDiskCache);
////		}
//		//不适用单例utils
//		BitmapUtils newBmpUtils = new BitmapUtils(context);
//		newBmpUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//		newBmpUtils.configMemoryCacheEnabled(isEnableMemoryCache);
//		newBmpUtils.configDiskCacheEnabled(isEnableDiskCache);
//		return newBmpUtils;
//	}
//	/**
//	 * 获取头像时使用
//	 * @param context
//	 * @param isEnableMemoryCache
//	 * @param isEnableDiskCache
//	 * @return
//	 */
//	public BitmapUtils getHeadPicBitmapUtils(Context context,int loadFailImage,boolean isEnableMemoryCache, boolean isEnableDiskCache){
////		if(null == headPicBmpUtils){
//////			headPicBmpUtils = new BitmapUtils(context,FileUtil.HEAD_PIC_PATH);
////			headPicBmpUtils = new BitmapUtils(context);
////		}
////		headPicBmpUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
////		headPicBmpUtils.configMemoryCacheEnabled(isEnableMemoryCache);
////		headPicBmpUtils.configDiskCacheEnabled(isEnableDiskCache);
////		headPicBmpUtils.configDefaultLoadFailedImage(loadFailImage);
//		
//		BitmapUtils newHeadPicBmpUtils = new BitmapUtils(context);		
//		try {
//			//不适用单例utils
//			newHeadPicBmpUtils.configDefaultLoadFailedImage(loadFailImage);
//			newHeadPicBmpUtils.configDefaultLoadingImage(loadFailImage);
//			newHeadPicBmpUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//			newHeadPicBmpUtils.configMemoryCacheEnabled(isEnableMemoryCache);
//			newHeadPicBmpUtils.configDiskCacheEnabled(isEnableDiskCache);
//		} catch (Exception e) {
//		}
//		
//		return newHeadPicBmpUtils;
//
////		headPicBmpUtils.configDefaultLoadingImage(bitmap)
//		
////		headPicBmpUtils.configDiskCacheFileNameGenerator(new FileNameGenerator() {
////
////			@Override
////			public String generate(String arg0) {
////				// TODO Auto-generated method stub
////				return arg0.substring(arg0.lastIndexOf("/") + 1);
////			}
////		});
//	}


	
}
