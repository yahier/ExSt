package yahier.exst.util;

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
import com.stbl.stbl.item.CountryPhoneCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择国家码
 *
 * @author lenovo
 */
public class WheelContryCode implements OnClickListener {
    WheelView wheel;
    TextView tvValue;
    PopupWindow window;
    List<CountryPhoneCode> list;
    TextView text;


    public void chooseTime(Context mContext, List<CountryPhoneCode> list) {
        this.list = list;
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
            titleList.add(list.get(i).getCountry() + "+" + list.get(i).getPrefix());
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
                listener.onTagOk(list.get(index));
                break;
        }

    }

    OnWheelListener listener;

    public void setOnCodeWheelListener(OnWheelListener listener) {
        this.listener = listener;
    }

    public interface OnWheelListener {
        void onTagOk(CountryPhoneCode tag);
    }
}
