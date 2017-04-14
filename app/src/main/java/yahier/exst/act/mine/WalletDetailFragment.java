package yahier.exst.act.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.WalletDetailAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.item.Currency;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/8.
 */

public class WalletDetailFragment extends BaseFragment {

    public static final int TYPE_GOLD_BEAN = 1;
    public static final int TYPE_SHITU_TICKET = 3;
    private int mType = 1;

    private static final int COUNT = 10;

    private EmptyView mEmptyView;

    private ArrayList<Currency> mCurrencyList;
    private RefreshListView mRefreshListView;
    private WalletDetailAdapter mAdapter;

    private int mLastId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_wallet_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getWalletDetailList();
    }

    private void initView() {
        mType = getArguments().getInt(KEY.TYPE, TYPE_GOLD_BEAN);

        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mCurrencyList = new ArrayList<>();
        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.getTargetView().setDivider(null);
        View headerView = LayoutInflater.from(mActivity).inflate(R.layout.header_wallet_detail, null);
        TextView titleTv = (TextView) headerView.findViewById(R.id.tv_title);
        if (mType == TYPE_SHITU_TICKET) {
            titleTv.setText(R.string.me_shitu_ticket_history);
        }
        mRefreshListView.getTargetView().addHeaderView(headerView);
        mAdapter = new WalletDetailAdapter(mCurrencyList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshListView.setLoadMoreEnabled(true);
                mAdapter.showNoMore(false);
                mLastId = 0;
                getWalletDetailList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getWalletDetailList();
            }
        });
    }

    private void getWalletDetailList() {
        mTaskManager.start(WalletTask.getWalletDetailList(mType, mLastId, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<Currency>>(mActivity) {

                    @Override
                    public void onFinish() {
                        mRefreshListView.reset();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mLastId == 0 && mAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<Currency> result) {
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
                            mAdapter.showNoMore(true);
                            mRefreshListView.setLoadMoreEnabled(false);
                        }
                    }
                }));
    }

}
