package yahier.exst.ui.BaseClass;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.stbl.stbl.ui.BaseClass.baseInterface.STBLTableInterface;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meteorshower on 16/3/24.
 */
public abstract class STBLBaseTableActivity<T> extends STBLBaseActivity implements STBLTableInterface{

    protected XListView mListView;
    protected BaseAdapter tableAdapter;
    protected List<T> arrayList = new ArrayList<T>();

    public void bindRefreshList(int resourcesId, BaseAdapter _tableAdapter){
        this.mListView = (XListView)findViewById(resourcesId);
        bindRefreshList(mListView, _tableAdapter, true);
    }

    public void bindRefreshList(XListView _mListView, BaseAdapter _tableAdapter, boolean moreType){
        this.mListView = _mListView;
        this.tableAdapter = _tableAdapter;

        this.mListView.setAdapter(this.tableAdapter);
        this.mListView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
                STBLBaseTableActivity.this.onReload();
            }

            @Override
            public void onLoadMore(XListView v) {
                STBLBaseTableActivity.this.loadMore();
            }
        });
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int fristCount = mListView.getHeaderViewsCount();

                int dataCount = STBLBaseTableActivity.this.tableAdapter.getCount();

                if (position >= fristCount + dataCount || position < fristCount)
                    return;

                STBLBaseTableActivity.this.onItemClick(parent, view, position - fristCount, id);
            }
        });
    }

    public void setPullLoad(boolean bean){
        this.mListView.setPullLoadEnable(bean);
        this.mListView.setPullRefreshEnable(bean);
    }

    public abstract void onReload();

    public abstract void loadMore();

    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public void startRefresh() {
        if (mListView != null)
            mListView.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        if (mListView != null) {
            mListView.onRefreshComplete();
            mListView.onLoadMoreComplete();
        }
    }

    public XListView getRefreshView(){
        return mListView;
    }

//    public void assemblePareData(List<T> arryaList){
//        this.arrayList = arryaList;
//    }
}
