package com.jlxc.app.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.news.model.LikeModel;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AllLikePersonActivity extends BaseActivityWithTopBar {

	public final static String INTENT_KEY_NEWS_ID = "news_id";

	// 点赞的人的listview
	@ViewInject(R.id.listview_all_like_person)
	private PullToRefreshListView allPersonListView;
	// 数据源
	private List<LikeModel> dataList = new ArrayList<LikeModel>();
	// 适配器
	private HelloHaAdapter<LikeModel> allPersonAdapter;
	// 当前的刷新模式
	private boolean isPullDown = false;
	// 当前的数据页
	private int currentPage = 1;
	// 动态的ID
	private String newsId;
	// 是否是最后一页数据
	private String lastPage = "0";
	// 是否正在请求数据
	private boolean isRequestingData = false;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	@Override
	public int setLayoutId() {
		return R.layout.activity_all_like_person_layout;
	}

	@Override
	protected void setUpView() {
		setBarText("点了赞的小伙伴 (ｏ・_・)");
		init();
		listviewSet();

		// 首次进入获取数据
		isPullDown = true;
		getCampusAllPerson(newsId, "1");
	}

	/**
	 * 初始化
	 * */
	private void init() {
		// 获取学校代码
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		newsId = bundle.getString(INTENT_KEY_NEWS_ID);

		//
		// 获取实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 数据绑定初始化
	 * */
	private void listviewSet() {
		// 设置刷新模式
		allPersonListView.setMode(Mode.BOTH);

		allPersonAdapter = new HelloHaAdapter<LikeModel>(
				AllLikePersonActivity.this,
				R.layout.all_like_person_item_layout, dataList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					LikeModel item) {

				// 绑定头像图片
				// 绑定头像图片
				if (null != item.getHeadSubImage()
						&& item.getHeadSubImage().length() > 0) {
					imgLoader
							.displayImage(
									item.getHeadSubImage(),
									(ImageView) helper
											.getView(R.id.iv_all_like_person_item_head),
									options);
				} else {
					((ImageView) helper
							.getView(R.id.iv_all_like_person_item_head))
							.setImageResource(R.drawable.default_avatar);
				}
				// 绑定昵称
				helper.setText(R.id.tv_all_like_person_item_name,
						item.getName());
			}
		};

		/**
		 * 刷新监听
		 * */
		allPersonListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (!isRequestingData) {
							isRequestingData = true;
							currentPage = 1;
							isPullDown = true;
							getCampusAllPerson(newsId,
									String.valueOf(currentPage));
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (!lastPage.equals("1") && !isRequestingData) {
							isRequestingData = true;
							isPullDown = false;
							getCampusAllPerson(newsId,
									String.valueOf(currentPage));
						}
					}
				});
		/**
		 * 设置底部自动刷新
		 * */
		allPersonListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!lastPage.equals("1")) {
							allPersonListView.setMode(Mode.PULL_FROM_END);
							allPersonListView.setRefreshing(true);
						}
					}
				});

		allPersonListView.setAdapter(allPersonAdapter);

		// 单击
		allPersonListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intentUsrMain = new Intent(AllLikePersonActivity.this,
						OtherPersonalActivity.class);
				intentUsrMain.putExtra(
						OtherPersonalActivity.INTENT_KEY,
						JLXCUtils.stringToInt(allPersonAdapter.getItem(
								position - 1).getUserID()));
				startActivityWithRight(intentUsrMain);
			}
		});
	}

	private void JsonToPersonData(List<JSONObject> jPersonList) {
		List<LikeModel> List = new ArrayList<LikeModel>();
		// 解析校园的人
		for (JSONObject likeObj : jPersonList) {
			LikeModel tempPerson = new LikeModel();
			tempPerson.setContentWithJson(likeObj);
			List.add(tempPerson);
		}
		if (isPullDown) {
			allPersonAdapter.replaceAll(List);
		} else {
			allPersonAdapter.addAll(List);
		}
		if (null != jPersonList) {
			jPersonList.clear();
		}
	}

	/**
	 * 获取学校所有学生信息
	 * */
	private void getCampusAllPerson(String newsID, String page) {
		String path = JLXCConst.GET_NEWS_LIKE_LIST + "?" + "&news_id=" + newsID
				+ "&page=" + page + "&size=";

		LogUtils.i("path=" + path);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							// 获取数据列表
							List<JSONObject> JPersonList = (List<JSONObject>) jResult
									.get("list");
							JsonToPersonData(JPersonList);
							allPersonListView.onRefreshComplete();

							lastPage = jResult.getString("is_last");
							if (lastPage.equals("0")) {
								currentPage++;
								allPersonListView.setMode(Mode.BOTH);
							} else {
								allPersonListView.setMode(Mode.PULL_FROM_START);
							}
							isRequestingData = false;
						}

						if (status == JLXCConst.STATUS_FAIL) {
							allPersonListView.onRefreshComplete();
							if (lastPage.equals("0")) {
								allPersonListView.setMode(Mode.BOTH);
							}
							ToastUtil.show(AllLikePersonActivity.this,
									jsonResponse
											.getString(JLXCConst.HTTP_MESSAGE));
						}
						isRequestingData = false;
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						allPersonListView.onRefreshComplete();
						if (lastPage.equals("0")) {
							allPersonListView.setMode(Mode.BOTH);
						}
						ToastUtil.show(AllLikePersonActivity.this,
								"网络故障，请检查=_=");
						isRequestingData = false;
					}
				}, null));
	}

}
