package com.jlxc.app.base.app;


import io.rong.imkit.RongIM;
import io.yunba.android.manager.YunBaManager;

import com.jlxc.app.base.helper.RongCloudEvent;
import com.jlxc.app.base.manager.DBManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.utils.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

/**
 *Application
 * 
 * @author Direct.Hao
 * 
 */
public class JLXCApplication extends Application {
	// application
	public static JLXCApplication application;
	public static boolean isDebug;

	public static JLXCApplication getInstance() {
        return application;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = (JLXCApplication) getApplicationContext();
		//数据库初始化
		DBManager.getInstance(); 
		//FileUtils初始化
		FileUtil.makeDirs();
		//融云初始化
		RongIM.init(this);
		// 融云SDK事件监听处理
        RongCloudEvent.init(this); 
		//云巴初始化
		YunBaManager.start(getApplicationContext());
        //初始化用户模型
        UserManager.getInstance().getUser();
        //友盟测试模式
        MobclickAgent.setDebugMode(true);
		 //初始化TuTuSDK
////		TuSdk.enableDebugLog(true);
////		TuSdk.init(this.getApplicationContext(),"f1c31a2ee6e0fe88-00-t14qn1");
        //初始化图片加载对象
        ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(getApplicationContext()));
	}  

//	public static String getCurProcessName(Context context) {
//        int pid = android.os.Process.myPid();
//        ActivityManager activityManager = (ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
//                .getRunningAppProcesses()) {
//            if (appProcess.pid == pid) {
//                return appProcess.processName;
//            }
//        }
//        return null;
//    }
//	
////	private void init() {
////		new Thread(new Runnable() {
////			@Override
////			public void run() {
////			}
////		}).start();
////	}
//
//	@Override
//	public void onTerminate() {
//		super.onTerminate();
//	}
}
