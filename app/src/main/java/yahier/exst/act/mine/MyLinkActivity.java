package yahier.exst.act.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.MyLinkAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.LinkTask;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshGridView;

import java.util.ArrayList;

public class MyLinkActivity extends ThemeActivity {

    private static final int COUNT = 15;
    private static final int REQUEST_ADDLINK = 0x2322;

    private RefreshGridView mRefreshGridView;
    private ArrayList<LinkBean> mDataList;
    private MyLinkAdapter mAdapter;

    private EmptyView mEmptyView;
    private Button mAddImgBtn;

    private long mUserId;

    private int mPage = 1;

    private boolean mIsDestroy;

    private boolean mIsEditMode;

    private LoadingDialog mLoadingDialog;
    private SystemTipDialog mTipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_link);
        setLabel(getString(R.string.me_wonderful_link));

        mUserId = Long.parseLong(SharedToken.getUserId(this));

        initView();
        getLinkList();
    }

    private void initView() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshGridView = (RefreshGridView) findViewById(R.id.refresh_grid_view);
        mRefreshGridView.setNumColumns(2);
        mRefreshGridView.setHorizontalSpacing(16);
        mRefreshGridView.setVerticalSpacing(16);
        mDataList = new ArrayList<>();
        mAdapter = new MyLinkAdapter(mDataList);
        mRefreshGridView.setAdapter(mAdapter);

        mRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkBean item = mDataList.get(position);
                if (mIsEditMode) {
                    mAdapter.toggleChecked(item);
                    mAddImgBtn.setText(String.format(getString(R.string.me_delete_d_link), mAdapter.getSelectedSize()));
                } else {
                    Intent intent = new Intent(MyLinkActivity.this, CommonWeb.class);
                    intent.putExtra("url", item.getLinkurl());
                    intent.putExtra("title", getString(R.string.me_wonderful_link));
                    startActivity(intent);
                }
            }
        });

        mRefreshGridView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getLinkList();
            }
        });
        mRefreshGridView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getLinkList();
            }
        });

        mRefreshGridView.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);

        setRightText(getString(R.string.me_edit), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getCount() == 0) {
                    ToastUtil.showToast(R.string.me_no_link);
                    return;
                }
                toggleEditMode();
            }
        });

        mAddImgBtn = (Button) findViewById(R.id.btn_add_img);
        mAddImgBtn.setText(R.string.me_add_link);
        mAddImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditMode) {
                    beforeDelete();
                } else {
                    Intent intent = new Intent(MyLinkActivity.this, MineAddLinkAct.class);
                    startActivityForResult(intent, REQUEST_ADDLINK);
                }
            }
        });

        mLoadingDialog = new LoadingDialog(this);
        mTipDialog = new SystemTipDialog(this);
        mTipDialog.setTitle(getString(R.string.me_tip));
        mTipDialog.setContent(getString(R.string.me_is_confirm_delete));
        mTipDialog.setInterface(new SystemTipDialog.ISystemTipDialog() {
            @Override
            public void onConfirm() {
                delete();
            }
        });
    }

    private void toggleEditMode() {
        mIsEditMode = !mIsEditMode;
        mAdapter.setIsEditMode(mIsEditMode);
        mRefreshGridView.setRefreshEnabled(!mIsEditMode);
        mRefreshGridView.setLoadMoreEnabled(!mIsEditMode);
        if (mIsEditMode) {
            setRightText(getString(R.string.me_cancel_edit));
            mAddImgBtn.setText(String.format(getString(R.string.me_delete_d_link), 0));
        } else {
            setRightText(getString(R.string.me_edit));
            mAddImgBtn.setText(R.string.me_add_link);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getLinkList() {
        LinkTask.getLinkList(mUserId, mPage, COUNT).setCallback(this, mGetLinkListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<LinkBean>> mGetLinkListCallback = new SimpleTask.Callback<ArrayList<LinkBean>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mRefreshGridView.reset();
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<LinkBean> result) {
            mRefreshGridView.reset();
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
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void beforeDelete() {
        ArrayList<LinkBean> selectedList = mAdapter.getSelectedList();
        if (selectedList.size() == 0) {
            ToastUtil.showToast(R.string.me_not_yet_choose_link);
            return;
        }
        mTipDialog.show();
    }

    private void delete() {
        ArrayList<LinkBean> selectedList = mAdapter.getSelectedList();
        JSONArray linkids = new JSONArray();
        for (LinkBean link : selectedList) {
            linkids.add(link.getLinkid());
        }
        mLoadingDialog.show();
        LinkTask.delete(linkids).setCallback(this, mDeleteCallback).start();
    }

    private SimpleTask.Callback<Integer> mDeleteCallback = new SimpleTask.Callback<Integer>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.me_delete_success);
            mAdapter.deleteSelectedItem();
            if (mAdapter.getCount() == 0) {
                mEmptyView.showEmpty();
                toggleEditMode();
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int imageSpacing = getResources().getDimensionPixelSize(R.dimen.dp_16);
            int columnWidth = (mRefreshGridView.getWidth() - imageSpacing) / 2;
            mAdapter.setItemWidth(columnWidth);
            mAdapter.setItemHeight(columnWidth + DimenUtils.dp2px(48));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mRefreshGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                mRefreshGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_ADDLINK) {
                LinkBean link = (LinkBean) data.getSerializableExtra("linkbean");
                if (link != null) {
                    mEmptyView.hide();
                    mDataList.add(0, link);
                    mAdapter.notifyDataSetChanged();
                    mRefreshGridView.getTargetView().smoothScrollToPositionFromTop(0, 0);
                }
            }
        }
    }

}
