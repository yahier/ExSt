package yahier.exst.common;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.IntroduceAdAct;
import com.stbl.stbl.act.dongtai.DongtaiAct2;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.EventTypeDynamicShare;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.UserAd;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ShareUtils;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.StblWebView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.rong.eventbus.EventBus;

public class CommonWeb extends ThemeActivity implements View.OnClickListener {
    StblWebView web;
    Context mContext;
    String tokenStr;
    String userid;

    private boolean firstLoad = true;
    final String markTokenBig = "{htk}";

    final String markuid = "uid";
    final String markUidBig = "{uid}";
    private ProgressBar mProgressBar;

    String url;
    String webTitle;
    volatile String sharetitle;
    volatile String sharedesc;
    volatile String shareimgurl;

    volatile String firstimgurl;
    volatile boolean isShare;
    final String jsKey = "stbl";//js调用的
    final String directPrefix = "stbl";//跳转前缀，如果遇到以此开始的网页，就交给CommonInteract处理

    View imgBack, imgClose;
    ImageView imgShare;
    TextView tvTitle;
    private final int REQUEST_FINISH = 0x111;//需要结束当前页面
    public final static int typeAd = 1;
    public final static int typeCommon = 0;
    private int type = typeCommon;
    TextView tvAdBusinessType;

    private boolean isFirstJump = true;//是否第一次跳转
    private boolean isBanner = false; //是否从首页banner进来的
    private boolean isHomeAd = false; //是否从首页广告进来的

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web);
        setTopGone();

        EventBus.getDefault().register(this);
        mContext = this;
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imgBack = findViewById(R.id.imgBack);
        imgClose = findViewById(R.id.imgClose);
        imgShare = (ImageView) findViewById(R.id.imgShare);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAdBusinessType = (TextView) findViewById(R.id.tvAdBusinessType);
        imgBack.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        imgShare.setOnClickListener(this);

        web = (StblWebView) findViewById(R.id.web);
        String domainwhitelist = (String) SharedPrefUtils.getFromPublicFile(KEY.domainwhitelist, "");
        web.setWhiteList(domainwhitelist);

        WebSettings settings = web.getSettings();
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
        //以上属性新加

        //新加 为调用localStorage
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);


        isBanner = getIntent().getBooleanExtra("isBanner",false);
        isHomeAd = getIntent().getBooleanExtra("isHomeAd",false);
        url = getIntent().getStringExtra("url");

        tokenStr = SharedToken.getToken(this);
        try {
            LogUtil.logE(tag, "1:" + tokenStr);
            tokenStr = URLEncoder.encode(tokenStr, "GBK");
            //String tokenStr2 = java.net.URLEncoder.encode(tokenStr, "UTF-8");
            LogUtil.logE(tag, "2:" + tokenStr);
            //LogUtil.logE(tag,"3:"+tokenStr2);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        userid = SharedToken.getUserId(this);
        ad = (Ad) getIntent().getSerializableExtra("ad");
        type = getIntent().getIntExtra("type", typeCommon);

        if (ad != null) {
            url = ad.adurl;
        }
        if (url == null) {
            return;
        }

        LogUtil.logE("url1:" + url);
        if (url.contains(markTokenBig)) {
            LogUtil.logE("__________contains");
            //String tokenTag = markToken + "=" + tokenStr;
            url = url.replace(markTokenBig, tokenStr);
        }

        if (url.contains(markUidBig)) {
            if (url.contains(markTokenBig)) {
                url = url + "&";
            }
            // String userTag = markuid + "=" + userid;   //uid=111111;
            url = url.replace(markUidBig, userid);  //{uid} uid=SDFMM
        }

        web.addJavascriptInterface(new GetShareDataInterface(), "getShareData");
        web.addJavascriptInterface(new JS(), jsKey);
        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith("stbl")) {
                    if (!isFirstJump) return;
                    isFirstJump = false;
                    Intent intent = new Intent(mContext, CommonWebInteact.class);
                    intent.setData(Uri.parse(url));
                    mContext.startActivity(intent);
                    finish();
                }
                isShare = true;
                hideRightImage();
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                view.loadUrl("javascript:window.getShareData.IsNotShare("
                        + "document.querySelector('meta[name=\"no_share\"]').getAttribute('content')" + ");");
                view.loadUrl("javascript:window.getShareData.OnGetShareTitle("
                        + "document.querySelector('meta[name=\"sharetitle\"]').getAttribute('content')" + ");");
                view.loadUrl("javascript:window.getShareData.OnGetShareDesc("
                        + "document.querySelector('meta[name=\"sharedesc\"]').getAttribute('content')" + ");");
                view.loadUrl("javascript:window.getShareData.OnGetShareImgUrl("
                        + "document.querySelector('meta[name=\"shareimgurl\"]').getAttribute('content')" + ");");

                view.loadUrl("javascript:window.getShareData.OnGetFirstImgUrl("
                        + "document.getElementsByTagName(\"img\")[0].src" + ");");
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);

                web.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isShare) {
                            imgShare.setVisibility(View.VISIBLE);
                        } else {
                            imgShare.setVisibility(View.INVISIBLE);
                        }
                    }
                }, 300);

                view.loadUrl("javascript: var v = document.getElementsByTagName('audio'); v[0].play();");

                if (!CommonWeb.this.url.equals(url)) {
                    imgClose.setVisibility(View.VISIBLE);
                } else {
                    imgClose.setVisibility(View.GONE);
                }
                checkShowAdBushiness();

                //LogUtil.logE("onPageFinished title", web.getTitle());
                tvTitle.setText(web.getTitle());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 LogUtil.logE("shouldOverrideUrlLoading"+"url:--"+url);
                if (url.startsWith("tel:")) {//支持拨打电话
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith(directPrefix)) {
                    Intent intent = new Intent(CommonWeb.this, CommonWebInteact.class);
                    intent.setData(Uri.parse(url));
                    CommonWeb.this.startActivityForResult(intent, REQUEST_FINISH);
                    return true;
                }

                return false;//原本false
            }

        });

        //web.addJavascriptInterface(this, "name");
        web.getSettings().setUserAgentString(web.getSettings().getUserAgentString() + ";STBL");

        web.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                TipsDialog.popup(mContext, message, mContext.getString(R.string.enter));
                result.confirm();// 不处理的话，页面就不能再滑动了喔
                LogUtil.logE("Url : " + url);
                return true;
                // return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //LogUtil.logE("CommonWeb progress=" + newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                LogUtil.logE("ANDROID_LAB", "TITLE=" + title);
