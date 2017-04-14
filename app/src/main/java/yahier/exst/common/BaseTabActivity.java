package yahier.exst.common;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.stbl.stbl.R;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.utils.StatusBarUtil;

import java.util.Locale;

/**
 * Created by Administrator on 2016/8/18.
 */

public class BaseTabActivity extends TabActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.SWITCH_LANG);
        switchLang();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setStatusBarColor(this, R.color.title_color);
            StatusBarUtil.StatusBarLightMode(this);
//        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
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
