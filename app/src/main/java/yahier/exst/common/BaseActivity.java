package yahier.exst.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

import com.stbl.base.library.task.TaskManager;
import com.stbl.stbl.R;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.CacheUtil;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogRecordUtil;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.utils.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

/**
 * Created by Administrator on 2016/8/18.
 */

public class BaseActivity extends AppCompatActivity {

    protected BaseActivity mActivity;
    protected TaskManager mTaskManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mTaskManager = new TaskManager();
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.SWITCH_LANG);
        switchLang();
//        getDelegate().getSupportActionBar().hide();
        if (ConfigControl.logable)
            LogRecordUtil.init();
        setStatusBar();
        CacheUtil.logCache("onCreate");
    }


    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, R.color.title_color);
        StatusBarUtil.StatusBarLightMode(this);
    }


    private void switchLang() {
        Locale locale = null;
        int position = 0;
        if (ConfigControl.switchLang) {
            position = (int) SharedPrefUtils.getFromPublicFile(KEY.MULTI_LANG_SETTING, 0);
        } else {
            position = 1;//只显示简体中文
        }
        switch (position) {
            case 1:
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 2:
                locale = Locale.ENGLISH;
                break;
            default:
                locale = Locale.getDefault();
                break;
        }
        SharedPrefUtils.putToPublicFile(KEY.IS_ENGLISH, locale.equals(Locale.ENGLISH));
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    public TaskManager getTaskManager() {
        return mTaskManager;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        CacheUtil.logCache("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTaskManager.onDestroy();
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
        CacheUtil.logCache("onDestroy");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.SWITCH_LANG:
                    receiveSwitchLang();
                    break;
            }
        }
    };

    protected void receiveSwitchLang() {
        switchLang();
        recreate();
    }
}
