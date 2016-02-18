package com.jlxc.app.personal.ui.activity;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.model.Conversation.ConversationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BaseActivity;
import com.jlxc.app.base.ui.activity.BigImgLookActivity;
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.ui.view.CustomerScrollView;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.TimeHandle;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.message.helper.MessageAddFriendHelper;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.personal.ui.view.PersonalPictureScrollView;
import com.jlxc.app.personal.ui.view.PersonalPictureScrollView.ScrollImageBrowseListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("NewApi")
public class OtherPersonalActivity extends BaseActivity {

	public final static String INTENT_KEY = "uid";

	// 根scollview
	@ViewInject(R.id.scrollView_other_person)
	private CustomerScrollView personScrollView;
	// 信息部分根布局
	@ViewInject(R.id.layout_other_person_content)
	private LinearLayout contentLayout;
	// 操作部分的根布局
	@ViewInject(R.id.layout_other_operate_container)
	private RelativeLayout operateLayout;
	// 背景图
	@ViewInject(R.id.back_image_View)
	private ImageView backImageView;
	// 头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	// 顶部姓名
	@ViewInject(R.id.top_name_text_view)
	private TextView topNameTextView;
	// 顶部学校
	@ViewInject(R.id.top_school_text_view)
	private TextView topSchoolTextView;

	// 设置按钮
	@ViewInject(R.id.setting_Button)
	private ImageButton settingButton;
	// TA的相片数量
	@ViewInject(R.id.his_news_count_text_view)
	private TextView hisNewsCountTextView;
	// TA的相片scroll
	@ViewInject(R.id.his_image_scroll_view)
	private PersonalPictureScrollView hisImageScrollView;
	// 姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	// 签名
	@ViewInject(R.id.sign_text_view)
	private TextView signTextView;
	// 学校
	@ViewInject(R.id.school_text_view)
	private TextView schoolTextView;
	// 性别
	@ViewInject(R.id.sex_text_view)
	private TextView sexTextView;
	// 性别图片
	@ViewInject(R.id.sex_image_view)
	private ImageView sexImageView;
	// 生日
	@ViewInject(R.id.birth_text_view)
	private TextView birthTextView;
	// 城市
	@ViewInject(R.id.city_text_view)
	private TextView cityTextView;

	// 发消息最外边的layout
	@ViewInject(R.id.add_send_layout)
	private LinearLayout addSendLayout;
	// 发消息layout
	@ViewInject(R.id.send_message_layout)
	private RelativeLayout sendMessageLayout;
	// 发消息btn
	@ViewInject(R.id.send_message_btn)
	private ImageButton sendMessageButton;
	// 添加好友layout
	@ViewInject(R.id.add_friend_layout)
	private RelativeLayout addFriendLayout;
	// 添加好友btn
	@ViewInject(R.id.add_friend_button)
	private ImageButton addFriendButton;

	// 共同好友
	@ViewInject(R.id.common_friend_layout)
	private LinearLayout commonFriendLayout;
	// 共同好友数量
	@ViewInject(R.id.common_friend_count_text_view)
	private TextView commonFriendCountTextView;
	// TA的好友数量
	@ViewInject(R.id.his_friend_count_text_view)
	private TextView hisFriendsCountTextView;
	// TA的粉丝数量
	@ViewInject(R.id.his_fans_count_text_view)
	private TextView hisFansCountTextView;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	// 新图片缓存工具 背景
	DisplayImageOptions backImageOptions;

	// TA的相片adapter
	// private HelloHaAdapter<String> hisImageAdapter;
	// TA的来访adapter
	// private HelloHaAdapter<String> hisFriendAdapter;
	// 查看的用户id
	private int uid;
	// 查看的用户模型
	private UserModel otherUserModel;
	// 前10张图片数组
	private List<String> newsImageList = new ArrayList<String>();

	@OnClick({ R.id.head_image_view, R.id.common_friend_layout,
			R.id.his_friend_layout, R.id.his_fans_layout,
			R.id.return_image_view, R.id.send_message_btn,
			R.id.add_friend_button, R.id.setting_Button, R.id.his_image_layout })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.head_image_view:
			if (null != otherUserModel) {
				// 头像
				Intent headIntent = new Intent(this, BigImgLookActivity.class);
				headIntent.putExtra(
						BigImgLookActivity.INTENT_KEY,
						JLXCConst.ATTACHMENT_ADDR
								+ otherUserModel.getHead_image());
				startActivity(headIntent);
			}

