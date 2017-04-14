package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;

import java.util.Map;


/**
 * Created by Administrator on 2016/9/14.
 */

public class SharedOfficeAccount {
    private final static String name = "SharedAccount";

    public static void putOfficeAccount(String userId) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        shared.edit().putString(userId, userId).apply();
    }

    public static boolean isOfficeAccount(String targetId) {

        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        Map<String, ?> value = shared.getAll();
        return value.containsKey(targetId);
    }

    public static void deleteData() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        shared.edit().clear().apply();
    }
}
