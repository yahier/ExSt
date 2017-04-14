package yahier.exst.act.ad;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.external.alipay.AliPay;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.ad.AdOrderBaseItem;
import com.stbl.stbl.item.ad.AdOrderItem;
import com.stbl.stbl.item.ad.AdPayPreData;
import com.stbl.stbl.item.ad.AdUserItem;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.RoundImageView;
import com.stbl.stbl.wxapi.WeixinPay;

import java.text.DecimalFormat;

import io.rong.eventbus.EventBus;

/**
 * 广告订单-帮他人支付页面
 * Created by Administrator on 2016/9/28 0028.
 */

public class AdHelpTaPayActivity extends ThemeActivity implements FinalHttpCallback,View.OnClickListener{
    private RoundImageView rivUserIcon;//广告主头像
    private TextView tvPrice; //商品价格
    private TextView tvStatus; //订单状态
    private ImageView ivAlipaySelect; //选择支付宝
    private ImageView ivWechatpaySelect; //选择微信
    private TextView tvHelpTaPay; //替ta支付

    private String orderno; //订单号
    private AdOrderItem orderDetail; //订单信息
    private AdPayPreData prePaydata;//预支付信息
    private int payType = AdOrderBaseItem.TYPE_ALIPAY; //支付方式
    private int oldPayType = AdOrderBaseItem.TYPE_ALIPAY; //支付失败记录
    private boolean isPayCallback = true; //支付是否有回调，如果没有回调，请求服务器；true为有回调

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_help_ta_pay_layout);
        orderno = getIntent().getStringExtra("orderno");
        setRightImage(R.drawable.share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享
                if (orderDetail == null) return;
                //分享
                ShareItem shareItem = new ShareItem();
                if (orderDetail.getUser() != null) {
                    shareItem.setTitle(getString(R.string.ad_share_title));
                    shareItem.setContent(String.format(getString(R.string.ad_share_tips), orderDetail.getUser().getNickname()));
                    if (TextUtils.isEmpty(orderDetail.getUser().getImgurl()) || orderDetail.getUser().getIsdefaultimgurl() == AdUserItem.defaultImg){
                        shareItem.setDefaultIcon(R.drawable.icon_adotherpay_share);
                    }else {
                        shareItem.setImgUrl(orderDetail.getUser().getImgurl());
                    }
                }
                new ShareDialog(AdHelpTaPayActivity.this).shareAdOtherPay(orderDetail.getOrderno(), shareItem);
            }
        });
        rivUserIcon = (RoundImageView) findViewById(R.id.riv_user_icon);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        ivAlipaySelect = (ImageView) findViewById(R.id.iv_alipay_select);
        ivWechatpaySelect = (ImageView) findViewById(R.id.iv_wechatpay_select);
        tvHelpTaPay = (TextView) findViewById(R.id.tv_help_ta_pay);

        ivAlipaySelect.setOnClickListener(this);
        ivWechatpaySelect.setOnClickListener(this);
        tvHelpTaPay.setOnClickListener(this);

        EventBus.getDefault().register(this);
        getOrderDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPayCallback) {
            WaitingDialog.show(this,false);
            tvHelpTaPay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isPayCallback) { //再检测一遍，防止回调比onresume方法慢，导致重复弹框之类问题
                        getOrderDetail();
                    }else{
                        WaitingDialog.dismiss();
                    }
                }
            },3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_alipay_select: //支付宝
                payType = AdOrderBaseItem.TYPE_ALIPAY;
                break;
            case R.id.iv_wechatpay_select: //微信
                payType = AdOrderBaseItem.TYPE_WECHATPAY;
                break;
            case R.id.tv_help_ta_pay: //支付
                //微信支付，没安装微信，提示，并拦截
                if (payType == AdOrderBaseItem.TYPE_WECHATPAY && !AppUtils.isWeixinAvilible(this)){
                    ToastUtil.showToast(getString(R.string.ad_install_wechat));
                    return;
                }
                if (prePaydata != null && oldPayType == payType){
                    pay(prePaydata);
                }else {
                    prePaydata = null;
                    getPayPreInfo(orderno);
                }
                oldPayType = payType;//记录这次的支付方式
                break;
        }
        payTypeSwitch();
    }

    /**显示选择支付类型*/
    private void payTypeSwitch(){
        ivAlipaySelect.setImageResource(R.drawable.icon_weixuanze);
        ivWechatpaySelect.setImageResource(R.drawable.icon_weixuanze);
        switch (payType){
            case AdOrderBaseItem.TYPE_ALIPAY:
                ivAlipaySelect.setImageResource(R.drawable.icon_xuanze);
                break;
            case AdOrderBaseItem.TYPE_WECHATPAY:
                ivWechatpaySelect.setImageResource(R.drawable.icon_xuanze);
                break;
        }
    }
    //微信、支付宝支付回调
    public void onEvent(ExternalPayResult result) {
        switch (result.getType()) {
            case ExternalPayResult.TYPE_WEIXIN:
                isPayCallback = true;
                if (result.getErrCode() == ExternalPayResult.errCodeSucceed) { //成功
                    LogUtil.logE("LogUtil","WEI XIN SUCCESS");
                    String transactionNo = result.getTransaction();
                    if (prePaydata != null){
                        doVerify(prePaydata.getOrderno(),prePaydata.getOrderpayno(),transactionNo);
                    }
                }else{ //失败
                    LogUtil.logE("LogUtil","WEI XIN FAILD");
                }
                break;
            case ExternalPayResult.TYPE_ALIPAY:
                isPayCallback = true;
                if (result.getErrCode() == ExternalPayResult.errCodeSucceed) { //成功
                    LogUtil.logE("LogUtil","TYPE_ALIPAY SUCCESS");
                    if (prePaydata != null){
                        doVerify(prePaydata.getOrderno(),prePaydata.getOrderpayno(),"");
                    }
                }else{ //失败
                    LogUtil.logE("LogUtil","TYPE_ALIPAY FAILD");
                }
                break;
        }
    }
    //支付验证失败弹框
    private void verifyFaild(){
        Bundle bundle = new Bundle();
        bundle.putInt("type",6);
        enterActBundle(CommonResultAct.class,bundle);
        finish();
//        TipsDialog2.popup(this, getString(R.string.ad_pay_verify_faild_tips), getString(R.string.queding), new TipsDialog2.OnTipsListener() {
//            @Override
//            public void onConfirm() {
//                finish();
//            }
//
//            @Override
//            public void onCancel() {}
//        });
    }

    //填充数据
    private void showData(){
        if (orderDetail != null){
            if (orderDetail.getUser() != null){
                AdUserItem user = orderDetail.getUser();
                PicassoUtil.load(this,user.getImgurl(),rivUserIcon,R.drawable.icon_shifu_default);
                setLabel(String.format(getString(R.string.ad_ta_other_pay_order),user.getNickname()));
            }
            if (orderDetail.getGoods() != null && orderDetail.getGoods().get(0) != null) {
//                DecimalFormat df = new DecimalFormat("0.00");
//                String price = df.format(orderDetail.getGoods().get(0).getPrice());
                tvPrice.setText(orderDetail.getGoods().get(0).getPrice());
            }
            CharSequence statusTips = "";
            if (orderDetail.getRestpaytime() <= 0){
                statusTips = getString(R.string.ad_other_pay_status_tips1);
            }else{
                long hour = orderDetail.getRestpaytime()/60/60;
                long minutes = (orderDetail.getRestpaytime() - hour*60*60)/60;

                String tips1 = getString(R.string.ad_order_pay_time);
                String tips2 = String.format(getString(R.string.ad_hour_minute),hour,minutes);
                String tips3 = tips1 + tips2;

                SpannableString ss = new SpannableString(tips3);
                ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_red)),tips3.indexOf(tips2),tips3.indexOf(tips2) + tips2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                statusTips = ss;
//                statusTips = String.format(getString(R.string.ad_other_pay_status_tips2),hour,minutes);
                if (hour <= 0) {
                    String tips4 = String.format(getString(R.string.ad_hour_minute),hour,minutes);
                    String tips5 = tips1 + tips4;

                    SpannableString ss2 = new SpannableString(tips5);
                    ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_red)),tips5.indexOf(tips4),tips3.indexOf(tips4) + tips4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    statusTips = ss2;
