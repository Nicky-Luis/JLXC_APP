package com.jlxc.app.personal.ui.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.utils.FileUtil;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.ToastUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//我的名片页面
public class MyCardActivity extends BaseActivityWithTopBar {

	//头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	//姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	//hellohaId
	@ViewInject(R.id.helloha_text_view)
	private TextView hellohaTextView;
	//hellohaEt
	@ViewInject(R.id.helloha_edit_text)
	private EditText hellohaEditText;
	//hellohaBtn
	@ViewInject(R.id.helloha_button)
	private Button saveButton;
	//设置按钮
	@ViewInject(R.id.set_button)
	private Button setButton;
	//二维码
	@ViewInject(R.id.qrcode_image_view)
	private ImageView qrcodeImageView;	
	//hellohaLayout
	@ViewInject(R.id.helloha_layout)
	private LinearLayout hellohalLayout;
	
	private DisplayImageOptions headImageOptions;  
    
	
//	//bitmapUtils
//	BitmapUtils bitmapUtil;
	
	//用户模型
	private UserModel userModel;
	
	@OnClick({R.id.set_button, R.id.helloha_button})
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.set_button:
			//设置按钮
			setButton.setVisibility(View.GONE);
			hellohalLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.helloha_button:
			//保存按钮
			saveHelloHaId();
			break;
		case R.id.my_card_layout:
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			break;
		default:
			break;
		}
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_card;
	}
	@Override
	protected void setUpView() {
		
		setBarText("我的名片");
		
		userModel = UserManager.getInstance().getUser();
		if (null != userModel.getHelloha_id() && userModel.getHelloha_id().length() > 0) {
			hellohaTextView.setVisibility(View.VISIBLE);
			hellohaTextView.setText("HelloHa号"+userModel.getHelloha_id());
		}else {
			setButton.setVisibility(View.VISIBLE);
		}
		
		headImageOptions = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.default_avatar)  
        .showImageOnFail(R.drawable.default_avatar)  
        .cacheInMemory(false)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		nameTextView.setText(userModel.getName());
//		bitmapUtil.display(headImageView, JLXCConst.ATTACHMENT_ADDR+userModel.getHead_image());
		ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR+userModel.getHead_image(), headImageView, headImageOptions);
		
		getQRCode();
	}
	
	//////////////////////////private method////////////////////////
	private void getQRCode() {
		String path = JLXCConst.GET_USER_QRCODE+"?"+"uid="+userModel.getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							String qrpath = jsonResponse.getString(JLXCConst.HTTP_RESULT); 
							ImageLoader.getInstance().displayImage(JLXCConst.ROOT_PATH+qrpath, qrcodeImageView, headImageOptions);
//							bitmapUtil.display(qrcodeImageView, JLXCConst.ROOT_PATH+qrpath);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(MyCardActivity.this, jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));
	}
	
	//保存HelloHaId
	private void saveHelloHaId() {
		
		//必须按格式
		if (hellohaEditText.getText().toString().matches(JLXCConst.USER_ACCOUNT_PATTERN) == false) {
			ToastUtil.show(this, "账号只能由6-20位字母数字下划线组成╮(╯_╰)╭");
			return;
		}
		
		final CustomAlertDialog confirmDialog = new CustomAlertDialog(
				this, "账号设置后不能更改，你愿意和它相伴一生吗", "确定", "取消");
		confirmDialog.show();
		confirmDialog.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						setHelloHaId();
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});				
		
//		//提示
//		new AlertDialog.Builder(this).setPositiveButton("确定", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				setHelloHaId();
//			}
//		}).setNegativeButton("取消", null).setTitle("注意").setMessage("账号设置后不能更改，你愿意和它相伴一生吗").show();
	}
	
	//设置HelloHaId
	private void setHelloHaId(){
		hellohaEditText.setEnabled(false);
		//网络请求
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid()+"");
		params.addBodyParameter("helloha_id", hellohaEditText.getText().toString().trim());
		showLoading("账号设置中", false);
		//上传
		HttpManager.post(JLXCConst.SET_HELLOHAID, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
				if (status == JLXCConst.STATUS_SUCCESS) {
					//设置成功
					userModel.setHelloha_id(hellohaEditText.getText().toString().trim());
					//本地缓存
					UserManager.getInstance().saveAndUpdate();
					//布局改变
					hellohalLayout.setVisibility(View.GONE);
					hellohaTextView.setVisibility(View.VISIBLE);
					hellohaTextView.setText("HelloHa号："+userModel.getHelloha_id());
				}else {
					if (jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT).getIntValue("flag") == 1) {
						//有号
						String helloHaId = jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT).getString("helloha_id");
						if (null != helloHaId && helloHaId.length()>0) {
							userModel.setHelloha_id(helloHaId);
							//本地缓存
							UserManager.getInstance().saveAndUpdate();
							//布局改变
							hellohalLayout.setVisibility(View.GONE);
							hellohaTextView.setVisibility(View.VISIBLE);
							hellohaTextView.setText("HelloHa账号："+userModel.getHelloha_id());
						}
					}
				}
				
				hideLoading();
				ToastUtil.show(MyCardActivity.this, jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
				hellohaEditText.setEnabled(true);
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, flag);
				hideLoading();
				ToastUtil.show(MyCardActivity.this, "网络异常");
				hellohaEditText.setEnabled(true);
			}
			
		}, null)); 
	}

}
