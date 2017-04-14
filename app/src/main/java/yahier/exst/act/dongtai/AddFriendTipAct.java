package yahier.exst.act.dongtai;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;

public class AddFriendTipAct extends ThemeActivity implements FinalHttpCallback {
	UserItem userItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend_tip);
		userItem = (UserItem) getIntent().getSerializableExtra("userItem");
		if (userItem == null) {
			return;
		}
		setLabel(userItem.getNickname());
		TextView tvNameTip = (TextView) findViewById(R.id.tvNameTip);
		tvNameTip.setText(userItem.getNickname() + "还不是你的好友，无法直接与TA聊天");
		findViewById(R.id.tvAdd).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AddFriendUtil(AddFriendTipAct.this, null).addFriendDirect(userItem);
			}
		});

	}



	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		if (methodName.equals(Method.imAddRelation)) {
			showToast("申请已经发出");
		}

	}
}
