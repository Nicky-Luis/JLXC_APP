package com.jlxc.app.group.ui.activity;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.activity.BigImgLookActivity;
import com.jlxc.app.base.ui.view.CustomListViewDialog;
import com.jlxc.app.base.ui.view.CustomSelectPhotoDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.jlxc.app.base.ui.view.gallery.imageloader.GalleyActivity;
import com.jlxc.app.base.utils.FileUtil;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.GroupCategoryModel;
import com.jlxc.app.group.model.GroupTopicModel;
import com.jlxc.app.news.model.CommentModel;
import com.jlxc.app.news.model.ItemModel;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.news.model.NewsConstants;
import com.jlxc.app.news.model.ItemModel.CommentItem;
import com.jlxc.app.news.model.ItemModel.LikeListItem;
import com.jlxc.app.news.model.ItemModel.TitleItem;
import com.jlxc.app.news.ui.activity.NewsDetailActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressWarnings("unused")
public class CreateGroupActivity extends BaseActivityWithTopBar {

	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_RESOULT = 4;// 结果
	public static final int GET_DEPARTMENT_REQUEST_CODE = 5;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	//创建成功
	public static final String NEW_TOPIC_OK = "newTopicOK*";

	// 图片名字
	private String tmpImageName;
	// 当前图片名字
	private String currentImageName;
	// 圈子名
	@ViewInject(R.id.topic_name_edit_text)
	private EditText topicNameEditText;
	// 圈子图片
	@ViewInject(R.id.topic_image)
	private ImageView topicImageView;
	// 圈子介绍
	@ViewInject(R.id.topic_desc_edit_text)
	private EditText topicDescEditText;
	// 圈子类别
	@ViewInject(R.id.topic_category_text_view)
	private TextView topicCategoryTextView;
	// 圈子类别ID
	private int topicCategoryID;
	// 类型list
	private List<GroupCategoryModel> categoryModels;

