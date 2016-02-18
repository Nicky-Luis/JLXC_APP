package com.jlxc.app.login.ui.activity;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.activity.MainTabActivity;
import com.jlxc.app.base.ui.view.CustomSelectPhotoDialog;
import com.jlxc.app.base.ui.view.gallery.imageloader.GalleyActivity;
import com.jlxc.app.base.utils.FileUtil;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RegisterInformationActivity extends BaseActivityWithTopBar {

	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_ZOOM = 3;// 缩放
	public static final int PHOTO_RESOULT = 4;// 结果
	
	public static final int GET_DEPARTMENT_REQUEST_CODE = 5;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	
	//图片名字
	private String tmpImageName;
	//当前图片名字
	private String currentImageName;
	
	//头像
	@ViewInject(R.id.headImageView)
	private ImageView headImageView;
	//性别
	@ViewInject(R.id.radioGroup)
	private RadioGroup radioGroup;
	//姓名
	@ViewInject(R.id.nameEt)
	private EditText nameEditText;
	//点击头像的image弹窗
//	AlertDialog imageDialog;
	
	//统计处理点击
	@OnClick({R.id.headImageView,R.id.register_information_activity,R.id.confirmBtn})
	private void clickEvent(View view) {
		switch (view.getId()) {
		//头像点击
		case R.id.headImageView:
			showChoiceImageAlert();
			break;
		//确认
		case R.id.confirmBtn:
			//提交信息
			uploadInformation();
			break;
//		case R.id.base_ll_right_btns:
//			//跳过
//			Intent intent = new Intent(this, MainTabActivity.class);
//			startActivityWithRight(intent);
//			break;
		case R.id.register_information_activity:
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);  
	        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	        break;
		default:
			break;
		}
	}
	
	
//	public Bitmap convertViewToBitmap(View view){
//		view.setDrawingCacheEnabled(Boolean.TRUE);
//		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();
//        
//		return bitmap;
//	}
	
	//上传个人信息
	private void uploadInformation(){
		
		//如果选择头像了
		//bitmap获取
//		headImageView.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(headImageView.getDrawingCache());
//        headImageView.setDrawingCacheEnabled(false);	
//        String photoName = getPhotoFileName();
//		//图片保存到本地
//		FileUtil.savePic(Bitmap.createBitmap(bitmap), FileUtil.HEAD_PIC_PATH, photoName, 100);
		if (null == currentImageName || currentImageName.length() < 1) {
			ToastUtil.show(this, "跪求您设置一下头像和昵称...");
			return;
		}
		if (nameEditText.getText().toString().length() < 1) {
			ToastUtil.show(this, "跪求您设置一下头像和昵称...");
			return;
		}
		
		if (nameEditText.getText().toString().length() > 10) {
			ToastUtil.show(this, "昵称不能超过10个字...");
			return;
		}
		
		final UserModel userModel = UserManager.getInstance().getUser();
		RequestParams params = new RequestParams();
		showLoading("信息上传中，请稍候...", false);
		final File imageFile = new File(FileUtil.HEAD_PIC_PATH+currentImageName);
		if (imageFile.exists()) {
			//图片
			params.addBodyParameter("image", imageFile);			
		}
			
		params.addBodyParameter("uid", userModel.getUid()+"");
//		params.addBodyParameter("uid", "1");
		String sex = "0";
		//性别
		switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.radioMale:
				sex = "0";	
				break;
			case R.id.radioFemale:
				sex = "1";
				break;
			default:
				break;
		}
		params.addBodyParameter("sex", sex);
		//姓名
		params.addBodyParameter("name", nameEditText.getText().toString());
		final int sexValue = Integer.parseInt(sex);
		HttpManager.post(JLXCConst.SAVE_PERSONAL_INFO, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse.getIntValue("status");
						switch (status) {
						case JLXCConst.STATUS_SUCCESS:						
							//设置修改内容
							userModel.setName(jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT).getString("name"));
							userModel.setSex(sexValue);
							userModel.setHead_image(jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT).getString("head_image"));
							userModel.setHead_image(jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT).getString("head_sub_image"));
							
							//数据持久化
							UserManager.getInstance().saveAndUpdate();
							
							//toast
							Toast.makeText(RegisterInformationActivity.this, jsonResponse.getString(JLXCConst.HTTP_MESSAGE), Toast.LENGTH_SHORT).show();
							hideLoading();
							//跳到主页
							Intent intent = new Intent(RegisterInformationActivity.this, MainTabActivity.class);
							startActivityWithRight(intent);
							
							//删除缓存
							if (imageFile.exists()) {
								imageFile.delete();
							}
							
							break;
						case JLXCConst.STATUS_FAIL:
							hideLoading();
							Toast.makeText(RegisterInformationActivity.this, jsonResponse.getString(JLXCConst.HTTP_MESSAGE), Toast.LENGTH_SHORT).show();
							break;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1, String flag) {
						LogUtils.i(arg0.getMessage(), 1);
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						Toast.makeText(RegisterInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
					}
		 }, null));
	}
	
	private void showChoiceImageAlert() {
//		if (imageDialog == null) {
//			imageDialog = new AlertDialog.Builder(this).setTitle("选择照片").setItems(new String[]{"拍照","相册"}, new OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					
//					if (which == 0) {
//						//相机
//						Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//						tmpImageName = JLXCUtils.getPhotoFileName()+"";
//						File tmpFile = new File(FileUtil.TEMP_PATH+tmpImageName);
//						intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
//				                Uri.fromFile(tmpFile));
//						startActivityForResult(intentCamera, TAKE_PHOTO);
//					}else {
//						//相册
//						tmpImageName = JLXCUtils.getPhotoFileName()+"";
//						Intent intentAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//						intentAlbum.setType(IMAGE_UNSPECIFIED);
//						startActivityForResult(intentAlbum, ALBUM_SELECT);
//						
//					}					
//				}
//			}).setNegativeButton("取消", null).create();
//		}
//		
//		imageDialog.show();
		
		// 设置为头像
		final CustomSelectPhotoDialog selectDialog = new CustomSelectPhotoDialog(this);
		selectDialog.show();
		selectDialog.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

					@Override
					public void onSelectGallery() {
						//相册
						tmpImageName = JLXCUtils.getPhotoFileName()+"";
//						Intent intentAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//						intentAlbum.setType(IMAGE_UNSPECIFIED);
//						startActivityForResult(intentAlbum, ALBUM_SELECT);
						// 相册
						Intent intentAlbum = new Intent(RegisterInformationActivity.this,GalleyActivity.class);
						intentAlbum.putExtra(GalleyActivity.INTENT_KEY_SELECTED_COUNT,0);
						intentAlbum.putExtra(GalleyActivity.INTENT_KEY_ONE, true);
						startActivityForResult(intentAlbum, ALBUM_SELECT);
						
						selectDialog.dismiss();
					}

					@Override
					public void onSelectCamera() {
						//相机
						Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						tmpImageName = JLXCUtils.getPhotoFileName()+"";
						File tmpFile = new File(FileUtil.TEMP_PATH+tmpImageName);
						intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
				                Uri.fromFile(tmpFile));
						startActivityForResult(intentCamera, TAKE_PHOTO);
						selectDialog.dismiss();
					}

				});		
	}
	
	//图片滤镜
