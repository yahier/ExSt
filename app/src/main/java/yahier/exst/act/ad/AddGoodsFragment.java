package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.AddGoodsAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.task.mine.CollectionTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class AddGoodsFragment extends BaseFragment {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private AddGoodsAdapter mAdapter;
    private ArrayList<Goods> mDataList;

    private int mPage = 1;

    private LoadingDialog mLoadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_goods, container, false);
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
        mAdapter = new AddGoodsAdapter(mDataList);
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

        mAdapter.setInterface(new AddGoodsAdapter.ICollectGoodsAdapter() {

            @Override
            public void onAdd(Goods item) {
                Intent intent = new Intent();
                intent.putExtra(KEY.TYPE, PublishShoppingLinkActivity.TYPE_GOODS);
                intent.putExtra(KEY.GOODS, item);
                mActivity.setResult(mActivity.RESULT_OK, intent);
                mActivity.finish();
            }

        });

        mLoadingDialog = new LoadingDialog(mActivity);
    }

    private void getCollectGoodsList() {
        mTaskManager.start(CollectionTask.getAddGoodsList(mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<Goods>>(mActivity) {

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
                    public void onSuccess(ArrayList<Goods> result) {
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
                }));
    }

}
