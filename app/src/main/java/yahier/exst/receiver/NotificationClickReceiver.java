package yahier.exst.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.UpdateManager;

/**
 * Created by tnitf on 2016/7/8.
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION.UPDATE_NOTIFICATION_CLICK)) {
            String url = (String) SharedPrefUtils.getFromPublicFile(KEY.LATEST_APK_URL, "");
            int versionCode = (int) SharedPrefUtils.getFromPublicFile(KEY.LATEST_VERSION_CODE, 0);
            if (!TextUtils.isEmpty(url) && versionCode != 0) {
                UpdateManager.getInstance().update(url, versionCode, true);
            }
        }
    }
}
