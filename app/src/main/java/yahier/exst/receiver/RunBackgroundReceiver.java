package yahier.exst.receiver;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.stbl.stbl.ui.DirectScreen.service.QavsdkServiceApi;
import com.stbl.stbl.utils.UISkipUtils;

/**
 * Created by meteorshower on 16/3/30.
 */
public class RunBackgroundReceiver implements Application.ActivityLifecycleCallbacks {

    private final String TAG = "MyApplication";
    private int countActivitys = 0;

    public RunBackgroundReceiver(Application application){
        application.registerActivityLifecycleCallbacks(RunBackgroundReceiver.this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivityCreated : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivityDestroyed : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivityPaused : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
    }

    @Override
    public void onActivityResumed(Activity activity) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivityResumed : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivitySaveInstanceState : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivityStarted : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");

        if (countActivitys == 0) {
//            Log.e(TAG, " --------------------------------------------------------------------------- ");
//            Log.e(TAG, " ---------------------------   回到前台状态   -------------------------------- ");
//            Log.e(TAG, " --------------------------------------------------------------------------- ");
            if(QavsdkServiceApi.isQavsdkScreenServiceRuning(activity)){
                UISkipUtils.startFloatingDirectScreenService(activity);
            }
        }
        countActivitys++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
//        Log.e(TAG, " ------------- onActivityStopped : "+activity.getClass().getSimpleName()+" -------------- ");
//        Log.e(TAG, " --------------------------------------------------------------------------- ");
        countActivitys --;

        if(countActivitys == 0){
//            Log.e(TAG, " --------------------------------------------------------------------------- ");
//            Log.e(TAG, " ---------------------------   回到后台状态   -------------------------------- ");
//            Log.e(TAG, " --------------------------------------------------------------------------- ");
            UISkipUtils.stopFloatingDirectScreenService(activity);
        }
    }
}
