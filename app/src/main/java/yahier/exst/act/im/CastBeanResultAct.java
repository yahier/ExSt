package yahier.exst.act.im;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.RedPacketPickResult;
import com.stbl.stbl.item.im.RedPacktPicker;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class CastBeanResultAct extends ThemeActivity implements FinalHttpCallback {
	ScrollView scroll;
	private ListView listView;
	private Adapter adapter;
	private Context mContext;
	long hongbaoId;
	ImageView imgCreater;
	TextView tvName, tvDes, tvNumInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_cast_bean_result);
		mContext = this;
		hongbaoId = getIntent().getLongExtra("hongbaoId", 0);
		setLabel(getString(R.string.im_cast_bean));
		initViews();
		getResult(hongbaoId);
	}

	void initViews() {
		imgCreater = (ImageView) findViewById(R.id.imgCreater);
		tvName = (TextView) findViewById(R.id.tvName);
		tvDes = (TextView) findViewById(R.id.tvDes);
		tvNumInfo = (TextView) findViewById(R.id.tvNumInfo);

		scroll = (ScrollView) findViewById(R.id.scroll);
		listView = (ListView) findViewById(R.id.list);
		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		scroll.post(new Runnable() {

			@Override
			public void run() {
				scroll.scrollTo(0, 0);

			}
		});
	}

	void getResult(long hongbaoid) {
		Params params = new Params();
		params.put("hongbaoid", hongbaoid);
		new HttpEntity(this).commonPostData(Method.imGetRedPacket, params, this);
	}

	class Adapter extends CommonAdapter {
		Context mContext;
		List<RedPacktPicker> list;

		public Adapter(Context mContext) {
			this.mContext = mContext;
			list = new ArrayList<RedPacktPicker>();
		}

		class Holder {
			ImageView user_img;
			TextView name, time;
			TextView tvNum, tvBest;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public RedPacktPicker getItem(int position) {
			return list.get(position);
		}

		public void setData(List<RedPacktPicker> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int i, View con, ViewGroup arg2) {

			Holder ho = null;
			if (con == null) {
				ho = new Holder();
				con = LayoutInflater.from(mContext).inflate(R.layout.im_group_castbean_list_item, null);
				ho.user_img = (ImageView) con.findViewById(R.id.user_img);
				ho.name = (TextView) con.findViewById(R.id.name);
				ho.time = (TextView) con.findViewById(R.id.time);
				ho.tvNum = (TextView) con.findViewById(R.id.tvNum);
				ho.tvBest = (TextView) con.findViewById(R.id.tvBest);
				con.setTag(ho);
			} else
				ho = (Holder) con.getTag();

			RedPacktPicker picker = list.get(i);
			PicassoUtil.load(mContext, picker.getPickusericonurl(), ho.user_img);
			ho.name.setText(picker.getPickusername());
			ho.time.setText(DateUtil.getHmOrMdHm(String.valueOf(picker.getPicktime())));
			ho.tvNum.setText(String.valueOf(picker.getPickamount()) + getString(R.string.im_individual));
			if (picker.getIsbest() == RedPacktPicker.isbestYes) {
				ho.tvBest.setVisibility(View.VISIBLE);
			} else {
				ho.tvBest.setVisibility(View.INVISIBLE);
			}
			return con;

		}

	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(mContext, item.getErr().getMsg());
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		LogUtil.logE("obj:"+obj);
		switch (methodName) {
		case Method.imGetRedPacket:
			RedPacketPickResult pcikResult = JSONHelper.getObject(obj, RedPacketPickResult.class);
			if(pcikResult==null){
				LogUtil.logE("pcikResult is null");
			}
			if(imgCreater==null){
				LogUtil.logE("imgCreater is null");
			}
			PicassoUtil.load(mContext, pcikResult.getCreateusericonurl(), imgCreater);// null pointer
			tvDes.setText(pcikResult.getHongbaodesc());
			tvName.setText(pcikResult.getCreateusername() + getString(R.string.im_red_package_of));
			String value;
			int qty = pcikResult.getQty();
			int qtyPicked = pcikResult.getPickqty();

			if (qty == qtyPicked) {
				value = qty + getString(R.string.im_red_package_all_finish);
			} else {
				value = qty + String.format(getString(R.string.im_all_red_package_residue),qtyPicked);//"个红包,已被领取" + qtyPicked + "个";
			}
			tvNumInfo.setText(value);

			List<RedPacktPicker> listUser = pcikResult.getPickusers();
			adapter.setData(listUser);
			ViewUtils.setListViewHeightBasedOnChildren(listView);
			LogUtil.logE("imPickRedPacket states:" + pcikResult.getStatus());
			switch (pcikResult.getStatus()) {
			case RedPacketPickResult.status_avilable:
				break;
			case RedPacketPickResult.status_expire:
				break;
			case RedPacketPickResult.status_finished:
				break;
			case RedPacketPickResult.status_hasGot:
				break;
			}
			break;
		}

	}
}
