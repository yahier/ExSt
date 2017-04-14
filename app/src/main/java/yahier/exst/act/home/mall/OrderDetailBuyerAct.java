package yahier.exst.act.home.mall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.home.seller.OrderDetailAct;
import com.stbl.stbl.act.home.seller.SellerExpressAct;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.model.OrderState;
import com.stbl.stbl.model.SellerOrderInfo;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.Payment.OnPayResult;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.TipsEditDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelMenu;
import com.stbl.stbl.util.WheelMenu.OnWheelMenuListener;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;

/**
 * 买家订单详情
 *
 * @author ruilin
 */
public class OrderDetailBuyerAct extends OrderDetailAct implements OnPayResult {

    long mOrderId;
    private String TAG = "OrderDetailBuyerAct";

//    final String[] payType = new String[]{"微信支付", "支付宝支付", "金豆"};

    View mView_pay;
    TextView tv_pay_type;
    WheelMenu mPayMenu;
    int mPayTypeIndex;
    //填写订单号requestcode
    public static final int REQUEST_EXPRESS = 0x1211;
    private final static int REQUEST_REFUND_CODE = 0x11; //退款申请成功返回刷新当前页
    View vYdgyLine;
    TextView tvYdgyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = getIntent();
        mView_pay = findViewById(R.id.block_pay_setting);
        tv_pay_type = (TextView) findViewById(R.id.tv_pay_type);
        mOrderId = it.getLongExtra("orderid", 0);
        TextView tvUser = (TextView) findViewById(R.id.tv_user);
        tvUser.setText(R.string.mall_contact_seller);
        vYdgyLine = findViewById(R.id.v_ydgy_line);
        tvYdgyId = (TextView) findViewById(R.id.tv_ydgy_id);
//		getOrderDetail(mOrderId);
        requireData(mOrderId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_REFUND_CODE) {
                boolean refresh = data.getBooleanExtra("refresh", false);
                if (refresh) {
                    requireData(mOrderId);
                }
            } else if (requestCode == REQUEST_EXPRESS) {
                String expressno = data.getStringExtra("KEY_NUM");
                int expresscomtype = data.getIntExtra("KEY_NAME", 0);
                refundDeliver(mOrderId, expressno, expresscomtype);
            }
        }
    }

    //买家填写订单号退货
    private void refundDeliver(long orderid, String expressno, int expresscomtype) {
        org.json.JSONObject json = new org.json.JSONObject();
        try {
            json.put("orderid", orderid);
            json.put("expressno", expressno);
            json.put("expresscomtype", expresscomtype);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.logD("LogUtil", "-----" + json.toString());
//		new HttpEntity(this).commonPostJson(Method.replyRefundDeliver, json.toString(),this);
        new HttpUtil(this, null).postJson(Method.replyRefundDeliver, json.toString(), this);
    }

    //设置用户头像
    @Override
    protected void setPhoto(final SellerOrderInfo info) {
        if (null != info.shopinfoview) {

            findViewById(R.id.tv_user).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    ThemeActivity.isMerchant(info.shopinfoview.getContactshopid());
//                    RongIM.getInstance().startPrivateChat(mAct, String.valueOf(info.shopinfoview.getContactshopid()),
//                            info.shopinfoview.getShopname());
                    RongIM.getInstance().startPrivateChat(mAct, String.valueOf(info.shopinfoview.getContactshopid()),
                            info.shopinfoview.getAlias() == null || info.shopinfoview.getAlias().equals("") ? info.shopinfoview.getShopname() : info.shopinfoview.getAlias());

                }
            });
        }
    }

    @Override
    protected void fillState(SellerOrderInfo info) {
        if (info.courierid != null && !info.courierid.equals("")){ //一点公益信使号
            vYdgyLine.setVisibility(View.VISIBLE);
            tvYdgyId.setVisibility(View.VISIBLE);
            tvYdgyId.setText(String.format(getString(R.string.mall_ydgy_id),info.courierid));
        }
        if (info.paytype != 0) {
            mPayTypeIndex = info.paytype;
            if (mPayTypeIndex < Payment.getPayTypeNames(this).size()){
                tv_pay_type.setText(Payment.getPayTypeNames(this).get(mPayTypeIndex-1));
            }else{
                if(Payment.getPayTypeNames(this).size() == 1){
                    mPayTypeIndex = Payment.TYPE_ALIPAY;
                    tv_pay_type.setText(getString(R.string.mall_alipay));
                }
            }
        }
        if (info.orderstate == OrderState.WAIT_FOR_PAY){
            setPaySettingView(true,true);
        }else{
            setPaySettingView(true,false);
        }
        setStatus(info.orderstatename, info.orderstatedescribe);
        showCloseMsg(info);
        mView_refund.setVisibility(View.GONE);
        switch (info.orderstate) {
            case OrderState.WAIT_FOR_PAY:  //待付款
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_delete_order));
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_order_pay));
                break;
            case OrderState.WAIT_FOR_SEND: //待发货
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_refund_apply));//退款
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_tip_deliver_goods));
                break;
            case OrderState.WAIT_FOR_RECEIVE: //待收货
                mView_wuliu.setVisibility(View.GONE);
                mView_notagree.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_check_logistics));
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_take_delivery_of_goods));
                mView_refund.setVisibility(View.VISIBLE);
                break;
            case OrderState.FINISH:  //交易成功
                mView_wuliu.setVisibility(View.VISIBLE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_return_apply));
                mView_agree.setVisibility(View.VISIBLE);
                if (info.isappraise == 1)
                    mView_agree.setText(getString(R.string.mall_remark_done));
                else
                    mView_agree.setText(getString(R.string.mall_remark));
                break;
            case OrderState.RETURN_APPLY: //申请退货中
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.GONE);
                mView_refund.setVisibility(View.GONE);
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_return_cancel));
                break;
            case OrderState.RETURN_WAIT_FOR_SEND: //卖家已同意退货
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_please_input_return));
                break;
            case OrderState.RETURN_DISAGREE_AMOUNT: //卖家不同意退款
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_refund_cancel));
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_help));
                break;
            case OrderState.RETURN_DISAGREE: //卖家不同意退货
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_return_cancel));
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_help));
                break;
            case OrderState.RETURN_FINISH: //退款成功
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.GONE);
                break;
            case OrderState.CLOSE: //交易完成
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.FAIL: //交易失败
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.GONE);
                break;
            case OrderState.RETURN_PAY_APPLY: //申请退款中
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_refund_cancel));
                mView_notagree.setVisibility(View.GONE);
                break;
            case OrderState.PAYED_CONFIRM://支付确认中
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.RETURN_WAITING: //退货中
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.VISIBLE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_agree.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setText(getString(R.string.mall_service_hot));
                break;
            case OrderState.APPLY_SERVICE: //客服介入-退款
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.VISIBLE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_refund_cancel));
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_service_hot));
                break;
            case OrderState.APPLY_SERVICE2: //客服介入-退货
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.VISIBLE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_notagree.setVisibility(View.VISIBLE);
                mView_notagree.setText(getString(R.string.mall_return_cancel));
                mView_agree.setVisibility(View.VISIBLE);
                mView_agree.setText(getString(R.string.mall_service_hot));
                break;
            case OrderState.CANCEL: //订单取消
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.VISIBLE);
                mView_agree.setVisibility(View.GONE);
                mView_notagree.setVisibility(View.GONE);
                break;
            case OrderState.FUND_TO_SELLER: //退款中
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.VISIBLE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            default:
//			setStatus("未知状态", "" + info.orderstate);
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 取消、退款、关闭
     */
    private void showCloseMsg(SellerOrderInfo info) {
        if (info == null) return;
        String sellerunagreemsg = info.sellerunagreemsg; //买家取消原因
        String sellerclosemsg = info.sellerclosemsg; //卖家关闭原因
        if (sellerunagreemsg != null && !sellerunagreemsg.equals("")) {
            mView_cancelReason.setVisibility(View.VISIBLE);
            tvReasonType.setText(R.string.mall_turn_down_reason);
            tvReason.setText(sellerunagreemsg);
        }
        if (sellerclosemsg != null && !sellerclosemsg.equals("")) {
            mView_cancelReason.setVisibility(View.VISIBLE);
            tvReasonType.setText(R.string.mall_close_reason);
            tvReason.setText(sellerclosemsg);
        }
    }

    @Override
    protected void onClickByState(View v) {
        Intent it;
        switch (mInfo.orderstate) {
            case OrderState.WAIT_FOR_PAY:
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 删除订单
//                        showCancelWindow();
                        orderDelete(mOrderId);
                        break;
                    case R.id.btn_agree:    // 付款
                        int payType = mPayTypeIndex;// + (mPayTypeIndex == 2 ? 2 : 1);
                        Log.e(TAG, " --------------- 付款 ： " + payType + " ------------------------ ");
                        new Payment(Payment.PAY_FOR_ORDER, this, this).prePay(payType, mOrderId);
                        break;
                }
                break;
            case OrderState.WAIT_FOR_SEND://待发货
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 退款申请
                        applyRefund(mOrderId,false);
                        break;
                    case R.id.btn_agree:    // 提醒发货
                        remindSend(mOrderId);
                        break;
                }
                break;
            case OrderState.WAIT_FOR_RECEIVE://待收货
                switch (v.getId()) {
                    case R.id.btn_agree:
                        TipsDialog.popup(this, getString(R.string.mall_delicery_of_goods_ok), getString(R.string.mall_waiting), getString(R.string.mall_receive), new OnTipsListener() {
                            @Override
                            public void onConfirm() {
                                doConfirmReceived(mOrderId);
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                        break;
                    case R.id.btn_notagree:
                        super.requireExpress(mInfo.expressno, mInfo.orderid);
                        break;
                    case R.id.btn_refund:
                        applyRefund(mOrderId,true);
                        break;
                }
                break;
            case OrderState.RETURN_DISAGREE:    // 卖家不同意退货
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 放弃退货
                        giveUpApply(mOrderId, 1);
                        break;
                    case R.id.btn_agree:    // 人工介入,投诉
//				TipsDialog.showOrderHumanHelp(this);
                        showHelperDialog();
                        break;
                }
                break;
            case OrderState.RETURN_DISAGREE_AMOUNT:
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 放弃退款
                        giveUpApply(mOrderId, 0);
                        break;
                    case R.id.btn_agree:    // 人工介入,投诉
//				TipsDialog.showOrderHumanHelp(this);
                        showHelperDialog();
                        break;
                }
                break;
            case OrderState.FINISH:                // 交易完成
                switch (v.getId()) {
                    case R.id.btn_agree:    // 评价商品
                        if (mInfo.isappraise != 1) {
                            finish();
                            commentGoods();
                        } else {
                            TipsDialog.popup(this, getString(R.string.mall_you_remark_the_goods), getString(R.string.mall_confirm2));
                        }
                        break;
                    case R.id.btn_notagree:    // 申请退货
                        finish();
                        applyReturn(mOrderId);
                        break;
                }
                break;
            case OrderState.APPLY_SERVICE: //客服介入-退款
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 放弃退款
//                        giveUpApply(mOrderId, 0);
                        helperCancel(mOrderId);
                        break;
                    case R.id.btn_agree:
                        TipsDialog.showOrderHumanHelp(this);
                        break;
                }
                break;
            case OrderState.APPLY_SERVICE2: //客服介入-退货
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 放弃退货
                        giveUpApply(mOrderId, 1);
                        break;
                    case R.id.btn_agree:
                        TipsDialog.showOrderHumanHelp(this);
                        break;
                }
                break;
            case OrderState.RETURN_WAIT_FOR_SEND:
                Intent intent = new Intent(mAct, SellerExpressAct.class);
                mAct.startActivityForResult(intent, MallOrderFrag.REQUEST_EXPRESS);
                break;
            case OrderState.RETURN_PAY_APPLY: //申请退款中
                if (v.getId() == R.id.btn_agree) {
                    giveUpApply(mOrderId,0);
                }
                break;
            case OrderState.RETURN_APPLY://申请退货中
                giveUpApply(mOrderId, 1);
                break;
            case OrderState.RETURN_WAITING: //退货中
