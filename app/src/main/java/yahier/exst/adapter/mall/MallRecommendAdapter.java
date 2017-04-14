package yahier.exst.adapter.mall;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.HomeLikeItem;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MallRecommendAdapter extends CommonAdapter {
	Activity mActivity;
	List<HomeLikeItem> list;
	int itemWidth;
	LayoutParams imgLayoutParams;
	int itemHeight;

	public MallRecommendAdapter(Activity mActivity, List<HomeLikeItem> list) {
		this.mActivity = mActivity;
		this.list = list;
		itemWidth = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_90);// 屏幕宽度一半的4/5
		itemHeight = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_90);
		imgLayoutParams = new LayoutParams(itemWidth, itemHeight);
	}

	public MallRecommendAdapter(Activity mActivity) {
		this.mActivity = mActivity;
		list = new ArrayList<HomeLikeItem>();
		itemWidth = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_90);// 屏幕宽度一半的4/5
	}

	@Override
	public Object getItem(int arg0) {
		if (list == null) return null;
		return list.size() > arg0 ? list.get(arg0) : null;
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
			con = LayoutInflater.from(mActivity).inflate(R.layout.mall_recommend_goods_item, null);
			con.setTag(ho);
			ho.img = (ImageView) con.findViewById(R.id.imageView1);
			ho.img.setLayoutParams(imgLayoutParams);
			ho.name = (TextView) con.findViewById(R.id.tv_name);
			ho.price = (TextView) con.findViewById(R.id.tv_price);
		} else
			ho = (Holder) con.getTag();

		final HomeLikeItem goods = list.get(i);
		if (goods == null) return con;
		ho.name.setText(goods.getTitle());
		ho.price.setText("￥" + goods.getParam());
		PicassoUtil.loadGoods(mActivity, goods.getImglarurl(), ho.img, itemWidth, itemHeight);
		return con;
	}

}