//	private void filterImage(final String path) {
//		File filterFile = new File(path);
//		// 组件委托
//		TuSdkComponentDelegate delegate = new TuSdkComponentDelegate()
//		{
//			@Override
//			public void onComponentFinished(TuSdkResult result, Error error,
//					TuFragment lastFragment)
//			{
//				File oriFile = result.imageFile;
//				File newFile = new File(path);
//				boolean filterOK = oriFile.renameTo(newFile);
//				if (filterOK) {
//    				BitmapManager.getInstance().getBitmapUtils(RegisterInformationActivity.this, false, false).display(headImageView, FileUtil.HEAD_PIC_PATH+tmpImageName);
//				}else {
//					ToastUtil.show(RegisterInformationActivity.this, "图片处理失败T_T");
//				}
//			}
//			
//		};
//		
//		TuEditComponent component = TuSdk.editCommponent(this,delegate);
//		component.componentOption().editEntryOption().setEnableCuter(false);
//		component.componentOption().editEntryOption().setEnableSticker(false);
//		component.componentOption().editEntryOption().setSaveToAlbum(false);
//		component.componentOption().editEntryOption().setAutoRemoveTemp(false);
//		component.componentOption().editEntryOption().setSaveToTemp(true);
//		component.componentOption().editEntryOption().setOutputCompress(80);
//		
//		TuSdkResult result = new TuSdkResult();
//		result.imageFile = filterFile;
//		
//		// 设置图片
//		component.setImage(result.image)
//		// 设置系统照片
//				.setImageSqlInfo(result.imageSqlInfo)
//				// 设置临时文件
//				.setTempFilePath(result.imageFile)
//				// 在组件执行完成后自动关闭组件
//				.setAutoDismissWhenCompleted(true)
//				// 开启组件
//				.showComponent();
//		
//	}
	
	 @Override  
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        // TODO Auto-generated method stub  
	        super.onActivityResult(requestCode, resultCode, data);  
	        if (resultCode == Activity.RESULT_OK) {  
	            String sdStatus = Environment.getExternalStorageState();  
	            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
	            	LogUtils.i("SD card is not avaiable/writeable right now.", 1);
	                return;  
	            }  
	        	//头像需要缩放	            
	            switch (requestCode) {
	            case TAKE_PHOTO:// 当选择拍照时调用   
	            	File tmpFile = new File(FileUtil.TEMP_PATH+tmpImageName);
	                startPhotoZoom(Uri.fromFile(tmpFile));
	                break;
	            case ALBUM_SELECT:// 当选择从本地获取图片时
	                // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
	                if (data != null) {
//	                    startPhotoZoom(data.getData());
						@SuppressWarnings("unchecked")
						List<String> resultList = (List<String>) data.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
						// 循环处理图片
						for (String fileRealPath : resultList) {
							//只取一张
							File tmpAlbumFile = new File(fileRealPath);
							startPhotoZoom(Uri.fromFile(tmpAlbumFile));
							break;
						}
	                }
	                break;
	            case PHOTO_RESOULT:// 返回的结果
	                if (data != null){
	        			
	        			if (null != tmpImageName) {
		    				//删除临时文件
		        			File file = new File(FileUtil.TEMP_PATH+tmpImageName);
		    				if (file.exists()) {
		    					file.delete();
		    				}
		    				
		    				DisplayImageOptions headImageOptions = new DisplayImageOptions.Builder()  
		    		        .showImageOnLoading(R.drawable.default_avatar)  
		    		        .showImageOnFail(R.drawable.default_avatar)  
		    		        .cacheInMemory(false)  
		    		        .cacheOnDisk(false)  
		    		        .bitmapConfig(Bitmap.Config.RGB_565)  
		    		        .build();
		    				//当前的图片名字
		    				currentImageName = tmpImageName;
		    				ImageLoader.getInstance().displayImage("file://"+FileUtil.HEAD_PIC_PATH+tmpImageName, headImageView, headImageOptions);
//		    				filterImage(FileUtil.HEAD_PIC_PATH+tmpImageName);
//		    				BitmapManager.getInstance().getBitmapUtils(RegisterInformationActivity.this, false, false).display(headImageView, FileUtil.HEAD_PIC_PATH+tmpImageName);
		    				
	        			}	 
	                }
	                break;
	            }
	        }  
	 }
	//开启缩放
	 public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		
		intent.putExtra("outputX", 960);
		intent.putExtra("outputY", 960);
		
		// outputX outputY 是裁剪图片宽高
