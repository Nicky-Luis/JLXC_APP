package com.jlxc.app.message.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.model.NewsPushModel;
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.base.ui.view.CustomListViewDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.message.ui.activity.NewFriendsActivity;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.receiver.NewMessageReceiver;
import com.jlxc.app.news.ui.activity.NewsDetailActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NotifyNewsFragment extends BaseFragment {

	// 列表
	@ViewInject(R.id.notify_list_view)
	private ListView notifyListView;
	// adapter
	private HelloHaAdapter<NewsPushModel> newsAdapter;
	// BitmapUtils bitmapUtils;
	// 新图片缓存工具 头像
	private DisplayImageOptions headImageOptions;
	// 头部layout
	private LinearLayout headLayout;
	//新朋友ImageViewA
	private ImageView coverAImageView;
	//新朋友ImageViewB
	private ImageView coverBImageView;	
	//新朋友ImageViewC
	private ImageView coverCImageView;
	//上述三个ImageView的数组
	private List<ImageView> coverList = new ArrayList<ImageView>(); 
	// 顶部未读新朋友
	private TextView unreadTextView;
	private int page = 1;
	private int size = 30;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_new_notify;
	}

	@Override
	public void loadLayout(View rootView) {

	}

	@Override
	public void setUpViews(View rootView) {
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		// 注册通知
		registerNotify();
		setListView();
		// 刷新列表
		refreshList();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (messageReceiver != null) {
			getActivity().unregisterReceiver(messageReceiver);
			messageReceiver = null;
		}
	}

	//////////////////////////private method/////////////////////////////
	// 设置listView
	private void setListView() {
		// 头部
		headLayout = (LinearLayout) View.inflate(getActivity(),
				R.layout.new_friend_cover_adapter, null);
		// 点击事件
		headLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newFriendIntent = new Intent(getActivity(),
						NewFriendsActivity.class);
				startActivityWithRight(newFriendIntent);
			}
		});
		// 内容
		coverAImageView = (ImageView) headLayout.findViewById(R.id.cover_A_image_view);
		coverBImageView = (ImageView) headLayout.findViewById(R.id.cover_B_image_view);
		coverCImageView = (ImageView) headLayout.findViewById(R.id.cover_C_image_view);
		coverList.add(coverAImageView);
		coverList.add(coverBImageView);
		coverList.add(coverCImageView);
		
		unreadTextView = (TextView) headLayout
				.findViewById(R.id.unread_text_view);
		
		notifyListView.addHeaderView(headLayout);

		newsAdapter = new HelloHaAdapter<NewsPushModel>(getActivity(),
				R.layout.new_notify_adapter) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					NewsPushModel item) {
				// TODO Auto-generated method stub
				// 姓名
				helper.setText(R.id.name_text_view, item.getName());
				// 时间
				helper.setText(R.id.time_text_view,
						TimeHandle.getShowTimeFormat(item.getPush_time()));
				// 内容
				if (item.getType() == NewsPushModel.PushLikeNews) {
					helper.setText(R.id.content_text_view, "为你点了赞");
				} else {
					helper.setText(R.id.content_text_view,
							item.getComment_content());
				}

				String headString = item.getHead_image();
				if (item.getHead_image().equals("null")) {
					headString = "";
				}
				// 头像
				ImageView headImageView = helper.getView(R.id.head_image_view);
				if (null != headString && headString.length() > 0) {
					ImageLoader.getInstance().displayImage(
							JLXCConst.ATTACHMENT_ADDR + headString,
							headImageView, headImageOptions);
				} else {
					headImageView.setImageResource(R.drawable.default_avatar);
				}

				// 有图片显示图片 没图片显示内容
				ImageView newsImageView = helper.getView(R.id.news_image_view);
				TextView newsTextView = helper.getView(R.id.news_text_view);
				if (null != item.getNews_image()
						&& !"".equals(item.getNews_image())) {
					newsImageView.setVisibility(View.VISIBLE);
					newsTextView.setVisibility(View.GONE);
					if (null != item.getNews_image()
							&& item.getNews_image().length() > 0) {
						ImageLoader.getInstance().displayImage(
								JLXCConst.ATTACHMENT_ADDR
										+ item.getNews_image(), newsImageView,
								headImageOptions);
					} else {
						newsImageView
								.setImageResource(R.drawable.default_avatar);
					}
				} else {
					newsImageView.setVisibility(View.GONE);
					newsTextView.setVisibility(View.VISIBLE);
					newsTextView.setText(item.getNews_content());
				}
			}
		};

		notifyListView.setAdapter(newsAdapter);
		// 点击进入详情
		notifyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NewsPushModel newsPushModel = newsAdapter.getItem(position - 1);
				Intent detailIntent = new Intent(getActivity(),
						NewsDetailActivity.class);
				detailIntent.putExtra(NewsConstants.INTENT_KEY_NEWS_ID, ""
						+ newsPushModel.getNews_id());
				startActivityWithRight(detailIntent);
				//发送通知刷新界面
				NewsPushModel.setIsRead();
				sendNotify();
			}
		});
		// 底部检测
		notifyListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 滚到底部了
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
					page++;
					List<NewsPushModel> pushModels = NewsPushModel
							.findWithPage(page, size);
					newsAdapter.addAll(pushModels);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		// 长按删除
		notifyListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, final int position, long id) {

						List<String> menuList = new ArrayList<String>();
						menuList.add("删除内容");
						final CustomListViewDialog confirmDialog = new CustomListViewDialog(
								getActivity(), menuList);
						confirmDialog.setClickCallBack(new ClickCallBack() {

							@Override
							public void Onclick(View view, int which) {
								//
								NewsPushModel newsPushModel = newsAdapter
										.getItem(position - 1);
								newsPushModel.remove();
								refreshList();
								confirmDialog.dismiss();
							}
						});
						if (null != confirmDialog && !confirmDialog.isShowing()) {
							confirmDialog.show();
						}

						return true;
					}
				});

	}

	// 刷新页面
	private void refreshList() {
		page = 1;
		List<NewsPushModel> pushModels = NewsPushModel.findWithPage(page, size);
		newsAdapter.replaceAll(pushModels);
		//刷新顶部
		refreshTopCover();
		// 未读
		int unreadNum = IMModel.unReadNewFriendsCount();
		if (unreadNum > 0) {
			unreadTextView.setText("" + unreadNum);
			unreadTextView.setVisibility(View.VISIBLE);
		} else {
			unreadTextView.setText("");
			unreadTextView.setVisibility(View.GONE);
		}

	}
	
	//刷新顶部
	private void refreshTopCover() {
		//先全隐藏
		for (ImageView coverImageView : coverList) {
			coverImageView.setVisibility(View.GONE);
		}
		
		// 刷新上端
		List<IMModel> imModels = IMModel.findThreeNewFriends();
		if (imModels.size() > 0) {
			for (int i = 0; i < imModels.size(); i++) {
				//有哪个显示哪个
				ImageView coverImageView = coverList.get(i);
				coverImageView.setVisibility(View.VISIBLE);
				IMModel imModel = imModels.get(i);
				if (null != imModel.getAvatarPath() && imModel.getAvatarPath().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR + imModel.getAvatarPath(),
							coverImageView, headImageOptions);
				} else {
					coverImageView.setImageResource(R.drawable.default_avatar);
				}
			}
		}
	}

	private NewMessageReceiver messageReceiver;

	// 注册通知
	private void registerNotify() {

		messageReceiver = new NewMessageReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				refreshList();
			}
		};
		// 回复或者点赞或新朋友到达
		IntentFilter newsfilter = new IntentFilter(
				JLXCConst.BROADCAST_NEW_MESSAGE_PUSH);
		getActivity().registerReceiver(messageReceiver, newsfilter);
	}
	
	//发送通知
	private void sendNotify() {
		//通知刷新
		Intent tabIntent = new Intent(JLXCConst.BROADCAST_TAB_BADGE);
		getActivity().sendBroadcast(tabIntent);
		//通知页面刷新
		Intent messageIntent = new Intent(JLXCConst.BROADCAST_NEW_MESSAGE_PUSH);
		getActivity().sendBroadcast(messageIntent);
		//顶部刷新
		Intent messageTopIntent = new Intent(JLXCConst.BROADCAST_MESSAGE_REFRESH);
		getActivity().sendBroadcast(messageTopIntent);
	}

}
