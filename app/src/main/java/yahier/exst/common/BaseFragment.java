package yahier.exst.common;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.stbl.base.library.task.TaskManager;

/**
 * Created by Administrator on 2016/8/24.
 */

public class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected TaskManager mTaskManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mTaskManager = new TaskManager();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTaskManager.onDestroy();
    }
}
