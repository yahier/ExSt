package yahier.exst.common;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.AdManagerActivity;
import com.stbl.stbl.act.ad.IntroduceAdAct;
import com.stbl.stbl.act.ad.PublishShoppingActivity;
import com.stbl.stbl.act.dongtai.DongtaiAct2;
import com.stbl.stbl.act.dongtai.DongtaiNotify;
import com.stbl.stbl.act.dongtai.DongtaiPulishLongAct;
import com.stbl.stbl.act.dongtai.PublishShortStatusActivity;
import com.stbl.stbl.act.home.HomeMainAct;
import com.stbl.stbl.act.im.MessageMainAct;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.im.SharedGroups;
import com.stbl.stbl.act.im.rong.RongCloudEvent;
import com.stbl.stbl.act.im.rong.TopSettingDB;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.act.login.LoginActivity;
import com.stbl.stbl.act.mine.MineMainAct;
import com.stbl.stbl.act.mine.MyQrcodeActivity;
import com.stbl.stbl.api.data.directScreen.GuestInviteInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.DirectScreenPushServer;
import com.stbl.stbl.api.utils.preferences.LoadStore;
import com.stbl.stbl.dialog.ShoppingCircleTipsDialog;
import com.stbl.stbl.dialog.UpdateLogDialog;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.DongtaiEventType;
import com.stbl.stbl.item.DongtaiNewMsgItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.IMSetting;
import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.item.im.GroupBoth;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.UpdateInfo;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.UpdateTask;
import com.stbl.stbl.task.YunzhanghuTask;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.ui.DirectScreen.dialog.InviteTipsDialog;
import com.stbl.stbl.ui.DirectScreen.service.QavsdkServiceApi;
import com.stbl.stbl.ui.DirectScreen.util.LiveRoomHelper;
import com.stbl.stbl.ui.DirectScreen.widget.IMPushControl;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.MainHandler;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.utils.UISkipUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.stbl.stbl.widget.jpush.JPushManager;
import com.umeng.analytics.MobclickAgent;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ErrorCode;

import static com.stbl.stbl.item.EventTypeCommon.typeToDongtaiIndex;

@SuppressWarnings("deprecation")
public class TabHome extends BaseTabActivity implements OnClickListener, FinalHttpCallback, ReceiverUtils.MessageReceiver {

    private String flag = TabHome.this.getClass().getSimpleName();
    private final String tab1 = "one";
    private final String tab2 = "two";
    private final String tab3 = "three";
    private final String tab4 = "four";
    private TabHost tabHost;
    private Activity mActivity;
    private LinearLayout con4;
    private ImageView[] imgs = new ImageView[4];
    private TextView[] tabTextViews = new TextView[4];
    private int[] ids = {R.id.bottom_text1, R.id.bottom_text2, R.id.bottom_text3, R.id.bottom_text4};
    private int[] tabLayoutIds = {R.id.tabhome_tablayout1, R.id.tabhome_tablayout2, R.id.tabhome_tablayout3, R.id.tabhome_tablayout4};
    private int[] tabTextViewIds = {R.id.tabhome_tabtview1, R.id.tabhome_tabtview2, R.id.tabhome_tabtview3, R.id.tabhome_tabtview4};
    private int[] draws = {R.drawable.maintab_menu_index_2x, R.drawable.maintab_menu_dynamic_2x, R.drawable.maintab_menu_news_2x, R.drawable.maintab_menu_my_2x};
    private int[] drawPressed = {R.drawable.maintab_menu_index_active_2x, R.drawable.maintab_menu_dynamic_active_2x, R.drawable.maintab_menu_news_active_2x, R.drawable.maintab_menu_my_active_2x};

    private int[] newyear_draws = {R.drawable.newyear_icon_home_nor_3x, R.drawable.newyear_icon_dynamic_nor_3x, R.drawable.newyear_icon_message_nor_3x, R.drawable.newyear_icon_my_nor_3x};
    private int[] newyear_drawPressed = {R.drawable.newyear_icon_home_sel_3x, R.drawable.newyear_icon_dynamic_sel_3x, R.drawable.newyear_icon_message_sel_3x, R.drawable.newyear_icon_my_sel_3x};

    //private int tabTextColorDefault = Color.parseColor("#504a3e");
    //private int tabTextColorPressed = Color.parseColor("#ce7305");

    private int tableft2, tableft3, tableft4;
    private TextView tabview_status;
    private int s_width;

    private int middleBtnWidth;
    private ImageView iview_line, iview_star;

    private LinearLayout layout_showmenu;
    private RelativeLayout linIconWindw;
    private int s_height;
    final String TAG = getClass().getSimpleName();
    public static RongIMClient mRongIMClient;
    private STProgressDialog pressageDialog;

    private boolean mIsDestroy;
    private TextView tvMessageCount;
    private InviteTipsDialog inviteDialog = null;
    int tabIndex = 0;
    //private int currentIndex = 0;
    boolean isActRunning = true;
    /**
     * 动态有新消息红点
     */
    private ImageView ivDongtaiPoint;
    /**
     * 是否打开动态提醒
     */
    private boolean isOpenDongtaiNewMsg = false;

    public static final String Index_index = "index_index"; //去动态某模块

