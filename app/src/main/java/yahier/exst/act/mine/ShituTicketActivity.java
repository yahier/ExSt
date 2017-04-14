package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.integral.MallIntegralAct;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/11/8.
 */

public class ShituTicketActivity extends BaseActivity implements View.OnClickListener {

    private WalletDetailFragment mFragment;
    private TextView mAmountTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shitu_ticket);

        initView();
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
        findViewById(R.id.tv_earn).setOnClickListener(this);
        findViewById(R.id.tv_consume).setOnClickListener(this);

        mFragment = new WalletDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY.TYPE, WalletDetailFragment.TYPE_SHITU_TICKET);
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
            case R.id.tv_earn:
                MobclickAgent.onEvent(this, UmengClickEventHelper.STPZQ);
                startActivity(new Intent(mActivity, EarnShituTicketActivity.class));
                break;
            case R.id.tv_consume:
                MobclickAgent.onEvent(this, UmengClickEventHelper.STPXF);
                startActivity(new Intent(mActivity, MallIntegralAct.class));
                break;
        }
    }
}
