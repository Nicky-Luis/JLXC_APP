package com.jlxc.app.personal.ui.activity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jlxc.app.R;
import com.jlxc.app.base.helper.PinyinHelper;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.personal.ui.view.FriendLetterListView;
import com.jlxc.app.personal.ui.view.FriendLetterListView.OnTouchingLetterChangedListener;

import android.R.bool;
import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactFriendListActivity extends BaseActivityWithTopBar
{

	private BaseAdapter adapter;

	private ListView listview;

//	private TextView overlay;
//
//	private ImageView suspend_search;

	private FriendLetterListView letterListView;

	private AsyncQueryHandler asyncQuery;

	private static final String NAME = "name", NUMBER = "number",
			SORT_KEY = "sort_key";

	private HashMap<String, Integer> alphaIndexer;

//	private String[] sections;

	public List<ContentValues> list = new ArrayList<ContentValues>();

//	private WindowManager windowManager;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_friend_list_contact;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
//			windowManager =
//					(WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
			asyncQuery = new MyAsyncQueryHandler(getContentResolver());
			listview = (ListView) findViewById(R.id.list_view);
			letterListView = (FriendLetterListView) findViewById(R.id.my_list_view);
			letterListView
					.setOnTouchingLetterChangedListener(new LetterListViewListener());

			alphaIndexer = new HashMap<String, Integer>();
			new Handler();
//			new OverlayThread();
			if (list.size() > 0)
			{
			}

			listview.setOnScrollListener(new OnScrollListener()
			{

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState)
				{
//					suspend_search.setVisibility(View.VISIBLE);
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount)
				{
//					suspend_search.setVisibility(View.GONE);
				}
			});
//			suspend_search.setOnClickListener(new OnClickListener()
//			{
//
//				@Override
//				public void onClick(View v)
//				{
//					ComponentName friendcName =
//							new ComponentName(FriendListActivity.this,
//									"com.example.test_intent.FriendSearch");
//					Intent friend_viewIntent = new Intent();
//					friend_viewIntent.setComponent(friendcName);
//					startActivity(friend_viewIntent);
//					Toast.makeText(getApplicationContext(), "sousuo",
//							Toast.LENGTH_LONG).show();
//					// TODO Auto-generated method stub
//				}
//			});
	}
	
	@SuppressWarnings("deprecation")
	public void getContent()
	{
		Cursor cur =
				getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
		startManagingCursor(cur);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	@SuppressLint("HandlerLeak") 
	private class MyAsyncQueryHandler extends AsyncQueryHandler
	{


		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor)
		{

			cursor.moveToFirst();
			LogUtils.i(cursor.getString(0) + " 000 " + cursor.getString(1)
							+ " 000 " + cursor.getString(2) + " 000 "
							+ cursor.getString(3), 1);

			while (cursor.moveToNext())
			{
				ContentValues cv = new ContentValues();
				cv.put(NAME, cursor.getString(1));
				cv.put(NUMBER, cursor.getString(2));
				cv.put(SORT_KEY, cursor.getString(3));
				list.add(cv);
			}
			if (list.size() > 0)
			{
				setAdapter(list);
			}
		}

	}

	private void setAdapter(List<ContentValues> list)
	{
		adapter = new ListAdapter(this, list);
		listview.setAdapter(adapter);

	}

	private class ListAdapter extends BaseAdapter
	{

		private class PrivateContactModel {
			
			private ContentValues values;
			private boolean isFirst;
			//首字母
			private String alpha;
			public ContentValues getValues() {
				return values;
			}
			public void setValues(ContentValues values) {
				this.values = values;
			}
			public boolean getIsFirst() {
				return isFirst;
			}
			public void setIsFirst(boolean isFirst) {
				this.isFirst = isFirst;
			}
			public String getAlpha() {
				return alpha;
			}
			public void setAlpha(String alpha) {
				this.alpha = alpha;
			}
		}
		
		private LayoutInflater inflater;

		private List<ContentValues> list;
		private List<PrivateContactModel> mapList = new ArrayList<ContactFriendListActivity.ListAdapter.PrivateContactModel>();
		
		public ListAdapter(Context context, List<ContentValues> list)
		{
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
//			sections = new String[list.size()];
			
			Map<String, List<ContentValues>> map = new HashMap<String, List<ContentValues>>();
			List<String> keyList = new ArrayList<String>();
			
			for (int i = 0; i < list.size(); i++)
			{
				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));
				if (map.containsKey(currentStr)) {
					List<ContentValues> tmpKeyList = map.get(currentStr);
					tmpKeyList.add(list.get(i));
				}else {
					List<ContentValues> tmpKeyList = new ArrayList<ContentValues>();
					tmpKeyList.add(list.get(i));
					map.put(currentStr, tmpKeyList);
					//添加key
					keyList.add(currentStr);
				}
//				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));
//				String previewStr =
//						(i - 1) >= 0 ? getAlpha(list.get(i - 1).getAsString(
//								SORT_KEY)) : " ";
//				if (!previewStr.equals(currentStr))
//				{
//					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
//					alphaIndexer.put(name, i);
//					sections[i] = name;
//				}
			}
			
			Collections.sort(keyList);
			
			//重新排序
			for (int i = 0; i < keyList.size(); i++) {
				String key = keyList.get(i);
				List<ContentValues> tmpKeyList = map.get(key);
				for (int j = 0; j < tmpKeyList.size(); j++) {
					PrivateContactModel contactModel = new PrivateContactModel();
					contactModel.setValues(tmpKeyList.get(j));
					contactModel.setAlpha(key);
					if (j == 0) {
						contactModel.setIsFirst(true);
						alphaIndexer.put(key, mapList.size());
					}
					mapList.add(contactModel);
				}
			}
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;

			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.contact_friend_list_item_adapter, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number =
						(TextView) convertView.findViewById(R.id.number);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			PrivateContactModel contactModel = mapList.get(position);
			ContentValues cv = contactModel.getValues();
			holder.name.setText(cv.getAsString(NAME));
			holder.number.setText(cv.getAsString(NUMBER));
			if (contactModel.isFirst == true) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(contactModel.getAlpha());
			} else {
				holder.alpha.setVisibility(View.GONE);
			}

