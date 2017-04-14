package yahier.exst.util;

import android.app.Activity;

import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.item.ServerError;

/**
 * Created by lr on 2016/5/30.
 */
public abstract class HttpTaskCallback<T> extends TaskCallback<T> {

    private Activity mActivity;

    public HttpTaskCallback(Activity activity) {
        mActivity = activity;
    }

    /**
     * 预处理错误
     */
    @Override
    public void onError(TaskError e) {
        if (e.msg == null && e.throwable != null) {
            e.msg = StringUtil.getUserTipExceptionMsg(e.throwable);
        }
        if (mActivity != null && e.code != 0) {
            ServerError.checkError(mActivity, e.code, e.msg);
            if (OkHttpHelper.checkError(e.code)) {
                e.msg = null;
            }
        }
    }

}
