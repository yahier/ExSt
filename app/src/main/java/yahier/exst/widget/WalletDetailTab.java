package yahier.exst.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.stbl.stbl.R;

/**
 * Created by tnitf on 2016/3/16.
 */
public class WalletDetailTab extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private View mBalanceLine;
    private View mGoldLine;
    private View mGreenLine;

    private ViewPager mViewPager;
    private int mCurrentPage;

    public WalletDetailTab(Context context) {
        this(context, null);
    }

    public WalletDetailTab(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_wallet_detail, this);

        mBalanceLine = findViewById(R.id.line_balance_detail);
        mGoldLine = findViewById(R.id.line_gold_detail);
        mGreenLine = findViewById(R.id.line_green_detail);

        findViewById(R.id.layout_balance_detail).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItem(0);
            }
        });

        findViewById(R.id.layout_gold_detail).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItem(1);
            }
        });

        findViewById(R.id.layout_green_detail).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItem(2);
            }
        });
    }

    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        invalidate();
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        showCurrentLine();
    }

    private void showCurrentLine() {
        mBalanceLine.setVisibility(View.INVISIBLE);
        mGoldLine.setVisibility(View.INVISIBLE);
        mGreenLine.setVisibility(View.INVISIBLE);
        switch (mCurrentPage) {
            case 0:
                mBalanceLine.setVisibility(View.VISIBLE);
                break;
            case 1:
                mGoldLine.setVisibility(View.VISIBLE);
                break;
            case 2:
                mGreenLine.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mCurrentPage = i;
        showCurrentLine();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
