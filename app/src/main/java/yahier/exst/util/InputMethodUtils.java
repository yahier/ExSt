package yahier.exst.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.stbl.stbl.common.MyApplication;

import java.util.Timer;
import java.util.TimerTask;

public class InputMethodUtils {

    public static InputMethodManager getInputMethodManager() {
        InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm;
    }

    /**
     * 隱藏輸入法
     *
     * @param view {@link EditText}
     */
    public static boolean hideInputMethod(EditText view) {
        if (view == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 顯示輸入法
     *
     * @param view {@link EditText}
     */
    public static void showInputMethod(EditText view) {
        if (view == null) {
            return;
        }
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager m = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        // m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // (这个方法可以实现输入法在窗口上切换显示，如果输入法在窗口上已经显示，则隐藏，如果隐藏，则显示输入法到窗口上)
    }

    public static void showInputMethodDelay(final EditText view, int delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                view.post(new Runnable() {

                    @Override
                    public void run() {
                        view.setFocusable(true);
                        view.setFocusableInTouchMode(true);
                        view.requestFocus();
                        showInputMethod(view);
                    }
                });
            }
        }, delay);
    }

    public static void showInputMethodDelay(final EditText view) {
        showInputMethodDelay(view, 350);
    }
}
