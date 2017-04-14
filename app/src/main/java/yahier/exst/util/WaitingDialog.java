package yahier.exst.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

public class WaitingDialog {

    static Dialog dialog;
    static TextView tvContent;

    public static void show(Context mContext, String content) {
//		dialog = new Dialog(mContext, R.style.dialog);
//		View view = LayoutInflater.from(mContext).inflate(R.layout.waiting_dialog, null);
//		tvContent = (TextView) view.findViewById(R.id.tvContent);
//		tvContent.setText(content);
//		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//		int width = wm.getDefaultDisplay().getWidth()/2;
//		view.measure(0, 0);
//		int height = view.getMeasuredHeight();
//		dialog.setContentView(view, new LinearLayout.LayoutParams(width, height));
//		dialog.show();
        show(mContext, content, true);
    }

    public static void show(Context mContext, boolean isCancelable) {
        show(mContext, mContext.getString(R.string.waiting), isCancelable);
    }

    public static void show(Context mContext, int content) {
        show(mContext, mContext.getString(content), true);
    }

    public static void show(Context mContext, int content, boolean isCancelable) {
        show(mContext, mContext.getString(content), isCancelable);
    }

    public static void show(Context mContext, String content, boolean isCancelable) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setCancelable(isCancelable);
            setContent(content);
            return;
        }
        dialog = new Dialog(mContext, R.style.dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.waiting_dialog, null);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText(content);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth() / 2;
        view.measure(0, 0);
        int height = view.getMeasuredHeight();
        dialog.setContentView(view, new LinearLayout.LayoutParams(width, height));
        dialog.setCancelable(isCancelable);
        dialog.show();

    }

    public static void showTouchNotCancel(Context mContext, String content) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            setContent(content);
            return;
        }
        dialog = new Dialog(mContext, R.style.dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.waiting_dialog, null);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText(content);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth() / 2;
        view.measure(0, 0);
        int height = view.getMeasuredHeight();
        dialog.setContentView(view, new LinearLayout.LayoutParams(width, height));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public static boolean isShow() {
        if (dialog != null && dialog.isShowing()) return true;
        else return false;

    }


    public static void setContent(String content) {
        if (tvContent != null)
            tvContent.setText(content);
    }

    public static void setContent(int content) {
        if (tvContent != null)
            tvContent.setText(content);
    }

    public static void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch(IllegalArgumentException e) {

            }
        }
    }

}
