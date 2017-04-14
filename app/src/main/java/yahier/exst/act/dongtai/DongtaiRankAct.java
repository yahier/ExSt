package yahier.exst.act.dongtai;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.util.Device;

public class DongtaiRankAct extends BaseActivity implements OnClickListener {

	// 定义FragmentTabHost对象
	private TabHost mTabHost;
	// 定义一个布局
	private LayoutInflater layoutInflater;
	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = { DongtaiRankFragment4.class,DongtaiRankFragment1.class, DongtaiRankFragment2.class/* DongtaiRankFragment3.class,*/ /*, DongtaiRankFragment5.class*/ };
	// Tab选项卡的文字
	private String[] mTextviewArray;// = { getString(R.string.rank_people_hub),getString(R.string.rank_active), getString(R.string.rank_earnings) /*"土豪",*/ /*, "帮群"*/ };
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	View viewScrollLine;
	public final static int typeHuoyue = 1;
	public final static int typeBenefit = 2;
	public final static int typeTuhao = 3;
	public final static int typeRenmai = 4;
	public final static int typeGroup = 5;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dongtai_rank_main);
		mTextviewArray = new String[]{getString(R.string.rank_people_hub), getString(R.string.rank_active), getString(R.string.rank_earnings) /*"土豪",*/ /*, "帮群"*/};
		initView();
		viewScrollLine = (View) findViewById(R.id.line_scroll);
	}

	private void scrollLine(int fromX, int endX) {
		TranslateAnimation anim = new TranslateAnimation(fromX, endX, 0, 0);
		anim.setFillAfter(true);
		anim.setDuration(300);
		viewScrollLine.startAnimation(anim);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		findViewById(R.id.top_banner_left).setOnClickListener(this);
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		// 得到fragment的个数
		int count = fragmentArray.length;
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(mTextviewArray.length-1);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabsAdapter.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
			// mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}

	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.dongtai_rank_tab_item, null);
		TextView textView = (TextView) view.findViewById(R.id.dongtai_rank_tab_item);
		textView.setText(mTextviewArray[index]);
		return view;
	}

	public class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Activity mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		int itemWidth1Of5;// 五分之一的宽度;
		int itemWidth2Of5;//
		int itemWidth3Of5;//
		int itemWidth4Of5;//
		int currentIndex = 0;

		final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
			itemWidth1Of5 = Device.getWidth(mContext) / 3;
			itemWidth2Of5 = Device.getWidth(mContext) / 3 * 2;
			itemWidth3Of5 = Device.getWidth(mContext) / 3 * 3;
//			itemWidth4Of5 = Device.getWidth(mContext) / 5 * 4;
		}

		public void addTab(TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);

			switch (position) {
			case 0:
				switch (currentIndex) {
				case 1:
					scrollLine(itemWidth1Of5, 0);
					break;
				case 2:
					scrollLine(itemWidth2Of5, 0);
					break;
				case 3:
					scrollLine(itemWidth3Of5, 0);
					break;
				case 4:
					scrollLine(itemWidth4Of5, 0);
					break;
				}
				break;
			case 1:
				switch (currentIndex) {
				case 0:
					scrollLine(0, itemWidth1Of5);
					break;
				case 2:
					scrollLine(itemWidth2Of5, itemWidth1Of5);
					break;
				case 3:
					scrollLine(itemWidth3Of5, itemWidth1Of5);
					break;
				case 4:
					scrollLine(itemWidth4Of5, itemWidth1Of5);
					break;
				}
				break;
			case 2:
				switch (currentIndex) {
				case 0:
					scrollLine(0, itemWidth2Of5);
					break;
				case 1:
					scrollLine(itemWidth1Of5, itemWidth2Of5);
					break;
				case 3:
					scrollLine(itemWidth3Of5, itemWidth2Of5);
					break;
				case 4:
					scrollLine(itemWidth4Of5, itemWidth2Of5);
					break;
				}
				break;
			case 3:
				switch (currentIndex) {
				case 0:
					scrollLine(0, itemWidth3Of5);
					break;
				case 1:
					scrollLine(itemWidth1Of5, itemWidth3Of5);
					break;
				case 2:
					scrollLine(itemWidth2Of5, itemWidth3Of5);
					break;
				case 4:
					scrollLine(itemWidth4Of5, itemWidth3Of5);
					break;
				}
				break;
			case 4:
				switch (currentIndex) {
				case 0:
					scrollLine(0, itemWidth4Of5);
					break;
				case 1:
					scrollLine(itemWidth1Of5, itemWidth4Of5);
					break;
				case 2:
					scrollLine(itemWidth2Of5, itemWidth4Of5);
					break;
				case 3:
					scrollLine(itemWidth3Of5, itemWidth4Of5);
					break;
				}
				break;
			}

			currentIndex = position;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.top_banner_left:
			finish();
			break;
		}
	}
}
