package yahier.exst.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 监听滑动的scrollview
 * Created by Administrator on 2016/3/29 0029.
 */
public class ScrollChangeScrollView extends ScrollView {
    private OnScrollListener listener;

    public ScrollChangeScrollView(Context context) {
        super(context);
    }

    public ScrollChangeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollChangeScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollListener(OnScrollListener listener){
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (listener != null) listener.onScroll(x,y,oldx,oldy);
    }

    public interface OnScrollListener{
        void onScroll(int x, int y, int oldx, int oldy);
    }

}
