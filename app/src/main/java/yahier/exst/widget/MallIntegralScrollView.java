package yahier.exst.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.stbl.stbl.R;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;

/**
 * 用于积分商场首页，有一个悬浮栏的效果
 * 暂时把效果去掉了，如果要解开需要把onLayout、onInterceptTouchEvent的注释解除；
 * 还需要以SwipeToLoadLayout包围gridview
 * Created by wangdan on 16/2/1.
 */
public class MallIntegralScrollView extends ScrollView {

    private static final String TAG = "MallIntegralScrollView";

    private OnScrollChangeListener mOnScrollChangeListener;

    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;
    private float mInitialMotionY;

    private ViewGroup layBar;
    private SwipeToLoadLayout swipeToLoadLayout;
    private View refreshView;

    private boolean mIsTouch;

    public MallIntegralScrollView(Context context) {
        super(context);
    }

    public MallIntegralScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MallIntegralScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRefreshView(View refreshView) {
        this.refreshView = refreshView;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        // 包含了TabLayout时，拿到焦点总会自动滚动到TabLayout位置
//        super.requestChildFocus(child, focused);
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//
//        if (layBar == null) {
//            layBar = (ViewGroup) findViewById(R.id.rl_laybar);
//            swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_to_load_layout);
//            swipeToLoadLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getHeight() - layBar.getHeight()));
//        }
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = MotionEventCompat.getActionMasked(ev);
//
//        if (refreshView != null) {
//            boolean canChildScrollUp = ViewCompat.canScrollVertically(refreshView, -1);
////            Logger.d(TAG, String.format("canChildScrollUp = %s", String.valueOf(canChildScrollUp)));
//            if (canChildScrollUp) {
//                return false;
//            }
//        }
//
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
//                if (initialMotionY == -1) {
//                    return false;
//                }
//                mInitialMotionY = initialMotionY;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (mActivePointerId == INVALID_POINTER) {
////                    Logger.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
//                    return false;
//                }
//
//                final float y = getMotionEventY(ev, mActivePointerId);
//                if (y == -1) {
//                    return false;
//                }
//                final float yDiff = y - mInitialMotionY;
//                if (yDiff < 0) {
//                    if (getChildAt(0).getHeight() <= getHeight() + getScrollY()) {
//                        return false;
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                mActivePointerId = INVALID_POINTER;
//                break;
//        }
//
//        return super.onInterceptTouchEvent(ev);
//    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mIsTouch = true;
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChanged(y, mIsTouch);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int scrollY, boolean isTouch);
    }

}
