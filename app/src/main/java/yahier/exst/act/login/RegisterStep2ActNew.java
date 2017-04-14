package yahier.exst.act.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnHttpGetCallback;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.wxapi.MD5Util;

import java.io.File;

/**
 * Created by lenovo on 2016/3/12.
 */
public class RegisterStep2ActNew extends ThemeActivity implements View.OnClickListener, FinalHttpCallback, OnHttpGetCallback {
    EditText inputCode, inputPWD;
    TextView btnOk, tvUserProtocal;
    //final String UserProtocal = "<font color='#999999'>注册即表示同意</font><font color='#e7be09'><b>《用户协议》</b></font>";
    TextView tvReSendOrTime;
    final int secondCount = 60;
    UserItem user;
    ImageView imgPwdVisibility;
    //新加图形验证码
    ImageView iv_imgverify;
    EditText input_imgverify;
    String randomid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_step2_new);
        setLabel(R.string.register);
        user = ((MyApplication) getApplication()).getUserItem();
        tvUserProtocal = (TextView) findViewById(R.id.tvUserProtocal);
        inputCode = (EditText) findViewById(R.id.inputCode);
        inputPWD = (EditText) findViewById(R.id.inputPwd);
        tvUserProtocal.setText(Html.fromHtml(getString(R.string.user_protical_tip)));
        tvUserProtocal.setOnClickListener(this);
        btnOk = (TextView) findViewById(R.id.btnOk);
        tvReSendOrTime = (TextView) findViewById(R.id.tvReSendOrTime);
        tvReSendOrTime.setOnClickListener(this);
        imgPwdVisibility = (ImageView) findViewById(R.id.imgPwdVisibility);
        imgPwdVisibility.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        RegisterActList.add(this);
        inputCode.addTextChangedListener(new TextsListener());
        inputPWD.addTextChangedListener(new TextsListener());
        iv_imgverify = (ImageView) findViewById(R.id.iv_imgverify);
        input_imgverify = (EditText) findViewById(R.id.input_imgverify);
        iv_imgverify.setOnClickListener(this);
        requireImgVerify();//获取图片验证码
    }


    class TextsListener extends TextListener {
        @Override
        public void afterTextChanged(Editable arg0) {
            setBtnCheck();
        }
    }

    void setBtnCheck() {
        String code = inputCode.getText().toString().trim();
        String pwd = inputPWD.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            imgPwdVisibility.setVisibility(View.GONE);
        } else {
            imgPwdVisibility.setVisibility(View.VISIBLE);
        }

        int pwdLength = pwd.length();
        if (code.equals("") || (pwdLength < 6 || pwdLength > 20)) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }


    }

    //获取验证码
    void getVerifyCode() {
        Params params = new Params();
        params.put("areacode", user.getPhonePrex());
        params.put("phone", user.getTelphone());
        //新加图形验证码
        String imgVerifyCode = input_imgverify.getText().toString();
        params.put("vertifycode", imgVerifyCode);
        params.put("randomid", randomid);
        new HttpEntity(this).commonPostData(Method.getSmsCode, params, this);
    }

    private void requireImgVerify() {
        HttpUtil.getHttpGetBitmap(this, Method.getImgVerify, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RegisterActList.remove(this);
    }

    @Override
    public void onClick(final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        },500);
        view.setEnabled(false);
        switch(view.getId()) {
            case R.id.btnOk:
                register();
                break;
            case R.id.tvUserProtocal:
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.regis_instrod, "");
                Intent intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            case R.id.tvReSendOrTime:
                String imgVerifyCode = input_imgverify.getText().toString();
                if (TextUtils.isEmpty(imgVerifyCode)) {
                    ToastUtil.showToast(R.string.please_input_verify_code);
                    return;
                }
                tvReSendOrTime.setEnabled(false);
                setCountDown(secondCount);
                getVerifyCode();
                break;
            case R.id.imgPwdVisibility:
                inputPWD.setText("");
                break;
            case R.id.iv_imgverify:
                requireImgVerify();
                break;
        }
    }

    private boolean mIsSendSmsSuccess;
    int seconds;
    Handler handler = new Handler();

    private void setCountDown(int seconds) {
        if (seconds < 0) return;
        this.seconds = seconds;
        tvReSendOrTime.setText(seconds + "s");
        handler.postDelayed(runnable, 1000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            tvReSendOrTime.setText(seconds + "s");
            if (seconds <= 0) {
                tvReSendOrTime.setText(mIsSendSmsSuccess ? R.string.resend_verify_code :R.string.obtain_sms_verify_code);
                tvReSendOrTime.setEnabled(true);
                tvReSendOrTime.setOnClickListener(RegisterStep2ActNew.this);
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void register() {
        String pwd = inputPWD.getText().toString().trim();
        String smscode = inputCode.getText().toString();
        String pwdMd5 = MD5Util.MD5Encode(pwd, null);
        Params params = new Params();
        params.put("areacode", user.getPhonePrex());
        params.put("phone", user.getTelphone());
        params.put("pwd", pwdMd5);
        params.put("nickname", user.getNickname());
        params.put("verifycode", smscode);

        if (!TextUtils.isEmpty(user.getImgurl())) {
            params.put("headimgurl", user.getImgurl());
            LogUtil.logE("register headimgurl", "user:" + user.getImgurl());

        }
        if (!TextUtils.isEmpty(user.getOpenid()) && !TextUtils.isEmpty(user.getUnionid())) {
            params.put("opentype", UserItem.otherauthtypeWeixin);  //授权类型 1：微信 2：QQ
            try {
                LogUtil.logE("微信加密前密后:" + user.getOpenid() + "_________" + user.getUnionid());
                params.put("openid", DesUtils.EncryptAsDoNet(user.getOpenid())); //authbusinessid 授权业务id 例如：微信openid.需要AES加密
                params.put("unionid", DesUtils.EncryptAsDoNet(user.getUnionid())); //授权业务id 例如：微信openid.需要AES加密
                LogUtil.logE("微信openId加密后:" + DesUtils.EncryptAsDoNet(user.getOpenid()) + "_________" + DesUtils.EncryptAsDoNet(user.getUnionid()));
            } catch(Exception e) {
                ToastUtil.showToast(R.string.data_error);
                return;
            }
        }


        WaitingDialog.show(this, R.string.registering,false);
        new HttpEntity(this).commonPostData(Method.userRegisterByPhoneNew, params, this);
    }

    public void uoloadHeadImg() {
        String imgPath = getIntent().getStringExtra("imgUserPath");
        if (imgPath == null) return;
        Params params = new Params();
        params.put("pic", new File(imgPath));
        new HttpEntity(this).commonPostImg(Method.userUploadHeadImg, params, this);

    }

    @Override
    public void onHttpGetImgVerify(String methodName, Bitmap bm, String randomid) {
        if (methodName == Method.getImgVerify) {
            iv_imgverify.setImageBitmap(bm);
            this.randomid = randomid;
        }
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        // 错误提示
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            switch(methodName) {
                case Method.userRegisterByPhoneNew:
                    WaitingDialog.dismiss();
                    break;
                case Method.getSmsCode:
                    tvReSendOrTime.setText(mIsSendSmsSuccess ? R.string.resend_verify_code :R.string.obtain_sms_verify_code);
                    seconds = 0;
                    handler.removeCallbacks(runnable);
                    tvReSendOrTime.setOnClickListener(this);
                    tvReSendOrTime.setEnabled(true);
                    requireImgVerify();
                    break;
                case Method.userUploadHeadImg:
                    ToastUtil.showToast(R.string.failed_to_upload_head_img);
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.getSmsCode:
                mIsSendSmsSuccess = true;
                ToastUtil.showToast(R.string.sent_verify_code_please_check);
                //setCountDown(secondCount);
                tvReSendOrTime.setOnClickListener(null);
                tvReSendOrTime.setEnabled(false);
                break;
            case Method.userRegisterByPhoneNew:
                WaitingDialog.dismiss();
                ToastUtil.showToast(R.string.register_success);
                AuthToken token = JSONHelper.getObject(obj, AuthToken.class);
                SharedToken.putTokenValue(token);
                //SharedToken.putValue(this, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRongyuntoken(),
                //        token.getUserinfo() != null ? token.getUserinfo().getRoleflag() : null, "" + token.getUserinfo().getMasterid(), token.getExpiriestime());
                STBLWession.getInstance().writeLiveRoomToken(token.getLiveRoomToken());
                //MyApplication app = (MyApplication) this.getApplication();
                //app.setUserItem(token.getUserinfo());
                //微信授权后会有头像
                uoloadHeadImg();
                enterAct(ChooseMasterAct.class);
                RegisterActList.clearAll();
                break;
            case Method.userUploadHeadImg:
                ToastUtil.showToast(R.string.head_img_upload_success);
                // ImgUrl imgUrl = JSONHelper.getObject(obj, ImgUrl.class);
                // UserItem userItem = app.getUserItem();
                // userItem.setImgurl(imgUrl.getLarge());//null pointer
                // app.setUserItem(userItem);
                //enterAct(RegisterChooseMaster.class);
                break;
        }
    }
}