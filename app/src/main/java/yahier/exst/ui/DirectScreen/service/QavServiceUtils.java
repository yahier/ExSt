package yahier.exst.ui.DirectScreen.service;

/**
 * Created by meteorshower on 16/3/9.
 */
public class QavServiceUtils {

    public static final String MODEL_TYPE = "modelType";//类型分类
    public static final int MODEL_TYPE_VALUE = -1;//类型分类默认值

    public static final int QAV_START_SERVICE = 1;//启动服务
    public static final int QAV_RESUME_SDK = 2;//onResume运行
    public static final int QAV_PAUSE_SDK = 3;//onPause运行
    public static final int QAV_BACKGROUNP_ING = 4;//后台运行中
    public static final int QAV_STOP_SERVICE = 5;//停止服务
    public static final int QAV_START_DIRECT_SCREEN = 6;//启动直播页面
    public static final int QAV_CANCEL_NOTICE = 7;//取消通知栏通知
}