//                    statusTips = String.format(getString(R.string.ad_other_pay_status_tips2_2), minutes);
                }
            }
            if (orderDetail.getOrderstate() == AdOrderBaseItem.HASPAY){
                statusTips = getString(R.string.ad_other_pay_status_tips3);
                payVerifyComplete();
            }else if(orderDetail.getOrderstate() == AdOrderBaseItem.PAYCANCEL){
                statusTips = getString(R.string.ad_other_pay_status_tips4);
            }

            if (orderDetail.getOrderstate() == AdOrderBaseItem.PAYVERIFY){
                verifyFaild();
            }
            tvStatus.setText(statusTips);
        }
    }
    //调用第三方支付
    private void pay(AdPayPreData prePayData){

        switch (payType){
            case AdOrderBaseItem.TYPE_ALIPAY: //支付宝
                if (prePayData == null) return;
                isPayCallback = false;
                new AliPay().pay(mActivity, prePayData.getOrderno()+"", mActivity.getString(R.string.mall_order_pay2), prePayData.getPayfee()+"", prePayData.getOrderpayno());
                break;
            case AdOrderBaseItem.TYPE_WECHATPAY://微信
                if (prePayData == null) return;
                isPayCallback = false;
                new WeixinPay().invokeBack(mActivity, prePayData.getWeixinjsonparameters());
                break;
        }
    }
    // 购买商品支付验证
    public void doVerify(String orderno,String orderpayno,String transactionNo) {
        Params params = new Params();
        LogUtil.logE("doVerify >> orderno:"+orderno+" orderpayno:"+orderpayno+" transationno:"+transactionNo);
        params.put("orderno", orderno); //支付金额
        params.put("orderpayno", orderpayno);// 商城生成的订单支付号
        params.put("transationid", transactionNo);//支付平台反馈的订单号
        new HttpEntity(this).commonPostData(Method.adsysOrderPayVerify, params, this);
    }

    //获取预支付信息
    private void getPayPreInfo(String orderNo){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("paytype",payType);
        params.put("orderno",orderNo);
        new HttpEntity(this).commonPostData(Method.adsysOrderPayPre,params,this);
    }

    /**获取订单详情*/
    private void getOrderDetail(){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("orderno",orderno);
        new HttpEntity(this).commonPostData(Method.adsysOrderDetailGet,params,this);
    }
    //支付确认完成后的操作
    private void payVerifyComplete(){
        Bundle bundle = new Bundle();
        bundle.putInt("type",4);
        bundle.putString("name",orderDetail != null && orderDetail.getUser() != null ? orderDetail.getUser().getNickname() : "");
        bundle.putFloat("price",prePaydata != null ? prePaydata.getPayfee() : 600f);
        enterActBundle(CommonResultAct.class,bundle);
        //结束广告介绍页
        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeCloseIntroduceAd));
        finish();
    }
    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result,BaseItem.class);
        if (item == null) return;
        if (item.getIssuccess() != BaseItem.successTag){
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null){
                ToastUtil.showToast(item.getErr().getMsg());
            }
            if (methodName.equals(Method.adsysOrderPayVerify)){
                verifyFaild();
            }
            return;
        }
        String json = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName){
            case Method.adsysOrderDetailGet: //订单详情
                orderDetail = JSONHelper.getObject(json,AdOrderItem.class);
                showData();
                break;
            case Method.adsysOrderPayPre:  //预支付信息
                prePaydata = JSONHelper.getObject(json,AdPayPreData.class);
                pay(prePaydata);
                break;
            case Method.adsysOrderPayVerify://支付确认完成
                JSONObject jobj = JSON.parseObject(json);
                if (jobj != null && jobj.containsKey("orderstate")){
                    int status = jobj.getInteger("orderstate");
                    if (status == AdOrderBaseItem.PAYVERIFY){
                        verifyFaild();
                        return;
                    }
                }
                payVerifyComplete();
                break;
        }
    }
}
