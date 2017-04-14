package yahier.exst.adapter.mall;

import java.util.ArrayList;
import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.HomeLikeItem;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MallAdapter extends CommonAdapter {
	Activity mActivity;
	List<HomeLikeItem> list;
	int itemWidth;
	LayoutParams imgLayoutParams;

	public boolean recommend = true; //推荐商品

	public MallAdapter(Activity mActivity, List<HomeLikeItem> list) {
		this.mActivity = mActivity;
		this.list = list;
		itemWidth = Device.getWidth(mActivity) / 2 - Util.dip2px(mActivity, 14);// 屏幕宽度一半的4/5
		imgLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	public MallAdapter(Activity mActivity) {
		this.mActivity = mActivity;
		list = new ArrayList<HomeLikeItem>();
		itemWidth = Device.getWidth(mActivity) / 2 - Util.dip2px(mActivity, 14);// 屏幕宽度一半的4/5
	}

	public void setData(List<HomeLikeItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	class Holder {
		ImageView img;
		TextView name;
		TextView price;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mActivity).inflate(R.layout.mall_goods_item, null);
			con.setTag(ho);
			ho.img = (ImageView) con.findViewById(R.id.imageView1);
			ho.img.setLayoutParams(imgLayoutParams);
			ho.name = (TextView) con.findViewById(R.id.tv_name);
			ho.price = (TextView) con.findViewById(R.id.tv_price);
		} else
			ho = (Holder) con.getTag();

		final HomeLikeItem goods = list.get(i);
		ho.name.setText(goods.getTitle());
		ho.price.setText("￥" + goods.getParam());
		PicassoUtil.loadGoods(mActivity, goods.getImglarurl(), ho.img, itemWidth, itemWidth);
		con.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (recommend) {
					MobclickAgent.onEvent(mActivity, UmengClickEventHelper.TJSP);
				}else{
					MobclickAgent.onEvent(mActivity, UmengClickEventHelper.CNXH);
				}
				Intent it = new Intent(mActivity, MallGoodsDetailAct.class);
				it.putExtra("goodsid", goods.getBusinessid());
				mActivity.startActivity(it);

			}
		});
		return con;
	}

}
