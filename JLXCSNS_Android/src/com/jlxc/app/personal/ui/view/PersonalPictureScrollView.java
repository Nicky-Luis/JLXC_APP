package com.jlxc.app.personal.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jlxc.app.R;
import com.jlxc.app.base.utils.JLXCConst;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalPictureScrollView extends LinearLayout {

	//图片缓存工具
	private DisplayImageOptions imageOptions;
	//数组
	private List<ImageView> imageList;
	//点击图片监听器
	private ScrollImageBrowseListener browseListener;
	
	public PersonalPictureScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public PersonalPictureScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//加载内容
		View view = View.inflate(context, R.layout.personal_picture_layout, this);
        //显示头像的配置  
		imageOptions = new DisplayImageOptions.Builder()  
                .showImageOnLoading(R.drawable.default_avatar)  
                .showImageOnFail(R.drawable.default_avatar)  
                .cacheInMemory(false)  
                .cacheOnDisk(true)  
                .bitmapConfig(Bitmap.Config.RGB_565)  
                .build();
		widgetInject(view);
	}
	//控件注入
	@SuppressLint("UseValueOf") 
	private void widgetInject(View view) {
		ImageView imageView1 = (ImageView) view.findViewById(R.id.personal_picture_image_view1);
		ImageView imageView2 = (ImageView) view.findViewById(R.id.personal_picture_image_view2);
		ImageView imageView3 = (ImageView) view.findViewById(R.id.personal_picture_image_view3);
		ImageView imageView4 = (ImageView) view.findViewById(R.id.personal_picture_image_view4);
		ImageView imageView5 = (ImageView) view.findViewById(R.id.personal_picture_image_view5);
		ImageView imageView6 = (ImageView) view.findViewById(R.id.personal_picture_image_view6);
		ImageView imageView7 = (ImageView) view.findViewById(R.id.personal_picture_image_view7);
		ImageView imageView8 = (ImageView) view.findViewById(R.id.personal_picture_image_view8);
		ImageView imageView9 = (ImageView) view.findViewById(R.id.personal_picture_image_view9);
		ImageView imageView10 = (ImageView) view.findViewById(R.id.personal_picture_image_view10);
		//数组初始化
		imageList = new ArrayList<ImageView>();
		imageList.add(imageView1);
		imageList.add(imageView2);
		imageList.add(imageView3);
		imageList.add(imageView4);
		imageList.add(imageView5);
		imageList.add(imageView6);
		imageList.add(imageView7);
		imageList.add(imageView8);
		imageList.add(imageView9);
		imageList.add(imageView10);
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != browseListener) {
					browseListener.clickImage((Integer) v.getTag());
				}
			}
		};
		
		for (int i = 0; i < imageList.size(); i++) {
			ImageView imageView =  imageList.get(i);
			//设置位置
			imageView.setTag(i);
			//设置监听
			imageView.setOnClickListener(listener);
		}
	}
	
	//设置内容
	public void setNewsImageList(List<String> list) {
		//全部隐藏
		for (ImageView imageView : imageList) {
			imageView.setVisibility(View.GONE);
		}
		int size = 0;
		//最大10
		if (list.size() > 10) {
			size = 10;
		}else {
			size = list.size();
		};
		
		for (int i = 0; i < size; i++) {
			ImageView imageView = imageList.get(i);
			String path = list.get(i);
			//设置图片
			if (null != path && path.length() >0) {
				ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + path, imageView, imageOptions);
			}else {
				imageView.setImageResource(R.drawable.default_avatar);
			}
			imageView.setVisibility(View.VISIBLE);
		}
	}

	public ScrollImageBrowseListener getBrowseListener() {
		return browseListener;
	}

	public void setBrowseListener(ScrollImageBrowseListener browseListener) {
		this.browseListener = browseListener;
	}

	/**
	 * 图片点击监听接口类 
	 */
	public interface ScrollImageBrowseListener{
		public void clickImage(int positon);
	}

}
