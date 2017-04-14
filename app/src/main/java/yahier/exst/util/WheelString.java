package yahier.exst.util;

import java.util.List;

import com.example.dateselect.util.ArrayWheelAdapter;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
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
 * 选择出生日期的wheelView
 *
 * @author lenovo
 */
public class WheelString implements OnClickListener {
    WheelView wheel;
    TextView tvValue;
    PopupWindow window;
    List<String> list;
    TextView text;
    TextView tvOk;
    String title;//标题

    int currentItem = 0;//默认项
    /**在chooseTime前调用*/
    public void setCurrentItem(int currentItem){
        this.currentItem = currentItem;
    }
    public void chooseTime(final Activity mContext, List<String> list, String title) {
        this.title = title;
        chooseTime(mContext,list);
    }

    public void chooseTime(final Activity mContext, List<String> list) {
        this.list = list;
        View view = LayoutInflater.from(mContext).inflate(R.layout.wheel_text, null);
        view.measure(0, 0);
        window = new PopupWindow(view, LayoutParams.FILL_PARENT, view.getMeasuredHeight(), true);
//        window.setBackgroundDrawable(new BitmapDrawable());
        window.setBackgroundDrawable(new ColorDrawable(0x000000));

        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = 0.5f; //0.0-1.0
        mContext.getWindow().setAttributes(lp);

        if (title != null){
            TextView tvTitle = (TextView) view.findViewById(R.id.window_title);
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.window_cancel).setOnClickListener(this);
        tvOk = (TextView) view.findViewById(R.id.window_ok);
        tvOk.setOnClickListener(this);
        window.showAtLocation(view, Gravity.CENTER, 0, 1000);
//		window.setAnimationStyle(R.style.bottom_popupAnimation);

        wheel = (WheelView) view.findViewById(R.id.text);
        wheel.setAdapter(new ArrayWheelAdapter(list));

        if (currentItem != 0){
            wheel.setCurrentItem(currentItem);
        }
        window.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
                lp.alpha = 1.0f; //0.0-1.0
                mContext.getWindow().setAttributes(lp);

            }
        });
    }

    public void setOkText(String okText){
        if (tvOk != null){
            tvOk.setText(okText);
        }
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
                listener.onTagOk(index, list.get(index));
                break;
        }

    }

    OnTimeWheelListener listener;

    public void setOnTimeWheelListener(OnTimeWheelListener listener) {
        this.listener = listener;
    }

    public interface OnTimeWheelListener {
        void onTagOk(int index, String tag);
    }
}
