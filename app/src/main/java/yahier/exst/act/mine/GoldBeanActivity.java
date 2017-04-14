package yahier.exst.act.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/11/8.
 */

public class GoldBeanActivity extends BaseActivity implements View.OnClickListener {

    private WalletDetailFragment mFragment;
    private TextView mAmountTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold_bean);

        initView();
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.UPDATE_GOLD_BEAN_AMOUNT);
    }

    @Override
    protected void setStatusBar(){
        StatusBarUtil.setStatusBarColor(this, R.color.theme_red_ff6);
        //StatusBarUtil.StatusBarLightMode(this);
    }

    private void initView() {
        mAmountTv = (TextView) findViewById(R.id.tv_amount);
        int amount = getIntent().getIntExtra(KEY.AMOUNT, 0);
        mAmountTv.setText(amount + "");
        findViewById(R.id.iv_back).setOnClickListener(this);
//        findViewById(R.id.layout_recharge).setOnClickListener(this);

        mFragment = new WalletDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY.TYPE, WalletDetailFragment.TYPE_GOLD_BEAN);
        mFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_container, mFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
//            case R.id.layout_recharge:
//                MobclickAgent.onEvent(this, UmengClickEventHelper.JDCZ);
//                startActivity(new Intent(mActivity, WalletChargeActivity.class));
//                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.UPDATE_GOLD_BEAN_AMOUNT:
                    int amount = intent.getIntExtra(KEY.AMOUNT, 0);
                    mAmountTv.setText(amount + "");
                    break;
            }
        }
    };

}
