package yahier.exst.ui.DirectScreen.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.stbl.stbl.api.data.LiveRoomCreateInfo;

/**
 * Created by meteorshower on 16/3/9.
 */
public class QavsdkServiceApi {

    /** 启动服务 */
    public static void startQavsdkScreenService(Context context, int roomId){
        Log.i("QavsdkServiceApi", " ------------------------------ startQavsdkScreenService --------------------------------- ");
        setRoomId(roomId);
        Intent intent = new Intent(context, QavsdkScreenService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_START_SERVICE);
        bundle.putInt("roomId", roomId);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void onResume(Context context){
        Log.i("QavsdkServiceApi", " ------------------------------ onResume ----------------------------------- ");
        Intent intent = new Intent(context, QavsdkScreenService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_RESUME_SDK);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void onPause(Context context){
        Log.i("QavsdkServiceApi", " ------------------------------- onPause ------------------------------------- ");
        Intent intent = new Intent(context, QavsdkScreenService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_PAUSE_SDK);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void runingBackground(Context context){
        Log.i("QavsdkServiceApi", " --------------------------------- runingBackground -------------------------- ");
        Intent intent = new Intent(context, QavsdkScreenService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_BACKGROUNP_ING);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    /** 启动直播展示 */
    public static void startDirectScreenService(Context context){
        Log.i("QavsdkServiceApi", " --------------------------------- startDirectScreenService -------------------------- ");
        Intent intent = new Intent(context, QavsdkScreenService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_START_DIRECT_SCREEN);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    /** 启动关闭通知栏通知 */
    public static void cancelNoticeService(Context context){
        Log.i("QavsdkServiceApi", " --------------------------------- cancelNoticeService -------------------------- ");
        Intent intent = new Intent(context, QavsdkScreenService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_CANCEL_NOTICE);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    /** 关闭服务 */
//    public static void stopQavsdkScreenService(Context context){
//        Log.i("QavsdkServiceApi", " --------------------------------- stopQavsdkScreenService --------------------------- ");
//        Intent intent = new Intent(context, QavsdkScreenService.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(QavServiceUtils.MODEL_TYPE, QavServiceUtils.QAV_STOP_SERVICE);
//        intent.putExtras(bundle);
//        context.startService(intent);
//    }

    /** 关闭服务 */
    public static void stopQavsdkScreenService(Context context){
        setRoomId(0);
        Intent intent = new Intent(context, QavsdkScreenService.class);
        context.stopService(intent);
    }

    /** 服务是否实在运行状态 */
    public static boolean isQavsdkScreenServiceRuning(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo param : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.stbl.stbl.ui.DirectScreen.service.QavsdkScreenService".equals(param.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static int roomId;

    public static int getRoomId() {
        return roomId;
    }

    public static void setRoomId(int roomId) {
        QavsdkServiceApi.roomId = roomId;
    }
}
