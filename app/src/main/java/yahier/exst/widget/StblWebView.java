package yahier.exst.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.SharedDevice;
import com.stbl.stbl.util.SharedToken;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tnitf on 2016/8/5.
 */
public class StblWebView extends WebView {

    private List<String> mWhiteList;

    public StblWebView(Context context) {
        super(context);
    }

    public StblWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StblWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void loadUrl(String url) {
        if (mWhiteList == null || mWhiteList.size() == 0) {
            super.loadUrl(url);
            return;
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            super.loadUrl(url);
            return;
        }
        boolean inWhiteList = false;
        for (String domain : mWhiteList) {
            if (url.startsWith("http://" + domain) || url.startsWith("https://" + domain)) {
                inWhiteList = true;
                break;
            }
        }
        if (inWhiteList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("x-stbl-token", getToken());
            map.put("x-stbl-ua", SharedDevice.toDeviceString());
            map.put("x-stbl-lang", AppUtils.getCurrLang());
            map.put("x-stbl-guid", AppUtils.getGUID());
            super.loadUrl(url, map);
        } else {
            super.loadUrl(url);
        }
    }

    public void setWhiteList(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                mWhiteList = JSON.parseArray(json, String.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getToken() {
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag(MyApplication.getContext()));
        if (UserRole.isTemp(roleFlag)) {
            return "-1";
        }
        return SharedToken.getToken(MyApplication.getContext());
    }
}
