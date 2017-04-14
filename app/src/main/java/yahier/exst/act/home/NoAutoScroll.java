package yahier.exst.act.home;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * 这个ScrollView不能乱滚动，解决scrollView嵌套adapterView后。再次进入页面时，scrollView位置移动的问题。
 * Created by lenovo on 2016/3/14.
 */
public class NoAutoScroll extends ScrollView {


    public NoAutoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

}
