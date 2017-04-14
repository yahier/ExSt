package yahier.exst.act.redpacket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.dialog.HandlingDialog;
import com.stbl.stbl.dialog.PayConfirmDialog;
import com.stbl.stbl.dialog.PayResultDialog;
import com.stbl.stbl.dialog.PayUnconfirmedDialog;
import com.stbl.stbl.dialog.RedPacketPayDialog;
import com.stbl.stbl.external.alipay.AliPay;
import com.stbl.stbl.item.redpacket.RedpacketSetting;
import com.stbl.stbl.item.redpacket.RedpacketSettingAll;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.model.RedpacketOrder;
import com.stbl.stbl.task.RedPacketTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.wxapi.WeixinPay;

/**
 * Created by Administrator on 2016/12/27.
 */

public class SendRedPacketActivity extends BaseActivity {

    private static final int RP_TYPE_NORMAL = 0;
    private static final int RP_TYPE_RANDOM = 1;

    private static final int PAY_STATUS_WAITING_PAY = 1000;
    private static final int PAY_STATUS_ALREADY_PAY = 1200;

    private static final int CONFIRM_PAY_TIMES = 3;

    private String buid;
    private int redpackettype = RP_TYPE_RANDOM;
    private int redpacketsize;
    private double redpacketmoney;
    private String redpacketmsg;
    private int paytype;

    private TextView mPopMsgTv;
    private TextView mCountTv;
    private EditText mCountEt;
    private TextView mCountUnitTv;
    private TextView mAmountTv;
    private TextView mPinTv;
    private EditText mAmountEt;
    private TextView mAmountUnitTv;
    private TextView mCurrRpTypeTv;
    private TextView mChangeTypeTv;
    private EditText mGreetingEt;
    private TextView mMoneyTv;
    private Button mPutMoneyBtn;

    private RedPacketPayDialog mPayDialog;
    private PayConfirmDialog mPayConfirmDialog;
    private HandlingDialog mHandlingDialog;
    private boolean mWxPaying;

    private String redpacketid;

    private int mConfirmTimes;

