package yahier.exst.act.dongtai;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.dongtai.DongtaiNotifyAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.DongtaiEventType;
import com.stbl.stbl.item.DongtaiNotifyItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.LoadMoreFooterView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.rong.eventbus.EventBus;

/**
 * 动态的通知
 * Created by lenovo on 2016/6/7.
 */
public class DongtaiNotify extends ThemeActivity implements FinalHttpCallback {

    private SwipeToLoadLayout mSwipeToLoadLayout;
    private EmptyView mEmptyView;
    private ListView mListView;
    private LoadMoreFooterView mFooterView;

    private DongtaiNotifyAdapter mAdapter;
    private List<DongtaiNotifyItem> mData = new ArrayList<>();
    private int lastid; //分页用，最后一条id
    private static final int COUNT = 15; //每页请求的条数
    //从动态右上方图标进入，则可以刷新。从通知栏进入就不刷新
    private boolean isAbleFresh = false;

    /**
     * 是否下拉刷新
     */
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_notify);
        setLabel(R.string.remind);

        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_to_load_layout);
        isAbleFresh = getIntent().getBooleanExtra("isFresh", false);
        if (isAbleFresh) {
            mSwipeToLoadLayout.setRefreshEnabled(true);
        } else {
            mSwipeToLoadLayout.setRefreshEnabled(false);
        }

        mSwipeToLoadLayout.setLoadMoreEnabled(false);
        mSwipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                lastid = 0;
                getNewDynamicData();
            }
        });
        mSwipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!isRefresh) {
                    getNewDynamicData();
                }
            }
        });

        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastid = 0;
                getNewDynamicData();
            }
        });

        mListView = (ListView) findViewById(R.id.swipe_target);

        mFooterView = new LoadMoreFooterView(this);
        mFooterView.setVisibility(View.GONE);
        mListView.addFooterView(mFooterView);
        mListView.setFooterDividersEnabled(false);

        mFooterView.setOnLoadMoreListener(new LoadMoreFooterView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!isRefresh) {
                    getNewDynamicData();
                }
            }
        });
        mAdapter = new DongtaiNotifyAdapter(this, mData);
        mListView.setAdapter(mAdapter);

        EventBus.getDefault().post(new DongtaiEventType(DongtaiEventType.NEW_MESSAGE));
        SharedCommon.putStatusesNotify(false);
        getNewDynamicData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取新的动态操作
     */
    private void getNewDynamicData() {
        Params params = new Params();
        params.put("lastid", lastid);
        params.put("count", COUNT);
        new HttpEntity(this).commonPostData(Method.statusesNewMessage, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        mSwipeToLoadLayout.setRefreshing(false);
        mSwipeToLoadLayout.setLoadingMore(false);
        if (mFooterView.getVisibility() == View.VISIBLE) {
            mFooterView.setBeforeLoad(getString(R.string.look_previous_message));
        }
        if (isRefresh) {
            isRefresh = false;
        }
        if (result == null) return;
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item == null) return;
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getErr() != null)
                ToastUtil.showToast(this, item.getErr().getMsg());
            if (lastid == 0 && mData.size() <= 0) {
                mEmptyView.showRetry();
            }
            return;
        }

        //String json = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.statusesNewMessage: //新通知
                String json = JSONHelper.getStringFromObject(item.getResult());
                int isNew = 0;
                JSONObject jsonValue = null;
                List<DongtaiNotifyItem> data = null;
                try {
                    jsonValue = new JSONObject(json);
                    if (jsonValue != null) {
                        isNew = jsonValue.optInt("isnew");
                        JSONArray dataArr = jsonValue.optJSONArray("statusesremind");
                        data = JSONHelper.getList(dataArr.toString(), DongtaiNotifyItem.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (data != null && data.size() > 0) {
                    if (lastid == 0) {
                        //防止进入后还有推送过来，已经查看，红点没隐藏
                        EventBus.getDefault().post(new DongtaiEventType(DongtaiEventType.NEW_MESSAGE));
                        JPushInterface.clearAllNotifications(this);
                        mData.clear();
                    }
                    lastid = data.get(data.size() - 1).getId();

                    mData.addAll(data);
                    mAdapter.notifyDataSetChanged();

                    if (isNew > 0) {
                        mFooterView.setBeforeLoad(getString(R.string.look_previous_message));
                    } else {
                        mListView.removeFooterView(mFooterView);
                        if (!mSwipeToLoadLayout.isLoadMoreEnabled()) {
                            mSwipeToLoadLayout.setLoadMoreEnabled(true);
                        }
                    }
                } else {
                    if (lastid != 0 && data != null && data.size() == 0) {
                        ToastUtil.showToast(getString(R.string.no_more_message));
                    }
                }
                if (mData.size() == 0) {
                    mEmptyView.showEmpty();
                } else {
                    mEmptyView.hide();
                }
                break;
        }
    }
}
