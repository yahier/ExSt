package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.model.FanliInfo;
import com.stbl.stbl.model.WithdrawAccount;
import com.stbl.stbl.task.FanliTask;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;

public class MyFanliActivity extends BaseActivity {

    private static final int REQUEST_CODE_APPLY_WITHDRAW = 1;

    private TextView mAccumulatedIncomeTv;
    private TextView mAvailableIncomeTv;
    private Button mWithdrawBtn;

    private FanliInfo mFanliInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fanli);

        initViews();
        getFanliInfo();
    }

    private void initViews() {
        mAccumulatedIncomeTv = (TextView) findViewById(R.id.tv_accumulated_income);
        mAvailableIncomeTv = (TextView) findViewById(R.id.tv_available_income);
        mWithdrawBtn = (Button) findViewById(R.id.btn_withdraw);

        findViewById(R.id.iv_back).setOnClickListener(mClickListener);
        mWithdrawBtn.setOnClickListener(mClickListener);
        findViewById(R.id.layout_money_flow).setOnClickListener(mClickListener);
        findViewById(R.id.layout_withdraw_account).setOnClickListener(mClickListener);
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    return;
                case R.id.btn_withdraw:
                    withdraw();
                    break;
                case R.id.layout_money_flow: {
                    Intent intent = new Intent(mActivity, MoneyFlowActivity.class);
                    startActivity(intent);
                }
                break;
                case R.id.layout_withdraw_account: {
                    Intent intent = new Intent(mActivity, WithdrawAccountActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    };

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
                        mAccumulatedIncomeTv.setText(result.totalamount);
                        mAvailableIncomeTv.setText(result.amount);
                        updateWithdrawText();
                    }
                }));
    }

    private void updateWithdrawText() {
        switch (mFanliInfo.status) {
            case FanliInfo.STATUS_NORMAL:
                mWithdrawBtn.setText(R.string.me_i_want_withdraw);
                mWithdrawBtn.setEnabled(true);
                break;
            case FanliInfo.STATUS_WITHDRAW_ONCE:
                mWithdrawBtn.setText(R.string.me_i_want_withdraw);
                mWithdrawBtn.setEnabled(true);
                break;
            case FanliInfo.STATUS_WITHDRAW_REVIEWING:
                mWithdrawBtn.setText(R.string.me_withdraw_reviewing);
                mWithdrawBtn.setEnabled(false);
                break;
        }
    }

    private void withdraw() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_APPLY_WITHDRAW:
                    mWithdrawBtn.setEnabled(false);
                    mWithdrawBtn.setText(R.string.me_withdraw_reviewing);
                    getFanliInfo();
                    break;
            }
        }
    }
}
