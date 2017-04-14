package yahier.exst.act.ad;

import android.os.Bundle;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.KEY;

/**
 * Created by Administrator on 2016/9/23.
 */

public class WithdrawSuccessActivity extends ThemeActivity {

    private TextView mWithdrawAmountTv;
    private TextView mWithdrawAccountTv;
    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_success);
        initView();
    }

    private void initView() {
        setLabel(getString(R.string.me_apply_withdraw));
        mWithdrawAmountTv = (TextView) findViewById(R.id.tv_withdraw_amount);
        mWithdrawAccountTv = (TextView) findViewById(R.id.tv_withdraw_account);

        String amount = getIntent().getStringExtra(KEY.WITHDRAW_AMOUNT);
        mWithdrawAmountTv.setText("¥" + amount);

        jumpBackFanli();
    }

    private void jumpBackFanli() {
        mWithdrawAmountTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsDestroy) {
                    finish();
                }
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }
}
