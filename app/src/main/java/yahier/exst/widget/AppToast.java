package yahier.exst.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;

public class AppToast extends Toast {

    private View mView;
    private TextView mTextView;

    private Handler mHandler;

    private static AppToast sToast = new AppToast(MyApplication.getContext());

    public AppToast(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(MyApplication.getContext()).inflate(
                R.layout.toast, null);
        mTextView = (TextView) mView.findViewById(R.id.text);

        mHandler = new Handler(Looper.getMainLooper());
    }

    public static void init(Context context) {
        if (sToast == null) {
            sToast = new AppToast(context.getApplicationContext());
        }
    }

    private static void show(final CharSequence text, final int duration) {
        if (sToast == null) {
            sToast = new AppToast(MyApplication.getContext());
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            internalShow(text, duration);
        } else {
            sToast.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    internalShow(text, duration);
                }
            });
        }
    }

    public static void show(int resId) {
        show(MyApplication.getContext().getString(resId));
    }

    public static void show(final CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void showLong(final CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    private static void internalShow(CharSequence text, int duration) {
        sToast.mTextView.setText(text);
        sToast.setDuration(duration);
        sToast.setGravity(Gravity.CENTER, 0, 0);
        sToast.show();
    }

    @Override
    public void show() {
        setView(mView);
        super.show();
    }

}
