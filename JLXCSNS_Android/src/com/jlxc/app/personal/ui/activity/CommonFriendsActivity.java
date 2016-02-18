package com.jlxc.app.personal.ui.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.manager.BitmapManager;
import com.jlxc.app.base.manager.HttpManager;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.personal.model.CommonFriendsModel;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommonFriendsActivity extends BaseActivityWithTopBar {

	public final static String INTENT_KEY = "uid";
	
	//gridView用adapter
	HelloHaAdapter<CommonFriendsModel> commonAdapter;
	
	@ViewInject(R.id.common_friends_grid_view)
	private GridView commonGridView;
	private int uid;
//	private BitmapUtils bitmapUtils;
	private DisplayImageOptions headImageOptions;
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_common_friends;
	}

	@Override
	protected void setUpView() {
		Intent intent = getIntent();
		setUid(intent.getIntExtra(INTENT_KEY, 0));
//		setBitmapUtils(BitmapManager.getInstance().getHeadPicBitmapUtils(this, R.drawable.default_avatar, true, true));
		headImageOptions = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.loading_default)  
        .showImageOnFail(R.drawable.default_avatar)  
        .cacheInMemory(false)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		setBarText("共同关注的人 ");
		
		initGridView();
		getData();
	}
	//初始化
	private void initGridView() {
		
		commonAdapter = new HelloHaAdapter<CommonFriendsModel>(this, R.layout.common_friends_image_layout) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					final CommonFriendsModel item) {
				ImageView headimImageView = helper.getView(R.id.image_item);
//				bitmapUtils.display(headimImageView, JLXCConst.ATTACHMENT_ADDR+item.getHead_sub_image());
				if (null != item.getHead_sub_image() && item.getHead_sub_image().length() > 0) {
					ImageLoader.getInstance().displayImage(JLXCConst.ATTACHMENT_ADDR+item.getHead_sub_image(), headimImageView, headImageOptions);
				}else {
					headimImageView.setImageResource(R.drawable.default_avatar);
				}
				
				LinearLayout linearLayout = (LinearLayout) helper.getView();
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//跳转到其他人页面
						Intent intent = new Intent(CommonFriendsActivity.this, OtherPersonalActivity.class);
						intent.putExtra(OtherPersonalActivity.INTENT_KEY, item.getFriend_id());
						startActivityWithRight(intent);
					}
				});
				
			}
		};
		commonGridView.setAdapter(commonAdapter);
	}

	//获取数据
	private void getData(){
		String path = JLXCConst.GET_COMMON_FRIENDS_LIST+"?"+"uid="+uid+"&current_id="+UserManager.getInstance().getUser().getUid();
		HttpManager.get(path,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(JLXCConst.HTTP_STATUS);
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(JLXCConst.HTTP_RESULT);
							
							String jsonString = jResult.getString(JLXCConst.HTTP_LIST);
							List<CommonFriendsModel> commonsFriendslist = JSON.parseArray(jsonString, CommonFriendsModel.class);
							if (commonsFriendslist.size()>0) {
								setBarText("共同关注的人"+"（"+commonsFriendslist.size()+"）");								
							}
							
							commonAdapter.replaceAll(commonsFriendslist);
						}
						if (status == JLXCConst.STATUS_FAIL) {
							ToastUtil.show(CommonFriendsActivity.this, jsonResponse.getString(JLXCConst.HTTP_MESSAGE));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(CommonFriendsActivity.this, "网络有毒=_=");
					}

				}, null));
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

}
