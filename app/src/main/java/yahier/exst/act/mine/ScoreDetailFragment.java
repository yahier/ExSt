package yahier.exst.act.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.ScoreDetailAdapter;
import com.stbl.stbl.item.Currency;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * 积分明细
 * Created by tnitf on 2016/7/15.
 */
public class ScoreDetailFragment extends Fragment {

    private static final int COUNT = 10;

    private Activity mActivity;

    private EmptyView mEmptyView;

    private boolean mIsDestroy;
    private ArrayList<Currency> mCurrencyList;
    private RefreshListView mRefreshListView;
    private ScoreDetailAdapter mAdapter;

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
        mEmptyView.setRetryText(getString(R.string.pull_to_refresh));

        mCurrencyList = new ArrayList<>();
        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.getTargetView().setDivider(null);
        mAdapter = new ScoreDetailAdapter(mActivity, mCurrencyList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshListView.setLoadMoreEnabled(true);
                mAdapter.setNoMore(false);
                mLastId = 0;
                getBeanCurrencyList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
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
        WalletTask.getBeanCurrencyList(3, mLastId, COUNT).setCallback(mActivity, mGetCurrencyListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Currency>> mGetCurrencyListCallback = new SimpleTask.Callback<ArrayList<Currency>>() {
        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mLastId == 0 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<Currency> result) {
            mRefreshListView.reset();
            if (mLastId == 0) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                } else {
                    mEmptyView.hide();
                }
                mCurrencyList.clear();
            }
            mCurrencyList.addAll(result);
            mAdapter.notifyDataSetChanged();
            if (mCurrencyList.size() > 0) {
                mLastId = mCurrencyList.get(mCurrencyList.size() - 1).getId();
            }
            if (result.size() < COUNT) {
                mAdapter.setNoMore(true);
                mRefreshListView.setLoadMoreEnabled(false);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
