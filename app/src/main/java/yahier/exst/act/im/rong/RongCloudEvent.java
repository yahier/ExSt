package yahier.exst.act.im.rong;

//import io.rong.app.activity.DePersonalDetailActivity;
//import io.rong.app.activity.MainActivity;
//import io.rong.app.activity.NewFriendListActivity;
//import io.rong.app.activity.PhotoActivity;
//import io.rong.app.activity.SOSOLocationActivity;
//import io.rong.app.database.DBManager;
//import io.rong.app.database.UserInfos;
//import io.rong.app.database.UserInfosDao;
//import io.rong.app.message.DeAgreedFriendRequestMessage;
//import io.rong.app.model.User;
//import io.rong.app.photo.PhotoCollectionsProvider;
//import io.rong.app.provider.ContactsProvider;
//import io.rong.app.ui.WinToast;

import io.rong.eventbus.EventBus;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

//import com.sea_monster.exception.BaseException;
//import com.sea_monster.network.AbstractHttpRequest;
//import com.sea_monster.network.ApiCallback;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.im.MessageMainAct;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.im.SharedGroups;
import com.stbl.stbl.adapter.GiftAdapter;
import com.stbl.stbl.common.ImagePagerAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.ApplyAgreeUser;
import com.stbl.stbl.item.im.ApplyGotItem;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedDevice;
import com.stbl.stbl.util.SharedOfficeAccount;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.util.rong.PrivateModule;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.widget.jpush.JPushManager;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.SystemUtils;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
//import io.rong.imkit.widget.provider.CameraInputProvider;
//import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
//import io.rong.notification.PushNotificationMessage;

/**
 * Created by zhjchen on 1/29/15.
 */

/**
 * <p/>
 * 该类包含的监听事件有： 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。 5、群组信息提供者：GetGroupInfoProvider。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。 8、地理位置提供者：LocationProvider。
 * //9、 自定义 push 通知： OnReceivePushMessageListener。
 * //10、会话列表界面操作的监听器：ConversationListBehaviorListener。
 */

