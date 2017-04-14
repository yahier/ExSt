package yahier.exst.act.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.EventStatusesType;
import com.stbl.stbl.act.im.rong.CollectProvider;
import com.stbl.stbl.adapter.mine.CollectDongtaiAdapter;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.CollectionTask;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

import io.rong.eventbus.EventBus;

/**
 * Created by tnitf on 2016/4/20.
 */
public class CollectDongtaiFragment extends Fragment {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private CollectDongtaiAdapter mAdapter;
    private ArrayList<StatusesCollect> mDataList;
    private Activity mActivity;

    private int mMode;

    private long mLastId = 0;
    private boolean mIsDestroy;

    private LoadingDialog mLoadingDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mMode = getArguments().getInt("mode", MyCollectionActivity.mode_look);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receive_gift, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        getCollectDongtaiList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();

        mDataList = new ArrayList<>();
        mAdapter = new CollectDongtaiAdapter(mDataList);
        mAdapter.setMode(mMode);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLastId = 0;
                getCollectDongtaiList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mLastId = mDataList.get(mDataList.size() - 1).getStatusescollectid();
                getCollectDongtaiList();
            }
        });

        mAdapter.setInterface(new CollectDongtaiAdapter.ICollectDongtaiAdapter() {
            @Override
            public void onItemClick(StatusesCollect item) {
                Intent intent = new Intent(mActivity, DongtaiDetailActivity.class);
                intent.putExtra("statusesId", item.getStatuses().getStatusesid());
                mActivity.startActivity(intent);
            }

            @Override
            public void onDelete(StatusesCollect item) {
                showDeleteTipDialog(item.getStatuses().getStatusesid());
            }

            @Override
            public void onAdd(StatusesCollect item) {
                EventBus.getDefault().post(new EventStatusesType(item));
                //动态用上面,群里点击用下面
                Intent intent = new Intent();
                intent.putExtra("data", item);
                mActivity.setResult(CollectProvider.resultStatusesOk, intent);
                mActivity.finish();
            }

        });

        mLoadingDialog = new LoadingDialog(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void showDeleteTipDialog(final long goodsid) {
        SystemTipDialog dialog = new SystemTipDialog(mActivity);
        dialog.show();
        dialog.setTitle(getString(R.string.me_tip));
        dialog.setContent(getString(R.string.me_is_confirm_delete));
        dialog.setInterface(new SystemTipDialog.ISystemTipDialog() {

            @Override
            public void onConfirm() {
                delete(goodsid);
            }
        });
    }

    private void getCollectDongtaiList() {
        CollectionTask.getCollectDongtaiList(mLastId, COUNT).setCallback(mActivity, mGetCollectDongtaiListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<StatusesCollect>> mGetCollectDongtaiListCallback = new SimpleTask.Callback<ArrayList<StatusesCollect>>() {
        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mLastId == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<StatusesCollect> result) {
            mRefreshListView.reset();
            if (mLastId == 0) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                } else {
                    mEmptyView.hide();
                }
                mDataList.clear();
            }
            mDataList.addAll(result);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void delete(long statusesid) {
        mLoadingDialog.show();
        CollectionTask.deleteCollectDongtai(statusesid).setCallback(mActivity, mDeleteCallback).start();
    }

    private SimpleTask.Callback<Long> mDeleteCallback = new SimpleTask.Callback<Long>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Long result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.me_delete_success);
            StatusesCollect target = null;
            for (StatusesCollect item : mDataList) {
                if (item.getStatuses().getStatusesid() == result.longValue()) {
                    target = item;
                    break;
                }
            }
            if (target != null) {
                mDataList.remove(target);
                mAdapter.notifyDataSetChanged();
            }
            if (mAdapter.getCount() == 0) {
                mEmptyView.showLoading();
                mLastId = 0;
                getCollectDongtaiList();
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };


}
