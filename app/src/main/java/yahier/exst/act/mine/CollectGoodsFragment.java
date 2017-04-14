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
import com.stbl.stbl.act.dongtai.EventStatusesType;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.im.rong.CollectProvider;
import com.stbl.stbl.adapter.mine.CollectGoodsAdapter;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.model.Goods;
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
public class CollectGoodsFragment extends Fragment {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private CollectGoodsAdapter mAdapter;
    private ArrayList<Goods> mDataList;
    private Activity mActivity;

    private int mMode;

    private int mPage = 1;
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

        getCollectGoodsList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();

        mDataList = new ArrayList<>();
        mAdapter = new CollectGoodsAdapter(mDataList);
        mAdapter.setMode(mMode);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getCollectGoodsList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getCollectGoodsList();
            }
        });

        mAdapter.setInterface(new CollectGoodsAdapter.ICollectGoodsAdapter() {
            @Override
            public void onItemClick(Goods item) {
                Intent intent = new Intent(mActivity, MallGoodsDetailAct.class);
                intent.putExtra("goodsid", item.getGoodsid());
                mActivity.startActivity(intent);
            }

            @Override
            public void onDelete(Goods item) {
                showDeleteTipDialog(item.getGoodsid());
            }

            @Override
            public void onAdd(Goods item) {
                EventBus.getDefault().post(new EventStatusesType(item));
                Intent intent = new Intent();
                intent.putExtra("data", item);
                mActivity.setResult(CollectProvider.resultGoodsOk, intent);
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

    private void getCollectGoodsList() {
        CollectionTask.getCollectGoodsList(mPage, COUNT).setCallback(mActivity, mGetCollectGoodsListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Goods>> mGetCollectGoodsListCallback = new SimpleTask.Callback<ArrayList<Goods>>() {
        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<Goods> result) {
            mRefreshListView.reset();
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
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void delete(long goodsid) {
        mLoadingDialog.show();
        CollectionTask.deleteCollectGoods(goodsid).setCallback(mActivity, mDeleteCallback).start();
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
            Goods target = null;
            for (Goods item : mDataList) {
                if (item.getGoodsid() == result.longValue()) {
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
                mPage = 1;
                getCollectGoodsList();
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };
}
