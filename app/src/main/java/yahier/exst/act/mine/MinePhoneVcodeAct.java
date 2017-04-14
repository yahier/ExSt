package yahier.exst.act.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;

/**
 * 填写验证码
 * 
 * @author lenovo
 * 
 */
public class MinePhoneVcodeAct extends ThemeActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine_phone_input_vcode);
		setLabel("修改手机号");

		findViewById(R.id.common_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				enterAct(MinePhoneResetPwdAct.class);
			}
		});
	}

}
