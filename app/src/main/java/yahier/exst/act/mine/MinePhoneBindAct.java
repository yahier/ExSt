package yahier.exst.act.mine;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MinePhoneBindAct extends ThemeActivity {
	TextView tv_phone;
	String phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine_phone_bind);
		setLabel("手机号码绑定");
		
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		Intent it = getIntent();
		phone = it.getStringExtra("phone");
		if (null == phone) {
			showToast("请传入电话号码");
			return;
		}
		tv_phone.setText(phone);
		
		findViewById(R.id.common_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null == phone) {
					return;
				}
				Intent it = new Intent(MinePhoneBindAct.this, MinePhoneFixAct.class);
				it.putExtra("phone", phone);
				MinePhoneBindAct.this.startActivity(it);
				finish();
			}
		});
	}

}