    @Override
    public void onMessage(int receiverType, Bundle bundle) {
        LogUtil.logE("JPushReceiver", "TabHome -  receiverType : " + receiverType);
        switch (receiverType) {
            case ReceiverUtils.DIRECT_PUSH_SERVER_TYPE:
                int pushModelType = bundle.getInt("modelType", -1);
                switch (pushModelType) {
                    case DirectScreenPushServer.DIRECT_PUSH_GUEST_DETAILS: {//嘉宾邀请
                        final GuestInviteInfo inviteInfo = (GuestInviteInfo) bundle.getSerializable("pushinfo");
                        if (inviteInfo == null) break;
                        String userId = SharedToken.getUserId();
                        String guestId = String.valueOf(LoadStore.getInstance().getRoomGuest());
                        String ownerId = String.valueOf(LoadStore.getInstance().getRoomOwner());
                        if (userId != null && userId.equals(ownerId)) return;
                        if (userId != null && userId.equals(guestId)) return;
                        Log.e(flag, " ------------------- UserId : " + userId + " ------ guestId : " + guestId + " ------ ownerId : " + ownerId + " ----- RoomId : " + inviteInfo.getRoomid());
                        inviteDialog = new InviteTipsDialog(this, "直播邀请", false, inviteInfo.getImgurl(), inviteInfo.getUsername() + "邀请您进入直播", "进入直播", new InviteTipsDialog.OkClickListener() {
                            @Override
                            public void onClick() {
//                                checkInRoom(inviteInfo.getRoomid());
                                new LiveRoomHelper(TabHome.this, inviteInfo.getRoomgroupid()).checkInRoom(inviteInfo.getRoomid(), true);

                            }
                        });
                        inviteDialog.show();
                    }
                    break;
                }
                break;
            case ReceiverUtils.TAB_HOME_IN_ROOM://直播间接到直播邀请，结束服务，到tabhome执行下一步
                if (inviteDialog != null) inviteDialog.dismiss();
                if (bundle == null) return;
                final int roomId = bundle.getInt("roomId");
                QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(new QavsdkManger.OnQavsdkRoomCloseListener() {
                    @Override
                    public void roomStatus(int status) {
                        dialogDismiss();
                        switch (status) {
//                            case 1: // 1:房间存在并进入；
//                                break;
                            case 2: // 2：房间存在需关闭回调；
//								break;
//                            case 3: //  3：房间不存在，可以创建新房间
//                                inRoom(roomId);
//                                new LiveRoomHelper(TabHome.this,flag).inRoom(roomId,null);
                                break;
//                            case 4:
//                                break;
                        }
                    }
                });
                break;
            case ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE: {
                int imModelType = bundle.getInt("ImModelType", -1);
                switch (IMPushControl.getIMPushEnum(imModelType)) {
                    case IM_PUSH_CLOSE_ROOM://关闭房间
                        if (QavsdkServiceApi.isQavsdkScreenServiceRuning(this)) {
                            TipsDialog tipsDialog = new TipsDialog(this, "提示", "wow,房主关闭了房间", "确定");
                            tipsDialog.show();
                        }
                        int closeroomid = bundle.getInt("roomId", 0);
                        int roomid = LoadStore.getInstance().getRoomId();//每次创建完房间会保存
                        if (roomid == closeroomid) {
                            QavsdkServiceApi.stopQavsdkScreenService(getApplicationContext());
                            UISkipUtils.stopFloatingDirectScreenService(TabHome.this);
                        }
                        break;
                    case IM_PUSH_SHOT_OFF_ROOM://踢出房间
                        boolean isMine = bundle.getBoolean("isMine", false);
                        if (isMine) {
                            QavsdkServiceApi.stopQavsdkScreenService(getApplicationContext());
                            UISkipUtils.stopFloatingDirectScreenService(TabHome.this);
                            TipsDialog tipsDialog = new TipsDialog(this, "提示", "sorry,你被房主踢出房间", "确定");
                            tipsDialog.show();
                        }
                        break;
                }
            }
            break;
            case ReceiverUtils.PUSH_TYPE_DONGTAI_NEW_MESSAGE: //动态新消息
                int dongtai_pushModelType = bundle.getInt("modelType", -1);
                DongtaiNewMsgItem item = (DongtaiNewMsgItem) bundle.getSerializable("pushinfo");
//                switch (dongtai_pushModelType) {
//                    case DongtaiPushServer.PUTSH_DONGTAI_NEW_MESSAGE:
                ivDongtaiPoint.setVisibility(View.VISIBLE);
//                        break;
//                }
                break;
        }
    }

    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {
            case ACTION.GO_TO_STATUS_PAGE:
                findViewById(tabLayoutIds[1]).performClick();
                final int index_index = intent.getIntExtra(Index_index, 0);
                if (index_index > 0) { //去动态某个模块
                    MainHandler.getInstance().postDelay(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new EventTypeCommon(typeToDongtaiIndex, index_index));
                        }
                    }, 200);
                }
                break;
            case ACTION.GO_TO_MESSAGE_MAIN:
                findViewById(tabLayoutIds[2]).performClick();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.tab_home);

        sBackToHome = false;
        QavsdkManger.getInstance().registerCreateRoom(this, flag);
        LocalBroadcastHelper.getInstance().send(new Intent(ACTION.FINISH_GUIDE_ACTIVITY));

        isOpenDongtaiNewMsg = getIntent().getBooleanExtra("open_dongtai_new_msg", false);
        LogUtil.logD("LogUtil", "TabHome isOpenDongtaiNewMsg -- " + isOpenDongtaiNewMsg);
        if (isOpenDongtaiNewMsg)
            enterAct(DongtaiNotify.class);

        ReceiverUtils.addReceiver(this);
        EventBus.getDefault().register(this);
        YunzhanghuTask.setRefreshSignListener();


        iview_line = (ImageView) findViewById(R.id.tabhome_icon_line);
        iview_star = (ImageView) findViewById(R.id.tabhome_icon_star);
        iview_star.setVisibility(View.GONE);

        ivDongtaiPoint = (ImageView) findViewById(R.id.dongtai_point);
        if (SharedCommon.getStatusesNotify()) {
            ivDongtaiPoint.setVisibility(View.VISIBLE);
        }

        layout_showmenu = (LinearLayout) findViewById(R.id.tabhome_layout_showmenu);
        linIconWindw = (RelativeLayout) findViewById(R.id.tab_home_window_rootlayout);
        //linIconWindw.setBackgroundColor(getResources().getColor(R.color.statuses_pulish_window_bg));//Color.BLACK
        // linIconWindw.getBackground().setAlpha(180);

        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = (ImageView) findViewById(ids[i]);
            tabTextViews[i] = (TextView) findViewById(tabTextViewIds[i]);
        }

        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec(tab1).setIndicator(tab1).setContent(new Intent(this, HomeMainAct.class)));
        tabHost.addTab(tabHost.newTabSpec(tab2).setIndicator(tab2).setContent(new Intent(this, DongtaiAct2.class)));
        tabHost.addTab(tabHost.newTabSpec(tab3).setIndicator(tab3).setContent(new Intent(this, MessageMainAct.class)));
        tabHost.addTab(tabHost.newTabSpec(tab4).setIndicator(tab4).setContent(new Intent(this, MineMainAct.class)));

        tabHost.setCurrentTabByTag(tab1);
        tvMessageCount = (TextView) findViewById(R.id.tvMessageCount);
        s_height = getResources().getDisplayMetrics().heightPixels;
        // testLabelView();
        // MyApplication app = (MyApplication) getApplication();
        s_width = getResources().getDisplayMetrics().widthPixels;
        float item_width = (1.0f / 6) * s_width * 0.8f;
        float left = (1.0f / 24) * s_width + (1.0f / 6) * s_width * 0.1f;

        tableft2 = (int) ((1.0f / 6) * s_width);
        tableft3 = (int) (s_width - (2.0f / 24) * s_width - ((1.0f / 6) * s_width) * 2);
        tableft4 = (int) (s_width - (2.0f / 24) * s_width - ((1.0f / 6) * s_width) * 1);
        middleBtnWidth = (int) ((1.0f / 4) * s_width);
        // 720,30.0,42,162,462,582
        LayoutParams lParams = new LayoutParams((int) item_width, LayoutParams.MATCH_PARENT);
        lParams.leftMargin = (int) left;
        tabview_status = (TextView) findViewById(R.id.tabhome_tview_select);
        tabview_status.setLayoutParams(lParams);
        getUserServerData();
        //dd();
        CommonTask.getOfficeAccount();
        connectKit();
        //LogUtil.logE("home threadId:" + Thread.currentThread().getId());
        checkIMCache();
        JPushManager.getInstance().startPush();


        getUpdateInfo();
        CommonTask.getRedpacketSetting();

        //重新切换tab.此处多用于消息模块
        tabIndex = getIntent().getIntExtra("tabIndex", 0);
        LogUtil.logE(TAG, "tabIndex:" + tabIndex);
        //切换到消息页面注册一下。当然还是默认显示首页咯
        if (tabIndex == 0) {
            int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
            if (!UserRole.isTemp(roleFlag)) {
                findViewById(tabLayoutIds[2]).performClick();
                findViewById(tabLayoutIds[1]).performClick();
                findViewById(tabLayoutIds[0]).performClick();
            }
        } else if (tabIndex < 4 && tabIndex > 0) {
            findViewById(tabLayoutIds[tabIndex]).performClick();
        }

        //动态动画操作相关
