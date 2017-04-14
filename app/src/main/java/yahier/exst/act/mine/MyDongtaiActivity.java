package yahier.exst.act.mine;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiAct2;
import com.stbl.stbl.act.dongtai.StatusesFragmentShoppingCircle;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.MyOnPageChangeListener;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.NoScrollViewPager;
import com.stbl.stbl.widget.SlidingTabLayout;
import com.stbl.stbl.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/5/7.
 */
public class MyDongtaiActivity extends BaseActivity implements SlidingTabLayout.OnViewPageListener {


    public int mode = 2;

    private TitleBar mBar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private CommonFragmentPagerAdapter mAdapter;
    private UserItem userItem;

    private int tabIndex = 0; //默认显示的页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_dongtai_act);
        userItem = (UserItem) getIntent().getSerializableExtra("userItem");
        tabIndex = getIntent().getIntExtra("dynamicType", 0);
        initView();
    }

    MyDongtaiFragment fragment1;

    private void initView() {
        mBar = (TitleBar) findViewById(R.id.bar);
        if (userItem == null || String.valueOf(userItem.getUserid()).equals(SharedToken.getUserId())) {
            mBar.setTitle(getString(R.string.me_my_status));
        } else {
            mBar.setTitle(userItem.getNickname() + "的动态");
        }

        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragment1 = new MyDongtaiFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("userItem", userItem);
        fragment1.setArguments(bundle1);
        fragmentList.add(fragment1);


        StatusesFragmentShoppingCircle fragment2 = new StatusesFragmentShoppingCircle();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", StatusesFragmentShoppingCircle.typeManage);
        bundle2.putSerializable("userItem", userItem);
        fragment2.setArguments(bundle2);

        fragment2.setArguments(bundle2);
        fragmentList.add(fragment2);
        String[] titles = new String[]{getString(R.string.me_status), getString(R.string.red_packet)};//

        mAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDefaultHeightAndPadding(33,0);
        mSlidingTabLayout.setLineWidth(70);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPagerListener(this);
        mSlidingTabLayout.setDividerColors(R.color.theme_green);

        if (tabIndex > 0 && tabIndex < fragmentList.size()) {
            mViewPager.setCurrentItem(tabIndex);
        }

    }


    @Override
    public void onPageSelected(int i) {
        try {
            fragment1.adapter.pauseInVisibleVideo();
            //不加上的话，在关注播放。再到广场播放，再回来关注，关注这里就没有显示视频这个item
            mViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragment1.adapter.resetVideo();
                }
            },200);

        } catch(Exception e) {
            LogUtil.logE("e:" + e.getStackTrace().toString());
        }
    }
}
