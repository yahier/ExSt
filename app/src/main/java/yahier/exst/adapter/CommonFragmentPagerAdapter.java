package yahier.exst.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList;
    private String[] mTitleArr;

    public CommonFragmentPagerAdapter(FragmentManager fm,
                                      ArrayList<Fragment> fragmentList, String[] titleArr) {
        super(fm);
        mFragmentList = fragmentList;
        mTitleArr = titleArr;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleArr == null) return "";
        return mTitleArr[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}
