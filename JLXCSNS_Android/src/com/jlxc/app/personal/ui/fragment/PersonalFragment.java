package com.jlxc.app.personal.ui.fragment;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.model.UserModel;
import com.jlxc.app.base.ui.activity.BigImgLookActivity;
import com.jlxc.app.base.ui.fragment.BaseFragment;
import com.jlxc.app.base.ui.view.CustomSelectPhotoDialog;
import com.jlxc.app.base.ui.view.CustomerScrollView;
import com.jlxc.app.base.ui.view.gallery.imageloader.GalleyActivity;
import com.jlxc.app.base.utils.FileUtil;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.discovery.ui.avtivity.DiscoveryHomeActivity;
import com.jlxc.app.login.ui.activity.SelectSchoolActivity;
import com.jlxc.app.personal.model.CityModel;
import com.jlxc.app.personal.model.ProvinceModel;
import com.jlxc.app.personal.ui.activity.MyFansListActivity;
import com.jlxc.app.personal.ui.activity.MyFriendListActivity;
import com.jlxc.app.personal.ui.activity.MyNewsListActivity;
import com.jlxc.app.personal.ui.activity.PersonalSettingActivity;
import com.jlxc.app.personal.ui.activity.PersonalSignActivity;
import com.jlxc.app.personal.ui.view.PersonalPictureScrollView;
import com.jlxc.app.personal.ui.view.PersonalPictureScrollView.ScrollImageBrowseListener;
import com.jlxc.app.personal.ui.view.cityView.OnWheelChangedListener;
import com.jlxc.app.personal.ui.view.cityView.WheelView;
import com.jlxc.app.personal.ui.view.cityView.adapters.ArrayWheelAdapter;
import com.jlxc.app.personal.utils.XmlParserHandler;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("NewApi")
public class PersonalFragment extends BaseFragment implements
		View.OnClickListener {

	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_ZOOM = 3;// 缩放
	public static final int PHOTO_RESOULT = 4;// 结果
	public static final int SIGN_RESOULT = 100;// 签名添加返回
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String tmpImageName = "";// 临时文件名

	public static final int HEAD_IMAGE = 1;// 头像
	public static final int BACK_IMAGE = 2;// 背景
	private int imageType;// 点击的图片类型

	// 省份数组
	protected String[] mProvinceDatas;
	// 城市map
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	// 当前省份名
	protected String mCurrentProviceName;
	// 当前城市名
	protected String mCurrentCityName;
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private Builder cityBuilder;
	private LinearLayout linearLayout;
	// 根scollview
	@ViewInject(R.id.scrollView_person)
	private CustomerScrollView personScrollView;
	// 相册部分根布局
	@ViewInject(R.id.my_image_layout)
	private LinearLayout imageLayout;
	// 设置与加好友根布局
	@ViewInject(R.id.layout_operate_container)
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
	// // 我的相片grid
	// @ViewInject(R.id.my_image_grid_view)
	// private GridView myImageGridView;
	// 我的相片scroll
	@ViewInject(R.id.my_image_scroll_view)
	private PersonalPictureScrollView myImageScrollView;
	// 我的动态数量
	@ViewInject(R.id.my_news_count_text_view)
	private TextView myNewsCountTextView;
	// 最近来访grid
	// @ViewInject(R.id.visit_grid_view)
	// private GridView visitGridView;
	// 最近来访数量
	@ViewInject(R.id.visit_count_text_view)
	private TextView visitCountTextView;
	// 我的好友grid
	// @ViewInject(R.id.friend_grid_view)
	// private GridView friendsGridView;
	// 我的好友数量
	@ViewInject(R.id.friend_count_text_view)
	private TextView friendsCountTextView;
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

	// 用户模型
	private UserModel userModel;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	// 新图片缓存工具 背景
	DisplayImageOptions backImageOptions;
	// 前10张图片数组
	private List<String> newsImageList = new ArrayList<String>();

	// // 我的相片adapter
	// private HelloHaAdapter<String> myImageAdapter;
	// // 最近来访adapter
	// private HelloHaAdapter<String> visitAdapter;
	// // 好友adapter
	// private HelloHaAdapter<IMModel> friendsAdapter;

	@OnClick(value = { R.id.name_layout, R.id.sign_layout, R.id.birth_layout,
			R.id.sex_layout, R.id.school_layout, R.id.city_layout,
			R.id.head_image_view, R.id.back_click_layout, R.id.my_image_layout,
			R.id.visit_layout, R.id.friend_layout, R.id.setting_Button,
			R.id.card_Button })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 姓名
		case R.id.name_layout:
			nameClick();
			break;
		// 签名
		case R.id.sign_layout:
			Intent intent = new Intent(getActivity(),
					PersonalSignActivity.class);
			startActivityForResult(intent, 1);
			getActivity().overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		// 生日
		case R.id.birth_layout:
			birthClick();
			break;
		// 性别
		case R.id.sex_layout:
			sexClick();
			break;
		// 学校
		case R.id.school_layout:
			Intent schoolIntent = new Intent(getActivity(),
					SelectSchoolActivity.class);
			schoolIntent.putExtra("notRegister", true);
			startActivityWithRight(schoolIntent);
			break;
		// 城市
		case R.id.city_layout:
			linearLayout = (LinearLayout) View.inflate(getActivity(),
					R.layout.wheel, null);
			mViewProvince = (WheelView) linearLayout
					.findViewById(R.id.id_province);
			mViewCity = (WheelView) linearLayout.findViewById(R.id.id_city);

			cityBuilder.setView(linearLayout);
			setUpListener();
			setUpData();

			final Dialog dialog = cityBuilder.show();

			TextView cancelTextView = (TextView) linearLayout
					.findViewById(R.id.tv_custom_alert_dialog_cancel);
			cancelTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			TextView confirmTextView = (TextView) linearLayout
					.findViewById(R.id.tv_custom_alert_dialog_confirm);
			confirmTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					cityTextView
							.setText(mCurrentProviceName + mCurrentCityName);
					uploadInformation("city", mCurrentProviceName + ","
							+ mCurrentCityName);
					dialog.dismiss();
				}
			});

			break;
		// 头像点击
		case R.id.head_image_view:
			// dialog
			// 设置为头像
			imageType = HEAD_IMAGE;
			final CustomSelectPhotoDialog selectDialog = new CustomSelectPhotoDialog(
					this.getActivity());
			selectDialog.show();
			selectDialog
					.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

						@Override
						public void onSelectGallery() {
							// 相册
							tmpImageName = JLXCUtils.getPhotoFileName() + "";
							Intent intentAlbum = new Intent(getActivity(),
									GalleyActivity.class);
							intentAlbum
									.putExtra(
											GalleyActivity.INTENT_KEY_SELECTED_COUNT,
											0);
							intentAlbum.putExtra(GalleyActivity.INTENT_KEY_ONE,
									true);
							startActivityForResult(intentAlbum, ALBUM_SELECT);

							selectDialog.dismiss();
						}

						@Override
						public void onSelectCamera() {
							// 相机
							Intent intentCamera = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							tmpImageName = JLXCUtils.getPhotoFileName() + "";
							File tmpFile = new File(FileUtil.TEMP_PATH
									+ tmpImageName);
							intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tmpFile));
							startActivityForResult(intentCamera, TAKE_PHOTO);
							selectDialog.dismiss();
						}

					});
			break;
		// 背景点击
		case R.id.back_click_layout:
			// 设置为背景
			imageType = BACK_IMAGE;
			final CustomSelectPhotoDialog selectBackDialog = new CustomSelectPhotoDialog(
					this.getActivity());
			selectBackDialog.show();
			selectBackDialog
					.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

						@Override
						public void onSelectGallery() {
							// 相册
							tmpImageName = JLXCUtils.getPhotoFileName() + "";
							Intent intentAlbum = new Intent(getActivity(),
									GalleyActivity.class);
							intentAlbum
									.putExtra(
											GalleyActivity.INTENT_KEY_SELECTED_COUNT,
											0);
							intentAlbum.putExtra(GalleyActivity.INTENT_KEY_ONE,
									true);
							startActivityForResult(intentAlbum, ALBUM_SELECT);

							selectBackDialog.dismiss();
						}

						@Override
						public void onSelectCamera() {
							// 相机
							Intent intentCamera = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							tmpImageName = JLXCUtils.getPhotoFileName() + "";
							File tmpFile = new File(FileUtil.TEMP_PATH
									+ tmpImageName);
							intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tmpFile));
							startActivityForResult(intentCamera, TAKE_PHOTO);
							selectBackDialog.dismiss();
						}

					});

			break;
		case R.id.my_image_layout:
			// 图片点击
			Intent myImageIntent = new Intent(getActivity(),
					MyNewsListActivity.class);
			myImageIntent.putExtra(MyNewsListActivity.INTNET_KEY_UID,
					userModel.getUid() + "");
			startActivityWithRight(myImageIntent);
			break;
		case R.id.visit_layout:
			// 最近来访点击
			// Intent visitIntent = new Intent(getActivity(),
			// VisitListActivity.class);
			// visitIntent.putExtra(VisitListActivity.INTENT_KEY,
			// userModel.getUid());
			// startActivityWithRight(visitIntent);
			Intent fansIntent = new Intent(getActivity(),
					MyFansListActivity.class);
			startActivityWithRight(fansIntent);
			break;
		case R.id.friend_layout:
			// 我的好友列表
			Intent friendIntent = new Intent(getActivity(),
					MyFriendListActivity.class);
			startActivityWithRight(friendIntent);
			break;
		case R.id.setting_Button:
			// 设置
			Intent setIntent = new Intent(getActivity(),
					PersonalSettingActivity.class);
			startActivityWithRight(setIntent);
			break;
		case R.id.card_Button:
			// // 名片点击
			// Intent cardIntent = new Intent(getActivity(),
			// MyCardActivity.class);
			// startActivityWithRight(cardIntent);
			// 发现入口
			Intent discoverIntent = new Intent(getActivity(),
					DiscoveryHomeActivity.class);
			startActivityWithRight(discoverIntent);
			break;
		default:
			break;
		}
	}

	// ////////////////////////////////life
	// cycle/////////////////////////////////
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_personal;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void setUpViews(View rootView) {

		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		// 背景
		backImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_back_image)
				.showImageOnFail(R.drawable.default_back_image)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		userModel = UserManager.getInstance().getUser();

		// 签名因为要跳到领一个页面 所以在只初始化一次
		if (null == userModel.getSign() || "".equals(userModel.getSign())) {
			signTextView.setText("牛逼的签名可以彰显彪悍的人生");
			signTextView.setTextColor(Color.rgb(204, 204, 204));
		} else {
			signTextView.setText(userModel.getSign());
			signTextView.setTextColor(Color.rgb(77, 77, 77));
		}

		// 解析省份城市xml
		initProvinceDatas();
		cityBuilder = new AlertDialog.Builder(getActivity(),
				AlertDialog.THEME_HOLO_LIGHT);
		// 设置newsScroll点击
		myImageScrollView.setBrowseListener(new ScrollImageBrowseListener() {
			@Override
			public void clickImage(int positon) {
				if (newsImageList.size() > 0) {
					List<String> newsImages = new ArrayList<String>();
					for (String path : newsImageList) {
						newsImages.add(JLXCConst.ATTACHMENT_ADDR + path);
					}
					Intent intent = new Intent(getActivity(),
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
					imageLayout.getLocationOnScreen(scollPosition);
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
	}

	@Override
	public void loadLayout(View rootView) {

	}

	@Override
	public void onResume() {
		super.onResume();
		// 头像 2015-07-07/01436273216_sub.jpg
		if (null != userModel.getHead_image()
				&& userModel.getHead_image().length() > 0) {
			ImageLoader.getInstance().displayImage(
					JLXCConst.ATTACHMENT_ADDR + userModel.getHead_image(),
					headImageView, headImageOptions);
		} else {
			headImageView.setImageResource(R.drawable.default_avatar);
		}

		if (null != userModel.getBackground_image()
				&& userModel.getBackground_image().length() > 0) {
			ImageLoader.getInstance()
					.displayImage(
							JLXCConst.ATTACHMENT_ADDR
									+ userModel.getBackground_image(),
							backImageView, backImageOptions);
		} else {
			backImageView.setImageResource(R.drawable.default_back_image);
		}

		// 姓名
		if (null == userModel.getName() || "".equals(userModel.getName())) {
			nameTextView.setText("暂无");
		} else {
			nameTextView.setText(userModel.getName());
			// 顶部姓名
			topNameTextView.setText(userModel.getName());
		}
		// 生日
		if (null == userModel.getBirthday()
				|| "".equals(userModel.getBirthday())) {
			birthTextView.setText("暂无");
		} else {
			birthTextView.setText(userModel.getBirthday());
		}
		// 性别
		if (userModel.getSex() == 0) {
			sexTextView.setText("帅锅");
			sexImageView.setImageResource(R.drawable.sex_boy);
		} else {
			sexTextView.setText("妹子");
			sexImageView.setImageResource(R.drawable.sex_girl);
		}
		// 学校字符串
		String schoolString = "";
		// 学校
		if (null == userModel.getSchool() || "".equals(userModel.getSchool())) {
			schoolTextView.setText("暂无");
		} else {
			schoolTextView.setText(userModel.getSchool());
			schoolString = userModel.getSchool();
		}
		// 城市
		if (null == userModel.getCity() || "".equals(userModel.getCity())) {
			cityTextView.setText("暂无");
		} else {
			cityTextView.setText(userModel.getCity());
		}

		// 顶部学校tv 暂时只有学校
		topSchoolTextView.setText(schoolString);

		// 获取当前最近的三张状态图片
		getNewsImages();
		// getVisitImages();
		getFansCount();
		getFriendsImages();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("test11" + " " + requestCode, 1);
		// 图片返回值
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				LogUtils.i("SD card is not avaiable/writeable right now.", 1);
				return;
			}
			// 头像需要缩放
			switch (requestCode) {
			case TAKE_PHOTO:// 当选择拍照时调用
				// 头像 获取剪切
				if (imageType == HEAD_IMAGE) {
					File tmpFile = new File(FileUtil.TEMP_PATH + tmpImageName);
					startPhotoZoom(Uri.fromFile(tmpFile));
				} else {

					// 图片压缩
					int[] screenSize = getScreenSize();
					if (FileUtil.tempToLocalPath(tmpImageName, screenSize[0],
							screenSize[1])) {
						// filterImage(FileUtil.BIG_IMAGE_PATH + tmpImageName);
						uploadImage(FileUtil.BIG_IMAGE_PATH + tmpImageName);
					}
				}

				break;
			case ALBUM_SELECT:// 当选择从本地获取图片时
				// 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
				if (data != null) {
					// 头像 获取剪切
					if (imageType == HEAD_IMAGE) {
						List<String> resultList = (List<String>) data
								.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
						// 循环处理图片
						for (String fileRealPath : resultList) {
							// 只取一张
							File tmpFile = new File(fileRealPath);
							startPhotoZoom(Uri.fromFile(tmpFile));
							break;
						}

					} else {

						List<String> resultList = (List<String>) data
								.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
						int[] screenSize1 = getScreenSize();
						// 循环处理图片
						for (String fileRealPath : resultList) {
							// 用户id+时间戳
							if (fileRealPath != null
									&& FileUtil.tempToLocalPath(fileRealPath,
											tmpImageName, screenSize1[0],
											screenSize1[1])) {
								uploadImage(FileUtil.BIG_IMAGE_PATH
										+ tmpImageName);
								break;
							}
						}

					}
				}
				break;
			case PHOTO_RESOULT:// 返回的结果
				if (data != null) {
					if (null != tmpImageName) {
						// 删除临时文件
						File file = new File(FileUtil.TEMP_PATH + tmpImageName);
						if (file.exists()) {
							file.delete();
						}
						uploadImage(FileUtil.HEAD_PIC_PATH + tmpImageName);
					}

				}
				break;
			}
		} else if (resultCode == SIGN_RESOULT) {
			// 签名返回
			String signString = data.getStringExtra("sign");
			if (null == signString || "".equals(signString)) {
				signTextView.setText("牛逼的签名可以彰显彪悍的人生");
				signTextView.setTextColor(Color.rgb(204, 204, 204));
			} else {
				signTextView.setText(signString);
				signTextView.setTextColor(Color.rgb(77, 77, 77));
			}
			uploadInformation("sign", signString);
		} else {
			File file = new File(FileUtil.TEMP_PATH + tmpImageName);
			if (file.exists()) {
				file.delete();
			}
			tmpImageName = "";
		}

	}

	// ////////////////////private method////////////////////////
	// 开启缩放
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		intent.putExtra("outputX", 960);
		intent.putExtra("outputY", 960);

		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		intent.putExtra("scale", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		// intent.putExtra("return-data", true);

		File headDir = new File(FileUtil.HEAD_PIC_PATH);
		if (!headDir.exists()) {
			headDir.mkdir();
		}
		File tmpFile = new File(FileUtil.HEAD_PIC_PATH + tmpImageName);
		// 地址
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
		startActivityForResult(intent, PHOTO_RESOULT);
	}

	// 姓名点击
	private void nameClick() {

		// dialog
		Builder nameAlertDialog = new AlertDialog.Builder(getActivity());
		LinearLayout textViewLayout = (LinearLayout) View.inflate(
				getActivity(), R.layout.dialog_text_view, null);
		nameAlertDialog.setView(textViewLayout);
		final EditText et_search = (EditText) textViewLayout
				.findViewById(R.id.name_edit_text);
		et_search.setText(userModel.getName());
		et_search.setSelection(et_search.getText().length());

		final Dialog dialog = nameAlertDialog.show();
		TextView cancelTextView = (TextView) textViewLayout
				.findViewById(R.id.tv_custom_alert_dialog_cancel);
		cancelTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		TextView confirmTextView = (TextView) textViewLayout
				.findViewById(R.id.tv_custom_alert_dialog_confirm);
		confirmTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = et_search.getText().toString().trim();
				if (name.length() < 1) {
					ToastUtil.show(getActivity(), "昵称不能为空");
					return;
				}
				if (name.length() > 10) {
					ToastUtil.show(getActivity(), "昵称不能超过10个字");
					return;
				}
				uploadInformation("name", name);
				nameTextView.setText(name);
				dialog.dismiss();
			}
		});

	}

	@SuppressLint({ "NewApi", "InflateParams" })
	private void birthClick() {

		LinearLayout dateTimeLayout = (LinearLayout) getActivity()
				.getLayoutInflater().inflate(R.layout.birth_picker_view, null);
		final DatePicker datePicker = (DatePicker) dateTimeLayout
				.findViewById(R.id.datepicker);
		Calendar calendar = Calendar.getInstance();
		if (userModel.getBirthday().length() > 0) {
			String[] date = userModel.getBirthday().split("-");
			if (date.length == 3) {
				calendar.set(JLXCUtils.stringToInt(date[0]),
						JLXCUtils.stringToInt(date[1]) - 1,
						JLXCUtils.stringToInt(date[2]));
			}
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Log.i("haha", year + " " + " " + monthOfYear + " "
								+ dayOfMonth);
					}
				});

		Builder builder = new AlertDialog.Builder(getActivity())
				.setView(dateTimeLayout);

		final Dialog dialog = builder.show();
		TextView cancelTextView = (TextView) dateTimeLayout
				.findViewById(R.id.tv_custom_alert_dialog_cancel);
		cancelTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		TextView confirmTextView = (TextView) dateTimeLayout
				.findViewById(R.id.tv_custom_alert_dialog_confirm);
		confirmTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String date = datePicker.getYear() + "-"
						+ (datePicker.getMonth() + 1) + "-"
						+ datePicker.getDayOfMonth();
				LogUtils.i(date, 1);
				birthTextView.setText(date);
				uploadInformation("birthday", date);
				dialog.dismiss();
			}
		});

	}

	// 性别点击
	private void sexClick() {

		// dialog
		LinearLayout sexViewLayout = (LinearLayout) View.inflate(getActivity(),
				R.layout.dialog_sex_view, null);
		final Dialog sexAlertDialog = new AlertDialog.Builder(getActivity())
				.create();
		sexAlertDialog.show();
		sexAlertDialog.setContentView(sexViewLayout);
		sexAlertDialog.setCanceledOnTouchOutside(true);
		// 设置大小
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		WindowManager.LayoutParams params = sexAlertDialog.getWindow()
				.getAttributes();
		params.width = (int) (width * 0.8);
		sexAlertDialog.getWindow().setAttributes(params);

		// 性别男
		TextView boyTextView = (TextView) sexViewLayout
				.findViewById(R.id.boy_text_view);
		boyTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sexTextView.setText("帅锅");
				sexImageView.setImageResource(R.drawable.sex_boy);
				uploadInformation("sex", "" + 0);
				sexAlertDialog.dismiss();
			}
		});
		// 性别女
		TextView girlTextView = (TextView) sexViewLayout
				.findViewById(R.id.girl_text_view);
		girlTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sexTextView.setText("妹子");
				sexImageView.setImageResource(R.drawable.sex_girl);
				uploadInformation("sex", "" + 1);
				sexAlertDialog.dismiss();
			}
		});

	}

	// 获取当前最近的十张状态图片
	private void getNewsImages() {

		String path = JLXCConst.GET_NEWS_COVER_LIST + "?" + "uid="
				+ UserManager.getInstance().getUser().getUid();

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
							// 数据处理
							JSONArray array = jResult
									.getJSONArray(JLXCConst.HTTP_LIST);

							newsImageList.clear();
							for (int i = 0; i < array.size(); i++) {
								JSONObject object = (JSONObject) array.get(i);
								newsImageList.add(object.getString("sub_url"));
							}
							// 设置图片
							myImageScrollView.setNewsImageList(newsImageList);

							int imageCount = jResult.getIntValue("news_count");
							if (imageCount > 0) {
								myNewsCountTextView.setText(imageCount + "条");
							} else {
								myNewsCountTextView.setText("0条");
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							// myImageAdapter.clear();
							myNewsCountTextView.setText("0条");
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));

	}

	// // 获取当前最近的三张最近来访人的头像
	// private void getVisitImages() {
	//
	// String path = JLXCConst.GET_VISIT_IMAGES + "?" + "uid="
	// + UserManager.getInstance().getUser().getUid();
	// HttpManager.get(path, new JsonRequestCallBack<String>(
	// new LoadDataHandler<String>() {
	//
	// @Override
	// public void onSuccess(JSONObject jsonResponse, String flag) {
	// super.onSuccess(jsonResponse, flag);
	// int status = jsonResponse
	// .getInteger(JLXCConst.HTTP_STATUS);
	// if (status == JLXCConst.STATUS_SUCCESS) {
	// JSONObject jResult = jsonResponse
	// .getJSONObject(JLXCConst.HTTP_RESULT);
	// // 数据处理
	// // JSONArray array = jResult
	// // .getJSONArray(JLXCConst.HTTP_LIST);
	// // List<String> headImageList = new
	// // ArrayList<String>();
	// // if (null != array && array.size() < 1) {
	// // visitAdapter.clear();
	// // }
	// // for (int i = 0; i < array.size(); i++) {
	// // JSONObject object = (JSONObject) array.get(i);
	// // headImageList.add(object
	// // .getString("head_sub_image"));
	// // }
	// // visitAdapter.replaceAll(headImageList);
	// // 人数
	// int visitCount = jResult.getIntValue("visit_count");
	// if (visitCount > 0) {
	// visitCountTextView.setText(visitCount + "");
	// } else {
	// visitCountTextView.setText("");
	// }
	// }
	//
	// if (status == JLXCConst.STATUS_FAIL) {
	// // ToastUtil.show(getActivity(),
	// // jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
	// // visitAdapter.clear();
	// visitCountTextView.setText("");
	// }
	// }
	//
	// @Override
	// public void onFailure(HttpException arg0, String arg1,
	// String flag) {
	// super.onFailure(arg0, arg1, flag);
	// ToastUtil.show(getActivity(), "网络有毒=_=");
	// }
	//
	// }, null));
	// }

	// 获取最近关注的三个人的头像
	private void getFansCount() {

		String path = JLXCConst.GET_FANS_COUNT + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
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
							int fansCount = jResult.getIntValue("fans_count");
							if (fansCount > 0) {
								visitCountTextView.setText(fansCount + "");
							} else {
								visitCountTextView.setText("0");
							}
						}

						if (status == JLXCConst.STATUS_FAIL) {
							visitCountTextView.setText("0");
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));
	}

	// 获取最近关注的三个人的头像
	private void getFriendsImages() {

		String path = JLXCConst.GET_FRIENDS_IMAGE + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
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
							// 人数
							int friendCount = jResult
									.getIntValue("friend_count");
							if (friendCount > 0) {
								friendsCountTextView.setText(friendCount + "");
							} else {
								friendsCountTextView.setText("0");
							}

						}

						if (status == JLXCConst.STATUS_FAIL) {
							// friendsAdapter.clear();
							friendsCountTextView.setText("0");
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));
	}

	private void uploadInformation(final String field, final String value) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid() + "");
		params.addBodyParameter("field", field);
		params.addBodyParameter("value", value);

		HttpManager.post(JLXCConst.CHANGE_PERSONAL_INFORMATION, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							// 设置数据
							if ("name".equals(field)) {
								// 姓名
								userModel.setName(value);
							} else if ("sign".equals(field)) {
								// 签名
								if ("".equals(value)) {
									signTextView.setText("牛逼的签名可以彰显彪悍的人生");
									signTextView.setTextColor(Color.rgb(204,
											204, 204));
								} else {
									userModel.setSign(value);
									signTextView.setText(value);
								}
							} else if ("birthday".equals(field)) {
								// 生日
								userModel.setBirthday(value);
							} else if ("sex".equals(field)) {
								// 性别
								userModel.setSex(Integer.parseInt(value));
							} else if ("city".equals(field)) {
								// 城市
								userModel.setCity(value);
							}
							// 本地持久化
							UserManager.getInstance().saveAndUpdate();
						}
						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(getActivity(), jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(getActivity(), "网络异常");
					}
				}, null));
	}

	// 上传头像
	private void uploadImage(final String path) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid() + "");
		File uplodaFile = new File(path);
		if (!uplodaFile.exists()) {
			return;
		}
		params.addBodyParameter("image", uplodaFile);
		// 类型
		if (imageType == HEAD_IMAGE) {
			params.addBodyParameter("field", "head_image");
		} else {
			params.addBodyParameter("field", "background_image");
		}

		showLoading(getActivity(), "上传中^_^");

		HttpManager.post(JLXCConst.CHANGE_INFORMATION_IMAGE, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							String serverPath = jsonResponse.getJSONObject(
									JLXCConst.HTTP_RESULT).getString("image");
							if (imageType == HEAD_IMAGE) {
								// 头像有缩略图
								String subPath = jsonResponse.getJSONObject(
										JLXCConst.HTTP_RESULT).getString(
										"subimage");
								userModel.setHead_image(serverPath);
								userModel.setHead_sub_image(subPath);
								ImageLoader.getInstance().displayImage(
										"file://" + FileUtil.HEAD_PIC_PATH
												+ tmpImageName, headImageView,
										headImageOptions);
								ImageLoader.getInstance().displayImage(
										JLXCConst.ATTACHMENT_ADDR + serverPath,
										headImageView, headImageOptions);

								// 刷新信息
								UserInfo userInfo = new UserInfo(JLXCConst.JLXC
										+ userModel.getUid(), userModel
										.getName(), Uri
										.parse(JLXCConst.ATTACHMENT_ADDR
												+ serverPath));
								RongIM.getInstance().refreshUserInfoCache(
										userInfo);
							} else {
								userModel.setBackground_image(serverPath);
								ImageLoader.getInstance().displayImage(
										"file://" + FileUtil.BIG_IMAGE_PATH
												+ tmpImageName, backImageView,
										backImageOptions);
								ImageLoader.getInstance().displayImage(
										JLXCConst.ATTACHMENT_ADDR + serverPath,
										backImageView, backImageOptions);

							}
							ToastUtil.show(getActivity(), jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
							// 本地持久化
							UserManager.getInstance().saveAndUpdate();
							// 删除临时文件
							File tmpFile = new File(path);
							if (tmpFile.exists()) {
								tmpFile.delete();
							}
							tmpImageName = "";
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(getActivity(), jsonResponse
									.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(getActivity(), "网络异常");
					}
				}, null));

	}

	// private String getRealPathFromURI(Uri contentURI) {
	// String result;
	// Cursor cursor = getActivity().getContentResolver().query(contentURI,
	// null, null, null, null);
	// if (cursor == null) { // Source is Dropbox or other similar local file
	// // path
	// result = contentURI.getPath();
	// } else {
	// cursor.moveToFirst();
	// int idx = cursor
	// .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
	// result = cursor.getString(idx);
	// cursor.close();
	// }
	// return result;
	// }

	/**
	 * XML解析
	 */

	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = getActivity().getAssets();
		try {
			// 解析
			InputStream input = asset.open("province_data.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			provinceList = handler.getDataList();
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
				}
			}
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					cityNames[j] = cityList.get(j).getName();
				}
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void setUpListener() {
		// 设置样式
		mViewProvince.setShadowColor(0xeeffffff, 0x00ffffff, 0x33ffffff);
		// mViewProvince.setWheelForeground(R.drawable.icon);
		// 省份改变
		mViewProvince.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateCities();
			}
		});
		// 设置样式
		mViewCity.setShadowColor(0xeeffffff, 0x00ffffff, 0x33ffffff);
		// 城市改变
		mViewCity.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateAreas();
			}
		});
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				getActivity(), mProvinceDatas));
		mViewProvince.setVisibleItems(3);
		mViewCity.setVisibleItems(3);
		updateCities();
		updateAreas();
	}

	/**
	 * WheelView滚动省份变化
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
	}

	/**
	 * WheelView滚动城市变化
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(),
				cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("imageType", imageType);
		outState.putSerializable("tmpImageName", tmpImageName);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);

		if (null != savedInstanceState) {
			imageType = savedInstanceState.getInt("imageType");
			tmpImageName = savedInstanceState.getString("tmpImageName");
		}
	}

	// //////////////////////getter setter/////////////////////

	public String getTmpImageName() {
		return tmpImageName;
	}

	public void setTmpImageName(String tmpImageName) {
		this.tmpImageName = tmpImageName;
	}

	// 点击事件.........
	@Override
	public void onClick(View v) {

	}
}
