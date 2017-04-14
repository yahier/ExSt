package yahier.exst.act.dongtai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.Message;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 * 动态视频集合
 */

public class VideoListAct extends ThemeActivity implements View.OnClickListener, XListView.OnXListViewListener, FinalHttpCallback, DongtaiMainAdapter.OnStatesesListener {

    long lastStatusesId;
    DongtaiMainAdapter adapter;
    XListView listView;
    List<Statuses> list;
    boolean isFresh = true;//默认应该是true
    final int requestDetailDongtai = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_video_list_act);
        setLabel("精彩视频");
        listView = (XListView) findViewById(R.id.list);
        listView.setOnXListViewListener(this);
        View emptyView = findViewById(R.id.empty);
        emptyView.setOnClickListener(this);
        listView.setEmptyView(emptyView);


        adapter = new DongtaiMainAdapter(this, DongtaiMainAdapter.typeStatuses);
        listView.setAdapter(adapter);
        adapter.setOnStatesesListener(this);

        getListData();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                adapter.checkIsPauseVideo(firstVisibleItem - 1, visibleItemCount + firstVisibleItem - 2);

            }
        });
    }

    Statuses statuses;
    Message message;
    int position;
    boolean isPauseVideo = true;


    public void onPause() {
        super.onPause();
        if (isPauseVideo) {
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
        adapter.stopVideo();
    }


    boolean isGetServerData = false;
    void getListData() {
        if(isGetServerData)return;
        isGetServerData = true;
        Params params = new Params();
        params.put("lastid", lastStatusesId);
        params.put("count", Statuses.requestCount);//Statuses.requestCount
        new HttpEntity(mActivity).commonPostData(Method.videoList, params, this);
    }

    @Override
    public void doForward(Statuses statuses, int position) {
        this.position = position;
        Intent intent = new Intent(mActivity, DongtaiRepost.class);
        intent.putExtra("data", statuses);
        startActivity(intent);
    }

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
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mActivity, item.getErr().getMsg());
            }
            switch(methodName) {
                case Method.videoList:
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    isGetServerData = false;
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.videoList:
                isGetServerData = false;
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                //cacheDB.saveDynamicCacheJson(obj);
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

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        //loadType = loadTypeTop;
        isFresh = true;
        lastStatusesId = 0;
        getListData();
    }

    @Override
    public void onLoadMore(XListView v) {
        //loadType = loadTypeBottom;
        isFresh = false;
//        listView.setPullLoadEnable(false);
        getListData();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.empty:
                onRefresh(listView);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LogUtil.logE("onActivityResult", requestCode + ":" + resultCode);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            adapter.resetVideo();
            // 如果进入转发页面再返回
        }  else if (requestCode == DongtaiMainAdapter.requestFullVideo) {
            // LogUtil.logE("activityForResult","testContinueVideo");
            adapter.testContinueVideo();

        }



    }

    //收藏成功后
    void collect(int count) {
        int headerCount = listView.getHeaderViewsCount();
        int visibilePosition = listView.getFirstVisiblePosition();
        //LogUtil.logE("cPosition:"+position + ":" + visibilePosition);
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
}
