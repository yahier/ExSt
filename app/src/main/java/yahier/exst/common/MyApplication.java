package yahier.exst.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import com.bumptech.glide.request.target.ViewTarget;
import com.stbl.base.ui.BaseApplication;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.ContactNotificationMessageProvider;
import com.stbl.stbl.act.im.rong.DeAgreedFriendRequestMessage;
import com.stbl.stbl.act.im.rong.DemoCommandNotificationMessage;
import com.stbl.stbl.act.im.rong.RongCloudEvent;
import com.stbl.stbl.act.login.LoginActivity;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.item.PhoneDevice;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.receiver.RunBackgroundReceiver;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DongtaiRemarkDB;
import com.stbl.stbl.util.ErrorRecodeDB;
import com.stbl.stbl.util.FaceConversionUtil;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.NetUtil;
import com.stbl.stbl.util.SharedDevice;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.StblExceptionHandler;
import com.stbl.stbl.util.ThreadPool;
import com.stbl.stbl.widget.AppToast;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.jpush.JPushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import cn.jpush.android.api.JPushInterface;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.ipc.RongExceptionHandler;

public class MyApplication extends BaseApplication {

    private static MyApplication mInstance = null;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //Stetho.initializeWithDefaults(this);
        // LeakCanary.install(this);//内存泄漏分析
        ViewTarget.setTagId(R.id.glide_tag);
        mInstance = this;
        ConfigControl.switchHost();
        context = getApplicationContext();

        //友盟测试开关
        MobclickAgent.setDebugMode(ConfigControl.logable);
        //云账户初始化
        //在Application的onCreate()方法中调用
        RedPacket.getInstance().initContext(this,
                RPConstant.AUTH_METHOD_SIGN);
        // 打开Log开关，正式发布需要关闭
        RedPacket.getInstance().setDebugMode(ConfigControl.logable);

        QavsdkManger.getInstance().initQavsdkControl(this);

        LocalBroadcastHelper.getInstance().init(this);
        AppToast.init(this);

        Config.ensurePath();

        // mLocationClient = new LocationClient(getApplicationContext());
        // mLocationClient.registerLocationListener(myListener);

