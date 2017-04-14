package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.SelectFriendActivity;
import com.stbl.stbl.act.im.rong.AdOtherPayMessage;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AdSendOrderDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.external.alipay.AliPay;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.AdGoodsItem;
import com.stbl.stbl.item.ad.AdOrderBaseItem;
import com.stbl.stbl.item.ad.AdOrderCreateItem;
import com.stbl.stbl.item.ad.AdPayPreData;
import com.stbl.stbl.item.im.UserList;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.wxapi.WeixinPay;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2016/9/21.
 * 购买广告位
 */

public class PurchaseAdAualifyAct extends ThemeActivity implements View.OnClickListener,FinalHttpCallback{
    private TextView tvServiceName;//服务名称
    private TextView tvServiceTime;//服务期限
    private TextView tvServicePrice;//服务价格
    private ImageView ivAlipaySelect;//支付宝
    private ImageView ivWechatpaySelect;//支付宝
    private ImageView ivOtherpaySelect;//支付宝
    private TextView tvPayBtn;//支付按钮
    private TextView tvTreaty;//服务条款

    private String totalMount = "0";//支付总额
    private int payType = AdOrderBaseItem.TYPE_ALIPAY; //默认支付宝
    final int requestChooseFriendCode = 101; //选择好友

    private AdOrderCreateItem orderCreateItem; //订单信息
    private AdOrderBaseItem oldOrderItem; //旧订单信息
    private AdPayPreData prePaydata;//预支付信息

    private UserItem targetUser; //目标用户信息
    private String inputMsg; //找人代付留言
    private AdSendOrderDialog dialog; //找人代付弹框

    private boolean sendMsgFail = false;//发送融云消息;失败=true
    private boolean isPayCallback = true; //支付是否有回调，如果没有回调，请求服务器；true为有回调

    private AdGoodsItem mGoodsItem;//广告位信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_purchase_qualify_act);
        setLabel(getString(R.string.ad_buy));

        EventBus.getDefault().register(this);
        tvServiceName = (TextView) findViewById(R.id.tv_service_name);
        tvServiceTime = (TextView) findViewById(R.id.tv_service_time);
        tvServicePrice = (TextView) findViewById(R.id.tv_service_price);
        ivAlipaySelect = (ImageView) findViewById(R.id.iv_alipay_select);
        ivWechatpaySelect = (ImageView) findViewById(R.id.iv_wechatpay_select);
        ivOtherpaySelect = (ImageView) findViewById(R.id.iv_otherpay_select);
        tvPayBtn = (TextView) findViewById(R.id.tv_pay_btn);
        tvTreaty = (TextView) findViewById(R.id.tv_treaty);