//                webTitle = title;
//                // setLabel(title);
//                tvTitle.setText(title);
//            }
        });


        //web.loadUrl("file:///android_asset/index.html");
        //web.loadUrl("http://dev-temp.shifugroup.net/");

        //如果不是广告，则直接显示。
        if (ad == null) {
            web.loadUrl(url);
        } else {
            checkAdService();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstJump = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(EventTypeCommon eventType){
        if (eventType == null) return;
        //刷新网页
        if (eventType.getType() == EventTypeCommon.typeRefreshWeb){
            if (web != null){
                web.reload();
            }
        }
    }
    //因为在dongtai2那边无法正常回调到这个类刷新，只能在这里调用分享
    public void onEvent(EventTypeDynamicShare shareEvent){
        if (shareEvent == null) return;
        CommonShare.shareShortStatusAfterPublish(this, shareEvent.getShareItem(), shareEvent.getUrl(),
                shareEvent.isShareToCircle(), shareEvent.isShareToQzone(), shareEvent.isShareTask());
    }

    //只在这里广告正常，才加载广告链接。
    void getAdDetail() {
        if (ad == null) return;
        //如果是系统广告，就不用判断。
        if (ad.issys == Ad.issysYes) {
            web.loadUrl(ad.adurl);
            return;
        }
        Params params = new Params();
        params.put("adid", ad.adid);
        new HttpEntity(mActivity).commonPostData(Method.getAdDetail, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {
                    findViewById(R.id.linAdExpire).setVisibility(View.VISIBLE);
                    findViewById(R.id.linBusinessType).setVisibility(View.GONE);
                    tvTitle.setText("广告详情");
                    return;
                }

                String obj = JSONHelper.getStringFromObject(item.getResult());
                ad = JSON.parseObject(obj, Ad.class);
                web.loadUrl(ad.adurl);
            }
        });

    }

    //既有开始的传值。也会重新用id获取一遍赋值
    Ad ad;

    //检查是否开通广告服务
    void checkAdService() {

        if (type != typeAd) {
            return;
        }
        getAdDetail();
        // imgShare.setImageResource(R.drawable.top_more);
        url = ad.adurl;
        int isader = (int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0);
        //底部的描述和开通蓝
        if (isader != Ad.isAderYes) {
            findViewById(R.id.linAd).setVisibility(View.VISIBLE);
            findViewById(R.id.tvOpenAdService).setOnClickListener(this);
        }


    }

    //
    void checkShowAdBushiness() {
        if (ad == null) return;

        final View linBusinessType = findViewById(R.id.linBusinessType);
        if (ad.businessclass == Ad.businessclassNone) {
            tvAdBusinessType.setVisibility(View.GONE);
            linBusinessType.setVisibility(View.GONE);
        } else {
            linBusinessType.setVisibility(View.VISIBLE);
            tvAdBusinessType.setText(ad.businessclassname);
            tvAdBusinessType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdHelper.toApplyAdCooperateAct(ad, mActivity);
                }
            });

            final View viewClose = findViewById(R.id.imgBusinessTypeClose);
            viewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float translationX = tvAdBusinessType.getTranslationX();
                    if (translationX == 0) {
                        ObjectAnimator.ofFloat(tvAdBusinessType, "translationX", 0, 400).start();
                        ObjectAnimator.ofFloat(viewClose, "rotation", 0, 135).start();
                    } else {
                        ObjectAnimator.ofFloat(tvAdBusinessType, "translationX", 400, 0).start();
                        ObjectAnimator.ofFloat(viewClose, "rotation", 135, 0).start();
                    }

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgBack:
                backAction();
                break;
            case R.id.imgClose:
                finish();
                break;
            case R.id.imgShare:
                if (isBanner){ MobclickAgent.onEvent(this,UmengClickEventHelper.SYBFX); }
                if (isHomeAd){ MobclickAgent.onEvent(this,UmengClickEventHelper.SYGGWFX); }
                ShareItem shareItem = new ShareItem();
                if (TextUtils.isEmpty(sharetitle)) {
                    shareItem.setTitle(webTitle);
                } else {
                    shareItem.setTitle(sharetitle);
                }
                if (TextUtils.isEmpty(sharedesc)) {
                    shareItem.setContent("");
                } else {
                    shareItem.setContent(sharedesc);
                }

                if (TextUtils.isEmpty(shareimgurl)) {
                    if (TextUtils.isEmpty(firstimgurl)) {
                        shareItem.setImgUrl("");
                    } else {
                        shareItem.setImgUrl(firstimgurl);
                    }
                } else {
                    shareItem.setImgUrl(shareimgurl);
                }
                //加上当前操作人的userId
                if (url.contains("?")) {
                    url = url + "&ui=" + getUserItem().getInvitecode();
                } else {
                    url = url + "?ui=" + getUserItem().getInvitecode();
                }
                //new CommonShare().showShareWindow(CommonWeb.this, shareItem, url);
                shareItem.link = url;
                LogUtil.logE("share url:" + url);
                if (ad == null)
                    new ShareDialog(CommonWeb.this).shareWebpage(shareItem);
                else {
                    //shareItem.setTitle("邀你加入我的人脉商务圈");
                    //shareItem.setContent("和我一起加入品牌+计划，以革命性推广，开拓人脉商机吧！来自 @" + SharedUser.getUserNick());
                    //shareItem.umImage = new UMImage(mActivity, R.drawable.brand_plus_share_icon);

                    new ShareDialog(CommonWeb.this).shareAdWebpage(shareItem, ad);
                }

                break;
            case R.id.tvOpenAdService:
                UmengClickEventHelper.onOpenMtClickEvent(CommonWeb.this);

                startActivity(new Intent(this, IntroduceAdAct.class));
                break;
        }
    }


    private final class JS implements Serializable {

        @JavascriptInterface
        public void share(final String title, final String content, final String image, final String url) {
            mHandler.post(new Runnable() { //线程调用，需要回到主线程，否则点击分享的时候会崩溃
                @Override
                public void run() {
                    MobclickAgent.onEvent(mActivity, UmengClickEventHelper.HTML5_INNER_SHARE);
                    ShareItem shareItem = new ShareItem();
                    shareItem.setTitle(title);
                    shareItem.setContent(content);
                    shareItem.setImgUrl(image);
                    String linkUrl = url;
                    //加上当前操作人的userId
                    if (linkUrl.contains("?")) {
                        linkUrl = linkUrl + "&ui=" + getUserItem().getInvitecode();
                    } else {
                        linkUrl = linkUrl + "?ui=" + getUserItem().getInvitecode();
                    }
                    //new CommonShare().showShareWindow(CommonWeb.this, shareItem, linkUrl);
                    shareItem.link = linkUrl;
                    new ShareDialog(CommonWeb.this).shareWebpage(shareItem);

                }
            });
        }

        @JavascriptInterface
        public String getToken() {
            return SharedToken.getToken();
        }


    }

    private class GetShareDataInterface implements Serializable { //异步调用的，不是同步

        @JavascriptInterface
        public void OnGetShareTitle(String title) {
            if (!TextUtils.isEmpty(title)) {
                sharetitle = title;
            }
        }

        @JavascriptInterface
        public void OnGetShareDesc(String desc) {
            if (!TextUtils.isEmpty(desc)) {
                sharedesc = desc;
            }
        }

        @JavascriptInterface
        public void OnGetShareImgUrl(String imgurl) {
            if (!TextUtils.isEmpty(imgurl)) {
                shareimgurl = imgurl;
            }
        }

        @JavascriptInterface
        public void OnGetFirstImgUrl(String imgurl) {
            if (!TextUtils.isEmpty(imgurl)) {
                firstimgurl = imgurl;
            }
        }

        @JavascriptInterface
        public void IsNotShare(String value) {
            if (!TextUtils.isEmpty(value)) {
                if (value.equals("1")) {
                    isShare = false;
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        backAction();
    }

    private void backAction() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (web != null) {
            web.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FINISH) {
            finish();
        } else {
        }
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
