package yahier.exst.act.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.widget.NoScrollViewPager;
import com.stbl.stbl.widget.SlidingTabLayout;
import com.stbl.stbl.widget.TitleBar;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/5/7.
 */
public class MyCollectionActivity extends BaseActivity {

    public static final int mode_look = 1;// 查看模式
    public static final int mode_statuses_choose = 2;// 选择模式
    public static final int mode_im_choose = 3;// 查看模式

    public int mode = 2;

    private TitleBar mBar;
    private SlidingTabLayout mSlidingTabLayout;
    private NoScrollViewPager mViewPager;
    private CommonFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        mode = getIntent().getIntExtra("mode", mode_look);
        initView();
    }

    private void initView() {
        mBar = (TitleBar) findViewById(R.id.bar);
        mBar.setTitle(getString(R.string.me_my_collection));
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (NoScrollViewPager) findViewById(R.id.viewpager);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        CollectGoodsFragment fragment1 = new CollectGoodsFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("mode", mode);
        fragment1.setArguments(bundle1);
        fragmentList.add(fragment1);
        String[] titles = new String[]{getString(R.string.me_goods)};

        switch (mode) {
            case mode_statuses_choose:
                break;
            case mode_look:
            case mode_im_choose:
                CollectDongtaiFragment fragment2 = new CollectDongtaiFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("mode", mode);
                fragment2.setArguments(bundle2);
                fragmentList.add(fragment2);
                titles = new String[]{getString(R.string.me_goods), getString(R.string.me_status)};//
                break;
        }
//        if (mode == mode_look) {
//            CollectDongtaiFragment fragment2 = new CollectDongtaiFragment();
//            Bundle bundle2 = new Bundle();
//            bundle2.putInt("mode", mode);
//            fragment2.setArguments(bundle2);
//            fragmentList.add(fragment2);
//            titles = new String[]{"商品", "动态"};//
//        }
        mAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mAdapter);
        if (mode == mode_look || mode == mode_im_choose) {
            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
            mSlidingTabLayout.setDefaultHeightAndPadding(33,0);
            mSlidingTabLayout.setLineWidth(70);
            mSlidingTabLayout.setViewPager(mViewPager);
        }

    }
}
