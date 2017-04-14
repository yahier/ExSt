package yahier.exst.act.dongtai;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.dongtai.DongtaiPraiseAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.item.PraiseItem;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserInfo;
import com.stbl.stbl.task.dongtai.DongtaiDetailTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;

import java.util.ArrayList;

/**
 * 动态详情点赞列表
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiPraiseFragment extends BaseFragment {

    private ListView mListView;
    private DongtaiPraiseAdapter mAdapter;
    private EmptyView mEmptyView;

    private ArrayList<PraiseItem> mDataList;

    private int mLastid = 0;
    private final int COUNT = 15;
    private long mStatusesid;

    private SwipeToLoadLayout mSwipeToLoadLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mStatusesid = getArguments().getLong("statusesId", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dongtai_praise, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        //getPraiseList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mListView = (ListView) getView().findViewById(R.id.swipe_target);

        mSwipeToLoadLayout = (SwipeToLoadLayout) getView().findViewById(R.id.swipe_to_load_layout);
        mSwipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getPraiseList();
            }
        });

        mDataList = new ArrayList<>();
        mAdapter = new DongtaiPraiseAdapter(mDataList);
        mListView.setAdapter(mAdapter);

        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastid = 0;
                getPraiseList();
            }
        });
    }

    public ListView getRefreshView() {
        return mListView;
    }

    public void refreshPraiseList() {
        mLastid = 0;
        getPraiseList();
    }

    // 获取评论列表
    private void getPraiseList() {
        mTaskManager.start(DongtaiDetailTask.getPraiseList(mStatusesid, mLastid, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<PraiseItem>>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        if (e.code == ServerError.codeStatusesNotExist) {
                            //show dialog in DongtaiDetailActivity
                        } else {
                            ToastUtil.showToast(e.msg);
                        }
                        if (mLastid == 0 && mDataList.size() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<PraiseItem> result) {
                        if (mLastid == 0) {
                            if (result.size() == 0) {
                                mEmptyView.showEmpty();
                                return;
                            }
                            mDataList.clear();
                        }
                        mDataList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        if (result.size() > 0) {
                            mLastid = result.get(result.size() - 1).getStatusesagreeid();
                        } else {
                            ToastUtil.showToast("没有更多了");
                        }
                    }

                    @Override
                    public void onFinish() {
                        mEmptyView.hide();
                        mSwipeToLoadLayout.setLoadingMore(false);
                    }
                }));
    }

    public void afterPraiseDongtai(PraiseItem item) {
        mEmptyView.hide();
        mDataList.add(0, item);
        mAdapter.notifyDataSetChanged();
    }

    public void afterUnpraiseDongtai() {
        PraiseItem target = null;
        for (PraiseItem item : mDataList) {
            UserInfo user = item.getUser();
            if (user == null) {
                continue;
            }
            if (SharedToken.getUserId().equals(user.getUserid() + "")) {
                target = item;
                break;
            }
        }
        if (target != null) {
            mDataList.remove(target);
            mAdapter.notifyDataSetChanged();
            if (mDataList.size() == 0) {
                mEmptyView.showEmpty();
            }
        }
    }

}
