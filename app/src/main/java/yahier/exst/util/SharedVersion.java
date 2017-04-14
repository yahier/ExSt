package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.EnterAd;
import com.stbl.stbl.util.database.DataCacheDB;

/**
 * Created by yahier on 17/1/21.
 * 记录当前用户版本信息
 */

public class SharedVersion {
    final static String name = "SharedVersion";
    final static String keyVersionCode = "version";
    static int versionCodeCurrent;

    static {
        try {
            PackageManager manager = MyApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            versionCodeCurrent = info.versionCode;
        } catch (Exception e) {

        }
    }


    //是否首次进入新版本.如果旧版本和当前取得的code不同,就是首次进入
    public static void checkFirstNewVersion() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        LogUtil.logE("SharedVersion", shared.getInt(keyVersionCode, 0) + ":" + versionCodeCurrent);
        if (shared.getInt(keyVersionCode, 0) != versionCodeCurrent) {
            exeInit();
        }
        shared.edit().putInt(keyVersionCode, versionCodeCurrent).apply();
    }


    private static void exeInit() {
        LogUtil.logE("SharedVersion", "exeInit");
        new DataCacheDB(MyApplication.getContext()).deleteAllData();

    }

}
