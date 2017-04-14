package yahier.exst.widget.refresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.stbl.stbl.R;

/**
 * Created by Aspsine on 2015/9/2.
 */
public class MyLoadMoreFooterView extends SwipeLoadMoreFooterLayout {

    private AnimationDrawable mRotateAnim;
    private int mFooterHeight;

    public MyLoadMoreFooterView(Context context) {
        this(context, null);
    }

    public MyLoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.dp_50);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ImageView arrowIv = (ImageView) findViewById(R.id.iv_arrow);
        mRotateAnim = (AnimationDrawable) arrowIv.getDrawable();
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onSwipe(int y, boolean isComplete) {
        if (!isComplete) {

        }
    }

    @Override
    public void onLoadMore() {
        mRotateAnim.start();
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void onReset() {
        mRotateAnim.stop();
    }
}
