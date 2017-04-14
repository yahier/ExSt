package yahier.exst.act.home.seller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.stbl.stbl.R;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.model.express.ExpressInfo;
import com.stbl.stbl.model.express.Station;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LoadingView;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 物流详情
 * 
 * @author ruilin
 * 
 */
public class ExpressDetailAct extends ThemeActivity implements OnFinalHttpCallback {
	public final static String KEY_ORDER_ID = "KEY_ORDER_ID";
	public final static String KEY_EXPRESS_ID = "KEY_EXPRESS_ID";
	public final static String KEY_EXPRESS_NUM = "KEY_EXPRESS_NUM";
	// public final static String KEY_COMPANY = "KEY_COMPANY";
	public final static String KEY_INFO = "KEY_INFO";

	ExpressAdapter mAdapter;
	ExpressInfo mInfo;
	ListView lv_content;
	ArrayList<Station> mData;

	ProgressDialog loadingDialog;

	TextView tv_expressno;
	TextView tv_company;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_express);
		setLabel(getString(R.string.mall_logistics_tail_after));

		Intent it = getIntent();
		String expressId = it.getStringExtra(KEY_EXPRESS_ID);
		String orderId = it.getStringExtra(KEY_ORDER_ID);
		String expressNum = it.getStringExtra(KEY_EXPRESS_NUM);

		mInfo = (ExpressInfo) it.getSerializableExtra(KEY_INFO);

		tv_expressno = (TextView) findViewById(R.id.tv_expressno);
		tv_company = (TextView) findViewById(R.id.tv_company);

		mData = new ArrayList<>();
		mAdapter = new ExpressAdapter(this, mData);
		lv_content = (ListView) findViewById(R.id.lv_content);
		lv_content.setAdapter(mAdapter);

		if (null == mInfo) {
			if (expressId == null && expressNum == null && orderId == null) {
				ToastUtil.showToast(this, getString(R.string.mall_orderid_or_logistics));
				return;
			}
			loadingDialog = LoadingView.createDefLoading(this);
			loadingDialog.setMessage(getString(R.string.mall_loading_data));
			loadingDialog.show();
			requireData(expressId, expressNum, orderId);
		} else if (null != mInfo) {
			setInfoText(mInfo.expressno, mInfo.companyname);
			//mData.addAll(mInfo.stationList);
			//mAdapter.notifyDataSetChanged();
			if (null != mInfo.stationList) {
				// setInfoText(expressNum, companyName);
				mData.addAll(mInfo.stationList);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private void setInfoText(String expressno, String companyname) {
		if (expressno != null)
			tv_expressno.setText(expressno);
		if (companyname != null)
			tv_company.setText(companyname);
	}

	class ExpressAdapter extends RCommonAdapter<Station> {

		public ExpressAdapter(Activity act, List<Station> mDatas) {
			super(act, mDatas, R.layout.mall_express_item);
		}

		@Override
		public void convert(RViewHolder helper, Station item) {
			helper.setText(R.id.tv_time, item.ftime);
			View line = helper.getView(R.id.line);
			TextView tv_msg = helper.getView(R.id.tv_msg);
			tv_msg.setText(item.context);
			int position = helper.getPosition();
			if (position != 0) {
				tv_msg.setTextColor(Color.GRAY);
			} else {
				tv_msg.setTextColor(Color.BLACK);
			}
			if (position == mDatas.size() - 1) {
				if (line.getVisibility() != View.GONE)
					line.setVisibility(View.GONE);
			} else {
				if (line.getVisibility() != View.VISIBLE)
					line.setVisibility(View.VISIBLE);
			}
			if (position == 0) {
				helper.setImageResource(R.id.iv_icon, R.drawable.icon_wuliu_hongdian);
			} else if (position == mDatas.size() - 1) {
				helper.setImageResource(R.id.iv_icon, R.drawable.icon_my_daishouhuo);
			} else {
				helper.setImageResource(R.id.iv_icon, R.drawable.icon_wuliu_huidian);
			}
		}
	}

	private void requireData(String expressid, String expressno, String orderid) {
		if (expressid == null && expressno == null && orderid == null)
			return;
		JSONObject json = new JSONObject();
		try {
			if (expressid != null)
				json.put("expressid", expressid);
			if (expressno != null)
				json.put("expressno", expressno);
			if (orderid != null)
				json.put("orderid", orderid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new HttpUtil(this, null).postJson(Method.expressInfo, json.toString(), this);
	}

	@Override
	public void onHttpResponse(String methodName, String json, Object handle) {
		loadingDialog.dismiss();
		switch (methodName) {
		case Method.expressInfo:
			mInfo = JSONHelper.getObject(json, ExpressInfo.class);
			if (null == mInfo) {
				finish();
				return;
			}
//			mInfo.stationList = ExpressInfo.parseStation(mInfo.expresstext);
			mInfo.toStationList(mInfo.express);
			if (null != mInfo.stationList) {
				mData.clear();
				mData.addAll(mInfo.stationList);
				mAdapter.notifyDataSetChanged();
			}
			tv_expressno.setText(mInfo.expressno);
			tv_company.setText(mInfo.companyname);
			break;
		}
	}

	@Override
	public void onHttpError(String methodName, String msg, Object handle) {
		loadingDialog.dismiss();

	}
}
