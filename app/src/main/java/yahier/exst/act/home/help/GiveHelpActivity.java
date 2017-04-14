package yahier.exst.act.home.help;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.BannerPagerAdapter;
import com.stbl.stbl.adapter.home.GiveHelpAdapter;
import com.stbl.stbl.adapter.home.GiveHelpAdapter.IHelpAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.IRecommManDialog;
import com.stbl.stbl.dialog.IRecommManDialog.IIRecommManDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.dialog.SystemTipDialog.ISystemTipDialog;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.bangyibang.BangYiBangItem;
import com.stbl.stbl.model.bangyibang.ShareInfo;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.home.HelpTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.SimpleTask.Callback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.AutoScrollViewPager;
import com.stbl.stbl.widget.BannerView;
import com.stbl.stbl.widget.DropDownMenu;
import com.stbl.stbl.widget.DropDownMenu.DropDownMenuItem;
import com.stbl.stbl.widget.DropDownMenu.OnMenuClickListener;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class GiveHelpActivity extends ThemeActivity {

    private Button mReceiveBtn;

    private View mHeaderView;
    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private ArrayList<BangYiBangItem> mDataList;
    private GiveHelpAdapter mAdapter;

    private BannerView mBannerView;
    private ArrayList<Banner> mBannerList;
    private BannerPagerAdapter mBannerAdapter;

    private LoadingDialog mLoadingDialog;

    private DropDownMenu mDropDownMenu;

    private boolean mIsDestroy;

    private int mSelectType = 0; // 0-全部，1-我发起的，2-我参与的
    private int mPage = 1; // 从1开始

    private String[] mTitleArray = new String[]{Res.getString(R.string.me_help_title_all),
            Res.getString(R.string.me_help_title_i_post),
            Res.getString(R.string.me_help_title_i_join)};

    private int mReceiveInviteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_help);

        initView();

        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.HELP_PUBLISH_SUCCESS, ACTION.HELP_RECOMMEND_SUCCESS,
                ACTION.HELP_ADOPT_SUCCESS, ACTION.HELP_CONTACT_SUCCESS, ACTION.HELP_TAKE_PART_IN_SUCCESS);

        getBannerList();
        getHomeHelpList();
    }

    private void initView() {
        setLabel(getString(R.string.me_help_title_all));
        setRightImage(R.drawable.tribe_tiny_info_more, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDropDownMenu.show(findViewById(R.id.theme_top_banner_right));
            }
        });

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mHeaderView = getLayoutInflater().inflate(R.layout.header_community, null);
        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider(null, 0);
        mHeaderView.setVisibility(View.GONE);
        mRefreshListView.getTargetView().addHeaderView(mHeaderView);
        mDataList = new ArrayList<>();
        mAdapter = new GiveHelpAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);

        mReceiveBtn = (Button) findViewById(R.id.btn_receive_invite);

        mLoadingDialog = new LoadingDialog(this);

        initPopMenu();

        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBannerList.size() == 0) {
                    getBannerList();
                }
                mPage = 1;
                getHomeHelpList();
            }
        });

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

        mBannerList = new ArrayList<>();
        mBannerView = (BannerView) mHeaderView.findViewById(R.id.banner);
        mBannerView.setVisibility(View.GONE);
        TextView searchTv = (TextView) mHeaderView.findViewById(R.id.et_search);
        searchTv.setText(R.string.me_help_search_hint);
        searchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(GiveHelpActivity.this, UmengClickEventHelper.BYBSS);
                startActivity(new Intent(GiveHelpActivity.this, SearchHelpActivity.class));
            }
        });
        mBannerAdapter = new BannerPagerAdapter(mBannerList);
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager pager, int position) {
                try {
                    Banner banner = mBannerList.get(position);
                    String url = banner.getParame();
                    String title = banner.getTitle();
                    AppUtils.routerActivity(GiveHelpActivity.this, url, title);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mBannerView.post(new Runnable() {
            @Override
            public void run() {
                mBannerView.getLayoutParams().height = (Device.getWidth() - DimenUtils.dp2px(80)) / 2;
            }
        });

        mReceiveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(GiveHelpActivity.this,UmengClickEventHelper.SDDYQ);
                startActivity(new Intent(GiveHelpActivity.this, ReceiveInviteActivity.class));
            }
        });

        findViewById(R.id.btn_find_people).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(GiveHelpActivity.this,UmengClickEventHelper.WYZR);
                startActivity(new Intent(GiveHelpActivity.this, FindSomeoneActivity.class));
            }
        });

        mAdapter.setInterface(new IHelpAdapter() {

            @Override
            public void onClickPublisher(int position) {
                BangYiBangItem item = mDataList.get(position);
                enterTribePage(item.getPublisher().getUserid());
            }

            @Override
            public void onView(int position) {
                BangYiBangItem item = mDataList.get(position);
                Intent intent = new Intent(GiveHelpActivity.this,
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
                MobclickAgent.onEvent(GiveHelpActivity.this,UmengClickEventHelper.BTA);
                BangYiBangItem item = mDataList.get(position);
                Intent intent = new Intent(GiveHelpActivity.this,
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

    }

    private void enterTribePage(long userId) {
        Intent intent = new Intent(this, TribeMainAct.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void initPopMenu() {
        ArrayList<DropDownMenuItem> itemList = new ArrayList<DropDownMenuItem>();
        itemList.add(new DropDownMenuItem(getString(R.string.me_help_all)));
        itemList.add(new DropDownMenuItem(getString(R.string.me_help_i_post)));
        itemList.add(new DropDownMenuItem(getString(R.string.me_help_i_join)));
        itemList.add(new DropDownMenuItem(getString(R.string.me_help_rule)));
        mDropDownMenu = DropDownMenu.newInstance(itemList,
                new OnMenuClickListener() {

                    @Override
                    public void onMenuClick(View view, int position) {
                        if (position == 3) {
                            String url = (String) SharedPrefUtils.getFromPublicFile(KEY.bang_instrod, "");
                            Intent intent = new Intent(GiveHelpActivity.this, CommonWeb.class);
                            intent.putExtra("url", url);
                            intent.putExtra("title", getString(R.string.me_help_rule));
                            startActivity(intent);
                        } else {
                            afterSelectMenu(position);
                        }
                    }
                });
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
        dialog.setInterface(new ISystemTipDialog() {

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
        dialog.setInterface(new ISystemTipDialog() {

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
        dialog.setInterface(new IIRecommManDialog() {

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

    private void getBannerList() {
        CommonTask.getBannerList(9).setCallback(this, mGetBannerListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Banner>> mGetBannerListCallback = new SimpleTask.Callback<ArrayList<Banner>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ArrayList<Banner> result) {
            if (result.size() > 0) {
                mBannerList.clear();
                mBannerList.addAll(result);
                mBannerView.notifyDataSetChanged();
                mBannerView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void getHomeHelpList() {
        HelpTask.getHomeHelpList(null, mSelectType, mPage).setCallback(this, mGetHelpListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<BangYiBangItem>> mGetHelpListCallback = new Callback<ArrayList<BangYiBangItem>>() {

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
            mRefreshListView.reset();
            if (mPage == 1) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                    switch (mSelectType) {
                        case 0:      //全部
                            //mEmptyView.setEmptyImage(R.drawable.empty_take_part_in);
                            mEmptyView.setEmptyText(getString(R.string.me_no_data));
                            break;
                        case 1:      //我发起的
                            //mEmptyView.setEmptyImage(R.drawable.empty_publish);
                            mEmptyView.setEmptyText(getString(R.string.me_not_yet_post_find_people_demand));
                            break;
                        case 2:      //我参与的
                            //mEmptyView.setEmptyImage(R.drawable.empty_take_part_in);
                            mEmptyView.setEmptyText(getString(R.string.me_not_yet_help_people));
                            break;
                    }
                } else {
                    mHeaderView.setVisibility(View.VISIBLE);
                    mEmptyView.hide();
                }
                mDataList.clear();
            }
            mDataList.addAll(result);
            mAdapter.notifyDataSetChanged();
            if (mPage == 1 && mDataList.size() > 0) {
                mRefreshListView.setSelection(0);
            }
            if (mSelectType == 0 && result.size() > 0) {
                mReceiveInviteCount = result.get(0).getRequestcount();
                setReceiveInviteCount(mReceiveInviteCount);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void setReceiveInviteCount(int count) {
        if (count > 0) {
            mReceiveBtn.setText(String.format(getString(R.string.me_receive_invite_d), count));
        } else {
            mReceiveBtn.setText(R.string.me_receive_invite);
        }
    }

    private void closeHelp(int position, long issueid) {
        HelpTask.closeHelp(position, issueid).setCallback(this, mCloseHelpCallback).start();
    }

    private SimpleTask.Callback<Integer> mCloseHelpCallback = new Callback<Integer>() {

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

    private SimpleTask.Callback<Integer> mdeleteHelpCallback = new Callback<Integer>() {

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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.HELP_PUBLISH_SUCCESS:
                    afterSelectMenu(1);
                    break;
                case ACTION.HELP_RECOMMEND_SUCCESS:
                    afterRecommendSuccess(intent);
                    break;
                case ACTION.HELP_ADOPT_SUCCESS:
                    afterAdoptSuccess(intent);
                    break;
                case ACTION.HELP_CONTACT_SUCCESS:
                    setReceiveInviteCount(mReceiveInviteCount - 1);
                    break;
                case ACTION.HELP_TAKE_PART_IN_SUCCESS:
                    afterSelectMenu(2);
                    break;
            }
        }
    };

    private void afterSelectMenu(int position) {
        setLabel(mTitleArray[position]);
        mSelectType = position;
        mPage = 1;
        mDataList.clear();
        mAdapter.notifyDataSetChanged();
        mEmptyView.showLoading();
        getHomeHelpList();
    }

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
