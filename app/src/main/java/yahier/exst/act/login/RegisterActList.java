package yahier.exst.act.login;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理注册activity流程
 * Created by lenovo on 2016/3/15.
 */
public class RegisterActList {
    static List<Activity> listActivity;

    static {
        listActivity = new ArrayList<Activity>();
    }

    public static void add(Activity act) {
        listActivity.add(act);
    }

    public static void remove(Activity act){
        listActivity.remove(act);
    }

    public static void clearAll() {
        for (int i = 0; i < listActivity.size(); i++) {
            if (!listActivity.get(i).isFinishing()) {
                listActivity.get(i).finish();
            }
        }
    }

}
