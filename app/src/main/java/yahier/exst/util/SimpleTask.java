package yahier.exst.util;

import android.app.Activity;

import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.model.TaskError;

import java.lang.ref.WeakReference;

/**
 * Created by lr on 2016/1/22.
 */
public abstract class SimpleTask<T> implements Runnable {

    private WeakReference<Activity> mActivityRef;
    private WeakReference<Callback<T>> mCallbackRef;

    protected abstract void call();

    public SimpleTask setCallback(Activity activity, Callback<T> callback) {
        if (activity == null) {
            throw new NullPointerException("activity is null!");
        }
        mActivityRef = new WeakReference<>(activity);
        if (callback == null) {
            throw new NullPointerException("Callback is null!");
        }
        mCallbackRef = new WeakReference<>(callback);
        return this;
    }

    public void start() {
        ThreadPool.getInstance().execute(this);
    }

    public void onCompleted(final T result) {
        final Callback<T> callback = mCallbackRef.get();
        if (callback == null) {
            return;
        }
        if (callback.onDestroy()) {
            return;
        }
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                callback.onCompleted(result);
            }
        });
    }

    public void onError(String msg) {
        onError(new TaskError(msg));
    }

    public void onError(int code, String msg) {
        onError(new TaskError(code, msg));
    }

    public void onError(Throwable e) {
        onError(StringUtil.getUserTipExceptionMsg(e));
    }

    public void onError(com.stbl.base.library.task.TaskError e) {
        onError(new TaskError(e.code, e.msg));
    }

    public void onError(final TaskError e) {
        if(mCallbackRef==null)return;//add 0924
        final Callback<T> callback = mCallbackRef.get();
        if (callback == null) {
            return;
        }
        if (callback.onDestroy()) {
            return;
        }
        MainHandler.getInstance().post(new Runnable() {

            @Override
            public void run() {
                Activity activity = mActivityRef.get();
                if (activity == null) {
                    return;
                }
                if (e.code != 0) {
                    ServerError.checkError(activity, e.code, e.msg);
                    if (OkHttpHelper.checkError(e.code)) {
                        e.msg = null;
                    }
                }
                callback.onError(e);
            }
        });
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Throwable e) {
            e.printStackTrace();
            onError(e);
        }
    }

    public interface Callback<T> {

        public void onError(TaskError e);

        public void onCompleted(T result);

        public boolean onDestroy();
    }

}
