package yahier.exst.util;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/9/2.
 */

public class ReportUtils {

    /**
     * 上报群发消息成功
     */
    public static void reportQFXX(Activity activity, boolean useSelectAll) {
        MobclickAgent.onEvent(activity, KEY.QFXXCS);//上报群发消息次数
        if (useSelectAll) {
            MobclickAgent.onEvent(activity, KEY.FZQXCS);//上报分组全选次数
        }
        long time = (long) SharedPrefUtils.getFromUserFile(KEY.QFXXRS_REPORT_TIME, 0L);
        if (!DateUtil.isToday(time)) {
            MobclickAgent.onEvent(activity, KEY.QFXXRS);//上报群发消息人数
            if (useSelectAll) {
                MobclickAgent.onEvent(activity, KEY.FZQXRS);//上报分组全选人数
            }
            SharedPrefUtils.putToUserFile(KEY.QFXXRS_REPORT_TIME, System.currentTimeMillis());
        }
    }

}
