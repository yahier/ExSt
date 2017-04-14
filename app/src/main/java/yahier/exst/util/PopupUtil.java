package yahier.exst.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.stbl.stbl.R;

public class PopupUtil {
	Activity mAct;
	PopupWindow pop;
	public final int methodFollow = 1;
	public final int methodShild = 2;
	public final int methodUnFollow = 3;
	public final int methodUnShild = 4;

	public PopupUtil(Activity mAct) {
		this.mAct = mAct;
	}

	public void show(String viewText1, final int methodIndex, String viewText2, final int methodIndex2) {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
			return;
		}
		View view = LayoutInflater.from(mAct).inflate(R.layout.window_get_pic, null);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pop.dismiss();

			}
		});

		if (viewText1 == null) {
			bt1.setVisibility(View.GONE);
		} else {
			bt1.setText(viewText1);
			bt1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					pop.dismiss();
					doMethod(methodIndex);
				}
			});

		}
		if (viewText2 == null) {
			bt2.setVisibility(View.GONE);
		} else {
			bt2.setText(viewText2);
			bt2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					pop.dismiss();
					doMethod(methodIndex2);
				}
			});

		}
		view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		view.measure(0, 0);
		pop = new PopupWindow(view, Device.getWidth(mAct), Device.getHeight(mAct));
		pop.setBackgroundDrawable(new ColorDrawable(-00000));
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
	}

	void doMethod(int mothedIndex) {
		switch (mothedIndex) {
		case methodFollow:
			listener.doFollow();
			break;
		case methodShild:
			listener.doShield();
			break;
		case methodUnFollow:
			listener.doUnFollow();
			break;
		case methodUnShild:
			listener.doUnShield();
			break;
		}
	}

	public void setOnMoreListener(OnMoreListener listener) {
		this.listener = listener;
	}

	OnMoreListener listener;

	public interface OnMoreListener {
		public void doFollow();

		public void doShield();

		public void doUnFollow();

		public void doUnShield();
	}

}
