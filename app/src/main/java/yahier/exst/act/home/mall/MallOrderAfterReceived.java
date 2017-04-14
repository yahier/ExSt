package yahier.exst.act.home.mall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mall.MallAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.model.HomeLikeItem;
import com.stbl.stbl.model.OrderProduct;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;

public class MallOrderAfterReceived extends ThemeActivity implements FinalHttpCallback {
	long orderId;
	ArrayList<OrderProduct> mData;
	GridView gridGoods;
	private String shopname; //商家名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_order_after_received);
		setLabel(getString(R.string.mall_receiving_success));

		Intent it = getIntent();
		orderId = it.getLongExtra("orderid", 0);
		if (orderId == 9) {
			LogUtil.logE("订单id为空");
			finish();
		}
		mData = (ArrayList<OrderProduct>) it.getSerializableExtra("list");
		shopname = it.getStringExtra("shopname");
		if (null == mData) {
			LogUtil.logE("商品列表为空");
			finish();
		}else{
			ImageView img = (ImageView) findViewById(R.id.imgGoods);
			if(mData.size() > 0)
			PicassoUtil.load(this, mData.get(0).getImgurl(), img);
		}
		// 猜你喜欢
		gridGoods = (GridView) findViewById(R.id.gridGoods);

		// 评价
		findViewById(R.id.btnRemark).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(MallOrderAfterReceived.this, GoodsRemarkAddAct.class);
				it.putExtra(GoodsRemarkAddAct.KEY_ORDER_ID, orderId);
				it.putExtra(GoodsRemarkAddAct.KEY_GOODS_LIST, mData);
				it.putExtra(GoodsRemarkAddAct.KEY_SHOP_NAME,shopname);
				startActivity(it);
				finish();
			}
		});
		new HttpEntity(this).commonPostData(Method.mallMayLike, null, this);
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("afterreceived_finish",true);
		setResult(Activity.RESULT_OK,intent);
		super.finish();
	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
		case Method.mallMayLike:
			List<HomeLikeItem> listLike = JSONHelper.getList(obj, HomeLikeItem.class);
			// 猜你喜欢
			if (listLike != null && listLike.size() > 0) {
				gridGoods.setAdapter(new MallAdapter(this, listLike));
			}
			break;

		}

	}

}
