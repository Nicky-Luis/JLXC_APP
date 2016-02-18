package com.jlxc.app.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.news.ui.activity.PublishNewsActivity;
import com.jlxc.app.R;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainPageFragment extends BaseFragment {

	// 上下文信息
	private Context mContext;
	// 主页viewpager
	@ViewInject(R.id.viewpager_main)
	private ViewPager mainPager;
	// title的项目
	@ViewInject(R.id.layout_title_content)
	private LinearLayout titleContent;
	// 主页viewpager
	@ViewInject(R.id.tv_news_guid)
	private TextView newsTitleTextView;
	// 校园
	@ViewInject(R.id.tv_campus_guid)
	private TextView campusTitleTextView;
	// 所有的
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	// 偏移图片
	@ViewInject(R.id.img_cursor)
	private ImageView imageCursor;
	// 通知按钮
	@ViewInject(R.id.img_main_publish_btn)
	private ImageView publishBtn;
	// 当前页卡编号
	private int currIndex;
	// 横线图片宽度
	private int cursorWidth;
	// 图片移动的偏移量
	private int offset;

	@Override
	public int setLayoutId() {
		return R.layout.fragment_main_page;
	}

	@Override
	public void loadLayout(View rootView) {
	}

	@Override
	public void setUpViews(View rootView) {
		mContext = this.getActivity().getApplicationContext();
		InitImage();
		InitViewPager();
		publishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intentUsrMain = new Intent(mContext,
						PublishNewsActivity.class);
				startActivityWithRight(intentUsrMain);
			}
		});
	}

	/*
	 * 初始化图片的位移像素
	 */
	public void InitImage() {
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int contentLeftMargin = ((FrameLayout.LayoutParams) titleContent
				.getLayoutParams()).leftMargin;
		int contentWidth = screenWidth - (2 * contentLeftMargin);

		// 设置游标的尺寸与位置
		LayoutParams cursorParams = (LayoutParams) imageCursor
				.getLayoutParams();
		cursorWidth = cursorParams.width;
		offset = contentWidth / 2;
		cursorParams.leftMargin = (contentWidth / 2 - cursorWidth) / 2
				+ contentLeftMargin;
		imageCursor.setLayoutParams(cursorParams);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {

		newsTitleTextView.setOnClickListener(new ViewClickListener(0));
		campusTitleTextView.setOnClickListener(new ViewClickListener(1));
		mFragmentList.add(new MainNewsListFragment());
		//mFragmentList.add(new CampusHomeFragment());

		mainPager.setAdapter(new MainFragmentPagerAdapter(
				getChildFragmentManager(), mFragmentList));
		mainPager.setCurrentItem(0);
		mainPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 头标点击监听
	 */
	private class ViewClickListener implements OnClickListener {
		private int index = 0;

		public ViewClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mainPager.setCurrentItem(index);
		}
	}

	/**
	 * ViewPager的适配器
	 * */
	class MainFragmentPagerAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> fragmentList;

		public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			fragmentList = list;
		}

		// 得到每个item
		@Override
		public Fragment getItem(int index) {
			return fragmentList.get(index);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			//
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int arg1) {
			// 初始化每个页卡选项
			return super.instantiateItem(arg0, arg1);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//
			super.destroyItem(container, position, object);
		}
	}

	/**
	 * 监听选项卡改变事件
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		float lastpostion = 0;

		public void onPageScrollStateChanged(int index) {
			currIndex = index;
		}

		// CurrentTab:当前页面序号
		// OffsetPercent:当前页面偏移的百分比
		// offsetPixel:当前页面偏移的像素位置
		public void onPageScrolled(int CurrentTab, float OffsetPercent,
				int offsetPixel) {
			// 下标的移动动画
			Animation animation = new TranslateAnimation(offset * lastpostion,
					offset * (CurrentTab + OffsetPercent), 0, 0);

			lastpostion = OffsetPercent + CurrentTab;
			// True:图片停在动画结束位置
			animation.setFillAfter(true);
			animation.setDuration(300);
			imageCursor.startAnimation(animation);
		}

		/**
		 * 状态改变后
		 * */
		public void onPageSelected(int index) {
			if (0 == index) {
				newsTitleTextView.setTextColor(getResources().getColor(
						R.color.main_brown));
				campusTitleTextView.setTextColor(getResources().getColor(
						R.color.main_clear_brown));
			} else {
				campusTitleTextView.setTextColor(getResources().getColor(
						R.color.main_brown));
				newsTitleTextView.setTextColor(getResources().getColor(
						R.color.main_clear_brown));
			}
		}
	}

}
