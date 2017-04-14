package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.mine.HelpSignAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.UserSign;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;

public class SignHelpMeAct extends ThemeActivity {

    private EmptyView mEmptyView;
    private XListView mListView;
    private HelpSignAdapter mAdapter;
    private ArrayList<UserSign> mDataList;

    private long mUserId;
    private int mPage = 1;

    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_help_me);
        setLabel(R.string.me_recently_help_signing);
        mUserId = getIntent().getLongExtra(EXTRA.USER_ID, 0);

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSignUserList();
            }
        });

        mListView = (XListView) findViewById(R.id.list);
        mDataList = new ArrayList<>();
        mAdapter = new HelpSignAdapter(mDataList);
        mListView.setAdapter(mAdapter);

        mListView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
                mPage = 1;
                getSignUserList();
            }

            @Override
            public void onLoadMore(XListView v) {
                mPage++;
                getSignUserList();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > mDataList.size()) {
                    return;
                }
                UserSign sign = mDataList.get(position - 1);
                Intent intent = new Intent(SignHelpMeAct.this, TribeMainAct.class);
                intent.putExtra("userId", sign.getUserid());
                startActivity(intent);
            }
        });

        getSignUserList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getSignUserList() {
        UserTask.getSignUserList(mUserId, mPage, 15).setCallback(this, mGetSignListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<UserSign>> mGetSignListCallback = new SimpleTask.Callback<ArrayList<UserSign>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mListView.onRefreshComplete();
            mListView.onLoadMoreComplete();
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<UserSign> result) {
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


}
