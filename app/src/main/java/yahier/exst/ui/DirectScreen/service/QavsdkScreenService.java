package yahier.exst.ui.DirectScreen.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.api.data.LiveRoomCreateInfo;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.LoadStore;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.ui.DirectScreen.DirectScreenControlActivity;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomGroupManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomMsgManager;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UISkipUtils;
import com.stbl.stbl.widget.avsdk.ExternalCaptureThread;
import com.stbl.stbl.widget.avsdk.QavsdkContacts;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.avsdk.control.QavsdkControl;
import com.tencent.av.sdk.AVConstants;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVView;
import com.tencent.av.utils.PhoneStatusTools;

/**
 * Created by meteorshower on 16/3/9.
 *
 * 直播后台服务
 */
public class QavsdkScreenService extends Service{

    private Logger logger = new Logger("QavsdkScreenService");
    private boolean mIsPaused = false;
    private int mOnOffCameraErrorCode = AVError.AV_OK;
    private int mSwitchCameraErrorCode = AVError.AV_OK;

    private QavsdkControl mQavsdkControl;
    OrientationEventListener mOrientationEventListener = null;
    private ExternalCaptureThread inputStreamThread;

    private String mRecvIdentifier = "";
    private String mSelfIdentifier = "";
    int mRotationAngle = 0;

