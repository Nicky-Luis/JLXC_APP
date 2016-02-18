package com.jlxc.app.login.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.ActivityManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.ui.activity.BaseActivity;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@SuppressLint("ResourceAsColor") 
public class LoginActivity extends BaseActivity {

	//用户名输入框
	@ViewInject(R.id.usernameEt)
	private EditText usernameEt;
	//登录注册按钮
	@ViewInject(R.id.loginRegisterBtn)
	private Button loginRegisterBtn;
	//布局文件
	@ViewInject(R.id.login_activity)
	private RelativeLayout loginLayout;
	
	@OnClick(value={R.id.loginRegisterBtn,R.id.login_activity})
	public void loginOrRegisterClick(View v) {
		
		switch (v.getId()) {
		//登录或者注册判断
		case R.id.loginRegisterBtn:
			loginOrRegister();
			break;
		case R.id.login_activity:
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);  
		        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
				LogUtils.e("登录或注册页面出错。");
			}
			
	        break;
		default:
			break;
		}
	}
	
	/**
	 * 登录或者注册跳转
	 */
	public void loginOrRegister() {
		final String username = usernameEt.getText().toString().trim();
		if (!username.matches(JLXCConst.PHONENUMBER_PATTERN)) {
			Toast.makeText(LoginActivity.this, "真笨，手机号码都写错了 （︶︿︶）",
					Toast.LENGTH_SHORT).show();
			return; 
		}
		//网络请求
		showLoading("正在验证，请稍后", true);
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
				
		HttpManager.post(JLXCConst.IS_USER, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				// TODO Auto-generated method stub
				super.onSuccess(jsonResponse, flag);
				
				int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
				switch (status) {
				case JLXCConst.STATUS_SUCCESS:
					hideLoading();
					
					JSONObject result = jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT);
					//登录
			        int loginDirection    = 1;
			        //注册
			        int registerDirection = 2;
			        int direction = result.getIntValue("direction");
		            if (direction == loginDirection) {
		            	//跳转
		            	Intent intent = new Intent(LoginActivity.this, SecondLoginActivity.class);
		            	intent.putExtra("username", username);
		            	startActivityWithRight(intent);
		            }
		            
		            if (direction == registerDirection) {
		            	//发送验证码
		            	sendVerify(username);
		            }
					
					break;
				case JLXCConst.STATUS_FAIL:
					hideLoading();
					Toast.makeText(LoginActivity.this, jsonResponse.getString(JLXCConst.HTTP_MESSAGE),
							Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, flag);
				hideLoading();
				Toast.makeText(LoginActivity.this, "网络异常",
						Toast.LENGTH_SHORT).show();
			}
			
		}, null)); 
	}
	
	//发送验证码
	public void sendVerify(final String username) {
		//发送验证码
//		try {
//			SMSSDK.getVerificationCode("86",usernameEt.getText().toString().trim());			
//		} catch (Exception e) {
//		}
		
		Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
    	intent.putExtra("username", usernameEt.getText().toString().trim());
    	startActivityWithRight(intent);	
		
//		//网络请求
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("phone_num", username);
//		HttpManager.post(JLXCConst.GET_MOBILE_VERIFY, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
//			
//			@Override
//			public void onSuccess(JSONObject jsonResponse, String flag) {
//				// TODO Auto-generated method stub
//				super.onSuccess(jsonResponse, flag);
//				int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
//				switch (status) {
//				case JLXCConst.STATUS_SUCCESS:
//					hideLoading();
//					//跳转
//	            	Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//	            	intent.putExtra("username", username);
//	            	startActivityWithRight(intent);	
//					
//					break;
//				case JLXCConst.STATUS_FAIL:
//					hideLoading();
//					Toast.makeText(LoginActivity.this, jsonResponse.getString(JLXCConst.HTTP_MESSAGE),
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//			@Override
//			public void onFailure(HttpException arg0, String arg1, String flag) {
//				// TODO Auto-generated method stub
//				super.onFailure(arg0, arg1, flag);
//				hideLoading();
//				Toast.makeText(LoginActivity.this, "网络异常",
//						Toast.LENGTH_SHORT).show();
//			}
//			
//		}, null)); 
	}
	
	/**
	 * 重写返回操作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			ActivityManager.getInstence().exitApplication();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	protected void loadLayout(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setUpView() {
		
		////////////////////////测试数据//////////////////////////
//		usernameEt.setText("13736661234");
//		UserModel userModel = UserManager.getInstance().getUser();
//		if (null != userModel.getUsername() && null != userModel.getLogin_token()) {
//			//跳转主页 自动登录
//			Intent intent = new Intent(this, MainTabActivity.class);
//			startActivity(intent);		
//		}
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		try {
//			EventHandler eh=new EventHandler(){
//				@Override
//				public void afterEvent(int event, int result, Object data) {
//					Message msg = new Message();
//					msg.arg1 = event;
//					msg.arg2 = result;
//					msg.obj = data;
//					handler.sendMessage(msg);
//				}
//			};
//			SMSSDK.registerEventHandler(eh);
//		} catch (Exception e) {
//			System.out.println("没初始化SMSSDK 因为这个短信sdk对DEBUG有影响 所以不是RELEASE不初始化");
//		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		try {
//			SMSSDK.unregisterAllEventHandler();	
//		} catch (Exception e) {
//			System.out.println("没初始化SMSSDK 因为这个短信sdk对DEBUG有影响 所以不是RELEASE不初始化");
//		}
	}
	
//	@SuppressLint("HandlerLeak") 
//	Handler handler=new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			hideLoading();
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			int event = msg.arg1;
//			int result = msg.arg2;
//			Object data = msg.obj;
//			Log.e("event", "event="+event);
//			if (result == SMSSDK.RESULT_COMPLETE) {
//				//短信注册成功后，返回MainActivity,然后提示新好友
//				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
////					Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
//				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
//	            	Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//	            	intent.putExtra("username", usernameEt.getText().toString().trim());
//	            	startActivityWithRight(intent);	
//	            	
//				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
////					Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//					
//				}
//			} else {
//				((Throwable) data).printStackTrace();
//				ToastUtil.show(LoginActivity.this, "网络有点小问题");
//			}
//			
//		}
//		
//	};

}
