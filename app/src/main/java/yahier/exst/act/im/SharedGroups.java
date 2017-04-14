package yahier.exst.act.im;

import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.LogUtil;

public class SharedGroups {
    public static final int showhongbaoOff = 0;
    public static final int showhongbaoOn = 1;
  //  private final static String secretaryUserId = "1380013800";
    /**
     * 临时存储师傅和我帮会的id.
     */

    private final static String name = "tempGroups";
    private final static String groupIdMasterKey = "groupIdMaster";
    private final static String groupIdMineKey = "groupIdMine";
  //  private final static String xiaoMishuIdKey = "xiaoMishuId";
  //  private final static String payHelper = "payHelper";
    private final static String hongbaoSwitchKey = "hongbaoSwitchKey";


    public static String getMasterGroupId(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        String tempgroupId = shared.getString(groupIdMasterKey, "");
        return tempgroupId;
    }

    public static String getMineGroupId(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        String tempgroupId = shared.getString(groupIdMineKey, "");
        return tempgroupId;
    }

//    public static String getPayHelperId(Context mContext) {
//        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
//        String tempgroupId = shared.getString(payHelper, "");
//        return tempgroupId;
//    }

    // 上面读取 ，下面存放

    public static void putMasterGroupId(Context mContext, String groupIdOfMaster) {
        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        shared.edit().putString(groupIdMasterKey, groupIdOfMaster).apply();
    }

    public static void putMineGroupId(Context mContext, String groupIdOfMe) {
        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        shared.edit().putString(groupIdMineKey, groupIdOfMe).apply();
    }


//    //在im中判断是否官方账号，现在只判断是否 小秘书或者支付助手
//    public static boolean isOfficeAccount(String targetId, Context mContext) {
//        if (getXiaoMishuId(mContext).equals(targetId) || getPayHelperId(mContext).equals(targetId)) {
//            return true;
//        }
//        return false;
//    }

    //在im中判断是否是我或者我师傅的群
    public static boolean isGroupMineOrMaster(String targetId, Context mContext) {
        if (getMasterGroupId(mContext).equals(targetId) || getMineGroupId(mContext).equals(targetId)) {
            return true;
        }
        return false;
    }

    //小秘书
//    public static void putXiaoMishuId(Context mContext, String xiaomishuId) {
//        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
//        if (xiaomishuId == null) {
//            xiaomishuId = secretaryUserId;
//        }
//        shared.edit().putString(xiaoMishuIdKey, xiaomishuId).apply();
//    }
//
//    //存放支付助手
//    public static void putPayHelper(Context mContext, String helperId) {
//        LogUtil.logE("putPayHelper","helperId:"+helperId);
//        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
//        shared.edit().putString(payHelper, helperId).apply();
//    }


//    public static String getXiaoMishuId(Context mContext) {
//        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
//        String tempgroupId = shared.getString(xiaoMishuIdKey, secretaryUserId);
//        return tempgroupId;
//    }

    //红包开关
    public static void putHongbaoSwitch(Context mContext, int hongbaoSwitch) {
        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        shared.edit().putInt(hongbaoSwitchKey, hongbaoSwitch).apply();
    }

    public static int getHongbaoSwitch(Context mContext) {
        SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        int tempgroupId = shared.getInt(hongbaoSwitchKey, showhongbaoOff);
        return tempgroupId;
    }


    public static void clearGroups() {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(name, Context.MODE_PRIVATE).edit();
        editor.remove(groupIdMasterKey).remove(groupIdMineKey).apply();
    }



}