			break;
		case R.id.setting_Button:
			// 设置点击
			// Intent settingIntent = new Intent(this,
			// FriendSettingActivity.class);
			// settingIntent.putExtra(FriendSettingActivity.INTENT_FUID,
			// otherUserModel.getUid());
			// settingIntent.putExtra(FriendSettingActivity.INTENT_NAME,
			// otherUserModel.getName());
			// startActivityWithRight(settingIntent);
			deleteFriend();
			break;
		case R.id.his_image_layout:
			// 图片点击
			Intent myImageIntent = new Intent(this, MyNewsListActivity.class);
			myImageIntent.putExtra(MyNewsListActivity.INTNET_KEY_UID, uid + "");
			startActivityWithRight(myImageIntent);
			break;
		case R.id.his_friend_layout:
			// 他的关注
			Intent otherAttentIntent = new Intent(this,
					OtherAttentOrFansActivity.class);
			otherAttentIntent.putExtra(
					OtherAttentOrFansActivity.INTENT_USER_KEY, uid);
			otherAttentIntent.putExtra(
					OtherAttentOrFansActivity.INTENT_STATE_KEY, true);
			startActivityWithRight(otherAttentIntent);
			break;
		case R.id.his_fans_layout:
			// 他的粉丝
			// 他的关注
			Intent otherFansIntent = new Intent(this,
					OtherAttentOrFansActivity.class);
			otherFansIntent.putExtra(OtherAttentOrFansActivity.INTENT_USER_KEY,
					uid);
			otherFansIntent.putExtra(
					OtherAttentOrFansActivity.INTENT_STATE_KEY, false);
			startActivityWithRight(otherFansIntent);
			break;
		case R.id.common_friend_layout:
			// 共同好友
			Intent commonIntent = new Intent(this, CommonFriendsActivity.class);
			commonIntent.putExtra(CommonFriendsActivity.INTENT_KEY, uid);
			startActivityWithRight(commonIntent);
			break;
		case R.id.return_image_view:
			// 返回
			finishWithRight();
			break;
		case R.id.send_message_btn:
			// 发送消息
			sendMessage();
			break;
		case R.id.add_friend_button:
			// 添加好友
			addFriendConfirm();
			break;
		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_other_personal;
	}

	@SuppressLint("ClickableViewAccessibility")
	@SuppressWarnings("deprecation")
	@Override
	protected void setUpView() {
		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// 背景
		backImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_back_image)
				.showImageForEmptyUri(R.drawable.default_back_image)
				.showImageOnFail(R.drawable.default_back_image)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		Intent intent = getIntent();
		uid = intent.getIntExtra(INTENT_KEY, 0);
		// 初始化adapter
		// hisImageAdapter = initAdapter(R.layout.my_image_adapter);
		// hisFriendAdapter = initAdapter(R.layout.attrament_other_image);
		// 设置adapter
		// hisImageGridView.setAdapter(hisImageAdapter);
		// hisFriendsGridView.setAdapter(hisFriendAdapter);
		// 不能点击
		// hisImageGridView.setEnabled(false);
		// hisFriendsGridView.setEnabled(false);

		// 设置newsScroll点击
		hisImageScrollView.setBrowseListener(new ScrollImageBrowseListener() {
			@Override
			public void clickImage(int positon) {
				if (newsImageList.size() > 0) {
					List<String> newsImages = new ArrayList<String>();
					for (String path : newsImageList) {
						newsImages.add(JLXCConst.ATTACHMENT_ADDR + path);
					}
					Intent intent = new Intent(OtherPersonalActivity.this,
							BigImgLookActivity.class);
					intent.putExtra(BigImgLookActivity.INTENT_KEY_IMG_LIST,
							(Serializable) newsImages);
					intent.putExtra(BigImgLookActivity.INTENT_KEY_INDEX,
							positon);
					startActivity(intent);
				}
			}
		});
		// 渐变色
		GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM,
				new int[] { 0xff999999, 0x05999999 });
		operateLayout.setBackgroundDrawable(grad);
		operateLayout.setAlpha(0.5f);
		// 监听滚动事件
		personScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					// 可以监听到ScrollView的滚动事件
					int[] scollPosition = new int[2];
					contentLayout.getLocationOnScreen(scollPosition);
					// 头部的坐标
					int[] operatePosition = new int[2];
					operateLayout.getLocationOnScreen(operatePosition);
					// 设置透明度渐变动画
					if (scollPosition[1] < (operatePosition[1] + operateLayout
							.getMeasuredHeight())) {
						operateLayout.setAlpha(1.0f);
					} else {
						operateLayout.setAlpha(0.5f);
					}
				}
				return false;
			}
		});
		// 获取数据
		getPersonalInformation();
	}

	@Override
	protected void loadLayout(View v) {

	}

	// ////////////////////private method////////////////////////
	// 获取用户信息
	private void getPersonalInformation() {

		String path = JLXCConst.PERSONAL_INFO + "?" + "uid=" + uid
				+ "&current_id=" + UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							handleData(jResult);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(OtherPersonalActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(OtherPersonalActivity.this, "网络有毒=_=");
					}

				}, null));
	}

	// 处理数据
	private void handleData(JSONObject jsonObject) {

		otherUserModel = new UserModel();
		otherUserModel.setContentWithJson(jsonObject);
		// 姓名
		if (null == otherUserModel.getName()
				|| "".equals(otherUserModel.getName())) {
			nameTextView.setText("暂无");
		} else {
			nameTextView.setText(otherUserModel.getName());
			topNameTextView.setText(otherUserModel.getName());
		}
		// 签名
		if (null == otherUserModel.getSign()
				|| "".equals(otherUserModel.getSign())) {
			signTextView.setText("此人向来洒脱，暂无签名");
			signTextView.setTextColor(Color.rgb(204, 204, 204));
		} else {
			signTextView.setText(otherUserModel.getSign());
			signTextView.setTextColor(Color.rgb(77, 77, 77));
		}
		// 生日
		if (null == otherUserModel.getBirthday()
				|| "".equals(otherUserModel.getBirthday())) {
			birthTextView.setText("暂无");
		} else {
			birthTextView.setText(otherUserModel.getBirthday());
		}
		// 性别
		if (otherUserModel.getSex() == 0) {
			sexTextView.setText("帅锅");
			sexImageView.setImageResource(R.drawable.sex_boy);
		} else {
			sexTextView.setText("妹子");
			sexImageView.setImageResource(R.drawable.sex_girl);
		}
		// 学校
		if (null == otherUserModel.getSchool()
				|| "".equals(otherUserModel.getSchool())) {
			schoolTextView.setText("暂无");
			topSchoolTextView.setText("");
		} else {
			schoolTextView.setText(otherUserModel.getSchool());
			topSchoolTextView.setText(otherUserModel.getSchool());
		}
		// 城市
		if (null == otherUserModel.getCity()
				|| "".equals(otherUserModel.getCity())) {
			cityTextView.setText("暂无");
		} else {
			cityTextView.setText(otherUserModel.getCity());
		}

		if (null != otherUserModel.getHead_sub_image()
				&& otherUserModel.getHead_sub_image().length() > 0) {
			// 头像
			ImageLoader.getInstance().displayImage(
					JLXCConst.ATTACHMENT_ADDR
							+ otherUserModel.getHead_sub_image(),
					headImageView, headImageOptions);
		} else {
			headImageView.setImageResource(R.drawable.default_avatar);
		}

		if (null != otherUserModel.getBackground_image()
				&& otherUserModel.getBackground_image().length() > 0) {
			// 背景
			ImageLoader.getInstance().displayImage(
					JLXCConst.ATTACHMENT_ADDR
							+ otherUserModel.getBackground_image(),
					backImageView, backImageOptions);
		} else {
			backImageView.setImageResource(R.drawable.default_back_image);
		}

		// 他的朋友
		// JSONArray friendArray = jsonObject.getJSONArray("friend_list");
		// List<String> friendImageList = new ArrayList<String>();
		// for (int i = 0; i < friendArray.size(); i++) {
		// JSONObject object = (JSONObject) friendArray.get(i);
		// friendImageList.add(object.getString("head_sub_image"));
		// }
		// hisFriendAdapter.replaceAll(friendImageList);

		newsImageList.clear();
		// 他的动态
		JSONArray imagesArray = jsonObject.getJSONArray("image_list");
		for (int i = 0; i < imagesArray.size(); i++) {
			JSONObject object = (JSONObject) imagesArray.get(i);
			newsImageList.add(object.getString("sub_url"));
		}
		// 设置图片
		hisImageScrollView.setNewsImageList(newsImageList);
		// hisImageAdapter.replaceAll(imagesList);

		// 访客数量
		// if (jsonObject.getIntValue("visit_count")>0) {
		// visitFriendCountTextView.setText(jsonObject.getIntValue("visit_count")+"人");
		// }else {
		// visitFriendCountTextView.setText("");
		// }
		// 状态数量
		if (jsonObject.getIntValue("news_count") > 0) {
			hisNewsCountTextView.setText(jsonObject.getIntValue("news_count")
					+ "条");
		} else {
			hisNewsCountTextView.setText("0条");
		}
		// 好友数量
		if (jsonObject.getIntValue("friend_count") > 0) {
			hisFriendsCountTextView.setText(jsonObject
					.getIntValue("friend_count") + "");
		} else {
			hisFriendsCountTextView.setText("0");
		}
		// 粉丝数量
		if (jsonObject.getIntValue("fans_count") > 0) {
			hisFansCountTextView.setText(jsonObject.getIntValue("fans_count")
					+ "");
		} else {
			hisFansCountTextView.setText("0");
		}
		// 共同好友数量
		if (jsonObject.getIntValue("common_friend_count") > 0) {
			commonFriendCountTextView.setText(jsonObject
					.getIntValue("common_friend_count")+"");
		} else {
			commonFriendCountTextView.setText("0");
		}

		// 融云刷新信息
		UserInfo userInfo = new UserInfo(JLXCConst.JLXC
				+ otherUserModel.getUid(), otherUserModel.getName(),
				Uri.parse(JLXCConst.ATTACHMENT_ADDR
						+ otherUserModel.getHead_image()));
		// 刷新信息
		RongIM.getInstance().refreshUserInfoCache(userInfo);

		// 如果是自己
		if (UserManager.getInstance().getUser().getUid() == uid) {

			addSendLayout.setVisibility(View.GONE);
			addFriendButton.setVisibility(View.GONE);
			sendMessageButton.setVisibility(View.GONE);
			settingButton.setVisibility(View.GONE);
		} else {
			addSendLayout.setVisibility(View.VISIBLE);
			boolean isFriend = false;
			if (jsonObject.getInteger("isFriend") > 0) {
				isFriend = true;
			}
			if (isFriend) {
				addFriendLayout.setVisibility(View.GONE);
				settingButton.setVisibility(View.VISIBLE);

				LayoutParams layoutParams = (LayoutParams) sendMessageButton
						.getLayoutParams();
				layoutParams.setMargins(layoutParams.leftMargin * 2, 0,
						layoutParams.rightMargin * 2, 0);
			} else {
				addFriendLayout.setVisibility(View.VISIBLE);
				settingButton.setVisibility(View.GONE);
			}
		}
	}

	// 发送消息
	private void sendMessage() {
		IMModel imModel = IMModel.findByGroupId(JLXCConst.JLXC
				+ otherUserModel.getUid());
		// 如果存在更新
		if (null != imModel) {
			imModel.setTitle(otherUserModel.getName());
			imModel.setAvatarPath(otherUserModel.getHead_image());
			imModel.update();
		} else {
			imModel = new IMModel();
			imModel.setType(IMModel.ConversationType_PRIVATE);
			imModel.setTargetId(JLXCConst.JLXC + otherUserModel.getUid());
			imModel.setTitle(otherUserModel.getName());
			imModel.setAvatarPath(otherUserModel.getHead_image());
			imModel.setIsNew(0);
			imModel.setIsRead(1);
			imModel.setCurrentState(IMModel.GroupNotAdd);
			imModel.setOwner(UserManager.getInstance().getUser().getUid());
			imModel.save();
		}
		// 启动聊天
		RongIM.getInstance().startConversation(OtherPersonalActivity.this,
				Conversation.ConversationType.PRIVATE, imModel.getTargetId(),
				imModel.getTitle());
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	// 添加好友
	// private void addFriend() {
	//
	// // new
	// AlertDialog.Builder(this).setTitle("确定要添加好友吗？").setNegativeButton("取消",
	// null)
	// // .setPositiveButton("确定", new OnClickListener() {
	// // @Override
	// // public void onClick(DialogInterface dialog, int which) {
	// // addFriendConfirm();
	// // }
	// // }).show();
	// final CustomAlertDialog confirmDialog = new CustomAlertDialog(
	// this, "确定要添加好友吗？", "确定", "取消");
	// confirmDialog.show();
	// confirmDialog.setClicklistener(new
	// CustomAlertDialog.ClickListenerInterface() {
	// @Override
	// public void doConfirm() {
	// addFriendConfirm();
	// confirmDialog.dismiss();
	// }
	//
	// @Override
	// public void doCancel() {
	// confirmDialog.dismiss();
	// }
	// });
	//
	// }

	private void addFriendConfirm() {
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("friend_id", otherUserModel.getUid() + "");

		showLoading("添加中^_^", false);
		HttpManager.post(JLXCConst.Add_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						ToastUtil.show(OtherPersonalActivity.this,
								jsonResponse.getString(JLXCConst.HTTP_MESSAGE));

						if (status == JLXCConst.STATUS_SUCCESS) {
							settingButton.setVisibility(View.VISIBLE);
							// 本地数据持久化
							IMModel imModel = new IMModel();
							imModel.setTargetId(JLXCConst.JLXC
									+ otherUserModel.getUid());
							imModel.setAvatarPath(otherUserModel
									.getHead_image());
							imModel.setTitle(otherUserModel.getName());
							MessageAddFriendHelper.addFriend(imModel);
							// //本地数据持久化
							// IMModel imModel =
							// IMModel.findByGroupId(JLXCConst.JLXC +
							// otherUserModel.getUid());
							// //如果存在更新
							// if (null != imModel) {
							// imModel.setTitle(otherUserModel.getName());
							// imModel.setAvatarPath(otherUserModel.getHead_image());
							// // imModel.setIsNew(0);
							// // imModel.setIsRead(1);
							// imModel.setCurrentState(IMModel.GroupHasAdd);
							// //
							// imModel.setAddDate(TimeHandle.getCurrentDataStr());
							// imModel.update();
							// }else {
							// imModel = new IMModel();
							// imModel.setType(IMModel.ConversationType_PRIVATE);
							// imModel.setTargetId(JLXCConst.JLXC+otherUserModel.getUid());
							// imModel.setTitle(otherUserModel.getName());
							// imModel.setAvatarPath(otherUserModel.getHead_image());
							// imModel.setIsNew(0);
							// imModel.setIsRead(1);
							// imModel.setCurrentState(IMModel.GroupHasAdd);
							// imModel.setOwner(UserManager.getInstance().getUser().getUid());
							// //
							// imModel.setAddDate(TimeHandle.getCurrentDataStr());
							// imModel.save();
							// }

							// 成功隐藏
							addFriendLayout.setVisibility(View.GONE);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(OtherPersonalActivity.this, "网络异常");
					}
				}, null));

	}

	// popWindow
	private PopupWindow popupWindow;
	private View view;

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void showPopupWindow(View parent) {
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			view = layoutInflater
					.inflate(R.layout.view_other_pop_setting, null);
			// 删除按钮
			TextView deleteTextView = (TextView) view
					.findViewById(R.id.delete_text_view);
			deleteTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					deleteFriend();
				}
			});
			popupWindow = new PopupWindow(view, 200, 200);
		}
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(parent, 0, 2);

		// popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
	}

	private void deleteFriend() {
		// popupWindow.dismiss();

		final CustomAlertDialog confirmDialog = new CustomAlertDialog(this,
				"确定取消关注“" + otherUserModel.getName() + "”吗", "确定", "取消");
		confirmDialog.show();
		confirmDialog
				.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						deleteFriendConfirm();
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});

		// new
		// AlertDialog.Builder(this).setTitle("确定要删除好友"+otherUserModel.getName()+"吗").setPositiveButton("确定",
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		//
		// }
		// }).setNegativeButton("取消", null).show();
	}

	private void deleteFriendConfirm() {
		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("friend_id", otherUserModel.getUid() + "");

		showLoading("取消中...", false);
		HttpManager.post(JLXCConst.DELETE_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						ToastUtil.show(OtherPersonalActivity.this,
								jsonResponse.getString(JLXCConst.HTTP_MESSAGE));

						if (status == JLXCConst.STATUS_SUCCESS) {
							// 本地删除 好友管理本地持久化废弃
							// IMModel imModel = new IMModel();
							// imModel.setTargetId(JLXCConst.JLXC+otherUserModel.getUid());
							// imModel.setOwner(UserManager.getInstance().getUser().getUid());
							// imModel.remove();
							RongIMClient.getInstance().removeConversation(
									ConversationType.PRIVATE,
									JLXCConst.JLXC + otherUserModel.getUid(),
									null);
							// UI变化
							addFriendLayout.setVisibility(View.VISIBLE);
							settingButton.setVisibility(View.GONE);
							LayoutParams layoutParams = (LayoutParams) sendMessageButton
									.getLayoutParams();
							layoutParams.setMargins(
									layoutParams.leftMargin / 2, 0,
									layoutParams.rightMargin / 2, 0);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
					}
				}, null));

	}

}
