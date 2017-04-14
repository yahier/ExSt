package yahier.exst.act.home.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelMenu;
import com.stbl.stbl.util.WheelMenu.OnWheelMenuListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.rong.eventbus.EventBus;

/**
 * 退款申请
 * 
 * @author ruilin
 * 
 */
public class MallRefundApplyAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
	private WheelMenu subMenu;
	private TextView tv_reason;
	private TextView tv_edit_count;
	EditText inputReason;
	long orderId;
	int reasontype;//退款类型
	/**是否走退货接口，在待收货状态申请退款*/
	private boolean isReturn = false;
	private Map<String,Integer> returnGoodsType = new HashMap<>(); //退货类型选择
	private final int maxLength = 140;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_refund_apply);
		setLabel(getString(R.string.mall_refund_info));
		findViewById(R.id.lin_comment).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		inputReason = (EditText) findViewById(R.id.inputReason);
		tv_reason = (TextView) findViewById(R.id.tv_reason);
		tv_edit_count = (TextView) findViewById(R.id.tv_edit_count);
		orderId = getIntent().getLongExtra("orderid", 0);
		isReturn = getIntent().getBooleanExtra("isreturn",false);
		if (orderId == 0) {
			showToast(getString(R.string.mall_no_orderid));
		}
		initData();
		inputReason.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				editStart = inputReason.getSelectionStart();
				editEnd = inputReason.getSelectionEnd();
				if (temp.length() > maxLength){
					ToastUtil.showToast(MallRefundApplyAct.this, getString(R.string.mall_can_not_exceed)+maxLength+getString(R.string.mall_a_word));
					s.delete(editStart -1,editEnd);
					int tempSelection = editStart;
					inputReason.setText(s);
					inputReason.setSelection(tempSelection);
				}
				tv_edit_count.setText(s.toString().length()+"/"+maxLength);
			}
		});
	}

	private void initData(){
		String returnGoodsJson = (String) SharedPrefUtils.getFromPublicFile(KEY.refundamounttype,"");
		if (returnGoodsJson.equals("")){
			CommonTask.getCommonDicBackground();
		}else{
			try {
				JSONArray arr = new JSONArray(returnGoodsJson);
				for (int i=0; i<arr.length(); i++){
					JSONObject obj = arr.optJSONObject(i);
					if (obj == null) continue;
					returnGoodsType.put(obj.optString("name"),obj.optInt("value"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_comment:
			if (null == subMenu) {
				final ArrayList<String> items = new ArrayList<>();
				for (String s :returnGoodsType.keySet()){
					items.add(s);
				}
				Collections.reverse(items);
				subMenu = new WheelMenu(this, items);
				subMenu.setOnWheelMenuListener(new OnWheelMenuListener() {
					@Override
					public void onTagOk(String tag, int index) {
						tv_reason.setText(tag);
						reasontype = returnGoodsType.get(tag);
					}
				});
			}
			subMenu.show();
			break;
		case R.id.btn_ok:
			if (reasontype == 0) {
				showToast(getString(R.string.mall_select_refund_reason));
				return;
			}
			if (inputReason.getText().toString().equals("")) {
				showToast(getString(R.string.mall_need_input_refund_reason));
				return;
			}
			doReplyReturnGoods();
			break;
		}
	}

	// 申请退款
	@SuppressLint("NewApi")
	public void doReplyReturnGoods() {
		JSONObject json = new JSONObject();
		try {
			json.put("orderid", orderId);
			json.put("reasontype", reasontype);// 退款原因
			json.put("reason", inputReason.getText().toString());// 原因
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtil.logE("LogUtil","json -- ;"+json.toString());
		if(!isReturn) {
			new HttpEntity(this).commonPostJson(Method.replyRefund, json.toString(), this);
		}else {
			new HttpEntity(this).commonPostJson(Method.orderApplyReturn, json.toString(), this);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void hideInputSoft() {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
		case Method.replyRefund:
		case Method.orderApplyReturn:
			showToast(getString(R.string.mall_apply_success_please));
//			EventType event = new EventType(EventType.TYPE_REFRESH_ORDER_LIST);
//			event.setParam(0); //0：在全部中才有退货
//			EventBus.getDefault().post(event);
			Intent intent = new Intent();
			intent.putExtra("refresh",true);
			setResult(Activity.RESULT_OK,intent);
			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 0));//刷新全部
//			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_LIST, 2));//刷新待发货
			//通知我的模块，订单数量更新
			EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
			finish();
			break;
		}
	}
}