//        imgStatusesMore = findViewById(R.id.imgMore);
//        imgShadow = findViewById(R.id.imgShadow);
//        linAnima = findViewById(R.id.linAnima);
//        linAnima.setOnClickListener(this);
//        linAnima.setClickable(false);
        //linAnima.setFocusableInTouchMode(false);//新加

//        imgPulishShoppingCircle = findViewById(R.id.imgPulishShoppingCircle);
//        imgPulishLong = findViewById(R.id.imgPulishLong);
//        imgPulishShort = findViewById(R.id.imgPulishShort);
//        imgStatusesMore.setOnClickListener(this);
//        imgPulishShoppingCircle.setOnClickListener(this);
//        imgPulishLong.setOnClickListener(this);
//        imgPulishShort.setOnClickListener(this);
//
//        imgPulishShoppingCircle.setClickable(false);
//        imgPulishLong.setClickable(false);
//        imgPulishShort.setClickable(false);

        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeListenerReceiver, homeFilter);

        View viewTab2 = findViewById(R.id.tabhome_tablayout2);
        viewTab2.setOnClickListener(new StatusesListener());
        checkNewYear();
    }


    private void checkNewYear() {
        if (SharedPrefUtils.isNewYear()) {
            draws = newyear_draws;
            drawPressed = newyear_drawPressed;
            ImageView main_bingo = (ImageView) findViewById(R.id.main_bingo);
            main_bingo.setImageResource(R.drawable.newyear_tab_home_icon);
            tabTextViews[0].setTextColor(getResources().getColor(R.color.bg_red));
        }
    }

    private final static BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 处理自己的逻辑
                    LogUtil.logE("click home key");
                    MyApplication.getContext().setNeedShowAd(true);
                    sBackToHome = true;
                }
            }
        }
    };

    //View imgStatusesMore, imgShadow, linAnima, imgPulishShoppingCircle, imgPulishLong, imgPulishShort;

    void checkIMCache() {
        RongDB db = new RongDB(this);
        db.queryAll();
    }

    void getUserServerData() {
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
        if (UserRole.isTemp(roleFlag)) {
            return;
        }
        getQueryList();
        //获取云账户签名.需要加游客判断
        YunzhanghuTask.getYunzhanghuSign(SharedToken.getUserId());
        //获取是否广告主。需要加游客判断
        AdTask.getIsAdvertiser(this);
        //获取用户金豆余额。需要加游客判断
        WalletTask.getWalletBalanceBackground(null);

        new HttpEntity(this).commonPostData(Method.getBothGroups, null, this);

        new HttpEntity(this).commonPostData(Method.userLoginInfo, null, this);
    }

    private void showDialog(String message) {
        if (pressageDialog == null)
            pressageDialog = new STProgressDialog(this);

        if (pressageDialog.isShowing())
            pressageDialog.dismiss();
        else {
            pressageDialog.setMsgText(message);
            pressageDialog.show();
        }
    }

    private void dialogDismiss() {
        if (pressageDialog != null)
            pressageDialog.dismiss();
    }


    /**
     * 连接融云 kit版本<br>
     * 有声音 没有显示通知栏 .连接这个界面才能打开界面
     */
    void connectKit() {
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
        if (UserRole.isTemp(roleFlag)) {
            return;
        }
        String rongyunToken = SharedToken.getRongyunToken();
        LogUtil.logE("rongyunToken:" + rongyunToken);
        RongIM.connect(rongyunToken, new ConnectCallback() {
            @Override
            public void onError(ErrorCode arg0) {
                LogUtil.logE(TAG, "--融云连接 onError");
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (true) return;
                        TipsDialog.popup(TabHome.this, getString(R.string.data_error), getString(R.string.enter), new TipsDialog.OnTipsListener() {

                            @Override
                            public void onConfirm() {
                                startActivity(new Intent(TabHome.this, LoginActivity.class));
                                finish();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onSuccess(String arg0) {
                LogUtil.logE(TAG, "--connect--onSuccess");
                RongCloudEvent.getInstance().setOtherListener();
                EventBus.getDefault().post(new IMEventType(IMEventType.typeRefreshIM));

            }

            @Override
            public void onTokenIncorrect() {
                LogUtil.logE(TAG, "connectKit--onTokenIncorrect");
                //finish();
                runOnUiThread(new Runnable() {
                    public void run() {
                        TipsDialog.popup(TabHome.this, getString(R.string.data_error), getString(R.string.enter), new TipsDialog.OnTipsListener() {

                            @Override
                            public void onConfirm() {
                                startActivity(new Intent(TabHome.this, LoginActivity.class));
                                finish();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                });


            }
        });

    }


    @Override
    public void onClick(View v) {
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
        //如果是游客，不能进入动态和IM
        if (UserRole.isTemp(roleFlag) && (v.getId() == R.id.tabhome_tablayout2 || v.getId() == R.id.tabhome_tablayout3)) {
            TipsDialog.popup(this, getString(R.string.temp_account_msg), getString(R.string.temp_account_cancel), getString(R.string.temp_account_ok), new TipsDialog.OnTipsListener() {
                @Override
                public void onConfirm() {
                    startActivity(new Intent(mActivity, GuideActivity.class));
                    finish();
                }

                @Override
                public void onCancel() {
                }
            });
        } else {
            //tab的选择器
            boolean isClickTab = false;
            for (int i = 0; i < tabLayoutIds.length; i++) {
                if (v.getId() == tabLayoutIds[i]) {
                    isClickTab = true;
                    break;
                }
            }
            if (isClickTab) {
                for (int i = 0; i < tabLayoutIds.length; i++) {
                    imgs[i].setImageResource(draws[i]);

                    tabTextViews[i].setTextColor(getResources().getColor(R.color.home_tab_text_normal));
                    if (v.getId() == tabLayoutIds[i]) {
                        imgs[i].setImageResource(drawPressed[i]);
                        if (SharedPrefUtils.isNewYear()) {
                            tabTextViews[i].setTextColor(getResources().getColor(R.color.bg_red));
                        } else {
                            tabTextViews[i].setTextColor(getResources().getColor(R.color.home_tab_text_selected));
                        }
                        setActiviItem(i);
                    }
                }
                //横线做动画
                selectButtonAnimation(v);
            }

        }

        switch (v.getId()) {
            case R.id.main_bingo:
                if (!isAnimationRuning) {
                    showMenu();
                }
                break;
            case R.id.layout_close:
                if (!isAnimationRuning) {
                    closeMenu();
                }
                break;
            case R.id.tabhome_showmenu_shoutu:
                // 我要收徒
                if (!isAnimationRuning) {
                    closeMenu();
                    MobclickAgent.onEvent(this, UmengClickEventHelper.WYSTI);
                    Intent intent = new Intent(mActivity, MyQrcodeActivity.class);
                    intent.putExtra("isFromMine", false);
                    mActivity.startActivity(intent);
                }
                break;
//            case R.id.tabhome_showmenu_item5:
//                // 我要逛逛
//                if (!isAnimationRuning) {
//                    MobclickAgent.onEvent(this, UmengClickEventHelper.WYGGI);
//                    closeMenu();
//                    Intent it = new Intent(this, MallAct.class);
//                    SharedMallType.putType(this, SharedMallType.typeSourceDefault);
//                    startActivity(it);
//                }
//                break;
            case R.id.tabhome_showmenu_account_upgrade:
                //升级账户
                if (!isAnimationRuning) {
                    MobclickAgent.onEvent(this, UmengClickEventHelper.SJZHI);
                    closeMenu();
                    Intent intent = new Intent(this, CommonWeb.class);
                    String url = (String) SharedPrefUtils.getFromPublicFile(KEY.account_update, "");
                    if (TextUtils.isEmpty(url)) {
                        return;
                    }
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
                break;
            case R.id.tabhome_showmenu_brand_plus: //品牌+
                UmengClickEventHelper.onPPJClickEvent(this);

                if (!isAnimationRuning) {
                    closeMenu();
                    if ((int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0) == 0) {
                        enterAct(IntroduceAdAct.class);
                        //获取是否广告主
                        AdTask.getIsAdvertiser(this);
                    } else {
//                    //已开通广告位
                        enterAct(AdManagerActivity.class);
                    }
                }
//                Intent intent = new Intent(this, RPRedPacketActivity.class);
//                // 设置发送红包所需要的参数
//                RedPacketInfo redPacketInfo = new RedPacketInfo();
//                redPacketInfo.fromAvatarUrl = "none";// 当前用户头像url 默认值为none 不可为空
//                redPacketInfo.fromNickName = "Max";// 当前用户昵称 不可为空
//                // 根据聊天类型设置红包参数
////                if (...){ // 如果是单聊
////                redPacketInfo.toUserId = SharedToken.getUserId(this); // 接收者id
////                redPacketInfo.chatType = RPConstant.CHATTYPE_SINGLE; // 单聊
////                }else if (...){ // 如果是群聊
//                redPacketInfo.toGroupId = "568489413"; // 群id
//                redPacketInfo.groupMemberCount = 10; // 群人数
//                redPacketInfo.chatType = RPConstant.CHATTYPE_GROUP; // 群聊
//                // 使用专属红包需要设置回调，不需要此功能可以不设置
////                RPGroupMemberUtil.getInstance().setGroupMemberListener(new NotifyGroupMemberCallback() {
////                    @Override
////                    public void getGroupMember(final String groupID, final GroupMemberCallback
////                            callback) {
////                        // 根据groupID获取成员信息(不包括当前用户)建议事先获取后直接传入list
////                        // RPUserBean需要传入用户id，头像url，昵称
////                        List<RPUserBean> userBeanList = new ArrayList<RPUserBean>();
////                        callback.setGroupMember(userBeanList);
////                    }
////                });
////            }
//            TokenData tokenData = new TokenData();
//// 传入当前用户id 不可为空
//            tokenData.appUserId = SharedToken.getUserId(this);
//            intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, tokenData);
//            intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
//            startActivityForResult(intent, 0);
//            startActivityForResult(intent, REQUEST_CODE_SEND_RED_PACKET);

                break;
            default:
                break;
            case R.id.imgMore:
                if (isShowStatusesAnim) {
                    showAnimation(false);
                } else {
                    MobclickAgent.onEvent(this, UmengClickEventHelper.DTFBAN);
                    showAnimation(true);
                }
                break;
            case R.id.linAnima:
                if (isShowStatusesAnim) {
                    showAnimation(false);
                }
                break;
            case R.id.tabhome_showmenu_redpacket:
                if (!isAnimationRuning) {
                    closeMenu();
                    MobclickAgent.onEvent(this, UmengClickEventHelper.FBHB);
                    if ((int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0) == 0) {
                        ShoppingCircleTipsDialog.create(this);
                    } else {
                        //去发布购物圈
                        enterAct(PublishShoppingActivity.class);
                    }
                }
                break;
            case R.id.tabhome_showmenu_long_status:
                if (!isAnimationRuning) {
                    closeMenu();
                    MobclickAgent.onEvent(this, UmengClickEventHelper.FBCDT);
                    enterAct(DongtaiPulishLongAct.class);
                }
                break;
            case R.id.tabhome_showmenu_short_status:
                if (!isAnimationRuning) {
                    closeMenu();
                    MobclickAgent.onEvent(this, UmengClickEventHelper.FBDDT);
                    enterAct(PublishShortStatusActivity.class);
                }
                break;
        }
    }

    //onActivityResult 测试用的
    public static final int REQUEST_CODE_SEND_RED_PACKET = 1111;

    // 发送红包成功后回调到该方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // 这里需要调用IM的消息发送接口，发送红包消息到聊天界面。
                case REQUEST_CODE_SEND_RED_PACKET:
                    // 从data中取出发红包需要的属性
                    if (data != null) {
                        // 祝福语
                        data.getStringExtra(RPConstant.EXTRA_RED_PACKET_GREETING);
                        // 红包ID
                        data.getStringExtra(RPConstant.EXTRA_RED_PACKET_ID);
                        // 专属红包使用该属性，如果没有专属红包可不获取该属性，可能为空
                        data.getStringExtra(RPConstant.EXTRA_RED_PACKET_RECEIVER_ID);
                        // 红包类型
                        data.getStringExtra(RPConstant.EXTRA_RED_PACKET_TYPE);
                        // 将获取到的属性放入消息体中，发送红包消息到聊天页面
//                        sendMessage(message);// 此处为伪代码，由开发者根据自己使用的IM来实现
                    }
                    break;
            }
        }
    }

    void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    boolean isShowStatusesAnim = false;

    //新的上下动画
    void showAnimation(boolean isShow) {
        this.isShowStatusesAnim = isShow;
        //StatusesAnimUtil.showAnimation(isShow, linAnima, imgPulishShoppingCircle, imgPulishLong, imgPulishShort, imgStatusesMore, imgShadow);

    }


    /**
     * 点击底部按钮时 按钮上方高亮线条动画
     */
    private void selectButtonAnimation(View view) {
        switch (view.getId()) {
//            case R.id.main_bingo:
//                if (!isAnimationRuning) {
//                    showMenu();
//
//                }
//
//                break;
            case R.id.tabhome_tablayout1:
//                if (imgStatusesMore != null) {
//                    linAnima.setVisibility(View.GONE);
//                }
                scrollAnimation(0);
                break;
            case R.id.tabhome_tablayout2:
                scrollAnimation(1);
                //ToastUtil.showToast("hello 2");
//                if (imgStatusesMore != null) {
//                    linAnima.setVisibility(View.VISIBLE);
//                    imgStatusesMore.setVisibility(View.VISIBLE);
//                    imgShadow.setVisibility(View.VISIBLE);
//                }
                break;
            case R.id.tabhome_tablayout3:
//                if (imgStatusesMore != null) {
//                    imgStatusesMore.setVisibility(View.GONE);
//                    imgShadow.setVisibility(View.GONE);
//                    linAnima.setVisibility(View.INVISIBLE);
//                }
                scrollAnimation(2);
                break;
            case R.id.tabhome_tablayout4:
//                if (imgStatusesMore != null) {
//                    imgStatusesMore.setVisibility(View.GONE);
//                    imgShadow.setVisibility(View.GONE);
//                    linAnima.setVisibility(View.INVISIBLE);
//                }
                scrollAnimation(3);
                break;
            default:
                break;
        }
    }

    void getQueryList() {
        Params params = new Params();
        params.put("type", IMSetting.queryTypeTop);
        new HttpEntity(this).commonPostData(Method.imSettingQuery, params, this);
    }


    boolean isStatusesSingleClick = true;

    private void setActiviItem(int index) {
        switch (index) {
            case 0:
                // tabHost.setCurrentTabByTag(tab1);
                if (tabIndex == index && tabHost.getCurrentTabTag().equals(tab1)) {
                    EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshHomePage));
                } else {
                    tabHost.setCurrentTabByTag(tab1);
                }

                break;
            case 1:
                if (tabIndex == index && tabHost.getCurrentTabTag().equals(tab2)) {
                    if (isStatusesSingleClick) {
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeToStatusesTop));
                    } else {
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshStatuses));
                    }
                } else {
                    tabHost.setCurrentTabByTag(tab2);
                }
                break;
            case 2:
                if (tabIndex == index && tabHost.getCurrentTabTag().equals(tab3)) {
                    EventBus.getDefault().post(new IMEventType(IMEventType.typeRefreshIM));
                } else {
                    tabHost.setCurrentTabByTag(tab3);
                }
                break;
            case 3:
                if (tabIndex == index && tabHost.getCurrentTabTag().equals(tab4)) {
                    EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                } else {
                    tabHost.setCurrentTabByTag(tab4);
                }
                break;

        }

    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        String con = JSONHelper.getStringFromObject(item.getResult());
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getErr() == null) {
//                ToastUtil.showToast(this, "err is null 后台数据有错");
                LogUtil.logE("LogUtil", "err is null 后台数据有错");
                return;
            }
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }

            if (item.getErr().getMsg() != null)
                if ("认证失败".equals(item.getErr().getMsg()) || item.getErr().getMsg().contains("非法")) {
                    SharedToken.clearToken();
                    startActivity(new Intent(TabHome.this, LoginActivity.class));
                    finish();
                }
            return;
        }
        if (methodName.equals(Method.userLoginInfo)) {
            UserItem user = JSONHelper.getObject(con, UserItem.class);
            //SharedUser.putPhone(this, user.getAreacode(), user.getTelphone(), user.getOtherauthtype(), user.getHaspaypassword());
            //SharedUser.putInfo(this, user.getNickname(), user.getImgmiddleurl());
            //SharedUser.putInviteCode(this, user.getInvitecode());
            SharedUser.putUserValue(user);
            // SharedToken.putRoleFlag(this, user.getRoleflag());拜师或者登录 刷新 都有保存了。
        } else if (methodName.equals(Method.refreshToken)) {
            if (item.getIssuccess() == BaseItem.successTag) {
                String obj = JSONHelper.getStringFromObject(item.getResult());
                AuthToken token = JSONHelper.getObject(obj, AuthToken.class);
                // SharedToken.putValue(this, token.getAccessToken(),
                // token.getRefreshToken(), token.getUserId());
            }
        } else if (methodName.equals(Method.getBothGroups)) {
            GroupBoth groupBoth = JSONHelper.getObject(con, GroupBoth.class);
            GroupTeam group1 = groupBoth.getMastergroup();
            GroupTeam group2 = groupBoth.getMygroup();
            if (group1 != null) {
                SharedGroups.putMasterGroupId(this, String.valueOf(group1.getGroupid()));
            }
            if (group2 != null) {
                SharedGroups.putMineGroupId(this, String.valueOf(group2.getGroupid()));
            }
        }

        switch (methodName) {
            case Method.imSettingQuery:
                List<IMSettingStatus> listStatus = JSONHelper.getList(con, IMSettingStatus.class);
                TopSettingDB settingDB = new TopSettingDB(this);
                if (listStatus.size() > 0) settingDB.deleteAllData();
                for (IMSettingStatus status : listStatus) {
                    settingDB.insert(status.getBusinesstype(), String.valueOf(status.getBusinessid()), TopSettingDB.stateTopYes);

                }
                break;
        }

    }

    /**
     *
     * */
    private void scrollAnimation(int position) {
        switch (position) {
            case 0:
                switch (tabIndex) {
                    case 1:
                        scrollLine(tableft2, 0);
                        // Log.e("scrollAnimation", "=========>>" + (tableft2 + "," + 0));
                        break;
                    case 2:
                        scrollLine(tableft3, 0);
                        //Log.e("scrollAnimation", "=========>>" + (tableft3 + "," + 0));
                        break;
                    case 3:
                        scrollLine(tableft4, 0);
                        //Log.e("scrollAnimation", "=========>>" + (tableft4 + "," + 0));
                        break;
                }
                break;
            case 1:
                switch (tabIndex) {
                    case 0:
                        scrollLine(0, tableft2);
                        //Log.e("scrollAnimation", "=========>>" + (0 + "," + tableft2));
                        break;
                    case 2:
                        scrollLine(tableft3, tableft2);
                        // Log.e("scrollAnimation", "=========>>" + (tableft3 + "," + tableft2));
                        break;
                    case 3:
                        scrollLine(tableft4, tableft2);
                        // Log.e("scrollAnimation", "=========>>" + (tableft4 + "," + tableft2));
                        break;

                }
                break;
            case 2:
                switch (tabIndex) {
                    case 0:
                        scrollLine(0, tableft3);
                        //Log.e("scrollAnimation", "=========>>" + (0 + "," + tableft3));
                        break;
                    case 1:
                        scrollLine(tableft2, tableft3);
                        // Log.e("scrollAnimation", "=========>>" + (tableft2 + "," + tableft3));
                        break;
                    case 3:
                        scrollLine(tableft4, tableft3);
                        // Log.e("scrollAnimation", "=========>>" + (tableft4 + "," + tableft3));
                        break;

                }
                break;
            case 3:
                switch (tabIndex) {
                    case 0:
                        scrollLine(0, tableft4);
                        break;
                    case 1:
                        scrollLine(tableft2, tableft4);
                        break;
                    case 2:
                        scrollLine(tableft3, tableft4);
                        break;

                }
                break;

        }
        tabIndex = position;
    }

    public void onEvent(IMEventType event) {
        if (event.getType() == IMEventType.typeRefreshAllUnreadCount) {
            if (event.getCount() > 0) {
                tvMessageCount.setText(String.valueOf(event.getCount()));
                tvMessageCount.setVisibility(View.VISIBLE);
            } else {
                tvMessageCount.setVisibility(View.GONE);
            }
        }
    }

    //动态新消息查看完消除红点
    public void onEvent(DongtaiEventType event) {
        if (event.getType() == DongtaiEventType.NEW_MESSAGE) {
            ivDongtaiPoint.setVisibility(View.GONE);
        }
    }

    private void scrollLine(int fromX, int endX) {
        TranslateAnimation anim = new TranslateAnimation(fromX, endX, 0, 0);
        anim.setFillAfter(true);
        anim.setDuration(200);
        anim.setInterpolator(new OvershootInterpolator(1));
        tabview_status.startAnimation(anim);
    }

    private void dd() {
        handler.post(middleBtnAnimatorRunnable);
    }


    private Runnable middleBtnAnimatorRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            int start = 3;
            final int end = middleBtnWidth - iview_line.getWidth() * 2;
            ValueAnimator vAnimator = ValueAnimator.ofInt(start, end);
            vAnimator.setDuration(2000);
            vAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // TODO Auto-generated method stub
                    LayoutParams flParams = (LayoutParams) iview_line.getLayoutParams();
                    int avalue = NumUtils.getObjToInt(animation.getAnimatedValue());
                    flParams.leftMargin = avalue;
                    iview_line.requestLayout();
                    iview_line.setAlpha(avalue * 1.0f / end);
                    if (avalue > end - 20 && !isStarAnimationStart) {
                        iview_star.setVisibility(View.VISIBLE);
                        final int dtime = 1500;
                        AnimationSet aniSet = new AnimationSet(true);

                        RotateAnimation rAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rAnimation.setDuration(dtime);
                        // rAnimation.setRepeatCount(3);
                        rAnimation.setAnimationListener(new AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                isStarAnimationStart = true;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub
                                AlphaAnimation animation2 = new AlphaAnimation(1, 0);
                                animation2.setDuration(1500);
                                animation2.setInterpolator(new LinearInterpolator());
                                animation2.setAnimationListener(new AnimationListener() {

                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        isStarAnimationStart = false;
                                        // TODO Auto-generated method stub
                                        LayoutParams flParams = (LayoutParams) iview_line.getLayoutParams();
                                        flParams.leftMargin = 20;
                                        iview_line.requestLayout();
                                        iview_star.setVisibility(View.GONE);
                                        iview_line.setAlpha(0f);
                                        handler.postDelayed(middleBtnAnimatorRunnable, 3000);
                                    }
                                });

                                iview_star.startAnimation(animation2);
                                AlphaAnimation animation3 = new AlphaAnimation(1, 0);
                                animation3.setDuration(1500);
                                animation3.setInterpolator(new LinearInterpolator());
                                iview_line.startAnimation(animation3);
                            }
                        });
                        rAnimation.setInterpolator(new LinearInterpolator());

                        AlphaAnimation animation2 = new AlphaAnimation(0, 1);
                        animation2.setDuration(200);
                        animation2.setInterpolator(new LinearInterpolator());

                        aniSet.addAnimation(rAnimation);
                        aniSet.addAnimation(animation2);

                        iview_star.startAnimation(aniSet);
                    }
                }
            });
            vAnimator.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub
                    LayoutParams flParams = (LayoutParams) iview_line.getLayoutParams();
                    flParams.leftMargin = 20;
                    iview_line.requestLayout();
                    iview_line.setAlpha(0f);
                    iview_star.setVisibility(View.GONE);
                }
            });
            vAnimator.start();
        }
    };


    public void setStatusesMoreVisibility(int visibility) {
//        imgStatusesMore.setVisibility(visibility);
//        imgShadow.setVisibility(visibility);
//        if (visibility == View.VISIBLE) {
//            linAnima.setVisibility(View.VISIBLE);
//        }
    }

    private boolean isStarAnimationStart = false;

    private Handler handler = new Handler();

    private void showMenu() {
//        imgStatusesMore.setVisibility(View.GONE);
//        imgShadow.setVisibility(View.GONE);
        linIconWindw.setVisibility(View.VISIBLE);

        int bottomMargin = ((RelativeLayout.LayoutParams) layout_showmenu.getLayoutParams()).bottomMargin;
        AnimationSet aSet = new AnimationSet(true);

        TranslateAnimation anim = new TranslateAnimation(0, 0, bottomMargin + layout_showmenu.getHeight(), 0);
        anim.setFillAfter(true);
        anim.setDuration(200);// 2000
        anim.setInterpolator(new OvershootInterpolator(0.5f));
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                isAnimationRuning = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                isAnimationRuning = false;
            }
        });
        aSet.addAnimation(anim);
        aSet.setInterpolator(new OvershootInterpolator(0.5f));

        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(200);
        aSet.addAnimation(animation);

        layout_showmenu.startAnimation(aSet);