//RongIMClient.OnReceivePushMessageListener,  ApiCallback
public final class RongCloudEvent implements RongIMClient.OnReceiveMessageListener, RongIM.OnSendMessageListener, RongIM.UserInfoProvider, RongIM.GroupInfoProvider,
        RongIM.ConversationBehaviorListener, RongIMClient.ConnectionStatusListener, RongIM.LocationProvider, RongIM.ConversationListBehaviorListener,
        Handler.Callback, FinalHttpCallback {

    private static final String TAG = RongCloudEvent.class.getSimpleName();

    private static RongCloudEvent mRongCloudInstance;

    private Context mContextApp;
    // private AbstractHttpRequest<User> getUserInfoByUserIdHttpRequest;
    // private AbstractHttpRequest<User> getFriendByUserIdHttpRequest;
    private Handler mHandler;

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {
        LogUtil.logE(TAG, "init");
        if (mRongCloudInstance == null) {
            synchronized (RongCloudEvent.class) {
                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContextApp = context;
        initDefaultListener();
        mHandler = new Handler(this);
        mLargeIcon = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon);
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {
        LogUtil.logE("cloudEvent:initDefaultListener");
        RongIM.setUserInfoProvider(this, true);// 设置用户信息提供者。
        RongIM.setGroupInfoProvider(this, true);// 设置群组信息提供者。
        RongIM.setConversationBehaviorListener(this);// 设置会话界面操作的监听器。
        //RongIM.setLocationProvider(this);// 设置地理位置提供者,不用位置的同学可以注掉此行代码
        RongIM.setConversationListBehaviorListener(this);
        //RongIM.getInstance().setMessageAttachedUserInfo(true);
        //RongIM.setOnReceivePushMessageListener(this);//自定义 push 通知。//新解开
        //以下为新加
        registerMessageAndProvider();
    }

    /*
     * 连接成功注册。 <p/> 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener() {
        LogUtil.logE("cloudEvent:setOtherListener");
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);// 设置消息接收监听器。
        //RongIM.getInstance().getRongIMClient().setOnReceivePushMessageListener(this);
        //RongIM.setOnReceivePushMessageListener(this);//自定义 push 通知。//新解开.似乎没有效果
        RongIM.getInstance().setSendMessageListener(this);// 设置发出消息接收监听器.如果有效果也应该是上面这句
        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);// 设置连接状态监听器。
    }


    private void registerMessageAndProvider() {
        //注册通知类消息
        RongIM.registerMessageType(MyNotiMessage.class);
        RongIM.registerMessageTemplate(new MyNotiMessageProvider());
        // 注册红包
        RongIM.registerMessageType(RedPackectMessage.class);
        RongIM.registerMessageTemplate(new RedPacketMessageProvider());
        // 注册撒豆
        RongIM.registerMessageType(CastBeanMessage.class);
        RongIM.registerMessageTemplate(new CastBeanMessageProvider());
        // 注册名片
        RongIM.registerMessageType(BusinessCardMessage.class);
        RongIM.registerMessageTemplate(new BusinessCardMessageProvider());
        // 注册讨论组的邀请 。为什么这类信息没有在通知栏显示呢。同时测试文字信息就可以。是否需要服务器在融云注册
        RongIM.registerMessageType(DiscussionInviteMessage.class);
        RongIM.registerMessageTemplate(new DiscussionMessageProvider());
        // 注册订单类型消息
        RongIM.registerMessageType(OrderMessage.class);
        RongIM.registerMessageTemplate(new OrderMessageProvider());
        // 注册动态消息
        RongIM.registerMessageType(StatusesMessage.class);
        RongIM.registerMessageTemplate(new StatusesMessageProvider());
        // 注册商品消息
        RongIM.registerMessageType(GoodsMessage.class);
        RongIM.registerMessageTemplate(new GoodsMessageProvider());
        // 注册广告找人代付消息
        RongIM.registerMessageType(AdOtherPayMessage.class);
        RongIM.registerMessageTemplate(new AdOtherPayMessageProvider());


        setPrivateInputProvider();

        //根据服务器配置决定是否开放红包 撒豆
//        if (SharedGroups.getHongbaoSwitch(MyApplication.getContext()) == SharedGroups.showhongbaoOn) {
//            InputProvider.ExtendProvider[] providerPrivate2 = {new PhotoCollectionsProvider(RongContext.getInstance()),// 图片
//                    //new LocationInputProvider(RongContext.getInstance()),// 地理位置
//                    //new VoIPInputProvider(RongContext.getInstance()),// 语音通话
//                    // new ContactsProvider(RongContext.getInstance()),// 通讯录
//                    new CameraInputProvider(RongContext.getInstance()),// 相机
//                    new BusinessCardProvider(RongContext.getInstance()),// 名片
//                    new CollectProvider(RongContext.getInstance()),// 收藏
//                    new RedPacketProvider(RongContext.getInstance()),// 红包
//
//            };
//
//            InputProvider.ExtendProvider[] providerGroup2 = {new PhotoCollectionsProvider(RongContext.getInstance()),// 图片
//                    new CameraInputProvider(RongContext.getInstance()),// 相机
//                    new CastBeanProvider(RongContext.getInstance()),// 撒豆
//                    new BusinessCardProvider(RongContext.getInstance()),// 名片
//                    new CollectProvider(RongContext.getInstance()),// 收藏
//            };
//
//            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, providerPrivate2);
//            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, providerGroup2);
//
//        } else {
//            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, providerPrivate);
//            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, providerGroup);
//        }
    }

    /**
     * 自定义 push 通知。true 自己来弹通知栏提示，false 融云 SDK 来弹通知栏提示。。但为什么都没有调用到此方法中呢
     *
     * @param
     * @return
     */


    //设置私有聊天输入扩展栏
    private void setPrivateInputProvider() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        Log.e("inputProvider", "size:" + moduleList.size());
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new PrivateModule());
            }
        }
    }

    //  @Override
    //  public boolean onReceivePushMessage(PushNotificationMessage msg) {
    //LogUtil.logE(TAG, "onReceived-onPushMessageArrive:" + msg.getContent());
