package yahier.exst.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by tnitf on 2016/3/11.
 */
public class LocalBroadcastHelper {

    private static volatile LocalBroadcastHelper sInstance;

    public static LocalBroadcastHelper getInstance() {
        if (sInstance == null) {
            synchronized (LocalBroadcastHelper.class) {
                if (sInstance == null) {
                    sInstance = new LocalBroadcastHelper();
                }
            }
        }
        return sInstance;
    }

    private Context mAppContext;
    private LocalBroadcastManager mManager;

    private LocalBroadcastHelper() {

    }

    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null!");
        }
        mAppContext = context.getApplicationContext();
        mManager = LocalBroadcastManager.getInstance(mAppContext);
    }

    public void send(Intent intent) {
        mManager.sendBroadcast(intent);
    }

    public void sendSync(Intent intent) {
        mManager.sendBroadcastSync(intent);
    }

    public void register(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        mManager.registerReceiver(receiver, filter);
    }

    public void register(BroadcastReceiver receiver, IntentFilter filter) {
        mManager.registerReceiver(receiver, filter);
    }


    public void unregister(BroadcastReceiver receiver) {
        mManager.unregisterReceiver(receiver);
    }

}
