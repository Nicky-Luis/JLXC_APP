package com.jlxc.app.base.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.jlxc.app.base.helper.JsonRequestCallBack;
import com.jlxc.app.base.helper.LoadDataHandler;
import com.jlxc.app.base.ui.view.CustomAlertDialog;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;

public class NewVersionCheckManager {
	private Context context;
	private Activity activity;
	private int localVersion = 0;

	public NewVersionCheckManager(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		try {
			localVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void checkNewVersion(final boolean showToast, final VersionCallBack callBack) {

		String path = JLXCConst.GET_LASTEST_VERSION+"?sys=1";
		HttpManager.get(path,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse.getInteger(JLXCConst.HTTP_STATUS);
						//回调
						if (null != callBack) {
							callBack.finish();
						}
						
						if (status == JLXCConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse.getJSONObject(JLXCConst.HTTP_RESULT);
							
							int remoteVersion = jResult.getIntValue("version_code");
							//版本地址
							final String versionPath = jResult.getString("version_path");
							
							if (0 != localVersion && remoteVersion > localVersion) {
//								createNewVersionDialog(summary, fileAbsolutePath, isForceUpdate, isClearData);
								
								final CustomAlertDialog confirmDialog = new CustomAlertDialog(
										context, "发现新版本？", "确定", "取消");
								confirmDialog.show();
								confirmDialog.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
											@Override
											public void doConfirm() {
												Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionPath));
												intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												activity.startActivity(intent);	
												confirmDialog.dismiss();
											}

											@Override
											public void doCancel() {
												confirmDialog.dismiss();
											}
										});								
							}else{
								if (showToast) {
									ToastUtil.show(context, "已经是最新版本");	
								}
							}
							
						}else {
							if (showToast) {
								ToastUtil.show(context, "服务器异常 sad");	
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						if (showToast) {
							ToastUtil.show(context, "网络环境不太好。。。");	
						}
						//回调
						if (null != callBack) {
							callBack.finish();
						}
					}

				}, null));
		
	}
	
	//回调
	public interface VersionCallBack{
		public void finish();
	}

//	private void createNewVersionDialog(String summary, final String downloadUrl, int isForceUpdate,
//			final int isClearData) {
//		if (null == context) {
//			return;
//		}
//		final AlertDialog dialog = new AlertDialog.Builder(context).create();
//		dialog.setCancelable(false);
//		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		View v = View.inflate(context, R.layout.update_layout, null);
//		TextView tv_summary = (TextView) v.findViewById(R.id.tv_summary);
//		if (isClearData == 1) {
//			summary = "由于本次更新强化本地数据加密，防止用户数据泄露，因此无法兼容老数据，更新后将清除本地数据，对您造成的不便请谅解，\n" + summary;
//		}
//		tv_summary.setText(summary);
//		Button btn_cancle = (Button) v.findViewById(R.id.btn_cancle);
//		Button btn_update = (Button) v.findViewById(R.id.btn_update);
//		if (isForceUpdate == 1) {
//			// 强制更新
//			btn_cancle.setVisibility(View.GONE);
//		}
//		btn_cancle.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//		btn_update.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				if (isClearData == 1) {
//					// 清空数据库
//					try {
//						// DBHelper.getInstence(context).clearDb();
//						DBHelper.getInstence(context).dropDatabase();
//					} catch (DbException e) {
//						e.printStackTrace();
//					}
//					// 清空首选项
//					UbabyPrefUtil.clear(context);
//				}
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				activity.startActivity(intent);
//			}
//		});
//		dialog.setView(v, 0, 0, 0, 0);
//		dialog.show();
//	}
}
