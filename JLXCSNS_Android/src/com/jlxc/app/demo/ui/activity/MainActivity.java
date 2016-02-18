package com.jlxc.app.demo.ui.activity;

import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.R.string;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.DBManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.ui.activity.BaseActivity;
import com.jlxc.app.demo.ui.fragment.FragmentPage1;
import com.jlxc.app.demo.ui.fragment.FragmentPage2;
import com.jlxc.app.login.ui.activity.RegisterActivity;
import com.jlxc.app.login.ui.activity.LoginActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MainActivity extends BaseActivity {

	// FragmentTabHost对象
	@ViewInject(android.R.id.tabhost)
	private FragmentTabHost mTabHost;
	private Button btnTest;

	private LayoutInflater layoutInflater;

	private Class<?> fragmentArray[] = { FragmentPage1.class,
			FragmentPage2.class };

	private int mImageViewArray[] = { R.drawable.tab_home_btn,
			R.drawable.tab_message_btn };

	private String mTextviewArray[] = { "主页", "哈哈" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ImageView menuImg = (ImageView)
		// findViewById(R.id.title_bar_menu_btn);
		// menuImg.setOnClickListener(this);
		initTab();
		// HttpManager.get("http://192.168.1.101/jlxc_php/index.php/Home/MobileApi/recommendFriendsList?user_id=19",
		// new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
		// @Override
		// public void onSuccess(JSONObject jsonResponse, String flag) {
		// // TODO Auto-generated method stub
		// super.onSuccess(jsonResponse, flag);
		//
		// Log.i("--", "haha"+jsonResponse.toJSONString());
		//
		// }
		// @Override
		// public void onFailure(HttpException arg0, String arg1, String flag) {
		// // TODO Auto-generated method stub
		// super.onFailure(arg0, arg1, flag);
		//
		// Log.i("--", "fail");
		// }
		//
		// }, null));
//		btnTest = (Button) findViewById(R.id.);
//		btnTest.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, RegisterActivity.class);
//				startActivity(intent);
//			}
//		});
	}

	public void initTab() {

		layoutInflater = LayoutInflater.from(this);

		// mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
//			mTabHost.getTabWidget().getChildAt(i)
//					.setBackgroundResource(R.drawable.selector_tab_background);
		}
	}

	/**
	 */
	@SuppressLint("InflateParams")
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		// int i = mTabHost.getCurrentTab();
		return view;
	}

	public void next(View view) {
		Log.i("--", "fff");
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityWithRight(intent);
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void loadLayout(View v) {

	}

	@Override
	protected void setUpView() {

	}
}
