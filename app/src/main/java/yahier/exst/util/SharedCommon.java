package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;

/**
 * Created by lenovo on 2016/4/23.
 */
public class SharedCommon {
    final static String tag = "SharedCommon";
    final static String nameKey = "common";
    final static String keyIp = "keyIp";
    final static String keyCityTreeUpdateTime = "cityTreeUpdateTime";
    final static String keyDict = "dictUpdateTime";

    //是否有新动态提醒
    public final static String keyIsGotNewStatusesNotify = "keyStatuses";


    public static void putIp(Context mContext, String ip) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyIp, "https://" + ip + "/").commit();
        LogUtil.logE(tag, "保存" + ip);
    }

    public static String getIp(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        String ip = shared.getString(keyIp, Config.hostMain);
        LogUtil.logE(tag, "get:" + ip);
        return ip;
    }


    public static void putStatusesNotify(boolean value) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putBoolean(keyIsGotNewStatusesNotify, value).commit();
    }


    public static boolean getStatusesNotify() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean value = shared.getBoolean(keyIsGotNewStatusesNotify, false);
        return value;
    }


    public static void putCityTreeUpdateTime() {
        String currentTime = String.valueOf(System.currentTimeMillis() / 1000);
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyCityTreeUpdateTime, currentTime).commit();
    }


    public static String getCityTreeUpdateTime() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        String value = shared.getString(keyCityTreeUpdateTime, "0");
        return value;
    }

    //字典更新时间
    public static void putDictUpdateTime() {
        String currentTime = String.valueOf(System.currentTimeMillis() / 1000);
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyDict, currentTime).commit();
    }

    //还原更新时间为0
    public static void backDictUpdateTime() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyDict, "0").commit();
    }


    public static String getDictUpdateTime() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        String value = shared.getString(keyDict, "0");
        return value;
    }

}