//        animation = new AlphaAnimation(0, 1);
//        animation.setDuration(200);
//        linIconWindw.startAnimation(animation);
    }

    private void closeMenu() {
        int bottomMargin = ((RelativeLayout.LayoutParams) layout_showmenu.getLayoutParams()).bottomMargin;
        // final int start = bottomMargin+layout_showmenu.getHeight();
        // ValueAnimator vAnimator = ValueAnimator.ofInt(start,0);
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, bottomMargin + layout_showmenu.getHeight());
        anim.setFillAfter(true);
        anim.setDuration(200);// 1000
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                isAnimationRuning = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                linIconWindw.setVisibility(View.GONE);
                isAnimationRuning = false;
                //如果在动态栏，就展示动态浮框按钮
//                if (tabIndex == 1) {
//                    imgStatusesMore.setVisibility(View.VISIBLE);
//                    imgShadow.setVisibility(View.VISIBLE);
//                }
            }
        });

        layout_showmenu.startAnimation(anim);

//        AlphaAnimation animation = new AlphaAnimation(1, 0);
//        animation.setDuration(200);
//        animation.setAnimationListener(new AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // TODO Auto-generated method stub
//                linIconWindw.setBackgroundColor(Color.BLACK);
//                linIconWindw.getBackground().setAlpha(180);
//            }
//        });
//        linIconWindw.startAnimation(animation);
    }

    private boolean isAnimationRuning = false;

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (isAnimationRuning) {
                    return true;
                }
                if (linIconWindw.getVisibility() == View.VISIBLE) {
                    closeMenu();
                    return true;
                }

