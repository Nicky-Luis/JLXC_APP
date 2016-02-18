package com.jlxc.app.demo.ui.activity;

import java.util.Arrays;
import java.util.LinkedList;

import com.jlxc.app.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jlxc.app.base.manager.ActivityManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NextActivity extends BaseActivityWithTopBar {
	private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler" };
	
	private LinkedList<String> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private ArrayAdapter<String> mAdapter;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				mListItems.addFirst("haha new line");
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				
				// Do work to refresh the list here.
//				HttpUtils httpUtils = new HttpUtils();
//				httpUtils.send(HttpMethod.GET, "http://www.lidroid.com", new RequestCallBack<String>() {
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
//						// TODO Auto-generated method stub
//						Log.i("--", arg1+"faile");
//						mPullRefreshListView.onRefreshComplete();
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> arg0) {
//						// TODO Auto-generated method stub
//						Log.i("--", arg0.result);
//						 
//						mListItems.addFirst("haha new line");
//						
//						mAdapter.notifyDataSetChanged();
//						// Call onRefreshComplete when the list has been refreshed.
//						mPullRefreshListView.onRefreshComplete();
//					}
//				});
			}
		});

		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mStrings));
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mListItems);

		// mPullRefreshListView.setAdapter(mAdapter);
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
	}
	
	public void back(View view) {
		Log.i("--", "back");
		finishWithRight();
	}
	
	public void finishWithRight() {
		ActivityManager.getInstence().popActivity(this);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	
	
	private class GetDataTask extends AsyncTask<Void, Void, String> {
		// ��̨���??��
		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
				
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.send(HttpMethod.GET, "", new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				
			} catch (InterruptedException e) {
			}
			String str="Added after refresh...I add";
			return str;
		}

		//�����Ƕ�ˢ�µ���Ӧ����������addFirst������addLast()�����¼ӵ����ݼӵ�LISTView��
		//���?AsyncTask��ԭ�??onPostExecute���?result��ֵ����doInBackground()�ķ���ֵ
		@Override
		protected void onPostExecute(String result) {
			//��ͷ��������������
			mListItems.addFirst(result);
			
			//֪ͨ������ݼ��Ѿ��ı�?�����ͨ�?����ô������ˢ��mListItems�ļ���
			mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);//����Ǳ��еģ�?AsyncTask�涨�ĸ�ʽ
		}
	}



	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		
		return R.layout.activity_next;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		
	}	

}