//                showHelperDialog();
                TipsDialog.showOrderHumanHelp(this);
                break;
            default:
                break;
        }
    }

    private OnWheelMenuListener onPayMenu = new OnWheelMenuListener() {
        @Override
        public void onTagOk(String tag, int index) {
//            mPayTypeIndex = index;
            mPayTypeIndex = Payment.getPayType(tag);
            Log.e(TAG, " -------------------- PayTypeIndex : " + index + " -- " + mPayTypeIndex + " --------------------- ");
            tv_pay_type.setText(tag);
        }
    };

    /**
     *
     * @param ifShow
     * @param clickEnable 是否可以点击
     */
    void setPaySettingView(boolean ifShow,boolean clickEnable) {
        if (ifShow) {
            if (mPayTypeIndex != 0 && mPayTypeIndex <= Payment.getPayTypeNames(this).size()){
                tv_pay_type.setText(Payment.getPayTypeNames(this).get(mPayTypeIndex-1));
            }else{
                if(Payment.getPayTypeNames(this).size() == 1){
                    mPayTypeIndex = Payment.TYPE_ALIPAY;
                    tv_pay_type.setText(getString(R.string.mall_alipay));
                }
            }
            mView_pay.setVisibility(View.VISIBLE);
            if(!clickEnable){
                tv_pay_type.setCompoundDrawables(null,null,null,null);
                return;
            }
            mView_pay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPayMenu == null) {
//                        List<String> listType = Arrays.asList(Payment.payTypesName);
                        List<String> listType = Payment.getPayTypeNames(OrderDetailBuyerAct.this);
                        mPayMenu = new WheelMenu(mAct, listType);
                        mPayMenu.setOnWheelMenuListener(onPayMenu);
                    }
                    mPayMenu.show();
                }
            });
        } else {
            mView_pay.setVisibility(View.GONE);
        }
    }

    // 取消投诉
    void helperCancel(final long orderid) {
       TipsDialog.popup(mAct, getString(R.string.mall_refund_cancel_tips), getString(R.string.mall_cancel),
               getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
                JSONObject json = new JSONObject();
                json.put("orderid", orderid);
                new HttpUtil(mAct, null).postJson(Method.orderHelperCancel, json.toJSONString(), OrderDetailBuyerAct.this);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    // 删除
    void orderDelete(final long orderid) {
        TipsDialog.popup(mAct, getString(R.string.mall_delete_order_tips), getString(R.string.mall_cancel),
                getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                //showGetGoodsWindow(item.orderid);
                JSONObject json = new JSONObject();
                json.put("orderid", orderid);
                new HttpUtil(mAct, null).postJson(Method.orderDelete, json.toJSONString(), OrderDetailBuyerAct.this);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });

    }
    public void showHelperDialog() {
        TipsEditDialog dialog = new TipsEditDialog(this, getString(R.string.mall_help), getString(R.string.mall_input_help_reason),
                getString(R.string.mall_cancel), getString(R.string.mall_confirm2));
        dialog.setOnTipsListener(new TipsEditDialog.OnTipsListener() {
            @Override
            public void onConfirm(String input) {
                doHelperOrder(input);
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show();
    }

    // 订单投诉
    void doHelperOrder(String reason) {
        LogUtil.logE("投诉订单id:" + mOrderId);
        JSONObject json = new JSONObject();
        json.put("orderid", mOrderId);
        json.put("reason", reason);
        new HttpUtil(mAct, null).postJson(Method.buyerOrderHelper, json.toJSONString(), this);

    }

    /**
     * 取消订单
     */
//    public void showCancelWindow() {
//        TipsEditDialog dialog = new TipsEditDialog(this, "取消原因", "请输入取消原因", "取消", "确定");
//        dialog.setOnTipsListener(new TipsEditDialog.OnTipsListener() {
//            @Override
//            public void onConfirm(String input) {
//                doCancelOrder(input);
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });
//        dialog.show();
//    }

    // 取消订单
//    void doCancelOrder(String reason) {
//        LogUtil.logE("取消订单id:" + mOrderId);
//        JSONObject json = new JSONObject();
//        json.put("orderid", mOrderId);
//        json.put("reason", reason);
//        new HttpUtil(mAct, null).postJson(Method.orderCancel, json.toJSONString(), this);
//
//    }

    // 确认收货
    void doConfirmReceived(long orderid) {
        JSONObject json = new JSONObject();
        json.put("orderid", orderid);
        new HttpUtil(this, null).postJson(Method.orderConfirmGet, json.toJSONString(), this);
    }

    // 申请退货
    void applyReturn(long orderid) {
        Intent it = new Intent(this, MallReturnApplyAct.class);
        it.putExtra("orderid", orderid);
        this.startActivity(it);
    }

    // 评价商品
    void commentGoods() {
        Intent it = new Intent(this, GoodsRemarkAddAct.class);
        it.putExtra(GoodsRemarkAddAct.KEY_ORDER_ID, mOrderId);
        it.putExtra(GoodsRemarkAddAct.KEY_GOODS_LIST, mDatas);
        this.startActivity(it);
    }

    // 提醒发货
    void remindSend(long orderid) {
        JSONObject json = new JSONObject();
        json.put("orderid", orderid);
        new HttpUtil(this, null).postJson(Method.orderRemindSend, json.toJSONString(), this);
    }

    /**
     * 申请退款
     * @param orderid 订单id
     * @param isReturn 是否调用退货接口，在待收货状态退款
     **/
    void applyRefund(long orderid,boolean isReturn) {
//		JSONObject json = new JSONObject();
//		json.put("orderid", orderid);
//		json.put("reason", "--");
//		json.put("reasontype", 1);	//申请退款类型(七天无理由退货 = 1,收到商品破损 = 2, 收到商品不符 = 3, 买家收货拒签 = 4, 其它 = 5,)
//		new HttpUtil(this, null).postJson(Method.replyRefund, json.toJSONString(), this);
        Intent intent = new Intent(this, MallRefundApplyAct.class);
        intent.putExtra("orderid", orderid);
        intent.putExtra("isreturn",isReturn);
        startActivityForResult(intent, REQUEST_REFUND_CODE);
    }

    /*
     *  放弃退款/退货申请
     *  0:退款
     *  1:退货
     */
    void giveUpApply(final long orderid, final int type) {
        TipsDialog.popup(this, getString(R.string.mall_confirm_cancel) + (type == 0 ? getString(R.string.mall_refund) : getString(R.string.mall_return_goods)) + getString(R.string.mall_ma),
                getString(R.string.mall_cancel), getString(R.string.mall_confirm2), new OnTipsListener() {
            @Override
            public void onConfirm() {
                JSONObject json = new JSONObject();
                json.put("orderid", orderid);
                new HttpUtil(mAct, null).postJson((type == 0 ? Method.replyRefundGiveup : Method.replyReturnGiveup), json.toJSONString(), mAct);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        super.onHttpResponse(methodName, json, handle);

        switch (methodName) {
            case Method.orderConfirmGet:
                showToast(getString(R.string.mall_confirm_success));
                finish();
                Intent intent = new Intent(this, MallOrderAfterReceived.class);
                intent.putExtra("orderid", mOrderId);
                intent.putExtra("list", mInfo.productsList);
                startActivity(intent);
                //更新订单页面待收货订单数量
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 3));
                //更新订单列表
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_5));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                break;
            case Method.orderHelperCancel://取消投诉
            case Method.replyRefundGiveup://取消退款
            case Method.replyReturnGiveup://取消退货
                ToastUtil.showToast(mAct, getString(R.string.mall_operate_success));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                //更新订单页面待付款订单数量
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 1));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                finish();
                break;
            case Method.orderDelete: //删除订单
            case Method.orderCancel:
                ToastUtil.showToast(mAct, getString(R.string.mall_operate_success));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                //更新订单页面待付款订单数量
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 1));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                finish();
                break;
            case Method.buyerOrderHelper:
                ToastUtil.showToast(mAct, getString(R.string.mall_operate_success));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                //更新订单页面待付款订单数量
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                finish();
                break;
            case Method.replyRefundDeliver: //退货
                requireData(mOrderId);
                ToastUtil.showToast(this, getString(R.string.mall_goods_mail_return));
                break;
        }
    }

    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
        finish();
    }

    @Override
    public void onPayResult(boolean isSuccess) {
        if (isSuccess) {
            //通知我的模块，订单数量更新
            EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
            //更新订单页面待付款订单数量
            EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 1));
            //更新订单列表
            EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_5));
            finish();
        }
    }
}
