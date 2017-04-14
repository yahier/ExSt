package yahier.exst.util;

import java.util.Calendar;

import com.example.dateselect.util.NumericWheelAdapter;
import com.example.dateselect.util.OnWheelScrollListener;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 选择出生日期的wheelView
 * 
 * @author lenovo
 * 
 */
public class WheelTime implements OnClickListener {
	WheelView wheel1;
	WheelView wheel2;
	WheelView wheel3;
	NumericWheelAdapter dayAdapter;
	final int leastYear = 1960;
	final int lastYear = Calendar.getInstance().get(Calendar.YEAR) - 12;

	Dialog pop;
	private Context mContext;

	public void chooseTime(final Activity mContext) {
		this.mContext = mContext;
		pop = new Dialog(mContext, R.style.Common_Dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.wheel_date, null);
		view.findViewById(R.id.window_cancel).setOnClickListener(this);
		view.findViewById(R.id.window_ok).setOnClickListener(this);
		wheel1 = (WheelView) view.findViewById(R.id.car_region_province);
		wheel2 = (WheelView) view.findViewById(R.id.car_region_city);
		wheel3 = (WheelView) view.findViewById(R.id.car_region_area);
		wheel1.setAdapter(new NumericWheelAdapter(leastYear, lastYear,mContext.getString(R.string.year)));
		wheel2.setAdapter(new NumericWheelAdapter(1, 12,mContext.getString(R.string.month)));

		wheel2.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				int month = wheel2.getCurrentItem() + 1;
				valueDayAdapter(month);
			}
		});
		Calendar rightNow = Calendar.getInstance();
		wheel1.setCurrentItem(lastYear - leastYear);// 上面坐标的起始是2013
		wheel2.setCurrentItem(rightNow.get(Calendar.MONTH));
		valueDayAdapter(rightNow.get(Calendar.MONTH) + 1);
		wheel3.setAdapter(dayAdapter);
		wheel3.setCurrentItem(rightNow.get(Calendar.DAY_OF_MONTH) - 1);// 上面坐标的起始是

		pop.setContentView(view);
		Window window = pop.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
		pop.show();
	}

	void valueDayAdapter(int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			dayAdapter = new NumericWheelAdapter(1, 31,mContext.getString(R.string.day));
			wheel3.setAdapter(dayAdapter);
			break;
		case 2:
		case 4:
		case 6:
		case 9:
		case 11:
			dayAdapter = new NumericWheelAdapter(1, 30,mContext.getString(R.string.day));
			wheel3.setAdapter(dayAdapter);
			break;
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.window_cancel:
			pop.dismiss();
			break;
		case R.id.window_ok:
			pop.dismiss();
			int year = wheel1.getCurrentItem() + leastYear;
			int month = wheel2.getCurrentItem() + 1;
			int day = wheel3.getCurrentItem() + 1;
			String value = year + "-" + month + "-" + day;
			listener.onTimeOk(value);
			break;
		}

	}

	OnTimeWheelListener listener;

	public void setOnTimeWheelListener(OnTimeWheelListener listener) {
		this.listener = listener;
	}

	public interface OnTimeWheelListener {
		void onTimeOk(String ymd);
	}
}