//		if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
//			intent.putExtra("outputX", 320);
//			intent.putExtra("outputY", 320);
//		}else if (Build.MANUFACTURER.equalsIgnoreCase("MEIZU")) {
//			intent.putExtra("outputX", 250);
//			intent.putExtra("outputY", 250);
//		}else {
//			intent.putExtra("outputX", 960);
//			intent.putExtra("outputY", 960);
//		}
		intent.putExtra("scaleUpIfNeeded", true);//黑边
		intent.putExtra("scale", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
//		intent.putExtra("return-data", true);
		
		File headDir = new File(FileUtil.HEAD_PIC_PATH);
		if (!headDir.exists()) {
			headDir.mkdir();
		}
		File tmpFile = new File(FileUtil.HEAD_PIC_PATH+tmpImageName);
		//地址
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
		startActivityForResult(intent, PHOTO_RESOULT);
	 }

	// 使用系统当前日期加以调整作为照片的名称
    @SuppressLint("SimpleDateFormat") 
    private String getPhotoFileName() {
    	//用户id+时间戳
    	String fileName = UserManager.getInstance().getUser().getUid()+""+System.currentTimeMillis()/1000;
        return fileName + ".jpg";
    }
	 
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_register_information;
	}

	@Override
	protected void setUpView() {
//		radioGroup.getCheckedRadioButtonId();
		radioGroup.check(R.id.radioMale);
//		addRightBtn("跳过");
		setBarText("填写个人信息~");
		RelativeLayout rlBar = (RelativeLayout) findViewById(R.id.layout_base_title);
		rlBar.setBackgroundResource(R.color.main_clear);
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub 
		super.onSaveInstanceState(outState);
		outState.putSerializable("tmpImageName", tmpImageName);  
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (null != savedInstanceState) {
			tmpImageName = savedInstanceState.getString("tmpImageName");
		}
	}

}
