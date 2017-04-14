package yahier.exst.act.home.mall;

import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.model.DiscountTicket;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MallCouponAct extends ThemeActivity implements FinalHttpCallback, OnXListViewListener {
	XListView listView;
	String methodName = Method.getDiscountList;
	boolean isUse;
	int shopIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_coupon);
		
		Intent it = getIntent();
		isUse = it.getBooleanExtra("isUse", false);
		if (isUse) {
			methodName = Method.getEnableCouponList;
			shopIndex = it.getIntExtra("index", 0);
		}
		
		listView = (XListView) findViewById(R.id.lv_content);
		listView.setEmptyView(findViewById(R.id.empty));
		listView.setOnXListViewListener(this);
		listView.setPullLoadEnable(false);
		setLabel(getString(R.string.mall_coupon));
		getList();
	}

	void getList() {
		new HttpEntity(this).commonPostData(Method.getDiscountList, null, this);
	}

	class Adapter extends RCommonAdapter<DiscountTicket> {
		List<DiscountTicket> mDatas;
		
		public Adapter(Activity act, List<DiscountTicket> mDatas) {
			super(act, mDatas, R.layout.mall_discount_list_item);
			this.mDatas = mDatas;
		}

		@Override
		public void convert(RViewHolder helper, DiscountTicket item) {
			helper.setText(R.id.disaccount, "￥" + item.getDisaccount());
			helper.setText(R.id.validate_time, DateUtil.getYMD(String.valueOf(item.getValidtime()))+getString(R.string.mall_run_out));
			//helper.getConvertView().setTag(item);
			helper.setText(R.id.tvTitle, item.getTitle());
			helper.getConvertView().setOnClickListener(onClick);
		}
		
		OnClickListener onClick = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isUse) {
					int position = (int) v.getTag();
					Intent it = new Intent();
					it.putExtra("item", mDatas.get(position));
					it.putExtra("index", shopIndex);
					setResult(RESULT_OK, it);
					finish();
				}
			}
		};
	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			if(item.getIssuccess()!=BaseItem.errorNoTaostTag){
			ToastUtil.showToast(this, item.getErr().getMsg());}
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
		case Method.getDiscountList:
			List<DiscountTicket> list = JSONHelper.getList(obj, DiscountTicket.class);
			listView.setAdapter(new Adapter(this, list));
			listView.EndLoad();
			listView.onRefreshComplete();
			break;
		}
	}

	@Override
	public void onRefresh(XListView v) {
		getList();
	}

	@Override
	public void onLoadMore(XListView v) {

	}
}
