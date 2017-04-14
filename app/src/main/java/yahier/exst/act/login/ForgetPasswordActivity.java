package yahier.exst.act.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.WxInfo;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnHttpGetCallback;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 注册页面不再显示微信
 */
public class ForgetPasswordActivity extends ThemeActivity implements OnHttpGetCallback, View.OnClickListener {

    private TextView mCountryCodeTv;
    private EditText mPhoneEt;
    private EditText mSmsCodeEt;
    private TextView mGetCodeTv;

    private boolean mIsDestroy;

    private LoadingDialog mLoadingDialog;

    private ArrayList<CountryPhoneCode> mCountryCodeList;
    private ArrayList<String> mStringList;

    private StringWheelDialog mWheelDialog;

    private CountryPhoneCode mCurrentCode;

    private WxInfo mWxInfo;
    View btnOk;
    View imgDeletePhone;
    //新加图形验证码
    ImageView iv_imgverify;
    EditText input_imgverify;
    String randomid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setLabel(R.string.forget_password);
        initView();
        getCountryCodeList();
    }

    private void initView() {
        mCountryCodeTv = (TextView) findViewById(R.id.tvZone);
        mPhoneEt = (EditText) findViewById(R.id.inputPhone);
        mSmsCodeEt = (EditText) findViewById(R.id.inputCode);
        mGetCodeTv = (TextView) findViewById(R.id.tvReSendOrTime);

        mLoadingDialog = new LoadingDialog(this);
        mWheelDialog = new StringWheelDialog(this);

        mCountryCodeList = new ArrayList<>();
        mStringList = new ArrayList<>();
        imgDeletePhone = findViewById(R.id.imgDeletePhone);
        imgDeletePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoneEt.setText("");
            }
        });
        findViewById(R.id.linZone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelDialog.show();
            }
        });

        mGetCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeGetSmsCode();
            }
        });
        btnOk = findViewById(R.id.btnOk);

        mCountryCodeTv.addTextChangedListener(new TextsListener());
        mPhoneEt.addTextChangedListener(new TextsListener());
        mSmsCodeEt.addTextChangedListener(new TextsListener());


        UserItem userItem = SharedUser.getUserItem();
        mPhoneEt.setText(userItem.getTelphone());
        mPhoneEt.setSelection(mPhoneEt.getText().length());

        String country = SharedUser.getCountry();
        String countryCode = SharedUser.getCountryCode();
        if (!TextUtils.isEmpty(country) && !TextUtils.isEmpty(countryCode)) {
            mCurrentCode = new CountryPhoneCode();
            mCurrentCode.setCountry(country);
            mCurrentCode.setPrefix(countryCode);
            mCountryCodeTv.setText(country + "+" + countryCode);
        }


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeLogin();
            }
        });


        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mCurrentCode = mCountryCodeList.get(position);

                mCountryCodeTv.setText(mCurrentCode.getCountry() + "+" + mCurrentCode.getPrefix());
            }

            @Override
            public void onRetry() {
                getCountryCodeList();
            }
        });
        iv_imgverify = (ImageView) findViewById(R.id.iv_imgverify);
        input_imgverify = (EditText) findViewById(R.id.input_imgverify);
        iv_imgverify.setOnClickListener(this);
        requireImgVerify();//获取图片验证码

    }

    private void requireImgVerify() {
        HttpUtil.getHttpGetBitmap(this, Method.getImgVerify, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_imgverify:
                requireImgVerify();
                break;
        }
    }

    class TextsListener extends TextListener {
        @Override
        public void afterTextChanged(Editable arg0) {
            setBtnCheck();
        }
    }

    void setBtnCheck() {
        String zone = mGetCodeTv.getText().toString().trim();
        String phone = mPhoneEt.getText().toString().trim();
        String code = mSmsCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            imgDeletePhone.setVisibility(View.GONE);
        } else {
            imgDeletePhone.setVisibility(View.VISIBLE);
        }
        if (zone.equals("") || code.equals("") || phone.equals("")) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }

    }

    private void beforeGetSmsCode() {
        if (mCurrentCode == null) {
            ToastUtil.showToast(R.string.please_choose_country_code);
            return;
        }
        String areacode = mCurrentCode.getPrefix();
        String phone = mPhoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(R.string.please_input_phone_number);
            return;
        }


        String imgVerifyCode = input_imgverify.getText().toString();
        if (TextUtils.isEmpty(imgVerifyCode)) {
            ToastUtil.showToast(R.string.please_input_verify_code);
            return;
        }
        getSmsCode(imgVerifyCode, areacode, phone);
    }

    private void beforeLogin() {
        if (mCurrentCode == null) {
            ToastUtil.showToast(R.string.please_choose_country_code);
            return;
        }
        String areacode = mCurrentCode.getPrefix();
        String phone = mPhoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(R.string.please_input_phone_number);
            return;
        }
        String smscode = mSmsCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(smscode)) {
            ToastUtil.showToast(R.string.please_input_verify_code);
            return;
        }
       // SharedUser.put(this, phone, "");
        SharedUser.putUserPhone(phone);
        SharedUser.putCountryCode(mCurrentCode.getCountry(), mCurrentCode.getPrefix());

        smsLogin(areacode, phone, smscode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        if (mWheelDialog.isShowing()) {
            mWheelDialog.dismiss();
        }
        mTimer.cancel();
    }

    private CountDownTimer mTimer = new CountDownTimer(60 * 1000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (mGetCodeTv.isEnabled()) {
                mGetCodeTv.setEnabled(false);
                //mGetCodeTv.setTextColor(0xff969696);
            }
            mGetCodeTv.setText((millisUntilFinished / 1000) + "s");
        }

        @Override
        public void onFinish() {
            mGetCodeTv.setEnabled(true);
            //mGetCodeTv.setTextColor(0xfff35e62);
            mGetCodeTv.setText(R.string.obtain_verify_code);
        }
    };

    private void getCountryCodeList() {
        LoginTask.getCountryCodeList().setCallback(this, mGetCountryCodeCallback).start();
    }

    private SimpleTask.Callback<HashMap<String, Object>> mGetCountryCodeCallback = new SimpleTask.Callback<HashMap<String, Object>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mWheelDialog.showRetry();
        }

        @Override
        public void onCompleted(HashMap<String, Object> result) {
            if (result.containsKey("CountryCodeList")) {
                ArrayList<CountryPhoneCode> codeList = (ArrayList<CountryPhoneCode>) result.get("CountryCodeList");
                if (codeList.size() == 0) {
                    mWheelDialog.showEmpty();
                    return;
                }
                mCountryCodeList.clear();
                mCountryCodeList.addAll(codeList);
            }

            if (result.containsKey("StringList")) {
                ArrayList<String> stringList = (ArrayList<String>) result.get("StringList");
                mStringList.clear();
                mStringList.addAll(stringList);

                mWheelDialog.setData(mStringList);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void getSmsCode(String vertifycode, String areacode, String phone) {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        LoginTask.getSmsCode(vertifycode, randomid, areacode, phone).setCallback(this, mGetSmsCodeCallback).start();
    }

    private SimpleTask.Callback<Integer> mGetSmsCodeCallback = new SimpleTask.Callback<Integer>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
            requireImgVerify();
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.sent_verify_code_please_check);
            mTimer.start();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void smsLogin(String areacode, String phone, String smscode) {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        LoginTask.smsLogin(areacode, phone, smscode).setCallback(this, mSmsLoginCallback).start();
    }

    private SimpleTask.Callback<AuthToken> mSmsLoginCallback = new SimpleTask.Callback<AuthToken>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(AuthToken result) {
            mLoadingDialog.dismiss();
            AuthToken token = result;
            loginSuccess(token);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void wxLogin(WxInfo wx) {
        try {
            String openidEncrypt = DesUtils.EncryptAsDoNet(wx.openid);
            String unionidEncrypt = DesUtils.EncryptAsDoNet(wx.unionid);
            LoginTask.weixinLogin(openidEncrypt, unionidEncrypt).setCallback(this, mWxLoginCallback).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SimpleTask.Callback<AuthToken> mWxLoginCallback = new SimpleTask.Callback<AuthToken>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            if (e.code == ServerError.ERR_LOGIN_WX_NO_RECORD && mWxInfo != null) {
                //保存微信授权得到的信息
                UserItem user = new UserItem();
                user.setImgurl(mWxInfo.headimgurl);
                user.setNickname(mWxInfo.nickname);
                user.setOpenid(mWxInfo.openid);
                if (mWxInfo.sex.equals("1")) {
                    user.setGender(UserItem.gender_boy);
                } else {
                    user.setGender(UserItem.gender_girl);
                }
                // MyApplication.getContext().setUserItem(user);
                // Intent intent = new Intent(ForgetPasswordActivity.this, RegisterWeixinAuthResultAct.class);
                //intent.putExtra(EXTRA.WX_INFO, mWxInfo);
                // startActivity(intent);
            } else {
                ToastUtil.showToast(e.getMessage());
            }
        }

        @Override
        public void onCompleted(AuthToken result) {
            mLoadingDialog.dismiss();
            AuthToken token = result;
            loginSuccess(token);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };


    @Override
    public void onHttpGetImgVerify(String methodName, Bitmap bm, String verifyCode) {
        if (methodName == Method.getImgVerify) {
            iv_imgverify.setImageBitmap(bm);
            this.randomid = verifyCode;
        }
    }

    private void loginSuccess(AuthToken token) {
        SharedToken.putTokenValue(token);
        //SharedToken.putValue(ForgetPasswordActivity.this, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRongyuntoken(),
        //        token.getUserinfo() != null ? token.getUserinfo().getRoleflag() : null, "" + token.getUserinfo().getMasterid(), token.getExpiriestime());
        STBLWession.getInstance().writeLiveRoomToken(token.getLiveRoomToken());
        //MyApplication.getContext().setUserItem(token.getUserinfo());


        int roleFlag;
        try {
            roleFlag = Integer.valueOf(token.getUserinfo().getRoleflag());
            if (UserRole.isNotMaster(roleFlag)) {
                Intent intent = new Intent(ForgetPasswordActivity.this, ChooseMasterAct.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(ForgetPasswordActivity.this, TabHome.class));
            }
        } catch (Exception e) {

        }

        finish();
    }
}
