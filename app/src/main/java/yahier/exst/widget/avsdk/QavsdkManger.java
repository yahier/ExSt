package yahier.exst.widget.avsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.LoadStore;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomMsgManager;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StringUtils;
import com.stbl.stbl.utils.UISkipUtils;
import com.stbl.stbl.widget.avsdk.control.QavsdkControl;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVConstants;
import com.tencent.av.sdk.AVRoom;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by meteorshower on 16/3/15.
 */
public class QavsdkManger {

    private Logger logger = new Logger("QavsdkManger");
    private static QavsdkManger mQavsdkManger = null;
    private QavsdkControl mQavsdkControl = null;
    private int roomId = 0;//房间ID
    private boolean firstJoinRoom, isGuest;
    private boolean formatQvContextCreateRoom = false;//创建房间初始化
    public static final int MAX_TIMEOUT = 5*1000;
    public static final int MSG_CREATEROOM_TIMEOUT = 1;
    private OnQavsdkUpdateListener listener;
    private HashMap<String, BroadcastReceiver> receiverMap = new HashMap<String, BroadcastReceiver>();
    private OnQavsdkRoomCloseListener closeListener;
    private boolean isOpenQavsdkService = false;//是否开启服务

    public static QavsdkManger getInstance(){
        if(mQavsdkManger == null)
            mQavsdkManger = new QavsdkManger();
        return mQavsdkManger;
    }

    public QavsdkControl getQavsdkControl(){
        if(mQavsdkControl == null)
            initQavsdkControl(MyApplication.getStblContext());

        return mQavsdkControl;
    }

    /** 初始化QavsdkControl */
    public void initQavsdkControl(Context context){
        if(!isOpenQavsdkService)
            return;

        mQavsdkControl = new QavsdkControl(context);

        AVAudioCtrl.AudioFrameDesc desc = new AVAudioCtrl.AudioFrameDesc();
        desc.sampleRate = 8000;
        desc.channelNum = 1;
        desc.bits = 16;
        mQavsdkControl.getAVAudioControl().setAudioDataFormat(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIC, desc);//采样码率与声道
        mQavsdkControl.getAVAudioControl().setAudioDataVolume(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIC, 50.0f);//频率 0-100
    }

    /** 是否开启测试环境 */
    public void setTestEnvStatus(boolean envStatus){
        if(!isOpenQavsdkService)
            return;

        getQavsdkControl().setTestEnvStatus(envStatus);
    }

    /** ———————————————————————————————————————————— 初始化QavsdkControls ——————————————————————————————————————*/

