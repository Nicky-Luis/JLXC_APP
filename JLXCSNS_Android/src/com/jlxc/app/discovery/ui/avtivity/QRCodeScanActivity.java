package com.jlxc.app.discovery.ui.avtivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlxc.app.R;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.JLXCConst;
import com.jlxc.app.base.utils.JLXCUtils;
import com.jlxc.app.base.utils.ToastUtil;
import com.jlxc.app.personal.ui.activity.OtherPersonalActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class QRCodeScanActivity extends BaseActivityWithTopBar {

	private final static int SCANNIN_GREQUEST_CODE = 1;
	@ViewInject(R.id.result)
	TextView mTextView;
	@ViewInject(R.id.qrcode_bitmap)
	ImageView mImageView;
	
	@OnClick({R.id.button1})
	private void methodClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			Intent intent = new Intent();
			intent.setClass(QRCodeScanActivity.this, MipcaCaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;

		default:
			break;
		}
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_qrcode_scan;
	}

	@Override
	protected void setUpView() {
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				String resultString = bundle.getString("result");
				mTextView.setText(resultString);
				mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
				//如果是可以用的
				if (resultString.contains(JLXCConst.JLXC)) {
					String baseUid = resultString.substring(4);
					int uid = JLXCUtils.stringToInt(new String(Base64.decode(baseUid, Base64.DEFAULT)));
					if (uid == UserManager.getInstance().getUser().getUid()) {
						ToastUtil.show(this, "不要没事扫自己玩(ㅎ‸ㅎ)");
					}else {
						Intent intent = new Intent(this, OtherPersonalActivity.class);
						intent.putExtra(OtherPersonalActivity.INTENT_KEY, uid);
						startActivity(intent);
					}
				}
			}
			break;
		}
    }

}
