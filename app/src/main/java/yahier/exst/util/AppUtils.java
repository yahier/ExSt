package yahier.exst.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.common.MyApplication;

import java.util.List;
import java.util.UUID;

/**
 * 公用的工具方法
 * Created by Administrator on 2016/4/20 0020.
 */
public class AppUtils {
    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {

        return isInstallApk(context, "com.tencent.mm");
    }

    /**
     * 判断是否安装了某个app
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstallApk(Context context, String packageName) {
        if (context == null || packageName == null) return false;
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void routerActivity(Activity activity, String url, String title) {
        if (url.contains("&ex=1&")) {
            String[] arr = url.split("&ex=1&");
            String router = "stbl://stbl/?" + arr[arr.length - 1];
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //startActivity(intent);
            Intent intent = new Intent(activity, CommonWebInteact.class);
            intent.setData(Uri.parse(router));
            activity.startActivity(intent);
        } else if (url.startsWith("stbl://")) {
            Intent intent = new Intent(activity, CommonWebInteact.class);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, CommonWeb.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            activity.startActivity(intent);
        }
    }

    /**
     * 是否完成app功能介绍引导
     *
     * @return
     */
    public static boolean isFinishIntroduce() {
        boolean isFinish = (boolean) SharedPrefUtils.getFromPublicFile(KEY.FINISH_INTRODUCE, false);
        return isFinish;
    }

    public static void setFinishIntroduce() {
        SharedPrefUtils.putToPublicFile(KEY.FINISH_INTRODUCE, true);
    }

    /**
     * 获取APP分配得到的总内存大小，单位是MB（例如我的华为手机，app分配得到128MB内存）
     *
     * @return
     */
    public static int getMemorySize() {
        int maxMemory = ((ActivityManager) MyApplication.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        return maxMemory;
    }

    /**
     * 判断程序是否在运行
     */
    public static boolean isTopActivity(Context context) {
        String packageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            //应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static String getCurrLang() {
        String lang = "cn";
        boolean isEnglish = (boolean) SharedPrefUtils.getFromPublicFile(KEY.IS_ENGLISH, false);
        if (isEnglish) {
            lang = "en";
        }
        return lang;
    }

    public static String getGUID() {
        String uuid = UUID.randomUUID().toString();
        String guid = uuid.replaceAll("-", "").toLowerCase() + System.currentTimeMillis();
        return guid;
    }
}
