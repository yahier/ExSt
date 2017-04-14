package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.EnterAd;

/**
 * Created by lenovo on 2016/5/11
 */
public class SharedAd {
    final static String tag = "SharedAd";
    final static String nameKey = "ad";
    final static String keyAdId = "keyid";
    final static String keyAdImguRL = "keyImgUrl";
    final static String keyAdDLinkUrl = "keyadlinkurl";
    final static String keyAdDuration = "keyduration";


    public static void putAd(EnterAd ad) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(keyAdId, ad.getAdid()).putString(keyAdImguRL, ad.getAdimgurl()).putString(keyAdDLinkUrl, ad.getAdlinkurl()).putLong(keyAdDuration, ad.getDuration()).commit();
    }

    public static EnterAd getAd() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        EnterAd ad = new EnterAd();
        ad.setAdid(shared.getString(keyAdId, ""));
        ad.setAdimgurl(shared.getString(keyAdImguRL, ""));
        ad.setAdlinkurl(shared.getString(keyAdDLinkUrl, ""));
        ad.setDuration(shared.getLong(keyAdDuration, 0));
        return ad;
    }


    public static void delete(Context mContext){
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().clear();
    }


}
