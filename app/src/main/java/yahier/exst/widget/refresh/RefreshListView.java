package yahier.exst.widget.refresh;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;

/**
 * Created by tnitf on 2016/4/11.
 */
public class RefreshListView extends SwipeToLoadLayout {

    private ListView mListView;

    public RefreshListView(Context context) {
        super(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = getTargetView();
    }

    @Override
    public ListView getTargetView() {
        return (ListView) super.getTargetView();
    }

    public void reset() {
        if (isRefreshing()) {
            setRefreshing(false);
        }
        if (isLoadingMore()) {
            setLoadingMore(false);
        }
    }

    public void setDivider(Drawable divider, int height) {
        mListView.setDivider(divider);
        mListView.setDividerHeight(height);
    }

    public void setDivider(int colorId, int height) {
        mListView.setDivider(new ColorDrawable(getResources().getColor(colorId)));
        mListView.setDividerHeight(height);
    }

    public void setDivider1px() {
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_gray_mid)));
        mListView.setDividerHeight(1);
    }



    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setSelection(int position) {
        mListView.setSelection(position);
    }

}
