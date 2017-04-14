package yahier.exst.act.home.mall;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.seller.ExpressDetailAct;
import com.stbl.stbl.adapter.mall.OrderGoodsAdapter;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;

import io.rong.eventbus.EventBus;

public class MallOrderAdapter4 extends RCommonAdapter<MallOrder> implements FinalHttpCallback {
	OnOrderItemClickListener mListener;
	int pageIndex;
	Activity mAct;
	MallOrder mallOrder;

	public MallOrderAdapter4(Activity act, List<MallOrder> mDatas) {
		super(act, mDatas, R.layout.mall_order_item4);
		this.mAct = act;
	}

	public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
		mListener = listener;
	}

	public void deleteItem(long orderid){
		if (mDatas == null) return;
		for (int i=0; i<mDatas.size(); i++){
			if (((MallOrder)mDatas.get(i)).orderid == orderid){
				deleteItem(i);
			}
		}
	}

	@Override
	public void convert(RViewHolder helper, final MallOrder item) {
		helper.setText(R.id.tvOrderNo, String.valueOf(item.orderid));
		helper.setText(R.id.tvMoneyCount, String.valueOf(item.getRealpayamount()));
//		helper.setText(R.id.order_type_tv, "卖家已发货");
		helper.setText(R.id.order_type_tv, item.getOrderstatename());
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

		// 进入详情
		helper.getView(R.id.item).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mAct, OrderDetailBuyerAct.class);
				intent.putExtra("orderid", item.orderid);
				mAct.startActivity(intent);

			}
		});
		//查看物流
		helper.getView(R.id.order_btn1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				lookExpress(item);
			}
		});
		// 确认收货
		helper.getView(R.id.order_btn2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mallOrder = item;
				
				
				TipsDialog.popup(mAct, mAct.getString(R.string.mall_take_delivery_of_goods_tips),mAct.getString(R.string.mall_cancel), mAct.getString(R.string.mall_confirm2),new OnTipsListener() {
					
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
			}
		});

	}
	
	//查看物流
	void lookExpress(MallOrder item){
		Intent intent = new Intent(mAct,ExpressDetailAct.class);
		intent.putExtra(ExpressDetailAct.KEY_ORDER_ID, ""+item.orderid);
		mAct.startActivity(intent);
	}

	public interface OnOrderItemClickListener {
		public void OnItemButtonClick(int position, int id);
	}

//	// 显示确认收货
//	public void showGetGoodsWindow(final long orderid) {
//		final Dialog dialog = new Dialog(mAct, R.style.dialog);
//		View view = LayoutInflater.from(mAct).inflate(R.layout.dialog_mall_cancel_order, null);
//		((TextView) view.findViewById(R.id.window_title)).setText("确定收货");
//		((TextView) view.findViewById(R.id.window_content)).setText("亲，请确认收到货后再点确定哦!");
//		dialog.setContentView(view);
//		dialog.show();
//		view.findViewById(R.id.window_ok).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				dialog.dismiss();
//				doConfireReceived(orderid);
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

	void doConfireReceived(long orderid) {
		Params params = new Params();
		params.put("orderid", orderid);
		new HttpEntity(mAct).commonPostData(Method.orderConfirmGet, params, this);
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
		case Method.orderConfirmGet:
//			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 3));
			deleteItem(mallOrder.orderid);
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));

			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_operate_success));
			Intent intent = new Intent(mAct, MallOrderAfterReceived.class);
			intent.putExtra("orderid", mallOrder.orderid);
			intent.putExtra("list", mallOrder.getProducts());
			intent.putExtra("shopname", TextUtils.isEmpty(mallOrder.getShopalias()) ? mallOrder.getShopname() : mallOrder.getShopalias());
//			intent.putExtra("shopname",mallOrder.getShopname());
			mAct.startActivityForResult(intent,MallOrderAct.REQUEST_REFRESH_TO_5);
			
			break;
		}
	}
}
