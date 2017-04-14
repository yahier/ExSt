package yahier.exst.act.home.seller;

import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.model.OrderProduct;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SimpleGoodsAdapter extends RCommonAdapter<OrderProduct> {
	OnClickListener onClick;
	boolean isIntegralExchange;//是否积分兑换
	
	public SimpleGoodsAdapter(Activity act, List mDatas,boolean isIntegralExchange) {
		super(act, mDatas, R.layout.seller_order_goods);
		this.isIntegralExchange = isIntegralExchange;
	}

	@Override
	public void convert(RViewHolder helper, OrderProduct item) {
		helper.setImageByUrl(R.id.icon_dizhi, item.getImgurl());
		helper.setText(R.id.tv_goodsname, item.getGoodsname());
		helper.setText(R.id.tv_xinghao, mActivity.getString(R.string.mall_model)+item.getSkuname());
		helper.setText(R.id.tv_num, "x"+item.getCount());
		if (!isIntegralExchange){
			helper.setText(R.id.tv_price, "¥"+item.getPrice());
		}else{
			helper.setText(R.id.tv_num, "");
			helper.setText(R.id.tv_price, "");
			TextView tvGoodsNmae = helper.getView(R.id.tv_goodsname);
			tvGoodsNmae.setTextColor(mActivity.getResources().getColor(R.color.gray_333));
			TextView tvXinghao = helper.getView(R.id.tv_xinghao);
			tvXinghao.setTextColor(mActivity.getResources().getColor(R.color.gray_a5));
		}

		View view = helper.getConvertView();
		if (null != onClick) {
			view.setOnClickListener(onClick);
		}
	}

	public void setOnItemClick(OnClickListener onClick) {
		this.onClick = onClick;
	}
}

