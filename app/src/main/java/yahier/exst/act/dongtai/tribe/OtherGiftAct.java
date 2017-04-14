package yahier.exst.act.dongtai.tribe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看他人获取的礼物
 * 
 * @author lenovo
 * 
 */
public class OtherGiftAct extends ThemeActivity implements FinalHttpCallback {

	private EmptyView mEmptyView;
	XListView listView;
	Context mContext;
	List<MineGiftItem> list = new ArrayList<MineGiftItem>();
	Adapter adapter;
	long userId;
	UserItem userItem;

	private int mPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_list);
		mContext = this;

		mEmptyView = (EmptyView) findViewById(R.id.empty_view);
		mEmptyView.setOnRetryListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPage = 1;
				getGift();
			}
		});
		listView = (XListView) findViewById(R.id.list);
		listView.setOnXListViewListener(new XListView.OnXListViewListener() {
			@Override
			public void onRefresh(XListView v) {
				mPage = 1;
				getGift();
			}

			@Override
			public void onLoadMore(XListView v) {
				mPage++;
				getGift();
			}
		});
		adapter = new Adapter(mContext);
		listView.setAdapter(adapter);
		userItem = (UserItem) getIntent().getSerializableExtra("userItem");
		if (userItem != null)
			setLabel(userItem.getAlias() == null || userItem.getAlias().equals("") ? userItem.getNickname() : userItem.getAlias() + "收到的礼物");
//			setLabel(userItem.getNickname() + "收到的礼物");
		getGift();
	}

	// 获取我送出的礼物
	void getGift() {
		Params params = new Params();
		params.put("selecttype", MineGiftItem.selecttype_get);
		if (userItem != null) {
			params.put("objuserid", userItem.getUserid());
		}
		params.put("page", mPage);
		params.put("count", 15);
		new HttpEntity(mContext).commonPostData(Method.userGetMineGift, params, this);
	}

	public class Adapter extends CommonAdapter implements View.OnClickListener{
		Context mContext;

		public Adapter(Context mContext) {
			this.mContext = mContext;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		public void setData() {

		}

		class Holder {
			ImageView giftimg, userimg;
			TextView value;
			TextView mTimeTv;
			TextView mNickTv;
		}

		@Override
		public View getView(final int i, View con, ViewGroup parent) {
			Holder ho = null;
			if (con == null) {
				ho = new Holder();
				con = LayoutInflater.from(mContext).inflate(R.layout.mine_gift_list_item, parent, false);
				ho.giftimg = (ImageView) con.findViewById(R.id.img);
				ho.userimg = (ImageView) con.findViewById(R.id.userImg);
				ho.value = (TextView) con.findViewById(R.id.value);
				ho.mTimeTv = (TextView) con.findViewById(R.id.tvTime);
				ho.mNickTv = (TextView) con.findViewById(R.id.userName);
				con.setTag(ho);
			} else {
				ho = (Holder) con.getTag();
			}
			MineGiftItem item = list.get(i);
			Gift gift = item.getGiftinfo();
			String typeValue = "";
			switch (gift.getCurrencytype()) {
			case Gift.type_renminbi:
				typeValue = "人民币";
				break;
			case Gift.type_jindou:
				typeValue = "金豆";
				break;
			case Gift.type_lvdou:
				typeValue = "绿豆";
				break;
			}

			String newMessageInfo = "我获<font color='#ff0000'><b>" + gift.getValue() + "</b></font>" + typeValue;// 测试有效

			ho.value.setText(Html.fromHtml(newMessageInfo));
			ho.mTimeTv.setText(DateUtil.getTimeOff(item.getCreatetime()));
			UserItem user = item.getObjuser();
//			ho.mNickTv.setText(user.getNickname());
			ho.mNickTv.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
			PicassoUtil.load(mContext, gift.getGiftimg(), ho.giftimg);
			PicassoUtil.load(mContext, user.getImgurl(), ho.userimg);

			ho.userimg.setTag(i);
			ho.userimg.setOnClickListener(this);
			return con;
		}

		@Override
		public void onClick(View v) {
			int position = (int) v.getTag();
			MineGiftItem gift = list.get(position);
			Intent intent = new Intent(mContext, TribeMainAct.class);
			intent.putExtra("userId", gift.getObjuser().getUserid());
			startActivity(intent);
		}

	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			listView.onRefreshComplete();
			listView.onLoadMoreComplete();
			ToastUtil.showToast(mContext, item.getErr().getMsg());
			if (mPage == 1 && adapter.getCount() == 0){
				mEmptyView.showRetry();
			}
			return;
		}
		if (methodName.equals(Method.userGetMineGift)){
			listView.onRefreshComplete();
			listView.onLoadMoreComplete();
			String obj = JSONHelper.getStringFromObject(item.getResult());
			List<MineGiftItem> dataList = JSONHelper.getList(obj, MineGiftItem.class);
			if (dataList != null){
				if (mPage == 1){
					if(dataList.size() == 0){
						mEmptyView.showEmpty();
						listView.setVisibility(View.GONE);
						return;
					}
					mEmptyView.hide();
					listView.setVisibility(View.VISIBLE);
					list.clear();
				}
				list.addAll(dataList);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
