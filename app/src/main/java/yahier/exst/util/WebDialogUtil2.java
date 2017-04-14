package yahier.exst.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.stbl.stbl.R;

/**
 * Created by Administrator on 2016/10/13.
 * 用dialog显示网页
 */

public class WebDialogUtil2 {
    WebView webView;

    void init(Context mContext) {
        final Dialog dialog = new Dialog(mContext, R.style.dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_web2, null);
        webView = (WebView) view.findViewById(R.id.web);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //以下属性新加
        // 设置可以支持缩放
        settings.setSupportZoom(true);
        // 设置出现缩放工具
        settings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        settings.setUseWideViewPort(true);
        //自适应屏幕
        //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + ";STBL");

        int width = Device.getWidth() - Util.dip2px(mContext, 50);
        int height = Device.getHeight() * 2 / 3;
        dialog.setContentView(view, new LinearLayout.LayoutParams(width, height));
        dialog.show();

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;


            }
        });

        view.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setUrl(Context mContext, String url) {
        init(mContext);
        webView.loadUrl(url);
    }


}
