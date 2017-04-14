package yahier.exst.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.stbl.stbl.util.DimenUtils;

/**
 * Created by tnitf on 2016/4/11.
 */
public class RefreshGridView extends SwipeToLoadLayout {

    private GridView mGridView;

    public RefreshGridView(Context context) {
        super(context);
    }

    public RefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mGridView = getTargetView();
    }

    @Override
    public GridView getTargetView() {
        return (GridView) super.getTargetView();
    }

    public void reset() {
        if (isRefreshing()) {
            setRefreshing(false);
        }
        if (isLoadingMore()) {
            setLoadingMore(false);
        }
    }

    public void setHorizontalSpacing(int dp) {
        mGridView.setHorizontalSpacing(DimenUtils.dp2px(dp));
    }

    public void setVerticalSpacing(int dp) {
        mGridView.setVerticalSpacing(DimenUtils.dp2px(dp));
    }

    public void setNumColumns(int num) {
        mGridView.setNumColumns(num);
    }

    public void setAdapter(ListAdapter adapter) {
        mGridView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mGridView.setOnItemClickListener(listener);
    }

}
