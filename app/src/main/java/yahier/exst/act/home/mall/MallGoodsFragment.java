package yahier.exst.act.home.mall;

import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mall.MallList2Adapter;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.GoodsClass;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;
import com.umeng.analytics.MobclickAgent;

import io.rong.eventbus.EventBus;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 商品列表控件
 * 
 * @author ruilin
 */
public class MallGoodsFragment extends Fragment implements OnXListViewListener, FinalHttpCallback {
	private Activity mContext;
	private XListView mListView;
	private MallList2Adapter mAdapter;
	private View mView;
	int classid;// 从intent传入
	int pageIndex = 1;

	// 排序类型（1-时间正序，2-时间倒序，3-销量正序，4-销量倒序，5-价格正序，6-价格倒序）
	final int sortTypeTime = 1;
	final int sortTypeTimeDesc = 2;
	final int sortTypeSale = 3;
	final int sortTypeSaleDesc = 4;
	final int sortTypePrice = 5;
	final int sortTypePriceDesc = 6;
	int sorttype = sortTypeTimeDesc;//

	int loadType = 0;// 加载模式
	final int loadTypeBottom = 0;// 底部加载
	final int loadTypeTop = 1;// 顶部加载。清除以前数据
	final int requestCount = 16;
	//自动加载是否完成
	private boolean isAutoUpdateComplete = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mView = inflater.inflate(R.layout.mall_goods_list, null);
		mListView = (XListView) mView.findViewById(R.id.lv_goods);
		mListView.setEmptyView(mView.findViewById(R.id.empty));
		mAdapter = new MallList2Adapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.setOnXListViewListener(this);
		initOrderMenuItm(R.id.btn_date, mView);
		initOrderMenuItm(R.id.btn_sales, mView);
		initOrderMenuItm(R.id.btn_sku, mView);

		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			private int lastVisibleItem = 0;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//				LogUtil.logE("LogUtil","firstVisibleItem: "+firstVisibleItem+" -- visibleItemCount: "+visibleItemCount+" -- totalItemCount: "+totalItemCount);
				if (firstVisibleItem > lastVisibleItem && isAutoUpdateComplete && mListView.isEnabled()) { //往上滑才进入判断
					if (totalItemCount - (visibleItemCount + firstVisibleItem) <= 2) {
						isAutoUpdateComplete = false;
//						LogUtil.logE("LogUtil","加载新数据。。。。。。");
						onLoadMore(mListView);
					}
				}
				//记录最后一个的可见firstvisibleitem
				lastVisibleItem = firstVisibleItem;
			}
		});
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//初始化搜索
		mOrderMenuClick.onClick(mView.findViewById(R.id.btn_date));

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	// EventBus的回调
	public void onEvent(GoodsClass goodsClass) {
		this.classid = goodsClass.classid;
		// getData();//屏蔽掉
	}

	void getData(int pageIndex) {
		LogUtil.logE("sorttype:" + sorttype + " pageIndex:" + pageIndex);
		Params params = new Params();
		params.put("classid", classid);
		params.put("sorttype", sorttype);
//		params.put("page", pageIndex);
		params.put("page", pageIndex);
		params.put("count", requestCount);
		new HttpEntity(mContext).commonPostData(Method.getTypeGoods, params, this);
	}

	private void initOrderMenuItm(int id, View parent) {
		LinearLayout btn_date = (LinearLayout) parent.findViewById(id);
		TextView text = (TextView) btn_date.getChildAt(0);
		btn_date.setOnClickListener(mOrderMenuClick);
		btn_date.setTag(0);
		text.setTextColor(Color.GRAY);
	}

	/** 排序菜单 */
	private OnClickListener mOrderMenuClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_date:
				MobclickAgent.onEvent(mContext, UmengClickEventHelper.SPAZXCK);
				onSortButtonPressed(R.id.btn_date, true);
				onSortButtonPressed(R.id.btn_sales, false);
				onSortButtonPressed(R.id.btn_sku, false);
				sorttype = sortTypeTimeDesc;
				mAdapter.deleteAll();
				loadType = loadTypeTop;
				pageIndex = 1;
				mListView.setPullLoadEnable(true);
				getData(pageIndex);
				break;
			case R.id.btn_sales:
				MobclickAgent.onEvent(mContext, UmengClickEventHelper.SPAXLCK);
				onSortButtonPressed(R.id.btn_date, false);
				onSortButtonPressed(R.id.btn_sales, true);
				onSortButtonPressed(R.id.btn_sku, false);
				sorttype = sortTypeSaleDesc;
				mAdapter.deleteAll();
				loadType = loadTypeTop;
				pageIndex = 1;
				mListView.setPullLoadEnable(true);
				getData(pageIndex);
				break;
			case R.id.btn_sku:
				MobclickAgent.onEvent(mContext, UmengClickEventHelper.SPAJGCK);
				pageIndex = 1;
				mAdapter.deleteAll();
				loadType = loadTypeTop;
				mListView.setPullLoadEnable(true);
				
				onSortButtonPressed(R.id.btn_date, false);
				onSortButtonPressed(R.id.btn_sales, false);
				onSortButtonPressed(R.id.btn_sku, true);
				break;
			default:
				break;
			}
		}
	};

	private void onSortButtonPressed(int id, boolean isPressed) {
		LinearLayout btn = (LinearLayout) mView.findViewById(id);
		TextView text = (TextView) btn.getChildAt(0);
		ImageView icon = btn.getChildCount() > 1 ? (ImageView) btn.getChildAt(1) : null;
		if (isPressed) {
			if ((int) btn.getTag() == 1) {
				btn.setTag(2);
			} else {
				btn.setTag(1);
			}
			text.setTextColor(Color.BLACK);
			// 只价格栏有图片
			if (null != icon) {
				int drawableId = R.drawable.icon_sort;
				switch ((int) btn.getTag()) {
				case 0:
					drawableId = R.drawable.icon_sort;
					break;
				case 1:
					drawableId = R.drawable.icon_sort_active;
					sorttype = sortTypePriceDesc;
					getData(pageIndex);
					break;
				case 2:
					drawableId = R.drawable.icon_sort_actives;
					sorttype = sortTypePrice;
					getData(pageIndex);
					break;
				}
				icon.setImageResource(drawableId);
			}
		} else {
			btn.setTag(0);
			text.setTextColor(Color.GRAY);
			if (null != icon) {
				icon.setImageResource(R.drawable.icon_sort);
			}
		}
	}

	@Override
	public void onRefresh(XListView v) {
		mListView.setPullLoadEnable(true);
		loadType = loadTypeTop;
		pageIndex = 1;
		getData(pageIndex);
	}

	@Override
	public void onLoadMore(XListView v) {
		loadType = loadTypeBottom;
		pageIndex++;
		getData(pageIndex);
	}

	@Override
	public void parse(String methodName, String result) {
		isAutoUpdateComplete = true;
		final BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			if (getActivity() != null)
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastUtil.showToast(getActivity(), item.getErr().getMsg());
					}
				});
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
		case Method.getTypeGoods:
			mListView.onLoadMoreComplete();
			mListView.onRefreshComplete();
			List<Goods> list = JSONHelper.getList(obj, Goods.class);
			if (null == list || list.size() < MallSearchAct2.requestCount) {
				LogUtil.logE("EndLoad");
				mListView.EndLoad();
			}else{
				mListView.setEnabled(true);
			}
			if (null != list) {
				if (loadType == loadTypeBottom) {
					mAdapter.addData(list);
				} else {
					mAdapter.setData(list);
				}
			}

			break;

		}

	}
}
