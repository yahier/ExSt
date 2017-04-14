package yahier.exst.adapter.mall;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.MallCart;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;

public class MallCreateOrderGoodsAdapter extends STBLBaseGroupAdapter<MallCartGoods> {

	private boolean isIntegralExchange;
	public MallCreateOrderGoodsAdapter(Context mContext, List<MallCartGoods> list,boolean isIntegralExchange) {
		super(mContext, list);
		this.isIntegralExchange = isIntegralExchange;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {

		con = getInflaterView(R.layout.mall_create_order_goods);

		ImageView imgGoods = (ImageView) con.findViewById(R.id.imgGoods);
		TextView tv_goodsname = (TextView) con.findViewById(R.id.tv_goodsname);
		TextView tv_price = (TextView) con.findViewById(R.id.tv_price);
		TextView tv_num = (TextView) con.findViewById(R.id.tv_num);
		TextView tv_xinghao = (TextView) con.findViewById(R.id.tv_xinghao);

		final MallCartGoods goods = getItem(i);
		PicassoUtil.load(getContext(), goods.getImgurl(), imgGoods);
		LogUtil.logE("购买时:"+goods.getImgurl());
		tv_goodsname.setText(goods.getGoodsname());
		tv_price.setText("￥"+goods.getRealprice());
		tv_num.setText("×"+goods.getGoodscount());
		tv_xinghao.setText(goods.getSkuname());

		//积分购买
		if (isIntegralExchange){
			tv_price.setText("");
			tv_num.setText("");
			con.setBackgroundColor(getContext().getResources().getColor(R.color.white));
		}
		return con;
	}

}