        RongIM.init(this);//新加
        /**
         * 注意： IMKit SDK调用第一步 初始化 只有两个进程需要初始化，主进程和 push 进程
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
            /**
             * 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

                RongCloudEvent.init(this);//已在TabHome中操作此步骤
                // DemoContext.init(this);在demo中有这一步操作
                Thread.setDefaultUncaughtExceptionHandler(new StblExceptionHandler(this));
                try {
                    RongIM.registerMessageType(DemoCommandNotificationMessage.class);
                    RongIM.registerMessageType(DeAgreedFriendRequestMessage.class);
                    RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Logger.setCanWriteLog(false);
        QavsdkManger.getInstance().setTestEnvStatus(false);///打开直播测试环境
        JPushInterface.setDebugMode(ConfigControl.logable);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        JPushManager.getInstance().stopPush();
        //JPushManager.getInstance().clearAlias(MyApplication.getContext());//清除别名

        initData();
        new RunBackgroundReceiver(MyApplication.this);

        //只在主进程执行
        if (getCurProcessName(getApplicationContext()).equals(getApplicationInfo().packageName)) {
            //LogUtil.logE("MyApplication","oncreate执行一些东西");
            CommonTask.getCommonDicBackground();
            CommonTask.getRedpacketSetting();
            //CommonTask.getOfficeAccount();
            //CommonTask.initFriendDB(GroupMemberList.typeRequestNoneGroup, 0, GroupMemberList.hasselfNo);
            uploadErrorRecord();
            //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHanlder(this));
            new DongtaiRemarkDB(this).deleteExpiredRemark();
        }

    }


    //重新上传 app运行中上传失败的记录
    void uploadErrorRecord() {
        ErrorRecodeDB db = new ErrorRecodeDB(MyApplication.getInstance());
        Cursor cursor = db.queryAll();
        //LogUtil.logE("app uploadErrorRecord count",cursor.getCount());
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(ErrorRecodeDB.columnType);
            int index2 = cursor.getColumnIndex(ErrorRecodeDB.columnMsg);
            int type = Integer.parseInt(cursor.getString(index1));
            String msg = cursor.getString(index2);
            CommonTask.uploadIMSentError(type, msg);
        }

    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void initData() {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.clearTempDir();
                saveDeviceInfo();
                FaceConversionUtil.getInstace().getFileText(MyApplication.getContext());
            }
        });
    }

    private void saveDeviceInfo() {
        String appVersion = "";
        String appVersionCode = "";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersion = info.versionName;
            appVersionCode = String.valueOf(info.versionCode);
        } catch(Exception e) {

        }

        TelephonyManager mTm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String phoneBrand = android.os.Build.BRAND;//手机品牌
        String phoneModel = android.os.Build.MODEL; // 手机型号
        String phoneSystem = android.os.Build.VERSION.RELEASE;//系统版本号
        int screenWidth = Device.getWidth();
        int screenHeight = Device.getHeight();
        String netType = NetUtil.getNetType(this);

        // LogUtil.logE(appVersion+"_"+appVersionCode);
        // 卡唯一码
        String IMSI = mTm.getSubscriberId();
        if (IMSI != null && IMSI.length() > 5) {
            IMSI = IMSI.substring(0, 5);
        }

        PhoneDevice device = new PhoneDevice();
        device.setAppVersion(appVersion);
        device.setAppVersionCode(appVersionCode);
        device.setPhoneBrand(phoneBrand);
        device.setPhoneModel(phoneModel);
        device.setPhoneSystemVersion(phoneSystem);
        device.setDeviceIEME(imei);
        device.setWidthHeight(screenWidth + "*" + screenHeight);
        device.setNetTypeValue(netType);
        device.setProviders(IMSI);
        SharedDevice.putDeviceValue(device);
        try {
            ApplicationInfo info = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String metaData = info.metaData.getString("UMENG_CHANNEL");
            setChannel(metaData);
            LogUtil.logE("LogUtil", "metaData---:" + metaData);
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置渠道号
     */
    public void setChannel(String metaData) {
        switch(metaData) {
            case "incompany"://官网
                ConfigControl.publishChannel = "0000";
                break;
            case "tencent"://腾讯应用宝
                ConfigControl.publishChannel = "0002";
                break;
            case "baidu"://百度
                ConfigControl.publishChannel = "0003";
                break;
            case "appchina"://应用汇
                ConfigControl.publishChannel = "0004";
                break;
            case "pp"://PP助手
                ConfigControl.publishChannel = "0005";
                break;
            case "qh360"://360开放平台
                ConfigControl.publishChannel = "0006";
                break;
            case "wandou"://豌豆荚
                ConfigControl.publishChannel = "0007";
                break;
            case "huawei"://华为智汇云
                ConfigControl.publishChannel = "0008";
                break;
            case "lenovo"://联想开发者社区
                ConfigControl.publishChannel = "0009";
                break;
            case "mi"://小米应用商店
                ConfigControl.publishChannel = "0010";
                break;
            case "samsung"://三星应用商店
                ConfigControl.publishChannel = "0011";
                break;
            case "zte"://中兴应用商店
                ConfigControl.publishChannel = "0012";
                break;
            //以下在0627新加
            case "tongbutui"://同步推
                ConfigControl.publishChannel = "0013";
                break;
            case "tianyi"://天翼
                ConfigControl.publishChannel = "0014";
                break;
            case "gfen"://机锋
                ConfigControl.publishChannel = "0015";
                break;
            case "anzhi"://安智
                ConfigControl.publishChannel = "0016";
                break;
            case "meizu"://魅族
                ConfigControl.publishChannel = "0017";
                break;
            case "yidong"://中国移动
                ConfigControl.publishChannel = "0018";
                break;
            case "sogou"://搜狗
                ConfigControl.publishChannel = "0019";
                break;
            case "mumayi"://木蚂蚁
                ConfigControl.publishChannel = "0020";
                break;
            case "itools"://itools
                ConfigControl.publishChannel = "0021";
                break;
            case "oppo"://OPPO
                ConfigControl.publishChannel = "0022";
                break;
            case "googleplay":
                ConfigControl.publishChannel = "0023";
                break;
            case "yingyongbei": //应用贝
                ConfigControl.publishChannel = "0024";
                break;
            case "leshi": //乐视
                ConfigControl.publishChannel = "0025";
                break;
            case "jinli": //金立
                ConfigControl.publishChannel = "0026";
                break;
            case "vivo": //vivo
                ConfigControl.publishChannel = "0027";
                break;
            case "maopao": //冒泡商店
                ConfigControl.publishChannel = "0028";
                break;
            case "nduo": //N多网
                ConfigControl.publishChannel = "0029";
                break;
            case "kupai": //酷派
                ConfigControl.publishChannel = "0030";
                break;
            case "yamaxun": //亚马逊
                ConfigControl.publishChannel = "0031";
                break;
            case "youyi": //优亿市场
                ConfigControl.publishChannel = "0032";
                break;
            case "suning": //苏宁
                ConfigControl.publishChannel = "0033";
                break;
            case "hao123": //hao123开放平台
                ConfigControl.publishChannel = "0034";
                break;
            case "rjdq2345": //2345软件大全
                ConfigControl.publishChannel = "0035";
                break;
            case "jufeng": //聚丰开放平台
                ConfigControl.publishChannel = "0036";
                break;
            case "tongyi": //统一下载站
                ConfigControl.publishChannel = "0037";
                break;
            case "opera": //opera mobile store
                ConfigControl.publishChannel = "0038";
                break;
            case "smartisan": //smartisan开发者中心
                ConfigControl.publishChannel = "0039";
                break;
            case "zol": //zol软件下载
                ConfigControl.publishChannel = "0040";
                break;
            case "sohu": //搜狐应用中心
                ConfigControl.publishChannel = "0041";
                break;
            case "zhuole": //卓乐市场
                ConfigControl.publishChannel = "0042";
                break;
            case "yingyongpai": //应用派
                ConfigControl.publishChannel = "0043";
                break;
            case "haixin": //海信开放平台
                ConfigControl.publishChannel = "0044";
                break;
            case "aiqiyi": //爱奇艺应用商店
                ConfigControl.publishChannel = "0045";
                break;
            case "it168": //it168软件下载
                ConfigControl.publishChannel = "0046";
                break;
            case "wo": //沃商店
                ConfigControl.publishChannel = "0047";
                break;
            case "anbei": //安贝市场
                ConfigControl.publishChannel = "0048";
                break;
            case "kuchuan": //酷传
                ConfigControl.publishChannel = "0049";
                break;
            case "a9": //a9师徒部落版本
                ConfigControl.publishChannel = "0050";
                break;
        }
    }

