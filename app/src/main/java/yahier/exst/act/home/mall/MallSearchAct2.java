package yahier.exst.act.home.mall;

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mall.MallList2Adapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.DongtaiSearchRecordDB;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.GoodsiSearchRecordDB;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

/**
 * 新做的一个商品搜索页面.打造成搜索 公共页面
 * 
 * @author lenovo
 * 
 */
public class MallSearchAct2 extends BaseActivity implements OnClickListener, OnXListViewListener, FinalHttpCallback {
	private View btnSearch;
	private EditText inputSearch;
	Button btnClear;
	int loadType = 0;// 加载模式
	final int loadTypeBottom = 0;// 底部加载
	final int loadTypeTop = 1;// 顶部加载。清除以前数据
	int position;
	SimpleCursorAdapter adapter_history;
	GoodsiSearchRecordDB db = new GoodsiSearchRecordDB(this);
	XListView listView;
	int pageIndex = 1;
	final static int requestCount = 16;
	MallList2Adapter dataAdapter;
	TextView tvSortDate, tvSortSale, tvSortPrice;
	private View mEmptyView;
	private ImageView ivClear; //清除搜索关键字
	// 排序类型（1-时间正序，2-时间倒序，3-销量正序，4-销量倒序，5-价格正序，6-价格倒序）
	final int sortTypeTime = 1;
	final int sortTypeTimeDesc = 2;
	final int sortTypeSale = 3;
	final int sortTypeSaleDesc = 4;
	final int sortTypePrice = 5;
	final int sortTypePriceDesc = 6;

	int sortType = sortTypeTimeDesc;

	int typeSource;
	int classid;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.mall_search2);
		typeSource = SharedMallType.getType(this);
		classid = getIntent().getIntExtra("classid", 0);
		initView();
	}

	void initView() {
		tvSortDate = (TextView) findViewById(R.id.btn_date_tv);
		tvSortDate.setSelected(true);
		tvSortSale = (TextView) findViewById(R.id.btn_sales_tv);
		tvSortPrice = (TextView) findViewById(R.id.btn_sku_tv);
		findViewById(R.id.btn_date).setOnClickListener(this);
		findViewById(R.id.btn_sales).setOnClickListener(this);
		findViewById(R.id.btn_sku).setOnClickListener(this);
		mEmptyView = findViewById(R.id.rl_empty);
		ivClear = (ImageView) findViewById(R.id.iv_clear);
		ivClear.setOnClickListener(this);
		View btn_back = findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		listView = (XListView) findViewById(R.id.list);
		listView.setOnXListViewListener(this);
//		listView.setEmptyView(findViewById(R.id.rl_empty));
		btnSearch = findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);
		inputSearch = (EditText) findViewById(R.id.et_search);
		inputSearch.addTextChangedListener(new TextListener() {

			@Override
			public void afterTextChanged(Editable edit) {
				if (edit.toString().trim().equals("")) {
					btnClear.setVisibility(View.VISIBLE);
					showCursorData();
				}else{
					ivClear.setVisibility(View.VISIBLE);
				}

			}
		});
		dataAdapter = new MallList2Adapter(this);

		btnClear = (Button) findViewById(R.id.btn_clear_history);
		btnClear.setOnClickListener(this);
		showCursorData();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				if (btnClear.getVisibility() == View.VISIBLE) {
					Cursor cursor = adapter_history.getCursor();
					int nameColumnIndex = cursor.getColumnIndex(DongtaiSearchRecordDB.word);
					String name = cursor.getString(nameColumnIndex);
					inputSearch.setText(name);
					inputSearch.setSelection(name.length());
					loadType = loadTypeTop;
					listView.setAdapter(dataAdapter);
					getMainList(name);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			overridePendingTransition(0, 0);
			break;
		case R.id.btnSearch:
			initList();
			listView.setAdapter(dataAdapter);
			getMainList();
			break;
		case R.id.btn_clear_history:
			db.deleteAllData();
			showCursorData();
			break;
		case R.id.btn_date:
			tvSortDate.setSelected(true);
			tvSortSale.setSelected(false);
			tvSortPrice.setSelected(false);
			tvSortPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sort, 0);
