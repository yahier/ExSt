package yahier.exst.act.ad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
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
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.FanliTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.StblWebView;

/**
 * 申请提现
 * Created by Administrator on 2016/9/23.
 */

public class ApplyWithdrawActivity extends ThemeActivity {

    private static final float STANDARD_AMOUNT = 100.0f;
    private static final int ERROR_CODE_OPENID_ISBIND = -8800002;//提现帐号不能绑定多个微信
    //业务操作类型blltype  1=绑定 2=解绑 3=申请提现
    private static final int blltype_3 = 3;

    private TextView mWxNickTv;
    private EditText mWithdrawAmountEt;
    private TextView mWithdrawAllTv;
    private Button mConfirmWithdrawBtn;

    private String mMaxAmount;
    private WithdrawAccount mAccount;
    private float mWithdrawLimit;
    private StblWebView mWebView;

    private TextView mWithdrawMaxAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_withdraw);
        mMaxAmount = getIntent().getStringExtra(KEY.MAX_AMOUNT);
        mAccount = (WithdrawAccount) getIntent().getSerializableExtra(KEY.WITHDRAW_ACCOUNT);
        mWithdrawLimit = (float) SharedPrefUtils.getFromPublicFile(KEY.withdrawlimit, STANDARD_AMOUNT);
        initView();
        checkAcccountStatus(blltype_3,"");
    }

    private void initView() {
        setLabel(getString(R.string.me_apply_withdraw));
        mWxNickTv = (TextView) findViewById(R.id.tv_wechat_nick);
        mWxNickTv.setText(mAccount.displayname);
        mWithdrawAmountEt = (EditText) findViewById(R.id.et_withdraw_amount);
        mWithdrawAllTv = (TextView) findViewById(R.id.tv_withdraw_all);
        mConfirmWithdrawBtn = (Button) findViewById(R.id.btn_confirm_withdraw);

        mWithdrawMaxAmount = (TextView) findViewById(R.id.tv_withdraw_max_amount);
        mWithdrawMaxAmount.setText(String.format(getString(R.string.me_withdraw_amount_s_yuan), mMaxAmount));

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
        String url = (String) SharedPrefUtils.getFromPublicFile(KEY.withdraw_apply, "");
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        } else {
            CommonTask.getCommonDicBackground();
        }

        if (Float.parseFloat(mMaxAmount) >= mWithdrawLimit) {
            mWithdrawAllTv.setEnabled(true);
        }

        mWithdrawAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWithdrawAmountEt.setText(mMaxAmount);
            }
        });

        mConfirmWithdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeWithdraw();
            }
        });

        mWithdrawAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //限制输入金额最多为 limit 位小数
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 2 + 1);
                        mWithdrawAmountEt.setText(s);
                        mWithdrawAmountEt.setSelection(s.length());
                    }
                }
                //第一位输入小数点的话自动变换为 0.
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mWithdrawAmountEt.setText(s);
                    mWithdrawAmountEt.setSelection(2);
                }
                //避免重复输入小数点前的0 ,没有意义
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mWithdrawAmountEt.setText(s.subSequence(0, 1));
                        mWithdrawAmountEt.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mConfirmWithdrawBtn.setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    private void beforeWithdraw() {
        final String amount = mWithdrawAmountEt.getText().toString().trim();
        if (Float.parseFloat(amount) < mWithdrawLimit) {
            ToastUtil.showToast(String.format(getString(R.string.me_minimum_withdraw_d), (int) mWithdrawLimit));
            return;
        }
        if (Float.parseFloat(amount) > Float.parseFloat(mMaxAmount)) {
            ToastUtil.showToast(R.string.me_exceed_available_amount);
            return;
        }
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
                    applyWithdraw(amount, pwd);
                }
            }
        });
    }

    private void applyWithdraw(String withdrawamount, String paypwd) {
        mConfirmWithdrawBtn.setEnabled(false);
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mTaskManager.start(FanliTask.applyWithdraw(withdrawamount, paypwd)
                .setCallback(new HttpTaskCallback<String>(mActivity) {

                    @Override
                    public void onFinish() {
                        mConfirmWithdrawBtn.setEnabled(true);
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        if (e.code == ERROR_CODE_OPENID_ISBIND){
                            showWithdrawErrorTips(e.msg);
                        }else{
                            ToastUtil.showToast(e.msg);
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        Intent intent = new Intent(mActivity, WithdrawSuccessActivity.class);
                        intent.putExtra(KEY.WITHDRAW_AMOUNT, result);
                        startActivity(intent);
                        setResult(RESULT_OK);
                        finish();
                    }
                }));
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
                            showWithdrawErrorTips(e.msg);
                        } else {
                            ToastUtil.showToast(e.msg);
                        }
                    }

                    @Override
                    public void onSuccess(Integer result) {

                    }
                }));
    }

    private void showWithdrawErrorTips(String errorMsg){
        TipsDialog2.popup(this, errorMsg, getString(R.string.cancel), "重新绑定", new TipsDialog2.OnTipsListener() {
            @Override
            public void onConfirm() {
                finish();
                startActivity(new Intent(mActivity, WithdrawAccountActivity.class));
            }

            @Override
            public void onCancel() {

            }
        });
    }

}
