package yahier.exst.act.home.seller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CommonItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelCommon;

import java.util.List;

/**
 * 填写快递单
 *
 * @author ruilin
 *
 */
public class SellerExpressAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
	public final static String KEY_NUM = "KEY_NUM";
	public final static String KEY_NAME = "KEY_NAME";

	final short MAX_COUNT = 60;
	EditText et_num;
	EditText et_company;
	TextView tv_count;

	int expressIndex = 0;
	WheelCommon menu;
	List<CommonItem> list; //可选快递公司

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seller_express_info);
		setLabel("快递运单号");
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.imageview1).setOnClickListener(this);
		tv_count = (TextView) findViewById(R.id.textView2);
		et_num = (EditText) findViewById(R.id.editText1);
		et_num.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_COUNT) });
		et_num.setSingleLine();
		et_num.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				tv_count.setText(et_num.getText().length() + "/" + MAX_COUNT);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		tv_count.setText(et_num.getText().length() + "/" + MAX_COUNT);

		et_company = (EditText) findViewById(R.id.editText6);
		et_company.setSingleLine();
		et_company.setFocusable(false);
		et_company.setOnClickListener(this);

		getExpressCompanys();
	}

	/**
	 * 获取快递公司列表
	 */
	void getExpressCompanys() {
		new HttpEntity(this).commonPostData(Method.getExpressCompanys, null, this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			// case R.id.top_left:
			// finish();
			// break;
			case R.id.button1: // 提交
				if (!check())
					return;
				InputMethodUtils.hideInputMethod(et_num);
				Intent it = new Intent();
				it.putExtra(KEY_NUM, et_num.getText().toString());
				it.putExtra(KEY_NAME, expressIndex); // 后台标识从1开始
				setResult(RESULT_OK, it);
				finish();
				break;
			case R.id.imageview1:
			case R.id.editText6:
				if (list == null) return;
				InputMethodUtils.hideInputMethod(et_num);
				if (menu == null){
					menu = new WheelCommon(this, list);
				}
				menu.show();
				menu.setOnWheelMenuListener(new WheelCommon.OnWheelMenuListener() {
					@Override
					public void onTagOk(CommonItem item, int index) {
						et_company.setText(item.getTitle());
//						expressIndex = index;
						expressIndex = item.getValue();

					}
				});
				break;
		}
	}

	public boolean check() {
		if (et_num.getText().toString().length() == 0) {
			ToastUtil.showToast(this, "请输入快递单号");
			return false;
		}
		if (et_company.getText().toString().length() == 0) {
			ToastUtil.showToast(this, "请选择快递公司");
			return false;
		}
		return true;
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
			case Method.getExpressCompanys:
				list = JSONHelper.getList(obj, CommonItem.class);
				if (list != null && list.size() > 0) {
					et_company.setText(list.get(0).getTitle());
					expressIndex = list.get(0).getValue();
				}
				break;

		}
	}
}

