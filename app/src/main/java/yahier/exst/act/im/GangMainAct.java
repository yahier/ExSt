package yahier.exst.act.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.ToastUtil;

public class GangMainAct extends ThemeActivity implements OnClickListener {
	ImageView imgHf, imgMute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gang_main);
		setLabel("在线直播");
		imgHf = (ImageView) findViewById(R.id.img_hf);
		imgMute = (ImageView) findViewById(R.id.img_mute);
		imgHf.setOnClickListener(this);
		findViewById(R.id.tool).setOnClickListener(this);
		//ToastUtil.showToast(this, "暂时用免提按钮进入左侧");

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.img_hf:
			break;
		case R.id.tool:
			showWindow();
			break;
		case R.id.item1:
			showWindow();
			break;
		case R.id.item2:
			showWindow();
			break;
		case R.id.item3:
			showWindow();
			break;
		case R.id.item4:
			showWindow();
			break;
		case R.id.item5:
			startActivity(new Intent(this,CastBeanAct.class));
			break;
		}

	}

	PopupWindow pop;

	private void showWindow() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
			return;
		}
		View view = getLayoutInflater().inflate(R.layout.gang_main_window, null);
		view.findViewById(R.id.item1).setOnClickListener(this);
		view.findViewById(R.id.item2).setOnClickListener(this);
		view.findViewById(R.id.item3).setOnClickListener(this);
		view.findViewById(R.id.item4).setOnClickListener(this);
		view.findViewById(R.id.item5).setOnClickListener(this);
		pop = new PopupWindow(Device.getWidth(this), Device.getHeight(this));
		pop.setContentView(view);
		pop.setOutsideTouchable(true);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pop.dismiss();
			}
		});
	}
}
