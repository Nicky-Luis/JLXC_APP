package com.jlxc.app.base.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import com.amap.api.mapcore2d.da;
import com.jlxc.app.base.app.JLXCApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;



/**
 * 文件工具基类
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi") public class FileUtil {

	public static boolean flag;
	public static final String ROOT_PATH = getExternalStorageDirectory();
	// 头像存放地址：/jlxc/headPic/icon.jpg
	public static String HEAD_PIC_PATH = ROOT_PATH + "/jlxc/image/headPic/";
	// 缩略图存放地址：/jlxc/image/subImage/bigImage_sub.jpg
	public static final String CHAT_SUB_IMAGE_PATH = ROOT_PATH + "/jlxc/chat/image/subImage/";
	// 大图存放地址：/jlxc/image/bigImage/bigImage.jpg
	public static String CHAT_BIG_IMAGE_PATH = ROOT_PATH + "/jlxc/chat/image/bigImage/";
	// 语音存放地址：/jlxc/audio/audio.mp3
	public static String CHAT_AUDIO_PATH = ROOT_PATH + "/jlxc/chat/audio/";

	//图片根目录
	public static final String IMAGE_ROOT_PATH = ROOT_PATH + "/jlxc/image/";
	
	public static String AUDIO_PATH = ROOT_PATH + "/jlxc/audio/";
	public static String BIG_IMAGE_PATH = ROOT_PATH + "/jlxc/image/bigImage/";
	public static String SUB_IMAGE_PATH = ROOT_PATH + "/jlxc/image/subImage/";
	// 临时目录
	public static final String TEMP_PATH = ROOT_PATH + "/jlxc/image/temp/";
	private static int BUFFER_SIZE = 4 * 1024;

	//创建目录
	public static void makeDirs() {
		File tmpFile = new File(TEMP_PATH);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
	}
	
	/**
	 * 获取sd卡存取路径
	 * 
	 * @return
	 */
	public static String getExternalStorageDirectory() {
		String cachePath = null;
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			external = Environment.getExternalStorageDirectory().getAbsolutePath();
//		}
		
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = JLXCApplication.getInstance().getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = JLXCApplication.getInstance().getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }
		
		return cachePath;
	}

	// public static String getPictureDirectoryPath(){
	// if
	// (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	// {
	// return null;
	// }
	//
	// Environment.getExternalStoragePublicDirectory(type)
	//
	// }

	/**
	 * 删除下载的文件
	 */
	public static void deleteDownloadFiles() {

	}

	/**
	 * 获取指定路径下文件的大小
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 检测文件是否存在
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean checkFileExist(String filePath) {
		try {
			File file = new File(filePath);
			return (file.exists() && file.isFile());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		String strPath = null;
		strPath = getExternalStorageDirectory();
		if (strPath != null) {
			return strPath;
		} else {
			return "/sdcard";
		}
	}

	/**
	 * 删除某个文件夹下的所有文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFileOfDirectory(String path) {
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteFileOfDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		return true;

	}

	public static void del(String filepath) {
		File f = new File(filepath);// 定义文件路径
		if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
			if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
				f.delete();
			} else {
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
					}
					delFile[j].delete();// 删除文件
				}
				// 最后把自己删掉
				f.delete();
			}
		}
	}

	/**
	 * 删除某个文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		flag = false;
		File file = new File(path);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 将图片存到本地
	 * 
	 * @param binaryData
	 * @param filePath
	 * @param fileName
	 * @param quality
	 */
	public static void savePic(byte[] binaryData, String filePath, String fileName, int quality) {
		try {
			Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
			File path = new File(filePath);
			if (!path.exists()) {
				path.mkdirs();
			}
			File picture = new File(path, fileName);
			BufferedOutputStream bos;
			bos = new BufferedOutputStream(new FileOutputStream(picture));
			bmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			bmp.recycle();
			bos.flush();
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将图片存到本地
	 * 
	 * @param bmp
	 * @param filePath
	 * @param fileName
	 * @param quality
	 */
	public static void savePic(Bitmap bmp, String filePath, String fileName, int quality) {
		File path = new File(filePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File picture = new File(path, fileName);
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(picture));
			bmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			bmp.recycle();
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将图片存到本地
	 * 
	 * @param oldPath
	 * @param filePath
	 * @param fileName
	 * @param quality
	 * @return
	 */
	public static boolean savePic(String oldPath, String filePath, String fileName) {
		File oldFile = new File(oldPath + fileName);
		if (!oldFile.exists()) {
			return false;
		} else {
			try {
				FileInputStream in = new FileInputStream(oldFile);
				File path = new File(filePath);
				if (!path.exists()) {
					path.mkdirs();
				}
				File picture = new File(path, fileName);
				FileOutputStream out = new FileOutputStream(picture);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (in.read(buffer) != -1) {
					out.write(buffer);
				}
				in.close();
				out.flush();
				out.close();
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param input
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static boolean savePic(InputStream input, String filePath, String fileName) {
		try {
			File path = new File(filePath);
			if (!path.exists()) {
				path.mkdirs();
			}
			File picture = new File(path, fileName);
			FileOutputStream out = new FileOutputStream(picture);
			byte[] buffer = new byte[BUFFER_SIZE];
			while (input.read(buffer) != -1) {
				out.write(buffer);
			}
			input.close();
			out.flush();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将拍照后临时目录的文件压缩后放到正式目录下
	 */
	public static boolean tempToLocalPath(String fileName, int reqWidth, int reqHeight) {
		File tempPath = new File(FileUtil.TEMP_PATH);
		if (!tempPath.exists()) {
			tempPath.mkdirs();
		}
		File tempFile = new File(tempPath + "/" + fileName);
		if (!tempFile.exists()) {
			return false;
		} else {
			try {
				FileInputStream in = new FileInputStream(tempFile);
				File path = new File(BIG_IMAGE_PATH);
				if (!path.exists()) {
					path.mkdirs();
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(in, null, options);
//				options.inSampleSize = 4;
				options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
				options.inJustDecodeBounds = false;
//				Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
				byte[] data = GetLocalOrNetBitmap(tempFile.getAbsolutePath());
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				int degree = readPictureDegree(tempFile.getAbsolutePath());
				bmp = rotaingImageView(degree, bmp);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(BIG_IMAGE_PATH + fileName));
				//小于150k
				if (getBitmapSize(bmp) < 1024*1024) {
					bmp.compress(Bitmap.CompressFormat.JPEG, 95, bos);
//					LogUtils.i("90 "+getBitmapSize(bmp), 1);
				}else if (getBitmapSize(bmp) < 2560*1024) {
					bmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
//					LogUtils.i("70 "+getBitmapSize(bmp), 1);
				} else {
					bmp.compress(Bitmap.CompressFormat.JPEG, 55, bos);
//					LogUtils.i("50 "+getBitmapSize(bmp), 1);
				}

				bmp.recycle();
				bos.flush();
				bos.close();
				return deleteFile(TEMP_PATH + fileName);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
	}
	
	 public static byte[] GetLocalOrNetBitmap(String url)  {  
        InputStream in = null;  
        BufferedOutputStream out = null;  
        try  
        {  
        	FileInputStream filein = new FileInputStream(url);
            in = new BufferedInputStream(filein, 1024);  
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();  
            out = new BufferedOutputStream(dataStream, 1024);  
            copy(in, out);  
            out.flush();  
            byte[] data = dataStream.toByteArray();
            return data;
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
//            data = null;  
//            return bitmap;  
        }  
        catch (IOException e)  
        {  
            e.printStackTrace();  
            return null;  
        }  
    }
	 
	 private static void copy(InputStream in, OutputStream out) throws IOException {
	        byte[] b = new byte[1024];
	        int read;
	        while ((read = in.read(b)) != -1) {
	            out.write(b, 0, read);
	        }
	 }

	/**
	 * 将相册中的临时目录的文件压缩后放到正式目录下
	 */
	public static boolean tempToLocalPath(String fileRealPath, String fileName, int reqWidth, int reqHeight) {
		try {
			File path = new File(BIG_IMAGE_PATH);
			if (!path.exists()) {
				path.mkdirs();
			}

			File file = new File(fileRealPath);
			if (file == null || !file.exists()) {
				return false;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileRealPath, options);
			// options.inSampleSize = 4;
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			Bitmap bmp = BitmapFactory.decodeFile(fileRealPath, options);
			int degree = readPictureDegree(file.getAbsolutePath());
			bmp = rotaingImageView(degree, bmp); 
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(BIG_IMAGE_PATH + fileName));
			//小于150k
			if (getBitmapSize(bmp) < 1024*1024) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 95, bos);
//				LogUtils.i("90 "+getBitmapSize(bmp), 1);
			}else if (getBitmapSize(bmp) < 2560*1024) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
//				LogUtils.i("70 "+getBitmapSize(bmp), 1);
			} else {
				bmp.compress(Bitmap.CompressFormat.JPEG, 55, bos);
//				LogUtils.i("50 "+getBitmapSize(bmp), 1);
			}

			bmp.recycle();
			bos.flush();
			bos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	
	/**  
     * 旋转图片  
     * @param angle  
     * @param bitmap  
     * @return Bitmap  
     */    
    public static Bitmap rotaingImageView (int angle , Bitmap bitmap) throws Exception {    
        //旋转图片 动作     
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);    
        System.out.println("angle2=" + angle);    
        // 创建新的图片     
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,    
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);    
        return resizedBitmap;    
    }   
	
	/** 
	 * 读取图片属性：旋转的角度 
	 * @param path 图片绝对路径 
	 * @return degree旋转的角度 
	 */  
   public static int readPictureDegree(String path) {  
       int degree  = 0;  
       try {  
               ExifInterface exifInterface = new ExifInterface(path);  
               int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
               switch (orientation) {  
               case ExifInterface.ORIENTATION_ROTATE_90:  
                       degree = 90;  
                       break;  
               case ExifInterface.ORIENTATION_ROTATE_180:  
                       degree = 180;  
                       break;  
               case ExifInterface.ORIENTATION_ROTATE_270:  
                       degree = 270;  
                       break;  
               }  
       } catch (IOException e) {  
               e.printStackTrace();  
       }  
       return degree;  
   } 


	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 保存语音
	 * 
	 * @param data
	 * @param filePath
	 * @param fileName
	 */
	public static void saveAudio(byte[] data, String filePath, String fileName) {
		File path = new File(filePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File audioFile = new File(filePath, fileName);
		try {
			FileOutputStream out = new FileOutputStream(audioFile);
			out.write(data);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 判断头像是否存在，存在则加载本地的，不存在则加载服务器上的，并将服务器上的图片下载到本地
	 * 
	 * @param context
	 * @param fileName
	 * @param localPath
	 * @param remotePath
	 * @param imageview
	 */
	// public static void loadHeadPicture(Context context, String remotePath,
	// ImageView imageview, boolean isDoctor) {
	// BitmapUtils bmpUtil = BitmapManager.getInstance().getBitmapUtils(context,
	// true, false);
	// if (null != remotePath) {
	// String fileName = remotePath.substring(remotePath.lastIndexOf("/") + 1);
	// if (isDoctor) {
	// bmpUtil.configDefaultLoadFailedImage(R.drawable.doc);
	// } else {
	// bmpUtil.configDefaultLoadFailedImage(R.drawable.patient);
	// }
	// if (checkFileExist(HEAD_PIC_PATH + fileName)) {
	// bmpUtil.display(imageview, HEAD_PIC_PATH + fileName);
	// } else {
	// bmpUtil.display(imageview, UBabyConst.DOMIN + remotePath);
	// HttpManager.getHttpUtils().download(UBabyConst.DOMIN + remotePath,
	// HEAD_PIC_PATH + fileName, true, true, null);
	// }
	// } else {
	// if (isDoctor) {
	// imageview.setImageResource(R.drawable.doc);
	// } else {
	// imageview.setImageResource(R.drawable.patient);
	// }
	// }
	// }



	/**
	 * 去掉字符串中的_sub
	 * 
	 * @param str
	 * @return
	 */
	public static String removeSubStr(String str) {
		if (null != str && !"".equals(str)) {
			String fileName = str.substring(str.lastIndexOf("/") + 1);
			if (null != fileName && !"".equals(fileName)) {
				return str.replace("_sub", "");
			} else {
				return str;
			}
		} else {
			return str;
		}
	}

	
	/**
	 * 删除本地附件
	 * 
	 * @param fullPath
	 */
	public static void deleteAttrament(String fullPath) {
		if (null != fullPath && !"".equals(fullPath)) {
			String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
			if (null != fileName && !"".equals(fileName)) {
				if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
					// 删除图片
					deleteFile(BIG_IMAGE_PATH + fileName);
				} else if (fileName.endsWith(".mp3")) {
					// 删除语音文件
					deleteFile(AUDIO_PATH + fileName);
				}
			}
		}
	}

	/**
	 * 通过文件名获取本地bitmap
	 * 
	 * @param filename
	 * @return
	 */
	public static Bitmap getBitmapByFilename(String filename) {
		File file = new File(filename);
		if (file.exists())
			return BitmapFactory.decodeFile(filename);
		return null;
	}

	/**
	 * 根据用户id获取用户头像，并在ImageView里显示
	 * 
	 * @param view
	 * @param contact_id
	 * @return
	 */
	public static boolean loadContactIcon(ImageView view, long contact_id) {
		// Contact contact = null;
		// if (CacheManager.getInstance().getCurrUser().getUserId() ==
		// contact_id) {
		// contact = CacheManager.getInstance().getCurrUser();
		// } else {
		// for (Contact t : CacheManager.getInstance().getContactList()) {
		// if (t.getUserId() == contact_id) {
		// contact = t;
		// break;
		// }
		// }
		// }
		// loadContactIcon(view, contact);
		return false;
	}

	/**
	 * 加载联系人头像
	 * 
	 * @param view
	 * @param iconName
	 */
	// public static boolean loadContactIcon(ImageView view, Contact contact) {
	// if (contact != null && null != contact.getIconPath() &&
	// !contact.getIconPath().equals("")) {
	// String iconPath = HEAD_PIC_PATH + contact.getIconPath();
	// if (checkFileExist(iconPath)) {
	// Bitmap bmp = getBitmapByFilename(iconPath);
	// view.setImageBitmap(bmp);
	// return true;
	// }
	// }
	// return false;
	// }

	/**
	 * 加载群组icon
	 * 
	 * @param view
	 * @param group
	 * @param cacheManager
	 * @return
	 */
	// public static boolean loadGroupIcon(ImageView view, Group group,
	// CacheManager cacheManager) {
	// if (group != null && group.getKind() == 0) {
	// Contact contact = cacheManager.findContact(group.getCreator());
	// return loadContactIcon(view, contact);
	// } else {
	// if (group != null && null != group.getIconPath() &&
	// !group.getIconPath().equals("")) {
	// String iconPath = HEAD_PIC_PATH + group.getIconPath();
	// if (checkFileExist(iconPath)) {
	// Bitmap bmp = getBitmapByFilename(iconPath);
	// view.setImageBitmap(bmp);
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	/**
	 * 加载本地图片
	 * 
	 * @param picFile
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap loadBigImage(String picFile, int reqWidth, int reqHeight) {
		File file = new File(picFile);
		if (!file.exists()) {
			return null;
		} else {
			// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picFile, options);
			// 调用上面定义的方法计算inSampleSize值
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			// 使用获取到的inSampleSize值再次解析图片
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(picFile, options);
		}
	}

	/**
	 * 加载网络获取图片
	 * 
	 * @param data
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap loadBigImage(byte[] data, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public static Bitmap loadBigImage(String picFile, Context context) {
		BitmapFactory.Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picFile, opts);
		int imageHeight = opts.outHeight;
		int imageWidth = opts.outWidth;
		WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		int windowHeight = wm.getDefaultDisplay().getHeight();
		int windowWidth = wm.getDefaultDisplay().getWidth();
		int scaleX = imageWidth / windowWidth;
		int scaleY = imageHeight / windowHeight;
		int scale = 1;
		if (scaleX > scaleY && scaleY >= 1) {
			scale = scaleX;
		}
		if (scaleX < scaleY && scaleX >= 1) {
			scale = scaleY;
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		try {
			InputStream in = new FileInputStream(new File(picFile));
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, opts);
			in.close();
			return bitmap;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap loadBigImage(InputStream in) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		Bitmap bmp = BitmapFactory.decodeStream(in, null, opt);
		try {
			in.close();
			return bmp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 计算图片缩放比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		int height = options.outHeight;
		int width = options.outWidth;
		
		int inSampleSize = 1;
		if (height > (reqHeight+100) || width > (reqWidth+100)) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
			//再次缩小图片
			LogUtils.i(" "+height+" "+reqHeight+" "+width+" "+reqWidth+" ", 1);
			LogUtils.i(" "+inSampleSize+" "+heightRatio+" "+widthRatio+" ", 1);
		} 
//		Log.d("kkk", "originWidth" + width + " originHeight" + height + " reqWidth" + reqWidth + " reqHeight"
//				+ reqHeight + " sampleSize" + inSampleSize);
		return inSampleSize;
	}

	// 将byte[]转换成InputStream
	public static InputStream Byte2InputStream(byte[] b) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return bais;
	}

	// 将InputStream转换成byte[]
	public static byte[] InputStream2Bytes(InputStream is) {
		String str = "";
		byte[] readByte = new byte[1024];
		try {
			while ((is.read(readByte, 0, 1024)) != -1) {
				str += new String(readByte).trim();
			}
			return str.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 将Bitmap转换成InputStream
	public static InputStream Bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// 将Bitmap转换成InputStream
	public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// 将InputStream转换成Bitmap
	public static Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	// Drawable转换成InputStream
	public static InputStream Drawable2InputStream(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return Bitmap2InputStream(bitmap);
	}

	// InputStream转换成Drawable
	public static Drawable InputStream2Drawable(InputStream is) {
		Bitmap bitmap = InputStream2Bitmap(is);
		return bitmap2Drawable(bitmap);
	}

	// Drawable转换成byte[]
	public static byte[] Drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return Bitmap2Bytes(bitmap);
	}

	// byte[]转换成Drawable
	public static Drawable Bytes2Drawable(byte[] b) {
		Bitmap bitmap = Bytes2Bitmap(b);
		return bitmap2Drawable(bitmap);
	}

	// Bitmap转换成byte[]
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	// byte[]转换成Bitmap
	public static Bitmap Bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	// Drawable转换成Bitmap
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// Bitmap转换成Drawable
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		@SuppressWarnings("deprecation")
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}
	
	public static int getBitmapSize(Bitmap bitmap){
//		long s = new Date().getTime();
//		LogUtils.i("1"+(s-new Date().getTime()), 1);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
	        return bitmap.getAllocationByteCount();	        
	    }
//	    LogUtils.i("2"+(s-new Date().getTime()), 1);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
	        return bitmap.getByteCount();
	    }
//	    LogUtils.i("3"+(s-new Date().getTime()), 1);
	    return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
	    
	}
}