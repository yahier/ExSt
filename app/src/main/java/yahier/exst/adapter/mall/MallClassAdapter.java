package yahier.exst.adapter.mall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsAct;
import com.stbl.stbl.adapter.mall.CartGoodsAdapter.OnItemListener;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.model.GoodsClass;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author lenovo
 * 
 */
public class MallClassAdapter extends CommonAdapter {
	Activity mContext;
	List<GoodsClass> list;
	int itemWidth;
	LayoutParams imgLayoutParams;

	public MallClassAdapter(Activity mContext,List<GoodsClass> list) {
		this.mContext = mContext;
		this.list = list;
		itemWidth = Device.getWidth(mContext) / 8;
		imgLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}


	@Override
	public int getCount() {
		return list.size();
	}

	class Holder {
		ImageView imgClass;
		TextView tvClassName;

	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mContext).inflate(R.layout.mall_class_item, null);
			ho.imgClass = (ImageView) con.findViewById(R.id.imgClass);
			ho.tvClassName = (TextView) con.findViewById(R.id.tvClassName);
			ho.imgClass.setLayoutParams(imgLayoutParams);
			con.setTag(ho);
		} else
			ho = (Holder) con.getTag();

		final GoodsClass mClass = list.get(i);
		PicassoUtil.load(mContext, mClass.getIconurl(), ho.imgClass);
		ho.tvClassName.setText(mClass.getClassname());
		con.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(mContext, UmengClickEventHelper.SPLM);
				Intent it = new Intent(mContext, MallGoodsAct.class);
				it.putExtra("item", mClass);
				mContext.startActivity(it);
			}
		});
		return con;
	}

}