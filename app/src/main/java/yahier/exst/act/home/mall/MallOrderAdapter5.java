package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;

import java.util.List;

import io.rong.eventbus.EventBus;

public class MallOrderAdapter5 extends RCommonAdapter<MallOrder> implements FinalHttpCallback {
	OnOrderItemClickListener mListener;
	int pageIndex;
	Activity mAct;
	final String pay = "pay";
	final String humanHelp = "humanHelp";
	MallOrder mallOrder;
	int operatePosition;
	private long refundOrderId;//当前买家点击的回邮的订单id
	MallOrderFrag frag;

	public MallOrderAdapter5(MallOrderFrag frag, List<MallOrder> mDatas) {
		super(frag.getActivity(), mDatas, R.layout.mall_order_item5);
		this.mAct = frag.getActivity();
		this.frag = frag;
	}
	//给
	public long getRefundOrderId(){
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
			setCommon(btn1, mAct.getString(R.string.mall_order_cancel), Method.orderCancel, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_payment), pay, item, helper.getPosition());
			break;
		case 21200:
			setCommon(btn1, mAct.getString(R.string.mall_refund_apply), Method.replyRefund, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_tip_deliver_goods), Method.orderRemindSend, item, helper.getPosition());
			break;
		case 21300:
			setCommon(btn1, mAct.getString(R.string.mall_check_logistics), Method.expressInfo, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_take_delivery_of_goods), Method.orderConfirmGet, item, helper.getPosition());
			break;
		case 21900:
			setCommon(btn1, mAct.getString(R.string.mall_return_apply), Method.orderApplyReturn, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_remark), Method.addGoodsComment, item, helper.getPosition());
			break;
		case 21201:
			btn1.setVisibility(View.INVISIBLE);
			setCommon(btn2, mAct.getString(R.string.mall_refund_cancel), Method.replyRefundGiveup, item, helper.getPosition());
			break;
		case 21202:
			setCommon(btn2, mAct.getString(R.string.mall_refund_cancel), Method.replyRefundGiveup, item, helper.getPosition());
			setCommon(btn1, mAct.getString(R.string.mall_service_hot), humanHelp, item, helper.getPosition());
			break;
		case 22101:
			setCommon(btn1, mAct.getString(R.string.mall_refund_cancel), Method.replyRefundGiveup, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_service_hot), humanHelp, item, helper.getPosition());
			break;
		case 22102:
			setCommon(btn1, mAct.getString(R.string.mall_refund_cancel), Method.orderHelperCancel, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_service_hot), humanHelp, item, helper.getPosition());
			break;
		case 22200:
			btn1.setVisibility(View.INVISIBLE);
			btn2.setText(R.string.mall_goods_return_mail);
			setCommon(btn2,mAct.getString(R.string.mall_goods_return_mail),Method.replyRefundDeliver, item, helper.getPosition());
			break;
		case 22400:
			btn1.setVisibility(View.INVISIBLE);
			setCommon(btn2, mAct.getString(R.string.mall_service_hot), humanHelp, item, helper.getPosition());
			break;
		case 21001:
			btn1.setVisibility(View.INVISIBLE);
			setCommon(btn2, mAct.getString(R.string.mall_delete_order), Method.orderDelete, item, helper.getPosition());
			break;
		case 29997:
			btn1.setVisibility(View.INVISIBLE);
			setCommon(btn2, mAct.getString(R.string.mall_delete_order), Method.orderDelete, item, helper.getPosition());
			break;
		case 29999:
			btn1.setVisibility(View.INVISIBLE);
//			setCommon(btn2, "删除订单", Method.orderDelete, item, helper.getPosition());
			setCommon(btn2, mAct.getString(R.string.mall_remark), Method.addGoodsComment, item, helper.getPosition());
			break;
		default:
			btn1.setVisibility(View.INVISIBLE);
			btn2.setVisibility(View.INVISIBLE);
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
					doCancelOrder(item.orderid);
					break;
				case Method.orderRemindSend:
					remindSend(item.orderid);
					break;
				case Method.orderConfirmGet:
					doConfireReceived(item.orderid);
					break;
				case Method.orderApplyReturn:
					applyReturn(item);
					break;
				case pay:
					break;
				case Method.expressInfo:
					lookExpress(item);
					break;
				case Method.replyRefundGiveup:
					giveUpRefund(item);
					break;
				case Method.replyRefund:

					break;
				case humanHelp:
					TipsDialog.showOrderHumanHelp(mAct);
					break;
				case Method.replyRefundDeliver: //买家回邮
					refundOrderId = item.orderid;
					Intent intent = new Intent(mAct,SellerExpressAct.class);
					frag.startActivityForResult(intent, MallOrderFrag.REQUEST_EXPRESS);
					break;
					case Method.orderHelperCancel: //取消投诉
						helperCancel(item);
						break;
				}
			}
		});

	}



	// 查看物流
	void lookExpress(MallOrder item) {
		Intent intent = new Intent(mAct, ExpressDetailAct.class);
		intent.putExtra(ExpressDetailAct.KEY_ORDER_ID, ""+item.orderid);
		mAct.startActivity(intent);
	}

	// 申请退款
//	void applyRefund(MallOrder item) {
//		Params params = new Params();
//		params.put("orderid", item.orderid);
//		new HttpEntity(mAct).commonPostData(Method.replyRefund, params, this);
//	}

	// 放弃退款申请
	void giveUpRefund(MallOrder item) {
		Params params = new Params();
		params.put("orderid", item.orderid);
		new HttpEntity(mAct).commonPostData(Method.replyRefundGiveup, params, this);
	}

	// 取消投诉
	void helperCancel(final MallOrder item) {
		TipsDialog.popup(mAct, mAct.getString(R.string.mall_refund_cancel_tips), mAct.getString(R.string.mall_cancel),
				mAct.getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {
			@Override
			public void onConfirm() {
				Params params = new Params();
				params.put("orderid", item.orderid);
				new HttpEntity(mAct).commonPostData(Method.orderHelperCancel, params, MallOrderAdapter5.this);
			}

			@Override
			public void onCancel() {
			}
		});
	}

	// 评价
	void doRemark(MallOrder item) {
		Intent it = new Intent(mAct, GoodsRemarkAddAct.class);
		it.putExtra(GoodsRemarkAddAct.KEY_ORDER_ID, item.orderid);
		it.putExtra(GoodsRemarkAddAct.KEY_GOODS_LIST, item.getProducts());
		it.putExtra(GoodsRemarkAddAct.KEY_SHOP_NAME, TextUtils.isEmpty(item.getShopalias()) ? item.getShopname() : item.getShopalias());
//		it.putExtra(GoodsRemarkAddAct.KEY_SHOP_NAME,item.getShopname());
		mAct.startActivity(it);
	}

	// 删除
	void orderDelete(long orderid) {
		Params params = new Params();
		params.put("orderid", orderid);
		new HttpEntity(mAct).commonPostData(Method.orderDelete, params, this);

	}

	// 取消
	void doCancelOrder(long operateOrderId) {
		Params params = new Params();
		params.put("orderid", operateOrderId);
		params.put("reason", "");
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
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_receiving_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 4));
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
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 4));
			deleteItem(operatePosition);
			break;
		case Method.orderCancel:
			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_order_cancel_success));
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 4));
			break;
		case Method.orderDelete:
			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_order_delete_success));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 4));
			deleteItem(operatePosition);
			break;
		case Method.replyRefund:
			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_refund_apply_success));
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 4));
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
}
