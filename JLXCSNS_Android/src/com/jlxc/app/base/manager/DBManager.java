package com.jlxc.app.base.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.jlxc.app.base.app.JLXCApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DBManager {

	public static final String ROOT_PATH = getExternalStorageDirectory();
	private static DBManager dbManager;
	private SQLiteOpenHelper helper;

	private DBManager() {
	};

	@SuppressLint("SdCardPath") 
	public synchronized static DBManager getInstance() {

		if (null == dbManager) {
			dbManager = new DBManager();
			dbManager.dbImport(JLXCApplication.getInstance());
			dbManager.helper = new SQLiteOpenHelper(JLXCApplication.getInstance(), ROOT_PATH+"/jlxc/jlxc.db", null, 1) {
				@Override
				public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {					
				}
				@Override
				public void onCreate(SQLiteDatabase db) {					
				}
			};
			//检测是否需要更新本地数据库
			dbManager.checkOldDB();
		}
		return dbManager;
	}
	//测试用增加
	public void add() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into person (name) values(?)", new Object[]{"li"});
		
		db.close();
	}
	//测试用查找
	public void find() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from jlxc_group", null);
		while (cursor.moveToNext()) {
			 String value = cursor.getString(1);
			 Log.i("--", value);
		}
		cursor.close();
		db.close();
	}
	
	//执行
	public int excute(String sql) {
		try {
			SQLiteDatabase db = helper.getWritableDatabase();
			db.execSQL(sql);
			//不关闭了 由于特殊的多线程环境 excute有时和query同时进行 导致db关闭崩溃
			//因为DB操作频率以及数据量 都不是很大 所以不关闭不会太影响效率
//			db.close();	
			return 1;
		} catch (Exception e) {
		}
		return 0;
	}
	
	//查询
	public Cursor query(String sql) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		
		return cursor;
//		查询完毕手动关闭
//		db.close(); //这里因为场景问题 没有关闭db只关闭了cursor 以后处理
//		cursor.close();

	}
	
	@SuppressLint("SdCardPath") 
	private void dbImport(Context context)
    {
		String dbpath = ROOT_PATH+"/jlxc/jlxc.db";
		File file =  new File(dbpath);
		if (file.exists()) {
			Log.i("--", "数据库已经存在");
			return;
		}
        try {
            File dir = new File(ROOT_PATH+"/jlxc");
            // 如果/sdcard/testdb目录中存在，创建这个目录
            if (!dir.exists()){
            	dir.mkdir();
            	Log.i("--", "没有目录");
            }
            // 如果在/sdcard/testdb目录中不存在
            // test.db文件，则从asset\db目录中复制这个文件到
            // SD卡的目录（/sdcard/testdb）
            if ((new File(dbpath)).exists() == false)
            {
            	// File f = (new File(databaseFilename2));
            	// f.delete();
              // 获得封装testDatabase.db文件的InputStream对象
                AssetManager asset = context.getAssets();
                InputStream is = asset.open("jlxc.db");
                //File dbfile = new File(context.getFilesDir().getAbsolutePath() +File.separator+ "mydb.db");  
                FileOutputStream fos = new FileOutputStream(dbpath);
                byte[] buffer = new byte[1024];
                int count = 0;
                // 开始复制testDatabase.db文件
                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
//                asset.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // deleteDatabase("testDatabase.db");
    }
	
	public SQLiteOpenHelper getHelper() {
		return helper;
	}

	public void setHelper(SQLiteOpenHelper helper) {
		this.helper = helper;
	}
	
//	private void copyDatabase(Context context) throws Exception{  
//        Log.i("Copy","copy start");  
//        File dbfile = new File(context.getFilesDir().getAbsolutePath() +File.separator+ "mydb.db");  
//        File dir = dbfile.getParentFile();  
//        if(dir.exists() == false){  
//            dir.mkdirs();  
//        }  
//        //��contentprovider��ɵ�dbɾ��  
//        if(dbfile.exists()){  
//            dbfile.delete();  
//        }  
//          
//        InputStream is = context.getResources().openRawResource(R.raw.library);   
//        FileOutputStream fos =  new FileOutputStream( dbfile);  
//          
//        byte[] buffer =new byte[1024];  
//        int size = 0;  
//        int length = 0; //�ֽ�  
//        while( (length= is.read(buffer)) > 0){  
//            fos.write(buffer,0,length);  
//            size += length;  
//              
//            Message msg = new Message();  
//            msg.what = 1;  
//            msg.arg1 = size;  
//            handler.sendMessage(msg);  
//        }  
//        fos.flush();  
//        fos.close();  
//        is.close();  
//          
//        Log.e("Copy","copy end");  
//        Message msg = new Message();  
//        msg.what = 0;  
//        msg.arg1 = 0;  
//        handler.sendMessage(msg);  
//    } 
	
	//files
	public static String getExternalStorageDirectory() {
		String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = JLXCApplication.getInstance().getExternalFilesDir("");
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = JLXCApplication.getInstance().getFilesDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }
		
		return cachePath;
	}
	
	//检测旧版本数据库是否需要更新
	private void checkOldDB() {
		
	}
}