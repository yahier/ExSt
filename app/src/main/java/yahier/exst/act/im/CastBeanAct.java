package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.RedPacket;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.ToastUtil;

/**
 * 测试提交2
 */
public class CastBeanAct extends ThemeActivity {
	EditText inputDouCount, inputPeopleCount, inputRemark;
	Button btnCastBean;

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_cast_beans);
		mContext = this;
		setLabel(getString(R.string.im_cast_bean));

		inputDouCount = (EditText) findViewById(R.id.inputDouCount);
		inputPeopleCount = (EditText) findViewById(R.id.inputPeopleCount);
		inputRemark = (EditText) findViewById(R.id.inputRemark);
		btnCastBean = (Button) findViewById(R.id.btnCastBean);
		btnCastBean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				String strPeopleCount = inputPeopleCount.getText().toString();
				String strDouCount = inputDouCount.getText().toString();
				if (strPeopleCount.equals("")) {
					ToastUtil.showToast(mContext, getString(R.string.im_please_input_member_count));
					return;
				}

				if (strDouCount.equals("")) {
					ToastUtil.showToast(mContext, getString(R.string.im_please_input_greenbean_count));
					return;
				}
				int peopleCount = 0;
				int douCount = 0;
				try {
					peopleCount = Integer.valueOf(strPeopleCount);
				} catch (Exception e) {
					ToastUtil.showToast(mContext, getString(R.string.im_member_count_is_integer));
				}
				try {
					douCount = Integer.valueOf(strDouCount);
				} catch (Exception e) {
					ToastUtil.showToast(mContext, getString(R.string.im_greenbean_count_is_integer));
				}
				String remark = inputRemark.getText().toString();
				if (remark.equals("")) {
					remark = getString(R.string.im_default_redpacket_message);
				}
				view.setEnabled(false);
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						view.setEnabled(true);
					}
				}, Config.interClickTime);
				createHongbao(douCount, remark, peopleCount);
			}
		});
	}

	void createHongbao(int amount, String hongbaodesc, int qty) {
		final Params params = new Params();
		params.put("amount", amount);
		params.put("hongbaodesc", hongbaodesc);
		params.put("hongbaoType", RedPacket.typeCastBean);//
		params.put("qty", qty);

		LogUtil.logE(amount + " " + hongbaodesc + " " + qty);
		Payment.getPassword(this,0, new OnInputListener() {

			@Override
			public void onInputFinished(String pwd) {
				params.put("paypwd", pwd);
				new HttpEntity(CastBeanAct.this).commonPostData(Method.imCreateRedPacket, params, new FinalHttpCallback() {

					@Override
					public void parse(String methodName, String result) {
						BaseItem item = JSONHelper.getObject(result, BaseItem.class);
						if (item.getIssuccess() != BaseItem.successTag) {
							ToastUtil.showToast(mContext, item.getErr().getMsg());
							return;
						}
						String obj = JSONHelper.getStringFromObject(item.getResult());
						RedPacket redPacket = JSONHelper.getObject(obj, RedPacket.class);
						// EventBus.getDefault().post(new
						// EventTypeWallet(EventTypeWallet.typeBance));// 刷新余额
						// EventBus.getDefault().post(new
						// EventTypeWallet(EventTypeWallet.typeShitudou));//
						// 刷新师徒豆
						LogUtil.logE("hongbaoId:" + redPacket.getHongbaoid());
						showToast(getString(R.string.im_send_red_package_success));
						Intent intent = getIntent();
						intent.putExtra("item", redPacket);
						setResult(RESULT_OK, intent);
						finish();

					}
				});
			}
		});
	}

}
