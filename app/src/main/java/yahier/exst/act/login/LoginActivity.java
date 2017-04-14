package yahier.exst.act.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.WxInfo;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.jpush.JPushManager;
import com.stbl.stbl.wxapi.MD5Util;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends ThemeActivity implements View.OnClickListener {

    private TextView mCountryCodeTv;
    private EditText mPhoneEt;
    private EditText inputPWD;
    private ImageView imgDeletePhone;
    private ImageView imgDeletePwd;
    private View linLogin3;
    ImageView iv_weixin_login;
    private boolean mIsDestroy;

    private ArrayList<CountryPhoneCode> mCountryCodeList;
    private ArrayList<String> mStringList;

    private StringWheelDialog mWheelDialog;

    private CountryPhoneCode mCurrentCode;

    private UMShareAPI mShareAPI;

    private WxInfo mWxInfo;

    private boolean mIsFromGuideActivity;
    View btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mIsFromGuideActivity = getIntent().getBooleanExtra(EXTRA.FROM_GUIDE_ACTIVITY, false);

        initView();
        mShareAPI = UMShareAPI.get(this);
        getCountryCodeList();
        if (!CommonShare.isWechatInstalled(this)) {
            linLogin3.setVisibility(View.GONE);
        }
    }

    private void initView() {
        setLabel(R.string.login_in);
        linLogin3 = findViewById(R.id.linLogin3);
        mCountryCodeTv = (TextView) findViewById(R.id.tvZone);
        mPhoneEt = (EditText) findViewById(R.id.inputPhone);

        inputPWD = (EditText) findViewById(R.id.et_password);
        imgDeletePhone = (ImageView) findViewById(R.id.imgDeletePhone);
        imgDeletePwd = (ImageView) findViewById(R.id.imgDeletePwd);
        btnOk = findViewById(R.id.btn_login);
        iv_weixin_login = (ImageView) findViewById(R.id.iv_weixin_login);

        mPhoneEt.addTextChangedListener(new TextsListener());
        mCountryCodeTv.addTextChangedListener(new TextsListener());
        inputPWD.addTextChangedListener(new TextsListener());

        imgDeletePhone.setOnClickListener(this);
        imgDeletePwd.setOnClickListener(this);
        mWheelDialog = new StringWheelDialog(this);

        mCountryCodeList = new ArrayList<>();
        mStringList = new ArrayList<>();

        findViewById(R.id.linZone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelDialog.show();
            }
        });

        findViewById(R.id.tv_forget_password).setOnClickListener(this);
        mPhoneEt.setText(SharedUser.getPhone());
        mPhoneEt.setSelection(mPhoneEt.getText().length());

        String country = SharedUser.getCountry();
        String countryCode = SharedUser.getCountryCode();
        if (!TextUtils.isEmpty(country) && !TextUtils.isEmpty(countryCode)) {
            mCurrentCode = new CountryPhoneCode();
            mCurrentCode.setCountry(country);
            mCurrentCode.setPrefix(countryCode);
            mCountryCodeTv.setText(country + "+" + countryCode);
        } else {
            SharedUser.putCountryCode("中国", "86");
            country = SharedUser.getCountry();
            countryCode = SharedUser.getCountryCode();
            mCurrentCode = new CountryPhoneCode();
            mCurrentCode.setCountry(country);
            mCurrentCode.setPrefix(countryCode);
            mCountryCodeTv.setText(country + "+" + countryCode);
        }


        btnOk.setOnClickListener(this);
        iv_weixin_login.setOnClickListener(this);
        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                if (mCountryCodeList.size() == 0) return;
                mCurrentCode = mCountryCodeList.get(position);
                SharedUser.putCountryCode(mCurrentCode.getCountry(), mCurrentCode.getPrefix());
                mCountryCodeTv.setText(mCurrentCode.getCountry() + "+" + mCurrentCode.getPrefix());
            }

            @Override
            public void onRetry() {
                getCountryCodeList();
            }
        });


    }


    class TextsListener extends TextListener {
        @Override
        public void afterTextChanged(Editable arg0) {
            setBtnCheck();
        }
    }

    void setBtnCheck() {
        String code = mCountryCodeTv.getText().toString().trim();
        String phone = mPhoneEt.getText().toString().toString();
        String pwd = inputPWD.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            imgDeletePhone.setVisibility(View.GONE);
        } else {
            imgDeletePhone.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(pwd)) {
            imgDeletePwd.setVisibility(View.GONE);
        } else {
            imgDeletePwd.setVisibility(View.VISIBLE);
        }


        if (code.equals("") || pwd.equals("") || phone.equals("")) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }

    }

