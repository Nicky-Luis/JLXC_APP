package com.jlxc.app.personal.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jlxc.app.R;
import com.jlxc.app.base.manager.UserManager;
import com.jlxc.app.base.ui.activity.BaseActivityWithTopBar;
import com.jlxc.app.base.utils.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PersonalSignActivity extends BaseActivityWithTopBar {

	@ViewInject(R.id.sign_edit_text)
	private EditText signEditText;
	
	@OnClick(value={R.id.base_ll_right_btns,R.id.personal_sign_activity})
	private void clickEvent(View view){
		
		switch (view.getId()) {
		case R.id.base_ll_right_btns:
			
			if (signEditText.getText().toString().length()>60) {
				ToastUtil.show(this, "签名不能超过60字╮(╯_╰)╭");
				return;
			}
			
			Intent intent = new Intent();
			intent.putExtra("sign", signEditText.getText().toString().trim());
			setResult(100, intent);
			finishWithRight();
			
			break;
		case R.id.personal_sign_activity:
			//收键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		default:
			break;
		}
		
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_sign;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		setBarText("签名"); 
		addRightBtn("保存");
		signEditText.setText(UserManager.getInstance().getUser().getSign());
		signEditText.setSelection(signEditText.getText().length());
	}

}
