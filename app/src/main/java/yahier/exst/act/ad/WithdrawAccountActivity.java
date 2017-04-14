package yahier.exst.act.ad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.PayPasswordSettingAct;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.model.WithdrawAccount;
import com.stbl.stbl.model.WxInfo;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.FanliTask;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.StblWebView;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 绑定微信账户
 * Created by Administrator on 2016/9/22.
 */

public class WithdrawAccountActivity extends ThemeActivity {

    private static final int REQUEST_CODE_BIND_WEIXIN = 1;
    private static final int ERROR_CODE_WITHDRAW_REVIEWING = -231004;
    private static final int ERROR_CODE_OPENID_ISBIND = -8800001;//提现帐号不能绑定多个微信
    private static final int ERROR_CODE_BIND_TIME_VALID = -8800005;//提现帐号解绑未超过限制时间
    //业务操作类型blltype  1=绑定 2=解绑 3=申请提现
    private static final int blltype_1 = 1;
    private static final int blltype_2 = 2;
    private static final int blltype_3 = 3;

    private TextView mWechatWalletTv;
    private TextView mWechatNickTv;
    private TextView mBindStatusTv;
    private RelativeLayout mWechatLayout;
    private boolean mIsBound;

    private UMShareAPI mShareAPI;

    private WxInfo mWxInfo;
    private WithdrawAccount mAccount;
    private StblWebView mWebView;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_account);
        setLabel(getString(R.string.me_withdraw_account));
        mShareAPI = UMShareAPI.get(this);
        mDialog = new LoadingDialog(this);
        Config.dialogSwitch = false;
        initView();
        if (getIntent().getBooleanExtra(KEY.NO_BOUND, false)) {
            updateView();
        } else {
            getWithdrawAccountList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.dialogSwitch = true;
    }

    private void initView() {
        mWechatWalletTv = (TextView) findViewById(R.id.tv_wechat_wallet);
        mWechatNickTv = (TextView) findViewById(R.id.tv_wechat_nick);
        mBindStatusTv = (TextView) findViewById(R.id.tv_bind_status);
        mWechatLayout = (RelativeLayout) findViewById(R.id.layout_wechat_wallet);
        mWebView = (StblWebView) findViewById(R.id.wv);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.logE("shouldOverrideUrlLoading" + "url:--" + url);
                if (url.startsWith("tel:")) {//支持拨打电话
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("stbl")) {
                    Intent intent = new Intent(mActivity, CommonWebInteact.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                return false;//原本false
            }
        });
        String url = (String) SharedPrefUtils.getFromPublicFile(KEY.withdraw_alert, "");
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        } else {
            CommonTask.getCommonDicBackground();
        }

        if (!CommonShare.isWechatInstalled(this)) {
            mWechatWalletTv.setText(R.string.me_please_install_wechat_before_bind_account);
            mBindStatusTv.setVisibility(View.GONE);
            mWechatLayout.setEnabled(false);
        }
    }

    private void updateView() {
        if (mIsBound) {
            mBindStatusTv.setText(R.string.me_change_bind);
            mWechatNickTv.setVisibility(View.VISIBLE);
            mWechatNickTv.setText(mAccount.displayname);
        } else {
            mBindStatusTv.setText(R.string.me_go_bind);
            mWechatNickTv.setVisibility(View.GONE);
        }

        mWechatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) {
                    showIsUnbindAccountAlertDialog();
                } else {
                    goBind();
                }
            }
        });
    }

    private void getWithdrawAccountList() {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mTaskManager.start(FanliTask.getWithdrawAccountList()
                .setCallback(new HttpTaskCallback<ArrayList<WithdrawAccount>>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<WithdrawAccount> result) {
                        for (WithdrawAccount account : result) {
                            if (account.isbound == WithdrawAccount.BIND_YES) {
                                mIsBound = true;
                                mAccount = account;
                                break;
                            }
                        }
                        updateView();
                    }
                }));
    }

    private void goBind() {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mTaskManager.start(WalletTask.checkPayPassword()
                .setCallback(new HttpTaskCallback<Boolean>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            weixinAuth();
                        } else {
                            showSetPayPwdAlertDialog();
                        }
                    }
                }));
    }

    private void unbind(String pwd) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mTaskManager.start(FanliTask.unbindWithdrawAccount(mAccount.bindingid, pwd)
                .setCallback(new HttpTaskCallback<String>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        if (e.code == ERROR_CODE_WITHDRAW_REVIEWING) {
                            showReviewingCannotUnbindAlertDialog();
                        } else {
                            ToastUtil.showToast(e.msg);
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        ToastUtil.showToast(R.string.me_unbind_done);
                        mIsBound = false;
                        mAccount = null;
                        updateView();
                    }
                }));
    }

    private void showReviewingCannotUnbindAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_your_withdraw_reviewing_unbing_after_review),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {

                    }
                }, true).show();
    }

    private void showSetPayPwdAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_please_set_pay_pwd_first),
                getString(R.string.cancel),
                getString(R.string.me_go_setting),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        Intent intent = new Intent(mActivity, PayPasswordSettingAct.class);
                        startActivity(intent);
                    }
                }).show();
    }

    private void showIsUnbindAccountAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_is_unbind_this_account),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        checkAcccountStatus(blltype_2,"");
                    }
                }).show();
    }

    private void showPayPasswordDialog() {
        Payment.getPassword(this, 0, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                if (pwd == null) {
                    ToastUtil.showToast(R.string.me_request_fail);
                    return;
                }
                if (pwd.equals("")) {
                    showSetPayPwdAlertDialog();
                } else {
                    unbind(pwd);
                }
            }
        });
    }

    private void weixinAuth() {
        mDialog.show();
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                LogUtil.logE("user info: " + data.toString());
                getWeixinInfo();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtil.showToast(R.string.me_auth_fail);
                mDialog.dismiss();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                //授权取消
                mDialog.dismiss();
            }
        });
    }

    private void getWeixinInfo() {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                LogUtil.logE("user info: " + data.toString());
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
                deleteOauth();
                checkAcccountStatus(blltype_1,mWxInfo.openid);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtil.showToast(R.string.me_auth_fail);
                mDialog.dismiss();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                //授权取消
                mDialog.dismiss();
            }
        });
    }

    private void deleteOauth() {
        mShareAPI.deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                mDialog.dismiss();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                mDialog.dismiss();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                mDialog.dismiss();
            }
        });
    }
    //查询用户提现账户状态,业务操作类型blltype  1=绑定 2=解绑 3=申请提现
    private void checkAcccountStatus(final int blltype, String openid){
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mTaskManager.start(FanliTask.checkAccountStatus(openid,blltype)
                .setCallback(new HttpTaskCallback<Integer>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        if (e.code == ERROR_CODE_OPENID_ISBIND) {
                            showBindErrorTips(e.msg);
                        } else if (e.code == ERROR_CODE_BIND_TIME_VALID) {
                            showUBindErrorTips(e.msg);
                        } else {
                            ToastUtil.showToast(e.msg);
                        }
                    }

                    @Override
                    public void onSuccess(Integer result) {
                        if (blltype == blltype_1) {
                            Intent intent = new Intent(mActivity, BindAccountActivity.class);
                            intent.putExtra(KEY.WX_INFO, mWxInfo);
                            startActivityForResult(intent, REQUEST_CODE_BIND_WEIXIN);
                        }else if(blltype == blltype_2){
                            showPayPasswordDialog();
                        }
                    }
                }));
    }

    private void showBindErrorTips(String errorMsg){
        TipsDialog2.popup(this,errorMsg,getString(R.string.btn_ok));
    }
    private void showUBindErrorTips(String errorMsg){
        TipsDialog2.popup(this,errorMsg,getString(R.string.btn_ok));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && mWxInfo != null) {
            switch (requestCode) {
                case REQUEST_CODE_BIND_WEIXIN:
                    mIsBound = true;
                    mAccount = new WithdrawAccount();
                    mAccount.isbound = WithdrawAccount.BIND_YES;
                    mAccount.accounttype = WithdrawAccount.ACCOUNT_TYPE_WECHAT;
                    mAccount.displayname = mWxInfo.nickname;
                    updateView();
                    getWithdrawAccountList();
                    break;
            }
        }
    }

}
