package com.jlxc.app.base.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.lidroid.xutils.ViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.jlxc.app.base.manager.ActivityManager;
import com.jlxc.app.base.ui.view.CustomProgressDialog;
import com.jlxc.app.R;

/**
 * 
 * 
 */
public abstract class BaseActivity extends FragmentActivity {
	// private ProgressDialog progressDialog;
	private Dialog progressDialog;

	/**
	 */
	public boolean hasActiveNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isConnected();
		}

		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ActivityManager.getInstence().pushActivity(this);
		int id = setLayoutId();
		View v = null;
		if (0 == id) {
			new Exception(
					"Please return the layout id in setLayoutId method,as simple as R.layout.cr_news_fragment_layout")
					.printStackTrace();
		} else {
			// layout注入
			v = LayoutInflater.from(this).inflate(setLayoutId(), null);
			setContentView(v);
			ViewUtils.inject(this);
			loadLayout(v);
		}

		setUpView();
	}

	/**
	 * 重写方法设置layoutID
	 * 
	 * @return
	 */
	public abstract int setLayoutId();

	protected abstract void loadLayout(View v);

	protected abstract void setUpView();

	/**
	 * 右侧进入动画
	 * 
	 * @param intent
	 */
	public void startActivityWithRight(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * 右侧退出动画
	 */
	public void finishWithRight() {
		ActivityManager.getInstence().popActivity(this);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 底部进入
	 * 
	 * @param intent
	 */
	public void startActivityWithBottom(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
	}

	/**
	 * 底部退出
	 */
	public void finishWithBottom() {
		ActivityManager.getInstence().popActivity(this);
		finish();
		overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
	}

	/**
	 * 显示加载动画
	 */
	public void showLoading(String message, boolean cancleable) {
		// if (progressDialog == null) {
		// progressDialog = new ProgressDialog(this);
		// progressDialog.setCancelable(cancleable);
		// progressDialog.setMessage(message + "");
		// }
		// if (!progressDialog.isShowing()) {
		// progressDialog.show();
		// }
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createLoadingDialog(
					BaseActivity.this, message, cancleable);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	/**
	 * 隐藏加载动画
	 */
	public void hideLoading() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	/**
	 * 显示alert
	 */
	public void showConfirmAlert(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton("确认", null).show();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		hideLoading();
		MobclickAgent.onPause(this);
	}

	/**
	 * key点击
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finishWithRight();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	public int[] getScreenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return new int[] { dm.widthPixels, dm.heightPixels };
	}
}
