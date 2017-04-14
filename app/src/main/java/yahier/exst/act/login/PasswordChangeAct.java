package yahier.exst.act.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.wxapi.MD5Util;

public class PasswordChangeAct extends ThemeActivity implements OnClickListener, OnFinalHttpCallback {
	EditText inputpwdOld;
	EditText et_pwd;
	EditText et_pwd2;
	LoadingDialog loading;
	String smsVerify;
	String phone = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_change);
		setLabel(getString(R.string.me_modify_password));
		Intent it = getIntent();
		//phone = it.getStringExtra("phone");
		phone = SharedUser.getPhone();
		if (phone == null) {
			finish();
			return;
		}
		inputpwdOld = (EditText)findViewById(R.id.inputpwdOld);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		et_pwd2 = (EditText) findViewById(R.id.et_pwd2);
		loading = new LoadingDialog(this);
		((TextView)findViewById(R.id.tv_phone)).setText(phone);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		
		//Intent intent = new Intent(this, PhoneVerifyAct.class);
		//intent.putExtra("phone", phone);
		//startActivityForResult(intent, 99);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 99) {
			if (resultCode == RESULT_OK) {
				smsVerify = data.getStringExtra("smsverify");
				if (null == smsVerify) {
					finish();
				}
			} else {
				finish();
				return;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			String pwdOld = inputpwdOld.getText().toString();
			if (pwdOld.length() == 0) {
				showToast(getString(R.string.me_please_input_current_password));
				return;
			}

			String pwd = et_pwd.getText().toString();
			if (pwd.length() == 0) {
				showToast(getString(R.string.me_please_input_password));
				return;
			}
			if ( pwd.length() < 6){
				showToast(getString(R.string.me_password_not_less_than_6));
				return;
			}
			String pwd2 = et_pwd2.getText().toString();
			if (!pwd.equals(pwd2)) {
				showToast(getString(R.string.me_input_password_differ));
				return;
			}
			modifyPwd(pwdOld,pwd);
			break;

		default:
			break;
		}
	}



	private void modifyPwd(String oldPwd,String pwd) {
		String md5PwdOld = MD5Util.MD5Encode(oldPwd, null);
		String md5Pwd = MD5Util.MD5Encode(pwd, null);

		JSONObject json = new JSONObject();
		//json.put("phone", phone);
		//json.put("phonecode", smsVerify);
		json.put("oldpassword", md5PwdOld);
		json.put("newpassword", md5Pwd);
		new HttpUtil(this, null, loading).postJson(Method.modifyPwd, json.toJSONString(), this);
	}

	@Override
	public void onHttpResponse(String methodName, String json, Object handle) {
		if(methodName.equals(Method.modifyPwd)){
			showToast(getString(R.string.me_password_modify_success));
			finish();
		}
	}

	@Override
	public void onHttpError(String methodName, String msg, Object handle) {
		
	}
}
