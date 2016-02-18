package com.jlxc.app.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.jlxc.app.R;
import com.jlxc.app.base.adapter.HelloHaAdapter;
import com.jlxc.app.base.adapter.HelloHaBaseAdapterHelper;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.LogUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.news.model.SchoolModel;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ChoiceLocationActivity extends BaseActivityWithTopBar implements AMapLocationListener, OnPoiSearchListener 
,Runnable{

	// 位置列表listview
	@ViewInject(R.id.location_refresh_list)
	private PullToRefreshListView loactionListView;	
	
	// 位置listview的适配器
	private HelloHaAdapter<String> locationAdapter;
	// 位置数据列表
	private List<String> mDatas = new ArrayList<String>();
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = false;
	
	//定位用
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;
	private List<PoiItem> poiItems;// poi数据
	
	@OnClick({R.id.no_postion_btn})
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.no_postion_btn:
			//不显示
			Intent intent = new Intent();
			intent.putExtra("location", "");
			//选择location返回值
			setResult(100, intent);
			// 停止定位
			finishWithRight();
			stopLocation();
			break;
		default:
			break;
		}
	}
	
	@Override
	public int setLayoutId() {
		return R.layout.activity_choice_location;
	}
	@Override
	protected void setUpView() {
		
		setBarText("我在这里");
		initListViewSet();
		// 设置为底部刷新模式
		loactionListView.setMode(Mode.BOTH);
		//设置定位
		aMapLocManager = LocationManagerProxy.getInstance(this);
		
		showLoading("定位中...", true);
		///////////定位////////////////
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		
		aMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}

	/***
	 * listview的设置
	 */
	private void initListViewSet() {
		locationAdapter = new HelloHaAdapter<String>(
				ChoiceLocationActivity.this, R.layout.location_listitem_adapter,
				mDatas) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					String item) {
				
				helper.setText(R.id.location_name_textView, item);
			}
		};

		// 适配器绑定
		loactionListView.setAdapter(locationAdapter);
		loactionListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		loactionListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				if (aMapLocation!=null) {
					LatLonPoint latLonPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
					doSearchQuery(latLonPoint);	
				}else {
					loactionListView.onRefreshComplete();
					loactionListView.setMode(Mode.BOTH);
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
//				// 上拉刷新
//				isPullDowm = false;
//				if (aMapLocation!=null) {
//					nextSearch();	
//				}else {
//					loactionListView.onRefreshComplete();
//					loactionListView.setMode(Mode.BOTH);
//				}
			}

		});

		/**
		 * 设置点击item到事件
		 * */
		loactionListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 隐藏输入键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

				Intent intent = new Intent();
				intent.putExtra("location", locationAdapter.getItem(position-1));
				//选择location返回值
				setResult(100, intent);
				// 停止定位
				finishWithRight();
				stopLocation();
			}
		});

		// 设置底部自动刷新
		loactionListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// 底部自动加载
						loactionListView.setMode(Mode.PULL_FROM_END);
						loactionListView.setRefreshing(true);
						isPullDowm = false;
						if (aMapLocation!=null) {
							nextSearch();	
						}else {
							loactionListView.onRefreshComplete();
							loactionListView.setMode(Mode.BOTH);
						}
					}
				});
	}

	/**
	 * 返回操作
	 * */
	private void back() {
		// 停止定位
		finishWithRight();
		stopLocation();
	}

	/**
	 * 重写返回操作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopLocation();// 停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destroy();
		}
		aMapLocManager = null;
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			hideLoading();
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
//			String cityCode = "";
//			String desc = "";
//			Bundle locBundle = location.getExtras();
//			if (locBundle != null) {
//				cityCode = locBundle.getString("citycode");
//				desc = locBundle.getString("desc");
//			}
			LatLonPoint latLonPoint = new LatLonPoint(geoLat, geoLng);
			doSearchQuery(latLonPoint);
			stopLocation();
			
//			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
//					+ "\n精    度    :" + location.getAccuracy() + "米"
//					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
//					+ location.getTime() + "\n城市编码:"
//					+ cityCode + "\n位置描述:" + desc + "\n省:"
//					+ location.getProvince() + "\n市:" + location.getCity()
//					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
//					.getAdCode());
			
//			myLocation.setText(str);
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			ToastUtil.show(this, "定位失败...");
			stopLocation();// 销毁掉定位
		}
	}
	
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//					List<SuggestionCity> suggestionCities = poiResult
//							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						
						//数据处理
						List<String> newDatas = new ArrayList<String>();
						for (PoiItem item : poiItems) {
							newDatas.add(item.getTitle());
						}
						if (isPullDowm) {
							locationAdapter.replaceAll(newDatas);
						} else {
							locationAdapter.addAll(newDatas);
						}
					} else {
						ToastUtil
						.show(this, "没结果");
					}
				}
			} else {
				ToastUtil
						.show(this, "没结果");
			}
		} else if (rCode == 27) {
			ToastUtil
					.show(this, "网络有毒");
		} else if (rCode == 32) {
			ToastUtil.show(this, "错误key");
		} else {
		}
		
		loactionListView.onRefreshComplete();
		loactionListView.setMode(Mode.BOTH);
	}
	
	
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(LatLonPoint lp) {
		currentPage = 0;
		query = new PoiSearch.Query("", "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(30);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页
		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 2000, true));//
			// 设置搜索区域为以lp点为圆心，其周围2000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}else {
			loactionListView.onRefreshComplete();
			loactionListView.setMode(Mode.BOTH);
		}
	}

	/**
	 * 点击下一页poi搜索
	 */
	public void nextSearch() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;

				query.setPageNum(currentPage);// 设置查后一页
				poiSearch.searchPOIAsyn();
			} else {
				ToastUtil
						.show(this, "没结果");
			}
		}else {
			loactionListView.onRefreshComplete();
			loactionListView.setMode(Mode.BOTH);
		}
	}

}