//			ContentValues cv = list.get(position);
//			holder.name.setText(cv.getAsString(NAME));
//			holder.number.setText(cv.getAsString(NUMBER));
//			String currentStr =
//					getAlpha(list.get(position).getAsString(SORT_KEY));
//			String previewStr =
//					(position - 1) >= 0 ? getAlpha(list.get(position - 1)
//							.getAsString(SORT_KEY)) : "";
//
//			if (!previewStr.equals(currentStr))
//			{
//				holder.alpha.setVisibility(View.VISIBLE);
//				holder.alpha.setText(currentStr);
//			}
//			else
//			{
//				holder.alpha.setVisibility(View.GONE);
//			}

			return convertView;
		}

		private class ViewHolder
		{

			TextView alpha;

			TextView name;

			TextView number;
		}
	}

//	private void initSuSearch()// 搜索
//	{
//		LayoutInflater inflater = LayoutInflater.from(this);
//		suspend_search =
//				(ImageView) inflater.inflate(R.layout.suspend_search, null);
//		WindowManager.LayoutParams lp =
//				new WindowManager.LayoutParams(80, 80, 170, -280,
//						WindowManager.LayoutParams.TYPE_APPLICATION,
//						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//						PixelFormat.TRANSLUCENT);
//		windowManager.addView(suspend_search, lp);
//	}
//
//	private void initOverlay()
//	{
//		LayoutInflater inflater = LayoutInflater.from(this);
//		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
//		WindowManager.LayoutParams lp =
//				new WindowManager.LayoutParams(
//						120,
//						120,
//						100,
//						0,
//						WindowManager.LayoutParams.TYPE_APPLICATION,
//						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//								| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//						PixelFormat.TRANSLUCENT);
//		// WindowManager windowManager = (WindowManager)
//		// this.getSystemService(Context.WINDOW_SERVICE);
//		windowManager.addView(overlay, lp);
//	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener
	{

		@Override
		public void onTouchingLetterChanged(final String s, float y, float x)
		{
			if (alphaIndexer.get(s) != null)
			{
				int position = alphaIndexer.get(s);

				listview.setSelection(position);
//				overlay.setText(sections[position]);
//				overlay.setVisibility(View.VISIBLE);

			}
		}

		@Override
		public void onTouchingLetterEnd()
		{
//			overlay.setVisibility(View.GONE);
		}
	}

//	private class OverlayThread implements Runnable
//	{
//
//		@Override
//		public void run()
//		{
////			overlay.setVisibility(View.GONE);
//		}
//	}

	private String getAlpha(String str) {
		
		if (str == null)
		{
			return "#";
		}
		if (str.trim().length() == 0)
		{
			return "#";
		}
		String nicknamePinyin = PinyinHelper.getInstance(this).getPinyins(str,"");
		char searchKey;
		if (nicknamePinyin != null && nicknamePinyin.length() > 0) {
			char key = nicknamePinyin.charAt(0);
			if (key >= 'a' && key <= 'z') {
				key -= 32;
			} else {
				key = '#';
			}
			searchKey = key;
		} else {
			searchKey = '#';
		}
       
		return searchKey+"";
	}

	@Override
	protected void onDestroy()
	{
//		if (windowManager != null)// 防止内存泄露
//		{
//			windowManager.removeView(overlay);
//			windowManager.removeView(suspend_search);
//		}
		super.onDestroy();
	}

	

}
