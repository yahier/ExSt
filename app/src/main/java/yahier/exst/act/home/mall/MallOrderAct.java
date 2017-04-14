package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.model.GoodsClass;
import com.stbl.stbl.model.OrderStateCount;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.ToastUtil;

import io.rong.eventbus.EventBus;

public class MallOrderAct extends BaseActivity implements OnCheckedChangeListener, OnFinalHttpCallback, OnClickListener {
    private String mStateId;
    private final String STATE_KEY = "STATE_ID";
    private LinearLayout mLayout;
    private View viewScrollLine;
    // private int scrollLoc;
    private LinearLayout tabGroup;

    private MallOrderFrag[] fragArray;
    private ViewPager mViewPager;

    private Activity mContext;
    TextView[] tabs;
    TextView[] tabsText;
    int tabIndex;
    OrderStateCount mStatictics;
    private Handler mHandler = new Handler();
    /**
     * 从其他页面返回需要刷新订单待评价列表
     */
    public static final int REQUEST_REFRESH_TO_5 = 0x1001;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        EventBus.getDefault().register(this);
        mLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.mall_order_main, null);
        setContentView(mLayout);
        fragArray = new MallOrderFrag[5];
        fragArray[0] = new MallOrderFrag();
        fragArray[1] = new MallOrderFrag();
        fragArray[2] = new MallOrderFrag();
        fragArray[3] = new MallOrderFrag();
        fragArray[4] = new MallOrderFrag();

        viewScrollLine = mLayout.findViewById(R.id.line_scroll);
        mLayout.findViewById(R.id.top_left).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        mViewPager = (ViewPager) mLayout.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            private int scrollLoc;

            @Override
            public void onPageSelected(int arg0) {
                int locX = Device.getWidth(mContext) * arg0 / fragArray.length;
                scrollLine(scrollLoc, locX);
                setSelectTextColor(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            // 线条滑动
            private void scrollLine(int fromX, int endX) {
                TranslateAnimation anim = new TranslateAnimation(fromX, endX, 0, 0);
                anim.setFillAfter(true);
                anim.setDuration(300);
                viewScrollLine.startAnimation(anim);
                scrollLoc = endX;
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(fm);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(5);
        tabGroup = (LinearLayout) mLayout.findViewById(R.id.orderGroup);
        tabs = new TextView[5];
        tabsText = new TextView[5];
        for (int i = 0; i < tabGroup.getChildCount(); i++) {
            ViewGroup group = (ViewGroup) tabGroup.getChildAt(i);
            tabs[i] = (TextView) group.getChildAt(1);
            tabsText[i] = (TextView) group.getChildAt(0);
            group.setOnClickListener(this);
        }
        tabIndex = getIntent().getIntExtra("index", 0);
        mViewPager.setCurrentItem(tabIndex);
        getCountData(null);
    }

    // EventBus的回调
    public void onEvent(EventType type) {
        switch (type.getType()) {
            case EventType.TYPE_MALL_NUM_CHANGE:
                getCountData(null);
                break;
            case EventType.TYPE_REFRESH_ORDER_LIST:
                LogUtil.logE("LogUtil", "orderAct onEvent TYPE_REFRESH_ORDER_LIST");
                getCountData(type.getParam());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_REFRESH_TO_5) {
                mViewPager.setCurrentItem(4);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onEvent(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 4));
                    }
                }, 200);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void getCountData(Object param) {
        new HttpUtil(mContext, param).post(Method.showOrderCount, this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio0:
                setSelectTextColor(0);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.radio1:
                setSelectTextColor(1);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.radio2:
                setSelectTextColor(2);
                mViewPager.setCurrentItem(2);
                break;
            case R.id.radio3:
                setSelectTextColor(3);
                mViewPager.setCurrentItem(3);
                break;
            case R.id.radio4:
                setSelectTextColor(4);
                mViewPager.setCurrentItem(4);
                break;
        }
    }

    private void setSelectTextColor(int tabIndex){
        if (tabIndex >= tabsText.length) return;
        for (int i = 0; i < tabsText.length; i++){
            TextView textView = tabsText[i];
            textView.setTextColor(ContextCompat.getColor(this,R.color.gray1));
        }
        TextView textView = tabsText[tabIndex];
        textView.setTextColor(ContextCompat.getColor(this,R.color.theme_red));
    }