    private RedpacketSettingAll mRedpacketSettingAll;
    private RedpacketSetting mRedpacketSetting;
    private int count;
    private double amount;
    int redpacketmsglen;//祝福语长度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buid = getIntent().getStringExtra(KEY.SQUARE_ID);
        if (TextUtils.isEmpty(buid)) {
            finish();
            return;
        }
        mRedpacketSettingAll = SharedPrefUtils.getRedpacketSettingAll();
        if (mRedpacketSettingAll == null) {
            finish();
            return;
        }
        redpacketmsglen = mRedpacketSettingAll.getConfig().getRedpacketmsglen();
        if ((int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0) == 0) {
            mRedpacketSetting = mRedpacketSettingAll.getUnadconfig();
        } else {
            mRedpacketSetting = mRedpacketSettingAll.getAdconfig();
        }
        setContentView(R.layout.activity_send_redpacket);
        initView();
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.WX_PAY_CALLBACK, ACTION.ALI_PAY_CALLBACK);
        setView();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, R.color.theme_red_ff6);
    }

    private void initView() {
        findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mPopMsgTv = (TextView) findViewById(R.id.tv_popup_msg);
        mCountTv = (TextView) findViewById(R.id.tv_money_count);
        mCountEt = (EditText) findViewById(R.id.et_money_count);
        mCountUnitTv = (TextView) findViewById(R.id.tv_count_unit);
        mAmountTv = (TextView) findViewById(R.id.tv_money_amount);
        mPinTv = (TextView) findViewById(R.id.tv_pin);
        mAmountEt = (EditText) findViewById(R.id.et_money_amount);
        mAmountUnitTv = (TextView) findViewById(R.id.tv_amount_unit);
        mCurrRpTypeTv = (TextView) findViewById(R.id.tv_curr_rp_type);
        mChangeTypeTv = (TextView) findViewById(R.id.tv_change_rp_type);
        mGreetingEt = (EditText) findViewById(R.id.et_greetings);
        mGreetingEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(redpacketmsglen) });
        mMoneyTv = (TextView) findViewById(R.id.tv_money);
        mPutMoneyBtn = (Button) findViewById(R.id.btn_put_money);

        mCountEt.addTextChangedListener(mTextWatcher);
        mAmountEt.addTextChangedListener(mTextWatcher);

        mAmountEt.addTextChangedListener(new TextWatcher() {
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
                        mAmountEt.setText(s);
                        mAmountEt.setSelection(s.length());
                    }
                    int index = s.toString().indexOf(".");
                    if (index > 5) {
                        String intStr = s.toString().substring(0, 5);
                        String floatStr = s.toString().substring(index, s.length());
                        s = intStr + floatStr;
                        mAmountEt.setText(s);
                        mAmountEt.setSelection(s.length());
                    }
                } else {
                    if (s.length() > 5) {
                        s = s.toString().subSequence(0, 5);
                        mAmountEt.setText(s);
                        mAmountEt.setSelection(s.length());
                    }
                }
                //第一位输入小数点的话自动变换为 0.
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mAmountEt.setText(s);
                    mAmountEt.setSelection(2);
                }
                //避免重复输入小数点前的0 ,没有意义
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mAmountEt.setText(s.subSequence(0, 1));
                        mAmountEt.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPutMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayDialog.setMoney(redpacketmoney);
                mPayDialog.setPayType(paytype);
                mPayDialog.show();
            }
        });

        mChangeTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRedpacketType((redpackettype + 1) % 2);
            }
        });
    }

    private void setView() {
        mPayDialog = new RedPacketPayDialog(mActivity);
        mPayConfirmDialog = new PayConfirmDialog(mActivity);
        mHandlingDialog = new HandlingDialog(mActivity);
        if (!CommonShare.isWechatInstalled(this)) {
            paytype = Payment.TYPE_ALIPAY;
            SharedPrefUtils.putToPublicFile(KEY.REDPACKET_PAY_TYPE, Payment.TYPE_ALIPAY);
        } else {
            int type = (int) SharedPrefUtils.getFromPublicFile(KEY.REDPACKET_PAY_TYPE, Payment.TYPE_WEIXIN);
            paytype = type;
        }
        changeRedpacketType(redpackettype);
    }

    private void changeRedpacketType(int redpackettype) {
        this.redpackettype = redpackettype;
        switch (this.redpackettype) {
            case RP_TYPE_NORMAL:
                mAmountTv.setText(R.string.me_single_amount);
                mCurrRpTypeTv.setText(R.string.me_curr_is_normal_redpacket);
                mChangeTypeTv.setText(R.string.me_change_to_random_redpacket);
                mPinTv.setVisibility(View.GONE);
                if (count > 0 && amount > 0) {
                    String amountStr = StringUtil.get2ScaleStringFloor(redpacketmoney / count);
                    mAmountEt.setText(amountStr);
                    mAmountEt.setSelection(amountStr.length());
                } else {
                    computeMoney();
                }
                break;
            case RP_TYPE_RANDOM:
                mAmountTv.setText(R.string.me_total_amount);
                mCurrRpTypeTv.setText(R.string.me_curr_is_random_redpacket);
                mChangeTypeTv.setText(R.string.me_change_to_normal_redpacket);
                mPinTv.setVisibility(View.VISIBLE);
                if (count > 0 && amount > 0) {
                    String moneyStr = StringUtil.get2ScaleString(count * amount);
                    mAmountEt.setText(moneyStr);
                    mAmountEt.setSelection(moneyStr.length());
                } else {
                    computeMoney();
                }
                break;
        }
    }

    private void computeMoney() {
        String countStr = mCountEt.getText().toString().trim();
        String amountStr = mAmountEt.getText().toString().trim();
        try {
            count = Integer.parseInt(countStr);
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        try {
            amount = Double.parseDouble(amountStr);
        } catch (Exception e) {
            e.printStackTrace();
            amount = 0;
        }
        double singleAmount = 0d;
        switch (redpackettype) {
            case RP_TYPE_NORMAL: {
                String money = StringUtil.get2ScaleString(count * amount);
                redpacketmoney = Double.parseDouble(money);
                singleAmount = amount;
            }
            break;
            case RP_TYPE_RANDOM: {
                redpacketmoney = amount;
                singleAmount = amount;
                if (count > 0) {
                    singleAmount = Double.parseDouble(StringUtil.get2ScaleStringFloor(redpacketmoney / count));
                }
            }
            break;
        }
        mMoneyTv.setText(String.format("¥ %.2f", redpacketmoney));
        if (TextUtils.isEmpty(countStr) || TextUtils.isEmpty(amountStr)) {
            mPutMoneyBtn.setEnabled(false);
        } else {
            mPutMoneyBtn.setEnabled(count > 0 && redpacketmoney > 0);
        }
        String tip = "";
        if (tip.length() == 0 && mRedpacketSetting.getSendqtymax() > 0 && countStr.length() > 0 && count > mRedpacketSetting.getSendqtymax()) {
            tip = String.format(getString(R.string.me_redpacket_max_count_limit), mRedpacketSetting.getSendqtymax());
            mCountTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mCountEt.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mCountUnitTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
        }
        if (tip.length() == 0 && mRedpacketSetting.getSendqtymin() > 0 && countStr.length() > 0 && count < mRedpacketSetting.getSendqtymin()) {
            tip = String.format(getString(R.string.me_redpacket_min_count_limit), mRedpacketSetting.getSendqtymin());
            mCountTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mCountEt.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mCountUnitTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
        }
        if (tip.length() == 0 && mRedpacketSetting.getSingleMaxAmount() > 0 && amountStr.length() > 0 && singleAmount > mRedpacketSetting.getSingleMaxAmount()) {
            tip = String.format(getString(R.string.me_redpacket_single_amount_max_limit), mRedpacketSetting.getSendavgmax());
            mAmountTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mAmountEt.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mAmountUnitTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
        }
        if (tip.length() == 0 && mRedpacketSetting.getSingleMinAmount() > 0 && amountStr.length() > 0 && singleAmount < mRedpacketSetting.getSingleMinAmount()) {
            tip = String.format(getString(R.string.me_redpacket_single_amount_min_limit), mRedpacketSetting.getSendavgmin());
            mAmountTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mAmountEt.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mAmountUnitTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
        }
        if (tip.length() == 0 && mRedpacketSetting.getMoneymax() > 0 && redpacketmoney > mRedpacketSetting.getMoneymax()) {
            tip = String.format(getString(R.string.me_redpacket_max_money_limit), mRedpacketSetting.getMoneymax());
            mAmountTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mAmountEt.setTextColor(Res.getColor(R.color.f_redpacket_red));
            mAmountUnitTv.setTextColor(Res.getColor(R.color.f_redpacket_red));
        }
        mPopMsgTv.setText(tip);
        if (!TextUtils.isEmpty(tip)) {
            mPopMsgTv.setVisibility(View.VISIBLE);
            mPutMoneyBtn.setEnabled(false);
            return;
        }
        if (mPopMsgTv.getVisibility() == View.VISIBLE) {
            mPopMsgTv.setVisibility(View.GONE);
            mCountTv.setTextColor(Res.getColor(R.color.f_black));
            mCountEt.setTextColor(Res.getColor(R.color.f_black));
            mCountUnitTv.setTextColor(Res.getColor(R.color.f_black));
            mAmountTv.setTextColor(Res.getColor(R.color.f_black));
            mAmountEt.setTextColor(Res.getColor(R.color.f_black));
            mAmountUnitTv.setTextColor(Res.getColor(R.color.f_black));
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            computeMoney();
        }
    };

    public void goPay(int paytype) {
        this.paytype = paytype;
        redpacketsize = count;
        String greeting = mGreetingEt.getText().toString().trim();
        if (TextUtils.isEmpty(greeting)) {
            greeting = mGreetingEt.getHint().toString();
        }
        redpacketmsg = greeting;
        mHandlingDialog.show();
        mTaskManager.start(RedPacketTask.createRedpacketOrder(buid, redpackettype, redpacketsize, redpacketmoney, redpacketmsg, this.paytype)
                .setCallback(new HttpTaskCallback<RedpacketOrder>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(RedpacketOrder result) {
                        redpacketid = result.redpacketid;
                        mHandlingDialog.show();
                        thirdPartyPay(result);
                    }

                    @Override
                    public void onFinish() {
                        mHandlingDialog.dismiss();
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWxPaying) {
            mWxPaying = false;
            mHandlingDialog.dismiss();
            mConfirmTimes = 0;
            payVerify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
        mCountEt.removeTextChangedListener(mTextWatcher);
        mAmountEt.removeTextChangedListener(mTextWatcher);
    }


    private void thirdPartyPay(RedpacketOrder order) {
        LogUtil.logE("redpacket pay", "begin orderpayno = " + order.prepayview.orderpayno);
        switch (paytype) {
            case Payment.TYPE_WEIXIN:
                mWxPaying = true;
                new WeixinPay().invokeBack(this, order.prepayview.orderthreepartytxt);
                break;
            case Payment.TYPE_ALIPAY:
                new AliPay().setIsShowToast(false).payNew(this, order.prepayview.orderthreepartytxt);
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.WX_PAY_CALLBACK:
                    //handlePayCallback(intent);
                    break;
                case ACTION.ALI_PAY_CALLBACK:
                    handlePayCallback(intent);
                    break;
            }
        }
    };

    private void handlePayCallback(Intent intent) {
        ExternalPayResult result = (ExternalPayResult) intent.getSerializableExtra(EXTRA.PAY_RESULT);
        int errCode = result.getErrCode();
        LogUtil.logE("paycallback = " + result.toString() + "\n, errCode = " + errCode);
        mHandlingDialog.dismiss();
        mConfirmTimes = 0;
        payVerify();
    }

    private void payVerify() {
        mConfirmTimes++;
        mPayConfirmDialog.show();
        mTaskManager.start(RedPacketTask.payVerify(redpacketid, (mConfirmTimes == CONFIRM_PAY_TIMES ? 1 : 0))
                .setCallback(new HttpTaskCallback<Integer>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        waitingPayConfirm();
                    }

                    @Override
                    public void onSuccess(Integer result) {
                        LogUtil.logE("paystatus = " + result);
                        switch (result) {
                            case PAY_STATUS_ALREADY_PAY:
                                mPayConfirmDialog.dismiss();
                                PayResultDialog dialog = new PayResultDialog(mActivity);
                                dialog.setResult(true);
                                dialog.show();
                                break;
                            case PAY_STATUS_WAITING_PAY:
                                waitingPayConfirm();
                                break;
                            default:
                                mPayConfirmDialog.dismiss();
                                break;
                        }
                    }
                }));
    }

    public void afterPaySuccess() {
        setResult(RESULT_OK);
        finish();
    }

    private void waitingPayConfirm() {
        if (mConfirmTimes < CONFIRM_PAY_TIMES) {
            mPutMoneyBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    payVerify();
                }
            }, getTime());
        } else {
            mPayConfirmDialog.dismiss();
//            PayUnconfirmedDialog dialog = new PayUnconfirmedDialog(mActivity);
//            dialog.show();
            PayResultDialog dialog = new PayResultDialog(mActivity);
            dialog.setResult(false);
            dialog.show();
        }
    }

    private int getTime() {
        try {
            return mRedpacketSettingAll.getConfig().getPaycallbackloadingtime() * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return 5000;
        }
    }

}
