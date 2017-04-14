package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.PhoneDevice;

/**
 * Created by lenovo on 2016/3/23.
 * 保存设备信息 .已经废弃
 */
public class SharedDevice {
    final static String tag = "SharedDevice";
    final static String nameKey = "device";
    final static String keyAppVersion = "keyAppVersion";//app版本名
    final static String keyAppVersionCode = "keyAppVersionCode";//app版本号
    final static String keyPhoneBrand = "keyPhoneBrand";//手机品牌
    final static String keyPhoneModel = "keyPhoneModel";//手机型号
    final static String keyPhoneSystemVersion = "keyPhoneSystemVersion";//系统版本
    final static String keyDeviceIEME = "keyDeviceIEME";//ieme码
    final static String keyWidthHeight = "keyWidthHeight";  //屏幕宽高
    final static String keyNetType = "keyNetType";
    final static String keyProviders = "keyProviders";//运营商代码


//    public static void putValue(Context mContext, String AppVersion,String appVersionCode,
//                                String phoneBrand, String phoneModel, String phoneSystemVersion,
//                                String deviceIEME, String widthHeight,String netTypeValue,String providers) {
//        if (mContext == null) return;
//        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
//        shared.edit().putString(keyAppVersion, AppVersion)
//                .putString(keyPhoneBrand, phoneBrand).putString(keyPhoneModel, phoneModel).putString(keyAppVersionCode, appVersionCode)
//                .putString(keyPhoneSystemVersion, phoneSystemVersion)
//                .putString(keyDeviceIEME, deviceIEME).putString(keyWidthHeight, widthHeight).putString(keyNetType,netTypeValue)
//                .putString(keyProviders,providers)
//                .commit();
//    }

    public static void putDeviceValue(PhoneDevice device) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyAppVersion, device.getAppVersion())
                .putString(keyPhoneBrand, device.getPhoneBrand()).putString(keyPhoneModel, device.getPhoneModel()).putString(keyAppVersionCode, device.getAppVersionCode())
                .putString(keyPhoneSystemVersion, device.getPhoneSystemVersion())
                .putString(keyDeviceIEME, device.getDeviceIEME()).putString(keyWidthHeight, device.getWidthHeight()).putString(keyNetType, device.getNetTypeValue())
                .putString(keyProviders, device.getProviders())
                .commit();
    }


    public static String toDeviceString() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        StringBuffer sb = new StringBuffer();
        PhoneDevice device = new PhoneDevice();
        device.setProviders(shared.getString(keyProviders, ""));
        device.setPhoneBrand(shared.getString(keyPhoneBrand, ""));
        device.setPhoneModel(shared.getString(keyPhoneModel, ""));
        device.setAppVersion(shared.getString(keyAppVersion, ""));
        device.setAppVersionCode(shared.getString(keyAppVersionCode, ""));
        device.setPhoneSystemVersion(shared.getString(keyPhoneSystemVersion, ""));
        device.setDeviceIEME(shared.getString(keyDeviceIEME, ""));
        device.setWidthHeight(shared.getString(keyWidthHeight, ""));
        device.setNetTypeValue(shared.getString(keyNetType, ""));


        //<手机/终端型号>/<软件版本号>;<构建号>(<终端操作系统>;<终端操作系统版本号>;<屏幕尺寸>;<联网方式>;<渠道编号>;<运营商>;<唯一识别码>;<破解>)
        //Sony L50t/stbl-app;1.0.1;10(Android;4.4.2;1080*1776;wifi;0000;46000;352787060282742)
        String SDFGH = device.getPhoneBrand() + " " + device.getPhoneModel() + "/stbl-app" + ";" + device.getAppVersion() + ";" + device.getAppVersionCode() + "(Android;" + device.getPhoneSystemVersion() + ";" + device.getWidthHeight() + ";" + device.getNetTypeValue() + ";" + ConfigControl.publishChannel + ";" + device.getProviders() + ";" + device.getDeviceIEME() + ")";
        //LogUtil.logE(tag, SDFGH);
        return SDFGH;
    }


    private static String getProviders(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyProviders, "");
    }

    public static void putNet(Context mContext, String netTypeValue) {
        if (mContext == null) return;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyNetType, netTypeValue).commit();

    }


    private static String getAppVersion(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyAppVersion, "");
    }

    private static String getAppVersionCode(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyAppVersionCode, "");
    }


    public static String getPhoneBrand(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyPhoneBrand, "");
    }

    public static String getPhoneModel(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyPhoneModel, "");
    }

    private static String getPhoneSystemVersion(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyPhoneSystemVersion, "");
    }

    public static String getPhoneIEME() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyDeviceIEME, "");
    }


    private static String getScreenWidthHeight(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyWidthHeight, "");
    }

    private static String getNetType(Context mContext) {
        if (mContext == null) return "";
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyNetType, "");
    }


    public static String getNet(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(keyNetType, "");
    }

//
//    public static String toString(Context mContext) {
//        if (mContext == null) return "";
//        StringBuffer sb = new StringBuffer();
//        //<手机/终端型号>/<软件版本号>;<构建号>(<终端操作系统>;<终端操作系统版本号>;<屏幕尺寸>;<联网方式>;<渠道编号>;<运营商>;<唯一识别码>;<破解>)
//        //Sony L50t/stbl-app;1.0.1;10(Android;4.4.2;1080*1776;wifi;0000;46000;352787060282742)
//        String SDFGH = getPhoneBrand(mContext) + " " + getPhoneModel(mContext) + "/stbl-app" + ";" + getAppVersion(mContext) + ";" + getAppVersionCode(mContext) + "(Android;" + getPhoneSystemVersion(mContext) + ";" + getScreenWidthHeight(mContext) + ";" + getNetType(mContext) + ";" + ConfigControl.publishChannel + ";" + getProviders(mContext) + ";" + getPhoneIEME(mContext) + ")";
//        LogUtil.logE(nameKey, SDFGH);
//        return SDFGH;
//    }

}
