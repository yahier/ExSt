package yahier.exst.act.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.ApplyWithdrawActivity;
import com.stbl.stbl.act.ad.MoneyFlowActivity;
import com.stbl.stbl.act.ad.WithdrawAccountActivity;
import com.stbl.stbl.act.redpacket.RedPacketRecordAct;
import com.stbl.stbl.barcoe.Intents;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.WealthInfo;
import com.stbl.stbl.model.FanliInfo;
import com.stbl.stbl.model.HongBaoWallet;
import com.stbl.stbl.model.WithdrawAccount;
import com.stbl.stbl.task.FanliTask;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.ui.activity.RPChangeActivity;

import java.util.ArrayList;

public class MineWalletAct extends BaseActivity {
    private static final int REQUEST_CODE_APPLY_WITHDRAW = 1;
    private TextView mGoldAmountTv;
    private TextView mTicketAmountTv;
//    private TextView mMoneyAmountTv;
    private TextView tv_balance; //余额
    private TextView tv_withdraw; //提现
    private View rl_withdraw; //提现栏

    private WealthInfo mWealthInfo;
    private FanliInfo mFanliInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_wallet);

        initViews();

        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.GET_WALLET_BALANCE);

        getWalletInfo();
//        getHongBaoWallet();
        getFanliInfo();
        checkIsShowOldWallet();
    }

    @Override
    protected void setStatusBar(){
        StatusBarUtil.setStatusBarColor(this, R.color.theme_red_ff6);
        //StatusBarUtil.StatusBarLightMode(this);
    }

    private void initViews() {
        mGoldAmountTv = (TextView) findViewById(R.id.tv_gold_amount);
        mTicketAmountTv = (TextView) findViewById(R.id.tv_ticket_amount);
//        mMoneyAmountTv = (TextView) findViewById(R.id.tv_my_money_amount);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_withdraw = (TextView) findViewById(R.id.tv_withdraw);

        findViewById(R.id.iv_back).setOnClickListener(mClickListener);
        findViewById(R.id.tv_wallet_detail).setOnClickListener(mClickListener);
        findViewById(R.id.rl_recharge).setOnClickListener(mClickListener);
        rl_withdraw = findViewById(R.id.rl_withdraw);
        rl_withdraw.setOnClickListener(mClickListener);
        findViewById(R.id.rl_rp_record).setOnClickListener(mClickListener);
        findViewById(R.id.rl_withdraw_accout).setOnClickListener(mClickListener);
        findViewById(R.id.rl_pay_password).setOnClickListener(mClickListener);
        findViewById(R.id.rl_issue).setOnClickListener(mClickListener);
        findViewById(R.id.rl_my_money).setOnClickListener(mClickListener);
    }

    private void checkIsShowOldWallet(){
        if(SharedPrefUtils.isShowOldWallet()==false){
            findViewById(R.id.rl_my_money).setVisibility(View.GONE);
            findViewById(R.id.viewOldWallet).setVisibility(View.GONE);
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    return;
                case R.id.tv_wallet_detail: //钱包明细
                    intent = new Intent(mActivity, MoneyFlowActivity.class);
                    break;
                case R.id.layout_gold_bean: //金豆页面
                    intent = new Intent(mActivity, GoldBeanActivity.class);
                    intent.putExtra(KEY.AMOUNT, mWealthInfo.getJindou());
                    break;
                case R.id.layout_shitu_ticket: // 师徒票页面
                    intent = new Intent(mActivity, ShituTicketActivity.class);
                    intent.putExtra(KEY.AMOUNT, mWealthInfo.getJifen());
                    break;
                case R.id.rl_recharge: //充值金豆
                    MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JDCZ);
                    startActivity(new Intent(mActivity, WalletChargeActivity.class));
                    break;
                case R.id.rl_withdraw: //提现
                    withdraw();
                    break;
                case R.id.rl_rp_record: //红包记录
                    startActivity(new Intent(mActivity, RedPacketRecordAct.class));
                    break;
                case R.id.rl_withdraw_accout: //提现账户
                    startActivity(new Intent(mActivity, WithdrawAccountActivity.class));
                    break;
                case R.id.rl_pay_password: //支付密码
                    intent = new Intent(mActivity, PayPasswordSettingAct.class);
                    intent.putExtra("phone", SharedUser.getPhone());
                    break;
                case R.id.rl_issue: //常见问题
                    String url = (String) SharedPrefUtils.getFromPublicFile(KEY.WALLET_PROBLEM_INTROD, "");
                    intent = new Intent(mActivity, CommonWeb.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", getString(R.string.me_faq));
                    break;
                case R.id.rl_my_money: //云账户钱包
                    intent = new Intent(mActivity, RPChangeActivity.class);
                    // 当前用户昵称 不可为空
                    String fromNickname = SharedUser.getUserNick();
                    // 当前用户头像url
                    String fromAvatarUrl = SharedUser.getUserItem().getImgmiddleurl();// 默认值为none 不可为空
                    if (fromAvatarUrl == null) fromAvatarUrl = "none";
                    RedPacketInfo redPacketInfo = new RedPacketInfo();
                    redPacketInfo.fromNickName = fromNickname;
                    redPacketInfo.fromAvatarUrl = fromAvatarUrl;
                    TokenData tokenData = new TokenData();
                    // 传入当前用户id 不可为空
                    tokenData.appUserId = SharedToken.getUserId(mActivity);
                    intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, tokenData);
                    intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
                    break;
            }
            if (intent != null) startActivity(intent);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_APPLY_WITHDRAW:
                    rl_withdraw.setEnabled(false);
                    tv_withdraw.setText(R.string.me_withdraw_reviewing);
                    getFanliInfo();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private void getFanliInfo() {
        mTaskManager.start(WalletTask.getFanliInfo()
                .setCallback(new HttpTaskCallback<FanliInfo>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(FanliInfo result) {
                        mFanliInfo = result;
                        tv_balance.setText(String.format(getString(R.string.me_unit),result.amount));
                        updateWithdrawText();
                    }
                }));
    }
    private void updateWithdrawText() {
        switch (mFanliInfo.status) {
            case FanliInfo.STATUS_NORMAL:
                tv_withdraw.setText(R.string.me_withdraw);
                rl_withdraw.setEnabled(true);
                break;
            case FanliInfo.STATUS_WITHDRAW_ONCE:
                tv_withdraw.setText(R.string.me_withdraw);
                rl_withdraw.setEnabled(true);
                break;
            case FanliInfo.STATUS_WITHDRAW_REVIEWING:
                tv_withdraw.setText(R.string.me_withdraw_reviewing);
                rl_withdraw.setEnabled(false);
                break;
        }
    }

    private void withdraw() {
        if(mFanliInfo==null){
            ToastUtil.showToast(R.string.data_error);
            return;
        }
        if (mFanliInfo.status == FanliInfo.STATUS_WITHDRAW_ONCE) {
            ToastUtil.showToast(R.string.me_one_day_one_withdraw);
            return;
        }
        getWithdrawAccountList();
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
                        if (result.size() == 0) {
                            showNeedBindAccountAlertDialog();
                        } else {
                            WithdrawAccount wxAccount = null;
                            for (WithdrawAccount account : result) {
                                if (account.isbound == WithdrawAccount.BIND_YES && account.accounttype == WithdrawAccount.ACCOUNT_TYPE_WECHAT) {
                                    wxAccount = account;
                                    break;
                                }
                            }
                            if (wxAccount != null) {
                                Intent intent = new Intent(mActivity, ApplyWithdrawActivity.class);
                                intent.putExtra(KEY.MAX_AMOUNT, mFanliInfo.amount);
                                intent.putExtra(KEY.WITHDRAW_ACCOUNT, wxAccount);
                                startActivityForResult(intent, REQUEST_CODE_APPLY_WITHDRAW);
                            } else {
                                showNeedBindAccountAlertDialog();
                            }
                        }
                    }
                }));
    }

    private void showNeedBindAccountAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_you_not_bind_withdraw_account_please_bind_withdraw_account),
                getString(R.string.cancel),
                getString(R.string.me_go_bind),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        Intent intent = new Intent(mActivity, WithdrawAccountActivity.class);
                        intent.putExtra(KEY.NO_BOUND, true);
                        startActivity(intent);
                    }
                }).show();
    }
    private void getWalletInfo() {
        mTaskManager.start(WalletTask.getWalletInfo()
                .setCallback(new HttpTaskCallback<WealthInfo>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(WealthInfo result) {
                        mWealthInfo = result;
                        mGoldAmountTv.setText(mWealthInfo.getJindou() + "");
                        mTicketAmountTv.setText(mWealthInfo.getJifen() + "");

                        findViewById(R.id.layout_shitu_ticket).setOnClickListener(mClickListener);
                        findViewById(R.id.layout_gold_bean).setOnClickListener(mClickListener);
                        Intent intent = new Intent(ACTION.UPDATE_GOLD_BEAN_AMOUNT);
                        intent.putExtra(KEY.AMOUNT, mWealthInfo.getJindou());
                        LocalBroadcastHelper.getInstance().send(intent);
                    }
                }));
    }

//    private void getHongBaoWallet() {
//        mTaskManager.start(WalletTask.getHongBaoWallet()
//                .setCallback(new TaskCallback<HongBaoWallet>() {
//                    @Override
//                    public void onError(TaskError e) {
//                        ToastUtil.showToast(e.msg);
//                    }
//
//                    @Override
//                    public void onSuccess(HongBaoWallet result) {
////                        mMoneyAmountTv.setText(result.Balance);
//                        findViewById(R.id.layout_my_money).setOnClickListener(mClickListener);
//                    }
//                }));
//    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.GET_WALLET_BALANCE:
                    getWalletInfo();
                    break;
            }
        }
    };

}
