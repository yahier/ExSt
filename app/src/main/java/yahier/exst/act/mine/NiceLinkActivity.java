package yahier.exst.act.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.NiceLinkListAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.mine.LinkTask;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/7/26.
 */
public class NiceLinkActivity extends ThemeActivity {

    private static final int COUNT = 15;
    private static final int REQUEST_CODE_ADD_LINK = 1;

    public static final int MODE_VIEW = 0;
    public static final int MODE_CHOOSE = 1;
    public int mMode;

    private EmptyView mEmptyView;
    private ArrayList<LinkBean> mDataList;
    private RefreshListView mRefreshListView;
    private NiceLinkListAdapter mAdapter;

    private UserItem mUser;

    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (UserItem) getIntent().getSerializableExtra("userItem");
        if (mUser == null) {
            finish();
            return;
        }
        mMode = getIntent().getIntExtra(KEY.MODE_NICE_LINK, MODE_VIEW);
        setContentView(R.layout.activity_nice_link);
        setLabel(getString(R.string.me_wonderful_link));
        initView();
        loadData();
    }

    private void initView() {
        if (SharedToken.getUserId(this).equals(String.valueOf(mUser.getUserid()))) {
            if (getIntent().getBooleanExtra("isFromTribe", false) == false) {
                setRightText(getString(R.string.me_add_link), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, MineAddLinkAct.class);
                        startActivityForResult(intent, REQUEST_CODE_ADD_LINK);
                    }
                });
            }
        }
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.pull_to_refresh));

        mDataList = new ArrayList<>();
        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        int padding = DimenUtils.dp2px(16);
        mRefreshListView.getTargetView().setPadding(padding, padding, padding, padding);
        mRefreshListView.getTargetView().setClipToPadding(false);
        mRefreshListView.getTargetView().setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mRefreshListView.setDivider(R.color.transparent, DimenUtils.dp2px(16));
        mAdapter = new NiceLinkListAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                loadData();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData();
            }
        });

        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkBean item = mDataList.get(position);
                switch (mMode) {
                    case MODE_VIEW: {
                        Intent intent = new Intent(mActivity, CommonWeb.class);
                        intent.putExtra("url", item.getLinkurl());
                        intent.putExtra("title", getString(R.string.me_wonderful_link));
                        startActivity(intent);
                    }
                    break;
                    case MODE_CHOOSE: {
                        Intent intent = new Intent();
                        intent.putExtra("data", item);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    break;
                }

            }
        });
    }

    private void loadData() {
        mTaskManager.start(LinkTask.getListData(mUser.getUserid(), mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<LinkBean>>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mDataList.size() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<LinkBean> result) {
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
                        if (result.size() > 0) {
                            mPage++;
                        } else {
                            ToastUtil.showToast(R.string.me_no_more);
                        }
                    }

                    @Override
                    public void onFinish() {
                        mRefreshListView.reset();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_LINK) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                LinkBean link = (LinkBean) data.getSerializableExtra("linkbean");
                if (link != null) {
                    mEmptyView.hide();
                    mDataList.add(0, link);
                    mAdapter.notifyDataSetChanged();
                    mRefreshListView.getTargetView().smoothScrollToPositionFromTop(0, 0);
                }
            }
        }
    }
}
