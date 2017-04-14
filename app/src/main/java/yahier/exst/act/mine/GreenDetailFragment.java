package yahier.exst.act.mine;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.GreenDetailAdapter;
import com.stbl.stbl.item.Currency;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GreenDetailFragment extends Fragment {

    private static final int COUNT = 10;

    private Activity mActivity;

    private EmptyView mEmptyView;

    private boolean mIsDestroy;
    private ArrayList<Currency> mCurrencyList;
    private XListView mListView;
    private GreenDetailAdapter mAdapter;

    private int mLastId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        getBeanCurrencyList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastId = 0;
                getBeanCurrencyList();
            }
        });
        mCurrencyList = new ArrayList<>();
        mListView = (XListView) getView().findViewById(R.id.lv);
        mAdapter = new GreenDetailAdapter(mCurrencyList);
        mListView.setAdapter(mAdapter);

        mListView.setOnXListViewListener(new XListView.OnXListViewListener() {

            @Override
            public void onRefresh(XListView v) {
                mLastId = 0;
                getBeanCurrencyList();
            }

            @Override
            public void onLoadMore(XListView v) {
                mLastId = mCurrencyList.get(mCurrencyList.size() - 1).getId();
                getBeanCurrencyList();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getBeanCurrencyList() {
        WalletTask.getBeanCurrencyList(2, mLastId, COUNT).setCallback(mActivity, mGetCurrencyListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Currency>> mGetCurrencyListCallback = new SimpleTask.Callback<ArrayList<Currency>>() {
        @Override
        public void onError(TaskError e) {
            mListView.onRefreshComplete();
            mListView.onLoadMoreComplete();
            ToastUtil.showToast(e.getMessage());
            if (mLastId == 0 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<Currency> result) {
            mListView.onRefreshComplete();
            mListView.onLoadMoreComplete();
            if (mLastId == 0) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                    mListView.setVisibility(View.GONE);
                    return;
                }
                mEmptyView.hide();
                mListView.setVisibility(View.VISIBLE);
                mCurrencyList.clear();
            }
            mCurrencyList.addAll(result);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
