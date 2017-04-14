package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.home.seller.ExpressDetailAct;
import com.stbl.stbl.act.home.seller.SellerExpressAct;
import com.stbl.stbl.adapter.mall.OrderGoodsAdapter;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.model.OrderState;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.Payment.OnPayResult;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsEditDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelString;
import com.stbl.stbl.util.WheelString.OnTimeWheelListener;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

public class MallOrderAdapter1 extends RCommonAdapter<MallOrder> implements FinalHttpCallback, OnTimeWheelListener, OnPayResult {
    OnOrderItemClickListener mListener;
    Activity mAct;
    MallOrderFrag frag;
    MallOrderAdapter1 mFra;
    MallOrder operateOrder;
    private long refundOrderId;//当前买家点击的回邮的订单id
    // final String orderCancel = "orderCancel";

    final String pay = "pay";
    final String humanHelp = "humanHelp";
    final String serviceHelp = "serviceHelp";//客服介入

    public MallOrderAdapter1(Activity mAct, List<MallOrder> mDatas) {
        super(mAct, mDatas, R.layout.mall_order_item);
        this.mAct = mAct;
        mFra = this;
    }

    public MallOrderAdapter1(MallOrderFrag frag, List<MallOrder> mDatas) {
        super(frag.getActivity(), mDatas, R.layout.mall_order_item);
        this.frag = frag;
        this.mAct = frag.getActivity();
        mFra = this;
    }

    //给
    public long getRefundOrderId() {
        return refundOrderId;
    }

    public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void convert(RViewHolder helper, final MallOrder item) {
        Button btn1 = helper.getView(R.id.order_btn1);
        Button btn2 = helper.getView(R.id.order_btn2);

        int state = item.getOrderstate();
        switch (state) {
            case 21000:
                setCommon(btn1, mAct.getString(R.string.mall_delete_order), Method.orderDelete, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_order_pay), pay, item, helper.getPosition());
                break;
            case 21200://退款申请
                setCommon(btn1, mAct.getString(R.string.mall_refund_apply), Method.replyRefund, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_tip_deliver_goods), Method.orderRemindSend, item, helper.getPosition());
                break;
            case 21300:
                setCommon(btn1, mAct.getString(R.string.mall_check_logistics), Method.expressInfo, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_take_delivery_of_goods), Method.orderConfirmGet, item, helper.getPosition());
                break;
            case 21900:
                setCommon(btn1, mAct.getString(R.string.mall_return_apply), Method.orderApplyReturn, item, helper.getPosition());
                String remark = mAct.getString(R.string.mall_remark);
                if (item.getIsappraise() == 1) remark = mAct.getString(R.string.mall_remark_done);
                setCommon(btn2, remark, Method.addGoodsComment, item, helper.getPosition());
                break;
            case 21201:
                btn1.setVisibility(View.GONE);
                setCommon(btn2, mAct.getString(R.string.mall_refund_cancel), Method.replyRefundGiveup, item, helper.getPosition());
                break;
            case 21202:
                setCommon(btn1, mAct.getString(R.string.mall_refund_cancel), Method.replyRefundGiveup, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_help), humanHelp, item, helper.getPosition());
                break;
            case 22101:
                setCommon(btn1, mAct.getString(R.string.mall_return_cancel), Method.replyReturnGiveup, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_help), humanHelp, item, helper.getPosition());
                break;
            case 22102:
                setCommon(btn1, mAct.getString(R.string.mall_refund_cancel), Method.orderHelperCancel, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_service_hot), serviceHelp, item, helper.getPosition());
                break;
            case 22103:
                setCommon(btn1, mAct.getString(R.string.mall_return_cancel), Method.replyReturnGiveup, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_service_hot), serviceHelp, item, helper.getPosition());
                break;
            case 22200:
                btn1.setVisibility(View.GONE);
//			btn2.setText("发货");
//			btn2.setText("商品回邮");
                setCommon(btn2, mAct.getString(R.string.mall_please_input_return), Method.replyRefundDeliver, item, helper.getPosition());
                break;
            case 22400:
                btn1.setVisibility(View.GONE);
                setCommon(btn2, mAct.getString(R.string.mall_help), humanHelp, item, helper.getPosition());
                break;
            case 21001:
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
//                setCommon(btn2, "删除订单", Method.orderDelete, item, helper.getPosition());
                break;
            case 29997:
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
//                setCommon(btn2, "删除订单", Method.orderDelete, item, helper.getPosition());
                break;
            case 29999:
                btn1.setVisibility(View.GONE);
                String remark2 = mAct.getString(R.string.mall_remark);
                if (item.getIsappraise() == 1) remark2 = mAct.getString(R.string.mall_remark_done);
                setCommon(btn2, remark2, Method.addGoodsComment, item, helper.getPosition());
