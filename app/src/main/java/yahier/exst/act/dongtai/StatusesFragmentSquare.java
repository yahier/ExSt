package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.XListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by lenovo on 2016/6/7.
 */
public class StatusesFragmentSquare extends Fragment implements View.OnClickListener, XListView.OnXListViewListener, FinalHttpCallback, DongtaiMainAdapter.OnStatesesListener {
    private Activity mActivity;
    private View view;
    XListView listView;

    List<Statuses> list;
    DongtaiMainAdapter adapter;
    boolean isFresh = true;

    long lastStatusesId;
    int page = 1;
    final int requestDetailDongtai = 100;
    final int requestForward = 101;// 转发
    final int resultUpdateCode = 102;// 更新数据
    final int resultDeleteCode = 104;
    TabHome home;
    boolean isActied = false;
    private DataCacheDB cacheDB;
    View emptyView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        home = (TabHome) mActivity.getParent();
        cacheDB = new DataCacheDB(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_statuses_square, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (XListView) view.findViewById(R.id.main_dontai_list);
        listView.setOnXListViewListener(this);
        emptyView = view.findViewById(R.id.empty);

        View headerVideo = LayoutInflater.from(mActivity).inflate(R.layout.dongtai_square_vidoe_layout, null);
        headerVideo.setOnClickListener(this);
        listView.addHeaderView(headerVideo);
        //listView.setEmptyView(emptyView);
        emptyView.setOnClickListener(this);
        adapter = new DongtaiMainAdapter(mActivity, DongtaiMainAdapter.typeStatusesSquare);
        adapter.setOnStatesesListener(this);
        listView.setAdapter(adapter);
        //getMainList();

        //滑动时，不显示动态悬浮框。
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

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
                adapter.checkIsPauseVideo(firstVisibleItem - 1, visibleItemCount + firstVisibleItem - 2);
            }
        });
        EventBus.getDefault().register(this);
        updateDynamicList(cacheDB.getDynamicSquareCacheJson());
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh(listView);
            }
        },800);

    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        adapter.stopVideo();
    }

    public void onEvent(EventTypeCommon type) {
        switch(type.getType()) {
            case EventTypeCommon.typeUpdateStatusesSquare:
                if (adapter.getCount() > 0) {
                    listView.setAutoFresh();//add new
                    return;
                }
                listView.smoothScrollToPosition(0);
                isFresh = true;
                lastStatusesId = 0;
                getMainList();
                break;
            case EventTypeCommon.typeToTopStatusesSquare:
                listView.smoothScrollToPosition(0);
                break;
            case EventTypeCommon.typeGetStatusesSquare:
                if (adapter.getCount() > 0)
                    return;
                listView.smoothScrollToPosition(0);
                isFresh = true;
                lastStatusesId = 0;
                getMainList();
                break;
        }

    }


    boolean isGetServerData = false;

    /**
     * 获取主列表数据
     */
    void getMainList() {
        if (isGetServerData) return;
        isGetServerData = true;
        Params params = new Params();
        // params.put("userid", app.getUserId());
        params.put("page", page);
        params.put("lastid", lastStatusesId);
        params.put("count", Statuses.requestCount);
        // params.put("samecity", samecity);
        // params.put("gender", gender);
        new HttpEntity(mActivity).commonPostData(Method.weiboSquare, params, this);
    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        //loadType = loadTypeTop;
        isFresh = true;
        lastStatusesId = 0;
        page = 1;
        getMainList();
    }

    @Override
    public void onLoadMore(XListView v) {
        //loadType = loadTypeBottom;
        isFresh = false;
        //listView.setPullLoadEnable(false);
        page++;
        getMainList();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.empty:
                onRefresh(listView);
                break;
            case R.id.linVideo:
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.DTJCSPRK);
                startActivity(new Intent(mActivity, VideoListAct.class));
                break;
        }
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mActivity, item.getErr().getMsg());
            }
            switch(methodName) {
                case Method.weiboSquare:
                    isGetServerData = false;
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    listView.setPullLoadEnable(true);
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.weiboSquare:
                isGetServerData = false;
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                cacheDB.saveDynamicSquareCacheJson(obj);
                updateDynamicList(obj);
                break;
            case Method.weiboPraise:
                PraiseResult praiseItem = JSONHelper.getObject(obj, PraiseResult.class);
                //adapter.updatePraiseText(listView, position, praiseItem.getCount(), praiseItem.getType());
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
//                adapter.addItem(statuses);
//                break;
            case Method.statusesSquareCount:
                break;
        }
    }

    /**
     * 更新动态列表
     */
    public void updateDynamicList(String json) {
        if (json == null || json.equals("")) return;
        list = JSONHelper.getList(json, Statuses.class);

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
        if (!adapter.isEmpty()) {
            emptyView.setVisibility(View.GONE);
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
        params.put("statusesid", mStatuses.getStatusesid());
        if (isCollected == Statuses.isfavoritedYes) {
            new HttpEntity(mActivity).commonPostData(Method.weiboCancelCollect, params, this);
        } else {
            new HttpEntity(mActivity).commonPostData(Method.weiboCollect, params, this);
        }

    }


    @Override
    public void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition) {
        this.position = position;
        LogUtil.logE("enterDongtaiDetail " + this.position);
        Intent intent = new Intent(mActivity, DongtaiDetailActivity.class);
        intent.putExtra("statusesId", statuses.getStatusesid());
        intent.putExtra(KEY.SCROLL_TO_POSITION, scrollToPosition);
        startActivityForResult(intent, requestDetailDongtai);

    }

    boolean isPauseVideo = true;

    @Override
    public void doFullClick(int position) {
        Intent intent = new Intent(mActivity, VideoPlayAct.class);
        isPauseVideo = false;
        startActivityForResult(intent, DongtaiMainAdapter.requestFullVideo);
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

    //收藏成功后
    void collect(int count) {
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        position = position - visibilePosition + headerCount;
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
        TextView tvPraise = (TextView) viewItem.findViewById(R.id.item_text3);//nullPointer
        final ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem3);
        StatusesAnimUtil.showAnimPraiseAnim(mActivity, tvPraise, img, praiseItem, statuses);
        adapter.update(position, statuses);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            adapter.resetVideo();
            switch(resultCode) {
                case resultDeleteCode:
                    long statusesId = data.getLongExtra("statusesId", 0);
                    adapter.deleteStatusesId(statusesId);
                    break;
                case resultUpdateCode:
                    Statuses statuses = (Statuses) data.getSerializableExtra("item");
                    LogUtil.logE("onActivityResult " + this.position);
                    adapter.updateItem(statuses, position);
                    break;
            }
            // 如果进入转发页面再返回
        } else if (requestCode == requestForward) {
            if (resultCode == requestForward) {
                //position++;// 之所以要++,是因为转发后，会加进来一条数据
                // adapter.updateForwardText(listView, position);
            }
        } else if (requestCode == DongtaiMainAdapter.requestFullVideo) {
            adapter.testContinueVideo();

        }

    }

}
