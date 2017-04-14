package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.login.PasswordChangeAct;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class SettingAccountMainAct extends ThemeActivity implements OnClickListener, OnFinalHttpCallback, FinalHttpCallback {

    TextView tv_phone, tv_pay, tv_weixin;
    private static final int REQUEST_BINDWX = 0x1191;
    int authType;
    int hasPayPwd;
    String areaCode;
    String phone;

    private boolean isBind = false; //当前是否属于绑定状态
    private UMShareAPI mShareAPI;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_main);
        setLabel(getString(R.string.me_account_and_safe));
        initViews();
        mShareAPI = UMShareAPI.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViewData();
    }

    private void initViews() {
        //findViewById(R.id.setting_item1).setOnClickListener(this);
        findViewById(R.id.setting_item2).setOnClickListener(this);
        findViewById(R.id.setting_item3).setOnClickListener(this);
        findViewById(R.id.setting_item4).setOnClickListener(this);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_weixin = (TextView) findViewById(R.id.tv_weixin);
        mLoadingDialog = new LoadingDialog(this);
    }

    private void setViewData() {
        UserItem user = SharedUser.getUserItem();
        authType = user.getOtherauthtype();
        areaCode = user.getAreacode();
        phone = user.getTelphone();
        hasPayPwd = user.getHaspaypassword();

        if (phone != null && !phone.equals("")) {
            if (phone.length() >= 11) {
                String mobile = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
                tv_phone.setText("+" + areaCode + " " + mobile);
            } else if (phone.length() >= 7 && phone.length() < 11) {
                String mobile = phone.substring(0, 2) + "****" + phone.substring(6, phone.length());
                tv_phone.setText("+" + areaCode + " " + mobile);
            } else {
                tv_phone.setText("+" + areaCode + " " + phone);
            }
        } else {
            tv_phone.setText("+" + areaCode + " "); //号码为空
        }
        if (hasPayPwd == UserItem.haspaypasswordYes) {
            tv_pay.setText(getString(R.string.me_click_modify));
        } else {
            tv_pay.setText(getString(R.string.me_unset));
        }
        if (authType == UserItem.otherauthtypeWeixin) {
            isBind = true;
            tv_weixin.setText(getString(R.string.me_already_bind));
        } else {
            isBind = false;
            tv_weixin.setText(getString(R.string.me_unbind));
        }
    }

    public void update() {
        getInfo();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.setting_item1:
                intent = new Intent(this, MinePhoneBindAct.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;
            case R.id.setting_item2:
                if (!isBind) {
                    weixinAuth();
                } else {
                    showConfirmUnbindDialog();
                }
//                if (user == null) {
//                    ToastUtil.showToast(this, "正在获取绑定状态");
//                    return;
//                }
//                intent = new Intent(this, WeixinBindAct.class);
//                intent.putExtra("phont", phone);
//                boolean isBind = authType == UserItem.otherauthtypeWeixin;
//                intent.putExtra("isBind", isBind); //是否已经绑定
//                startActivityForResult(intent, REQUEST_BINDWX);
                break;
            case R.id.setting_item3:
                intent = new Intent(this, PasswordChangeAct.class);
                intent.putExtra("phone", phone);
                intent.putExtra("changePwd", 1);
                startActivity(intent);
                break;
            case R.id.setting_item4:
                intent = new Intent(this, PayPasswordSettingAct.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void showConfirmUnbindDialog() {
//        SystemTipDialog dialog = new SystemTipDialog(this);
//        dialog.show();
//        dialog.setTitle("提示");
//        dialog.setContent("确定解除绑定吗？");
//        dialog.setInterface(new SystemTipDialog.ISystemTipDialog() {
//
//            @Override
//            public void onConfirm() {
//                bindOrUnBindWx(null);
//            }
//        });

        TipsDialog.popup(this, getString(R.string.me_is_confirm_remove_bind), getString(R.string.me_cancle), getString(R.string.me_confirm), new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
                bindOrUnBindWx(null);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // 查看用户信息
    public void getInfo() {
        LoadingDialog loading = new LoadingDialog(this);
        //loading.setMessage("正在获取手机绑定信息");
        //loading.setCancelable(false);
//		JSONObject params = new JSONObject();
//		params.put("levelflag", UserItem.type_levelflag_yes);
//		params.put("tagsflag", UserItem.type_tag_no);
        new HttpUtil(this, null, loading).post(Method.userLoginInfo, this);
    }

    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        LogUtil.logD("LogUtil", "methodName:" + methodName + "--json:" + json);
        switch (methodName) {
            case Method.userLoginInfo:
//			UserContainer st = JSONHelper.getObject(json, UserContainer.class);
//			user = st.getUser();
//                user = JSONHelper.getObject(json, UserItem.class);
//                if (user != null) {
//                    app.setUserItem(user);
//                    setViewData();
//                }
                break;
        }
    }

    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
        // TODO Auto-generated method stub
        finish();
    }


    private void bindOrUnBindWx(final WxInfo wxInfo) {
        Payment.getPassword(this, 0, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                Params params = new Params();
                params.put("opentype", 1); //1-微信，2-QQ
                if (!TextUtils.isEmpty(pwd)) {
                    params.put("paypwd", pwd);
                }
                if (wxInfo != null) {
                    String resultOpenId = "";
                    String resultUnionId = "";
                    try {
                        resultOpenId = DesUtils.EncryptAsDoNet(wxInfo.openid);
                        resultUnionId = DesUtils.EncryptAsDoNet(wxInfo.unionid);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    params.put("openid", resultOpenId);
                    params.put("unionid", resultUnionId);
                    params.put("nickname", wxInfo.nickname);
                    params.put("sex", wxInfo.sex);
                    params.put("headimgurl", wxInfo.headimgurl);
                    params.put("country", wxInfo.country);
                    params.put("city", wxInfo.city);
                    params.put("province", wxInfo.province);
                    params.put("language", wxInfo.language);
                    //LogUtil.logE("openid:"+resultOpenId+"--opentype:"+1+"--paypwd:"+pwd + "--headimgurl:" + wxInfo.headimgurl);
                }
                mLoadingDialog.show();
                new HttpEntity(SettingAccountMainAct.this).commonPostData(Method.wxBindOrUnBind, params, SettingAccountMainAct.this);
            }
        });
    }

    @Override
    public void parse(String methodName, String result) {
        mLoadingDialog.dismiss();
        LogUtil.logE("methodName:" + methodName + "--result:" + result);
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        try {
            JSONObject json = new JSONObject(result);
            int issuccess = json.optInt("issuccess");
            if (issuccess != BaseItem.successTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Method.wxBindOrUnBind.equals(methodName)) {
            isBind = !isBind;
            if (isBind) {
                ToastUtil.showToast(this, getString(R.string.me_bind_success));
                tv_weixin.setText(getString(R.string.me_already_bind));
                //SharedUser.putAuthType(this, UserItem.otherauthtypeWeixin);
                SharedUser.putAuthType(UserItem.otherauthtypeWeixin);
            } else {
                ToastUtil.showToast(this, getString(R.string.me_remove_bind_success));
                tv_weixin.setText(getString(R.string.me_unbind));
               // SharedUser.putAuthType(this, UserItem.otherauthtypeNone);
                SharedUser.putAuthType(UserItem.otherauthtypeNone);
            }
        }
    }

    private void weixinAuth() {
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                LogUtil.logE("user info: " + data.toString());
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
                LogUtil.logE("user info: " + data.toString());

                Set<String> keys = data.keySet();
                WxInfo wxInfo = new WxInfo();
                for (String key : keys) {
                    if (key.equals("sex")) {
                        wxInfo.sex = data.get(key).toString();
                    } else if (key.equals("nickname")) {
                        wxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("unionid")) {
                        wxInfo.unionid = data.get(key).toString();
                    } else if (key.equals("province")) {
                        wxInfo.province = data.get(key).toString();
                    } else if (key.equals("openid")) {
                        wxInfo.openid = data.get(key).toString();
                    } else if (key.equals("language")) {
                        wxInfo.language = data.get(key).toString();
                    } else if (key.equals("headimgurl")) {
                        wxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("country")) {
                        wxInfo.country = data.get(key).toString();
                    } else if (key.equals("city")) {
                        wxInfo.city = data.get(key).toString();
                    } else if (key.equals("screen_name")) {
                        wxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("profile_image_url")) {
                        wxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("gender")) {
                        wxInfo.sex = data.get(key).toString();
                    }
                }
                bindOrUnBindWx(wxInfo);
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

    class WxInfo {
        String sex;
        String nickname;
        String unionid;
        String province;
        String openid = "";
        String language;
        String headimgurl;
        String country;
        String city;
    }

    private boolean mIsDestroy;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }
}
