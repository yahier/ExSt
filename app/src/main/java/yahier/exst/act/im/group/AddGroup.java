package yahier.exst.act.im.group;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;

/**
 * 建群之输入名称
 * @author lenovo
 *
 */
public class AddGroup extends ThemeActivity implements OnClickListener {
	EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_group_create_act);
		setLabel("建群");
		input = (EditText) findViewById(R.id.input);
		setRightImage(R.drawable.test_small, this);
		findViewById(R.id.delete).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.theme_top_banner_right:
			showToast("等着调接口");
			break;
		case R.id.delete:
			input.setText("");
			break;
		}

	}

}
