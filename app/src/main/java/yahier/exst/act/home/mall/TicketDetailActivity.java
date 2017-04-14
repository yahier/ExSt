package yahier.exst.act.home.mall;

import android.os.Bundle;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.ScoreDetailFragment;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.WealthInfo;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;

/**
 * 积分明细
 * Created by tnitf on 2016/7/26.
 */
public class TicketDetailActivity extends ThemeActivity {

    private TextView mAmountTv;
    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        setLabel(getString(R.string.shitupiao_detail));
        initView();
        getWalletBalance();
    }

    private void initView() {
        mAmountTv = (TextView) findViewById(R.id.tv_amount);
        int jifen = (int) SharedPrefUtils.getFromUserFile(KEY.jifen, -1);
        if (jifen > 0) {
            mAmountTv.setText(jifen + "");
        }
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ScoreDetailFragment()).commit();
    }

    private void getWalletBalance() {
        WalletTask.getWalletBalance().setCallback(this, mGetAccountBalanceCallback).start();
    }

    private SimpleTask.Callback<WealthInfo> mGetAccountBalanceCallback = new SimpleTask.Callback<WealthInfo>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(WealthInfo result) {
            mAmountTv.setText(result.getJifen() + "");
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

}
