package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.wxapi.MD5Util;

/**
 * 添加支付密码
 *
 * @author ruilin
 */
public class PayPasswordSettingAct extends ThemeActivity implements OnFinalHttpCallback, OnClickListener {
    EditText et_new;
    EditText et_confirm;
    String phone;
    String smsVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_pay);
        setLabel(getString(R.string.me_pay_password));
        //current = findViewById(R.id.current);

        //et_current = (EditText) findViewById(R.id.et_current);
        et_new = (EditText) findViewById(R.id.et_new);
        et_confirm = (EditText) findViewById(R.id.et_confirm);
        //et_new.setHint("6位数字密码");
        findViewById(R.id.btn_ok).setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//		Intent it = getIntent();
//		phone = it.getStringExtra("phone");
//		if (phone == null) {
//			showToast("请传入手机号码");
//			finish();
//			return;
//		}
        phone = SharedUser.getPhone();
        Intent intent = new Intent(this, PhoneVerifyAct.class);
        intent.putExtra("phone", phone);
        startActivityForResult(intent, 99);
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
        String newStr = et_new.getText().toString().trim();
        String confirmStr = et_confirm.getText().toString().trim();
        if (!StringUtil.isSixNum(confirmStr)) {
            showToast(getString(R.string.me_please_input_6_number_password));
            return;
        }
        if (!newStr.equals(confirmStr)) {
            showToast(getString(R.string.me_password_input_differ));
            return;
        }
        JSONObject json = new JSONObject();
        String pwdMd5 = MD5Util.MD5Encode(et_new.getText().toString(), null);
        json.put("paypwd", pwdMd5);
        json.put("verifycode", smsVerify);//手机验证码
        new HttpUtil(this, null).postJson(Method.userPayPassword, json.toJSONString(), this);
    }

    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        if (methodName.equals(Method.userPayPassword)) {
            //SharedUser.putHaspaypassword(this, UserItem.haspaypasswordYes);
            UserItem user = MyApplication.getContext().getUserItem();
            user.setHaspaypassword(UserItem.haspaypasswordYes);
            SharedUser.putUserValue(user);
            finish();
            showToast(getString(R.string.me_pay_password_set_success));
        }
    }

    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
    }

}
