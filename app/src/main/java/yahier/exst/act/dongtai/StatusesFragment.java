package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Message;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.widget.XListView;

import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by lenovo on 2016/6/7.
 */
public class StatusesFragment extends Fragment implements View.OnClickListener, XListView.OnXListViewListener, FinalHttpCallback, DongtaiMainAdapter.OnStatesesListener {
    private DongtaiAct2 mActivity;
    private View view;
    XListView listView;
    long lastStatusesId;
    List<Statuses> list;
    DongtaiMainAdapter adapter;
    boolean isFresh = true;//默认应该是true
    final int requestDetailDongtai = 100;
    final int requestForward = 101;// 转发/不再记录转发数
    final int resultUpdateCode = 102;// 更新数据
    final int resultDeleteCode = 104;
    public final static String actionGetOneNewStatuses = "getOneNewStatuses";
    GetNewStatusesReceiver receiver;

    boolean isPauseVideo = true;
    boolean isActied = true;
    private DataCacheDB cacheDB;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (DongtaiAct2) activity;
        home = (TabHome) mActivity.getParent();
        receiver = new GetNewStatusesReceiver();
        mActivity.registerReceiver(receiver, new IntentFilter(actionGetOneNewStatuses));
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.DONGTAI_DELETED);
        cacheDB = new DataCacheDB(activity);
    }


    public void onPause() {
        super.onPause();
        if (isPauseVideo && isActied) {
            adapter.pauseInVisibleVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果不是从全屏返回的。就还原adapter的视频
        if (isPauseVideo) {
            adapter.resetVideo();
        }
        //还原
        isPauseVideo = true;
    }

    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
        LogUtil.logE("StatusesFragment", "onDestroy");
        adapter.stopVideo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_dongtai_frag, container, false);
        return view;
    }

    TabHome home;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (XListView) view.findViewById(R.id.main_dontai_list);
        listView.setOnXListViewListener(this);
        View emptyView = view.findViewById(R.id.empty);
        emptyView.setOnClickListener(this);
        listView.setEmptyView(emptyView);
        adapter = new DongtaiMainAdapter(mActivity, DongtaiMainAdapter.typeStatuses);
        listView.setAdapter(adapter);
        adapter.setOnStatesesListener(this);
        //滑动时，不显示动态悬浮框。
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState) {
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
                adapter.checkIsPauseVideo(firstVisibleItem - 1, visibleItemCount + firstVisibleItem - 2);

            }
        });
        EventBus.getDefault().register(this);
        LogUtil.logE("StatusesFragment", "updateDynamicList 1 " + cacheDB.getDynamicCacheJson());
        //getRelatedList();
        updateDynamicList(cacheDB.getDynamicCacheJson());
        onRefresh(listView);
    }

    public void onEvent(EventTypeCommon type) {
        switch (type.getType()) {
            case EventTypeCommon.typeUpdateStatusesAttend:
                if (adapter.getCount() > 0) {
                    listView.setAutoFresh();//add new
                    break;
                }
                listView.smoothScrollToPosition(0);
                isFresh = true;
                lastStatusesId = 0;
                getRelatedList();
                break;
            case EventTypeCommon.typeToTopStatusesAttend:
                listView.smoothScrollToPosition(0);
                break;
            case EventTypeCommon.typeGetStatusesAttend:
                if (adapter.getCount() > 0)
                    return;
                listView.smoothScrollToPosition(0);
                isFresh = true;
                lastStatusesId = 0;
                getRelatedList();
                break;
        }


    }


    boolean isGetServerData = false;

    void getRelatedList() {
        if (isGetServerData) return;
        isGetServerData = true;
        Params params = new Params();
        params.put("lastid", lastStatusesId);
        params.put("count", Statuses.requestCount);//Statuses.requestCount
        params.put("selecttype", Statuses.type_public);
        new HttpEntity(mActivity).commonPostData(Method.weiboGetRelatedList, params, this);
    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        //loadType = loadTypeTop;
        isFresh = true;
        lastStatusesId = 0;
        getRelatedList();
    }

    @Override
    public void onLoadMore(XListView v) {
        //loadType = loadTypeBottom;
        isFresh = false;
        // listView.setPullLoadEnable(false);
        getRelatedList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty:
                onRefresh(listView);
                break;
        }
    }

    class GetNewStatusesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(actionGetOneNewStatuses)) {
                statuses = (Statuses) intent.getSerializableExtra("statuses");
                adapter.addItemAfterPulish(statuses);
            }

        }

    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mActivity, item.getErr().getMsg());
            }
            switch (methodName) {
                case Method.weiboGetRelatedList:
                    isGetServerData = false;
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    listView.setPullLoadEnable(true);
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.weiboGetRelatedList:
                isGetServerData = false;
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                cacheDB.saveDynamicCacheJson(obj);
                LogUtil.logE("StatusesFragment", "updateDynamicList 2 " + cacheDB.getDynamicCacheJson());
                updateDynamicList(obj);
                break;
            case Method.weiboPraise:
                PraiseResult praiseItem = JSONHelper.getObject(obj, PraiseResult.class);
                showPraiseAnim(praiseItem);
                break;
            case Method.weiboCollect:
                Collect mCollect = JSONHelper.getObject(obj, Collect.class);
                collect(mCollect.getCollectcount());
                break;
            case Method.weiboCancelCollect:
                Collect mCollect2 = JSONHelper.getObject(obj, Collect.class);
                cancelCollect(mCollect2.getCollectcount());
                break;
//            case Method.weiboGetOneDetail:
//                statuses = JSONHelper.getObject(obj, Statuses.class);
//                adapter.addItemAfterPulish(statuses);
//                break;
            case Method.statusesSquareCount:
                break;

        }
    }


    //收藏成功后
    void collect(int count) {
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        int oldPosition = position;
        position = oldPosition - visibilePosition + headerCount;
        LogUtil.logE("cPosition:" + oldPosition + ":" + position + ":" + visibilePosition);
        View viewItem = listView.getChildAt(position);
        if (viewItem == null) return;
        TextView tvFovor = (TextView) viewItem.findViewById(R.id.item_text4);
        ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem4);
        StatusesAnimUtil.showAnimCollect(mActivity, tvFovor, img, count);
        //更新
        statuses.setFavorcount(count);
        statuses.setIsfavorited(Statuses.isfavoritedYes);
        adapter.update(position, statuses);

    }


    //取消收藏
    void cancelCollect(int count) {
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        //LogUtil.logE("cPosition:"+position + ":" + visibilePosition);
        position = position - visibilePosition + headerCount;
        View viewItem = listView.getChildAt(position);
        if (viewItem == null) return;
        TextView tvFovor = (TextView) viewItem.findViewById(R.id.item_text4);
        final ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem4);
        StatusesAnimUtil.showAnimCancelCollect(mActivity, tvFovor, img, count);
        //更新
        statuses.setFavorcount(count);
        statuses.setIsfavorited(Statuses.isfavoritedNo);
        adapter.update(position, statuses);
    }


    //点赞与取消点赞
    void showPraiseAnim(PraiseResult praiseItem) {
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        position = position - visibilePosition + headerCount;
        //更新
        statuses.setPraisecount(praiseItem.getCount());
        View viewItem = listView.getChildAt(position);
        if (viewItem == null) return;
        TextView tvPraise = (TextView) viewItem.findViewById(R.id.item_text3);
        final ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem3);
        StatusesAnimUtil.showAnimPraiseAnim(mActivity, tvPraise, img, praiseItem, statuses);
        //adapter.update(position, statuses);
    }


    /**
     * 更新动态列表
     */
    public void updateDynamicList(String json) {
        if (json == null || json.equals("")) return;
        list = JSONHelper.getList(json, Statuses.class);
        if (list != null) {
            LogUtil.logE("size ", list.size());
        }
        if (list != null && list.size() > 0) {
            lastStatusesId = list.get(list.size() - 1).getStatusesid();
            if (isFresh) {
                adapter.setData(list);
            } else {
                adapter.addData(list);
            }
        } else {
            listView.EndLoad();
        }
    }

    int position;

    // 转发
    @Override
    public void doForward(Statuses statuses, int position) {
        this.position = position;
        Intent intent = new Intent(mActivity, DongtaiRepost.class);
        intent.putExtra("data", statuses);
        startActivityForResult(intent, requestForward);
    }

    Statuses statuses;
    Message message;

    @Override
    public void doRemark(int position, Statuses statuses, String content) {
        message = new Message();
        message.setContent(content);
        message.setName(SharedUser.getUserNick());
        // 以上构造评论item
        this.position = position;
        this.statuses = statuses;

        Params params = new Params();
        params.put("statusesid", String.valueOf(statuses.getStatusesid()));
        params.put("content", content);
        new HttpEntity(mActivity).commonPostData(Method.weiboRemark, params, this);

    }

    @Override
    public void doPraise(Statuses mStatuses, int position) {
        this.position = position;
        this.statuses = mStatuses;
        Params params = new Params();
        params.put("statusesid", mStatuses.getStatusesid());
        new HttpEntity(mActivity).commonPostData(Method.weiboPraise, params, this);
    }


    @Override
    public void doCollect(Statuses mStatuses, int position, int isCollected) {
        this.statuses = mStatuses;
        this.position = position;
        Params params = new Params();
        //params.put("userid", app.getUserId());
        params.put("statusesid", mStatuses.getStatusesid());
        if (isCollected == Statuses.isfavoritedYes) {
            new HttpEntity(mActivity).commonPostData(Method.weiboCancelCollect, params, this);
        } else {
            new HttpEntity(mActivity).commonPostData(Method.weiboCollect, params, this);
        }

    }


    @Override
    public void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition) {
        this.statuses = statuses;
        this.position = position;
        //LogUtil.logE("enterDongtaiDetail " + this.position);
        Intent intent = new Intent(mActivity, DongtaiDetailActivity.class);
        intent.putExtra("statusesId", statuses.getStatusesid());
        intent.putExtra(KEY.SCROLL_TO_POSITION, scrollToPosition);
        startActivityForResult(intent, requestDetailDongtai);

    }

    @Override
    public void doFullClick(int position) {
        Intent intent = new Intent(mActivity, VideoPlayAct.class);
        isPauseVideo = false;
        startActivityForResult(intent, DongtaiMainAdapter.requestFullVideo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LogUtil.logE("onActivityResult", requestCode + ":" + resultCode);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            adapter.resetVideo();
            switch (resultCode) {
                case resultDeleteCode:
                    long statusesId = data.getLongExtra("statusesId", 0);
                    adapter.deleteStatusesId(statusesId);
                    break;
                case resultUpdateCode:
                    //Statuses statuses = (Statuses) data.getSerializableExtra("item");
                    //adapter.updateItem(statuses, position);
                    //去掉上面，加上下面
                    adapter.notifyDataSetChanged();
                    break;
            }
            // 如果进入转发页面再返回
        } else if (requestCode == requestForward) {
            if (resultCode == requestForward) {
                position++;// 之所以要++,是因为转发后，会加进来一条数据
                //adapter.updateForwardText(listView, position);
            }
        } else if (requestCode == DongtaiMainAdapter.requestFullVideo) {
            // LogUtil.logE("activityForResult","testContinueVideo");
            adapter.testContinueVideo();

        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.DONGTAI_DELETED: {
                    long statusesId = intent.getLongExtra(KEY.STATUSES_ID, 0L);
                    adapter.deleteStatusesId(statusesId);
                }
                break;
            }
        }
    };

}
