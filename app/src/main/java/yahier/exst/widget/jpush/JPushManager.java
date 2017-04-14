package yahier.exst.widget.jpush;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedToken;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by meteorshower on 16/3/4.
 */
public class JPushManager {

    private final String TAG = JPushManager.this.getClass().getSimpleName();

    public final String KEY_APP_KEY = "JPUSH_APPKEY";
    private static JPushManager jpushManager = null;

    public static JPushManager getInstance() {
        if (jpushManager == null)
            jpushManager = new JPushManager();
        triedTimes = 0;
        return jpushManager;
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    public void startPush() {
        JPushInterface.init(MyApplication.getStblContext());
    }

    public void stopPush() {
        JPushInterface.stopPush(MyApplication.getStblContext());
    }

    public void resumePush() {
        JPushInterface.resumePush(MyApplication.getStblContext());
    }

    static int triedTimes = 0;
    final int tryMaxTimes = 3;

    public void setAlias(final Context context) {
        String userId = SharedToken.getUserId(MyApplication.getContext());
        //LogUtil.logE(TAG, "User Id : " + userId);
        try {
            JPushInterface.setAlias(MyApplication.getContext(), userId, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    if (i != 0) {//绑定失败
                        LogUtil.logE(TAG, "Set Alias is Failture!!" + i + " -- " + s);
                        triedTimes++;
                        if (triedTimes < tryMaxTimes) {
                            setAlias(context);
                        }
                    } else {
                        LogUtil.logE(TAG, "Set Alias is Success!!");
                        JPushManager.getInstance().commentJpushDeviceId(context);
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.logE(TAG + "—exception", e.getLocalizedMessage().toString());
            // Log.e("JpushInterface——exception", e.getLocalizedMessage().toString());
        }
    }

    public void clearAlias(final Context context) {
        //LogUtil.logE(TAG, "clearAlias");
        try {
            JPushInterface.setAlias(MyApplication.getContext(), "", new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    if (i != 0) {//绑定失败
                        //LogUtil.logE(TAG, "clearAlias is Failture!!" + i + " -- " + s);
                        triedTimes++;
                        if (triedTimes < tryMaxTimes) {
                            clearAlias(context);
                        } else {
                            stopPush();
                        }
                    } else {
                        //LogUtil.logE(TAG, "clearAlias is Success!!");
                    }
                    //JPushManager.getInstance().commentJpushDeviceId(context);
                }
            });
        } catch (Exception e) {
            //e.printStackTrace();
           // LogUtil.logE(TAG+"—exception", e.getLocalizedMessage().toString());
        }
    }


    public void setTag(final Context context, final int roomId) {
        if (context == null) return;
        Set<String> set = new HashSet<String>();
        set.add(String.valueOf(roomId));
        set.add(getDeviceId(context));

        JPushInterface.setTags(context, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i != 0) {
                   // Log.e(TAG, "Set Tag is Failture!!" + i + " -- " + s);
                    setTag(context, roomId);
                } else {
                   // Log.e(TAG, "Set Tag is Success!!");
                }
            }
        });
    }

    // 取得版本号
    public String getVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    //取得DeviceId
    public String getDeviceId(Context context) {
        String deviceId = JPushInterface.getUdid(context);
        return deviceId;
    }

    //获取IMEI串号
    public String getImei(Context context, String imei) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
           // Log.e(TAG, e.getMessage());
        }
        return imei;
    }

    // 取得AppKey
    public String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return appKey;
    }

    public void setPushNotification() {

        //自定义Layout通知
//        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(PushSetActivity.this, R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
//        builder.layoutIconDrawable = R.drawable.ic_launcher;
//        builder.developerArg0 = "developerArg2";
//        JPushInterface.setPushNotificationBuilder(2, builder);

        //通用基础通知
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(PushSetActivity.this);
//        builder.statusBarDrawable = R.drawable.ic_launcher;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
//        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
//        JPushInterface.setPushNotificationBuilder(1, builder);

    }

//设置Tag
//    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
    //调用JPush api设置Push时间
//    JPushInterface.setPushTime(getApplicationContext(), days, startime, endtime);

    //同步推送设备号
    public void commentJpushDeviceId(Context context) {
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag(context));
        if (UserRole.isTemp(roleFlag)) {
            return;
        }
        LogUtil.logE("JPushManager", "commentJpushDeviceId");
        JSONObject json = new JSONObject();
        json.put("deviceid", JPushInterface.getRegistrationID(context));
        new HttpEntity(context).commonPostJson(Method.jpushCommentDeviceId, json.toJSONString(), new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                LogUtil.logE("JPushManager", result);
            }
        });
    }
}
