package yahier.exst.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.dateselect.util.ArrayWheelAdapter;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;
import com.stbl.stbl.item.Tag;

/**
 * 选择出生日期的wheelView
 * 
 * @author lenovo
 * 
 */
public class WheelTag implements OnClickListener {
	WheelView wheel;
	TextView tvValue;
	PopupWindow window;
	List<Tag> list;
	TextView text;
	int type = 1;


	public void chooseTime(Context mContext, List<Tag> list, int type) {
		this.list = list;
		this.type = type;
		View view = LayoutInflater.from(mContext).inflate(R.layout.wheel_text, null);
		view.measure(0, 0);
		window = new PopupWindow(view, LayoutParams.FILL_PARENT, view.getMeasuredHeight(), true);
		window.setBackgroundDrawable(new BitmapDrawable());
		view.findViewById(R.id.window_cancel).setOnClickListener(this);
		view.findViewById(R.id.window_ok).setOnClickListener(this);
		window.showAtLocation(view, Gravity.CENTER, 0, 1000);
		wheel = (WheelView) view.findViewById(R.id.text);

		List<String> titleList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			titleList.add(list.get(i).getTitle());
		}
		wheel.setAdapter(new ArrayWheelAdapter(titleList));

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
			//// text.setText(list.get(index).getTitle());
			if (listener != null && index < list.size())
				listener.onTagOk(list.get(index), type);
			break;
		}

	}

	OnTimeWheelListener listener;

	public void setOnTimeWheelListener(OnTimeWheelListener listener) {
		this.listener = listener;
	}

	public interface OnTimeWheelListener {
		void onTagOk(Tag tag, int type);
	}
}
