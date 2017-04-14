package yahier.exst.ui.DirectScreen.service.floating;

import java.util.Calendar;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 防止按钮被重复点击
 * @author yukai 2015-09-14
 */
public abstract class NoDuplicateClickListener implements OnClickListener {
	public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    public abstract void onNoDulicateClick(View v);
	@Override
	public void onClick(View v) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDulicateClick(v);
        } 
	}
}
