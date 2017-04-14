package yahier.exst.widget.refresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * Created by Aspsine on 2015/9/9.
 */
public class MyRefreshHeaderView extends SwipeRefreshHeaderLayout {

    private LinearLayout mRefreshingLayout;
    private TextView tvRefresh;

    private int mHeaderHeight;

    private AnimationDrawable mRotateAnim;

    public MyRefreshHeaderView(Context context) {
        this(context, null);
    }

    public MyRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.dp_50);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRefreshingLayout = (LinearLayout) findViewById(R.id.layout_refreshing);
        tvRefresh = (TextView) findViewById(R.id.tvRefresh);
        ImageView arrowIv = (ImageView) findViewById(R.id.iv_arrow);

        mRotateAnim = (AnimationDrawable) arrowIv.getDrawable();
    }

    @Override
    public void onRefresh() {
        tvRefresh.setVisibility(GONE);
        mRefreshingLayout.setVisibility(VISIBLE);
        mRotateAnim.start();
    }

    @Override
    public void onPrepare() {
        Log.d("TwitterRefreshHeader", "onPrepare()");
    }

    @Override
    public void onSwipe(int y, boolean isComplete) {
        if (!isComplete) {
            if (y >= mHeaderHeight) {
                tvRefresh.setText(R.string.refresh_header_hint_ready);
            } else if (y < mHeaderHeight) {
                tvRefresh.setText(R.string.refresh_header_hint_normal);
            }
        }
    }

    @Override
    public void onRelease() {
        Log.d("TwitterRefreshHeader", "onRelease()");
    }

    @Override
    public void complete() {

    }

    @Override
    public void onReset() {
        mRotateAnim.stop();
        mRefreshingLayout.setVisibility(GONE);
        tvRefresh.setVisibility(VISIBLE);
    }

}
