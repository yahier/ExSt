package yahier.exst.adapter.mall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Util;

/**
 * ListView当成GridView
 * 
 * @author lenovo
 * 
 */

public class MallList2Adapter extends CommonAdapter {
	Activity mActivity;
	List<Goods> list;
	int itemWidth;
	LayoutParams layoutParams;

	public MallList2Adapter(Activity mActivity) {
		this.mActivity = mActivity;
		list = new ArrayList<Goods>();
		itemWidth = Device.getWidth(mActivity) / 2 - Util.dip2px(mActivity, 14);// 屏幕宽度一半的4/5
		layoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	public void setData(List<Goods> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public void deleteAll() {
		this.list.clear();
		notifyDataSetChanged();
	}

	public void addData(List<Goods> list) {
		LogUtil.logE("addData...");
		this.list.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return (list.size() + 1) / 2;
	}

	class Holder {
		LinearLayout item1, item2;
		ImageView img, img2;
		TextView name, name2;
		TextView price, price2;
	}

	@Override
	public View getView(int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mActivity).inflate(R.layout.mall_goods_2item, null);
			con.setTag(ho);
			ho.img = (ImageView) con.findViewById(R.id.imageView1);
			ho.img.setLayoutParams(layoutParams);
			ho.name = (TextView) con.findViewById(R.id.tv_name);
			ho.price = (TextView) con.findViewById(R.id.tv_price);
			ho.img2 = (ImageView) con.findViewById(R.id.imageView2);
			ho.img2.setLayoutParams(layoutParams);
			ho.name2 = (TextView) con.findViewById(R.id.tv_name2);
			ho.price2 = (TextView) con.findViewById(R.id.tv_price2);
			ho.item1 = (LinearLayout) con.findViewById(R.id.item1);
			ho.item2 = (LinearLayout) con.findViewById(R.id.item2);
		} else
			ho = (Holder) con.getTag();

		final Goods goods = list.get(i * 2);
		PicassoUtil.loadGoods(mActivity, goods.getImgurl(), ho.img, itemWidth, itemWidth);

		ho.name.setText(goods.getGoodsname());
		ho.price.setText("￥" + goods.getMinprice());
		ho.item1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(mActivity, MallGoodsDetailAct.class);
				LogUtil.logE("传入的id:" + goods.getGoodsid());
				it.putExtra("goodsid", goods.getGoodsid());
				mActivity.startActivity(it);

			}
		});

		if (list.size() > i * 2 + 1) {
			ho.item2.setVisibility(View.VISIBLE);
			final Goods goods2 = list.get(i * 2 + 1);
			ho.name2.setText(goods2.getGoodsname());
			ho.price2.setText("￥" + goods2.getMinprice());
			PicassoUtil.loadGoods(mActivity, goods2.getImgurl(), ho.img2, itemWidth, itemWidth);
			ho.item2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent it = new Intent(mActivity, MallGoodsDetailAct.class);
					LogUtil.logE("传入的2 id:" + goods2.getGoodsid());
					it.putExtra("goodsid", goods2.getGoodsid());
					mActivity.startActivity(it);

				}
			});
		} else {
			ho.item2.setVisibility(View.INVISIBLE);
		}

		return con;
	}

}
