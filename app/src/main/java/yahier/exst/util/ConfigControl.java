package yahier.exst.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.stbl.stbl.R;

/**
 * 控制运行环境的域名
 * Created by lenovo on 2016/5/2.
 */
public class ConfigControl {
    private final static int typeDev = 1; //开发
    private final static int typeTest2 = 2; //测试
    private final static int typeProduce = 3; //生产
    private final static int typePrePublish = 4; //预发布

    private static int type = typeProduce;
    //开发环境
    private static String devhostUploadWeiboImg = "https://d1-upload.shifugroup.net/";
    private static String devhostMain = "https://d1-api.shifugroup.net/";
    private static String devhostWap = "https://d1-wap.shifugroup.net/";
    public static String  devhostMainRedPacket = "https://d1-api-high.shifugroup.net/";
    /// 测试环境.也是版本提交环境
    private static String testhostUploadWeiboImg = "https://t1-upload.shifugroup.net/";
    private static String testhostMain = "https://t1-api.shifugroup.net/";
    private static String testhostWap = "https://t1-wap.shifugroup.net/";
    public static String  testhostMainRedPacket = "https://t1-api-high.shifugroup.net/";
    // 生产环境
    private static String producehostUploadWeiboImg = "https://upload.shifugroup.net/";
    private static String producehostMain = "https://api.shifugroup.net/";
    private static String producehostWap = "https://app-wap.shifugroup.net/";
    public static String  producehostMainRedPacket = "https://api-high.shifugroup.net/";
    //预发布版本
    private static String prePublishhostUploadWeiboImg = "https://pre-upload.shifugroup.net/";
    private static String prePublishhostMain = "https://pre-api.shifugroup.net/";
    public static String  prePublishhostMainRedPacket = "https://pre-api-high.shifugroup.net/";
    public static String publishChannel = "0000";//发布渠道号 0000官网，0002tencent
    public static boolean logable = true;  //log开关
    public static boolean switchLang = false; //切换app语言，不上英文版就false

    public static String packageName = "com.stbl.stbl";

    public static void switchHost() {
        switch (type) {
            case typeDev:
                Config.hostMain = devhostMain;
                Config.hostUploadWeiboImg = devhostUploadWeiboImg;
                Config.hostMainRedPacket = devhostMainRedPacket;
                break;
            case typeTest2:
                Config.hostMain = testhostMain;
                Config.hostUploadWeiboImg = testhostUploadWeiboImg;
                Config.hostMainRedPacket = testhostMainRedPacket;
                break;
            case typeProduce:
                Config.hostMain = producehostMain;
                Config.hostUploadWeiboImg = producehostUploadWeiboImg;
                Config.hostMainRedPacket = producehostMainRedPacket;
                //logable = false;
                break;
            case typePrePublish:
                Config.hostMain = prePublishhostMain;
                Config.hostUploadWeiboImg = prePublishhostUploadWeiboImg;
                Config.hostMainRedPacket = prePublishhostMainRedPacket;
                //logable = false;
                break;

        }
    }


    public static String getAboutVersionTime(Context mContext) {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return "V " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return (mContext.getString(R.string.common_version_know));
        }
    }


}
