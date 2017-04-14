package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelContryCode;

import java.util.List;

public class MinePhoneFixAct extends ThemeActivity implements OnFinalHttpCallback, WheelContryCode.OnWheelListener {
    EditText et_phone;
    TextView tvZone;
    int zoneIndex;

    String oldPhone;
    String newPhone;
    String smsVerify;

    String prePhoneCode;//国家码
    List<CountryPhoneCode> listCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_phone_fix);
        setLabel("修改手机号");

        Intent it = getIntent();
        oldPhone = it.getStringExtra("phone");
        if (null == oldPhone) {
            showToast("获取已绑定手机号码失败");
            finish();
            return;
        }
        tvZone = (TextView) findViewById(R.id.tv_zone);
        et_phone = (EditText) findViewById(R.id.et_phone);

        //	list = Arrays.asList(zone);

        findViewById(R.id.common_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (et_phone.getText().toString().trim().length() == 0) {
                    showToast("请输入手机号码");
                    return;
                }
                newPhone = et_phone.getText().toString().trim();
                Intent it = new Intent(MinePhoneFixAct.this, PhoneVerifyAct.class);
                it.putExtra("areacode", prePhoneCode);
                it.putExtra("phone", newPhone);
                MinePhoneFixAct.this.startActivityForResult(it, 99);
            }
        });

        tvZone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listCode == null) {
                    return;
                }
                WheelContryCode wheelTime2 = new WheelContryCode();
                wheelTime2.setOnCodeWheelListener(MinePhoneFixAct.this);
                wheelTime2.chooseTime(MinePhoneFixAct.this, listCode);
            }
        });
        getContriesPhoneCode();
    }


    //获取国家的电话码
    void getContriesPhoneCode() {
        Params params = new Params();
        new HttpEntity(this).commonPostData(Method.getContriesPhoneCode, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {
                    ToastUtil.showToast(MinePhoneFixAct.this, item.getErr().getMsg());
                    return;
                }
                String obj = JSONHelper.getStringFromObject(item.getResult());
                listCode = JSONHelper.getList(obj, CountryPhoneCode.class);
                if (listCode != null && listCode.size() > 0) {
                    tvZone.setText(listCode.get(0).getCountry() + "+" + listCode.get(0).getPrefix());
                    prePhoneCode = listCode.get(0).getPrefix();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99) {
            if (resultCode == RESULT_OK) {
                smsVerify = data.getStringExtra("smsverify");
                if (null == smsVerify) {
                    finish();
                    return;
                }
//				Intent it = new Intent(this, MinePhoneResetPwdAct.class);
//				startActivity(it);
//				finish();
                requireChangePhone();
            } else {
                finish();
                return;
            }
        }
    }

    private void requireChangePhone() {
        JSONObject json = new JSONObject();
        json.put("newphone", newPhone);
        json.put("phonecode", smsVerify);
        json.put("areacode", prePhoneCode);
        new HttpUtil(this, null).postJson(Method.userChangePhone, json.toJSONString(), this);
    }

    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        if (methodName.equals(Method.userChangePhone)) {
            String bindPhone = JSONHelper.getObject(json, String.class);
            if (null != bindPhone) {
                UserItem user = app.getUserItem();
               // SharedUser.putCodePhone(this, prePhoneCode, bindPhone);
                SharedUser.putUserValue(user);
                if (null != user) {
                    user.setTelphone(bindPhone);
                }
                showToast("绑定成功");
                finish();
                return;
            }
        }
    }

    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTagOk(CountryPhoneCode tag) {
        tvZone.setText(tag.getCountry() + "+" + tag.getPrefix());
        prePhoneCode = tag.getPrefix();
    }
}