        ivAlipaySelect.setOnClickListener(this);
        ivWechatpaySelect.setOnClickListener(this);
        ivOtherpaySelect.setOnClickListener(this);
        tvPayBtn.setOnClickListener(this);
        tvTreaty.setOnClickListener(this);
        setGoodsInfo();
        getHaveOldOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPayCallback) {
            WaitingDialog.show(this,false);
            tvPayBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isPayCallback) //再检测一遍，防止回调比onresume方法慢，导致重复弹框之类问题
                        getHaveOldOrder();
                }
            },3000);
        }
    }

    //设置支付方式是否可以选择
    public void setSelectEnable(boolean enable){
        ivAlipaySelect.setEnabled(enable);
        ivWechatpaySelect.setEnabled(enable);
        ivOtherpaySelect.setEnabled(enable);
    }
    @Override
    public void finish() {
        super.finish();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestChooseFriendCode) { //选择好友
            switch(resultCode) {
                case RESULT_OK:
                    UserList users = (UserList) data.getSerializableExtra("users");
                    if(users.getList() != null){
                        targetUser = users.getList().get(0);
                        showMessageDialog();
                    }else{
                        setSelectEnable(true);
                        goonPay();
                    }
                    break;

            }
        }
        setSelectEnable(true);
    }
    private void setGoodsInfo(){
        AdHelper.getAdGoods(this, new AdHelper.OnGetGoodsListener() {
            @Override
            public void onSuccess(AdGoodsItem goodsItem) {
                mGoodsItem = goodsItem;
                if (tvServiceName != null && tvServiceTime != null && tvServicePrice != null){
                    tvServiceName.setText(goodsItem.getGoodsname());
                    tvServiceTime.setText(goodsItem.getServicetime());
                    tvServicePrice.setText("￥"+goodsItem.getPrice());
                    totalMount = goodsItem.getPrice();
                }
            }
            @Override
            public void onError() {
            }
        });
    }
    /**找人代付信息框*/
    private void showMessageDialog(){
        if (targetUser == null) return;
        dialog = new AdSendOrderDialog(this,targetUser.getImgurl(),targetUser.getNickname(), SharedUser.getUserNick(),
                getString(R.string.ad_send),getString(R.string.ad_cancel),mGoodsItem);
        dialog.setCancelable(false);
        dialog.setOnTipsListener(new AdSendOrderDialog.OnTipsListener() {
            @Override
            public void onConfirm(String input) {
                inputMsg = input;
                if (orderCreateItem != null && orderCreateItem.getOrderstate() != AdOrderBaseItem.PAYCANCEL
                         && orderCreateItem.getOrderstate() != AdOrderBaseItem.PAYTIMEOUT
                        || oldOrderItem != null  && oldOrderItem.getOrderstate() != AdOrderBaseItem.PAYCANCEL
                        && oldOrderItem.getOrderstate() != AdOrderBaseItem.PAYTIMEOUT) { //存在旧订单
                    String orderno = orderCreateItem != null ? orderCreateItem.getOrderno() : oldOrderItem.getOrderno();
                    String applyUsername = SharedUser.getUserNick();
                    float price = 0;
                    try{
                        price = Float.parseFloat(totalMount);
                    }catch (ClassCastException e){
                        e.printStackTrace();
                    }
                    if (sendMsgFail){ //上次发消息失败，直接发消息
                        sendMessageToUser(String.valueOf(targetUser.getUserid()),orderno,applyUsername,price);
                    }else {
                        postMsgToService(targetUser.getUserid(), input, orderno, applyUsername, price);
                    }
                }else{
                    createOrder();
                }
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
//                goonPay();
                setSelectEnable(true);
                String orderNo = orderCreateItem != null && orderCreateItem.getOrderstate() != AdOrderBaseItem.PAYCANCEL ? orderCreateItem.getOrderno()
                        : oldOrderItem != null && oldOrderItem.getOrderstate() != AdOrderBaseItem.PAYCANCEL ? oldOrderItem.getOrderno() : null;
                //取消订单
                if (orderNo != null && !"".equals(orderNo)) cancelOrder(orderNo);
            }
        });
        dialog.show();
    }
    /**同步留言到服务端*/
    private void postMsgToService(final long targetuserid, String msg, final String orderno, final String fromusername, final float price){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("orderno",orderno);
        params.put("objuserid",targetuserid);
        params.put("message",msg);
        new HttpEntity(this).commonPostData(Method.adsysOrderOtherpayRecord, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                LogUtil.logE("LogUtil",methodName +"--"+result);
                BaseItem item = JSONHelper.getObject(result,BaseItem.class);
                if (item == null) return;
                if (item.getIssuccess() != BaseItem.successTag) {
                    WaitingDialog.dismiss();
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                        ToastUtil.showToast(item.getErr().getMsg());
                        return;
                    }
                }
                String json = JSONHelper.getStringFromObject(item.getResult());
                switch (methodName) {
                    case Method.adsysOrderOtherpayRecord: //同步留言信息
                        sendMessageToUser(String.valueOf(targetuserid),orderno,fromusername,price);
                        break;
                }
            }
        });
    }

    /**发送私聊消息给好友*/
    private void sendMessageToUser(String userid, String orderno, final String fromusername, float price){
        AdOtherPayMessage otherPayMessage = AdOtherPayMessage.obtain(orderno,fromusername,targetUser != null ? targetUser.getNickname() : "",price);
        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, userid, otherPayMessage, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showToast(getString(R.string.ad_msg_send_faild));
                sendMsgFail = true;
                WaitingDialog.dismiss();
            }

            @Override
            public void onSuccess(Integer integer) {
                WaitingDialog.dismiss();
                sendMsgFail = false;
                Bundle bundle = new Bundle();
                bundle.putInt("type",3);
                bundle.putString("name",targetUser != null ? targetUser.getNickname() : "");
                enterActBundle(CommonResultAct.class,bundle);
                //结束广告介绍页
//                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeCloseIntroduceAd));
                if (dialog != null && dialog.isShow()){
                    dialog.dismiss();
                }
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_alipay_select: //支付宝支付
                payType = AdOrderBaseItem.TYPE_ALIPAY;
                break;
            case R.id.iv_wechatpay_select: //微信支付
                payType = AdOrderBaseItem.TYPE_WECHATPAY;
                break;
            case R.id.iv_otherpay_select: //招人代付
                payType = AdOrderBaseItem.TYPE_OTHER;
                break;
            case R.id.tv_pay_btn: //支付按钮
                //微信支付，没安装微信，提示，并拦截
                if (payType == AdOrderBaseItem.TYPE_WECHATPAY && !AppUtils.isWeixinAvilible(this)){
                    ToastUtil.showToast(getString(R.string.ad_install_wechat));
                    return;
                }
                setSelectEnable(false);
                int oldPayType = orderCreateItem != null ? orderCreateItem.getPaytype() : oldOrderItem != null ? oldOrderItem.getPaytype() : 0;
                if (oldPayType != 0 && oldPayType != AdOrderBaseItem.TYPE_OTHER || payType != AdOrderBaseItem.TYPE_OTHER) {
                    if (orderCreateItem == null && oldOrderItem == null ||
                            orderCreateItem != null && orderCreateItem.getOrderstate() == AdOrderBaseItem.PAYCANCEL ||
                            orderCreateItem != null && orderCreateItem.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT ||
                            oldOrderItem != null && oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYCANCEL||
                            oldOrderItem != null && oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT) {
                        createOrder();//创建订单
                    }else{
                        goonPay();
                    }
                }else{
                    choiceFriends();
                }
                break;
            case R.id.tv_treaty: //服务条款
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.stbl_brand_protocol,"");
                if ("".equals(url)){
                    CommonTask.getCommonDicBackground();
                }else{
                    Intent intent = new Intent(this,CommonWeb.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
                break;
        }
        payTypeSwitch();
    }

    /**选择好友*/
    void choiceFriends() {
        Intent intent = new Intent(this, SelectFriendActivity.class);
        intent.putExtra("maxSelected", 1);
        startActivityForResult(intent, requestChooseFriendCode);
    }
    /**旧订单未支付，取消或去支付*/
    private void goonPay() {
        if (oldOrderItem != null && oldOrderItem.getOrderstate() == AdOrderBaseItem.WaitPay && oldOrderItem.getPaytype() == AdOrderBaseItem.TYPE_OTHER){
            TipsDialog2.popup(this, getString(R.string.ad_wait_pay_other_tips), getString(R.string.queding), new TipsDialog2.OnTipsListener() {
                @Override
                public void onConfirm() {
                    finish();
                }

                @Override
                public void onCancel() {}
            });
            return;
        }

        if (orderCreateItem != null || oldOrderItem != null && oldOrderItem.getOrderstate() == AdOrderBaseItem.WaitPay) {
            TipsDialog2.popup(this, getString(R.string.ad_order_no_pay), getString(R.string.ad_cancel),
                    getString(R.string.ad_go_on_pay), new TipsDialog2.OnTipsListener() {
                        @Override
                        public void onConfirm() {
                            payType = orderCreateItem != null ? orderCreateItem.getPaytype() : oldOrderItem.getPaytype();
                            payTypeSwitch();
                            if (payType != AdOrderBaseItem.TYPE_OTHER) {
                                //继续支付
                                String orderNo = orderCreateItem != null ? orderCreateItem.getOrderno() : oldOrderItem.getOrderno();
                                getPayPreInfo(orderNo);
                            }else{
                                pay(null);
                            }
                        }

                        @Override
                        public void onCancel() {
                            String orderNo = orderCreateItem != null ? orderCreateItem.getOrderno() : oldOrderItem.getOrderno();
                            //取消订单
                            cancelOrder(orderNo);
                        }
                    }).setCancelable(false);
        }
        if (oldOrderItem != null && oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYVERIFY){
            TipsDialog2.popup(this, getString(R.string.ad_have_pay_verify), getString(R.string.queding), new TipsDialog2.OnTipsListener() {
                @Override
                public void onConfirm() {
                    finish();
                }

                @Override
                public void onCancel() {}
            });
        }
        if (oldOrderItem != null && oldOrderItem.getOrderstate() == AdOrderBaseItem.HASPAY){
//            TipsDialog2.popup(this, getString(R.string.ad_order_has_pay), getString(R.string.queding), new TipsDialog2.OnTipsListener() {
//                @Override
//                public void onConfirm() {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putBoolean("isJumpToManager",true);
                    enterActBundle(CommonResultAct.class,bundle);
                    //结束广告介绍页
                    EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeCloseIntroduceAd));
                    finish();
