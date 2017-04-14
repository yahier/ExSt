package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.MineWalletAct;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.ad.AdGoodsItem;
import com.stbl.stbl.item.ad.AdOrderBaseItem;
import com.stbl.stbl.item.ad.AdOrderItem;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.StblWebView;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2016/9/21.
 * 广告位介绍
 */

public class IntroduceAdAct extends BaseActivity implements View.OnClickListener,FinalHttpCallback{
    private TextView tvOpenAdQualify;//开通广告位
    private View rlMyRebate; //我的佣金
    private View ivHaveRebate; //返利提醒
    private View tvService;//客服咨询
    private StblWebView wvDescription;//广告位介绍
    private View topLeft; //返回
    private View topRight; //分享

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_introduce_act);
        initView();
        EventBus.getDefault().register(this);
        setOpenBtnText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHaveOldOrder();
        //判断返利是否需要显示
        ivHaveRebate.setVisibility(AdHelper.isShowRebate() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(){
        topLeft = findViewById(R.id.top_left);
        topRight = findViewById(R.id.top_right);
        tvOpenAdQualify = (TextView) findViewById(R.id.tv_open_ad_qualify);
        rlMyRebate = findViewById(R.id.rl_my_rebate);
        ivHaveRebate = findViewById(R.id.iv_have_rebate);
        tvService = findViewById(R.id.tv_service);
        wvDescription = (StblWebView) findViewById(R.id.wv_description);

        topLeft.setOnClickListener(this);
        topRight.setOnClickListener(this);
        tvOpenAdQualify.setOnClickListener(this);
        rlMyRebate.setOnClickListener(this);
        tvService.setOnClickListener(this);

        wvDescription.getSettings().setJavaScriptEnabled(true);
        wvDescription.getSettings().setDomStorageEnabled(true);

        WebSettings setting = wvDescription.getSettings();
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        wvDescription.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        String url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_main_close,"");
        if (!"".equals(url)) {
            wvDescription.loadUrl(url);
        }else{
            CommonTask.getCommonDicBackground();
        }
    }
    private void setOpenBtnText(){
        AdHelper.getAdGoods(this, new AdHelper.OnGetGoodsListener() {
            @Override
            public void onSuccess(AdGoodsItem goodsItem) {
                if (tvOpenAdQualify != null && goodsItem != null) {
                    tvOpenAdQualify.setText(getResources().getString(R.string.ad_open_service) + "(" + goodsItem.getDisplaytitle() + ")");
                }
            }
            @Override
            public void onError() {
            }
        });
    }
    /**显示订单入口*/
    private void showOrderText(AdOrderItem oldOrderItem){
        //有他人代付订单才显示-订单入口
        if (oldOrderItem != null && oldOrderItem.getPaytype() == AdOrderBaseItem.TYPE_OTHER){
            boolean isReturn = true;
            if (oldOrderItem.getOrderstate() == AdOrderBaseItem.WaitPay) {
                tvOpenAdQualify.setText(R.string.ad_order_paying);
                isReturn = false;
            }
            if (oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYVERIFY) {
                tvOpenAdQualify.setText(R.string.ad_order_pay_verify);
                isReturn = false;
            }
            if (oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYCANCEL ||
                    oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT) {
                tvOpenAdQualify.setText(R.string.ad_order_is_pastdue);
                isReturn = false;
            }
            if (oldOrderItem.getOrderstate() == AdOrderBaseItem.HASPAY) {
                tvOpenAdQualify.setText(R.string.ad_haspay);
                isReturn = false;
                if ((int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER+SharedToken.getUserId(), 0) != 0) {
                    Intent intent = new Intent(this,AdManagerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            if (isReturn) return;
            final String orderno = oldOrderItem.getOrderno();
            tvOpenAdQualify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IntroduceAdAct.this, AdSeeHelpPayOrderActivity.class);
                    intent.putExtra("orderno", orderno);
                    startActivity(intent);
                }
            });
        }else{
//            tvOpenAdQualify.setText(getString(R.string.ad_open_service));
            setOpenBtnText();
            tvOpenAdQualify.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.top_left: //返回
                finish();
                break;
            case R.id.top_right: //分享
                UmengClickEventHelper.onAdShareClickEvent(this);
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_main_open,"");
                if ("".equals(url)) return;
                ShareItem shareItem = new ShareItem();
                shareItem.title = getString(R.string.ad_share_adurl_title);
                shareItem.content = String.format(getString(R.string.ad_share_adurl_content), SharedUser.getUserNick());
                shareItem.link = url;
                new ShareDialog(this).shareWebpage(shareItem);
                break;
            case R.id.tv_open_ad_qualify: //开通服务
                intent = new Intent(this,PurchaseAdAualifyAct.class);
                startActivity(intent);
                break;
            case R.id.rl_my_rebate: //我的返利
                /**记录是否有新返利信息*/
                AdHelper.setShowRebate(false);
//                intent = new Intent(this,MyFanliActivity.class);
                intent = new Intent(this,MineWalletAct.class);
                startActivity(intent);
                break;
            case R.id.tv_service: //了解更多
//                TipsDialog2.showHotLine(this);//客服咨询
                url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_brandplus_introd,"");
                if ("".equals(url)) return;
                intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url",url);
                startActivity(intent);
                break;
        }
    }

    public void onEvent(EventTypeCommon type){
        if (type != null && type.getType() == EventTypeCommon.typeCloseIntroduceAd){
            finish();
        }
    }

    //查询是否有未支付旧订单
    private void getHaveOldOrder(){
        WaitingDialog.show(this,false);
        Params params = new Params();
        new HttpEntity(this).commonPostData(Method.adsysOrderDetailGet,params,this);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        LogUtil.logE("LogUtil",methodName +"--"+result);
        BaseItem item = JSONHelper.getObject(result,BaseItem.class);
        if (item == null) return;
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
//                ToastUtil.showToast(item.getErr().getMsg());
                return;
            }
        }
        String json = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName){
            case Method.adsysOrderDetailGet: //是否有未支付订单
                AdOrderItem oldOrderItem = JSONHelper.getObject(json,AdOrderItem.class);
                showOrderText(oldOrderItem);
                break;
        }
    }
}
