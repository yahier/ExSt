package yahier.exst.act.im;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;

/**
 * 举报用户
 * 被yahier临时更改了方法
 * @author lenovo
 * 
 */
public class ReportUserAct extends ThemeActivity implements FinalHttpCallback {
	Button okBtn;
	TextView tvLeftCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dongtai_report);
		setLabel(R.string.jubao);
		final EditText input = (EditText) findViewById(R.id.input_report);
		okBtn = (Button) findViewById(R.id.ok_report);
		tvLeftCount = (TextView) findViewById(R.id.tvLeftCount);

		final String userId = getIntent().getStringExtra("userId");
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String content = input.getText().toString().trim();
				if (content.equals("")) {
					ToastUtil.showToast(R.string.please_input_first);
				}
				doReportUser(userId, content);
			}
		});

		final int maxWordSize = 140;
		input.addTextChangedListener(new TextListener() {

			@Override
			public void afterTextChanged(Editable edit) {
				int size = edit.toString().length();
				if (size > maxWordSize) {
					tvLeftCount.setText(String.format(getString(R.string.im_already_exceed),size - maxWordSize));
					okBtn.setEnabled(false);
					okBtn.setBackgroundColor(getResources().getColor(R.color.theme_milk));
				} else if (size > 100) {
					tvLeftCount.setText(String.format(getString(R.string.im_can_input) , maxWordSize - size));
					okBtn.setEnabled(true);
					okBtn.setBackgroundResource(R.drawable.common_btn_red);
				} else if (edit.toString().trim().equals("")) {
					tvLeftCount.setText("");
					okBtn.setEnabled(false);
					okBtn.setBackgroundColor(getResources().getColor(R.color.theme_milk));
				} else {
					tvLeftCount.setText("");
					okBtn.setEnabled(true);
					okBtn.setBackgroundResource(R.drawable.common_btn_red);
				}
			}
		});

	}

	/**
	 * 举报动态
	 * 
	 * @param statusesid
	 * @param complaintsuserid
	 * @param content
	 */
	public void doReportStatuses(long statusesid, long complaintsuserid, String content) {
		okBtn.setEnabled(false);
		Params params = new Params();
		params.put("statusesid", statusesid + "");
		params.put("complaintsuserid", complaintsuserid + "");// 被诉人id
		params.put("content", content);
		new HttpEntity(this).commonPostData(Method.report, params, this);
	}

	/**
	 * 举报用户
	 */
	private void doReportUser(String userId, String content) {
		okBtn.setEnabled(false);
		Params params = new Params();
		params.put("reportuserid", app.getUserId());
		params.put("msg", content);
		new HttpEntity(this).commonPostData(Method.report, params, this);

	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		if(methodName.equals(Method.report)){
			if (item.getIssuccess() == BaseItem.successTag) {
				ToastUtil.showToast(R.string.jubao_success);
				finish();
			}
		}
	}

}