//		PushNotificationManager.getInstance().onReceivePush(msg);
//		Intent intent = new Intent();
//		Uri uri;
//		intent.setAction(Intent.ACTION_VIEW);
//		Conversation.ConversationType conversationType = msg.getConversationType();
//		uri = Uri.parse("rong://" + RongContext.getInstance().getPackageName()).buildUpon().appendPath("conversationlist").build();
//		intent.setData(uri);
//		Log.e(TAG, "onPushMessageArrive-url:" + uri.toString());
//		Notification notification = null;
//		PendingIntent pendingIntent = PendingIntent.getActivity(RongContext.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		if (android.os.Build.VERSION.SDK_INT < 11) {
//			notification = new Notification(RongContext.getInstance().getApplicationInfo().icon, "自定义 notification", System.currentTimeMillis());
//			notification.setLatestEventInfo(RongContext.getInstance(), "自定义 title", "这是 Content:" + msg.getObjectName(), pendingIntent);
//			notification.flags = Notification.FLAG_AUTO_CANCEL;
//			notification.defaults = Notification.DEFAULT_SOUND;
//		} else {
//			notification = new Notification.Builder(RongContext.getInstance()).setLargeIcon(getAppIcon()).setSmallIcon(R.drawable.icon).setTicker("自定义 notification").setContentTitle("自定义 title")
//					.setContentText("这是 Content:" + msg.getObjectName()).setContentIntent(pendingIntent).setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).build();
//		}
//
//		NotificationManager nm = (NotificationManager) RongContext.getInstance().getSystemService(RongContext.getInstance().NOTIFICATION_SERVICE);
//		nm.notify(0, notification);
    //      return false;//之前是true
    //  }


    private Bitmap getAppIcon() {
        BitmapDrawable bitmapDrawable;
        Bitmap appIcon;
        bitmapDrawable = (BitmapDrawable) RongContext.getInstance().getApplicationInfo().loadIcon(RongContext.getInstance().getPackageManager());
        appIcon = bitmapDrawable.getBitmap();
        return appIcon;
    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    /**
     * 发送新消息广播
     */
    void sendNewMessageCast() {
        Intent intent = new Intent(MessageMainAct.actionGetOrSentNewMessage_RefreshConversationList);
        mContextApp.sendBroadcast(intent);
        //在这里发事件会提示 Could not dispatch event: class com.stbl.stbl.item.im.IMEventType to subscribing class class com.stbl.stbl.act.im.MessageMainAct。
        // EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateContactList));
    }


    /**
     * 收到新广播去刷新数字
     */
    void sendNewApplyReceiver() {
        Intent intent = new Intent(MessageMainAct.actionGetNewApply);
        mContextApp.sendBroadcast(intent);
    }

    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     *
     * @param message 接收到的消息的实体信息。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(Message message, int left) {
        LogUtil.logE(TAG, "onReceived name:" + message.getObjectName());

        String content = mContextApp.getString(R.string.im_you_have_new_msg);
        MessageContent messageContent = message.getContent();
        //Log.e(TAG, "onReceived name2:"+messageContent.getClass().getSimpleName());
        if (messageContent instanceof TextMessage) {// 文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            content = textMessage.getContent();
            LogUtil.logE(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {// 图片消息
            content = mContextApp.getString(R.string.im_you_have_photo_msg);
            ImageMessage imageMessage = (ImageMessage) messageContent;
            LogUtil.logE(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {// 语音消息
            content = mContextApp.getString(R.string.im_you_have_voice_msg);
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            LogUtil.logE(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {// 图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            LogUtil.logE(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
        } else if (messageContent instanceof ContactNotificationMessage) {// 好友添加消息
            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
            LogUtil.logE(TAG, "onReceived-ContactNotificationMessage:senderUserId;" + message.getSenderUserId());
            LogUtil.logE(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());


            content = mContextApp.getString(R.string.im_you_have_friend_apply_msg);
            sendNewApplyReceiver();
            //return true;
        } else if (messageContent instanceof OrderMessage) {//订单消息
            Intent intent = new Intent(ACTION.UPDATE_ORDER_COUNT);
            LocalBroadcastHelper.getInstance().send(intent);
            content = mContextApp.getString(R.string.im_you_have_order_msg);
        } else if (messageContent instanceof GoodsMessage) {//Good
            Intent intent = new Intent(ACTION.UPDATE_ORDER_COUNT);
            content = mContextApp.getString(R.string.im_you_have_goods_msg);
            LocalBroadcastHelper.getInstance().send(intent);
        } else if (messageContent instanceof DemoCommandNotificationMessage) {//同意加好友的消息
            String name = ((DemoCommandNotificationMessage) messageContent).getName();
            String data = ((DemoCommandNotificationMessage) messageContent).getData();
            //Log.e(TAG, name+" name:name:"+data);
            if (name == null) return true;
            if (name.equals("add")) {
                ApplyAgreeUser item = JSONHelper.getObject(data, ApplyAgreeUser.class);
                String tipContent = mContextApp.getString(R.string.im_you_have_benn_friends_chat_now);
                //新加。对方同意我的好友申请后，我这里也新加一个聊天列表item
                if (item != null) {
                    tipContent = String.format(mContextApp.getString(R.string.you_have_benn_friends_with_someone_chat_now), item.getNickname());
//                    FriendsDB db = new FriendsDB(mContextApp);
                    //FriendsDB db = FriendsDB.getInstance(mContextApp);
                    //db.insert(item.getUserid(), item.getNickname(), item.getImgurl());
                }
                RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.PRIVATE, item.getUserid() + "", SharedToken.getUserId(), InformationNotificationMessage.obtain(tipContent), null);

            } else if (name.equals("AdRebate")) {
                /**记录是否有新返利信息*/
                AdHelper.setShowRebate(true);
            } else if (name.equals("AdBusiness")) {
                /**记录是否有新商务合作信息*/
                AdHelper.setShowCooperater(true);
            } else if (name.equals("UserContact")) {
                /**有了新徒弟*/
                AdHelper.setShowNewStudent(true);
                LocalBroadcastHelper.getInstance().send(new Intent(ACTION.SHOW_NEW_STUDENT_REMIND));
            }

        } else if (messageContent instanceof MyNotiMessage) {//Good
            MyNotiMessage notiMessage = (MyNotiMessage) messageContent;
            LogUtil.logE("onReceived noti 1", notiMessage.getMessage());
            LogUtil.logE("onReceived noti 2", notiMessage.getExtra());
            //notiMessage.getMessage()
        } else {
            Log.e(TAG, "onReceived-其他消息，自己来判断处理");
        }

        //新加。判断是否通知
        sendNewMessageCast();
        boolean isNotNotify = false;
        MessageSettingDB db = new MessageSettingDB(mContextApp);
        String typeLocal = null;
        if (message.getConversationType() == Conversation.ConversationType.GROUP) {
            if (SharedGroups.isGroupMineOrMaster(message.getTargetId(), mContextApp)) {
                //是群组
                isNotNotify = db.isNotNotify(MessageSettingDB.typeGroup, message.getTargetId());
                typeLocal = ConversationActivity.typeLocalGroup;
            } else {
                //是讨论组
                isNotNotify = db.isNotNotify(MessageSettingDB.typeDiscussion, message.getTargetId());
                typeLocal = ConversationActivity.typeLocalDiscussion;
            }
        } else if (message.getConversationType() == Conversation.ConversationType.PRIVATE) {
            isNotNotify = db.isNotNotify(MessageSettingDB.typePrivate, message.getTargetId());

        }

        //新加 对应@功能.如果有人@当前用户,则发送通知。
        if (content.contains(mContextApp.getString(R.string.group_notice_prefix)) || content.contains("@" + SharedUser.getUserNick())) {
            isNotNotify = false;
        }

        //LogUtil.logE(TAG, "isNotNotify:" + isNotNotify);
        if (!isNotNotify) {
            boolean isBack = SystemUtils.isInBackground(mContextApp);
            //如果不在前台，就发在通知栏显示
            if (isBack) {
                sendNotificationBar(message.getConversationType(), typeLocal, message.getTargetId(), content);
            }
        }

        //message.getConversationType()//获取消息类别。可以根据此类型控制 仅私聊有通知。
        Log.e(TAG, "onReceived结束");
        return true;//默认是false

    }

    private Bitmap mLargeIcon;
    final int idNotification = 1;

    void sendNotificationBar(Conversation.ConversationType type, String typeLocal, String targetId, String... contents) {
        String content = mContextApp.getString(R.string.im_you_have_new_msg);
        if (contents.length > 0) {
            content = contents[0];
        }
        NotificationManager nm = (NotificationManager) mContextApp.getSystemService(Context.NOTIFICATION_SERVICE);

        //直接跳转到会话页面
        Intent clickIntent = new Intent(mContextApp, ConversationActivity.class); //点击通知
        Uri uri = Uri.parse("rong://" + mContextApp.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(type.getName().toLowerCase())
                .appendQueryParameter("targetId", targetId).appendQueryParameter("typeLocal", typeLocal).build();
        clickIntent.setData(uri);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContextApp, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //2.实例化一个通知，指定图标、概要、时间
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContextApp);
        builder.setSmallIcon(R.drawable.icon)
                .setLargeIcon(mLargeIcon)
                .setContentTitle(mContextApp.getString(R.string.im_stbl))
                .setTicker(content)
                .setContentText(content)
                .setContentIntent(pendingIntent);
        Notification n = builder.build();
        n.flags = Notification.FLAG_AUTO_CANCEL;//设置点击后自动消失
        //指定声音
        n.defaults = Notification.DEFAULT_SOUND;
        nm.notify(idNotification, n);
    }


    @Override
    public Message onSend(Message message) {
        return message;
    }

    /**
     * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message 消息。
     */
    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        LogUtil.logE(TAG + " onSent sentMessageErrorCode:" + sentMessageErrorCode);
        sendNewMessageCast();
        if (message.getSentStatus() == Message.SentStatus.FAILED) {
            MessageContent messageContent = message.getContent();
            if (messageContent instanceof InformationNotificationMessage) {// 文本消息
                return false;
            }

            if (messageContent instanceof DemoCommandNotificationMessage) {// 命令消息。不做任何提示
                return true;
            }
            CommonTask.uploadIMSentError(sentMessageErrorCode, message);
            if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {// 不在聊天室

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {// 不在讨论组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {// 不在群组
                if (SharedGroups.isGroupMineOrMaster(message.getTargetId(), mContextApp)) {
                    tipSender(message, "你不在该群");
                } else {
                    tipSender(message, "你不在该讨论组");
                }
                return true;
            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {// 你在他的黑名单中
                tipSender(message, mContextApp.getString(R.string.im_not_friend_tips));

                return true;
            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.FORBIDDEN_IN_GROUP) {
                //系统已经代发了。
                //testInfoMsg(message, "发送失败，你已经被群主禁言");
            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.UNKNOWN) {
                if (SharedDevice.getNet(mContextApp).equals("")) {
                    tipSender(message, mContextApp.getString(R.string.im_network_abnormal_tips));
                } else {
                    tipSender(message, mContextApp.getString(R.string.im_unknown_err_tips));
                }

            }

        }
        return false;//false
    }


    /**
     * 加入本地信息，只发送者可见。
     *
     * @param message
     * @param content
     */
    void tipSender(Message message, String content) {
        RongIM.getInstance().getRongIMClient().insertMessage(message.getConversationType(), message.getTargetId(), message.getSenderUserId(), InformationNotificationMessage.obtain(content), null);
    }

    /**
     * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
     *
     * @param userId
     * 用户 Id。
     * @return 用户信息，（注：由开发者提供用户信息）。
     */

    UserInfo user = null;

    @Override
    public UserInfo getUserInfo(final String userId) {
        // demo 代码 开发者需替换成自己的代码。
        final RongDB userDB = new RongDB(mContextApp);
        IMAccount account = userDB.get(RongDB.typeUser, userId);
        if (account != null) {
            Uri uri = null;
            if (account.getImgurl() != null) {
                uri = Uri.parse(account.getImgurl());
            }
            String nameValue = account.getAlias();
            if (nameValue == null || nameValue.equals("")) {
                nameValue = account.getNickname();
            }
            UserInfo user = new UserInfo(account.getUserid(), nameValue, uri);
            return user;
        }

        final Params params = new Params();
        final String[] flag = {"false"};
        params.put("touserids", userId);
        new HttpEntity(mContextApp).commonPostData(Method.imGetSimpleUserInfo, params, new FinalHttpCallback() {

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
                    Uri uri = Uri.parse(userItem.getImgurl());
                    String nameValue = userItem.getAlias();
                    if (nameValue == null || nameValue.equals("")) {
                        nameValue = userItem.getNickname();
                    }

                    user = new UserInfo(userId, nameValue, uri);
                    IMAccount account = new IMAccount(userId, userItem.getNickname(), userItem.getImgurl(), userItem.getCertification());
                    account.setType(RongDB.typeUser);
                    account.setAlias(userItem.getAlias());
                    userDB.insert(account);
                    //userDB.insert(RongDB.typeUser, userId, userItem.getNickname(), userItem.getImgurl(), userItem.getCertification(), userItem.getAlias());
                    //立即刷新
                    RongIM.getInstance().refreshUserInfoCache(user);
                }

            }
        });


        return user;
    }


    /**
     * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
     *
     * @param groupId 群组 Id.
     * @return 群组信息，（注：由开发者提供群组信息）。
     */
    @Override
    public Group getGroupInfo(String groupId) {
        Log.e(TAG, "----getGroupInfo:" + groupId);
        /**
         * demo 代码 开发者需替换成自己的代码。
         */
        // if (DemoContext.getInstance().getGroupMap() == null)
        // return null;
        // return DemoContext.getInstance().getGroupMap().get(groupId);
        Group group = new Group("4563987087324602", "groupname", null);
        return group;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user) {
        Log.e(TAG, "----onUserPortraitClick userId:" + user.getUserId() + "   会话类型:" + conversationType.getName());
        if (user != null) {
            if (conversationType.equals(Conversation.ConversationType.GROUP)) {
                // 如果是自己，当然就不用显示window
                if (SharedToken.getUserId(context).equals(user.getUserId())) {
                    return false;
                }
                showGroupUserPortraitClickWindow(context, user);
            } else if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
                //不能点击系统账号进入部落
                if (SharedOfficeAccount.isOfficeAccount(user.getUserId())) {
                    return false;
                }
                //以下两个context,都是之前用的mContext
                Intent intent = new Intent(context, TribeMainAct.class);
                String userIdStr = user.getUserId();
                try {
                    long userId = Long.valueOf(userIdStr);
                    intent.putExtra("userId", userId);
                    context.startActivity(intent);
                } catch (Exception e) {
                    LogUtil.logE(TAG, "e:" + e.getMessage() + ":" + e.getLocalizedMessage());
                }


            }

        }

        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        Log.e(TAG, "----onUserPortraitLongClick");

        return false;
    }

    Context mContextUi;

    void showGroupUserPortraitClickWindow(final Context mContext, final UserInfo user) {
        mContextUi = mContext;
        final String userId = user.getUserId();
        final Dialog pop = new Dialog(mContext, R.style.Common_Dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.im_group_window, null);
        OnClickListener onClick = new OnClickListener() {

            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                pop.dismiss();
                switch (view.getId()) {
                    case R.id.item_group_follow:
                        doFollow(userId);
                        break;
                    case R.id.item_group_add:
                        new AddFriendUtil(mContext, null).addFriendDirect(Long.valueOf(user.getUserId()), user.getName());
                        break;
                    case R.id.item_group_supercard://跳部落
                        enterTribe(mContext, userId);
                        break;
                    case R.id.item_group_gift://出错
                        initGiftWindow(mContext, userId);
                        getGiftList();
                        break;
                    case R.id.item_group_report:
                        report(mContext, userId);
                        break;
                    case R.id.item_group_chat:
                        RongIM.getInstance().startPrivateChat(mContext, userId, user.getName());
                        break;
                    //case R.id.item_group_shut:
                    //  shut("4530997889835099", userId);// 儒家群 4530997889835099
                    //   break;
                    case R.id.item_popupwindows_cancel:
                        pop.dismiss();
                        break;

                }
            }
        };
        view.findViewById(R.id.item_group_follow).setOnClickListener(onClick);
        view.findViewById(R.id.item_group_add).setOnClickListener(onClick);
        view.findViewById(R.id.item_group_supercard).setOnClickListener(onClick);
        view.findViewById(R.id.item_group_gift).setOnClickListener(onClick);
        view.findViewById(R.id.item_group_report).setOnClickListener(onClick);
        view.findViewById(R.id.item_group_chat).setOnClickListener(onClick);
        view.findViewById(R.id.item_group_shut).setOnClickListener(onClick);
        view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(onClick);
        pop.setContentView(view);
        Window window = pop.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        pop.show();
    }

    private void doFollow(String userId) {
        try {
            Params params = new Params();
            params.put("target_userid", userId);
            new HttpEntity(mContextUi).commonPostData(Method.userFollow, params, this);
        } catch (Exception e) {

        }
    }


    private void enterTribe(Context mContext, String userId) {
        LogUtil.logE(TAG, "userId:" + userId);
        Intent intent = new Intent(mContext, TribeMainAct.class);
        intent.putExtra("userId", Long.valueOf(userId));
        mContext.startActivity(intent);
    }

    // 送礼物
    private void getGiftList() {
        new HttpEntity(mContextApp).commonPostData(Method.userGetGiftList, null, this);
    }

    Dialog dialogGift;
    GridView gridGift;

    // 初始化礼物window
    void initGiftWindow(final Context mContext, final String userId) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.award_window, null);
        gridGift = (GridView) view.findViewById(R.id.grid);
        dialogGift = new Dialog(mContext, R.style.dialog);
        dialogGift.setContentView(view);
        gridGift.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dialogGift.dismiss();
                GiftAdapter adapter = (GiftAdapter) gridGift.getAdapter();
                Gift gift = adapter.getItem(arg2);
                giveGift(mContext, userId, gift.getGiftid(), gift.getValue());
            }
        });
    }


    // 填充礼物数据.从parse方法调用
    void showDaShangDialog(final List<Gift> listGift) {
        if (dialogGift.isShowing()) {
            return;
        }
        ((Activity) mContextUi).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GiftAdapter adapter = new GiftAdapter(mContextUi, listGift);
                gridGift.setAdapter(adapter);
                dialogGift.show();
            }
        });
    }


    // 送出礼物
    void giveGift(final Context mContext, String userId, int giftid, int value) {
        final Params params = new Params();
        params.put("businessid", userId);//
        params.put("giftid", giftid);

        Payment.getPassword(mContext, value, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                params.put("paypwd", pwd);
                WaitingDialog.show(mContext, R.string.waiting, false);
                new HttpEntity(mContext).commonPostData(Method.imSendGift, params, RongCloudEvent.this);
            }
        });


    }

    private void report(Context mContext, String userId) {
        Intent intent = new Intent(mContext, ReportStatusesOrUserAct.class);
        intent.putExtra("userId", userId);
        intent.putExtra("type", ReportStatusesOrUserAct.typeUser);
        mContext.startActivity(intent);

    }

    // 禁言
    private void shut(String groupid, String userId) {
        Params params = new Params();
        params.put("groupid", groupid);
        params.put("memberids", UserItem.addRelationTypeFriend);
        new HttpEntity(mContextApp).commonPostData(Method.imShutup, params, this);
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        Log.e(TAG, "----onMessageClick");
        /**
         * demo 代码 开发者需替换成自己的代码。
         */
        if (message.getContent() instanceof LocationMessage) {
            // Intent intent = new Intent(context, SOSOLocationActivity.class);
            // intent.putExtra("location", message.getContent());
            // context.startActivity(intent);
        } else if (message.getContent() instanceof RichContentMessage) {
            // RichContentMessage mRichContentMessage = (RichContentMessage)
            // message.getContent();
            // Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());

        } else if (message.getContent() instanceof ImageMessage) {


            //注释 by yahier date:0223
            //showMoreImgMessage(context, message.getTargetId(), message);

        }

        Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }


    /**
     * 实现图片连续预览。一次性获取所有
     *
     * @param context
     * @param targetId
     * @param clickedMesage
     */
    void showMoreImgMessage(final Context context, String targetId, final Message clickedMesage) {
        final int messageId = clickedMesage.getMessageId();
        Conversation.ConversationType type = clickedMesage.getConversationType();
        //-1即不传入messageId.查询全部
        RongIM.getInstance().getRongIMClient().getHistoryMessages(type, targetId, "RC:ImgMsg", -1, Integer.MAX_VALUE, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages == null) {
                    messages = new ArrayList<Message>();
                }

                // LogUtil.logE("showMoreImgMessage message:size:" + messages.size());//从新到旧
                ArrayList<String> list = new ArrayList<String>();
                int index = 0;
                int size = messages.size();
                int failedSize = 0;
                for (int i = 0; i < size; i++) {
                    MessageContent content = messages.get(i).getContent();
                    if (content instanceof ImageMessage) {
                        ImageMessage imageMessage = (ImageMessage) content;
                        if (imageMessage.getRemoteUri() != null) {
                            list.add(0, imageMessage.getRemoteUri().toString());//toString遭遇到nullPointer
                        } else {
                            failedSize++;
                        }
                        if (messageId == messages.get(i).getMessageId()) {
                            index = size - i - 1 - failedSize;
                            LogUtil.logE("message index", index);
                        }
                    }
                }

                //重新取出合理的数量。现在左右最大各取10张.重新定index的值
                ArrayList<String> list2 = new ArrayList<String>();
                final int sideMax = 10;//以点击的图为中心，左右两边再最多各取10张。
                int index2 = index >= sideMax ? sideMax : index;
                for (int i = index - sideMax; i <= index + sideMax && i < size; i++) {
                    if (i < 0) continue;
                    list2.add(list.get(i));
                }

                //LogUtil.logE(TAG, "list2 size:" + list2.size());
                Intent intent = new Intent(context, ImagePagerAct.class);
                intent.putExtra("index", index2);
                intent.putStringArrayListExtra("list", list2);
                context.startActivity(intent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

    }


//    @Override
//    public boolean onMessageLinkClick(String link) {
//        Log.e(TAG, "----onMessageLongClick:" + link);
//
//        return false;
//    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {

        Log.e(TAG, "----onMessageLongClick");
        return false;
    }

    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param status 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus status) {
        LogUtil.logE(TAG, "onChanged:" + status);
        switch (status) {
            case KICKED_OFFLINE_BY_OTHER_CLIENT:
                JPushManager.getInstance().clearAlias(MyApplication.getContext());
                LogUtil.logE("onChanged", "账号在其它地方登陆");
                EventBus.getDefault().post(new IMEventType(IMEventType.typeLoginOtherDevice));
                break;
            case DISCONNECTED:
                break;
        }
        // if (status.getMessage().equals(ConnectionStatus.DISCONNECTED.getMessage())) {}
    }

    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context  上下文
     * @param callback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback callback) {
        Log.e(TAG, "----onStartLocation 想打开第三方地图");
        /**
         * demo 代码 开发者需替换成自己的代码。
         */
        // DemoContext.getInstance().setLastLocationCallback(callback);
        // context.startActivity(new Intent(context,
        // SOSOLocationActivity.class));//SOSO地图

    }

    /**
     * 点击会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation conversation) {
        Log.e(TAG, "----onConversationClick ");
//		 MessageContent messageContent = conversation.getMessageContent();
//		 if (messageContent instanceof TextMessage) {//文本消息
//		 TextMessage textMessage = (TextMessage) messageContent;
//		 } else if (messageContent instanceof ContactNotificationMessage) {
//		 Log.e(TAG, "---onConversationClick--ContactNotificationMessage-");
//		 context.startActivity(new Intent(context,
//		 NewFriendListActivity.class));
//		 return true;
//		 }
        return false;//默认是false
    }

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    /**
     * 长按会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 长按会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation conversation) {
        return false;
    }

//    @Override
    //   public void onComplete(AbstractHttpRequest abstractHttpRequest, Object obj) {
    // if (getUserInfoByUserIdHttpRequest != null &&
    // getUserInfoByUserIdHttpRequest.equals(abstractHttpRequest)) {
    // if (obj instanceof User) {
    // final User user = (User) obj;
    // if (user.getCode() == 200) {
    // UserInfos addFriend = new UserInfos();
    // addFriend.setUsername(user.getResult().getUsername());
    // addFriend.setUserid(user.getResult().getId());
    // addFriend.setPortrait(user.getResult().getPortrait());
    // addFriend.setStatus("0");
    // mUserInfosDao.insertOrReplace(addFriend);
    // }
    // }
    // } else if (getFriendByUserIdHttpRequest != null &&
    // getFriendByUserIdHttpRequest.equals(abstractHttpRequest)) {
    // Log.e(TAG, "-------hasUserId----000000-------");
    // if (obj instanceof User) {
    // final User user = (User) obj;
    // Log.e(TAG, "-------hasUserId------11111111-----");
    // if (user.getCode() == 200) {
    // Log.e(TAG, "-------hasUserId------2222222-----");
    // mHandler.post(new Runnable() {
    // @Override
    // public void run() {
    // if (DemoContext.getInstance() != null) {
    //
    // Log.e(TAG, "-------hasUserId--------is what---" +
    // DemoContext.getInstance().hasUserId(user.getResult().getId()));
    // if (DemoContext.getInstance().hasUserId(user.getResult().getId())) {
    // Log.e(TAG, "-------hasUserId-----------");
    // DemoContext.getInstance().updateUserInfos(user.getResult().getId(),
    // "1");
    // } else {
    // Log.e(TAG, "-------hasUserId---no--------");
    // UserInfo info = new UserInfo(user.getResult().getId(),
    // user.getResult().getUsername(),
    // Uri.parse(user.getResult().getPortrait()));
    // DemoContext.getInstance().insertOrReplaceUserInfo(info, "1");
    // }
    // }
    // }
    // });
    //
    // }
    // }
    // }
    //   }

//    @Override
//    public void onFailure(AbstractHttpRequest abstractHttpRequest, BaseException e) {
//
//    }

    @Override
    public boolean handleMessage(android.os.Message message) {
        return false;
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        WaitingDialog.dismiss();
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(mContextUi, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.userFollow:
                ToastUtil.showToast(mContextUi, mContextApp.getString(R.string.im_attention_success));
                break;
            case Method.userGetGiftList:
                // 礼物列表
                List<Gift> listGift = JSONHelper.getList(obj, Gift.class);
                showDaShangDialog(listGift);
                break;
            case Method.imSendGift:
                ToastUtil.showToast(mContextUi, mContextApp.getString(R.string.im_sent_gift_success));
                break;
        }


    }
}
