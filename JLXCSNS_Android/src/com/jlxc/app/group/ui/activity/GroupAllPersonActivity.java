package com.jlxc.app.group.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.ui.view.CustomListViewDialog;
import com.jlxc.app.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.group.model.GroupPersonModel;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//圈子成员列表 暂时和校园成员列表共用一个页面
public class GroupAllPersonActivity extends BaseActivityWithTopBar {

	//intent
	public static String INTENT_KEY = "topicid";
	// 圈子的人gridview
	@ViewInject(R.id.gv_school_all_person)
	private GridView personListGridView;
	// 筛选按钮
	private TextView filterBtn;
	// 学生数据
	private List<GroupPersonModel> personList;
	// 适配器
	private HelloHaAdapter<GroupPersonModel> personAdapter;
	// 学校代码
	private int topicId;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	@Override
	public int setLayoutId() {
		return R.layout.activity_school_all_person_layout;
	}

	@Override
	protected void setUpView() {
		init();
		GridViewSetup();
		setRightBtn();
		setBarText("成员列表");
		// 查询数据
		getTopicAllPerson();
	}

	/**
	 * 筛选按钮
	 * */
	private void setRightBtn() {

		// 筛选
		List<String> menuList = new ArrayList<String>();
		menuList.add("全部要看");
		menuList.add("只看帅哥");
		menuList.add("只看美女");
		final CustomListViewDialog downDialog = new CustomListViewDialog(
				GroupAllPersonActivity.this, menuList);
		downDialog.setClickCallBack(new ClickCallBack() {

			@Override
			public void Onclick(View view, int which) {

				switch (which) {
				// 全部都看
				case 0:
					personAdapter.replaceAll(personList);
					break;
				// 只看男生
				case 1:
					List<GroupPersonModel> boyList = new ArrayList<GroupPersonModel>();
					for (GroupPersonModel personModel : personList) {
						if (personModel.getSex().equals("0")) {
							boyList.add(personModel);
						}
					}
					personAdapter.replaceAll(boyList);
					break;
				// 只看女生
				case 2:
					List<GroupPersonModel> girlList = new ArrayList<GroupPersonModel>();
					for (GroupPersonModel personModel : personList) {
						if (personModel.getSex().equals("1")) {
							girlList.add(personModel);
						}
					}
					personAdapter.replaceAll(girlList);
					break;

				default:
					break;
				}
				downDialog.cancel();
			}
		});

		filterBtn = this.addRightBtn("筛选");
		filterBtn.setEnabled(false);
		filterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				downDialog.show();
			}
		});
	}

	/**
	 * 初始化
	 * */
	private void init() {
		// 获取学校代码
		Intent intent = this.getIntent();
		topicId = intent.getIntExtra(INTENT_KEY, 0);
		// 获取实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/***
	 * gridview的初始化
	 */
	private void GridViewSetup() {
		personAdapter = new HelloHaAdapter<GroupPersonModel>(
				GroupAllPersonActivity.this,
				R.layout.campus_person_gridview_item_layout, personList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					GroupPersonModel item) {

				// 绑定头像图片
				if (null != item.getHeadSubImage()
						&& item.getHeadSubImage().length() > 0) {
					imgLoader.displayImage(JLXCConst.ATTACHMENT_ADDR + item.getHeadSubImage(),
							(ImageView) helper.getView(R.id.iv_campus_person_head),options);
				} else {
					((ImageView) helper.getView(R.id.iv_campus_person_head))
							.setImageResource(R.drawable.default_avatar);
				}

				// 绑定昵称
				helper.setText(R.id.txt_campus_person_name, item.getUserName());
			}
		};
		personListGridView.setSelector(R.drawable.selector_deep_white_click);
		personListGridView.setAdapter(personAdapter);
		PersonGridViewItemClick personItemClickListener = new PersonGridViewItemClick();
		personListGridView.setOnItemClickListener(personItemClickListener);
	}

	/**
	 * 数据解析
	 * */
	private void JsonToPersonData(List<JSONObject> JPersonList) {
		List<GroupPersonModel> dataList = new ArrayList<GroupPersonModel>();
		// 解析校园的人
		for (JSONObject personObj : JPersonList) {
			GroupPersonModel tempPerson = new GroupPersonModel();
			tempPerson.setContentWithJson(personObj);
			dataList.add(tempPerson);
		}
		personList = dataList;
		personAdapter.replaceAll(personList);
		if (null != JPersonList) {
			JPersonList.clear();
		}
		// 没人时，设置筛选不可用
		if (personAdapter.getCount() > 0) {
			filterBtn.setEnabled(true);
		}
	}

	/**
	 * 获取学校所有学生信息
	 * */
	private void getTopicAllPerson() {
		showLoading("成员获取中..", true);
		String path = JLXCConst.GET_TOPIC_MEMBER_LIST + "?" + "&topic_id="
				+ topicId;

		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							hideLoading();
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 获取数据列表
							List<JSONObject> JPersonList = (List<JSONObject>) jResult
									.get("list");
							JsonToPersonData(JPersonList);
						}

						if (status == JLXCConst.STATUS_FAIL) {
							hideLoading();
							ToastUtil.show(GroupAllPersonActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(GroupAllPersonActivity.this,
								"网络太烂，请检查=_=");
					}

				}, null));
	}

	/**
	 * 学校的人gridview点击监听
	 */
	public class PersonGridViewItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			GroupPersonModel personModel = (GroupPersonModel) parent
					.getAdapter().getItem(position);

			Intent intentUsrMain = new Intent(GroupAllPersonActivity.this,
					OtherPersonalActivity.class);
			intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY,
					JLXCUtils.stringToInt(personModel.getUserId()));
			startActivityWithRight(intentUsrMain);
		}
	}
}
