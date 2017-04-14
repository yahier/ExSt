package yahier.exst.act.dongtai;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.DongtaiEventType;
import com.stbl.stbl.item.DongtaiNewMsgItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.EventTypeDynamicShare;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.task.dongtai.StatusTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.MyOnPageChangeListener;
import com.stbl.stbl.util.ShareUtils;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.SlidingTabLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;

import io.rong.eventbus.EventBus;

/**
 * 动态入口类，三个fragment都从这里开始。
 */
public class DongtaiAct2 extends BaseActivity implements View.OnClickListener, ReceiverUtils.MessageReceiver {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private CommonFragmentPagerAdapter mAdapter;

    private View imgNotify, imgSearch;
    private View tvAttend, tvSquare, tvShoppingCircle;
    private View lineTab;
    //int lineDistance;

    int lineDistance1, lineDistance2;
    View linBig;
    boolean isAnimated = false;
    private ImageView ivNewMessagePoint;
    private int tabIndex;

    @Override
    public void onMessage(int receiverType, Bundle bundle) {
        //LogUtil.logE("JPushReceiver", "DongtaiAct2 -  receiverType : " + receiverType);
        switch (receiverType) {
            case ReceiverUtils.PUSH_TYPE_DONGTAI_NEW_MESSAGE: //动态新消息
                int dongtai_pushModelType = bundle.getInt("modelType", -1);
                DongtaiNewMsgItem item = (DongtaiNewMsgItem) bundle.getSerializable("pushinfo");
//                switch (dongtai_pushModelType) {
//                    case DongtaiPushServer.PUTSH_DONGTAI_NEW_MESSAGE:
                ivNewMessagePoint.setVisibility(View.VISIBLE);
//                        break;
//                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dongtai2);
        initView();
    }
    @Override
    protected void setStatusBar() {
    }

    StatusesFragment fragment1;
    StatusesFragmentSquare fragment2;
    StatusesFragmentShoppingCircle fragment3;

    private void initView() {
        linBig = findViewById(R.id.linBig);
        linBig.setOnClickListener(this);
        lineTab = findViewById(R.id.lineTab);
        imgSearch = findViewById(R.id.imgSearch);
        imgNotify = findViewById(R.id.imgNotify);
        imgSearch.setOnClickListener(this);
        imgNotify.setOnClickListener(this);
        tvAttend = findViewById(R.id.tvAttend);
        tvSquare = findViewById(R.id.tvSquare);
        tvShoppingCircle = findViewById(R.id.tvShoppingCircle);
        tvAttend.setOnClickListener(this);
        tvSquare.setOnClickListener(this);
        tvShoppingCircle.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        ivNewMessagePoint = (ImageView) findViewById(R.id.iv_newmessage_point);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragment1 = new StatusesFragment();
        fragmentList.add(fragment1);

        fragment2 = new StatusesFragmentSquare();
        fragmentList.add(fragment2);

        fragment3 = new StatusesFragmentShoppingCircle();
        fragmentList.add(fragment3);

        mAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, null);
        mViewPager.setAdapter(mAdapter);


        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        fragment1.isActied = true;
                        fragment2.isActied = false;
                        MobclickAgent.onEvent(DongtaiAct2.this, UmengClickEventHelper.DTGZTAB);
                        switch (tabIndex) {
                            case 1:
                                ObjectAnimator.ofFloat(lineTab, "translationX", lineDistance1, 0).start();
                                break;
                            case 2:
                                ObjectAnimator.ofFloat(lineTab, "translationX", lineDistance2, 0).start();
                                break;
                        }
                        break;
                    case 1:
                        fragment1.isActied = false;
                        fragment2.isActied = true;
                        MobclickAgent.onEvent(DongtaiAct2.this, UmengClickEventHelper.DTGCTAB);
                        switch (tabIndex) {
                            case 0:
                                ObjectAnimator.ofFloat(lineTab, "translationX", 0, lineDistance1).start();
                                break;
                            case 2:
                                ObjectAnimator.ofFloat(lineTab, "translationX", lineDistance2, lineDistance1).start();
                                break;
                        }
                        break;
                    case 2:
                        fragment1.isActied = false;
                        fragment2.isActied = true;
                        MobclickAgent.onEvent(DongtaiAct2.this, UmengClickEventHelper.DTSQTAB);
                        switch (tabIndex) {
                            case 0:
                                ObjectAnimator.ofFloat(lineTab, "translationX", 0, lineDistance2).start();
                                break;
                            case 1:
                                ObjectAnimator.ofFloat(lineTab, "translationX", lineDistance1, lineDistance2).start();
                                break;
                        }
                        break;
                }
                tabIndex = i;
                try {
                    fragment1.adapter.pauseInVisibleVideo();
                    fragment2.adapter.pauseInVisibleVideo();
                    //不加上的话，在关注播放。再到广场播放，再回来关注，关注这里就没有显示视频这个item
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            fragment1.adapter.resetVideo();
                            fragment2.adapter.resetVideo();
                        }
                    });

                } catch (Exception e) {
                    LogUtil.logE("e:" + e.getStackTrace().toString());
                }
            }
        });

        ReceiverUtils.addReceiver(this);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(StatusesFragment.actionGetOneNewStatuses);
        filter.addAction(ACTION.ADD_SHOPPING_CIRCLE);
        registerReceiver(mNewStatusesReceiver, filter);
        if (SharedCommon.getStatusesNotify()) {
            ivNewMessagePoint.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        super.onResume();
        LogUtil.logE("DongtaiAct2", "tabIndex:" + tabIndex);
        //计算需要滑动的距离
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] locations = new int[2];
                tvAttend.getLocationOnScreen(locations);
                int[] locations2 = new int[2];
                tvSquare.getLocationOnScreen(locations2);
                int[] locations3 = new int[2];
                tvShoppingCircle.getLocationOnScreen(locations3);

                lineDistance1 = locations2[0] - locations[0];
                lineDistance2 = locations3[0] - locations[0];

                int[] locationsLine = new int[2];
                lineTab.getLocationOnScreen(locationsLine);

                LogUtil.logE("DongtaiAct2", locations[0] + ":" + locations2[0] + ":" + locations3[0] + ":" + locationsLine[0]);
                //设置崩溃后,黑线正确显示.如果post不延迟,有时没有滑动。
                switch (tabIndex) {
                    case 0:
                        if (locations[0] != locationsLine[0]) {
                            lineTab.setTranslationX(locations[0] - locationsLine[0]);
                            LogUtil.logE("DongtaiAct2", "if 1");
                        } else {
                            LogUtil.logE("DongtaiAct2", "else 1");
                        }
                        break;
                    case 1:
                        if (locations2[0] != locationsLine[0]) {
                            lineTab.setTranslationX(locations2[0] - locationsLine[0]);
                            LogUtil.logE("DongtaiAct2", "if 2");
                        } else {
                            LogUtil.logE("DongtaiAct2", "else 2");
                        }
                        break;
                    case 2:
                        if (locations3[0] != locationsLine[0]) {
                            lineTab.setTranslationX(locations3[0] - locationsLine[0]);
                            LogUtil.logE("DongtaiAct2", "if 3");
                        } else {
                            LogUtil.logE("DongtaiAct2", "else 3");
                        }
                        break;
                }

            }
        },100);


        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeGetStatusesAttend));
        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeGetStatusesSquare));
        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeGetStatusesShoppingCircle));
    }


    public void onEvent(EventTypeCommon type) {
        switch (type.getType()) {
            case EventTypeCommon.typeRefreshStatuses:
                switch (tabIndex) {
                    case 0:
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeUpdateStatusesAttend));
                        break;
                    case 1:
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeUpdateStatusesSquare));
                        break;
                    case 2:
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeUpdateStatusesShoppingCircle));
                        break;
                }
                break;
            case EventTypeCommon.typeToStatusesTop:
                switch (tabIndex) {
                    case 0:
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeToTopStatusesAttend));
                        break;
                    case 1:
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeToTopStatusesSquare));
                        break;
                    case 2:
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeToTopStatusesShoppingCircle));
                        break;
                }
                break;
            case EventTypeCommon.typeToDongtaiIndex:
                int index = (int) type.getUserId();
                if (index < mViewPager.getChildCount())
                    mViewPager.setCurrentItem(index);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgSearch:
                MobclickAgent.onEvent(this, UmengClickEventHelper.DTSS);
                enterAct(DongtaiSearch.class);//
                break;
            case R.id.imgNotify:
                MobclickAgent.onEvent(this, UmengClickEventHelper.DTXX);
                Intent intent = new Intent(this, DongtaiNotify.class);//DongtaiNotify
                intent.putExtra("isFresh", true);
                startActivity(intent);
                break;
            case R.id.tvAttend:
                MobclickAgent.onEvent(this, UmengClickEventHelper.DTGZTAB);
                mViewPager.setCurrentItem(0, true);
                // ObjectAnimator.ofFloat(lineTab, "translationX", lineDistance, 0).start();
                break;
            case R.id.tvSquare:
                MobclickAgent.onEvent(this, UmengClickEventHelper.DTGCTAB);
                mViewPager.setCurrentItem(1, true);
                // ObjectAnimator.ofFloat(lineTab, "translationX", 0, lineDistance).start();
                break;
            case R.id.tvShoppingCircle:
                MobclickAgent.onEvent(this, UmengClickEventHelper.DTSQTAB);
                mViewPager.setCurrentItem(2, true);
                break;

        }
    }

    //动态新消息查看完消除红点
    public void onEvent(DongtaiEventType event) {
        if (event.getType() == DongtaiEventType.NEW_MESSAGE) {
            ivNewMessagePoint.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        TabHome home = (TabHome) getParent();
        return home.onKeyDown(keyCode, event);
    }

    private void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiverUtils.removeReceiver(this);
        unregisterReceiver(mNewStatusesReceiver);
        EventBus.getDefault().unregister(this);
    }

    private BroadcastReceiver mNewStatusesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case StatusesFragment.actionGetOneNewStatuses:
                    onNewStatuses(intent);
                    break;
                case ACTION.ADD_SHOPPING_CIRCLE:
                    onNewShoppingCircle(intent);
                    break;
            }
        }
    };

    private void onNewStatuses(Intent intent) {
        final Statuses statuses = (Statuses) intent.getSerializableExtra("statuses");
        final boolean isShareToCircle = intent.getBooleanExtra(KEY.IS_SHARE_TO_CIRCLE, false);
        final boolean isShareToQzone = intent.getBooleanExtra(KEY.IS_SHARE_TO_QZONE, false);
        final boolean isShareTask = intent.getBooleanExtra("type_task", false);
        if (statuses == null) {
            return;
        }
        mTaskManager.start(StatusTask.getShareUrl(String.valueOf(ShareDialog.sharedMiStatuses), String.valueOf(statuses.getStatusesid()))
                .setCallback(new HttpTaskCallback<String>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(String result) {
                            String url = result;
                            ShareItem shareItem = ShareUtils.createDongtaiShare(statuses);
                        if (isShareTask) { //做解锁任务
                            EventBus.getDefault().post(new EventTypeDynamicShare(url,isShareToCircle,isShareToQzone,isShareTask,shareItem));
                        }else{
                            CommonShare.shareShortStatusAfterPublish(DongtaiAct2.this, shareItem, url, isShareToCircle, isShareToQzone, isShareTask);
                        }
                    }
                }));
    }

    private void onNewShoppingCircle(Intent intent) {
        final ShoppingCircleDetail detail = (ShoppingCircleDetail) intent.getSerializableExtra("item");
        final boolean isShareToCircle = intent.getBooleanExtra(KEY.IS_SHARE_TO_CIRCLE, false);
        final boolean isShareToQzone = intent.getBooleanExtra(KEY.IS_SHARE_TO_QZONE, false);
        if (detail == null) {
            return;
        }
        mTaskManager.start(StatusTask.getShareUrl(String.valueOf(ShareDialog.sharedShoppingCircle), String.valueOf(detail.getSquareid()))
                .setCallback(new HttpTaskCallback<String>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(String result) {
                        String url = result;
                        ShareItem shareItem = ShareUtils.createShoppingCircle(detail);
                        CommonShare.shareShortStatusAfterPublish(DongtaiAct2.this, shareItem, url, isShareToCircle, isShareToQzone,false);
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
