package yahier.exst.ui.DirectScreen.homeNotify;

import android.content.Context;

import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.MsgConnectionListener;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.MsgRecordListener;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.NewMsgNofityListener;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomMsgCallBack;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.SendMsgStateListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUser;
import com.tencent.TIMUserStatusListener;
import com.tencent.TIMValueCallBack;
import com.tencent.qalsdk.QALSDKManager;

import java.util.List;

/**
 * Created by meteorshower on 16/4/10.
 */
public class RoomMsgManager {

    private Logger logger = new Logger(this.getClass().getSimpleName());
    private static RoomMsgManager roomMsgManager = null;
    private boolean isOpenMsgService = false;

    public static RoomMsgManager getInstance(){
        if(roomMsgManager == null)
            roomMsgManager = new RoomMsgManager();
        return roomMsgManager;
    }

    private TIMManager timManager = null;
    public static final int SEND_MSG_GROUP_CHAT_TYPE = 1;
    public static final int SEND_MSG_ONLY_CHAT_TYPE = 2;
    private NewMsgNofityListener newMsgNotifyListener;

    private TIMManager getTimManager() {
        if (timManager == null)
            timManager = TIMManager.getInstance();
        return timManager;
    }

    /** 登录 */
    public void loginIM(){
        loginIM(null);
    }

    /** 登录 */
    public void loginIM(final OnRoomMsgCallBack<Boolean> callBack) {
        if (!isOpenMsgService)
            return;

        init(MyApplication.getStblContext());

        TIMUser userInfo = new TIMUser();
        userInfo.setAccountType(STBLWession.getInstance().readAccountType());
        userInfo.setIdentifier(STBLWession.getInstance().readIdentifier());
        userInfo.setAppIdAt3rd(STBLWession.getInstance().readIdentifier());

        getTimManager().login(STBLWession.getInstance().readSdkAppid(), userInfo, STBLWession.getInstance().readUserSig(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if(callBack != null)
                    callBack.roomMsgError(i);
                ToastUtil.showToast(IMError.getErrorValue(i));
                logger.e(" --------------------------------------------------------------- ");
                logger.e(" ------------------- TIM Login Error : "+s+" ------------------- ");
                logger.e(" --------------------------------------------------------------- ");
                //登录失败重试
//                loginIM();
            }

            @Override
            public void onSuccess() {
                if(callBack != null)
                    callBack.roomMsgSuccess(true);
                //登录成功
                logger.e(" --------------------------------------------------------------- ");
                logger.e(" ---------------------- TIM Login Success ---------------------- ");
                logger.e(" --------------------------------------------------------------- ");
            }
        });
    }

    public void init(Context context){
        if (!isOpenMsgService)
            return;

        getTimManager().setEnv(0);//1：测试环境，0：正式环境
        getTimManager().setMode(1);//1 启用IM功能 0 不启用IM功
        getTimManager().init(context);
    }

    /** 监听是否掉线 */
    public void listenerUserStatus(boolean isUserStatus){
        if (!isOpenMsgService)
            return;

        if(isUserStatus){
            getTimManager().setUserStatusListener(new TIMUserStatusListener() {
                @Override
                public void onForceOffline() {
                    loginIM();
                    logger.e(" --------------------------------------------------------------- ");
                    logger.e(" ---------------------- onForceOffline 掉线 ---------------------- ");
                    logger.e(" --------------------------------------------------------------- ");
                }

                @Override
                public void onUserSigExpired() {

                }
            });
        }else{
            getTimManager().setUserStatusListener(null);
        }
    }

    /** 注册消息监听 */
    public void registerNotifyMsgListener() {
        if (!isOpenMsgService)
            return;

        newMsgNotifyListener = new NewMsgNofityListener();
        getTimManager().addMessageListener(newMsgNotifyListener);
        getTimManager().addMessageUpdateListener(newMsgNotifyListener);

        getTimManager().setConnectionListener(new MsgConnectionListener());
        listenerUserStatus(true);
    }

    public void unregisterNotifyMsgListener(){
        if (!isOpenMsgService)
            return;

        getTimManager().removeMessageListener(newMsgNotifyListener);
        getTimManager().removeMessageUpdateListener(newMsgNotifyListener);
        listenerUserStatus(false);
    }

    /** 发送消息 */
    public void sendMsgChat(String identifier, TIMMessage msg, final SendMsgStateListener listener, int type){
        if (!isOpenMsgService)
            return;

        TIMConversation conversation = getTIMConversation(identifier, getTIMChatType(type));
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                logger.e(" --------------------------------------------------------------- ");
                logger.e(" ---------------- sendMsgChat Error : "+s+" ---------------- ");
                logger.e(" --------------------------------------------------------------- ");
                if(listener != null)
                    listener.sendMsgError(i);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                logger.e(" --------------------------------------------------------------- ");
                logger.e(" ---------------- sendMsgChat Success : "+timMessage.toString()+" ---------------- ");
                logger.e(" --------------------------------------------------------------- ");
                if (listener != null)
                    listener.sendMsgSuccess(timMessage);
            }
        });
    }

    /** 获取消息记录 */
    public void getMsgRecord(String identifier, int count, TIMMessage msg, final MsgRecordListener listener, int type){
        if (!isOpenMsgService)
            return;

        TIMConversation conversation = getTIMConversation(identifier, getTIMChatType(type));
        conversation.getMessage(count, msg, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {
                if(listener != null)
                    listener.msgError(i);
            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {
                if(listener != null)
                    listener.msgSuccess(timMessages);
            }
        });
    }

    /** 消息已读上报 */
    public void setReadMessage(String identifier,TIMMessage msg, int type){
        if (!isOpenMsgService)
            return;

        TIMConversation conversation = getTIMConversation(identifier, getTIMChatType(type));
        conversation.setReadMessage(msg);
    }

    /** 注销 */
    public void logOutIM(){
        if (!isOpenMsgService)
            return;

        getTimManager().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                logger.e(" --------------------------------------------------------------- ");
                logger.e(" ------------------ TIM Logout Error : "+s+" ------------------- ");
                logger.e(" --------------------------------------------------------------- ");
            }

            @Override
            public void onSuccess() {

                logger.e(" --------------------------------------------------------------- ");
                logger.e(" ---------------------- TIM Logout Success --------------------- ");
                logger.e(" --------------------------------------------------------------- ");
            }
        });
    }

    private TIMConversation getTIMConversation(String identifier, TIMConversationType type){
        return getTimManager().getConversation(type, identifier);
    }

    private TIMConversationType getTIMChatType(int type){
        switch(type){
            case SEND_MSG_GROUP_CHAT_TYPE:
                return TIMConversationType.Group;
            case SEND_MSG_ONLY_CHAT_TYPE:
                return TIMConversationType.C2C;
            default:
                return TIMConversationType.C2C;
        }
    }

    public boolean isOpenMsgService() {
        return isOpenMsgService;
    }

    public void setOpenMsgService(boolean openMsgService) {
        isOpenMsgService = openMsgService;
    }
}