    public String getUserId() {
        return String.valueOf(SharedToken.getUserId());
    }

    public UserItem getUserItem() {
        return SharedUser.getUserItem();
    }


    public static MyApplication getInstance() {
        return mInstance;
    }

    public static MyApplication getContext() {
        return mInstance;
    }


    public void restartApplication(String msg) {
        //新加的。用来关闭MessageMainAct和ConversationActivity
        EventBus.getDefault().post(new IMEventType(IMEventType.typeIMOtherDevice));

        Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        intent.putExtra("msg", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        MobclickAgent.onKillProcess(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static Context getStblContext() {
        return context;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ImageUtils.clearMemoryCache();
        System.gc();
//        LogUtil.logE("onTrimMemory level = " + level);
    }

    private boolean mNeedShowAd = true;

    public void setNeedShowAd(boolean needShowAd) {
        mNeedShowAd = needShowAd;
    }

    public boolean getNeedShowAd() {
        return mNeedShowAd;
    }

    //各个分享平台的配置，建议放在全局Application或者程序入口
    {
        //final static String wxAppId = "wx2933b730046aeea9";
        //final static String wxSecret = "6d2f70dca202d6b4ba176affba3d1194";

        //final static String qqAppId = "1105104399";//1105026416
        //final static String qqSecret = "KEYU9WHaQt2RJfyOQzx";//9NDdVIxzssCMThVw

        //final static String weiboKey = "974852079";
        //final static String weiboSecret = "684a04b96946440d31b02c79fd83b7e2";

        PlatformConfig.setWeixin("wx2933b730046aeea9", "6d2f70dca202d6b4ba176affba3d1194");
        PlatformConfig.setQQZone("1105104399", "KEYU9WHaQt2RJfyOQzx");
        PlatformConfig.setSinaWeibo("974852079", "684a04b96946440d31b02c79fd83b7e2");
        com.umeng.socialize.Config.IsToastTip = false;
    }
}