//                }
//
//                @Override
//                public void onCancel() {}
//            });
        }
    }
    /**显示选择支付类型*/
    private void payTypeSwitch(){
        ivAlipaySelect.setImageResource(R.drawable.icon_weixuanze);
        ivWechatpaySelect.setImageResource(R.drawable.icon_weixuanze);
        ivOtherpaySelect.setImageResource(R.drawable.icon_weixuanze);
        switch (payType){
            case AdOrderBaseItem.TYPE_ALIPAY:
                ivAlipaySelect.setImageResource(R.drawable.icon_xuanze);
                break;
            case AdOrderBaseItem.TYPE_WECHATPAY:
                ivWechatpaySelect.setImageResource(R.drawable.icon_xuanze);
                break;
            case AdOrderBaseItem.TYPE_OTHER:
                ivOtherpaySelect.setImageResource(R.drawable.icon_xuanze);
                break;
        }
    }

    //调用第三方支付
    private void pay(AdPayPreData prePayData){
        tvPayBtn.setEnabled(false);
        tvPayBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
              tvPayBtn.setEnabled(true);
            }
        },2000);
        switch (payType){
            case AdOrderBaseItem.TYPE_ALIPAY: //支付宝
                if (prePayData == null) return;
                isPayCallback = false;
                new AliPay().pay(mActivity,mGoodsItem != null ? mGoodsItem.getGoodsname() : getString(R.string.ad_goods_name)
                        , prePayData.getOrderno(), prePayData.getPayfee()+"", prePayData.getOrderpayno());
                break;
            case AdOrderBaseItem.TYPE_WECHATPAY://微信
                if (prePayData == null) return;
                isPayCallback = false;
                new WeixinPay().invokeBack(mActivity, prePayData.getWeixinjsonparameters());
                break;
            case AdOrderBaseItem.TYPE_OTHER://招人代付
                choiceFriends();
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
                    payVerify(transactionNo);
                }else{ //失败
                    LogUtil.logE("LogUtil","WEI XIN FAILD");
                    goonPay();
                }
                break;
            case ExternalPayResult.TYPE_ALIPAY:
                isPayCallback = true;
                if (result.getErrCode() == ExternalPayResult.errCodeSucceed) { //成功
                    LogUtil.logE("LogUtil","TYPE_ALIPAY SUCCESS");
                    payVerify("");
                }else{ //失败
                    LogUtil.logE("LogUtil","TYPE_ALIPAY FAILD");
                    goonPay();
                }
                break;
        }
    }
    //判断是新订单支付，还是旧订单支付
    private void payVerify(String transactionNo) {
        WaitingDialog.show(this,false);
        if (orderCreateItem != null && orderCreateItem.getOrderprepayview() != null){
            String orderno = orderCreateItem.getOrderprepayview().getOrderno();
            String orderpayno = orderCreateItem.getOrderprepayview().getOrderpayno();
            doVerify(orderno,orderpayno,transactionNo);
        }else if (prePaydata != null){
            doVerify(prePaydata.getOrderno(),prePaydata.getOrderpayno(),transactionNo);
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
    //取消订单
    private void cancelOrder(String orderNo){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("orderno",orderNo);
        new HttpEntity(this).commonPostData(Method.adsysOrderCancel,params,this);
    }
    //创建新订单
    private void createOrder(){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("paytype",payType);
        params.put("totalamount",totalMount);
        new HttpEntity(this).commonPostData(Method.adsysV1OrderCreate,params,this);
    }
    //查询是否有未支付旧订单
    private void getHaveOldOrder(){
        WaitingDialog.show(this,false);
        Params params = new Params();
        new HttpEntity(this).commonPostData(Method.adsysOrderUnpayGet,params,this);
    }
    //获取预支付信息
    private void getPayPreInfo(String orderNo){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("paytype",payType);
        params.put("orderno",orderNo);
        new HttpEntity(this).commonPostData(Method.adsysOrderPayPre,params,this);
    }
    //支付确认完成后的操作
    private void payVerifyComplete(String json){
        boolean isJumpToManager = false;
        if (json != null){ //支付成功
            JSONObject jobj = JSONObject.parseObject(json);
            if (jobj.containsKey("orderstate")){
                int status = jobj.getInteger("orderstate");
                if (status == AdOrderBaseItem.HASPAY){
                    isJumpToManager = true;
                }
            }
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type",1);
        bundle.putBoolean("isJumpToManager",isJumpToManager);
        enterActBundle(CommonResultAct.class,bundle);
        //获取是否广告主
        AdTask.getIsAdvertiser(this);
        //结束广告介绍页
        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeCloseIntroduceAd));
        finish();
    }
    //支付验证失败弹框
    private void verifyFaild(){
//        TipsDialog2.popup(this, getString(R.string.ad_pay_verify_faild_tips), getString(R.string.queding), new TipsDialog2.OnTipsListener() {
//            @Override
//            public void onConfirm() {
                Bundle bundle = new Bundle();
                bundle.putInt("type",6);
                bundle.putBoolean("isJumpToManager",false);
                enterActBundle(CommonResultAct.class,bundle);
                //结束广告介绍页
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeCloseIntroduceAd));
                finish();
