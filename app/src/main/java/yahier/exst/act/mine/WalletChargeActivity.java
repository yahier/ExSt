package yahier.exst.act.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.ChargeAmountAdapter;
import com.stbl.stbl.adapter.mine.PayModeAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.external.alipay.AliPay;
import com.stbl.stbl.model.ChargeAmount;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.model.PayMode;
import com.stbl.stbl.model.Recharge;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.NestedGridView;
import com.stbl.stbl.wxapi.WeixinPay;
import com.tencent.mm.sdk.modelbase.BaseResp;

import java.util.ArrayList;

public class WalletChargeActivity extends ThemeActivity {

    private TextView mBeanAmountTv;

    private NestedGridView mAmountGridView;
    private ArrayList<ChargeAmount> mAmountList;
    private ChargeAmountAdapter mAmountAdapter;

    private NestedGridView mPayGridView;
    private ArrayList<PayMode> mPayList;
    private PayModeAdapter mPayAdapter;

    private int mAmount;

    private int mPayType = 1; //1-微信 2-支付宝

    private LoadingDialog mLoadingDialog;
    private boolean mIsDestroy;

    private long mPayOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_charge);

        initView();

        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.WX_PAY_CALLBACK, ACTION.ALI_PAY_CALLBACK);

        getChargeAmount();
    }

    private void initView() {
        setLabel(getString(R.string.me_recharge));
        mBeanAmountTv = (TextView) findViewById(R.id.tv_bean_amount);
        int goldAmount = (int) SharedPrefUtils.getFromUserFile(KEY.jindou, 0);
        if (goldAmount < 0) {
            goldAmount = 0;
        }
        mBeanAmountTv.setText(String.format(getString(R.string.me_d_number), goldAmount));

        mAmountGridView = (NestedGridView) findViewById(R.id.gv_charge_amount);
        mAmountList = new ArrayList<>();
        mAmountAdapter = new ChargeAmountAdapter(mAmountList);
        mAmountGridView.setAdapter(mAmountAdapter);

        mPayGridView = (NestedGridView) findViewById(R.id.gv_pay_mode);
        mPayList = new ArrayList<>();
        mPayList.add(new PayMode(getString(R.string.me_weixin_pay), Payment.TYPE_WEIXIN));
        mPayList.add(new PayMode(getString(R.string.me_ali_pay), Payment.TYPE_ALIPAY));
        mPayAdapter = new PayModeAdapter(mPayList);
        mPayGridView.setAdapter(mPayAdapter);
        mPayAdapter.init();

        mLoadingDialog = new LoadingDialog(this);

        mAmountGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAmountAdapter.toggleSelect(position);
                mAmount = mAmountList.get(position).getEx2();
            }
        });

        mPayGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPayAdapter.toggleSelect(position);
                mPayType = mPayList.get(position).getType();
            }
        });

        findViewById(R.id.btn_confirm_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyGoldBean();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private void buyGoldBean() {
        if (mAmount == 0) {
            ToastUtil.showToast(R.string.me_please_choose_buy_amount);
            return;
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        WalletTask.buyGoldBean(mPayType, mAmount).setCallback(this, mChargeBuyCallback).start();
    }

    private SimpleTask.Callback<Recharge> mChargeBuyCallback = new SimpleTask.Callback<Recharge>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Recharge result) {
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
            thirdPartyPay(result);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void thirdPartyPay(Recharge recharge) {
        LogUtil.logE("wallet pay", "begin pay order = " + recharge.getOrderpayno());
        mPayOrder = recharge.getOrderpayno();
        switch (mPayType) {
            case Payment.TYPE_WEIXIN:
                new WeixinPay().invokeBack(this, recharge.getWeixinjsonparameters());
                break;
            case Payment.TYPE_ALIPAY:
                new AliPay().pay(this, getString(R.string.me_stbl_recharge), getString(R.string.me_stbl_recharge), recharge.getPayfee() + "", String.valueOf(recharge.getOrderpayno()));
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.WX_PAY_CALLBACK:
                    handlePayCallback(intent);
                    break;
                case ACTION.ALI_PAY_CALLBACK:
                    handlePayCallback(intent);
                    break;
            }
        }
    };

    private void handlePayCallback(Intent intent) {
        ExternalPayResult result = (ExternalPayResult) intent.getSerializableExtra(EXTRA.PAY_RESULT);
        LogUtil.logE("paycallback", result.toString());
        int errCode = result.getErrCode();
        if (errCode == BaseResp.ErrCode.ERR_OK) {
            buyBeanVerify(mPayOrder);
        } else {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.me_pay_fail);
        }
    }

    private void buyBeanVerify(long orderpayno) {
        WalletTask.buyBeanVerify(orderpayno).setCallback(this, mVerifyCallback).start();
    }

    private SimpleTask.Callback<Integer> mVerifyCallback = new SimpleTask.Callback<Integer>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
            if (e.getMessage().contains(getString(R.string.me_alipay_confirm))) {
                LocalBroadcastHelper.getInstance().send(new Intent(ACTION.GET_WALLET_BALANCE));
                finish();
            }
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.me_recharge_success);
            LocalBroadcastHelper.getInstance().send(new Intent(ACTION.GET_WALLET_BALANCE));
            finish();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void getChargeAmount() {
        WalletTask.getChargeAmountList().setCallback(this, mGetChargeAmountCallback).start();
    }

    private SimpleTask.Callback<ArrayList<ChargeAmount>> mGetChargeAmountCallback = new SimpleTask.Callback<ArrayList<ChargeAmount>>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ArrayList<ChargeAmount> result) {
            if (result.size() > 0) {
                mAmountList.clear();
                mAmountList.addAll(result);
                mAmountAdapter.init();
                mAmount = mAmountList.get(0).getEx2();
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
