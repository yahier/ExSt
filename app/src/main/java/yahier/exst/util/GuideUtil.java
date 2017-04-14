package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lenovo on 2016/5/4.
 */
public class GuideUtil {

    final static String nameKey = "SharedGuide";
    final static String keyPageHome = "keyPageHome";
    final static String keyPageStatuses = "keyPageStatuses";
    final static String keyPageIM = "keyPageIM";
    final static String keyPageMine = "keyPageMine";
    final static String keyPageMall = "keyPageMall";

    public static boolean guidePage1(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean guide = shared.getBoolean(keyPageHome, false);
        if (!guide)
            shared.edit().putBoolean(keyPageHome, true).commit();
        return guide;
    }



    public static boolean isGuidePage1Finished(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean guide = shared.getBoolean(keyPageHome, false);
        return guide;
    }

    public static void guidePage1Finished(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putBoolean(keyPageHome, true).commit();
    }

    public static boolean guidePage2(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean guide = shared.getBoolean(keyPageStatuses, false);
        if (!guide)
            shared.edit().putBoolean(keyPageStatuses, true).commit();
        return guide;
    }

    public static boolean guidePage3(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean guide = shared.getBoolean(keyPageIM, false);
        if (!guide)
            shared.edit().putBoolean(keyPageIM, true).commit();
        return guide;
    }

    public static boolean guidePage4(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean guide = shared.getBoolean(keyPageMine, false);
        if (!guide)
            shared.edit().putBoolean(keyPageMine, true).commit();
        return guide;
    }

    public static boolean guidePageMall(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        boolean guide = shared.getBoolean(keyPageMall, false);
        if (!guide)
            shared.edit().putBoolean(keyPageMall, true).commit();
        return guide;
    }

}