//	private void initFragmentManager(Fragment[] fragArray) {
//		FragmentManager fm = getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		for (int i = 0; i < fragArray.length; i++) {
//			if (fragArray[i] == null) {
//				Fragment fra = createFragment(i);
//				ft.add(R.id.pager, fra);
//			}
//		}
//		ft.commit();
//	}

    private void changeFragment(int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < fragArray.length; i++) {
            if (i != index)
                ft.hide(fragArray[i]);
        }
        ft.show(fragArray[index]);
        ft.commit();
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = fragArray[position];
            Bundle bundle = new Bundle();
            bundle.putInt("index", position);
            frag.setArguments(bundle);
            return frag;
        }

        @Override
        public int getCount() {
            return fragArray.length;
        }
    }

//	@Override
//	public void parse(String methodName, String result) {
//		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
//		if (item.getIssuccess() != BaseItem.successTag) {
//			ToastUtil.showToast(mContext, item.getErr().getMsg());
//			return;
//		}
//		switch (methodName) {
//		case Method.showOrderCount:
//			String obj = JSONHelper.getStringFromObject(item.getResult());
//			OrderStateCount count = JSONHelper.getObject(obj, OrderStateCount.class);
//			// if(count==null)return;
//			//int[] ids = { R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4 };
//			int[] values = { count.getWaitpaycount(), count.getWaitsendcount(), count.getWaitreceipcount(), count.getAftersalecount() };
//			for (int i = 0; i < 4; i++) {
//				setNumText(tabs[i + 1], values[i]);
//			}
//
//			break;
//		}
//
//	}

    void setNumText(TextView text, int value) {
        if (value == 0) {
            text.setVisibility(View.INVISIBLE);
        } else {
            text.setVisibility(View.VISIBLE);
            text.setText(String.valueOf(value));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linOrder1:
                setSelectTextColor(0);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.linOrder2:
                setSelectTextColor(1);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.linOrder3:
                setSelectTextColor(2);
                mViewPager.setCurrentItem(2);
                break;
            case R.id.linOrder4:
                setSelectTextColor(3);
                mViewPager.setCurrentItem(3);
                break;
            case R.id.linOrder5:
                setSelectTextColor(4);
                mViewPager.setCurrentItem(4);
                break;
        }
    }

    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        LogUtil.logD("onHttpResponse", methodName + json);
        switch (methodName) {
            case Method.showOrderCount:
                OrderStateCount count = JSONHelper.getObject(json, OrderStateCount.class);
                if (count != null) {
                    int[] values = {count.getWaitpaycount(), count.getWaitsendcount(), count.getWaitreceipcount(), count.getAppraisecount()};
                    for (int i = 0; i < 4; i++) {
                        setNumText(tabs[i + 1], values[i]);
                    }
                    if (handle != null && mStatictics != null && fragArray != null) {
                        int index = (int) handle;
                        fragArray[index].update();
                        if (index != 1 && count.getWaitpaycount() != mStatictics.getWaitpaycount()) {
                            fragArray[1].update();
                        }
                        if (index != 2 && count.getWaitsendcount() != mStatictics.getWaitsendcount()) {
                            fragArray[2].update();
                        }
                        if (index != 3 && count.getWaitreceipcount() != mStatictics.getWaitreceipcount()) {
                            fragArray[3].update();
                        }
                        if (index == 0 || (index != 4 && count.getAftersalecount() != mStatictics.getAftersalecount())) {
                            fragArray[4].update();
                        }
                    }
                    mStatictics = count;
                }
                break;
        }
    }

    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
        LogUtil.logE(methodName + msg);
    }
}