    //初始化QavContext Receiver
    private class RegisterFormatQavContextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(QavsdkContacts.ACTION_START_CONTEXT_COMPLETE)) {
                int mLoginErrorCode = intent.getIntExtra(QavsdkContacts.EXTRA_AV_ERROR_RESULT, QavsdkError.AV_OK);

                if(getQavsdkControl().getAVContext() == null){
                    formatAvContext();
                }else if (mLoginErrorCode != QavsdkError.AV_OK) {
                    ToastUtil.showToast(context, QavsdkError.getErrorValue(mLoginErrorCode));
                }else if(formatQvContextCreateRoom){
                    formatQvContextCreateRoom = false;
                    createRoomEnter(roomId, listener);
                }
            } else if (action.equals(QavsdkContacts.ACTION_CLOSE_CONTEXT_COMPLETE)) {//关闭Context
                getQavsdkControl().setIsInStopContext(false);
            }
        }
    };

    /** 注册启动QavsdkControl */
    public void registerStartQavsdkControl(Context context, String classFlag){
        if(!isOpenQavsdkService)
            return;

        String keyReceiver = getRegisterContextName(classFlag, "Start");
        if(receiverMap.containsKey(keyReceiver))
            return;
        receiverMap.put(keyReceiver, new RegisterFormatQavContextReceiver());

        IntentFilter intentFilterQavContext = new IntentFilter();
        intentFilterQavContext.addAction(QavsdkContacts.ACTION_START_CONTEXT_COMPLETE);
        intentFilterQavContext.addAction(QavsdkContacts.ACTION_CLOSE_CONTEXT_COMPLETE);
        context.registerReceiver(receiverMap.get(keyReceiver), intentFilterQavContext);
    }

    //初始化AvContext环境
    public boolean formatAvContext(){
        if(!isOpenQavsdkService)
            return isOpenQavsdkService;

        String UserId = STBLWession.getInstance().readIdentifier();
        if(StringUtils.isEmpty(UserId)){
            ToastUtil.showToast("直播帐号信息错误！！");
            return false;
        }

        if (!getQavsdkControl().hasAVContext()){
            logger.test_i("Qav UserId: ", UserId + " -- UserSign: " + STBLWession.getInstance().readUserSig());
            getQavsdkControl().startContext(UserId, STBLWession.getInstance().readUserSig());
            RoomMsgManager.getInstance().init(MyApplication.getStblContext());
            return true;
        }else{
            logger.i(" ------------------ Dont have format !! ------------------ ");
        }
        return false;
    }

    /** 注销FormatQavsdkControl */
    public void unregisterQavsdkControl(Context context, String classFlag){
        if(!isOpenQavsdkService)
            return;

        try{
            String keyReceiver = getRegisterContextName(classFlag, "Start");
            if(receiverMap.containsKey(keyReceiver)) {
                context.unregisterReceiver(receiverMap.get(keyReceiver));
                receiverMap.remove(keyReceiver);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** —————————————————————————————————————————————— 创建房间 —————————————————————————————————————————————— */
    //房间创建Receiver
    private class RegisterCreateRoomReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(QavsdkContacts.ACTION_ROOM_CREATE_COMPLETE)) {//房间创建
                handlerQavRoom.removeMessages(MSG_CREATEROOM_TIMEOUT);
                int mCreateRoomErrorCode = intent.getIntExtra(QavsdkContacts.EXTRA_AV_ERROR_RESULT, QavsdkError.AV_OK);
                if(listener != null)
                    listener.closeProgress();

                if(roomId == 0)
                    return;
                if (mCreateRoomErrorCode == QavsdkError.AV_OK) {
                    logger.e(" --------------------------- Start Direct Screen Act !! "+firstJoinRoom+"---------------------------");
                    LoadStore.getInstance().setRoomId(roomId);
                    UISkipUtils.startMainDirectScreenActivity(context, roomId, firstJoinRoom, isGuest);
                    if(listener != null)
                        listener.startSdkSuccess();
                    roomId = 0;
                    firstJoinRoom = false;
                } else {
                    logger.e(" --------------------------- Join Room Error : "+mCreateRoomErrorCode+" ---------------------------");
                    if ((getQavsdkControl() != null) && (getQavsdkControl().getAVContext() != null) && (getQavsdkControl().getAVContext().getAudioCtrl() != null)) {
                        getQavsdkControl().getAVContext().getAudioCtrl().stopTRAEService();
                    }
                    ToastUtil.showToast(MyApplication.getStblContext(), QavsdkError.getErrorValue(mCreateRoomErrorCode));
                }
            } else if (action.equals(QavsdkContacts.ACTION_CLOSE_ROOM_COMPLETE)) {//房间关闭
                if (closeListener != null){
                    closeListener.roomStatus(2);
                    closeListener = null;
                }
            }
        }
    };

    /** 创建房间绑定 */
    public void registerCreateRoom(Context context, String classFlag){
        if(!isOpenQavsdkService)
            return;

        registerStartQavsdkControl(context, classFlag);
        String keyReceiver = getRegisterContextName(classFlag, "Create");
        if(receiverMap.containsKey(keyReceiver))
            return;
        receiverMap.put(keyReceiver, new RegisterCreateRoomReceiver());

        IntentFilter intentFilterCreateRoom = new IntentFilter();
        intentFilterCreateRoom.addAction(QavsdkContacts.ACTION_ROOM_CREATE_COMPLETE);
        intentFilterCreateRoom.addAction(QavsdkContacts.ACTION_CLOSE_ROOM_COMPLETE);
        context.registerReceiver(receiverMap.get(keyReceiver), intentFilterCreateRoom);
    }

    /** 创建房间 */
    public boolean createRoomEnter(int roomId,OnQavsdkUpdateListener listener){
        if(!isOpenQavsdkService)
            return isOpenQavsdkService;

        return createRoomEnter(roomId, false, listener);
    }

    /** 创建房间 */
    public boolean createRoomEnter(int roomId, boolean firstJoinRoom,OnQavsdkUpdateListener listener){
        if(!isOpenQavsdkService)
            return isOpenQavsdkService;

        return createRoomEnter(roomId, firstJoinRoom, false, listener);
    }

    /** 创建房间 */
    public boolean createRoomEnter(int roomId, boolean firstJoinRoom, boolean isGuest,OnQavsdkUpdateListener listener){
        logger.e(" --------------------------- Create Room Enter !! "+firstJoinRoom+"---------------------------");
        if(!isOpenQavsdkService)
            return isOpenQavsdkService;

        this.firstJoinRoom = firstJoinRoom;
        this.isGuest = isGuest;
        setOnQavsdkUpdateListener(listener);
        this.roomId = roomId;
        if(!getQavsdkControl().hasAVContext()){
            formatQvContextCreateRoom = true;
            return formatAvContext();
        }

        formatQvContextCreateRoom = false;

        String roomRoleValue = QavsdkContacts.ROOM_ROLE_VALUE;

        setControlAuthBits();

        if (QavsdkContacts.isNetworkAvailable(MyApplication.getStblContext())) {
            if (this.roomId != 0) {
                if ((getQavsdkControl() != null) && (getQavsdkControl().getAVContext() != null) && (getQavsdkControl().getAVContext().getAudioCtrl() != null)) {
                    getQavsdkControl().getAVContext().getAudioCtrl().startTRAEService();
                }
                getQavsdkControl().enterRoom(this.roomId, roomRoleValue);
                handlerQavRoom.sendEmptyMessageDelayed(MSG_CREATEROOM_TIMEOUT, MAX_TIMEOUT);
                return true;
            }
        } else {
            if(listener != null)
                listener.closeProgress();
            ToastUtil.showToast(MyApplication.getStblContext(), "网络无连接，请检查网络");
        }

        return false;
    }

    private Handler handlerQavRoom = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CREATEROOM_TIMEOUT:
                    if (mQavsdkControl != null) {
                        mQavsdkControl.setCreateRoomStatus(false);
                        mQavsdkControl.setCloseRoomStatus(false);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    //设置上下行通道参数
    private void setControlAuthBits(){
//		QavsdkContacts.auth_bits = AVRoom.AUTH_BITS_DEFUALT;//音视频上下行通道设置（默认设置）
        QavsdkContacts.auth_bits = AVRoom.AUTH_BITS_CREATE_ROOM | AVRoom.AUTH_BITS_JOIN_ROOM | AVRoom.AUTH_BITS_RECV_SUB;//创建房间|加入房间|接收
        QavsdkContacts.auth_bits |= AVRoom.AUTH_BITS_SEND_AUDIO;//音频上行（发送）
        QavsdkContacts.auth_bits |= AVRoom.AUTH_BITS_RECV_AUDIO;//音频下行（接收）
//		QavsdkContacts.auth_bits |= AVRoom.AUTH_BITS_SEND_VIDEO;//视频主路上行（发送）
//		QavsdkContacts.auth_bits |= AVRoom.AUTH_BITS_RECV_VIDEO;//视频主路下行（接收）

//        QavsdkContacts.auth_bits |= AVRoom.AUDIO_CATEGORY_VOICECHAT;

//		if (null != mQavsdkControl) {
//			mQavsdkControl.setIsSupportMultiView(true);//是否支持多路视频输出
//			mQavsdkControl.setIsOpenBackCameraFirst(true);//是否先打开后置摄像头
//		}
    }

    public void unregisterCreateRoom(Context context, String classFlag){
        if(!isOpenQavsdkService)
            return;

        unregisterQavsdkControl(context, classFlag);

        try{
            String keyReceiver = getRegisterContextName(classFlag, "Create");
            if(receiverMap.containsKey(keyReceiver)) {
                context.unregisterReceiver(receiverMap.get(keyReceiver));
                receiverMap.remove(keyReceiver);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setOnQavsdkUpdateListener(OnQavsdkUpdateListener listener){
        this.listener = listener;
    }

    public interface OnQavsdkUpdateListener{

        public void startSdkSuccess();

        public void closeProgress();
    }

    public void setOnQavsdkRoomCloseListener(OnQavsdkRoomCloseListener closeListener){
        this.closeListener = closeListener;
    }
    public interface OnQavsdkRoomCloseListener{
        /**
         * 房间状态：房间存在，需要关闭，再创建新房间
         * @param status 1:房间存在并进入； 2：房间存在需关闭； 3：房间不存在，可以创建新房间; 4:不关闭之前房间
         */
        void roomStatus(int status);
    }

    /** —————————————————————————————————————————— 公共调用方法 ———————————————————————————————————————————————— */

    /** 创建并绑定Parent View */
    public boolean onCreate(View parentView){
        if(!isOpenQavsdkService)
            return isOpenQavsdkService;

        if (getQavsdkControl().getAVContext() != null) {
            getQavsdkControl().onCreate(MyApplication.getStblContext(), parentView);
            return true;
        }
        return false;
    }

    /** 获取实时在麦列表 */
    public ArrayList<MemberInfo> getAtAudioMemberList(){
        if(!isOpenQavsdkService)
            return new ArrayList<>();

        return getQavsdkControl().getMemberList();
    }

    /** 执行上麦/下麦设置 */
    public void setAudioEnableMic(boolean enableMic){
        if(!isOpenQavsdkService)
            return;

        AVAudioCtrl avAudioCtrl = getQavsdkControl().getAVContext().getAudioCtrl();
        if(avAudioCtrl != null)
        avAudioCtrl.enableMic(enableMic);
    }

    /** 设置网络类型 */
    public void setNetType(Context context){
        if(!isOpenQavsdkService)
            return;

        int netType = QavsdkContacts.getNetWorkType(context);
        if (netType != AVConstants.NETTYPE_NONE) {
            getQavsdkControl().setNetType(QavsdkContacts.getNetWorkType(context));
        }
    }

    /** 是否开启免提 */
    public boolean getHandfreeChecked(){
        if(!isOpenQavsdkService)
            return isOpenQavsdkService;

        return getQavsdkControl().getHandfreeChecked();
    }

    /** 设置声音输出模式:免提/话筒 */
    public void setAudioOutputMode(){
        if(!isOpenQavsdkService)
            return;

        getQavsdkControl().getAVContext().getAudioCtrl().setAudioOutputMode(getQavsdkControl().getHandfreeChecked() ?
                AVAudioCtrl.OUTPUT_MODE_SPEAKER : AVAudioCtrl.OUTPUT_MODE_HEADSET);
    }

    public void enableSpeaker(boolean enable){
        if(!isOpenQavsdkService)
            return;

        if (getQavsdkControl().getAVContext() != null && getQavsdkControl().getAVContext().getAudioCtrl() != null)
        getQavsdkControl().getAVContext().getAudioCtrl().enableSpeaker(enable);
    }

    /** 退出SDK服务 */
    public void editQavsdk(){
        STBLWession.getInstance().resetQavsdkSig();
        if(!isOpenQavsdkService)
            return;

        getQavsdkControl().stopContext();
        RoomMsgManager.getInstance().logOutIM();
    }

    //音频模式
//        final String[] modeList = {"VoiceChat",  "MediaPlayRecord", "MediaPlayback"};
//        mQavsdkControl.setAudioCat(whichButton);


    //开启\关闭免提
//		mQavsdkControl.getAVContext().getAudioCtrl().setAudioOutputMode(mQavsdkControl.getHandfreeChecked() ?
//				AVAudioCtrl.OUTPUT_MODE_SPEAKER : AVAudioCtrl.OUTPUT_MODE_HEADSET);

    //打开\关闭麦克风
//		AVAudioCtrl avAudioCtrl = mQavsdkControl.getAVContext().getAudioCtrl();
//		avAudioCtrl.enableMic(true);

    //打开\关闭摄像头
//		boolean isEnable = mQavsdkControl.getIsEnableCamera();
//		mOnOffCameraErrorCode = mQavsdkControl.toggleEnableCamera();
//		if (mOnOffCameraErrorCode != AVError.AV_OK) {
//			Toast.makeText(ctx, isEnable ? "关闭摄像头失败" : "打开摄像头失败", Toast.LENGTH_SHORT).show();
//			mQavsdkControl.setIsInOnOffCamera(false);
//		}

    //打开\关闭外部视频输入
//		boolean isEnableExternalCapture = mQavsdkControl.getIsEnableExternalCapture();
//		mEnableExternalCaptureErrorCode = mQavsdkControl.enableExternalCapture(!isEnableExternalCapture);
//		if (mEnableExternalCaptureErrorCode != AVError.AV_OK) {
//			Toast.makeText(ctx, isEnableExternalCapture ? "关闭外部视频输入失败" : "打开外部视频输入失败", Toast.LENGTH_SHORT).show();
//			mQavsdkControl.setIsOnOffExternalCapture(false);
//		}

    //录制开始\结束
//		mQavsdkControl.enableUserRender(true)

    //前后置摄像头切换
//		boolean isFront = mQavsdkControl.getIsFrontCamera();
//		mSwitchCameraErrorCode = mQavsdkControl.toggleSwitchCamera();
//		if (mSwitchCameraErrorCode != AVError.AV_OK) {
//			Toast.makeText(ctx, isFront ? "换用后置摄像头失败" : "换用前置摄像头失败", Toast.LENGTH_SHORT).show();
//			mQavsdkControl.setIsInSwitchCamera(false);
//		}

    private String getRegisterContextName(String classflag,String flag){
        StringBuffer sb = new StringBuffer();
        sb.append("register");
        sb.append(flag);
        sb.append(classflag);
        return sb.toString();
    }

    public boolean isOpenQavsdkService() {
        return isOpenQavsdkService;
    }

    public void setOpenQavsdkService(boolean openQavsdkService) {
        isOpenQavsdkService = openQavsdkService;
        RoomMsgManager.getInstance().setOpenMsgService(openQavsdkService);
    }
}
