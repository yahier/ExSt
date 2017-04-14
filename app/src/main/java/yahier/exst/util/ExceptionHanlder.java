package yahier.exst.util;

import android.content.Context;

/**
 * Created by lenovo on 2016/7/20.
 * 可以参照 RongExceptionHandler
 */
public class ExceptionHanlder implements Thread.UncaughtExceptionHandler {
    final String TAG = getClass().getSimpleName();
    Context mContext;

    public ExceptionHanlder(Context context) {
        this.mContext = context;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtil.logE(TAG,ex.getLocalizedMessage());
        System.exit(2);
    }
}