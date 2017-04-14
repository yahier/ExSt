package yahier.exst.act.im.rong;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.widget.ChatSellerWidget;
//import com.stbl.stbl.act.im.ConversationYaFragment;
import com.stbl.stbl.act.im.ConversationMemberMentionedAct;
import com.stbl.stbl.act.im.DiscussionInfoAct;
import com.stbl.stbl.act.im.GetNewMessageReceiver;
import com.stbl.stbl.act.im.GroupInfoAct;
import com.stbl.stbl.act.im.MessageMainAct;
import com.stbl.stbl.act.im.MyConversationFragment;
import com.stbl.stbl.act.im.PrivateInfoAct;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.im.SharedGroups;
import com.stbl.stbl.act.mine.MyDongtaiActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.item.im.IsFriend;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.GoodsDetail;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedOfficeAccount;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.mention.IMentionedInputListener;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import tv.danmaku.ijk.media.player.pragma.DebugLog;

/**
 * Created by yahier.聊天会话页面。
 */
public class ConversationActivity extends ThemeActivity implements OnClickListener, ChatSellerWidget.OnChatSellerSendListener, GetNewMessageReceiver.ReceiverListener {
    TextView imgLeft;
    TextView tvTitle;
    String targetId;
    ImageView theme_top_banner_right;// 默认呢 这个只在群组显示
    String conversationType;
    Context mContext;
    private ChatSellerWidget chatSellerWidget;

    final String conversationPrivate = "/conversation/private";
    final String conversationGroup = "/conversation/group";
    final String conversationCustomerService = "/conversation/customer_service";// 暂时将系统消息当作他
    boolean isFriend = true;//默认当成好友，不显添加好友栏

    String title;

    public final static String typeLocalGroup = "group";
    public final static String typeLocalDiscussion = "discussion";
    public final static String typeLocalPrivate = "private";
    String typeLocal = typeLocalPrivate;
    GoodsDetail mGoodDetail;
    GetNewMessageReceiver receiver;
    final String tag = getClass().getSimpleName();
    View viewAddLin;
    IMAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        checkConnectStatus();
        // LogUtil.logE("ConversationActivity..oncreate");
        EventBus.getDefault().register(this);

        mContext = this;
        initView();
        sendNewMessageCast();
        Intent intent = getIntent();
        mGoodDetail = (GoodsDetail) intent.getSerializableExtra("goods");
        if (mGoodDetail != null) {
            chatSellerWidget.setOnChatSellerSendListener(this);
            chatSellerWidget.setChatSellerData(mGoodDetail);
        }
        targetId = intent.getData().getQueryParameter("targetId");

        LogUtil.logE("targetId:" + targetId);
        //title = intent.getData().getQueryParameter("title");


        conversationType = intent.getData().getPath();
        viewAddLin = findViewById(R.id.linAddFriend);
        theme_top_banner_right.setVisibility(View.VISIBLE);
        switch (conversationType) {
            case conversationGroup:
                theme_top_banner_right.setImageResource(R.drawable.icon_group_management);
                viewAddLin.setVisibility(View.GONE);
                typeLocal = intent.getData().getQueryParameter("typeLocal");
                if (typeLocal == null)
                    return;

                switch (typeLocal) {
                    case typeLocalGroup:
                        break;
                    case typeLocalDiscussion:
                        checkTargetUserInfo(RongDB.typeDiscussion);
                        break;
                }
                break;
            case conversationPrivate:
                //tvTitle.setText(title);
                theme_top_banner_right.setImageResource(R.drawable.icon_private_management);
                checkIsFriend(targetId);
                checkLinAdd();
                checkTargetUserInfo(RongDB.typeUser);
                if (ThemeActivity.isMerchantDoNothing(targetId)) {
                    theme_top_banner_right.setVisibility(View.GONE);
                }
                break;
        }

        // 如果是小秘书或者支付助手。就不显示底部的输入栏
        if (SharedOfficeAccount.isOfficeAccount(targetId)) {
            MyConversationFragment fragmentConversation = (MyConversationFragment) getSupportFragmentManager().findFragmentById(R.id.fragConversation);
            fragmentConversation.setmRongExtensionGone();//内容调用了 his.mRongExtension.setVisibility(View.GONE);
        } else {
            //pass
        }
        //testMessage();
        receiver = new GetNewMessageReceiver();
        imgLeft.postDelayed(new Runnable() {
            @Override
            public void run() {
                receiver.setOnReceiverListener(ConversationActivity.this);
            }
        }, 1000);