//    @Override
//    public void onBackPressed() {
//        LogUtil.logE("onBackPressed");
//        if (WaitingDialog.isShow()) {
//            WaitingDialog.dismiss();
//        } else {
//            super.onBackPressed();
//        }
//
//    }

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
        String password = inputPWD.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(R.string.please_input_account_pwd);
            return;
        }
        if (password.length() < 6) {
            ToastUtil.showToast(R.string.pwd_must_more_than_6);
            return;
        }
        //SharedUser.put(this, phone, "");
        SharedUser.putUserPhone(phone);
        String pwdMd5 = MD5Util.MD5Encode(password, null);
        login(areacode, phone, pwdMd5);
    }

    private void weixinAuth() {
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                if (data != null)
                    LogUtil.logE("user info: " + data.toString());
                else
                    LogUtil.logE("user info: data is null");
                getWeixinInfo();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtil.showToast(R.string.me_auth_fail);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                //授权取消
            }
        });
    }

    private void getWeixinInfo() {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                if (data != null)
                    LogUtil.logE("user info: " + data.toString());
                else
                    LogUtil.logE("user info: data is null");

                Set<String> keys = data.keySet();
                mWxInfo = new WxInfo();
                for (String key : keys) {
                    if (key.equals("sex")) {
                        mWxInfo.sex = data.get(key).toString();
                    } else if (key.equals("nickname")) {
                        mWxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("unionid")) {
                        mWxInfo.unionid = data.get(key).toString();
                    } else if (key.equals("province")) {
                        mWxInfo.province = data.get(key).toString();
                    } else if (key.equals("openid")) {
                        mWxInfo.openid = data.get(key).toString();
                    } else if (key.equals("language")) {
                        mWxInfo.language = data.get(key).toString();
                    } else if (key.equals("headimgurl")) {
                        mWxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("country")) {
                        mWxInfo.country = data.get(key).toString();
                    } else if (key.equals("city")) {
                        mWxInfo.city = data.get(key).toString();
                    } else if (key.equals("screen_name")) {
                        mWxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("profile_image_url")) {
                        mWxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("gender")) {
                        mWxInfo.sex = data.get(key).toString();
                    }
                }
                wxLogin(mWxInfo);
                deleteOauth();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtil.showToast(R.string.me_auth_fail);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                //授权取消
            }
        });
    }

    private void deleteOauth() {
        mShareAPI.deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        WaitingDialog.dismiss();
        if (mWheelDialog.isShowing()) {
            mWheelDialog.dismiss();
        }
    }

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

    private void login(String areacode, String phone, String pwd) {
        WaitingDialog.show(this, R.string.logging);
        LoginTask.pwdLogin(areacode, phone, pwd).setCallback(this, mLoginCallback).start();
    }

    private SimpleTask.Callback<AuthToken> mLoginCallback = new SimpleTask.Callback<AuthToken>() {
        @Override
        public void onError(TaskError e) {
            WaitingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(AuthToken result) {
            WaitingDialog.dismiss();
            AuthToken token = result;
            loginSuccess(token);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    /**
     * 微信登录
     *
     * @param wx
     */
    private void wxLogin(WxInfo wx) {
        try {
            String openidEncrypt = DesUtils.EncryptAsDoNet(wx.openid);
            String unionidEncrypt = DesUtils.EncryptAsDoNet(wx.unionid);
            LogUtil.logE("openidEncrypt:" + openidEncrypt);
            LogUtil.logE("unionidEncrypt:" + unionidEncrypt);
            LoginTask.weixinLogin(openidEncrypt, unionidEncrypt).setCallback(this, mWxLoginCallback).start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private SimpleTask.Callback<AuthToken> mWxLoginCallback = new SimpleTask.Callback<AuthToken>() {
        @Override
        public void onError(TaskError e) {
            WaitingDialog.dismiss();
            if (e.code == ServerError.ERR_LOGIN_WX_NO_RECORD && mWxInfo != null) {
                //保存微信授权得到的信息
                UserItem user = new UserItem();
                user.setImgurl(mWxInfo.headimgurl);
                user.setNickname(mWxInfo.nickname);
                user.setOpenid(mWxInfo.openid);
                user.setUnionid(mWxInfo.unionid);
                if (mWxInfo.sex.equals("1")) {
                    //user.setGender(UserItem.gender_boy);
                } else {
                    //user.setGender(UserItem.gender_girl);
                }
                //不再进入到微信结果页
                SharedUser.putUserValue(user);
                Intent intent = new Intent(LoginActivity.this, RegisterStep1ActNew.class);
                //intent.putExtra(KEY.USER_ITEM, user);
                startActivity(intent);
            } else {
                ToastUtil.showToast(e.getMessage());
            }
        }

        @Override
        public void onCompleted(AuthToken result) {
            WaitingDialog.dismiss();
            AuthToken token = result;
            loginSuccess(token);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void loginSuccess(AuthToken token) {
        //SharedToken.putValue(LoginActivity.this, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRongyuntoken(),
        //        token.getUserinfo() != null ? token.getRoleflag() : null, "" + token.getUserinfo().getMasterid(), token.getExpiriestime());
        SharedToken.putTokenValue(token);
        STBLWession.getInstance().writeLiveRoomToken(token.getLiveRoomToken());
        //MyApplication.getContext().setUserItem(token.getUserinfo());
        JPushManager.getInstance().setAlias(this);
        int roleFlag;
        try {
            roleFlag = Integer.valueOf(token.getUserinfo().getRoleflag());
            if (UserRole.isNotMaster(roleFlag)) {
                Intent intent = new Intent(LoginActivity.this, ChooseMasterAct.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(LoginActivity.this, TabHome.class));
            }

            //CommonTask.initFriendDB(GroupMemberList.typeRequestNoneGroup, 0, GroupMemberList.hasselfNo);
        } catch(Exception e) {

        }

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!mIsFromGuideActivity) {
            startActivity(new Intent(this, GuideActivity.class));
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgDeletePhone:
                mPhoneEt.setText("");
                break;
            case R.id.imgDeletePwd:
                inputPWD.setText("");
                break;
            case R.id.btn_login:
                beforeLogin();
                break;
            case R.id.iv_weixin_login:
                weixinAuth();
                break;
            case R.id.tv_forget_password:
                String phone = mPhoneEt.getText().toString();
                SharedUser.putUserPhone(phone);
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
