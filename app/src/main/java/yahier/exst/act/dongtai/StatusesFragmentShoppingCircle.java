package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.BrandPlusActivity;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.adapter.ShoppingCircleAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.Message;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.AdUserItem2;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.item.ad.ShoppingCircleListItem;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by lenovo on 2016/9/21
 */
public class StatusesFragmentShoppingCircle extends Fragment implements View.OnClickListener, XListView.OnXListViewListener, FinalHttpCallback, ShoppingCircleAdapter.OnStatesesListener {
    private Activity mActivity;
    private View view;
    XListView listView;
    int lastStatusesId;
    List<ShoppingCircleDetail> list;
    ShoppingCircleAdapter adapter;
    boolean isFresh = true;//默认应该是true
    public final static int requestDetailDongtai = 100;
    final int resultUpdateCode = 102;// 更新数据
    final int resultDeleteCode = 104;
    GetNewStatusesReceiver receiver;

    private DataCacheDB cacheDB;

    public final static int typeBrowse = 1;
    public final static int typeManage = 2;
    int type = typeBrowse;
    int page = 1;
    UserItem userItem;
    private EmptyView mEmptyView;
    private ImageView mRPTaskIn;//红包解锁任务入口

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        home = (TabHome) mActivity.getParent();
        receiver = new GetNewStatusesReceiver();
        mActivity.registerReceiver(receiver, new IntentFilter(ACTION.ADD_SHOPPING_CIRCLE));
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.DELETE_SHOPPING_CIRCLE);
        cacheDB = new DataCacheDB(activity);
        if (getArguments() != null) {
            userItem = (UserItem) getArguments().getSerializable("userItem");
        }

    }


    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
        LogUtil.logE("StatusesFragment", "onDestroy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_statuses_hongbao, container, false);
        Bundle bundle = getArguments();
        if (bundle != null)
            type = bundle.getInt("type", typeBrowse);
        return view;
    }

    TabHome home;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (XListView) view.findViewById(R.id.main_dontai_list);
        listView.setOnXListViewListener(this);
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        if (type == typeBrowse) {
            View headerVideo = LayoutInflater.from(mActivity).inflate(R.layout.dongtai_shopping_circle_brand_layout, null);
            //headerVideo.setPadding((int)getResources().getDimension(R.dimen.dp_12),(int)getResources().getDimension(R.dimen.dp_12),(int)getResources().getDimension(R.dimen.dp_12),(int)getResources().getDimension(R.dimen.dp_12));
            headerVideo.findViewById(R.id.linBrands).setOnClickListener(this);
            mRPTaskIn = (ImageView) headerVideo.findViewById(R.id.iv_unlock_task);
            mRPTaskIn.setOnClickListener(this);
            listView.addHeaderView(headerVideo);
            adapter = new ShoppingCircleAdapter(mActivity, type);
            listView.setAdapter(adapter);
            updateDynamicList(cacheDB.getShoppingCirclrCacheJson());
        }
        if (adapter == null) {
            adapter = new ShoppingCircleAdapter(mActivity, type);
            listView.setAdapter(adapter);
        }
        adapter.setOnStatesesListener(this);
        //getRelatedList();

        //滑动时，不显示动态悬浮框。
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (home == null) return;
                switch(scrollState) {
                    case SCROLL_STATE_IDLE:
                        home.setStatusesMoreVisibility(View.VISIBLE);
                        break;
                    case SCROLL_STATE_FLING:
                        home.setStatusesMoreVisibility(View.GONE);
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        home.setStatusesMoreVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //adapter.checkIsPauseVideo(firstVisibleItem - 1, visibleItemCount + firstVisibleItem - 2);

            }
        });
        EventBus.getDefault().register(this);
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 1500);
        getRpPermission();
    }

    public void onEvent(EventTypeCommon type) {
        switch(type.getType()) {
            case EventTypeCommon.typeUpdateStatusesShoppingCircle:
                if (adapter.getCount() > 0) {
                    listView.setAutoFresh();//add new
                    break;
                }
                listView.smoothScrollToPosition(0);
                refresh();
                break;
            case EventTypeCommon.typeToTopStatusesShoppingCircle:
                listView.smoothScrollToPosition(0);
                break;
            case EventTypeCommon.typeGetStatusesShoppingCircle:
                if (adapter.getCount() > 0)
                    return;
                listView.smoothScrollToPosition(0);
                refresh();
                break;
            //设置了品牌后，重新刷新

            case EventTypeCommon.typeUpdateBrand:
                if (this.type == typeManage) {
                    listView.smoothScrollToPosition(0);
                    refresh();
                    break;
                }
            case EventTypeCommon.typeRefreshWeb: //刷新红包系统权限
                getRpPermission();
                break;

        }


    }
    //获取红包系统权限
    private void getRpPermission(){
        new HttpEntity(mActivity).commonRedpacketPostData(Method.redpacketGetPermission, new Params(), this);
    }

    long sortid;
    boolean isGetServerData = false;

    void getRelatedList() {
        if (isGetServerData) return;
        isGetServerData = true;
        Params params = new Params();
        switch(type) {
            case typeBrowse:
                if (sortid != 0)
                    params.put("sortid", sortid);
                params.put("page", page);
                params.put("count", Statuses.requestCount);//
                new HttpEntity(mActivity).commonRedpacketPostData(Method.getV2ShoppingCircieList, params, this);
                break;
            case typeManage:
                params.put("lastid", lastStatusesId);
                if (userItem != null)
                    params.put("objuserid", userItem.getUserid());
                params.put("count", Statuses.requestCount);//
                new HttpEntity(mActivity).commonPostData(Method.getV2MyShoppingCircleList, params, this);
                break;
        }
    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        refresh();
    }


    void refresh() {
        isFresh = true;
        page = 1;
        sortid = 0;
        lastStatusesId = 0;
        getRelatedList();
    }

    @Override
    public void onLoadMore(XListView v) {
        if (isGetServerData) return;
        //loadType = loadTypeBottom;
        isFresh = false;
        //listView.setPullLoadEnable(false);
        page++;
        LogUtil.logE("onLoadMore page", page);
        getRelatedList();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.empty:
                onRefresh(listView);
                break;
            case R.id.linBrands:
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.DTPPRK);
                startActivity(new Intent(mActivity, BrandPlusActivity.class));
                break;
            case R.id.iv_unlock_task: //做任务入口
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JSQHBQXRW);
                Intent intent = new Intent(getActivity(), CommonWeb.class);
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.MISSION_ITEM, "");
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                intent.putExtra("url", url);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void parse(String methodName, String result) {
        LogUtil.logE("StatusesFragmentShoppingCircle", "parse回调" + methodName);
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mActivity, item.getErr().getMsg());
            }
            listView.onLoadMoreComplete();
            listView.onRefreshComplete();
            listView.setPullLoadEnable(true);
            switch(methodName) {
                case Method.getV2ShoppingCircieList:
                case Method.getV2MyShoppingCircleList:
                    isGetServerData = false;
                    if (lastStatusesId == 0 && adapter.getCount() == 0) {
                        mEmptyView.showRetry();
                        listView.setVisibility(View.GONE);
                    }
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.getV2ShoppingCircieList:
                isGetServerData = false;
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                cacheDB.saveShoppingCircleCacheJson(obj);
                updateDynamicList(obj);
                break;
            case Method.getV2MyShoppingCircleList:
                isGetServerData = false;
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                updateDynamicList(obj);
                break;
            case Method.redpacketGetPermission: //获取红包系统权限
                if (mRPTaskIn == null) break;
                JSONObject rpobj = JSON.parseObject(obj);
                if (rpobj.containsKey("authlevel")){
                    int authlevel = rpobj.getInteger("authlevel");
                    if (authlevel == 0){
                        mRPTaskIn.setVisibility(View.VISIBLE);
                    }else{
                        mRPTaskIn.setVisibility(View.GONE);
                    }
                }
                break;

        }
    }


    /**
     * 更新动态列表
     */
    public void updateDynamicList(String json) {
        if (json == null || json.equals("")) return;
        LogUtil.logE("adapter json", json);
        switch(type) {
            case typeBrowse:
                ShoppingCircleListItem item = JSONHelper.getObject(json, ShoppingCircleListItem.class);
                if (item == null) return;
                if (item.getIsexpire() == ShoppingCircleListItem.isexpireYes) {
                    //过期了。重新刷新新数据
                    listView.smoothScrollToPosition(0);
                    refresh();
                    return;
                }
                list = item.getDatalist();
                sortid = item.getSortid();
                if (list != null && list.size() > 0) {
                    lastStatusesId = list.get(list.size() - 1) != null ? list.get(list.size() - 1).getId() :0;
                    if (isFresh) {
                        adapter.setData(list);
                    } else {
                        adapter.addData(list);
                    }
                } else {
                    listView.EndLoad();
                }
                break;
            case typeManage:
                list = JSONHelper.getList(json, ShoppingCircleDetail.class);
                if (list != null && list.size() > 0) {
                    lastStatusesId = list.get(list.size() - 1) != null ? list.get(list.size() - 1).getId() :0;
                    if (isFresh) {
                        adapter.setData(list);
                    } else {
                        adapter.addData(list);
                    }
                } else {
                    listView.EndLoad();
                }
                break;
        }
        if (!adapter.isEmpty()) {
            mEmptyView.hide();
            listView.setVisibility(View.VISIBLE);
        } else if (adapter.getCount() == 0) {
            mEmptyView.showEmpty();
            listView.setVisibility(View.GONE);
        }
    }

    int position;
    ShoppingCircleDetail statuses;


    @Override
    public void enterDongtaiDetail(int position, ShoppingCircleDetail statuses, boolean scrollToPosition) {
        UmengClickEventHelper.onRedpackageClickEvent(mActivity);
        this.statuses = statuses;
        this.position = position;
        Intent intent = new Intent(mActivity, AdDongtaiDetailAct.class);
        intent.putExtra("statusesId", statuses.getSquareid());
        if (statuses.getRpinfo() != null) {
            intent.putExtra("redpacketstatus", statuses.getRpinfo().getStatus());
        }
        intent.putExtra(KEY.SCROLL_TO_POSITION, scrollToPosition);
        startActivityForResult(intent, requestDetailDongtai);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LogUtil.logE("onActivityResult", requestCode + ":" + resultCode);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            switch(resultCode) {
                case resultDeleteCode:
                    String squareId = data.getStringExtra("squareId");
                    adapter.deleteStatusesId(squareId);
                    break;
                case resultUpdateCode:
                    //Statuses statuses = (Statuses) data.getSerializableExtra("item");
                    //adapter.updateItem(statuses, position);
                    //去掉上面，加上下面
                    adapter.notifyDataSetChanged();
                    break;
                case requestDetailDongtai:
                    ShoppingCircleDetail statuses = (ShoppingCircleDetail) data.getSerializableExtra("item");
                    if (statuses == null) return;

                    int attentionState = statuses.getUserinfo().getIsattention();
                    for (ShoppingCircleDetail itemData : list) {
                        if (itemData.getUserid() == statuses.getUserid()) {
                            AdUserItem2 user2 = itemData.getUserinfo();
                            user2.setIsattention(attentionState);
                            itemData.setUserinfo(user2);
                        }

                        //用详情 重新替换列表 item
                        if (itemData.getSquareid().equals(statuses.getSquareid())) {
                            adapter.updateItem(statuses, position);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }

        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action) {
                case ACTION.DELETE_SHOPPING_CIRCLE: {
                    String statusesId = intent.getStringExtra(KEY.SQUARE_ID);
                    adapter.deleteStatusesId(statusesId);
                }
                break;
            }
        }
    };

    class GetNewStatusesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(ACTION.ADD_SHOPPING_CIRCLE)) {
                //ShoppingCircleDetail item = (ShoppingCircleDetail) intent.getSerializableExtra("item");
                //adapter.addItemAfterPulish(item);
            }

        }

    }

}
