package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.TopSettingDB;
import com.stbl.stbl.adapter.ContactAdapter;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.IMSetting;
import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.ApplyItem;
import com.stbl.stbl.item.im.GroupBoth;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.ui.BaseClass.STBLActivity;
import com.stbl.stbl.util.BadgeUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.GuideUtil;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIMClientWrapper;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;

/**
 * 消息模块的主页 hello dev分支
 *
 * @author lenovo
 */
public class MessageMainAct extends STBLActivity implements OnClickListener, FinalHttpCallback, GetNewMessageReceiver.ReceiverListener, ContactAdapter.OnItemListener {
    ListView listView;
    //View emptyView;
    RelativeLayout top_banner;
    PopupWindow window;
    ContactAdapter adapter;

    GroupTeam group1, group2;
    Context mContext;
    public final static String actionGetOrSentNewMessage_RefreshConversationList = "STBL_IM_NEW_MESSAGE";
    public final static String actionGetNewApply = "STBL_IM_NEW_Apply";
    final String tag = getClass().getSimpleName();

    //View frameInvite;
    final int typeGroupMaster = 1;
    final int typeGroupMine = 2;
    GetNewMessageReceiver receiver;
    //View tvAddTip;
    //public static boolean isShowGuide = false;
    //View imgGroup1Silence, imgGroup2Silence;
    private DataCacheDB cacheDB = new DataCacheDB(this);
    TextView tvApplyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_main);
        initView();
        mContext = this;
        // 去去掉list.setOnItemClick
        //CommonTask.getFriendList(GroupMemberList.typeRequestNoneGroup, 0, GroupMemberList.hasselfNo);
        EventBus.getDefault().register(this);
        //getBothGroup();
        getRongConversationList();
        getApplyCount();
        receiver = new GetNewMessageReceiver();
        receiver.setOnReceiverListener(this);

    }

    @Override
    protected void setStatusBar() {
    }


    @Override
    protected void onResume() {
        super.onResume();
        receiver.setOnReceiverListener(this);
//        if (group1 == null) {
//            getBothGroup();
//        }
        getRongConversationList();
        if (!GuideUtil.guidePage3(this) && GuideUtil.isGuidePage1Finished(this)) {
            show(pageIM);
        }

    }

    /**
     * 获取被申请数量
     */
    void getApplyCount() {
        new HttpEntity(this).commonPostData(Method.imGetReplayCount, null, this);
    }


    public void onEvent(IMEventType type) {
        switch (type.getType()) {
            case IMEventType.typeUpdateGroupInfo:
                //getBothGroup();
                break;
            case IMEventType.typeRefreshIM:
                listView.smoothScrollToPositionFromTop(0, 0);//  smoothScrollToPosition
                //listView.smoothScrollToPosition( 0);//  smoothScrollToPosition
                //getBothGroup();
                getRongConversationList();
                getApplyCount();
                // CommonTask.getFriendList(GroupMemberList.typeRequestNoneGroup, 0, GroupMemberList.hasselfNo);
                break;
            case IMEventType.typeUpdateContactList:
                getRongConversationList();
                break;
            case IMEventType.typeIMOtherDevice:
                LogUtil.logE("关闭typeIMOtherDevice");
                finish();
                break;
            case IMEventType.typeIMViewedApply:
                tvApplyCount.setVisibility(View.GONE);
                tvApplyCount.setText(String.valueOf(0));
                notifyShowHomeMessageCount();
                break;

        }
    }

    void initView() {
        tvApplyCount = (TextView) findViewById(R.id.tvApplyCount);
        findViewById(R.id.linContact).setOnClickListener(this);
        findViewById(R.id.top_right).setOnClickListener(this);
        findViewById(R.id.top_banner_middle).setOnClickListener(this);
        top_banner = (RelativeLayout) findViewById(R.id.top_banner);
        listView = (ListView) findViewById(R.id.list);


        adapter = new ContactAdapter(this);
        adapter.setOnItemListener(this);
        listView.setAdapter(adapter);

    }


    //底部icon会挡住最后一条消息
    private View getFooterView() {
        View footerview = new View(this);
        int height = Util.dip2px(this, 15);
        footerview.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height));
        footerview.setBackgroundColor(getResources().getColor(R.color.transparent));
        return footerview;
    }

    /**
     * 删除会话
     */
    public void onItemDelete(final Conversation.ConversationType type, final String targetId, final int position) {
        RongIM.getInstance().getRongIMClient().removeConversation(type, targetId, new ResultCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean arg0) {
                //先去掉数据库里的置顶
                int mType = 0;
                switch (type) {
                    case PRIVATE:
                        mType = IMSettingStatus.businesstypePrivate;
                        break;
                    case GROUP:
                        if (SharedGroups.isGroupMineOrMaster(targetId, mContext)) {
                            mType = IMSettingStatus.businesstypeGroup;
                        } else {
                            mType = IMSettingStatus.businesstypeDiscussion;
                        }
                        break;

                }
                //getRongConversationList();
                adapter.delete(position);
                //清除聊天记录
                RongIM.getInstance().getRongIMClient().clearMessages(type, targetId, null);
                deleteTop(targetId, mType);
            }

            @Override
            public void onError(ErrorCode arg0) {

            }
        });
    }


    void deleteTop(final String targetId, final int type) {
        Params params = new Params();
        params.put("businessid", targetId);
        params.put("businesstype", type);
        params.put("updatetype", IMSetting.queryTypeTop);
        params.put("opeatetype", IMSetting.topOperateDelete);
        new HttpEntity(this).commonPostData(Method.setTargetTop, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {

                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                //删除服务器置顶后，就删除本地的置顶记录
                if (item.getIssuccess() == BaseItem.successTag) {

                    LogUtil.logE("删除了置顶");
                    TopSettingDB settingDb = new TopSettingDB(mContext);
                    settingDb.delete(type, targetId);
                }


            }
        });
    }


    // 获取师傅的帮会和我的帮会
    void getBothGroup() {
        if (group1 != null && group2 != null) {
            addGroup(typeLaterAddGroup);
            return;
        }
        showGroups(cacheDB.getIMGroupsCacheJson());
        new HttpEntity(this).commonPostData(Method.getBothGroups, null, this);
    }

    /**
     * 获取融云各种类型的会话列表。
     */
    void getRongConversationList() {
        //LogUtil.logE("im", "getRongConversationList");
        List<Conversation> list = null;
        RongIMClientWrapper clientWrapper = RongIM.getInstance().getRongIMClient();
        if (clientWrapper != null)
            list = clientWrapper.getConversationList(ConversationType.PRIVATE, ConversationType.DISCUSSION, ConversationType.GROUP);
        if (list == null) {
            //LogUtil.logE("group", "list is null");
            adapter.clear();
        } else {
            //LogUtil.logE("group size", list.size());
            adapter.setData(list);
            notifyShowHomeMessageCount();
        }

        initTopSetting();
        //需要重新考虑 什么时候去做这个请求
        getBothGroup();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //通知去显示未读消息数目

    void notifyShowHomeMessageCount() {
        String replyContStr = tvApplyCount.getText().toString();
        int privateCount = adapter.getUnReadCount();
        int numReply = 0;

        try {
            numReply = Integer.valueOf(replyContStr);
        } catch (Exception e) {

        }
        int countAll = privateCount + numReply;
        IMEventType event = new IMEventType(IMEventType.typeRefreshAllUnreadCount, countAll);
        EventBus.getDefault().post(event);
        if (countAll > 0)
            BadgeUtil.setBadgeCount(getApplicationContext(), countAll);
        else
            BadgeUtil.resetBadgeCount(getApplicationContext());
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.linContact:
                intent = new Intent(this, ContactsActivity.class);
                int applyCount = Integer.parseInt(tvApplyCount.getText().toString().equals("") ? "0" : tvApplyCount.getText().toString());
                intent.putExtra("applyCount", applyCount);
                startActivity(intent);
//                view.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvApplyCount.setVisibility(View.GONE);
//                        tvApplyCount.setText(String.valueOf(0));
//                        notifyShowHomeMessageCount();
//                    }
//                }, 100);
                break;
            case R.id.top_right:
                showRightWindow();
                break;
        }
    }

    void showRightWindow() {
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                switch (view.getId()) {
                    case R.id.window_btn1:
                        MobclickAgent.onEvent(MessageMainAct.this, UmengClickEventHelper.XXCJTLZ);
                        enterAct(CreateDiscussionTeamAct.class);
                        break;
                    case R.id.window_btn2:
                        MobclickAgent.onEvent(MessageMainAct.this, UmengClickEventHelper.XXTJHY);
                        enterAct(FriendAddAct.class);
                        break;
                    case R.id.window_btn3:
                        enterAct(GroupSendAct.class);
                        break;
                    case R.id.windowLin:

                        break;
                }

            }
        };

        if (window != null && window.isShowing()) {
            window.dismiss();
            return;
        }
        View windowView = getLayoutInflater().inflate(R.layout.message_right_window, null);
        //windowView.setBackgroundResource(R.drawable.bg_statuses_main_window);
        windowView.findViewById(R.id.window_btn1).setOnClickListener(listener);
        windowView.findViewById(R.id.window_btn2).setOnClickListener(listener);
        windowView.findViewById(R.id.window_btn3).setOnClickListener(listener);
        windowView.measure(0, 0);
        windowView.setOnClickListener(listener);
        int height = windowView.getMeasuredHeight();
        int width = windowView.getMeasuredWidth();
        window = new PopupWindow(windowView, Device.getWidth(), (Device.getHeight() - Device.getStatusBasrHeight(this) - top_banner.getHeight()));//bg_statuses_main_window
        window.setFocusable(true);
        window.setTouchable(true);
        //window.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_statuses_main_window));
        int widthOff = Device.getWidth(this) - windowView.getMeasuredWidth();
        //window.showAtLocation(windowView, Gravity.NO_GRAVITY, widthOff, Device.getStatusBasrHeight(this) + top_banner.getHeight());
        window.showAtLocation(windowView, Gravity.NO_GRAVITY, 0, top_banner.getHeight() + Device.getStatusBasrHeight(this));//Device.getStatusBasrHeight(this) + top_banner.getHeight()
    }

    public void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        TabHome home = (TabHome) getParent();
        return home.onKeyDown(keyCode, event);
    }


    void showGroups(String obj) {
        if (obj == null) return;
        GroupBoth groupBoth = JSONHelper.getObject(obj, GroupBoth.class);
        group1 = groupBoth.getMastergroup();
        group2 = groupBoth.getMygroup();
        addGroup(typeInitAddGroup);
    }


    final int typeInitAddGroup = 0;
    final int typeLaterAddGroup = 1;

    void addGroup(int type) {
        RongDB userDB = new RongDB(this);
        if (group1 != null) {
            //下面新加
            if (type == typeInitAddGroup)
                userDB.insert(new IMAccount(RongDB.typeGroup, group1.getGroupid() + "", group1.getGroupname(), group1.getIconlarurl(), UserItem.certificationNo, ""));
            Conversation con = Conversation.obtain(ConversationType.GROUP, String.valueOf(group1.getGroupid()), group1.getGroupname());
            con.setPortraitUrl(group1.getIconlarurl());
            adapter.addGroupItem(con);
        }

        if (group2 != null) {
            //下面新加
            if (type == typeInitAddGroup)
                userDB.insert(new IMAccount(RongDB.typeGroup, group2.getGroupid() + "", group2.getGroupname(), group2.getIconlarurl(), UserItem.certificationNo, ""));
            Conversation con = Conversation.obtain(ConversationType.GROUP, String.valueOf(group2.getGroupid()), group2.getGroupname());
            con.setPortraitUrl(group2.getIconlarurl());
            adapter.addGroupItem(con);

        }
//        UserItem syetmImUser = groupBoth.getAssistant();
//        if (syetmImUser == null)
//            return;
//        userDB.insert(new IMAccount(RongDB.typeUser, syetmImUser.getUserid() + "", syetmImUser.getNickname(), syetmImUser.getImgurl(), syetmImUser.getCertification(), ""));

    }

    @Override
    public void parse(String methodName, String result) {
        if (listView != null) {
            //listView.onRefreshComplete();
        }

        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getBothGroups:
                if (obj == null) return;
                cacheDB.saveIMGroupsCacheJson(obj);
                showGroups(obj);


                break;
            case Method.imGetReplayCount:
                ApplyItem apply = JSONHelper.getObject(obj, ApplyItem.class);
                int applycount = apply.getApplycount();
                if (applycount > 0) {
                    // frameInvite.setVisibility(View.VISIBLE);
                    tvApplyCount.setText(String.valueOf(applycount));
                    tvApplyCount.setVisibility(View.VISIBLE);
                    //tvAddTip.setVisibility(View.VISIBLE);
                } else {
                    tvApplyCount.setText(String.valueOf(0));
                    tvApplyCount.setVisibility(View.GONE);
                    //tvAddTip.setVisibility(View.INVISIBLE);
                    //frameInvite.setVisibility(View.GONE);//新加的屏蔽
                }

                notifyShowHomeMessageCount();
                break;
        }
    }

    public void newMessage() {
        //LogUtil.logE(tag,"newMessage");
        getRongConversationList();
    }

    public void newApply() {
        LogUtil.logE("im", "newApply");
        getApplyCount();
//        String countStr = tvApplyCount.getText().toString();
//        try {
//            int count = Integer.valueOf(countStr);
//            count++;
//            tvApplyCount.setVisibility(View.VISIBLE);
//            tvApplyCount.setText(String.valueOf(count));
//
//            // frameInvite.setVisibility(View.VISIBLE);
//            // tvAddTip.setVisibility(View.VISIBLE);
//            notifyShowHomeMessageCount();
//        } catch(Exception e) {
//
//        }
    }

    void initTopSetting() {
        TopSettingDB settingDB = new TopSettingDB(this);
        List<IMSettingStatus> list = settingDB.getList();
        for (int i = 0; i < list.size(); i++) {
            IMSettingStatus setting = list.get(i);

            switch (setting.getBusinesstype()) {
                case IMSettingStatus.businesstypePrivate:
                    Conversation con = Conversation.obtain(ConversationType.PRIVATE, String.valueOf(setting.getBusinessid()), null);
                    con.setTop(true);
                    adapter.addTopItem(con);
                    break;
                case IMSettingStatus.businesstypeDiscussion:
                    Conversation con1 = Conversation.obtain(ConversationType.GROUP, String.valueOf(setting.getBusinessid()), null);
                    con1.setTop(true);
                    adapter.addTopItem(con1);
                    break;
            }

        }


    }

}
