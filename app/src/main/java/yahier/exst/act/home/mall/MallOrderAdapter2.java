package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.stbl.stbl.R;
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

public class MallOrderAdapter2 extends RCommonAdapter<MallOrder> implements FinalHttpCallback, OnTimeWheelListener, OnPayResult {
    OnOrderItemClickListener mListener;
    int pageIndex;
    Activity mAct;

    long operateOrderId;
    int operatePosition;
    MallOrder operateOrder;
    int ismergepay;

    public MallOrderAdapter2(Activity act, List<MallOrder> mDatas) {
        super(act, mDatas, R.layout.mall_order_item2);
        this.mAct = act;
    }

    public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void convert(final RViewHolder helper, final MallOrder item) {
        helper.setText(R.id.tvOrderNo, String.valueOf(item.orderid));
        helper.setText(R.id.tvMoneyCount, String.valueOf(item.getRealpayamount()));
//		helper.setText(R.id.order_type_tv, "待付款");
        helper.setText(R.id.order_type_tv, item.getOrderstatename());
        LinearLayout llPage = helper.getView(R.id.llPage);
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

        helper.getView(R.id.item).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mAct, OrderDetailBuyerAct.class);
                intent.putExtra("orderid", item.orderid);
                mAct.startActivity(intent);
            }
        });
        // 取消订单
        helper.getView(R.id.order_btn1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                operatePosition = helper.getPosition();
                operateOrderId = item.orderid;
//                showCancelWindow();
                orderDelete(operateOrderId);
            }
        });
        helper.getView(R.id.order_btn2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                operateOrder = item;
                if (operateOrder.getPaytype() != 0) { //生成订单时选择了支付方式，默认给
                    new Payment(Payment.PAY_FOR_ORDER, mActivity, MallOrderAdapter2.this).prePay(operateOrder.getPaytype(), operateOrder.orderid);
                } else {
                    choosePayType();
                }
            }
        });
        //支付确认中隐藏删除和支付按钮
        if (item.getOrderstate() == OrderState.PAYED_CONFIRM){
            helper.getView(R.id.order_btn1).setVisibility(View.GONE);
            helper.getView(R.id.order_btn2).setVisibility(View.GONE);
        }else{
            helper.getView(R.id.order_btn1).setVisibility(View.VISIBLE);
            helper.getView(R.id.order_btn2).setVisibility(View.VISIBLE);
        }
    }

    public interface OnOrderItemClickListener {
        public void OnItemButtonClick(int position, int id);
    }

    //	public void showCancelWindowOld() {
//		final Dialog dialog = new Dialog(mAct, R.style.dialog);
//		View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_mall_cancel_order, null);
//		dialog.setContentView(view);
//		dialog.show();
//		((TextView) view.findViewById(R.id.window_content)).setText("亲，你确定要放弃购买这几件商品吗?");
//		view.findViewById(R.id.window_ok).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				dialog.dismiss();
//				doCancelOrder();
//			}
//		});
//
//		view.findViewById(R.id.window_cancel).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				dialog.dismiss();
//
//			}
//		});
//
//	}
    // 删除
    void orderDelete(final long orderid) {
        TipsDialog.popup(mAct, mAct.getString(R.string.mall_delete_order_tips),mAct.getString(R.string.mall_cancel),
                mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                //showGetGoodsWindow(item.orderid);
                Params params = new Params();
                params.put("orderid", orderid);
                new HttpEntity(mAct).commonPostData(Method.orderDelete, params, MallOrderAdapter2.this);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });

    }
//	public void showCancelWindow() {
//		TipsEditDialog dialog = new TipsEditDialog(mAct,"取消原因","请输入取消原因","取消","确定");
//		dialog.setOnTipsListener(new TipsEditDialog.OnTipsListener() {
//			@Override
//			public void onConfirm(String input) {
//				doCancelOrder(input);
//			}
//
//			@Override
//			public void onCancel() {
//			}
//		});
//		dialog.show();
//	}

//	void doCancelOrder(String reason) {
//		if (operateOrder != null && operateOrder.getOrderstate() == OrderState.PAYED_CONFIRM) {
//			TipsDialog.popup(mActivity, "订单正在等待系统确认，无法进行该操作！", "确定");
//			return;
//		}
//		LogUtil.logE("取消订单id:" + operateOrderId);
//		Params params = new Params();
//		params.put("orderid", operateOrderId);
//		params.put("reason", reason);
//		new HttpEntity(mAct).commonPostData(Method.orderCancel, params, this);
//	}

    void choosePayType() {
        WheelString list = new WheelString();
        list.setOnTimeWheelListener(this);
        List<String> listType = new ArrayList<String>();
//		listType.add("微信支付");
//		listType.add("支付宝支付");
//		listType.add("余额支付");
//		listType.add("金豆");
//		listType = Arrays.asList(Payment.payTypesName);
        listType = Payment.getPayTypeNames(mAct);
        list.chooseTime(mAct, listType);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(mAct, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
//            case Method.orderCancel:
//                ToastUtil.showToast(mAct, "订单取消成功");
//                // EventBus.getDefault().post(new
//                // EventType(EventType.TYPE_MALL_NUM_CHANGE));
//                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 1));
//                // 通知我的模块，订单数量更新
//                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
//
//                deleteItem(operatePosition);
//                break;
            case Method.orderDelete:
                ToastUtil.showToast(mAct, mAct.getString(R.string.mall_order_delete_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
//                deleteItem(operatePosition);
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 1));
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
            deleteItem(operatePosition);
            EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
            EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 2));
        }else{
            EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
            EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 2));
            EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 1));
        }
    }

}
