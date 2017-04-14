package yahier.exst.act.mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.DongtaiRepost;
import com.stbl.stbl.act.dongtai.EventStatusesType;
import com.stbl.stbl.act.dongtai.StatusesAnimUtil;
import com.stbl.stbl.act.dongtai.VideoPlayAct;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.Message;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;


import java.util.List;


/**
 * Created by tnitf on 2016/4/20.
 */
public class MyDongtaiFragment extends Fragment implements XListView.OnXListViewListener, FinalHttpCallback, DongtaiMainAdapter.OnStatesesListener {

    private Activity mActivity;


    private EmptyView mEmptyView;
    XListView listView;
    DongtaiMainAdapter adapter;
    long lastStatusesId = 0;// 最后一条动态的id

    int loadType = 0;// 加载模式
    final int loadTypeBottom = 0;// 底部加载
    final int loadTypeTop = 1;// 顶部加载。清除以前数据
    final int requestCount = 15;
    final int requestDetailDongtai = 100;
    final int resultDeleteCode = 104;
    final int requestForward = 101;// 转发/不再记录转发数
    final int resultUpdateCode = 102;// 更新数据
    UserItem userItem;

    private int mPage = 1;
    private boolean mIsDestroy;

    private LoadingDialog mLoadingDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        userItem = (UserItem) getArguments().getSerializable("userItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mine_dongtai, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        //getCollectGoodsList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadType = loadTypeTop;
                lastStatusesId = 0;
                getRelatedList();
            }
        });

        adapter = new DongtaiMainAdapter(mActivity, DongtaiMainAdapter.typeStatuses);
        adapter.setOnStatesesListener(this);
        listView = (XListView) getView().findViewById(R.id.list);
        listView.setOnXListViewListener(this);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                adapter.checkIsPauseVideo(firstVisibleItem - 1, visibleItemCount + firstVisibleItem - 2);
            }
        });
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.DONGTAI_DELETED);
        getRelatedList();
    }


    boolean isGetServerData = false;

    void getRelatedList() {
        if (isGetServerData) return;
        isGetServerData = true;
        Params params = new Params();
        if (userItem != null) {
            params.put("objuserid", userItem.getUserid());
        }
        LogUtil.logE("lastStatusesId:" + lastStatusesId);
        params.put("lastid", lastStatusesId);
        params.put("count", requestCount);
        params.put("selecttype", Statuses.type_personal);
        new HttpEntity(mActivity, false).commonPostData(Method.weiboGetRelatedList, params, this);
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

    @Override
    public void onPause() {
        super.onPause();
        if (isPauseVideo) {
            adapter.pauseInVisibleVideo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
        adapter.stopVideo();
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
                    if (lastStatusesId == 0 && adapter.getCount() == 0) {
                        mEmptyView.showRetry();
                        listView.setVisibility(View.GONE);
                    }
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
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
                // listView.setEmptyView(emptyView);
                List<Statuses> list = JSONHelper.getList(obj, Statuses.class);
                if (list != null && list.size() > 0) {
                    mEmptyView.hide();
                    listView.setVisibility(View.VISIBLE);
                    lastStatusesId = list.get(list.size() - 1).getStatusesid();
                    if (loadType == loadTypeBottom) {
                        adapter.addData(list);
                        if (list.size() < requestCount) {
                            listView.EndLoad();
                        }
                    } else {
                        adapter.setData(list);
                    }
                } else {
                    if (adapter.getCount() == 0) {
                        mEmptyView.showEmpty();
                        listView.setVisibility(View.GONE);
                    }
                }
                break;
            case Method.weiboSetTop:
                ToastUtil.showToast(R.string.me_set_top_success);
                break;
            case Method.weiboUnSetTop:
                ToastUtil.showToast(R.string.me_cancel_set_top_success);
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
        }


    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        loadType = loadTypeTop;
        lastStatusesId = 0;
        getRelatedList();
    }

    boolean isPauseVideo = true;

    @Override
    public void onLoadMore(XListView v) {
        loadType = loadTypeBottom;
        getRelatedList();

    }

    int position;

    @Override
    public void doCollect(Statuses statuses, int position, int isCollected) {
        this.position = position;
        this.statuses = statuses;
        Params params = new Params();
        params.put("userid", SharedToken.getUserId());
        params.put("statusesid", statuses.getStatusesid());
        if (isCollected == Statuses.isfavoritedYes) {
            new HttpEntity(mActivity).commonPostData(Method.weiboCancelCollect, params, this);
        } else {
            new HttpEntity(mActivity).commonPostData(Method.weiboCollect, params, this);
        }

    }


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
    public void doPraise(Statuses statuses, int position) {
        this.position = position;
        this.statuses = statuses;
        Params params = new Params();
        params.put("userid", SharedToken.getUserId());
        params.put("statusesid", statuses.getStatusesid());
        new HttpEntity(mActivity).commonPostData(Method.weiboPraise, params, this);
    }


    @Override
    public void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition) {
        this.statuses = statuses;
        this.position = position;
        LogUtil.logE("enterDongtaiDetail " + this.position);
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
        LogUtil.logE("onActivityResult", requestCode + ":" + resultCode);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            adapter.resetVideo();
            switch (resultCode) {
                case resultDeleteCode:
                    long statusesId = data.getLongExtra("statusesId", 0);
                    adapter.deleteStatusesId(statusesId);
                    break;
                case resultUpdateCode:
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
            adapter.testContinueVideo();

        }

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
        TextView tvPraise = (TextView) viewItem.findViewById(R.id.item_text3);
        final ImageView img = (ImageView) viewItem.findViewById(R.id.imgItem3);
        if (tvPraise == null || img == null) return;
        StatusesAnimUtil.showAnimPraiseAnim(mActivity, tvPraise, img, praiseItem, statuses);
        adapter.update(position, statuses);
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
