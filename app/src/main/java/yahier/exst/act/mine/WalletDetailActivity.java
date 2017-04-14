package yahier.exst.act.mine;

import android.os.Bundle;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.WalletDetailAdapter;
import com.stbl.stbl.common.ThemeActivity;
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

public class WalletDetailActivity extends ThemeActivity {

    public static final int TYPE_GOLD_BEAN = 1;
    public static final int TYPE_SHITU_TICKET = 3;
    private int mType = 1;

    private static final int COUNT = 10;

    private EmptyView mEmptyView;

    private ArrayList<Currency> mCurrencyList;
    private RefreshListView mRefreshListView;
    private WalletDetailAdapter mAdapter;

    private int mLastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);

        initView();
        getWalletDetailList();
    }

    private void initView() {
        mType = getIntent().getIntExtra(KEY.TYPE, TYPE_GOLD_BEAN);

        setLabel(mType == TYPE_GOLD_BEAN ? getString(R.string.me_gold_bean_detail) : getString(R.string.me_shitu_ticket_detail));

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mCurrencyList = new ArrayList<>();
        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.getTargetView().setDivider(null);
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