        //setGroup();
        setCustomMemberPage();
    }


    void setCustomMemberPage() {
        if (typeLocal.equals(typeLocalPrivate)) return;
        RongMentionManager.getInstance().setMentionedInputListener(new IMentionedInputListener() {
            @Override
            public boolean onMentionedInput(Conversation.ConversationType conversationType, String s) {

                Intent intent = new Intent(mContext, ConversationMemberMentionedAct.class);
                intent.putExtra("groupid", targetId);
                intent.putExtra("type", typeLocal);
                startActivity(intent);
                return true;
            }
        });

    }


    private RongIM.IGroupMemberCallback mMentionMemberCallback;

    void setGroup() {
        RongIM.getInstance().setGroupMembersProvider(new RongIM.IGroupMembersProvider() {
            @Override
            public void getGroupMembers(String groupId, RongIM.IGroupMemberCallback callback) {
                mMentionMemberCallback = callback;
                getGroupMembersForMention();
            }
        });
    }


    private void getGroupMembersForMention() {
        List<UserInfo> userInfos = new ArrayList<>();
        Uri uri = Uri.parse("http://a.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=7c8cdf01830a19d8cb568c0106caaebd/faf2b2119313b07e7ab3d6b80fd7912397dd8c70.jpg");
        for (int i = 0; i < 10; i++) {
            UserInfo user = new UserInfo("1000000" + i, "yahier " + i, uri);
            userInfos.add(user);
        }
        mMentionMemberCallback.onGetGroupMembersResult(userInfos);

    }

    //5月2号新加入的判断。
    void checkConnectStatus() {
        if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null)
            return;
        RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus();
        LogUtil.logE(tag, "status:" + status);//KICKED_OFFLINE_BY_OTHER_CLIENT
        String messageTip = null;
        if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
            messageTip = getString(R.string.im_login_account_other_device);
        }
//        if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE) {
//            messageTip = "网络不可用";
//        }
        if (messageTip == null) return;
        ServerError.logout(this, messageTip);

