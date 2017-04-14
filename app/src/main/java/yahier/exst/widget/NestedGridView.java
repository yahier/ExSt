package yahier.exst.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

//这个类是为了在ScrollView中嵌套GridView时，可以完全显示GridView
public class NestedGridView extends GridView {

    public NestedGridView(Context context) {
        super(context);
    }

    public NestedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }

}
