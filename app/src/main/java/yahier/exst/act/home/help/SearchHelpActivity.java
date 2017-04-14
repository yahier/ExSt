package yahier.exst.act.home.help;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.home.GiveHelpAdapter;
import com.stbl.stbl.adapter.home.SearchHistoryAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.dialog.IRecommManDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.bangyibang.BangYiBangItem;
import com.stbl.stbl.model.bangyibang.ShareInfo;
import com.stbl.stbl.task.home.HelpTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ThreadPool;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.SearchBar;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

public class SearchHelpActivity extends BaseActivity {

    private SearchBar mSearchBar;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private ArrayList<BangYiBangItem> mDataList;
    private GiveHelpAdapter mAdapter;

    private boolean mIsDestroy;

    private final int mSelectType = 0;
    private int mPage = 1; // 从1开始

    private ListView mHistoryLv;
    private SearchHistoryAdapter mHistoryAdapter;
    private ArrayList<String> mHistoryList;

    private String mKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_help);

        initView();

        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.HELP_RECOMMEND_SUCCESS, ACTION.HELP_ADOPT_SUCCESS);

        querySearchHistory();
    }

    private void initView() {
        mSearchBar = (SearchBar) findViewById(R.id.bar_search);

        mHistoryLv = (ListView) findViewById(R.id.lv_history);
        mHistoryLv.addFooterView(getLayoutInflater().inflate(R.layout.footer_search_history, null));

        mHistoryList = new ArrayList<>();
        mHistoryAdapter = new SearchHistoryAdapter(mHistoryList);
        mHistoryLv.setAdapter(mHistoryAdapter);

        mHistoryLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int size = mHistoryList.size();
                if (position < size) {
                    String keyword = mHistoryList.get(position);
                    mPage = 1;
                    mKeyword = keyword;
                    mSearchBar.setKeyword(mKeyword);
                    mSearchBar.hideKeyboard();

                    mHistoryLv.setVisibility(View.GONE);
                    updateSearchHistory(keyword);

                    mEmptyView.showLoading();
                    mRefreshListView.setVisibility(View.GONE);
                    mDataList.clear();
                    mAdapter.notifyDataSetChanged();
                    getHomeHelpList();
                } else {
                    clearSearchHistory();
                    mSearchBar.showKeyboard();
                }
            }
        });

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPage = 1;
                getHomeHelpList();
            }
        });

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider(null, 0);
        mRefreshListView.setVisibility(View.GONE);
        mDataList = new ArrayList<>();
        mAdapter = new GiveHelpAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getHomeHelpList();
            }
        });

        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getHomeHelpList();
            }
        });

        mAdapter.setInterface(new GiveHelpAdapter.IHelpAdapter() {

            @Override
            public void onClickPublisher(int position) {
                BangYiBangItem item = mDataList.get(position);
                enterTribePage(item.getPublisher().getUserid());
            }

            @Override
            public void onView(int position) {
                BangYiBangItem item = mDataList.get(position);
                Intent intent = new Intent(SearchHelpActivity.this,
                        ReceiveRecommActivity.class);
                intent.putExtra(EXTRA.ISSUE_ID, item.getIssueid());
                intent.putExtra(EXTRA.ISSUE_STATE, item.getIssuestate());
                startActivity(intent);
            }

            @Override
            public void onClose(int position) {
                BangYiBangItem item = mDataList.get(position);
                showCloseTipDialog(position, item.getIssueid());
            }

            @Override
            public void onDelete(int position) {
                BangYiBangItem item = mDataList.get(position);
                showDeleteTipDialog(position, item.getIssueid());
            }

            @Override
            public void onHelp(int position) {
                BangYiBangItem item = mDataList.get(position);
                Intent intent = new Intent(SearchHelpActivity.this,
                        HelpTaActivity.class);
                intent.putExtra(EXTRA.BANG_YI_BANG_ITEM, item);
                startActivity(intent);
            }

            @Override
            public void onClickIRecommMan(int position) {
                BangYiBangItem item = mDataList.get(position);
                showIRecommManDialog(item);
            }
        });

        mSearchBar.setInterface(new SearchBar.ISearchBar() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onSearch(String keyword) {
                mSearchBar.hideKeyboard();
                mKeyword = keyword;
                mHistoryLv.setVisibility(View.GONE);
                updateSearchHistory(keyword);

                mEmptyView.showLoading();
                mRefreshListView.setVisibility(View.GONE);
                mDataList.clear();
                mAdapter.notifyDataSetChanged();
                mPage = 1;
                getHomeHelpList();
            }

            @Override
            public void onClear() {
                mRefreshListView.setVisibility(View.GONE);
                mEmptyView.hide();
                if (mHistoryList.size() > 0) {
                    mHistoryLv.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        writeToFile();
    }

    private void clearSearchHistory() {
        SharedPrefUtils.putToUserFile(KEY.HELP_SEARCH_HISTORY, "[]");
        mHistoryList.clear();
        mHistoryAdapter.notifyDataSetChanged();
        mHistoryLv.setVisibility(View.GONE);
    }

    //更新搜索历史
    private void updateSearchHistory(String keyword) {
        if (mHistoryList.contains(keyword)) {
            mHistoryList.remove(keyword);
        }
        mHistoryList.add(0, keyword);
        if (mHistoryList.size() > 4) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                list.add(mHistoryList.get(i));
            }
            mHistoryList.clear();
            mHistoryList.addAll(list);
        }
        mHistoryAdapter.notifyDataSetChanged();
    }

    //onPause才写文件
    private void writeToFile() {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String array = JSONArray.toJSON(mHistoryList).toString();
                SharedPrefUtils.putToUserFile(KEY.HELP_SEARCH_HISTORY, array);
            }
        });
    }

    private void enterTribePage(long userId) {
        Intent intent = new Intent(this, TribeMainAct.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void showCloseTipDialog(final int position, final long issueid) {
        SystemTipDialog dialog = new SystemTipDialog(this);
        dialog.show();
//        String content = getString(R.string.close_publish_help_tip);
//        BangYiBangItem item = mDataList.get(position);
//        if (DateUtil.isAfterOneHour(item.getPublishtime() * 1000L)) {
//            content = getString(R.string.close_publish_help_tip_1hour);
//        }
        String content = getString(R.string.me_help_close_alert);
        dialog.setContent(content);
        dialog.setInterface(new SystemTipDialog.ISystemTipDialog() {

            @Override
            public void onConfirm() {
                closeHelp(position, issueid);
            }
        });
    }

    private void showDeleteTipDialog(final int position, final long issueid) {
        SystemTipDialog dialog = new SystemTipDialog(this);
        dialog.show();
        dialog.setContent(getString(R.string.delete_publish_help_tip));
        dialog.setInterface(new SystemTipDialog.ISystemTipDialog() {

            @Override
            public void onConfirm() {
                deleteHelp(position, issueid);
            }
        });
    }

    private void showIRecommManDialog(BangYiBangItem item) {
        IRecommManDialog dialog = new IRecommManDialog(this);
        dialog.show();
        dialog.setData(item);
        dialog.setInterface(new IRecommManDialog.IIRecommManDialog() {

            @Override
            public void onClickHead(long userId) {
                enterTribePage(userId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private void getHomeHelpList() {
        HelpTask.getHomeHelpList(mKeyword, mSelectType, mPage).setCallback(this, mGetHelpListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<BangYiBangItem>> mGetHelpListCallback = new SimpleTask.Callback<ArrayList<BangYiBangItem>>() {

        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<BangYiBangItem> result) {
            mHistoryLv.setVisibility(View.GONE);
            mRefreshListView.reset();
            if (mPage == 1) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                    mRefreshListView.setVisibility(View.GONE);
                    return;
                }
                mEmptyView.hide();
                mRefreshListView.setVisibility(View.VISIBLE);
                mDataList.clear();
            }
            mDataList.addAll(result);
            mAdapter.notifyDataSetChanged();
            if (mPage == 1 && mDataList.size() > 0) {
                mRefreshListView.setSelection(0);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void closeHelp(int position, long issueid) {
        HelpTask.closeHelp(position, issueid).setCallback(this, mCloseHelpCallback).start();
    }

    private SimpleTask.Callback<Integer> mCloseHelpCallback = new SimpleTask.Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            int position = result;
            BangYiBangItem item = mDataList.get(position);
            item.setIsclose(1);
            item.setIssuestate(10);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void deleteHelp(int position, long issueid) {
        HelpTask.deleteHelp(position, issueid).setCallback(this, mdeleteHelpCallback).start();
    }

    private SimpleTask.Callback<Integer> mdeleteHelpCallback = new SimpleTask.Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            int position = result;
            mDataList.remove(position);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void querySearchHistory() {
        HelpTask.querySearchHistory().setCallback(this, mQuerySearchHistoryCallback).start();
    }

    private SimpleTask.Callback<ArrayList<String>> mQuerySearchHistoryCallback = new SimpleTask.Callback<ArrayList<String>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ArrayList<String> result) {
            if (result.size() == 0) {
                return;
            }
            mHistoryList.clear();
            mHistoryList.addAll(result);
            mHistoryAdapter.notifyDataSetChanged();
            mHistoryLv.setVisibility(View.VISIBLE);
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
                case ACTION.HELP_RECOMMEND_SUCCESS:
                    afterRecommendSuccess(intent);
                    break;
                case ACTION.HELP_ADOPT_SUCCESS:
                    afterAdoptSuccess(intent);
                    break;
            }
        }
    };

    private void afterRecommendSuccess(Intent intent) {
        long issueid = intent.getLongExtra(EXTRA.ISSUE_ID, 0);
        ShareInfo info = (ShareInfo) intent.getSerializableExtra(EXTRA.SHARE_INFO);

        for (BangYiBangItem item : mDataList) {
            if (item.getIssueid() == issueid) {
                item.setShareinfo(info);
                item.setIsin(1);
                item.setUserintype(20);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void afterAdoptSuccess(Intent intent) {
        long issueid = intent.getLongExtra(EXTRA.ISSUE_ID, 0);
        for (BangYiBangItem item : mDataList) {
            if (item.getIssueid() == issueid) {
                item.setIssuestate(20);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

}
