package yahier.exst.act.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.widget.SlidingTabLayout;
import com.stbl.stbl.widget.TitleBar;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/20.
 */
public class VisitTrackActivity extends BaseActivity {

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
        mBar.setTitle(getString(R.string.me_visit_footprint));
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        MyTrackFragment fragment1 = new MyTrackFragment();
        fragmentList.add(fragment1);
        MyVisitorFragment fragment2 = new MyVisitorFragment();
        fragmentList.add(fragment2);

        String[] titles = new String[]{getString(R.string.me_my_footprint), getString(R.string.me_my_visitor)};
        mAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDefaultHeightAndPadding(33,0);
        mSlidingTabLayout.setLineWidth(70);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

}
