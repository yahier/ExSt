package yahier.exst.act.home.mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.common.tagclound.TagCloudLayout;
import com.stbl.stbl.common.tagclound.TagCloudLayout.TagItemClickListener;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.model.GoodsDetail;
import com.stbl.stbl.model.MallCart;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.model.ShopInfo;
import com.stbl.stbl.model.Sku;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.AddAndSubView;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 选择商品型号。点击 加入购物车和立即购买后都会进入到这里
 *
 * @author ruilin
 *
 */
public class MallTypeChoiceAct extends ThemeActivity implements FinalHttpCallback {
	private TagCloudLayout mTagcloud;
	private int mTagSelected;
	Button okBtn;
	AddAndSubView countView;
	TextView tvPrice, tvSkuCount;
	ImageView imgGoods;
	GoodsDetail goods;
	int typeOperate;
	final int typeOperateAddCart = 0;
	final int typeOperateBuy = 1;
	long skuId;
	Sku skuSelected;// 选中的库存类型
	int typeSource;
	private int mCartNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_type_choice);
		setLabel(getString(R.string.mall_choice_type));

		goods = (GoodsDetail) getIntent().getSerializableExtra("item");
		typeSource = SharedMallType.getType(this);
		if (goods == null) {
			showToast(getString(R.string.mall_goods_no_exist));
			return;
		}
		typeOperate = getIntent().getIntExtra("typeOperate", typeOperateAddCart);
		mCartNum = getIntent().getIntExtra("cartNum",0);

		setTopCartNum(mCartNum);
		setTopCarOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(MallTypeChoiceAct.this, MallCartAct.class);
				startActivity(it);
			}
		});

		// 这三个值的显示都是根据选择来回调
		okBtn = (Button) findViewById(R.id.btn_ok);
		tvPrice = (TextView) findViewById(R.id.tvPrice);
		tvSkuCount = (TextView) findViewById(R.id.tvSkuCount);
		imgGoods = (ImageView) findViewById(R.id.imgGoods);
		PicassoUtil.load(this, goods.getFimgurl(), imgGoods);
		countView = (AddAndSubView) findViewById(R.id.addAndSubView1);
		initTag(goods.getSkulist());

		if (typeOperate == typeOperateAddCart){
			okBtn.setBackgroundResource(R.drawable.btn_selector_green);
			okBtn.setText(R.string.mall_add_to_shoppingcar);
		}

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// if (goods.getSkulist().size() > 0) {
				// 该型号库存大于0，并且选择的数量小于或等于库存数量
				if (skuSelected.getStockcount() > 0 && countView.getNum() <= skuSelected.getStockcount()) {
					switch (typeOperate) {
						case typeOperateAddCart:
							okBtn.setEnabled(false);//防止重复点击
							addShoppoingCart();
							break;
						case typeOperateBuy:
							Intent intent = new Intent(MallTypeChoiceAct.this, MallOrderCommitAct.class);
							MallCart mall = new MallCart();
							mall.setCartshops(generateBuyData());
							intent.putExtra("item", mall);
							startActivity(intent);
							EventBus.getDefault().post(new EventType(EventType.TYPE_BUY_addToCart));
							finish();
							return;
					}

				} else {
					ToastUtil.showToast(MallTypeChoiceAct.this, getString(R.string.mall_the_type_num_zero));
				}
			}
		});

	}

	private void initTag(final List<Sku> list) {
		skuId = list.get(0).getSkuid();
		skuSelected = list.get(0);
		tvPrice.setText("￥" + String.valueOf(skuSelected.getRealprice()));
		tvSkuCount.setText(String.valueOf(skuSelected.getStockcount()));
		countView.setMax(skuSelected.getStockcount());
		TagBaseAdapter mAdapter = new TagBaseAdapter(this, list);
		mTagcloud = (TagCloudLayout) findViewById(R.id.tagcloud);
		mTagcloud.setSingleSelected(true);
		mTagcloud.setAdapter(mAdapter);
		setTagSelected(0);
		// 云标签点击事件
		mTagcloud.setItemClickListener(new TagItemClickListener() {
			@Override
			public void itemClick(int position) {
				mTagSelected = position;
				// 获取选择项目
				skuSelected = list.get(position);
				skuId = skuSelected.getSkuid();
				tvPrice.setText("￥"+String.valueOf(skuSelected.getRealprice()));
				tvSkuCount.setText(String.valueOf(skuSelected.getStockcount()));
				countView.setMax(skuSelected.getStockcount());// 每个型号的最大值不一样，选择后更新
			}
		});
	}

	public class TagBaseAdapter extends CommonAdapter {

		private Context mContext;
		private List<Sku> mList;

		public TagBaseAdapter(Context context, List<Sku> list) {
			mContext = context;
			mList = list;
		}

		@Override
		public Sku getItem(int arg0) {
			return getItem(arg0);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		/**
		 * 是否库存为0的商品
		 * @return
         */
		public boolean isEmptyItem(int index){
			if (mList != null && mList.size() > index){
				if (mList.get(index).getStockcount() <= 0){
					return true;
				}
			}
			return false;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.tagview, null);
				holder = new ViewHolder();
				holder.tagBtn = (Button) convertView.findViewById(R.id.tag_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Sku sku = mList.get(position);
			holder.tagBtn.setText(sku.getSkuname());
			if (sku.getStockcount() <= 0){
				holder.tagBtn.setBackgroundResource(R.drawable.tag_view_gray_bg);
				holder.tagBtn.setTextColor(getResources().getColor(R.color.gray3));
				holder.tagBtn.setEnabled(false);
			}else{
				holder.tagBtn.setBackgroundResource(R.drawable.tag_view);
				holder.tagBtn.setTextColor(getResources().getColor(R.color.gray1));
				holder.tagBtn.setEnabled(true);
			}

			return convertView;
		}

		class ViewHolder {
			Button tagBtn;
		}
	}

	private void setTagSelected(int index) {
		mTagcloud.setItemSelected(mTagSelected, true);
	}

	// 生成购买的数据
	List<MallCartShop> generateBuyData() {
		List<MallCartShop> listShop = new ArrayList<MallCartShop>();
		List<MallCartGoods> listGoods = new ArrayList<MallCartGoods>();
		// goods
		MallCartGoods good = new MallCartGoods();
		good.setGoodsid(goods.getGoodsid());
		good.setGoodsname(goods.getGoodsname());
		good.setImgurl(goods.getFimgurl());
		good.setSkuid(skuSelected.getSkuid());
		good.setSkuname(skuSelected.getSkuname());
		LogUtil.logE("realPrice:" + skuSelected.getRealprice());
		good.setRealprice(skuSelected.getRealprice());
		good.setGoodscount(countView.getNum());
		good.setSelected(true);
		listGoods.add(good);
		// shop
		MallCartShop shop = new MallCartShop();
		shop.setShopid(goods.getShopid());
		ShopInfo info = goods.getShopinfoview();
//		shop.setShopname(info == null ? "" : info.getShopname());
		shop.setShopname(info == null ? "" : info.getAlias() == null || info.getAlias().equals("") ? info.getShopname() : info.getAlias());
		shop.setCartgoods(listGoods);
		shop.setSelected(true);
		String totalAount = StringUtil.get2ScaleString(skuSelected.getRealprice() * countView.getNum());
		totalAount = totalAount.equals("") ? String.valueOf(skuSelected.getRealprice() * countView.getNum()) : totalAount;
		shop.setTotalamount(Float.valueOf(totalAount));
		LogUtil.logE(skuSelected.getRealprice() + "_______" + countView.getNum() + "----totalprice:" + skuSelected.getRealprice() * countView.getNum());
		listShop.add(shop);
		return listShop;
	}

	void addShoppoingCart() {
		int count = countView.getNum();
		if (count == 0) {
			showToast(getString(R.string.mall_num_no_0));
			return;
		}
		Params params = new Params();
		params.put("skuid", skuId);
		params.put("goodscount", count);
		params.put("malltype", typeSource);
		new HttpEntity(this).commonPostData(Method.addShoppingCart, params, this);
	}

	@Override
	public void parse(String methodName, String result) {
		okBtn.setEnabled(true);
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
			case Method.addShoppingCart:
				showToast(getString(R.string.mall_add_success));
				EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
				okBtn.setEnabled(true);//防止重复点击
				mCartNum += countView.getNum();
				setTopCartNum(mCartNum);
				break;

		}

	}
}
