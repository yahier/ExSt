package yahier.exst.adapter.mall;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.adapter.mall.CartGoodsAdapter.OnItemListener;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseAdapter;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;

import java.util.List;

/**
 * 购物车adapter
 * 
 * @author lenovo
 * 
 */
public class CartAdapter extends STBLBaseAdapter<MallCartShop> {

	int scrollState = 1;
	public final int scrollStateIdel = 1;
	public final int scrollStateScroll = 2;
	public final int scrollOpen = 3;
	private boolean deleteFlag = false;

	public CartAdapter(Context context, List<MallCartShop> arrayList) {
		super(context, arrayList);
	}

	public void setScrollState(int scrollState) {
		this.scrollState = scrollState;
		notifyDataSetChanged();
	}
	//改变购物车所以商品的选择状态
	public void setAllChecked(boolean isChecked) {
		if (getListData() == null || getListData().size() == 0)
			return;

		for(int i = 0 ; i < getListData().size() ; i++){
			MallCartShop info = getListData().get(i);
			boolean isSelect = false;
			if(!deleteFlag && info.getShopid() == 0) {//下架商品
				info.setSelected(false);
			}else {
				info.setSelected(isChecked);
				isSelect = isChecked;
			}
			//改变商品选择状态
			for (int k=0; k<info.getCartgoods().size(); k++){
				MallCartGoods goods = info.getCartgoods().get(k);
				goods.setSelected(isSelect);
			}
			getListData().set(i, info);
		}
		notifyDataSetChanged();
	}
	//重置所有商品选择状态为false
	public void resetAllChecked() {
		if (getListData() == null || getListData().size() == 0)
			return;
		try {
			for (int i = 0; i < getListData().size(); i++) {
				MallCartShop info = getListData().get(i);
				info.setSelected(false);
				if (info.getCartgoods() != null) {
					for (int k = 0; k < info.getCartgoods().size(); k++) {
						info.getCartgoods().get(k).setSelected(false);
					}
				}
				getListData().set(i, info);
			}
			notifyDataSetChanged();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void setDeleteFlag(boolean deleteFlag){
		this.deleteFlag = deleteFlag;
		//重置了商品选择状态，重置总价格
		resetAllChecked();
		itemListener.onMoneyChanged(getListData());
		notifyDataSetChanged();
	}

	public boolean isDeleteFlag(){
		return deleteFlag;
	}

	@Override
	public View getView(final int i, View con, ViewGroup parent) {
		Holder ho = null;
		if (con == null) {
			ho = new Holder();
			con = getInflaterView(R.layout.mall_cart_item);

			ho.tvShopName = (TextView) con.findViewById(R.id.tvShopName);
			ho.releShop=(RelativeLayout)con.findViewById(R.id.releShop);
			ho.llPage = (LinearLayout) con.findViewById(R.id.swipe_layout);
			ho.checkBox = (CheckBox) con.findViewById(R.id.checkBox1);

			con.setTag(ho);
		} else
			ho = (Holder) con.getTag();

		final MallCartShop shop = getItem(i);
//		ho.tvShopName.setText(shop.getShopname());
		ho.tvShopName.setText(TextUtils.isEmpty(shop.getAlias()) ? shop.getShopname() : shop.getAlias());
		//该商店的所有商品
		final CartGoodsAdapter adapter = new CartGoodsAdapter(getContext(), shop.getCartgoods());
		adapter.setOnItemListener(new OnItemListener() {

			@Override
			public void onMoneyChanged(float moneyCount, List<MallCartGoods> listGoods) {
				//如果该商店商品全选，则商店选择也置为选择状态
				shop.setTotalamount(moneyCount);
				shop.setCartgoods(listGoods);
				if (listGoods != null){
					boolean isTotalSelect = true; //是否该店铺商品全选
					for(int i=0; i<listGoods.size(); i++) {
						if (!listGoods.get(i).isSelected()){
							isTotalSelect = false;
						}
					}
					shop.setSelected(isTotalSelect);
				}
				getListData().set(i, shop);
				itemListener.onMoneyChanged(getListData());
//				notifyDataSetChanged();
			}
		});
		adapter.setAdapter(ho.llPage);

		if(shop.getShopid() > 0){
			adapter.setEnterFlag(false, false);
			adapter.setOnItemClickListener(new STBLBaseGroupAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(int position, View view, ViewGroup parentView) {

					Intent intent = new Intent(getContext(), MallGoodsDetailAct.class);
					intent.putExtra("goodsid", shop.getCartgoods().get(position).getGoodsid());
					getContext().startActivity(intent);
				}
			});

			ho.checkBox.setEnabled(true);
			ho.checkBox.setChecked(shop.isSelected());

			ho.releShop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getContext(), TribeMainAct.class);
					intent.putExtra("userId", shop.getShopid());
					getContext().startActivity(intent);
				}
			});

		}else{
			adapter.setEnterFlag(true, deleteFlag);
			ho.checkBox.setEnabled(deleteFlag);
			ho.checkBox.setChecked(shop.isSelected());
			ho.releShop.setOnClickListener(null);
			adapter.setOnItemClickListener(null);
		}

		if(ho.checkBox.isEnabled()){
//			ho.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//				@Override
//				public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
//					adapter.setAllChecked(isChecked);
//					shop.setSelected(isChecked);
//					shop.setCartgoods(adapter.getListData());
//					getListData().set(i, shop);
//					itemListener.onMoneyChanged(getListData());
//				}
//			});
			final CheckBox checkBox = ho.checkBox;
			checkBox.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						adapter.setAllChecked(!shop.isSelected());
						shop.setSelected(!shop.isSelected());
						shop.setCartgoods(adapter.getListData());
						getListData().set(i, shop);
						itemListener.onMoneyChanged(getListData());
						return true;
					}
					return false;
				}
			});
		}else{
			ho.checkBox.setOnCheckedChangeListener(null);
		}

		return con;
	}

	private class Holder {
		RelativeLayout releShop;
		ImageView user_img;
		TextView tvShopName;
		TextView user_gender_age;
		LinearLayout llPage;
		CheckBox checkBox;
	}

	public void setOnItemListener(OnCartItemListener itemListener) {
		this.itemListener = itemListener;
	}

	OnCartItemListener itemListener;

	public interface OnCartItemListener {
		void onMoneyChanged(List<MallCartShop> list);
	}

}
