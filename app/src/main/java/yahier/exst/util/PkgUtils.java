package yahier.exst.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.stbl.stbl.common.MyApplication;

import java.io.File;

public class PkgUtils {

    public static String sUserAgent;

    public static void installApk(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            MyApplication.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized static String getUserAgent() {
        if (TextUtils.isEmpty(sUserAgent)) {
            sUserAgent = buildUserAgent();
        }
        return sUserAgent;
    }

    public static String buildUserAgent() {
        StringBuilder builder = new StringBuilder();
        builder.append("STBL Android:")
                .append(getVersionName())
                .append(";")
                .append(Build.BRAND)
                .append(";")
                .append(Build.MODEL)
                .append(";")
                .append(Build.VERSION.RELEASE)
                .append(";")
                .append(getDeviceId());
        return builder.toString();
    }

    public static int getVersionCode() {
        int versionCode = 0;
        try {
            PackageInfo info = MyApplication.getContext().getPackageManager().getPackageInfo(
                    MyApplication.getContext().getPackageName(), Context.MODE_PRIVATE);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName() {
        String versionName = "";
        try {
            PackageInfo info = MyApplication.getContext().getPackageManager().getPackageInfo(
                    MyApplication.getContext().getPackageName(),
                    Context.MODE_PRIVATE);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getDeviceId() {
        TelephonyManager manager = (TelephonyManager) MyApplication
                .getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

}