	// 统计处理点击
	@OnClick({ R.id.topic_image, R.id.topic_add_layout,
			R.id.base_ll_right_btns, R.id.topic_category_text_view })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 头像点击
		case R.id.topic_image:
			showChoiceImageAlert();
			break;
		// 选择类型
		case R.id.topic_category_text_view:
			choiceCategory();
			break;
		case R.id.topic_add_layout:
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			break;
		case R.id.base_ll_right_btns:
			// 完成
			createNewGroupFinish();
			break;
		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_group;
	}

	@Override
	protected void setUpView() {
		setBarText("创建一个新频道");
		addRightBtn("完成");
		categoryModels = new ArrayList<GroupCategoryModel>();
	}

	// 选择类别
	private void choiceCategory() {
		// 存在直接用
		if (categoryModels.size() > 0) {
			showCategoryList();
		} else {
			showLoading("获取中..", true);
			// 不存在获取一次
			String path = JLXCConst.GET_TOPIC_CATEGORY;
			HttpManager.get(path, new JsonRequestCallBack<String>(
					new LoadDataHandler<String>() {

						@Override
						public void onSuccess(JSONObject jsonResponse,
								String flag) {
							super.onSuccess(jsonResponse, flag);
							hideLoading();
							int status = jsonResponse
									.getInteger(JLXCConst.HTTP_STATUS);
							if (status == JLXCConst.STATUS_SUCCESS) {
								// 已经有了
								if (categoryModels.size() > 0) {
									showCategoryList();
									return;
								}
								JSONObject jResult = jsonResponse
										.getJSONObject(JLXCConst.HTTP_RESULT);
								JSONArray categoryArray = jResult
										.getJSONArray(JLXCConst.HTTP_LIST);
								// 模型拼装
								for (int i = 0; i < categoryArray.size(); i++) {
									JSONObject object = categoryArray
											.getJSONObject(i);
									GroupCategoryModel model = new GroupCategoryModel();
									model.setCategory_id(object
											.getIntValue("category_id"));
									model.setCategory_name(object
											.getString("category_name"));
									categoryModels.add(model);
								}

								showCategoryList();
							}

							if (status == JLXCConst.STATUS_FAIL) {
								ToastUtil.show(CreateGroupActivity.this,
										"获取失败,请重试");
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1,
								String flag) {
							hideLoading();
							super.onFailure(arg0, arg1, flag);
							ToastUtil
									.show(CreateGroupActivity.this, "获取失败,请重试");
						}

					}, null));
		}
	}

	private void showCategoryList() {
		List<String> menuList = new ArrayList<String>();
		for (GroupCategoryModel category : categoryModels) {
			menuList.add(category.getCategory_name());
		}
		final CustomListViewDialog downDialog = new CustomListViewDialog(this,
				menuList);
		downDialog.setClickCallBack(new ClickCallBack() {
			@Override
			public void Onclick(View view, int which) {
				GroupCategoryModel model = categoryModels.get(which);
				topicCategoryID = model.getCategory_id();
				topicCategoryTextView.setText(model.getCategory_name());
				downDialog.cancel();
			}
		});
		downDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		downDialog.show();

	}

	// 创建完成
	private void createNewGroupFinish() {
		
		// 封面
		if (null == currentImageName || currentImageName.length() < 1) {
			ToastUtil.show(this, "封面怎么可以没有 ∪︿∪");
			return;
		}
		// 名称
		if (topicNameEditText.getText().toString().trim().length() < 1) {
			ToastUtil.show(this, "标题怎么可以没有 ∪︿∪");
			return;
		}
		// 名称
		if (topicNameEditText.getText().toString().trim().length() > 16) {
			ToastUtil.show(this, "标题不能超过16个字  ∪︿∪");
			return;
		}
		// 介绍
		if (topicDescEditText.getText().toString().trim().length() < 1) {
			ToastUtil.show(this, "描述不能没有  ∪︿∪");
			return;
		}
		// 介绍
		if (topicDescEditText.getText().toString().trim().length() < 25) {
			ToastUtil.show(this, "描述不能少于26个字 ∪︿∪");
			return;
		}
		// 介绍
		if (topicDescEditText.getText().toString().trim().length() > 200) {
			ToastUtil.show(this, "描述不能超过200个字");
			return;
		}
		// 类型
		if (topicCategoryID < 1) {
			ToastUtil.show(this, "类型还没选呢  ∪︿∪");
			return;
		}

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("topic_name", topicNameEditText.getText()
				.toString().trim());
		params.addBodyParameter("topic_desc", topicDescEditText.getText()
				.toString().trim());
		params.addBodyParameter("category_id", topicCategoryID + "");
		File uplodaFile = new File(FileUtil.BIG_IMAGE_PATH + currentImageName);
		if (!uplodaFile.exists()) {
			return;
		}
		params.addBodyParameter("image", uplodaFile);

		showLoading("创建中 (≡ω≡．)", false);

		HttpManager.post(JLXCConst.POST_NEW_TOPIC, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						LogUtils.i(jsonResponse.toJSONString(), 1);
						hideLoading();
						int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							ToastUtil.show(CreateGroupActivity.this,
									jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
							
							// 删除临时文件
							File tmpFile = new File(FileUtil.BIG_IMAGE_PATH
									+ currentImageName);
							if (tmpFile.exists()) {
								tmpFile.delete();
							}
							tmpImageName = "";
							currentImageName = "";
							JSONObject jsonObject = jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT);
							//发送通知
							GroupTopicModel model = new GroupTopicModel();
							model.setTopic_id(jsonObject.getIntValue("id"));
							model.setTopic_cover_image(jsonObject.getString("topic_cover_image"));
							model.setTopic_name(jsonObject.getString("topic_name"));
							model.setTopic_detail(jsonObject.getString("topic_detail"));
							model.setNews_count(0);
							model.setMember_count(1);
							sendBroadCastData(model);
							finishWithRight();
						}

						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(CreateGroupActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(CreateGroupActivity.this, "网络异常  ∪︿∪");
					}
				}, null));
	}

	private void showChoiceImageAlert() {

		// 设置为头像
		final CustomSelectPhotoDialog selectDialog = new CustomSelectPhotoDialog(
				this);
		selectDialog.show();
		selectDialog
				.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

					@Override
					public void onSelectGallery() {
						// 相册
						tmpImageName = JLXCUtils.getPhotoFileName() + "";
						// 相册
						Intent intentAlbum = new Intent(
								CreateGroupActivity.this, GalleyActivity.class);
						intentAlbum.putExtra(
								GalleyActivity.INTENT_KEY_SELECTED_COUNT, 0);
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
	}

	// 图片滤镜
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				LogUtils.i("SD card is not avaiable/writeable right now.", 1);
				return;
			}
			// 头像需要缩放
			switch (requestCode) {
			case TAKE_PHOTO:// 当选择拍照时调用
				// 图片压缩
				int[] screenSize = getScreenSize();
				if (FileUtil.tempToLocalPath(tmpImageName, screenSize[0],
						screenSize[1])) {
					displayImage(tmpImageName);
				}
				break;
			case ALBUM_SELECT:// 当选择从本地获取图片时
				if (data != null) {
					@SuppressWarnings("unchecked")
					List<String> resultList = (List<String>) data
							.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
					// 循环处理图片
					for (String fileRealPath : resultList) {
						// 只取一张
						int[] screenSize1 = getScreenSize();
						if (fileRealPath != null
								&& FileUtil.tempToLocalPath(fileRealPath,
										tmpImageName, screenSize1[0],
										screenSize1[1])) {
							displayImage(tmpImageName);
							break;
						}
					}
				}
				break;
			}
		}
	}

	public void displayImage(String imagePath) {

		currentImageName = tmpImageName;

		DisplayImageOptions headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar)
				.cacheInMemory(false).cacheOnDisk(false)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().displayImage(
				"file://" + FileUtil.BIG_IMAGE_PATH + imagePath,
				topicImageView, headImageOptions);
	}
	
	/**
	 * 保存对动态的数据，并广播给上一个activity
	 * */
	private void sendBroadCastData(GroupTopicModel model) {
		
		Intent mIntent = new Intent(JLXCConst.BROADCAST_NEW_TOPIC_REFRESH);
		mIntent.putExtra(NEW_TOPIC_OK, model);
		// 发送广播
		LocalBroadcastManager.getInstance(CreateGroupActivity.this).sendBroadcast(mIntent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("tmpImageName", tmpImageName);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (null != savedInstanceState) {
			tmpImageName = savedInstanceState.getString("tmpImageName");
		}
	}

}
