package yahier.exst.adapter.mall.integral;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.MallIntegralProduct;
import com.stbl.stbl.util.PicassoUtil;

import java.util.List;

/**
 * 积分商城首页商品列表
 */
public class MallIntegralAdapter extends CommonAdapter {
	Activity mActivity;
	List<MallIntegralProduct> list;
//	int itemWidth;
//	LayoutParams imgLayoutParams;

	public MallIntegralAdapter(Activity mActivity, List<MallIntegralProduct> list) {
		this.mActivity = mActivity;
		this.list = list;
//		itemWidth = Device.getWidth(mActivity) / 2 - Util.dip2px(mActivity, 5);// 屏幕宽度一半的4/5
//		imgLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	public void setData(List<MallIntegralProduct> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
//		return 10;
	}

	@Override
	public MallIntegralProduct getItem(int arg0) {
		if (list != null && arg0 < list.size()){
			return list.get(arg0);
		}
		return null;
	}

	class Holder {
		ImageView img;
		TextView name;
//		TextView brand;
		TextView price;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mActivity).inflate(R.layout.mall_integral_goods_item, null);
			con.setTag(ho);
			ho.img = (ImageView) con.findViewById(R.id.iv_goods_photo);
//			ho.img.setLayoutParams(imgLayoutParams);
			ho.name = (TextView) con.findViewById(R.id.tv_name);
//			ho.brand = (TextView) con.findViewById(R.id.tv_brand);
			ho.price = (TextView) con.findViewById(R.id.tv_price);
		} else
			ho = (Holder) con.getTag();

		final MallIntegralProduct goods = getItem(i);
		if (goods == null) return con;

//		ho.brand.setText(goods.getShopname());
		ho.name.setText(goods.getGoodsname());
		ho.price.setText(goods.getPrice());
//		PicassoUtil.load(mActivity, goods.getImgurl(), ho.img, itemWidth, itemWidth);
		PicassoUtil.load(mActivity, goods.getImgurl(), ho.img);
//		con.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent it = new Intent(mActivity, MallIntegralGoodsDetailAct.class);
//				it.putExtra("goodsid", goods.getGoodsid());
//				mActivity.startActivity(it);
//
//			}
//		});
		return con;
	}

}
