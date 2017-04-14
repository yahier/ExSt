package yahier.exst.adapter.mall.integral;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.home.mall.integral.MallIntegralOrderDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.MallIntegralProduct;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.model.OrderProduct;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 积分商城兑换记录
 */
public class MallExchangeHistoryAdapter extends CommonAdapter {
	Activity mActivity;
	List<MallOrder> list;

	public MallExchangeHistoryAdapter(Activity mActivity, List<MallOrder> list) {
		this.mActivity = mActivity;
		this.list = list;
	}

	public void setData(List<MallOrder> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
//		return 10;
	}

	@Override
	public MallOrder getItem(int arg0) {
		if (list != null && arg0 < list.size()){
			return list.get(arg0);
		}
		return null;
	}

	class Holder {
		ImageView iv_goods_photo;
//		TextView tv_shopname;
		TextView tv_goodsname;
		TextView tv_price;
		TextView tv_time;
		TextView tv_state;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mActivity).inflate(R.layout.mall_exchange_history_item, null);
			con.setTag(ho);
			ho.iv_goods_photo = (ImageView) con.findViewById(R.id.iv_goods_photo);
//			ho.tv_shopname = (TextView) con.findViewById(R.id.tv_shopname);
			ho.tv_goodsname = (TextView) con.findViewById(R.id.tv_goodsname);
			ho.tv_price = (TextView) con.findViewById(R.id.tv_price);
			ho.tv_time = (TextView) con.findViewById(R.id.tv_time);
			ho.tv_state = (TextView) con.findViewById(R.id.tv_state);
		} else
			ho = (Holder) con.getTag();

		final MallOrder order = getItem(i);
		if (order == null) return con;

//		ho.tv_shopname.setText(order.getShopname());
		if (order.getProducts() != null && order.getProducts().size() > 0){
			OrderProduct product = order.getProducts().get(0);
			if (product != null) {
				ho.tv_goodsname.setText(product.getGoodsname());
				PicassoUtil.load(mActivity,product.getImgurl(),ho.iv_goods_photo);
			}
		}
		ho.tv_price.setText(String.valueOf((int)order.getRealpayamount()));
		ho.tv_state.setText(order.getOrderstatename());
		if(order.getCreatetime() != 0)
			ho.tv_time.setText(DateUtil.getDateToFormatHmd(String.valueOf(order.getCreatetime())));
		con.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(mActivity, MallIntegralOrderDetailAct.class);
				it.putExtra("orderid", order.orderid);
				mActivity.startActivity(it);
			}
		});
		return con;
	}

}