//            }
//
//            @Override
//            public void onCancel() {}
//        });
    }
    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        setSelectEnable(true);
        LogUtil.logE("LogUtil",methodName +"--"+result);
        BaseItem item = JSONHelper.getObject(result,BaseItem.class);
        if (item == null) return;
        if (item.getIssuccess() != BaseItem.successTag) {
            switch (methodName) {
                case Method.adsysOrderCancel://取消订单
                case Method.adsysOrderPayPre://预支付信息
                case Method.adsysV1OrderCreate: //创建订单
                    goonPay();
                    break;
                case Method.adsysOrderPayVerify: //支付验证
                    verifyFaild();
                    break;
            }
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                ToastUtil.showToast(item.getErr().getMsg());
            }
            return;
        }
        String json = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName){
            case Method.adsysV1OrderCreate: //创建订单
                orderCreateItem = JSONHelper.getObject(json,AdOrderCreateItem.class);
                if (orderCreateItem == null) return;
                //结束代付订单详情页
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeCloseHelpOrder));
                if (payType != AdOrderBaseItem.TYPE_OTHER) {
                    prePaydata = orderCreateItem.getOrderprepayview();
                    pay(prePaydata);
                }else{
                    String orderNo = orderCreateItem != null ? orderCreateItem.getOrderno() : oldOrderItem.getOrderno();
                    float price = 0;
                    try{
                        price = Float.parseFloat(totalMount);
                    }catch (ClassCastException e){
                        e.printStackTrace();
                    }
                    postMsgToService(targetUser.getUserid(),inputMsg,orderNo,SharedUser.getUserNick(),price);
                }

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
                payVerifyComplete(json);
                break;
            case Method.adsysOrderUnpayGet: //是否有未支付订单
                oldOrderItem = JSONHelper.getObject(json,AdOrderBaseItem.class);
                if (oldOrderItem != null && (oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYCANCEL || oldOrderItem.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT)){
                    oldOrderItem = null;//取消、超时的订单后续不做操作
                }
                payType = orderCreateItem != null ? orderCreateItem.getPaytype() : oldOrderItem != null ? oldOrderItem.getPaytype() : AdOrderBaseItem.TYPE_ALIPAY;
                payTypeSwitch();
                goonPay();
                break;
            case Method.adsysOrderPayPre:  //预支付信息
                prePaydata = JSONHelper.getObject(json,AdPayPreData.class);
                pay(prePaydata);
                break;
            case Method.adsysOrderCancel: //取消订单
                oldOrderItem = null;
                prePaydata = null;
                orderCreateItem = null;
                finish();
                break;
        }
    }
}
