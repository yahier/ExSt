package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.SingleSelectFriendActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;

public class TransferBeanActivity extends ThemeActivity {

    private static final int REQUEST_CODE_SELECT_FRIEND = 1;

    private EditText mBeanAmountEt;
    private TextView mExchangeTipTv;

    private TextView mReceiveManTv;

    private UserItem mUser;
    private int mPayType = 4; //4-金豆，5-绿豆

    private boolean mIsDestroy;
    private LoadingDialog mLoadingDialog;

    private int jindou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_bean);

        initView();
    }

    private void initView() {
        setLabel("转让金豆");

        mBeanAmountEt = (EditText) findViewById(R.id.et_exchange_amount);
        mExchangeTipTv = (TextView) findViewById(R.id.tv_exchange_tip);
        mReceiveManTv = (TextView) findViewById(R.id.tv_receive_bean_man);

        mLoadingDialog = new LoadingDialog(this);

        jindou = (int) SharedPrefUtils.getFromUserFile(KEY.jindou, 0);

        mExchangeTipTv.setText("当前最多可转让:" + jindou + "个金豆");

        findViewById(R.id.layout_select_receive_bean_man).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferBeanActivity.this, SingleSelectFriendActivity.class);
                if (mUser != null) {
                    intent.putExtra(EXTRA.USER_ITEM, mUser);
                }
                intent.putExtra(EXTRA.HAS_SELF, 0); //0-不包含，1-包含
                startActivityForResult(intent, REQUEST_CODE_SELECT_FRIEND);
            }
        });


        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeTransfer();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_FRIEND) {
            switch (resultCode) {
                case RESULT_OK:
                    UserItem user = (UserItem) data.getSerializableExtra(EXTRA.USER_ITEM);
                    if (user != null) {
                        mUser = user;
                        String text = "选择了好友：<font color='#DEA524'>" + mUser.getNickname() + "</font>";
                        mReceiveManTv.setText(Html.fromHtml(text));
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void beforeTransfer() {
        String amountStr = mBeanAmountEt.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            ToastUtil.showToast("请填写转让数量");
            return;
        }
        final int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast("请填写转让数量");
            return;
        }

        if (mUser == null) {
            ToastUtil.showToast("请选择好友");
            return;
        }
        final long touserids = mUser.getUserid();
        Payment.getPassword(this, amount, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                transferBean(amount, touserids, pwd);
            }
        });
    }

    private void transferBean(long amount, long touserids, String paypwd) {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        WalletTask.transferBean(mPayType, amount, touserids, paypwd).setCallback(this, mTransferBeanCallback).start();
    }

    private SimpleTask.Callback<Integer> mTransferBeanCallback = new SimpleTask.Callback<Integer>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast("转让成功");
            LocalBroadcastHelper.getInstance().send(new Intent(ACTION.GET_WALLET_BALANCE));
            finish();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };
}