//                if (isShowStatusesAnim) {
//                    showAnimation(false);
//                    return true;
//                }
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    ToastUtil.showToast(R.string.click_twice_to_exit);
                    firstTime = secondTime;// 更新firstTime
                    return true;
                } else { // 两次按键小于2秒时，退出应用
                    finish();
                    //System.exit(0);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    //动态点击 双击监听
    long firstStatusesTime;

    class StatusesListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstStatusesTime > 1000) {
                firstStatusesTime = secondTime;
                // ToastUtil.showToast("单击");
                isStatusesSingleClick = true;
                TabHome.this.onClick(view);
            } else {
                isStatusesSingleClick = false;
                //ToastUtil.showToast("双击");
                TabHome.this.onClick(view);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushManager.getInstance().resumePush();
        if (QavsdkServiceApi.isQavsdkScreenServiceRuning(getApplicationContext())) {
            UISkipUtils.startFloatingDirectScreenService(this);
        }
        if (sBackToHome) {
            sBackToHome = false;
            getUpdateInfo();
        }
        tabInit();
    }

    /**
     * 处理再次进入app后tab项不对的情况
     */
    private void tabInit() {
        String curTab = tabHost.getCurrentTabTag();
        int tabIndex = 0;
        switch (curTab) {
            case tab1:
                tabIndex = 0;
                break;
            case tab2:
                tabIndex = 1;
                break;
            case tab3:
                tabIndex = 2;
                break;
            case tab4:
                tabIndex = 3;
                break;
        }

        for (int i = 0; i < tabLayoutIds.length; i++) {
            imgs[i].setImageResource(draws[i]);
            tabTextViews[i].setTextColor(getResources().getColor(R.color.home_tab_text_normal));
            if (tabLayoutIds[tabIndex] == tabLayoutIds[i]) {
                imgs[i].setImageResource(drawPressed[i]);
                if (SharedPrefUtils.isNewYear()) {
                    tabTextViews[i].setTextColor(getResources().getColor(R.color.bg_red));
                } else {
                    tabTextViews[i].setTextColor(getResources().getColor(R.color.home_tab_text_selected));
                }
            }
        }


    }

    private static boolean sBackToHome = false;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        QavsdkManger.getInstance().formatAvContext();
        JPushManager.getInstance().setAlias(this);
    }

    private void getUpdateInfo() {
        UpdateTask.getUpdateInfo().setCallback(this, mGetUpdateInfoCallback).start();
    }

    private UpdateLogDialog mUpdateDialog;
    private SimpleTask.Callback<UpdateInfo> mGetUpdateInfoCallback = new SimpleTask.Callback<UpdateInfo>() {
        @Override
        public void onError(TaskError e) {

        }

        @Override
        public void onCompleted(UpdateInfo result) {
            if (mUpdateDialog == null) {
                mUpdateDialog = new UpdateLogDialog(TabHome.this);
            }
            if (mUpdateDialog.isShowing() || mUpdateDialog.isShowingAlertDialog()) {
                return;
            }
            if (result.getShowtips() == UpdateInfo.showDialog)
                mUpdateDialog.show(result);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        handler.removeCallbacks(middleBtnAnimatorRunnable);
        QavsdkManger.getInstance().unregisterCreateRoom(this, flag);
        EventBus.getDefault().unregister(this);
        isActRunning = false;
        ReceiverUtils.removeReceiver(this);
        unregisterReceiver(homeListenerReceiver);
    }

}
