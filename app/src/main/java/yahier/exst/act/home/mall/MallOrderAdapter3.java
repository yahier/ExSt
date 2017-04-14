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
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;

import java.util.List;

import io.rong.eventbus.EventBus;

public class MallOrderAdapter3 extends RCommonAdapter<MallOrder> implements FinalHttpCallback {
	OnOrderItemClickListener mListener;
	int pageIndex;
	Activity mAct;
	MallOrderAdapter3 mFra;
	
	public MallOrderAdapter3(Activity act, List<MallOrder> mDatas) {
		super(act, mDatas, R.layout.mall_order_item3);
		this.mAct = act;
		mFra = this;
	}

	public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public void convert(RViewHolder helper, final MallOrder item) {
		helper.setText(R.id.tvOrderNo, String.valueOf(item.orderid));
		helper.setText(R.id.tvMoneyCount, String.valueOf(item.getRealpayamount()));
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

		//申请退款
		helper.getView(R.id.order_btn1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
//				replyRefund(item.orderid);
				view.setEnabled(false);
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						view.setEnabled(true);
					}
				}, Config.interClickTime);
				MallOrderAdapter1.applyRefund(mAct, item.orderid, mFra);
			}
		});
		// 提醒发货
		helper.getView(R.id.order_btn2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				remindSend(item.orderid);
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

	}

	// 提醒发货
	void remindSend(long orderid) {
		Params params = new Params();
		params.put("orderid", orderid);
		new HttpEntity(mAct).commonPostData(Method.orderRemindSend, params, this);
	}

	// 申请退款
//	void replyRefund(long orderid) {
//		Params params = new Params();
//		params.put("orderid", orderid);
//		params.put("reasontype", 1);
//		params.put("reason", "冲动消费你懂的");
//		new HttpEntity(mAct).commonPostData(Method.replyRefund, params, this);
//
//	}

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
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
		case Method.orderRemindSend:
			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_tips_success));
			break;
        case Method.replyRefund:
			ToastUtil.showToast(mAct, mAct.getString(R.string.mall_refund_apply_success));
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 2));
			break;
	}

	}
}
