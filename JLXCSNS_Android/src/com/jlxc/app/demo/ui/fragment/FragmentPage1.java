package com.jlxc.app.demo.ui.fragment;

import com.jlxc.app.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

	@SuppressLint("InflateParams") 
	public class FragmentPage1 extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
		return inflater.inflate(R.layout.fragment_1, null);		
	}	
	

}
 