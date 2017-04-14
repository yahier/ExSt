package yahier.exst.act.dongtai;

import android.content.Intent;
import android.os.Bundle;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.dongtai.DongtaiRewardAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.task.dongtai.DongtaiDetailTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/6/15.
 */
public class DongtaiRewardActivity extends ThemeActivity {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private DongtaiRewardAdapter mAdapter;
    private ArrayList<StatusesReward> mDataList;

    private long mStatusesId;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai_reward);
        setLabel("打赏列表");
        mStatusesId = getIntent().getLongExtra("statusesid", 0L);
        if (mStatusesId == 0) {
            finish();
            return;
        }
        initView();
        getRewardList();
    }

    private void initView() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.pull_down_to_retry));

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();
        mDataList = new ArrayList<>();
        mAdapter = new DongtaiRewardAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);

        mAdapter.setInterface(new DongtaiRewardAdapter.AdapterInterface() {
            @Override
            public void onClickHead(StatusesReward item) {
                Intent intent = new Intent(DongtaiRewardActivity.this, TribeMainAct.class);
                intent.putExtra("userId", item.getUser().getUserid());
                startActivity(intent);
            }
        });

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getRewardList();
            }
        });

        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getRewardList();
            }
        });
    }

    private void getRewardList() {
        mTaskManager.start(DongtaiDetailTask.getRewardList(mStatusesId, mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<StatusesReward>>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<StatusesReward> result) {
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
                    public void onFinish() {
                        mRefreshListView.reset();
                    }
                }));
    }
}
