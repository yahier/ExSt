package yahier.exst.adapter.mall;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.OrderProduct;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.PicassoUtil;

public class OrderGoodsAdapter extends STBLBaseGroupAdapter<OrderProduct> {

	public OrderGoodsAdapter(Context context, List<OrderProduct> list) {
		super(context, list);
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = new Holder();
		con = getInflaterView(R.layout.mall_create_order_goods);
		ho.imgGoods = (ImageView) con.findViewById(R.id.imgGoods);
		ho.tv_goodsname = (TextView) con.findViewById(R.id.tv_goodsname);
		ho.tv_price = (TextView) con.findViewById(R.id.tv_price);
		ho.tv_num = (TextView) con.findViewById(R.id.tv_num);
		ho.tv_xinghao = (TextView) con.findViewById(R.id.tv_xinghao);

		final OrderProduct goods = getItem(i);
		PicassoUtil.load(getContext(), goods.getImgurl(), ho.imgGoods);
		ho.tv_goodsname.setText(goods.getGoodsname());
		ho.tv_price.setText("￥"+goods.getPrice()+"");
		ho.tv_num.setText("×"+goods.getCount());
		ho.tv_xinghao.setText(getContext().getString(R.string.mall_model)+goods.getSkuname());
		
//		con.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(mContext,MallGoodsDetailAct.class);
//				intent.putExtra("goodsid", goods.getGoodsid());
//				mContext.startActivity(intent);
//			}
//		});
		return con;
	}

	private class Holder {
		private ImageView imgGoods;
		private TextView tv_goodsname,tv_price,tv_num,tv_xinghao;
	}

}
