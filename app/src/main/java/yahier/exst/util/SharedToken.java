package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.AuthToken;

public class SharedToken {
    final static String nameKey = "token";
    public final static String roleSeller = "3";
    public final static String roleSellerMaotai = "4";
    final static String tokenKey = "token";
    final static String refreshTokenKey = "refreshTokenKey";
    final static String userIdKey = "userId";
    final static String rongyunTokenKey = "rongyunToken";
    final static String roleFlagKey = "roleFlagKey";
    final static String masterIdKey = "masterIdKey";
    final static String roomId = "roomid";
    final static String ownerId = "ownerId";
    final static String keyTimeSaveAccessToken = "keyTimeSaveAccessToken";//保存时间
    final static String keyExpiriestime = "expiriestime";

    public static void putTokenValue(AuthToken token) {
        long now = System.currentTimeMillis() / 1000;
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(tokenKey, token.getAccesstoken())
                .putString(refreshTokenKey, token.getRefreshtoken())
                .putString(userIdKey, token.getUserid())
                .putString(rongyunTokenKey, token.getRongyuntoken())
                .putString(roleFlagKey, token.getRoleflag())
                .putLong(keyExpiriestime, token.getExpiriestime()).putLong(keyTimeSaveAccessToken, now)
                .commit();
        if (token.getUserinfo() != null) {
            shared.edit().putString(masterIdKey, String.valueOf(token.getUserinfo().getMasterid())).commit();

        }
    }


    public static AuthToken getAccessToken() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        AuthToken token = new AuthToken();
        token.setAccesstoken(shared.getString(tokenKey, ""));
        token.setExpiriestime(shared.getLong(keyExpiriestime, 0));
        token.setRefreshtoken(shared.getString(refreshTokenKey, ""));
        token.setUserid(shared.getString(userIdKey, ""));
        token.setRongyuntoken(shared.getString(rongyunTokenKey, ""));
        token.setRoleflag(shared.getString(roleFlagKey, ""));
        return token;
    }

    public static void putValue(Context mContext, String token,
                                String refreshToken, String userId,
                                String rongyunToken, String roleFlag,
                                String masterId, long expirestime) {
        if (mContext == null) return;
        long now = System.currentTimeMillis() / 1000;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(tokenKey, token)
                .putString(refreshTokenKey, refreshToken)
                .putString(userIdKey, userId)
                .putString(rongyunTokenKey, rongyunToken)
                .putString(roleFlagKey, roleFlag)
                .putString(masterIdKey, masterId).putLong(keyExpiriestime, expirestime).putLong(keyTimeSaveAccessToken, now)
                .commit();
        //LogUtil.logE("LogUtil", "rolefalg:" + roleFlag);
    }


    public static void putValue(Context mContext, String token, String refreshToken, String userId, String roleFlag, long expirestime
    ) {
        if (mContext == null) return;
        long now = System.currentTimeMillis() / 1000;
        //LogUtil.logE("SharedToken", now + "");
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(tokenKey, token).putString(refreshTokenKey, refreshToken).putLong(keyExpiriestime, expirestime)
                .putString(userIdKey, userId)
                .putString(roleFlagKey, roleFlag).putLong(keyTimeSaveAccessToken, now)
                .commit();

    }

    public static void putMasterId(Context mContext, String masterId) {
        if (mContext == null) return;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(masterIdKey, masterId).commit();
    }


    public static void putRoleFlag(Context mContext, String roleFlag) {
        if (mContext == null) return;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putString(roleFlagKey, roleFlag).commit();
    }


    public static String getRefreshToken(Context... con) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(refreshTokenKey, "");
    }

    //
//    //获取融云token
    public static String getRongyunToken(Context... con) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(rongyunTokenKey, "");
    }

    //
    public static String getToken(Context... con) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(tokenKey, "");
    }

    //
    public static String getUserId(Context... con) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(userIdKey, "0");
    }

    //
    public static String getRoleFlag(Context... con) {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getString(roleFlagKey, "-1");
    }


//    public static long getMasteridkey(Context mContext) {
//        if (mContext == null) return 0;
//        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
//        try {
//            return Long.parseLong(shared.getString(masterIdKey, "0"));
//        } catch (Exception e) {
//            return 0;
//        }
//    }



    /**
     * 用于跟悬浮球服务是否启动，判断是否已经启动了房间
     * 是自己创建的房间，则跳回房间
     *
     * @param mContext
     * @param ownerid
     */
    public static void putOwnerId(Context mContext, long ownerid) {
        if (mContext == null) return;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        shared.edit().putLong(ownerId, ownerid).commit();
    }

    /**
     * 用于跟悬浮球服务是否启动，判断是否已经启动了房间
     * * 是自己创建的房间，则跳回房间
     *
     * @param mContext
     * @return
     */
    public static long getOwnerId(Context mContext) {
        if (mContext == null) return 0;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getLong(ownerId, 0);
    }

    /**
     * 在三个场景调用。退出账号，接口error.融云提示账号在其它设备上登录
     * @return
     */
    public static boolean clearToken() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.edit().clear().commit();
    }


    public static long getExpireTime(Context mContext) {
        if (mContext == null) return 0;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getLong(keyExpiriestime, 0);
    }


    //获取accessToken的保存时间
    public static long getRecordTime(Context mContext) {
        if (mContext == null) return 0;
        SharedPreferences shared = mContext.getSharedPreferences(nameKey, Context.MODE_PRIVATE);
        return shared.getLong(keyTimeSaveAccessToken, 0);
    }

    /**
     * 是否在有效期内。返回false，就要重新登录
     *
     * @param mContext
     * @return
     */
    public static boolean isTokenValid(Context mContext) {
        long time = System.currentTimeMillis() / 1000 - getRecordTime(mContext);
        boolean isValid;
        if (time < getExpireTime(mContext)) {
            isValid = true;
        } else {
            isValid = false;
        }
        LogUtil.logE("isTokenValidByExpireTime", "" + isValid);
        return isValid;
    }

    /**
     * 是否在24小时之内刷新过。没有则需要刷新token
     *
     * @param mContext
     * @return
     */
    public static boolean isFreshedIn24Hours(Context mContext) {
        LogUtil.logE("isTokenValidByExpireTime", getRecordTime(mContext) + "");
        long passedTime = System.currentTimeMillis() / 1000 - getRecordTime(mContext);
        boolean isValid;
        if (passedTime < 24 * 3600) {
            isValid = true;
        } else {
            isValid = false;
        }
        LogUtil.logE("isFreshedIn24Hours", "" + isValid);
        return isValid;//
    }


}
