package yahier.exst.act.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.mine.FansListAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.AttendCon;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

public class MineFansListAct extends ThemeActivity {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private ArrayList<AttendCon> mDataList;
    private FansListAdapter mAdapter;

    private long mUserId;
    private int mPage = 1;

    private boolean mIsDestroy;

    //  private AddFriendDialog mAddFriendDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_attend_list_atc);
        setLabel(getString(R.string.me_fans_list));

        mUserId = getIntent().getLongExtra(EXTRA.USER_ID, 0);
        if (mUserId == 0) {
            finish();
            return;
        }

        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.FOCUS_UNFOCUS_USER);

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();
        mDataList = new ArrayList<>();
        mAdapter = new FansListAdapter(mDataList, isSelf());
        mRefreshListView.setAdapter(mAdapter);

        mAdapter.setInterface(new FansListAdapter.IFansListAdapter() {
            @Override
            public void onFocusFriend(int position) {
                UserItem user = mDataList.get(position).getUser();
                focus(user.getUserid());
            }
        });

        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttendCon item = mDataList.get(position);
                Intent intent = new Intent(MineFansListAct.this, TribeMainAct.class);
                intent.putExtra("userId", item.getUser().getUserid());
                startActivity(intent);
            }
        });

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getFansList();
            }
        });

        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getFansList();
            }
        });

//        mAddFriendDialog = new AddFriendDialog(this);
//        mAddFriendDialog.setInterface(new AddFriendDialog.IAddFriendDialog() {
//            @Override
//            public void onConfirm(long userId, String msg) {
//                addFriend(userId, msg);
//            }
//        });

        getFansList();
    }

    public boolean isSelf() {
        if (String.valueOf(mUserId).equals(SharedToken.getUserId(this))) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private void getFansList() {
        UserTask.getFansList(mUserId, mPage, COUNT).setCallback(this, mGetFansListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<AttendCon>> mGetFansListCallback = new SimpleTask.Callback<ArrayList<AttendCon>>() {

        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<AttendCon> result) {
            mRefreshListView.reset();
            if (mPage == 1) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                    mEmptyView.setEmptyText(getString(R.string.me_no_fans));
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

//    public void addFriend(long touserid, String reason) {
//        if (TextUtils.isEmpty(reason)) {
//            ToastUtil.showToast("验证信息不能为空");
//            return;
//        }
//        UserTask.addRelation(touserid, reason, UserItem.addRelationTypeFriend).setCallback(this, mAddFriendCallback).start();
//    }
//
//    private SimpleTask.Callback<Integer> mAddFriendCallback = new SimpleTask.Callback<Integer>() {
//        @Override
//        public void onError(TaskError e) {
//            ToastUtil.showToast(e.getMessage());
//        }
//
//        @Override
//        public void onCompleted(Integer result) {
//            ToastUtil.showToast("已发出好友申请");
//        }
//
//        @Override
//        public boolean onDestroy() {
//            return mIsDestroy;
//        }
//    };

    private void focus(long target_userid) {
        UserTask.focus(target_userid).setCallback(this, mFocusCallback).start();
    }

    private SimpleTask.Callback<Long> mFocusCallback = new SimpleTask.Callback<Long>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Long result) {
            for (AttendCon user : mDataList) {
                if (user.getUser().getUserid() == result) {
                    user.setIsattention(Relation.isattention_yes);
                    mAdapter.notifyDataSetChanged();
                    ToastUtil.showToast(getString(R.string.me_focus_success));
                    break;
                }
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.FOCUS_UNFOCUS_USER:
                    afterFocusUnfocus(intent);
                    break;
            }
        }
    };

    private void afterFocusUnfocus(Intent intent) {
        long userId = intent.getLongExtra(KEY.USER_ID, 0L);
        int isFocus = intent.getIntExtra(KEY.IS_FOCUS, 0);
        for (AttendCon con : mDataList) {
            if (con.getUser().getUserid() == userId) {
                con.setIsattention(isFocus);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

}
