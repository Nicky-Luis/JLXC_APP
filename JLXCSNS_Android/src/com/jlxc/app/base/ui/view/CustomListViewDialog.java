package com.jlxc.app.base.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;

public class CustomListViewDialog extends Dialog {

	private Context mContext;
	// layout
	private ListView itemListView;
	// item 列表
	private List<String> listContent = new ArrayList<String>();
	// 回调监听事件
	private ClickCallBack clickCallBack;
	// 适配器
	private HelloHaAdapter<String> itemAdapter;

	public CustomListViewDialog(Context context, List<String> list) {
		super(context, R.style.item_dialog);
		this.listContent = list;
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View rootView = inflater.inflate(R.layout.dialog_list_item, null);
		itemListView = (ListView) rootView
				.findViewById(R.id.listView_dialog_item);
		dialogItemSet();
		DisplayMetrics displayMet = this.getContext().getResources()
				.getDisplayMetrics();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				displayMet.widthPixels * 2 / 3,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		this.setContentView(rootView, params);
		super.onCreate(savedInstanceState);
	}

	private void dialogItemSet() {
		itemAdapter = new HelloHaAdapter<String>(mContext,
				R.layout.item_dialog_layout, listContent) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, String item) {
				helper.setText(R.id.tv_item_dialog, item);
			}
		};
		itemListView.setAdapter(itemAdapter);
		itemListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				clickCallBack.Onclick(view,position);
				
			}
		});
	}

	// 设置回调
	public void setClickCallBack(ClickCallBack clickBack) {
		this.clickCallBack = clickBack;
	}

	/**
	 * 点赞回调接口
	 * */
	public interface ClickCallBack {
		public void Onclick(View view,int which);
	}
}
