package yahier.exst.adapter;

import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.util.PicassoUtil;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 打赏的礼物列表
 * 
 * @author lenovo
 * 
 */
public class GiftAdapter extends CommonAdapter {
	Context mContext;
	List<Gift> list;

	public GiftAdapter(Context mContext, List<Gift> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Gift getItem(int arg0) {
		return list.get(arg0);
	}

	class Holder {
		ImageView img;
		TextView name, value;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = LayoutInflater.from(mContext).inflate(R.layout.gift_list_item, null);
			ho.img = (ImageView) con.findViewById(R.id.img);
			ho.name = (TextView) con.findViewById(R.id.name);
			ho.value = (TextView) con.findViewById(R.id.value);
			con.setTag(ho);
		} else
			ho = (Holder) con.getTag();
		
		Gift gift = list.get(i);
		String typeValue = "";
		switch (gift.getCurrencytype()) {
		case Gift.type_renminbi:
			typeValue = mContext.getString(R.string.rmb);
			break;
		case Gift.type_jindou:
			typeValue = mContext.getString(R.string.gold_bean);
			break;
		case Gift.type_lvdou:
			typeValue = mContext.getString(R.string.green_bean);
			break;
		}
		
		String newMessageInfo = "<font color='#e38c01'>"+gift.getValue()+"</font>"+typeValue;
		PicassoUtil.load(mContext, gift.getGiftimg(), ho.img);
		ho.name.setText(gift.getGiftname());
		ho.value.setText(Html.fromHtml(newMessageInfo));
		return con;
	}

}
