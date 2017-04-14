package yahier.exst.ui.ItemAdapter.vp;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ViewPagerStateFragmentAdapter extends FragmentStatePagerAdapter {
	
	private String[] titles;
	private List<Fragment> arrayList;

	public ViewPagerStateFragmentAdapter(FragmentManager fm,String[] titles,List<Fragment> arrayList) {
		super(fm);
		this.titles = titles;
		this.arrayList = arrayList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return arrayList.get(arg0);
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
	
	/**
	 * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
	 */
	@Override
	public void finishUpdate(ViewGroup container) {
		super.finishUpdate(container);
	}

}
