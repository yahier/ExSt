package yahier.exst.act.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.widget.SlidingTabLayout;
import com.stbl.stbl.widget.TitleBar;

import java.util.ArrayList;

public class MyGiftActivity extends BaseActivity {

    private TitleBar mBar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private CommonFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gift);

        initView();
    }

    private void initView() {
        mBar = (TitleBar) findViewById(R.id.bar);
        mBar.setTitle(getString(R.string.me_my_gift_box));
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        ArrayList<Fragment> fragmentList = new ArrayList<>();

        CommonGiftFragment fragment1 = new CommonGiftFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(KEY.SELECT_TYPE, MineGiftItem.selecttype_get);
        bundle1.putLong(KEY.USER_ID, Long.parseLong(SharedToken.getUserId(this)));
        fragment1.setArguments(bundle1);
        fragmentList.add(fragment1);

        CommonGiftFragment fragment2 = new CommonGiftFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt(KEY.SELECT_TYPE, MineGiftItem.selecttype_sent);
        bundle2.putLong(KEY.USER_ID, Long.parseLong(SharedToken.getUserId(this)));
        fragment2.setArguments(bundle2);
        fragmentList.add(fragment2);

        String[] titles = new String[]{getString(R.string.me_i_receive), getString(R.string.me_i_send)};
        mAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDefaultHeightAndPadding(33,0);
        mSlidingTabLayout.setLineWidth(70);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
