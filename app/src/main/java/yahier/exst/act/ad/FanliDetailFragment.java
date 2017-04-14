package yahier.exst.act.ad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.ad.FanliDetailAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.model.MoneyFlowItem;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/29.
 */

public class FanliDetailFragment extends BaseFragment {

    private static final int COUNT = 10;

    private EmptyView mEmptyView;

    private ArrayList<MoneyFlowItem> mList;
    private RefreshListView mRefreshListView;
    private FanliDetailAdapter mAdapter;

    private int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fanli_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getFanliDetailList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mList = new ArrayList<>();
        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.getTargetView().setDivider(null);
        mAdapter = new FanliDetailAdapter(mList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshListView.setLoadMoreEnabled(true);
                mAdapter.showNoMore(false);
                mPage = 1;
                getFanliDetailList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getFanliDetailList();
            }
        });
    }

    private void getFanliDetailList() {
        mTaskManager.start(AdTask.getFanliDetailList(mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<MoneyFlowItem>>(mActivity) {

                    @Override
                    public void onFinish() {
                        mRefreshListView.reset();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<MoneyFlowItem> result) {
                        if (mPage == 1) {
                            if (result.size() == 0) {
                                mEmptyView.showEmpty();
                            } else {
                                mEmptyView.hide();
                            }
                            mList.clear();
                        }
                        mList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        if (result.size() >= COUNT) {
                            mPage++;
                        } else {
                            mAdapter.showNoMore(true);
                            mRefreshListView.setLoadMoreEnabled(false);
                        }
                    }
                }));
    }

}
