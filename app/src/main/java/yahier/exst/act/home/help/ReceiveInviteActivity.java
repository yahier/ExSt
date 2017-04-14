package yahier.exst.act.home.help;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.home.ReceiveInviteAdapter;
import com.stbl.stbl.adapter.home.ReceiveInviteAdapter.IReceiveInviteAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.bangyibang.Invited;
import com.stbl.stbl.task.home.HelpTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.SimpleTask.Callback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import java.util.ArrayList;

public class ReceiveInviteActivity extends ThemeActivity {

    private EmptyView mEmptyView;
    private XListView mListView;
    private ArrayList<Invited> mDataList;
    private ReceiveInviteAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;

    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_invite);
        setLabel(R.string.me_receive_invite);

        initView();

        getReceiveInviteList(mPage);
    }

    private void initView() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mListView = (XListView) findViewById(R.id.lv);
        mDataList = new ArrayList<>();
        mAdapter = new ReceiveInviteAdapter(mDataList);
        mListView.setAdapter(mAdapter);

        mLoadingDialog = new LoadingDialog(this);

        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReceiveInviteList(mPage);
            }
        });

        mListView.setOnXListViewListener(new OnXListViewListener() {

            @Override
            public void onRefresh(XListView v) {
                mPage = 1;
                mListView.setPullLoadEnable(true);
                getReceiveInviteList(mPage);
            }

            @Override
            public void onLoadMore(XListView v) {
                mPage++;
                getReceiveInviteList(mPage);
            }
        });

        mAdapter.setInterface(new IReceiveInviteAdapter() {

            @Override
            public void onContactPublisher(int position) {
                Invited invited = mDataList.get(position);
                enterTribePage(invited.getPublisheruserid());
                if (invited.getIscontacted() == 0) {
                    contact(position, invited.getRecommendid());
                }
            }

            @Override
            public void onClickPublisher(int position) {
                Invited invited = mDataList.get(position);
                enterTribePage(invited.getPublisheruserid());
            }

            @Override
            public void onClickRecommendMan(int position) {
                Invited invited = mDataList.get(position);
                enterTribePage(invited.getRecommenderuserid());
            }
        });
    }

    private void enterTribePage(long userId) {
        Intent intent = new Intent(this, TribeMainAct.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void getReceiveInviteList(int page) {
        HelpTask.getReceiveInviteList(page).setCallback(this, mGetInviteCallback)
                .start();
    }

    private SimpleTask.Callback<ArrayList<Invited>> mGetInviteCallback = new Callback<ArrayList<Invited>>() {

        @Override
        public void onError(TaskError e) {
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
            mListView.onRefreshComplete();
            mListView.onLoadMoreComplete();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ArrayList<Invited> result) {
            mListView.onRefreshComplete();
            mListView.onLoadMoreComplete();
            if (mPage == 1) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                    mListView.setVisibility(View.GONE);
                    return;
                }
                mEmptyView.hide();
                mListView.setVisibility(View.VISIBLE);
                mDataList.clear();
            }
            mDataList.addAll(result);
            mAdapter.notifyDataSetChanged();
            if (result.size() < 15) {
                mListView.EndLoad();
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void contact(int position, int recommendId) {
        HelpTask.contact(position, recommendId).setCallback(this, mContactCallback).start();
    }

    private SimpleTask.Callback<Integer> mContactCallback = new Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer position) {
            Invited invited = mDataList.get(position);
            invited.setIscontacted(1);
            long time = System.currentTimeMillis();
            invited.setContacttime((int) (time / 1000));
            mAdapter.notifyDataSetChanged();
            LocalBroadcastHelper.getInstance().send(new Intent(ACTION.HELP_CONTACT_SUCCESS));
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
