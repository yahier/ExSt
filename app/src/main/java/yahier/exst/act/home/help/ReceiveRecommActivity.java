package yahier.exst.act.home.help;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.GiftAdapter;
import com.stbl.stbl.adapter.home.ReceiveRecommAdapter;
import com.stbl.stbl.adapter.home.ReceiveRecommAdapter.IReceiveRecommAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.dialog.SystemTipDialog.ISystemTipDialog;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.bangyibang.Recommend;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.home.HelpTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import java.util.ArrayList;

public class ReceiveRecommActivity extends ThemeActivity {

    private EmptyView mEmptyView;
    private XListView mListView;
    private ArrayList<Recommend> mDataList;
    private ReceiveRecommAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;

    private long mIssueid;
    private int mIssueState;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_recomm);
        setLabel(getString(R.string.me_receive_recommend));

        mIssueid = getIntent().getLongExtra(EXTRA.ISSUE_ID, 0);
        mIssueState = getIntent().getIntExtra(EXTRA.ISSUE_STATE, 0);

        initView();

        getReceiveRecommList(mIssueid, mPage);
    }

    private void initView() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mListView = (XListView) findViewById(R.id.lv);
        mDataList = new ArrayList<>();
        mAdapter = new ReceiveRecommAdapter(mDataList);
        mListView.setAdapter(mAdapter);
        mAdapter.setIssueState(mIssueState);

        mLoadingDialog = new LoadingDialog(this);
        initGiftWindow();

        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReceiveRecommList(mIssueid, mPage);
            }
        });

        mListView.setOnXListViewListener(new OnXListViewListener() {

            @Override
            public void onRefresh(XListView v) {
                mPage = 1;
                getReceiveRecommList(mIssueid, mPage);
            }

            @Override
            public void onLoadMore(XListView v) {
                mPage++;
                getReceiveRecommList(mIssueid, mPage);
            }
        });

        mAdapter.setInterface(new IReceiveRecommAdapter() {

            @Override
            public void onContact(int position) {
                Recommend recommend = mDataList.get(position);
                enterTribePage(recommend.getShareuserid());
                if (recommend.getIscontacted() == 0) {
                    contact(position, recommend.getRecommendid());
                }
            }

            @Override
            public void onAdopt(int position) {
                Recommend recommend = mDataList.get(position);
                showAdoptTipDialog(position, recommend.getRewardbean(), recommend);
            }

            @Override
            public void onReward(int position) {
                Recommend recommend = mDataList.get(position);
                mSendGiftUserId = recommend.getRecommenderuserid();
                LogUtil.logE("推荐人:" + recommend.getRecommendernickname());
                beforeGetGiftList();
            }
        });
    }

    private void enterTribePage(long userId) {
        Intent intent = new Intent(this, TribeMainAct.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void showAdoptTipDialog(final int position, int bounty, final Recommend recommend) {
        SystemTipDialog dialog = new SystemTipDialog(this);
        dialog.show();
        String content = String.format(getString(R.string.me_confirm_adopt_s_s), recommend.getRecommendernickname(), recommend.getShareusername());
        dialog.setContent(Html.fromHtml(content));
        dialog.setInterface(new ISystemTipDialog() {

            @Override
            public void onConfirm() {
                adopt(position, recommend.getRecommendid());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getReceiveRecommList(long issueid, int page) {
        HelpTask.getReceiveRecommList(issueid, page).setCallback(this, mGetRecommCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Recommend>> mGetRecommCallback = new SimpleTask.Callback<ArrayList<Recommend>>() {

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
        public void onCompleted(ArrayList<Recommend> result) {
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

        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void contact(int position, int recommendId) {
        HelpTask.contact(position, recommendId).setCallback(this, mContactCallback).start();
    }

    private SimpleTask.Callback<Integer> mContactCallback = new SimpleTask.Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer position) {
            Recommend recommend = mDataList.get(position);
            recommend.setIscontacted(1);
            long time = System.currentTimeMillis();
            recommend.setContacttime((int) (time / 1000));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void adopt(int position, int recommendId) {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        HelpTask.adopt(position, recommendId).setCallback(this, mAdoptCallback).start();
    }

    private SimpleTask.Callback<Integer> mAdoptCallback = new SimpleTask.Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer position) {
            mLoadingDialog.dismiss();
            Recommend recommend = mDataList.get(position);
            recommend.setIsselected(1);
            long time = System.currentTimeMillis();
            recommend.setSelecttime((int) (time / 1000));
            mAdapter.setIssueState(20);
            mAdapter.notifyDataSetChanged();
            Intent intent = new Intent(ACTION.HELP_ADOPT_SUCCESS);
            intent.putExtra(EXTRA.ISSUE_ID, mIssueid);
            LocalBroadcastHelper.getInstance().send(intent);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    //----------------------------------------------------------------------------

    ArrayList<Gift> mGiftList;
    Dialog mGiftDialog;
    GridView mGiftLv;

    private long mSendGiftUserId;

    /**
     * 获取礼物列表
     */
    void beforeGetGiftList() {
        if (mGiftList == null) {
            getGiftList();
        } else {
            showDaShangDialog(mGiftList);
        }
    }

    private void getGiftList() {
        mLoadingDialog.show();
        CommonTask.getGiftList().setCallback(this, mGetGiftListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Gift>> mGetGiftListCallback = new SimpleTask.Callback<ArrayList<Gift>>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ArrayList<Gift> result) {
            mLoadingDialog.dismiss();
            mGiftList = result;
            showDaShangDialog(mGiftList);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    void showDaShangDialog(ArrayList<Gift> list) {
        if (mGiftDialog.isShowing()) {
            return;
        }
        if (list == null || list.size() == 0) {
            return;
        }
        GiftAdapter adapter = new GiftAdapter(this, list);
        mGiftLv.setAdapter(adapter);
        mGiftDialog.show();
    }

    // 初始化礼物window
    void initGiftWindow() {
        mGiftDialog = new Dialog(this, R.style.Common_Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.award_window, null);

        mGiftLv = (GridView) view.findViewById(R.id.grid);
        mGiftDialog.setContentView(view);
        mGiftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mGiftDialog.dismiss();
                GiftAdapter adapter = (GiftAdapter) mGiftLv.getAdapter();
                sendGift(mSendGiftUserId, adapter.getItem(arg2).getGiftid(), adapter.getItem(arg2).getValue());
            }
        });
    }


    private void sendGift(final long userId, final int giftid, int goldValue) {
        Payment.getPassword(this, goldValue, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                mLoadingDialog.show();
                CommonTask.sendGift(userId, giftid, pwd).setCallback(ReceiveRecommActivity.this, mSendGiftCallback).start();
            }
        });
    }

    private SimpleTask.Callback<Integer> mSendGiftCallback = new SimpleTask.Callback<Integer>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.me_reward_success);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };


}
