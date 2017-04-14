package yahier.exst.act.ad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.NiceLinkListAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.task.mine.LinkTask;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/14.
 */

public class NiceLinkFragment extends BaseFragment {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private ArrayList<LinkBean> mDataList;
    private RefreshListView mRefreshListView;
    private NiceLinkListAdapter mAdapter;

    private long mUserId;
    private int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_nice_link, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserId = Long.parseLong(SharedToken.getUserId(mActivity));
        initView();
        loadData();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.pull_to_refresh));

        mDataList = new ArrayList<>();
        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        int padding = DimenUtils.dp2px(16);
        mRefreshListView.getTargetView().setPadding(padding, padding, padding, padding);
        mRefreshListView.getTargetView().setClipToPadding(false);
        mRefreshListView.getTargetView().setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mRefreshListView.setDivider(R.color.transparent, DimenUtils.dp2px(16));
        mAdapter = new NiceLinkListAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshListView.setLoadMoreEnabled(true);
                mPage = 1;
                loadData();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData();
            }
        });

        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkBean item = mDataList.get(position);
                Intent intent = new Intent();
                intent.putExtra(KEY.TYPE, PublishShoppingLinkActivity.TYPE_NICE_LINK);
                intent.putExtra(KEY.LINK_BEAN, item);
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }
        });
    }

    private void loadData() {
        mTaskManager.start(LinkTask.getListData(mUserId, mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<LinkBean>>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mDataList.size() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<LinkBean> result) {
                        if (mPage == 1) {
                            if (result.size() == 0) {
                                mEmptyView.showEmpty();
                            } else {
                                mEmptyView.hide();
                            }
                            mDataList.clear();
                        }
                        mDataList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        if (result.size() >= COUNT) {
                            mPage++;
                        } else {
                            mRefreshListView.setLoadMoreEnabled(false);
                        }
                    }

                    @Override
                    public void onFinish() {
                        mRefreshListView.reset();
                    }
                }));
    }
}
