package yahier.exst.act.mine;

import java.util.ArrayList;
import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Currency;
import com.stbl.stbl.item.CurrentResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MineBillAct extends ThemeActivity implements FinalHttpCallback, OnXListViewListener {
	XListView listView;
	final String count = "20";
	long lastid;
	long earlytime;
	InnerAdapter adapter;
	int loadType = 0;// 加载模式
	final int loadTypeBottom = 0;// 底部加载
	final int loadTypeTop = 1;// 顶部加载。清除以前数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine_wallet_shitudou_trade_record);
		setLabel("账单");
		listView = (XListView) findViewById(R.id.list);
		listView.setOnXListViewListener(this);
		adapter = new InnerAdapter(this);
		listView.setAdapter(adapter);
		getList();
	}

	void getList() {
		Params params = new Params();
		params.put("lastid", lastid);
		params.put("count", count);
		new HttpEntity(this).commonPostData(Method.userCurrencyBillList, params, this);
	}

	@Override
	public void parse(String methodName, String result) {

		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		if(methodName.equals(Method.userCurrencyBillList)){
			listView.onLoadMoreComplete();
			listView.onRefreshComplete();
			String obj = JSONHelper.getStringFromObject(item.getResult());
			CurrentResult item2 = JSONHelper.getObject(obj, CurrentResult.class);
			List<Currency> list = item2.getCurrencys();
			// adapter.addData(item2);
			if (list != null && list.size() > 0) {
				earlytime = list.get(list.size() - 1).getCreatetime();
				if (loadType == loadTypeBottom) {
					adapter.addData(list);
					if (list.size() < Statuses.requestCount) {
						listView.EndLoad();
					}
				} else {
					adapter.setData(list);
				}
			}
		}
	}

	class InnerAdapter extends CommonAdapter {
		Context mContext;
		CurrentResult result;
		List<Currency> list;

		public InnerAdapter(Context mContext) {
			this.mContext = mContext;
			list = new ArrayList<Currency>();
		}

		public void setData(List<Currency> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		public void addData(List<Currency> list) {
			this.list.addAll(list);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		class Holder {
			TextView tvContent, tvDate, tvValue;
		}

		@Override
		public View getView(final int i, View con, ViewGroup parent) {
			Holder ho = null;
			if (con == null) {
				ho = new Holder();
				con = LayoutInflater.from(mContext).inflate(R.layout.mine_bill_item, null);
				ho.tvContent = (TextView) con.findViewById(R.id.item_content);
				ho.tvDate = (TextView) con.findViewById(R.id.item_date);
				ho.tvValue = (TextView) con.findViewById(R.id.item_value);
				con.setTag(ho);
			} else
				ho = (Holder) con.getTag();
			Currency c = list.get(i);
			ho.tvContent.setText(c.getSysremark());
			String value = StringUtil.gets(c.getCreatetime());
			ho.tvDate.setText(DateUtil.getHmOrMdHm(value));

			if (c.getOptype() == Currency.optypeGet) {
				ho.tvValue.setText("+ " + c.getAmount());
			} else {
				ho.tvValue.setText("- " + c.getAmount());
			}
			return con;
		}

	}

	@Override
	public void onRefresh(XListView v) {
		earlytime = 0;
		loadType = loadTypeTop;
		getList();
	}

	@Override
	public void onLoadMore(XListView v) {
		loadType = loadTypeBottom;
		getList();
	}

}
