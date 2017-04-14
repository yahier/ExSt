package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.mine.AttendListAdapter;
import com.stbl.stbl.adapter.mine.StudentListAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.TudiItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

public class MineTudiListAct extends ThemeActivity {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private ArrayList<TudiItem> mDataList;
    private StudentListAdapter mAdapter;

    private long mUserId;
    private int mPage = 1;

    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_attend_list_atc);
        setLabel(getString(R.string.me_student_list));

        mUserId = getIntent().getLongExtra(EXTRA.USER_ID, 0);
        if (mUserId == 0) {
            finish();
            return;
        }
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();
        mDataList = new ArrayList<>();
        mAdapter = new StudentListAdapter(mDataList,isSelf());
        mRefreshListView.setAdapter(mAdapter);
        mAdapter.setInterface(new StudentListAdapter.IStudentListAdapter() {
            @Override
            public void onAddFriend(int position) {
                UserItem user = mDataList.get(position);

                new AddFriendUtil(MineTudiListAct.this, null).addFriendDirect(user);
                // mAddFriendDialog.setUserInfo(user.getUserid(), user.getNickname());
                // mAddFriendDialog.show();
            }
        });
        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserItem user = mDataList.get(position);
                Intent intent = new Intent(MineTudiListAct.this, TribeMainAct.class);
                intent.putExtra("userId", user.getUserid());
                startActivity(intent);
            }
        });

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getStudentList();
            }
        });

        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getStudentList();
            }
        });

        getStudentList();
    }

    public boolean isSelf(){
        if (String.valueOf(mUserId).equals(SharedToken.getUserId(this))){
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getStudentList() {
        UserTask.getStudentList(mUserId, mPage, COUNT).setCallback(this, mGetStudentListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<TudiItem>> mGetStudentListCallback = new SimpleTask.Callback<ArrayList<TudiItem>>() {

        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<TudiItem> result) {
            mRefreshListView.reset();
            if (mPage == 1) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                    mEmptyView.setEmptyText(getString(R.string.me_no_student));
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

}
