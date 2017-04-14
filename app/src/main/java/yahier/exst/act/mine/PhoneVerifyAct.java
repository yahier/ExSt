package yahier.exst.act.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.OnHttpGetCallback;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;

/**
 * 短信验证
 * @author ruilin
 *
 */
public class PhoneVerifyAct extends ThemeActivity implements OnClickListener, OnFinalHttpCallback, OnHttpGetCallback {
	String phone;
	ImageView iv_imgverify;
	EditText et_verify;
	EditText et_imgverify;
	TextView tv_send;
	String verifyCode;
	String smsVerify;
	int seconds = 0;
	LoadingDialog loading;
	String areacode;//国家码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_verify);
		setLabel(getString(R.string.me_phone_verify));
		final Activity act = this;
		Intent it = getIntent();

		areacode = getUserItem().getAreacode();
		phone =  it.getStringExtra("phone");
		if (phone == null) {
			finish();
			return;
		}
		loading = new LoadingDialog(this);
		((TextView)findViewById(R.id.tv_phone)).setText("+"+areacode+" "+phone);
		iv_imgverify = (ImageView) findViewById(R.id.iv_imgverify);
		iv_imgverify.setOnClickListener(this);
		et_verify = (EditText) findViewById(R.id.et_verify);
		et_imgverify = (EditText) findViewById(R.id.et_imgverify);
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_send.setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		requireImgVerify();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok: {
			String inputSmsVerify = et_verify.getText().toString();
			if (inputSmsVerify.length() == 0) {
				ToastUtil.showToast(this, getString(R.string.me_please_input_message_verify_code));
				return;
			}
			requirePhoneVerify(phone, inputSmsVerify);
		}
			break;
		case R.id.tv_send:
			if (seconds > 0) {
				return;
			}
			if (null == verifyCode) {
				ToastUtil.showToast(this, getString(R.string.me_please_input_verify_code));
				requireImgVerify();
				return;
			}
			String inputImgVerify = et_imgverify.getText().toString();
			requireSmsVerify(phone, "", verifyCode, inputImgVerify);
			break;
		case R.id.iv_imgverify:
			requireImgVerify();
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler();
	
	private void setCountDown(int seconds) {
		if (seconds < 0)
			return;
		this.seconds = seconds;
		tv_send.setText(String.format(getString(R.string.me_d_second), seconds));
		handler.postDelayed(runnable, 1000);
	}
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			seconds--;
			tv_send.setText(String.format(getString(R.string.me_d_second), seconds));
			if (seconds <= 0) {
				// 计时结束
				tv_send.setText(R.string.me_send_verify_code);
			} else {
				handler.postDelayed(this, 1000);
			}
		}
	};
	
	public void requirePhoneVerify(String phone, String phonecode) {
		JSONObject json = new JSONObject();
		json.put("areacode", areacode);//国家区号
		json.put("phone", phone);
		json.put("phonecode", phonecode);
		smsVerify = phonecode;
		new HttpUtil(this, null, loading).postJson(Method.phoneVerify, json.toJSONString(), this);
	}
	
	public void requireSmsVerify(String phone, String phonecode, String verifycode, String inputVerify) {
		JSONObject json = new JSONObject();
		json.put("areacode", areacode);//国家区号
		json.put("phone", phone);
		json.put("phonecode", phonecode);
		json.put("randomid", verifycode);
		json.put("vertifycode", inputVerify);
		new HttpUtil(this, null, loading).postJson(Method.getSmsVerify, json.toJSONString(), this);
	}
	
	private void requireImgVerify() {
		HttpUtil.getHttpGetBitmap(this, Method.getImgVerify, this);
	}

	@Override
	public void onHttpResponse(String methodName, String json, Object handle) {
		if(methodName.equals(Method.getSmsVerify)){
			setCountDown(60);
		}else if(methodName.equals(Method.phoneVerify)){
			Intent it = new Intent();
			it.putExtra("smsverify", smsVerify);
			setResult(RESULT_OK, it);
			finish();
		}
	}

	@Override
	public void onHttpError(String methodName, String msg, Object handle) {
		// TODO Auto-generated method stub
		smsVerify = null;
	}
	
	@Override
	public void onHttpGetImgVerify(String methodName, Bitmap bm, String verifyCode) {
		if (methodName == Method.getImgVerify) {
			iv_imgverify.setImageBitmap(bm);
			this.verifyCode = verifyCode;
		}
	}
}
