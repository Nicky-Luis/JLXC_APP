package com.jlxc.app.message.ui.fragment;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.jlxc.app.R;
import com.jlxc.app.base.model.NewsPushModel;
import com.jlxc.app.base.ui.activity.MainTabActivity;
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.news.receiver.NewMessageReceiver;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class MessageMainFragment extends BaseFragment {

	@ViewInject(R.id.vPager)
	private ViewPager mPager;//页卡内容
	// title的layout
	@ViewInject(R.id.layout_title_content)
	private LinearLayout titleContent;
	//会话tv
	@ViewInject(R.id.conversation_text_view)
	private TextView conversationTextView;
	//通知tv
	@ViewInject(R.id.notify_text_view)
    private TextView notifyTextView;	
	//会话未读tv
	@ViewInject(R.id.conversation_unread_text_view)
	private TextView conversationUnreadTextView;
	//通知未读tv
	@ViewInject(R.id.notify_unread_text_view)
    private TextView notifyUnreadTextView;
	@ViewInject(R.id.img_cursor)
    private ImageView cursor;// 动画图片
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
	// 横线图片宽度
	private int cursorWidth;
	// 屏幕的尺寸
	private int screenWidth = 0, screenHeight = 0;
	
	@OnClick({R.id.conversation_layout,R.id.notify_layout})
    private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.conversation_layout:
			mPager.setCurrentItem(0);
			break;
		case R.id.notify_layout:
			mPager.setCurrentItem(1);				
			break;
		default:
			break;
		}
		
	}
    
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_message_main;
	}

	@Override
	public void loadLayout(View rootView) {
		// TODO Auto-generated method stub 

	}

	@Override
	public void setUpViews(View rootView) {
		// TODO Auto-generated method stub
		initImage();
		initViewPager();
		registerNotify();
		
		notifyTextView.setTextColor(getResources().getColor(
				R.color.main_clear_brown));
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		refreshMessage();
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (newMessageReceiver != null) {
			getActivity().unregisterReceiver(newMessageReceiver);
			newMessageReceiver = null;
		}
	}
	
	/*
	 * 初始化图片的位移像素
	 */
	public void initImage() {
		
		// 获取屏幕尺寸
		DisplayMetrics displayMet = getResources().getDisplayMetrics();
		screenWidth = displayMet.widthPixels;
		screenHeight = displayMet.heightPixels;
		int contentLeftMargin = ((LinearLayout.LayoutParams) titleContent
				.getLayoutParams()).leftMargin;
		int contentWidth = screenWidth - (2 * contentLeftMargin);
//		cursorWidth = 60;
//		int cursorheight = 10;

		offset = contentWidth / 2;
		// 设置游标的尺寸与位置
		LayoutParams cursorParams = (LayoutParams) cursor
				.getLayoutParams();
//		cursorParams.width = cursorWidth;
//		cursorParams.height = cursorheight;
		cursorParams.leftMargin = contentLeftMargin+(contentWidth/2-cursorParams.width)/2;
		cursor.setLayoutParams(cursorParams);
		cursor.setScaleType(ImageView.ScaleType.FIT_XY);
	}
	
	/**
     * 初始化ViewPager
     */
    @SuppressLint("InflateParams") 
    private void initViewPager() {
    	
        mPager.setAdapter(new MessageFragmentPagerAdapter(getChildFragmentManager()));;
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    
    private class MessageFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        public MessageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0:
                        ConversationListFragment listFragment = ConversationListFragment.getInstance();
                        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                                .appendPath("conversationlist")
                                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
                                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//应用公众服务。
                                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                                .build();
                        listFragment.setUri(uri);
                        fragment = listFragment;
                    break;
                case 1:
                	fragment = new NotifyNewsFragment();
                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    
    
    /**
     * 页卡切换监听
*/
    public class MyOnPageChangeListener implements OnPageChangeListener {

		float lastpostion = 0;

		public void onPageScrollStateChanged(int index) {
			currIndex = index;
			//设置已读
			NewsPushModel.setIsRead();
			
			//通知刷新
			Intent tabIntent = new Intent(JLXCConst.BROADCAST_TAB_BADGE);
			getActivity().sendBroadcast(tabIntent);
			//自己也刷新
			refreshMessage();
		}

		// CurrentTab:当前页面序号
		// OffsetPercent:当前页面偏移的百分比
		// offsetPixel:当前页面偏移的像素位置
		public void onPageScrolled(int CurrentTab, float OffsetPercent,
				int offsetPixel) {
			// 下标的移动动画
			Animation animation = new TranslateAnimation(offset * lastpostion, offset
					* (CurrentTab + OffsetPercent), 0, 0);

			lastpostion = OffsetPercent + CurrentTab;
			// True:图片停在动画结束位置
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		public void onPageSelected(int index) {
			if (0 == index) {
				conversationTextView.setTextColor(getResources().getColor(
						R.color.main_brown));
				notifyTextView.setTextColor(getResources().getColor(
						R.color.main_clear_brown));
			} else {
				conversationTextView.setTextColor(getResources().getColor(
						R.color.main_clear_brown));
				notifyTextView.setTextColor(getResources().getColor(
						R.color.main_brown));
			}
		}
    }
    
	private NewMessageReceiver newMessageReceiver;
	//注册通知
	private void registerNotify(){
		//刷新顶部tab
		newMessageReceiver = new NewMessageReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//刷新顶部tab
				refreshMessage();
			}
		};
		IntentFilter intentFilter = new IntentFilter(JLXCConst.BROADCAST_MESSAGE_REFRESH);
//		LocalBroadcastManager mLocalBroadcastManager;
//		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		getActivity().registerReceiver(newMessageReceiver, intentFilter);
	}
	
	//刷新顶部栏
	private void refreshMessage() {
		
	    //新好友请求未读
		int newFriendsCount = 0;
	    //未读推送
		int newsUnreadCount = 0;
		 
		try {
			newFriendsCount = IMModel.unReadNewFriendsCount();
			newsUnreadCount = NewsPushModel.findUnreadCount().size();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		int pushCount = newFriendsCount + newsUnreadCount;
		
		final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.APP_PUBLIC_SERVICE, Conversation.ConversationType.PUBLIC_SERVICE};
		//聊天未读
		int IMUnreadCount = 0;
		if (null != RongIM.getInstance()) {
			if (null != RongIM.getInstance().getRongIMClient()) {
				try {
					IMUnreadCount = RongIM.getInstance().getRongIMClient().getUnreadCount(conversationTypes);					
				} catch (Exception e) {
					LogUtils.i("unread 异常", 1);
				}
			}
		}
	    //徽标 最多显示99
	    if (pushCount > 99) {
	        pushCount = 99;
	    }
	  //暂时不显示pushCount
	    if (pushCount < 1) {
	    	notifyUnreadTextView.setVisibility(View.GONE);
	    }else{
	    	notifyUnreadTextView.setVisibility(View.VISIBLE);
//	    	notifyUnreadTextView.setText(""+pushCount);
	    }
	    
	    if (IMUnreadCount > 99) {
	        IMUnreadCount = 99;
	    }
	    //暂时不显示IMUnreadCount
	    if (IMUnreadCount < 1) {
	    	conversationUnreadTextView.setVisibility(View.GONE);
	    }else{
	    	conversationUnreadTextView.setVisibility(View.VISIBLE);
//	    	conversationUnreadTextView.setText(""+IMUnreadCount);
	    }
	    
		//如果正好是当前这个页面则设置成已读
		if (mPager.getCurrentItem() == 1 && ((MainTabActivity)getActivity()).mTabHost.getCurrentTab() == 1) {
			//设置已读
			NewsPushModel.setIsRead();
		}
	}

}
