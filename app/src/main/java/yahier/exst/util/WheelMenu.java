package yahier.exst.util;

import java.util.List;

import com.example.dateselect.util.ArrayWheelAdapter;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

/**
 * 弹出滑动菜单
 * @author ruilin
 *
 */
public class WheelMenu implements OnClickListener {
	WheelView wheel;
	TextView tvValue;
	PopupWindow window;
	List<String> list;
	TextView text;
	View view;
	
	public WheelMenu(final Activity mContext, List<String> list) {
		this.list = list;
		view = LayoutInflater.from(mContext).inflate(R.layout.wheel_text, null);
		view.measure(0, 0);
		window = new PopupWindow(view, LayoutParams.MATCH_PARENT, view.getMeasuredHeight(), true);
		window.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		window.setBackgroundDrawable(dw);

		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();  
		lp.alpha = 0.7f; //0.0-1.0  
		mContext.getWindow().setAttributes(lp); 
        
		window.setAnimationStyle(R.style.bottom_popupAnimation);
		view.findViewById(R.id.window_cancel).setOnClickListener(this);
		view.findViewById(R.id.window_ok).setOnClickListener(this);
		wheel = (WheelView) view.findViewById(R.id.text);
		wheel.setAdapter(new ArrayWheelAdapter(list));
		
		window.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();  
				lp.alpha = 1.0f; //0.0-1.0  
				mContext.getWindow().setAttributes(lp); 
			}
		});
	}
	
	public void setCurrentItem(int index) {
		wheel.setCurrentItem(index);
	}
	
	public void show() {
		if (!window.isShowing())
			window.showAtLocation(view, Gravity.CENTER, 0, 1000);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.window_cancel:
			window.dismiss();
			break;
		case R.id.window_ok:
			window.dismiss();
			int index = wheel.getCurrentItem();
			listener.onTagOk(list.get(index), index);
			break;
		}
	}

	OnWheelMenuListener listener;

	public void setOnWheelMenuListener(OnWheelMenuListener listener) {
		this.listener = listener;
	}

	public interface OnWheelMenuListener {
		void onTagOk(String tag, int index);
	}
}
