package yahier.exst.adapter.mall;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseGroupAdapter;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.AddAndSubView;
import com.stbl.stbl.widget.AddAndSubView.OnNumChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.eventbus.EventBus;
/**
 * 购物车的产品类。
 * 这个类给了我很多莫名其妙的地方，各种已经的技巧在此类面前损失惨重。
 * @author yahier
 *
 */
public class CartGoodsAdapter extends STBLBaseGroupAdapter<MallCartGoods> implements FinalHttpCallback {

	private boolean enterFlag = false, deleteFlag = false;

	public CartGoodsAdapter(Context context, List<MallCartGoods> arrayList) {
		super(context, arrayList);
	}

	public void setEnterFlag(boolean enterFlag, boolean deleteFlag){
		this.enterFlag = enterFlag;
		this.deleteFlag = deleteFlag;
		notifyDataSetChanged();
	}

	public void setAllChecked(boolean isChecked) {
		if (getListData() == null || getListData().size() == 0)
			return;

		for (int i = 0; i < getListData().size(); i++) {
			MallCartGoods goods = getListData().get(i);
			goods.setSelected(isChecked);
			getListData().set(i, goods);
		}
		notifyDataSetChanged();
	}

	int operatePosition;

	@Override
	public View getView(final int position, View con, ViewGroup parent) {

		con = getInflaterView(R.layout.mall_cart_goods_item);

		CheckBox checkBox = (CheckBox) con.findViewById(R.id.checkBox1);
		AddAndSubView andSubView = (AddAndSubView) con.findViewById(R.id.tvNum);

		ImageView img = (ImageView) con.findViewById(R.id.imgGoods);
		TextView tvPrice = (TextView) con.findViewById(R.id.tvPrice);
		TextView tvType = (TextView) con.findViewById(R.id.tvXinghao);
		TextView tvTitle = (TextView) con.findViewById(R.id.tVgoodsname);
		LinearLayout main_zone = (LinearLayout) con.findViewById(R.id.main_zone);

		final MallCartGoods goods = getItem(position);
		PicassoUtil.load(getContext(), goods.getImgurl(), img);
		tvPrice.setText("¥"+String.valueOf(goods.getRealprice()));
		tvType.setText(String.valueOf(goods.getSkuname()));
		tvTitle.setText(String.valueOf(goods.getGoodsname()));
		andSubView.setNum(goods.getGoodscount());

		if(!enterFlag) {
			andSubView.setOnNumChangeListener(new OnNumChangeListener() {

				@Override
				public void onNumChange(AddAndSubView view, int num) {
					goods.setGoodscount(num);
					updateGoods(goods.getCartid(),num);
					getListData().set(position, goods);
					float s = getCheckedList();
					//曾想在这里回调 重新计算高度，但引发了无线循环的调用了。
					itemListener.onMoneyChanged(s, getListData());
				}
			});

			checkBox.setEnabled(true);
			checkBox.setChecked(goods.isSelected());
		}else{
			andSubView.setOnNumChangeListener(null);
			checkBox.setEnabled(deleteFlag);
			checkBox.setChecked(goods.isSelected());
		}

		if(checkBox.isEnabled()){
//			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//				@Override
//				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//					goods.setSelected(arg1);
//					getListData().set(position, goods);
//					float count = getCheckedList();
//					itemListener.onMoneyChanged(count, getListData());
//				}
//			});
			checkBox.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN){
						goods.setSelected(!goods.isSelected());
						getListData().set(position, goods);
						float count = getCheckedList();
						itemListener.onMoneyChanged(count, getListData());
						return true;
					}
					return false;
				}
			});
		}else{
			checkBox.setOnCheckedChangeListener(null);
		}
		return con;
	}

	float getCheckedList() {
		float count = 0;
		for (int i = 0; i < getListData().size(); i++) {
			MallCartGoods goods = getItem(i);// 越界
			if (goods.isSelected()) {
				count += goods.getRealprice() * goods.getGoodscount();
			}
		}
		return count;
	}

	void updateGoods(int goodsId,int updateNum){
		JSONObject json = getUpdateJson(goodsId,updateNum);
		if (json != null)
		new HttpEntity(getContext()).commonPostJson(Method.buyerCartUpdate,json.toString(),this);
	}
	//获取更新商品数量的json
	JSONObject getUpdateJson(int goodsId,int updateNum){
		try {
			JSONObject json = new JSONObject();
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("cartid", goodsId);
			obj.put("goodscount",updateNum);

			arr.put(obj);
			json.put("carteditdata",arr);
			return json;
		}catch (JSONException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void parse(String methodName, String result) {
		LogUtil.logE("LogUtil","methosName---:"+methodName+"-- result--:"+result);
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(getContext(), item.getErr().getMsg());
			return;
		}
		//String obj = JSONHelper.getStringFromObject(item.getResult());
		if(methodName.equals(Method.cartDelete)){
			ToastUtil.showToast(getContext(), getContext().getString(R.string.mall_remove_car_item_success));
			getListData().remove(operatePosition);
			notifyDataSetChanged();
			float count = getCheckedList();
			itemListener.onMoneyChanged(count, getListData());
			EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
			//搞毛了就重新请求数据
		}else if(methodName.equals(Method.buyerCartUpdate)){ //更改商品数量

		}
	}

	public void setOnItemListener(OnItemListener itemListener) {
		this.itemListener = itemListener;
	}

	OnItemListener itemListener;

	public interface OnItemListener {
		void onMoneyChanged(float moneyCount, List<MallCartGoods> list);
	}

}