//        TipsDialog dialog = TipsDialog.popup(mContext, "错误", messageTip, "确定");
//        dialog.setCancelable(false);
//        dialog.setOnTipsListener(new TipsDialog.OnTipsListener() {
//            @Override
//            public void onConfirm() {
//                finish();
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RongDB rongDB = new RongDB(this);
        switch (conversationType) {
            case conversationGroup:
                if (typeLocal == null)
                    return;
                switch (typeLocal) {
                    case typeLocalGroup:
                        account = rongDB.get(RongDB.typeGroup, targetId);
                        if (account != null) {
                            title = account.getNickname();
                        }
                        setTitle(title);
                        break;
                    case typeLocalDiscussion:
                        account = rongDB.get(RongDB.typeDiscussion, targetId);
                        if (account != null) {
                            title = account.getNickname();
                        }
                        setTitle(title);
                        break;
                }
                break;
            case conversationPrivate:
                account = rongDB.get(RongDB.typeUser, targetId);
                if (account != null) {
                    title = account.getNickname();
                }
                setTitle(title);
                break;
        }
    }

    void setTitle(String title) {
        if (account == null || account.getPeopleNum() == 0)
            tvTitle.setText(title);
        else {
            tvTitle.setText(title + "(" + account.getPeopleNum() + ")");
        }
    }


    //查询是否是好友 //0601加入的判断
    private void checkIsFriend(final String targetUserId) {
        final Params params = new Params();
        params.put("objuserid", targetUserId);
        new HttpEntity(mContext).commonPostData(Method.imIsFriend, params, new FinalHttpCallback() {

            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() == BaseItem.successTag) {
                    String obj = JSONHelper.getStringFromObject(item.getResult());
                    IsFriend isFriendItem = JSONHelper.getObject(obj, IsFriend.class);
                    if (isFriendItem.getIsfriend() == IsFriend.isfriendYes) {
                        isFriend = true;
                    } else {
                        isFriend = false;
                        //不是官方账号 也不是好友。就不显示右边的图标
                        if (SharedOfficeAccount.isOfficeAccount(targetUserId) == false)
                            theme_top_banner_right.setVisibility(View.GONE);
                    }
                    checkLinAdd();
                } else {
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                        //ToastUtil.showToast(mContext, item.getErr().getMsg());
                        viewAddLin.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    void checkLinAdd() {
        //如果已经是官方 或者好友 或者 是 商家
        if (SharedOfficeAccount.isOfficeAccount(targetId) || isFriend || ThemeActivity.isMerchantDoNothing(targetId)) {
            viewAddLin.setVisibility(View.GONE);
        } else {
            viewAddLin.setVisibility(View.VISIBLE);
            View tvAdd = viewAddLin.findViewById(R.id.tvAdd);
            final long userId = Long.valueOf(targetId);
            tvAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AddFriendUtil(mContext, null).addFriendDirect(userId, title);
                    view.setEnabled(false);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, 1000);

                }
            });
        }

    }

    /**
     * 测试获取消息
     */
    void testMessage() {

        LogUtil.logE("testMessage");
        List<Message> list = RongIM.getInstance().getRongIMClient().getHistoryMessages(Conversation.ConversationType.PRIVATE, targetId, 0, 10);
        if (list == null) {
            LogUtil.logE("1 message:还没有消息");
        } else {
            LogUtil.logE("1 message size:" + list.size());
        }
        //这个付费接口还真是完完全全没有反应啊。第一次取传0,3条数据
        RongIM.getInstance().getRongIMClient().getRemoteHistoryMessages(Conversation.ConversationType.PRIVATE, targetId, 0, 3, resultback);
        //下面这个是不要钱哈.但获取的消息是页面已经可以显示出来的，没啥用
        //RongIM.getInstance().getRongIMClient().getLatestMessages(Conversation.ConversationType.PRIVATE, targetId, 4, resultback);

    }

    //历史消息的回调
    RongIMClient.ResultCallback resultback = new RongIMClient.ResultCallback<List<Message>>() {

        @Override
        public void onSuccess(List<Message> messages) {

            if (messages == null) {
                LogUtil.logE("message:还没有消息");
                return;
            }
            LogUtil.logE("message:size:" + messages.size());
            for (int i = 0; i < messages.size(); i++) {
                MessageContent content = messages.get(i).getContent();
                if (content instanceof ImageMessage) {

                }
            }
        }

        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {
            LogUtil.logE("message: onError");
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(IMEventType type) {
        switch (type.getType()) {
            case IMEventType.typeQuitDiscussion:
                finish();
                break;
//            case IMEventType.typeUpdateDiscussionName:
//                setTitle(String.valueOf(type.getValue()));
//                break;
            case IMEventType.typeUpdateGroupInfo:
                setTitle(String.valueOf(type.getValue()));
                break;
            case IMEventType.typeIMOtherDevice:
                //LogUtil.logE("关闭typeIMOtherDevice");
                finish();
                break;
            case IMEventType.typeLoginOtherDevice:
                LogUtil.logE("onEvent", "typeLoginOtherDevice");
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkConnectStatus();
                    }
                }, 2000);
                break;
        }
    }


    public void onEvent(EventUpdateAlias event) {
        if (conversationType.endsWith(conversationPrivate)) {
            tvTitle.setText(event.getAlias());
        }

    }

    /**
     * 检查是否有userInfo的信息，没有则请求网络
     */
    void checkTargetUserInfo(int type) {
        final RongDB userDB = new RongDB(mContext);
        Params params = new Params();
        switch (type) {
            case RongDB.typeUser:
                IMAccount account = userDB.get(RongDB.typeUser, targetId);
                if (account != null)
                    return;
                params.put("touserids", targetId);
                new HttpEntity(mContext).commonPostData(Method.imGetSimpleUserInfo, params, new FinalHttpCallback() {

                    @Override
                    public void parse(String methodName, String result) {
                        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                        if (item.getIssuccess() == BaseItem.successTag) {
                            String obj = JSONHelper.getStringFromObject(item.getResult());
                            IMAccount userItem = null;
                            List<IMAccount> users = JSONHelper.getList(obj, IMAccount.class);
                            if (users != null && users.size() > 0) {
                                userItem = users.get(0);
                            } else {
                                return;
                            }
                            userDB.insert(new IMAccount(RongDB.typeUser, targetId, userItem.getNickname(), userItem.getImgurl(), userItem.getCertification(), userItem.getAlias()));
                        }
                    }
                });
                break;
            case RongDB.typeDiscussion:
                IMAccount account2 = userDB.get(RongDB.typeDiscussion, targetId);
                if (account2 != null)
                    return;
                params.put("groupid", targetId);
                new HttpEntity(this).commonPostData(Method.imShowBaseDiscussion, params, new FinalHttpCallback() {

                    @Override
                    public void parse(String methodName, String result) {
                        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                        if (item.getIssuccess() == BaseItem.successTag) {
                            String obj = JSONHelper.getStringFromObject(item.getResult());
                            DiscussionTeam discussion = JSONHelper.getObject(obj, DiscussionTeam.class);
                            userDB.insert(new IMAccount(RongDB.typeDiscussion, targetId, discussion.getGroupname(), "", UserItem.certificationNo, ""));
                        }

                        //

                    }
                });
                break;
        }

    }

    /**
     * 主要是为了刷新未读数字
     * getRongConversationList();
     * getUnReadCount(typeGroupMaster, group1);
     * getUnReadCount(typeGroupMine, group2);
     */
    void sendNewMessageCast() {
        Intent intent = new Intent(MessageMainAct.actionGetOrSentNewMessage_RefreshConversationList);
        sendBroadcast(intent);
    }

    void initView() {
        imgLeft = (TextView) findViewById(R.id.theme_top_banner_left);
        imgLeft.setOnClickListener(this);
        theme_top_banner_right = (ImageView) findViewById(R.id.theme_top_banner_right);

        theme_top_banner_right.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.theme_top_banner_middle);
        chatSellerWidget = (ChatSellerWidget) findViewById(R.id.chat_seller_view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.theme_top_banner_left:
                onBackEvent();
                break;
            case R.id.theme_top_banner_right:
                Intent intent;
                switch (typeLocal) {
                    case typeLocalDiscussion:
                        intent = new Intent(this, DiscussionInfoAct.class);
                        intent.putExtra("discussionId", targetId);
                        startActivity(intent);
                        break;
                    case typeLocalGroup:
                        intent = new Intent(this, GroupInfoAct.class);
                        intent.putExtra("groupid", targetId);
                        startActivity(intent);
                        break;
                    case typeLocalPrivate:
                        intent = new Intent(this, PrivateInfoAct.class);
                        intent.putExtra("targetId", targetId);
                        startActivity(intent);
                        break;
                }
                break;
        }
    }

    public void chatSellerSend() {
        Goods good = new Goods();
        good.setGoodsid(mGoodDetail.getGoodsid());
        good.setImgurl(mGoodDetail.getFimgurl());
        //LogUtil.logE("img::::" + good.getImgurl());
        good.setGoodsname(mGoodDetail.getGoodsname());
        good.setMinprice(mGoodDetail.getMinprice());
        good.setSalecount(mGoodDetail.getAccount().getSalecount());
        //
        GoodsMessage message = GoodsMessage.obtain(good);
        RongIM.getInstance()
                .getRongIMClient()
                .sendMessage(Conversation.ConversationType.PRIVATE,
                        targetId, message, null, null,
                        new RongIMClient.SendMessageCallback() {
                            @Override
                            public void onError(Integer integer,
                                                RongIMClient.ErrorCode errorCode) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                //chatSellerWidget.setVisibility(View.GONE);
                            }
                        });

    }

    //使消息不计数
    void clearReadStatus() {
        Conversation.ConversationType conversationtype = null;
        switch (conversationType) {
            case conversationGroup:
                conversationtype = Conversation.ConversationType.GROUP;
                break;
            case conversationPrivate:
                conversationtype = Conversation.ConversationType.PRIVATE;
                break;
        }

        if (conversationtype == null) return;
        RongIMClient.getInstance().clearMessagesUnreadStatus(conversationtype, targetId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                //LogUtil.logE(tag, "clear onSuccess");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                //LogUtil.logE(tag, "clear onError");
            }
        });
    }

    public void newMessage() {
        //LogUtil.logE(tag, "newMessage");
        clearReadStatus();
    }

    public void newApply() {

    }


    @Override
    public void onBackPressed() {
        onBackEvent();
    }


    void onBackEvent() {
        Intent intent = getIntent();
        intent.setComponent(new ComponentName(this, TabHome.class));
        intent.putExtra("tabIndex", 2);
        intent.setAction(ACTION.GO_TO_MESSAGE_MAIN);
        startActivity(intent);
        super.onBackPressed();
    }

    /**
     * 本act的栈里  是否还有其它act
     * @return
     */
    private boolean hasActivityInTask() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int numActivities = am.getRunningTasks(1).get(0).numActivities;
        if (numActivities > 1)
            return true;
        return false;

    }


}