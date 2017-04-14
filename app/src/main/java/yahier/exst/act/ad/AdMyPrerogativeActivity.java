package yahier.exst.act.ad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RoundImageView;
import com.stbl.stbl.widget.StblWebView;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.stbl.stbl.R.string.ad_my_prerogative;

/**
 * 我的权益
 * Created by Administrator on 2016/9/26 0026.
 */

public class AdMyPrerogativeActivity extends ThemeActivity {
//    private RoundImageView rivUserIcon;//用户头像
//    private TextView tvName; //名称
    private boolean isJumpToManager = false; //是否跳转到管理页
    private StblWebView wvDescription;//
    final String markTokenBig = "{htk}";
    final String directPrefix = "stbl";//跳转前缀，如果遇到以此开始的网页，就交给CommonInteract处理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_my_prerogative_layout);
        setLabel(R.string.ad_my_prerogative);
        setRightText(getString(R.string.ad_order));
        setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去订单页面
                Intent intent = new Intent(AdMyPrerogativeActivity.this,AdMyOrderActivity.class);
                startActivity(intent);
            }
        });
//        rivUserIcon = (RoundImageView) findViewById(R.id.riv_user_icon);
//        tvName = (TextView) findViewById(R.id.tv_name);
        wvDescription = (StblWebView) findViewById(R.id.wv_description);

        isJumpToManager = getIntent().getBooleanExtra("isJumpToManager",false);

//        String imgUrl = SharedUser.getUserItem().getImgurl();
//        PicassoUtil.load(this,imgUrl,rivUserIcon, R.drawable.icon_shifu_default);
//        String name = SharedUser.getUserNick();
//        if (name != null){
//            tvName.setText(String.format(getString(R.string.ad_my_prerogative_tips1),name));
//        }

        wvDescription.getSettings().setJavaScriptEnabled(true);
        wvDescription.getSettings().setDomStorageEnabled(true);

        WebSettings setting = wvDescription.getSettings();
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        wvDescription.addJavascriptInterface(new Share(), "stbl");
        wvDescription.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(directPrefix)) {
                    Intent intent = new Intent(AdMyPrerogativeActivity.this, CommonWebInteact.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        String url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_server_introduce,"");
        if (!"".equals(url)) {
            String tokenStr = SharedToken.getToken(this);
            try {
                tokenStr = URLEncoder.encode(tokenStr, "GBK");
            } catch(UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (url.contains(markTokenBig)) {
                url = url.replace(markTokenBig, tokenStr);
                LogUtil.logE("__________url--"+url);
            }
            wvDescription.loadUrl(url);
        }else{
            CommonTask.getCommonDicBackground();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (isJumpToManager) {
            Intent intent = new Intent(this, AdManagerActivity.class);
            startActivity(intent);
        }
    }

    private final class Share implements Serializable {

        @JavascriptInterface
        public void share(String title, String content, String image, String linkUrl) {
            ShareItem shareItem = new ShareItem();
            shareItem.setTitle(title);
            shareItem.setContent(content);
            shareItem.setImgUrl(image);
            //加上当前操作人的userId
            if (linkUrl.contains("?")) {
                linkUrl = linkUrl + "&ui=" + getUserItem().getInvitecode();
            } else {
                linkUrl = linkUrl + "?ui=" + getUserItem().getInvitecode();
            }
            //new CommonShare().showShareWindow(CommonWeb.this, shareItem, linkUrl);
            shareItem.link = linkUrl;
            new ShareDialog(AdMyPrerogativeActivity.this).shareWebpage(shareItem);
        }

        @JavascriptInterface
        public String getToken() {
            return SharedToken.getToken();
        }


    }
}