    private Context ctx;
    private int roomId = 0;
    private Bitmap mLargeIcon;
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int netType = QavsdkContacts.getNetWorkType(ctx);
            mQavsdkControl.setNetType(netType);
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(QavsdkContacts.ACTION_SURFACE_CREATED)) {//界面初始化

            } else if (action.equals(QavsdkContacts.ACTION_VIDEO_CLOSE)) {//视频关闭
                String identifier = intent.getStringExtra(QavsdkContacts.EXTRA_IDENTIFIER);
                int videoSrcType = intent.getIntExtra(QavsdkContacts.EXTRA_VIDEO_SRC_TYPE, AVView.VIDEO_SRC_TYPE_NONE);
                mRecvIdentifier = identifier;
                if (!TextUtils.isEmpty(mRecvIdentifier) && videoSrcType != AVView.VIDEO_SRC_TYPE_NONE) {
                    mQavsdkControl.setRemoteHasVideo(false, mRecvIdentifier, videoSrcType);
                }

            } else if (action.equals(QavsdkContacts.ACTION_VIDEO_SHOW)) {//视频打开
                String identifier = intent.getStringExtra(QavsdkContacts.EXTRA_IDENTIFIER);
                int videoSrcType = intent.getIntExtra(QavsdkContacts.EXTRA_VIDEO_SRC_TYPE, AVView.VIDEO_SRC_TYPE_NONE);
                mRecvIdentifier = identifier;
                if (!TextUtils.isEmpty(mRecvIdentifier) && videoSrcType != AVView.VIDEO_SRC_TYPE_NONE) {
                    mQavsdkControl.setRemoteHasVideo(true, mRecvIdentifier, videoSrcType);
                }
            } else if (action.equals(QavsdkContacts.ACTION_ENABLE_CAMERA_COMPLETE)) {//打开摄像头操作

                mOnOffCameraErrorCode = intent.getIntExtra(QavsdkContacts.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                boolean isEnable = intent.getBooleanExtra(QavsdkContacts.EXTRA_IS_ENABLE, false);

                if (mOnOffCameraErrorCode == AVError.AV_OK) {
                    if (!mIsPaused) {
                        mQavsdkControl.setSelfId(mSelfIdentifier);
                        mQavsdkControl.setLocalHasVideo(isEnable, mSelfIdentifier);
                    }
                } else {
                    ToastUtil.showToast(ctx, isEnable ? "打开摄像头失败" : "关闭摄像头失败");
                }
                //开启渲染回调的接口
                //mQavsdkControl.setRenderCallback();
            } else if (action.equals(QavsdkContacts.ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE)) {//外部设备输入
                mOnOffCameraErrorCode = intent.getIntExtra(QavsdkContacts.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                boolean isEnable = intent.getBooleanExtra(QavsdkContacts.EXTRA_IS_ENABLE, false);

                if (mOnOffCameraErrorCode == AVError.AV_OK) {
                    //打开外部摄像头之后就开始传输，用户可以实现自己的逻辑
                    if (isEnable) {
                        inputStreamThread = new ExternalCaptureThread(getApplicationContext());
                        inputStreamThread.start();
                    } else {
                        if (inputStreamThread != null) {
                            inputStreamThread.canRun = false;
                            inputStreamThread = null;
                        }
                    }
                } else {
                    ToastUtil.showToast(ctx, isEnable ? "打开外部视频输入失败" : "关闭外部视频输入失败");
                }
            } else if (action.equals(QavsdkContacts.ACTION_SWITCH_CAMERA_COMPLETE)) {//选择摄像头

                mSwitchCameraErrorCode = intent.getIntExtra(QavsdkContacts.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                boolean isFront = intent.getBooleanExtra(QavsdkContacts.EXTRA_IS_FRONT, false);
                if (mSwitchCameraErrorCode != AVError.AV_OK) {
                    ToastUtil.showToast(ctx, isFront ? "换用前置摄像头失败" : "换用后置摄像头失败");
                }
            } else if (action.equals(QavsdkContacts.ACTION_MEMBER_CHANGE)) {//成员变化
                mQavsdkControl.onMemberChange();
            } else if (action.equals(QavsdkContacts.ACTION_CHANGE_AUTHRITY)) {
                int result = intent.getIntExtra(QavsdkContacts.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                ToastUtil.showToast(ctx, result == AVError.AV_OK ? "成功切换AuthRity" : "切换AuthRity失败");
            } else if (action.equals(QavsdkContacts.ACTION_OUTPUT_MODE_CHANGE)) {//输出模式改变
                //更新UI
            } else if (action.equals(QavsdkContacts.ACTION_CLOSE_ROOM_COMPLETE)) {//关闭房间
                stopService(new Intent(getApplicationContext(), QavsdkScreenService.class));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        ctx = this;
        mLargeIcon = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon);
        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QavsdkContacts.ACTION_SURFACE_CREATED);
        intentFilter.addAction(QavsdkContacts.ACTION_VIDEO_SHOW);
        intentFilter.addAction(QavsdkContacts.ACTION_VIDEO_CLOSE);
        intentFilter.addAction(QavsdkContacts.ACTION_ENABLE_CAMERA_COMPLETE);
        intentFilter.addAction(QavsdkContacts.ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE);
        intentFilter.addAction(QavsdkContacts.ACTION_SWITCH_CAMERA_COMPLETE);
        intentFilter.addAction(QavsdkContacts.ACTION_MEMBER_CHANGE);
        intentFilter.addAction(QavsdkContacts.ACTION_OUTPUT_MODE_CHANGE);
        registerReceiver(mBroadcastReceiver, intentFilter);

        IntentFilter netIntentFilter = new IntentFilter();
        netIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, netIntentFilter);

        mQavsdkControl = QavsdkManger.getInstance().getQavsdkControl();

        int netType = QavsdkContacts.getNetWorkType(ctx);
        if (netType != AVConstants.NETTYPE_NONE && mQavsdkControl != null) {
            mQavsdkControl.setNetType(QavsdkContacts.getNetWorkType(ctx));
        }

        registerOrientationListener();

        UISkipUtils.stopFloatingDirectScreenService(ctx);
        stopForeground(true);

        mQavsdkControl.getQualityTips();

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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null) {
            QavsdkServiceApi.stopQavsdkScreenService(getApplicationContext());
            return super.onStartCommand(intent, flags, startId);
        }

        Bundle bundle = intent.getExtras();
        if(bundle != null){
            int modelType = bundle.getInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.MODEL_TYPE_VALUE);
            Log.i("QavsdkScreenService", " ---------- onStartCommand ModelType : "+modelType +" ----------------- ");
            switch(modelType){
                case QavServiceUtils.QAV_START_SERVICE:{
                    roomId = bundle.getInt("roomId", 0);
                    mRecvIdentifier = String.valueOf(roomId);
                    mSelfIdentifier = STBLWession.getInstance().readIdentifier();
                    Log.i("QavsdkScreenService", " ---- Start Service : "+ mSelfIdentifier + " ---- ");
                    RoomMsgManager.getInstance().registerNotifyMsgListener();
                }
                    break;
                case QavServiceUtils.QAV_RESUME_SDK: {
                    mQavsdkControl.onResume();
                    if (mOnOffCameraErrorCode != AVError.AV_OK) {
                        ToastUtil.showToast(ctx, "打开摄像头失败");
                    }
                    startOrientationListener();
                    Log.i("QavsdkScreenService", " ---- Service Resume ---- ");
                }
                    break;
                case QavServiceUtils.QAV_PAUSE_SDK:{
                    mQavsdkControl.onPause();
                    if (mOnOffCameraErrorCode != AVError.AV_OK) {
                        ToastUtil.showToast(ctx, "关闭摄像头失败");
                    }
                    stopOrientationListener();
                    Log.i("QavsdkScreenService", " ---- Service Pause ---- ");
                }
                    break;
                case QavServiceUtils.QAV_BACKGROUNP_ING:{
                    Intent clickIntent = new Intent(ctx, DirectScreenControlActivity.class); //点击通知
                    clickIntent.putExtra("roomId", roomId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
                    builder.setSmallIcon(R.drawable.icon)
                            .setLargeIcon(mLargeIcon)
                            .setContentTitle("直播正在进行中")
                            .setTicker(getString(R.string.direct_scree_backgrounp))
                            .setContentText("请保持程序在后台运行")
                            .setContentIntent(pendingIntent);
                    Notification n = builder.build();
                    startForeground(0x111, n);
                }
                break;
                case QavServiceUtils.QAV_STOP_SERVICE:{
                    Log.i("QavsdkScreenService", " ---- Service Stop ---- ");
                    stopService(new Intent(getApplicationContext(), QavsdkScreenService.class));
                }
                    break;
                case QavServiceUtils.QAV_START_DIRECT_SCREEN:{
                    UISkipUtils.startMainDirectScreenActivity(getApplicationContext(), roomId);
                }
                break;
                case QavServiceUtils.QAV_CANCEL_NOTICE:{
                    stopForeground(true);
                }
                break;
            }
        }else{
            Log.i("QavsdkScreenService", " ------------------- Bundle is null !! ----------------------- ");
            QavsdkServiceApi.stopQavsdkScreenService(getApplicationContext());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RoomMsgManager.getInstance().unregisterNotifyMsgListener();
        RoomGroupManager.getInstance().clearRoomGroupIdAndQuiteGroup();
        JSONObject json = new JSONObject();
        json.put("roomid", roomId);
        new HttpEntity(ctx).commonPostJson(Method.imLiveroomOut, json.toJSONString(),null);

        mQavsdkControl.onPause();
        stopOrientationListener();
        Log.e("memoryLeak", "memoryLeak avactivity onDestroy");
        mQavsdkControl.onDestroy();
        // 注销广播
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
        Log.e("memoryLeak", "memoryLeak avactivity onDestroy end");


        if ((mQavsdkControl != null) && (mQavsdkControl.getAVContext() != null) &&
                (mQavsdkControl.getAVContext().getAudioCtrl() != null)) {
            mQavsdkControl.getAVContext().getAudioCtrl().stopTRAEService();
        }

        if (inputStreamThread != null) {
            inputStreamThread.canRun = false;
            inputStreamThread = null;
        }
        if ((mQavsdkControl != null) && (mQavsdkControl.getAVContext() != null) && (mQavsdkControl.getAVContext().getAudioCtrl() != null)) {
            mQavsdkControl.getAVContext().getAudioCtrl().stopTRAEService();
        }
        mQavsdkControl.exitRoom();

        roomId = 0;
        //用于判断是否房主，嘉宾的，服务结束，清除
        LoadStore.getInstance().setRoomGuest(0);
        LoadStore.getInstance().setRoomOwner(0);
        LoadStore.getInstance().setRoomId(0);
    }

    protected class VideoOrientationEventListener extends OrientationEventListener {

        boolean mbIsTablet = false;

        public VideoOrientationEventListener(Context context, int rate) {
            super(context, rate);
            mbIsTablet = PhoneStatusTools.isTablet(context);
        }

        int mLastOrientation = -25;

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                if (mLastOrientation != orientation) {
					/*
					 * if (mControlUI != null) { mControlUI.setRotation(270); }
					 * if (mVideoLayerUI != null) {
					 * mVideoLayerUI.setRotation(270); }
					 */
                }
                mLastOrientation = orientation;
                return;
            }

            if (mLastOrientation < 0) {
                mLastOrientation = 0;
            }

            if (((orientation - mLastOrientation) < 20)
                    && ((orientation - mLastOrientation) > -20)) {
                return;
            }

            if(mbIsTablet){
                orientation -= 90;
                if (orientation < 0) {
                    orientation += 360;
                }
            }

            mLastOrientation = orientation;

            if (orientation > 314 || orientation < 45) {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(0);

                }

                mRotationAngle = 0;
            } else if (orientation > 44 && orientation < 135) {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(90);
                }
                mRotationAngle = 90;
            } else if (orientation > 134 && orientation < 225) {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(180);
                }
                mRotationAngle = 180;
            } else {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(270);
                }
                mRotationAngle = 270;
            }
        }
    }

    void registerOrientationListener() {
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new VideoOrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_UI);
        }
    }

    void startOrientationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }
    }

    void stopOrientationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
    }

    //设置Yuv参数
    private void setYuvParams(){
//		QavsdkContacts.inputYuvFilePath = input_file_path.getText().toString();//输入流路径
//		QavsdkContacts.yuvWide = Integer.parseInt(yuv_wide.getText().toString());//视频宽
//		QavsdkContacts.yuvHigh = Integer.parseInt(yuv_high.getText().toString());//视频高
//		QavsdkContacts.outputYuvFilePath = output_file_path.getText().toString();//输出流路径
//		QavsdkContacts.yuvFormat = 0;//应该是渲染模式：0为I420、1为RGB（注：RGB为第一采集视频，I420需要在RGB上执行压缩）
    }

}