//                setCommon(btn2, "删除订单", Method.orderDelete, item, helper.getPosition());
                break;
            case 22100:
                btn1.setVisibility(View.GONE);
                setCommon(btn2, mAct.getString(R.string.mall_return_cancel), Method.replyReturnGiveup, item, helper.getPosition());
                break;
            case 22300: //申请退货中
                btn1.setVisibility(View.GONE);
//                setCommon(btn2, "投诉", humanHelp, item, helper.getPosition());
                setCommon(btn2, mAct.getString(R.string.mall_service_hot), serviceHelp, item, helper.getPosition());
                break;
            default:
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                break;
        }

        helper.getView(R.id.item).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mAct, OrderDetailBuyerAct.class);
                intent.putExtra("orderid", item.orderid);
                mAct.startActivity(intent);
            }
        });

        helper.setText(R.id.tvMoneyCount, String.valueOf(item.getRealpayamount()));
        helper.setText(R.id.tvOrderCode, String.valueOf(item.orderid));
        LinearLayout llPage = (LinearLayout) helper.getView(R.id.llPage);
        OrderGoodsAdapter adapter = item.getAdapter(mActivity);
        adapter.setDividerColor(R.color.gray3);
        adapter.setDividerHeight(0.5f);
        adapter.setAdapter(llPage);
        adapter.setOnItemClickListener(new STBLBaseGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, ViewGroup parentView) {
                Intent intent = new Intent(mAct, OrderDetailBuyerAct.class);
                intent.putExtra("orderid", item.orderid);
                mAct.startActivity(intent);
            }
        });

        TextView tvStateValue = helper.getView(R.id.tvStateValue);
        tvStateValue.setText(item.getOrderstatename());

    }

    MallOrder mallOrder;
    int operatePosition;

    void setCommon(Button btn, String value, final String methodName, final MallOrder item, final int position) {
        btn.setVisibility(View.VISIBLE);
        this.mallOrder = item;
        btn.setText(value);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                operatePosition = position;
                switch (methodName) {
                    case Method.orderDelete:
                        orderDelete(item.orderid);
                        break;
                    case Method.addGoodsComment:
                        doRemark(item);
                        break;
                    case Method.orderCancel:
                        //doCancelOrder(item.orderid);
                        showCancelWindow(item.orderid);
                        break;
                    case Method.orderRemindSend:
                        remindSend(item.orderid);
                        break;
                    case Method.orderConfirmGet:
                        TipsDialog.popup(mAct, mAct.getString(R.string.mall_take_delivery_of_goods_tips), mAct.getString(R.string.mall_cancel), mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

                            @Override
                            public void onConfirm() {
                                //showGetGoodsWindow(item.orderid);
                                doConfireReceived(item.orderid);
                            }

                            @Override
                            public void onCancel() {
                                // TODO Auto-generated method stub

                            }
                        });
//					doConfireReceived(item.orderid);
                        break;
                    case Method.orderApplyReturn:
                        applyReturn(item);
                        break;
                    case pay:
                        operateOrder = item;
                        if (operateOrder.getPaytype() != 0) { //生成订单时选择了支付方式，默认给
                            new Payment(Payment.PAY_FOR_ORDER, mActivity, MallOrderAdapter1.this).prePay(operateOrder.getPaytype(), operateOrder.orderid);
                        } else {
                            choosePayType();
                        }
                        break;
                    case Method.expressInfo:
                        lookExpress(item);
                        break;
                    case Method.replyRefundGiveup:
                        giveUpRefund(item);
                        break;
                    case Method.replyRefund:
                        applyRefund(mAct, item.orderid, mFra);
                        break;
                    case humanHelp: //投诉
//						TipsDialog.showOrderHumanHelp(mAct);
                        showHelperDialog(item.orderid);
                        break;
                    case serviceHelp: //客服介入
                        TipsDialog.showOrderHumanHelp(mAct);
                        break;
                    case Method.replyRefundDeliver: //买家回邮
                        refundOrderId = item.orderid;
                        Intent intent = new Intent(mAct, SellerExpressAct.class);
                        if (frag != null) {
                            frag.startActivityForResult(intent, MallOrderFrag.REQUEST_EXPRESS);
                        } else {
                            mAct.startActivityForResult(intent, MallOrderFrag.REQUEST_EXPRESS);
                        }
                        break;
                    case Method.replyReturnGiveup: //放弃退货
                        giveUpReturn(item);
                        break;
                    case Method.orderHelperCancel: //取消投诉
                        helperCancel(item);
                        break;
                }
            }
        });

    }

    public void showHelperDialog(final long orderid) {
        TipsEditDialog dialog = new TipsEditDialog(mAct, mAct.getString(R.string.mall_help), mAct.getString(R.string.mall_input_help_reason),
                mAct.getString(R.string.mall_cancel), mAct.getString(R.string.mall_confirm2));
        dialog.setOnTipsListener(new TipsEditDialog.OnTipsListener() {
            @Override
            public void onConfirm(String input) {
                doHelperOrder(orderid, input);
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show();
    }

    // 订单投诉
    void doHelperOrder(long orderid, String reason) {
        LogUtil.logE("投诉订单id:" + orderid);
        JSONObject json = new JSONObject();
        json.put("orderid", orderid);
        json.put("reason", reason);
        new HttpEntity(mAct).commonPostJson(Method.buyerOrderHelper, json.toJSONString(), this);

    }

    void choosePayType() {
        WheelString list = new WheelString();
        list.setOnTimeWheelListener(this);
        List<String> listType = new ArrayList<String>();
//        listType.add("微信支付");
//        listType.add("支付宝支付");
//        listType.add("余额支付");
//        listType.add("金豆");
//        listType = Arrays.asList(Payment.payTypesName);
        listType = Payment.getPayTypeNames(mAct);
        list.chooseTime(mAct, listType);
    }

//	Dialog dialog;
//	EditText inputReason = null;

    /**
     * 取消订单
     */
    public void showCancelWindow(final long orderid) {
        TipsEditDialog dialog = new TipsEditDialog(mAct, mAct.getString(R.string.mall_cencel_reason), mAct.getString(R.string.mall_input_cancel_reason),
                mAct.getString(R.string.mall_cancel), mAct.getString(R.string.mall_confirm2));
        dialog.setOnTipsListener(new TipsEditDialog.OnTipsListener() {
            @Override
            public void onConfirm(String input) {
                doCancelOrder(orderid, input);
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show();
    }

    // 取消投诉
    void helperCancel(final MallOrder item) {
        TipsDialog.popup(mAct, mAct.getString(R.string.mall_refund_cancel_tips), mAct.getString(R.string.mall_cancel),
                mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
                Params params = new Params();
                params.put("orderid", item.orderid);
                new HttpEntity(mAct).commonPostData(Method.orderHelperCancel, params, MallOrderAdapter1.this);
            }

            @Override
            public void onCancel() {
            }
        });
    }


    // 查看物流
    void lookExpress(MallOrder item) {
        Intent intent = new Intent(mAct, ExpressDetailAct.class);
        intent.putExtra(ExpressDetailAct.KEY_ORDER_ID, "" + item.orderid);
        mAct.startActivity(intent);
    }

    // 申请退款
    public static void applyRefund(Activity act, long orderid, FinalHttpCallback callback) {
//		Params params = new Params();
//		params.put("orderid", orderid);
//		params.put("reasontype", 1);
//		params.put("reason", "--");
//		new HttpEntity(act).commonPostData(Method.replyRefund, params, callback);
        Intent intent = new Intent(act, MallRefundApplyAct.class);
        intent.putExtra("orderid", orderid);
        act.startActivity(intent);
    }

    // 放弃退款申请
    void giveUpRefund(final MallOrder item) {
        TipsDialog.popup(mAct, mAct.getString(R.string.mall_refund_cancel_tips), mAct.getString(R.string.mall_cancel),
                mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                //showGetGoodsWindow(item.orderid);
                Params params = new Params();
                params.put("orderid", item.orderid);
                new HttpEntity(mAct).commonPostData(Method.replyRefundGiveup, params, MallOrderAdapter1.this);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });
    }

    // 放弃退货申请
    void giveUpReturn(final MallOrder item) {
        TipsDialog.popup(mAct, mAct.getString(R.string.mall_return_cancel_tips), mAct.getString(R.string.mall_cancel),
                mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                //showGetGoodsWindow(item.orderid);
                Params params = new Params();
                params.put("orderid", item.orderid);
                new HttpEntity(mAct).commonPostData(Method.replyReturnGiveup, params, MallOrderAdapter1.this);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });
    }

    // 评价
    void doRemark(MallOrder item) {
        if (item.getIsappraise() != 1) {
            Intent it = new Intent(mAct, GoodsRemarkAddAct.class);
            it.putExtra(GoodsRemarkAddAct.KEY_ORDER_ID, item.orderid);
            it.putExtra(GoodsRemarkAddAct.KEY_GOODS_LIST, item.getProducts());
            mAct.startActivity(it);
        } else {
//			TipsDialog.popup(mActivity, "您已经评价过该商品了！", "确定");
        }
    }

    // 删除
    void orderDelete(final long orderid) {
        TipsDialog.popup(mAct, mAct.getString(R.string.mall_delete_order_tips),mAct.getString(R.string.mall_cancel),
                mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                //showGetGoodsWindow(item.orderid);
                Params params = new Params();
                params.put("orderid", orderid);
                new HttpEntity(mAct).commonPostData(Method.orderDelete, params, MallOrderAdapter1.this);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });

    }

    // 取消
    void doCancelOrder(long orderid, String reason) {
        Params params = new Params();
        params.put("orderid", orderid);
        params.put("reason", reason);
        new HttpEntity(mAct).commonPostData(Method.orderCancel, params, this);
    }

    // 提醒发货
    void remindSend(long orderid) {
        Params params = new Params();
        params.put("orderid", orderid);
        new HttpEntity(mAct).commonPostData(Method.orderRemindSend, params, this);
    }

    // 确认收货
    void doConfireReceived(long orderid) {
        Params params = new Params();
        params.put("orderid", orderid);
        new HttpEntity(mAct).commonPostData(Method.orderConfirmGet, params, this);

    }

    // 申请退货
    public void applyReturn(MallOrder item) {
        Intent it = new Intent(mAct, MallReturnApplyAct.class);
        it.putExtra("orderid", item.orderid);
        mAct.startActivity(it);
    }

    public interface OnOrderItemClickListener {
        public void OnItemButtonClick(int position, int id);
    }

    @Override
    public void parse(String methodName, String result) {

        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(mAct, item.getErr().getMsg());
            return;
        }
        // String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.orderConfirmGet:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_receiving_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));

                Intent intent = new Intent(mAct, MallOrderAfterReceived.class);
                intent.putExtra("orderid", mallOrder.orderid);
                intent.putExtra("list", mallOrder.getProducts());
                mAct.startActivity(intent);
                break;
            case Method.orderRemindSend:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_tips_success));
                break;
            case Method.replyRefundGiveup:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_refund_cancel_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));

//                deleteItem(operatePosition);
                break;
            case Method.replyReturnGiveup:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_return_cancel_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                break;
            case Method.orderCancel:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_order_cancel_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));

                break;
            case Method.orderDelete:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_order_delete_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
//                deleteItem(operatePosition);
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));

                break;
            case Method.buyerOrderHelper:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_help_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                break;
            case Method.replyRefund:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_refund_apply_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));

                break;
            case Method.orderHelperCancel: //取消投诉
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_refund_cancel_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
                //通知我的模块，订单数量更新
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                break;

        }

    }

    int payType;

    @Override
    public void onTagOk(int index, String tag) {
        if (operateOrder != null && operateOrder.getOrderstate() == OrderState.PAYED_CONFIRM) {
            TipsDialog.popup(mActivity, mAct.getString(R.string.mall_order_confirming_tips), mAct.getString(R.string.mall_confirm2));
            return;
        }
        payType = index + 1;
        new Payment(Payment.PAY_FOR_ORDER, mActivity, this).prePay(payType, operateOrder.orderid);
    }

    @Override
    public void onPayResult(boolean isSuccess) {
        if (isSuccess) {
            // 通知我的模块，订单数量更新
            EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
            EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));
//            deleteItem(operatePosition);
        }
    }
}
