package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;

public class UserInfoInputAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
	int type;
	public final static String key = "type";
	public final int type_nickname = 1;// 昵称
	public final int type_signatrue = 2;// 签名
	private EditText input;
	MyApplication app;
	TextView textTitle;
	private LoadingDialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_input_simple);
		app = (MyApplication) getApplication();
		textTitle = (TextView) findViewById(R.id.theme_top_banner_middle);
		input = (EditText) findViewById(R.id.input);
		findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
		findViewById(R.id.theme_top_banner_right).setOnClickListener(this);
		Intent intent = getIntent();
		type = intent.getIntExtra(key, 0);

		switch (type) {
		case type_nickname:
			textTitle.setText(R.string.me_modify_nick);
			break;
		case type_signatrue:
			textTitle.setText(R.string.me_modify_signature);
			break;
		}
		mLoadingDialog = new LoadingDialog(this);

		input.setText(intent.getStringExtra("content"));
		setRightText(getString(R.string.me_done), this);
		findViewById(R.id.delete).setOnClickListener(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.theme_top_banner_left:
			finish();
			break;
		case R.id.theme_top_tv_right:
			String str = input.getText().toString().trim();
			if (TextUtils.isEmpty(str)){
				ToastUtil.showToast(R.string.me_content_cannot_null);
				return;
			}
			update();
			break;
		case R.id.delete:
			input.setText("");
			break;
		}
	}

	// 更新用户信息
	public void update() {
		mLoadingDialog.show();
		Params params = new Params();
		params.put("userid", app.getUserId());
		switch (type) {
		case type_nickname:
			String str = input.getText().toString();
			params.put("nickname", str);
			break;
		case type_signatrue:
			params.put("signature", input.getText().toString());
			break;
		}
		new HttpEntity(this).commonPostData(Method.userUpdate, params, this);
	}

	@Override
	public void parse(String methodName, String result) {
		mLoadingDialog.dismiss();
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		String con = JSONHelper.getStringFromObject(item.getResult());
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		UserItem user = app.getUserItem();
		if(methodName.equals(Method.userUpdate)){
			switch (type) {
				case type_nickname:
					ToastUtil.showToast(R.string.me_modify_nick_success);
					user.setNickname(input.getText().toString());
					SharedUser.putUserValue(user);
					LocalBroadcastHelper.getInstance().send(new Intent(ACTION.UPDATE_USER_INFO));
					RongDB rongdb = new RongDB(this);
					rongdb.update(new IMAccount(RongDB.typeUser, SharedToken.getUserId(this), user.getNickname(), user.getImgurl(),user.getCertification(),user.getAlias()));
					finish();
					break;
				case type_signatrue:
					ToastUtil.showToast(R.string.me_modify_signature_success);
					user.setSignature(input.getText().toString());
					SharedUser.putUserValue(user);
					LocalBroadcastHelper.getInstance().send(new Intent(ACTION.UPDATE_USER_INFO));
					finish();
					break;
			}
		}

	}
}