//			if (sortType != sortTypeTime && sortType != sortTypeTimeDesc) {
//				sortType = sortTypeTime;
//			} else if (sortType == sortTypeTime) {
//				sortType = sortTypeTimeDesc;
//			} else if (sortType == sortTypeTimeDesc) {
//				sortType = sortTypeTime;
//			}
			
			sortType = sortTypeTimeDesc;
			initList();
			getMainList();
			break;
		case R.id.btn_sales:
			tvSortSale.setSelected(true);
			tvSortDate.setSelected(false);
			tvSortPrice.setSelected(false);
			tvSortPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sort, 0);
//			if (sortType != sortTypeSale && sortType != sortTypeSaleDesc) {
//				sortType = sortTypeSale;
//			} else if (sortType == sortTypeSale) {
//				sortType = sortTypeSaleDesc;
//			} else if (sortType == sortTypeSaleDesc) {
//				sortType = sortTypeSale;
//			}
			sortType = sortTypeSaleDesc;
			initList();
			getMainList();
			break;
		case R.id.btn_sku:
			if (sortType != sortTypePrice && sortType != sortTypePriceDesc) {
				sortType = sortTypePrice;
				tvSortPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sort_actives, 0);// up
				// LogUtil.logE("价格");
			} else if (sortType == sortTypePrice) {
				sortType = sortTypePriceDesc;
				tvSortPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sort_active, 0);
				// LogUtil.logE("价格倒序");
			} else if (sortType == sortTypePriceDesc) {
				sortType = sortTypePrice;
				tvSortPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sort_actives, 0);
				// LogUtil.logE("价格");
			}
			tvSortPrice.setSelected(true);
			tvSortSale.setSelected(false);
			tvSortDate.setSelected(false);
			initList();
			getMainList();
			break;
			case R.id.iv_clear:
				inputSearch.setText("");
				break;
		}
	}

	void initList() {
		listView.setPullLoadEnable(true);
		loadType = loadTypeTop;
		dataAdapter.deleteAll();
		pageIndex = 1;
	}

	@SuppressWarnings("deprecation")
	void showCursorData() {
		Cursor c = db.query();
		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
		adapter_history = new SimpleCursorAdapter(this, R.layout.single_list_item, c, new String[] { GoodsiSearchRecordDB.word }, new int[] { R.id.name });
		if (c.getCount() == 0) {
			btnClear.setVisibility(View.GONE);
		} else {
			btnClear.setVisibility(View.VISIBLE);
		}
		listView.setAdapter(adapter_history);
	}

	void getMainList(String... name) {
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
		btnClear.setVisibility(View.GONE);
		String word = inputSearch.getText().toString();
		if (name != null && name.length != 0) {
			word = name[0];
		}
		db.insert(word);
		Params params = new Params();
		LogUtil.logE("page:" + pageIndex);
		if (classid != 0) {
			params.put("classid", classid);
		}
		params.put("page", pageIndex);
		params.put("count", requestCount);
		params.put("sorttype", sortType);
		params.put("keyword", word);
		params.put("malltype", typeSource);
		new HttpEntity(this).commonPostData(Method.searchGoods, params, this);
	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			if(item.getIssuccess()!=BaseItem.errorNoTaostTag){
				ToastUtil.showToast(this, item.getErr().getMsg());
			}
			if (methodName.equals(Method.weiboSearch)) {
				listView.onLoadMoreComplete();
				listView.onRefreshComplete();
			}
			mEmptyView.setVisibility(View.VISIBLE);
			return;
		}
		switch (methodName) {
		case Method.searchGoods: {
			listView.onLoadMoreComplete();
			listView.onRefreshComplete();
			String obj = JSONHelper.getStringFromObject(item.getResult());
			List<Goods> list = JSONHelper.getList(obj, Goods.class);
			if (null == list || list.size() < MallSearchAct2.requestCount) {
				LogUtil.logE("EndLoad");
				listView.EndLoad();
			}
			if (null != list) {
				LogUtil.logE("size:" + list.size());
				mEmptyView.setVisibility(View.GONE);
				if (loadType == loadTypeBottom) {
					dataAdapter.addData(list);
				} else {
					dataAdapter.setData(list);
				}
			}else{
				mEmptyView.setVisibility(View.VISIBLE);
			}
		}
			break;
		}
	}

	@Override
	public void onRefresh(XListView v) {
		pageIndex = 1;
		loadType = loadTypeTop;
		listView.setPullLoadEnable(true);
		getMainList();
	}

	@Override
	public void onLoadMore(XListView v) {
		pageIndex++;
		loadType = loadTypeBottom;
		getMainList();

	}

}
