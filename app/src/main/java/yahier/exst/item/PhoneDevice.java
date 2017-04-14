package yahier.exst.item;

import android.content.Context;

import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.LogUtil;

/**
 * Created by Administrator on 2016/8/17.
 */

public class PhoneDevice {
    String AppVersion;
    String appVersionCode;
    String phoneBrand;
    String phoneModel;
    String phoneSystemVersion;
    String deviceIEME;
    String widthHeight;
    String netTypeValue;
    String providers;

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(String providers) {
        this.providers = providers;
    }

    public String getNetTypeValue() {
        return netTypeValue;
    }

    public void setNetTypeValue(String netTypeValue) {
        this.netTypeValue = netTypeValue;
    }

    public String getWidthHeight() {
        return widthHeight;
    }

    public void setWidthHeight(String widthHeight) {
        this.widthHeight = widthHeight;
    }

    public String getDeviceIEME() {
        return deviceIEME;
    }

    public void setDeviceIEME(String deviceIEME) {
        this.deviceIEME = deviceIEME;
    }

    public String getPhoneSystemVersion() {
        return phoneSystemVersion;
    }

    public void setPhoneSystemVersion(String phoneSystemVersion) {
        this.phoneSystemVersion = phoneSystemVersion;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }


    public  String toString(Context mContext) {
        if (mContext == null) return "";
        StringBuffer sb = new StringBuffer();
        //<手机/终端型号>/<软件版本号>;<构建号>(<终端操作系统>;<终端操作系统版本号>;<屏幕尺寸>;<联网方式>;<渠道编号>;<运营商>;<唯一识别码>;<破解>)
        //Sony L50t/stbl-app;1.0.1;10(Android;4.4.2;1080*1776;wifi;0000;46000;352787060282742)
        String SDFGH = phoneBrand+" "+phoneModel+"/stbl-app"+";"+AppVersion+";"+appVersionCode+"(Android;"+phoneSystemVersion+ ";"+widthHeight+";"+netTypeValue+";"+ ConfigControl.publishChannel+";"+providers+";"+deviceIEME+")";
        LogUtil.logE("PhoneDevice", SDFGH);
        return SDFGH;
    }
}
