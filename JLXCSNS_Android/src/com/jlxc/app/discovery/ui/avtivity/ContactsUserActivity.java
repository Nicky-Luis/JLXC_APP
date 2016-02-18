package com.jlxc.app.discovery.ui.avtivity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.discovery.model.PersonModel;
import com.jlxc.app.message.helper.MessageAddFriendHelper;
import com.jlxc.app.message.model.IMModel;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactsUserActivity extends BaseActivityWithTopBar {

	// 获取库Phon表字段
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };
	// 联系人显示名称
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	// 电话号码
	private static final int PHONES_NUMBER_INDEX = 1;
	// 联系人名称
	private ArrayList<String> mContactsName = new ArrayList<String>();
	// 联系人号码
	private ArrayList<String> mContactsNumber = new ArrayList<String>();
	// 联系人listview
	@ViewInject(R.id.listview_contacts_user)
	private PullToRefreshListView contactsListView;
	// 提示信息
	@ViewInject(R.id.tv_contacts_prompt)
	private TextView promptTextView;
	// 数据源
	private List<PersonModel> dataList = new ArrayList<PersonModel>();
	// 适配器
	private HelloHaAdapter<PersonModel> contactsAdapter;
	// // bitmap的处理
	// private static BitmapUtils bitmapUtils;
	// 新图片缓存工具 头像
	private DisplayImageOptions headImageOptions;

	@Override
	public int setLayoutId() {
		return R.layout.activity_add_contacts_user;
	}

	@Override
	protected void setUpView() {

		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		init();
		getPhoneContacts();
		if (isCanUseSim()) {
			//判断SIM卡是否可用
			getSIMContacts();
		}
		listviewSet();

		if (mContactsNumber.size() > 0) {
			// 获取联系人数据
			getContactsPerson(String.valueOf(UserManager.getInstance()
					.getUser().getUid()), getContactsJSON(mContactsNumber));
		} else {
			promptTextView.setVisibility(View.VISIBLE);
			promptTextView.setText("真替你感到寂寞，一个好友都没有 (´•︵•`)");
		}
	}

	// ////////////////////private method///////////////////////////////////
	/**
	 * 初始化
	 * */
	private void init() {
		setBarText("通讯录中的小伙伴 (・ω・=)");
		// bitmap初始化
		// bitmapUtils = BitmapManager.getInstance().getBitmapUtils(
		// ContactsUserActivity.this, true, true);
		//
		// bitmapUtils.configDefaultLoadingImage(android.R.color.darker_gray);
		// bitmapUtils.configDefaultLoadFailedImage(R.drawable.default_avatar);
	}

	/**
	 * 数据绑定初始化
	 * */
	private void listviewSet() {
		// 设置刷新模式
		contactsListView.setMode(Mode.DISABLED);
		contactsListView.setClickable(true);
		contactsAdapter = new HelloHaAdapter<PersonModel>(
				ContactsUserActivity.this, R.layout.add_contacts_item_layout,
				dataList) {

			@Override
			protected void convert(final HelloHaBaseAdapterHelper helper,
					PersonModel item) {
				final PersonModel currentPerson = item;
				// 绑定头像图片
				// helper.setImageUrl(R.id.iv_contacts_head, bitmapUtils,
				// item.getHeadSubImage(), new NewsBitmapLoadCallBack());
				ImageView headImageView = helper.getView(R.id.iv_contacts_head);
				if (null != item.getHeadSubImage()
						&& item.getHeadSubImage().length() > 0) {
					ImageLoader.getInstance().displayImage(
							JLXCConst.ATTACHMENT_ADDR + item.getHeadSubImage(),
							headImageView, headImageOptions);
				} else {
					headImageView.setImageResource(R.drawable.default_avatar);
				}
				// 绑定昵称
				helper.setText(R.id.tv_contact_user_name, item.getUserName());
				// 通讯录名称
				String contactName = "";
				if (mContactsNumber.contains(item.getPhoneNumber())) {
					int index = mContactsNumber.indexOf(item.getPhoneNumber());
					contactName = mContactsName.get(index);
				}

				helper.setText(R.id.tv_contacts_name, "通讯录好友：" + contactName);
				helper.setOnClickListener(R.id.layout_contacts_root_view,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 跳转至用户的主页
								Intent intentUsrMain = new Intent(
										ContactsUserActivity.this,
										OtherPersonalActivity.class);
								intentUsrMain.putExtra(
										OtherPersonalActivity.INTENT_KEY,
										JLXCUtils.stringToInt(currentPerson
												.getUerId()));
								startActivityWithRight(intentUsrMain);
							}
						});

				Button addButton = helper.getView(R.id.btn_contacts_add);
				if (null != item.getIsFriend()
						&& "1".equals(item.getIsFriend())) {
					addButton.setText("已关注");
					addButton.setBackgroundResource(R.color.main_gary);
					addButton.setTextColor(getResources().getColorStateList(R.color.main_white));
					addButton.setEnabled(false);
				} else {
					addButton.setText("关注");
					addButton.setBackgroundResource(R.color.main_yellow);
					addButton.setTextColor(getResources().getColorStateList(R.color.main_brown));
					addButton.setEnabled(true);
				}

				// 点击添加按钮
				helper.setOnClickListener(R.id.btn_contacts_add,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 点击添加按钮
								IMModel imModel = new IMModel();
								imModel.setTargetId(JLXCConst.JLXC
										+ currentPerson.getUerId());
								imModel.setTitle(currentPerson.getUserName());
								String headImage = currentPerson.getHeadImage();
								if (headImage != null) {
									headImage = headImage.replace(
											JLXCConst.ATTACHMENT_ADDR, "");
								} else {
									headImage = "";
								}
								imModel.setAvatarPath(headImage);
								addFriend(imModel, helper.getPosition());
							}
						});
			}
		};

		/**
		 * 刷新监听
		 * */
		contactsListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						contactsListView.postDelayed(new Runnable() {
							@Override
							public void run() {
								contactsListView.onRefreshComplete();
							}
						}, 1);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						contactsListView.postDelayed(new Runnable() {
							@Override
							public void run() {
								contactsListView.onRefreshComplete();
							}
						}, 1);
					}
				});

		// 快速滑动时不加载图片
		contactsListView.setAdapter(contactsAdapter);
		contactsListView.setClickable(true);
	}

	/**
	 * 电话数据转换为jsonObject
	 * */
	private String getContactsJSON(ArrayList<String> numberList) {
		JSONArray array = new JSONArray();

		for (String phoneNum : numberList) {
			array.add(phoneNum);
		}
		return array.toJSONString();
	}

	/**
	 * 得到手机通讯录联系人信息
	 **/
	private void getPhoneContacts() {
		ContentResolver resolver = ContactsUserActivity.this
				.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				if (phoneNumber.length() == 11) {
					mContactsName.add(contactName);
					mContactsNumber.add(phoneNumber);
				}
			}
			phoneCursor.close();
		}
	}

	/**
	 * 得到手机SIM卡联系人人信息
	 **/
	private void getSIMContacts() {
		ContentResolver resolver = ContactsUserActivity.this
				.getContentResolver();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				if (phoneNumber.length() == 11) {
					mContactsName.add(contactName);
					mContactsNumber.add(phoneNumber);
				}
			}
			phoneCursor.close();
		}
	}

	/**
	 * sdcard是否可读写
	 * */
	private boolean IsCanUseSdCard() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * sim卡是否可读
	 **/
	private boolean isCanUseSim() {
		try {
			TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取通讯录好友信息
	 * */
	private void getContactsPerson(String userId, String contact) {
		// 网络请求
		showLoading("信息获取中ミ ﾟДﾟ彡", true);
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", userId);
		params.addBodyParameter("contact", contact);

		HttpManager.post(JLXCConst.GET_CONTACT_USER, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						switch (status) {
						case JLXCConst.STATUS_SUCCESS:
							hideLoading();
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 获取数据列表
							@SuppressWarnings("unchecked")
							List<JSONObject> JPersonList = (List<JSONObject>) jResult
									.get("list");
							jsonToPersonData(JPersonList);
							break;

						case JLXCConst.STATUS_FAIL:
							hideLoading();
							ToastUtil.show(ContactsUserActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						promptTextView.setVisibility(View.VISIBLE);
						promptTextView.setText("网络太差了吧 (´•︵•`)");
						ToastUtil.show(ContactsUserActivity.this,
								"网络太差，请检查 =_=");
					}

				}, null));
	}

	/**
	 * 数据解析
	 * */
	private void jsonToPersonData(List<JSONObject> jPersonList) {

		dataList.clear();
		for (JSONObject likeObj : jPersonList) {
			PersonModel tempPerson = new PersonModel();
			tempPerson.setContentWithJson(likeObj);
			dataList.add(tempPerson);
		}
		contactsAdapter.addAll(dataList);

		if (contactsAdapter.getCount() == 0) {
			promptTextView.setVisibility(View.VISIBLE);
			promptTextView.setText("真替你感到寂寞，一个好友都没有 (´•︵•`)");
		} else {
			promptTextView.setVisibility(View.GONE);
		}
	}

	/**
	 * 加载图片时的回调函数
	 * */
	public class NewsBitmapLoadCallBack extends
			DefaultBitmapLoadCallBack<ImageView> {
		private final ImageView iView;

		public NewsBitmapLoadCallBack() {
			this.iView = null;
		}

		// 开始加载
		@Override
		public void onLoadStarted(ImageView container, String uri,
				BitmapDisplayConfig config) {
			//
			super.onLoadStarted(container, uri, config);
		}

		// 加载过程中
		@Override
		public void onLoading(ImageView container, String uri,
				BitmapDisplayConfig config, long total, long current) {
		}

		// 加载完成时
		@Override
		public void onLoadCompleted(ImageView container, String uri,
				Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
			container.setImageBitmap(bitmap);
		}
	}

	// 添加好友
	private void addFriend(final IMModel imModel, final int index) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("friend_id",
				imModel.getTargetId().replace(JLXCConst.JLXC, "") + "");

		showLoading("添加中^_^", false);
		HttpManager.post(JLXCConst.Add_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);

						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						ToastUtil.show(ContactsUserActivity.this,
								jsonResponse.getString(JLXCConst.HTTP_MESSAGE));

						if (status == JLXCConst.STATUS_SUCCESS) {
							// 添加好友
							MessageAddFriendHelper.addFriend(imModel);
							// 更新
							PersonModel personModel = dataList.get(index);
							personModel.setIsFriend("1");
							contactsAdapter.replaceAll(dataList);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(ContactsUserActivity.this, "网络异常");
					}
				}, null));
	}
}
