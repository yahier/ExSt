package yahier.exst.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.DimenUtils;

public class AlertDialog extends Dialog {

    private TextView mContentTv;
    private TextView mSubContentTv;
    private Button mNegativeBtn;
    private Button mPositiveBtn;
    private LinearLayout mActionLayout;

    private AlertDialogInterface mInterface;

    public AlertDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_alert);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mContentTv = (TextView) findViewById(R.id.tv_content);
        mSubContentTv = (TextView) findViewById(R.id.tv_sub_content);
        mActionLayout = (LinearLayout) findViewById(R.id.layout_action);
        mNegativeBtn = (Button) findViewById(R.id.btn_negative);
        mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mInterface != null) {
                    mInterface.onNegative();
                }
            }
        });

        mPositiveBtn = (Button) findViewById(R.id.btn_positive);
        mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mInterface != null) {
                    mInterface.onPositive();
                }
            }
        });
    }

    public void setMessage(CharSequence msg) {
        mContentTv.setText(msg);
    }

    public void setSubMessage(CharSequence msg) {
        mSubContentTv.setVisibility(View.VISIBLE);
        mSubContentTv.setText(msg);
    }

    public void setInterface(AlertDialogInterface i) {
        mInterface = i;
    }

    public void setNegativeText(CharSequence text) {
        mNegativeBtn.setText(text);
    }

    public void setPositiveText(CharSequence text) {
        mPositiveBtn.setText(text);
    }

    public void setOnlyPositive(boolean onlyPositive) {
        if (onlyPositive) {
            mNegativeBtn.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DimenUtils.dp2px(120), DimenUtils.dp2px(40));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.setMargins(0, DimenUtils.dp2px(20), 0, 0);
            mActionLayout.setLayoutParams(params);
        }
    }

    public static AlertDialog create(Activity activity, CharSequence msg, AlertDialogInterface i) {
        AlertDialog dialog = new AlertDialog(activity);
        dialog.setMessage(msg);
        dialog.setInterface(i);
        return dialog;
    }

    public static AlertDialog create(Activity activity, CharSequence msg, CharSequence negative, CharSequence positive, AlertDialogInterface i) {
        AlertDialog dialog = new AlertDialog(activity);
        dialog.setMessage(msg);
        dialog.setNegativeText(negative);
        dialog.setPositiveText(positive);
        dialog.setInterface(i);
        return dialog;
    }

    public static AlertDialog create(Activity activity, CharSequence msg, CharSequence subMsg, CharSequence negative, CharSequence positive, AlertDialogInterface i) {
        AlertDialog dialog = new AlertDialog(activity);
        dialog.setMessage(msg);
        dialog.setSubMessage(subMsg);
        dialog.setNegativeText(negative);
        dialog.setPositiveText(positive);
        dialog.setInterface(i);
        return dialog;
    }

    public static AlertDialog create(Activity activity, CharSequence msg, AlertDialogInterface i, boolean onlyPositive) {
        AlertDialog dialog = new AlertDialog(activity);
        dialog.setMessage(msg);
        dialog.setInterface(i);
        dialog.setOnlyPositive(onlyPositive);
        return dialog;
    }

    public interface AlertDialogInterface {
        void onNegative();

        void onPositive();
    }
}
