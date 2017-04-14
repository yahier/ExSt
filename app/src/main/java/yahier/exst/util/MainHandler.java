package yahier.exst.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by lr on 2016/1/22.
 */
public class MainHandler {

    private static volatile MainHandler instance;

    public static MainHandler getInstance() {
        if (instance == null) {
            synchronized (MainHandler.class) {
                if (instance == null) {
                    instance = new MainHandler();
                }
            }
        }
        return instance;
    }

    private Handler mHandler;

    private MainHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void postDelay(Runnable r, long delayMillis) {
        mHandler.postDelayed(r, delayMillis);
    }
}
