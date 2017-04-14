package yahier.exst.act.ad;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.MyOnPageChangeListener;
import com.stbl.stbl.widget.SlidingTabLayout;

import java.util.ArrayList;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2016/9/29.
 */

public class MoneyFlowActivity extends BaseActivity {

  //  private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private CommonFragmentPagerAdapter mAdapter;
    private View lineTab;
    TextView tvItem1, tvItem2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_flow);
        initView();
    }

    private void initView() {
        lineTab = findViewById(R.id.lineTab);
        tvItem1 = (TextView) findViewById(R.id.tvItem1);
        tvItem2 = (TextView) findViewById(R.id.tvItem2);
        tvItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, true);
            }
        });

        tvItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, true);
            }
        });


        findViewById(R.id.imgQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFanliTaxIntroDialog();
            }
        });
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        setLabel(getString(R.string.me_money_flow));
//        setRightText(getString(R.string.me_withdraw_intro), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFanliTaxIntroDialog();
//            }
//        });
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FanliDetailFragment());
        fragmentList.add(new WithdrawDetailFragment());

        String[] titles = new String[]{getString(R.string.me_fanli_detail), getString(R.string.me_withdraw_detail)};
        mAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mAdapter);
//        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
//        mSlidingTabLayout.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {

                switch(i) {
                    case 0:
                        ObjectAnimator.ofFloat(lineTab, "translationX", lineDistance, 0).start();
                        break;
                    case 1:
                        ObjectAnimator.ofFloat(lineTab, "translationX", 0, lineDistance).start();
                        break;

                }

            }
        });
    }


    int lineDistance;

    public void onResume() {
        super.onResume();
        //计算需要滑动的距离
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                int[] locations = new int[2];
                tvItem1.getLocationOnScreen(locations);
                int[] locations2 = new int[2];
                tvItem2.getLocationOnScreen(locations2);
                lineDistance = locations2[0] - locations[0];

            }
        });
    }

    private void showFanliTaxIntroDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_fanli_tax_intro),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {

                    }
                }, true).show();
    }

}
