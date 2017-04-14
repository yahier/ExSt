package yahier.exst.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 建立item高度变化的listview。现在用在了动态主页的评论listview上。
 * 
 * @author lenovo
 * 
 */
public class AutoHeightListView extends ListView {

	public AutoHeightListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

}
