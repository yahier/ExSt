package yahier.exst.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.stbl.stbl.R;

public class BannerView extends RelativeLayout {

    private AutoScrollViewPager mViewPager;
    private CirclePageIndicator mPageIndicator;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.banner, this);

        mViewPager = (AutoScrollViewPager) findViewById(R.id.vp);
        mPageIndicator = (CirclePageIndicator) findViewById(R.id.cpi);

        mViewPager.setOffscreenPageLimit(2);
    }

    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
        mPageIndicator.setViewPager(mViewPager);
        mViewPager.startAutoScroll();
    }

    public AutoScrollViewPager getViewPager() {
        return mViewPager;
    }

    public void notifyDataSetChanged() {
        mViewPager.notifyDataSetChanged();
        mPageIndicator.setCurrentItem(0);
        mViewPager.startAutoScroll();
    }

    public void setOnPageClickListener(AutoScrollViewPager.OnPageClickListener listener) {
        mViewPager.setOnPageClickListener(listener);
    }

}
