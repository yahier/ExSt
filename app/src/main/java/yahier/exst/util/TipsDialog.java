package yahier.exst.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * 提示框
 *
 * @author ruilin
 */
public class TipsDialog implements OnClickListener {
    AlertDialog dialog;
    Context mContext;
    View mView;
    TextView titleView;
    boolean cancelAble = true;
    private DialogInterface.OnCancelListener cancelListener;

    public TipsDialog(Context ctx, String title, CharSequence msg, String btnCancel, String btnOk) {
        init(ctx, title, msg, btnOk, btnCancel);
        mView.findViewById(R.id.btn_one).setVisibility(View.GONE);
        mView.findViewById(R.id.btn_two).setVisibility(View.VISIBLE);
    }

    public TipsDialog(Context ctx, String title, CharSequence msg, String btnOk) {
        init(ctx, title, msg, btnOk, "");
        mView.findViewById(R.id.btn_one).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.btn_two).setVisibility(View.GONE);
    }

    private void init(Context ctx, String title, CharSequence msg, String btnOk, String btnCancel) {
        mContext = ctx;
        mView = LayoutInflater.from(mContext).inflate(R.layout.mall_pop_tips, null);
        Button btn = (Button) mView.findViewById(R.id.button1);
        btn.setOnClickListener(this);
        btn.setText(btnCancel);
        btn = (Button) mView.findViewById(R.id.button2);
        btn.setOnClickListener(this);
        btn.setText(btnOk);
        btn = (Button) mView.findViewById(R.id.button3);
        btn.setOnClickListener(this);
        btn.setText(btnOk);
        titleView = (TextView) mView.findViewById(R.id.title);
        titleView.setText(title);
        TextView contetnView = (TextView) mView.findViewById(R.id.tv_content);
        contetnView.setText(msg);

    }

    public void setTitleGone() {
        titleView.setVisibility(View.GONE);
        mView.findViewById(R.id.line1).setVisibility(View.GONE);
    }

    public void setCancelable(boolean cancelAble) {
        if (dialog == null) return;
        this.cancelAble = cancelAble;
        dialog.setCancelable(cancelAble);
        dialog.setCanceledOnTouchOutside(cancelAble);
    }

    public void setOnDissmissListener(DialogInterface.OnDismissListener dissmissListener) {
        if (dialog != null)
            dialog.setOnDismissListener(dissmissListener);
    }

    public boolean isShow() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void show() {
        if (mContext == null) return;
        if ((mContext instanceof Activity)) {
            if (((Activity) mContext).isFinishing()) return;
        }
        if (null == dialog) {
            dialog = new AlertDialog.Builder(mContext).create();
            dialog.show();
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = Device.getWidth() - DimenUtils.dp2px(48);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            window.setContentView(mView);
//			win.setWindowAnimations(resId);
            //mView.measure(0, 0);
//			dialog.getWindow().setLayout(Util.dip2px(mContext, 300), Util.dip2px(mContext, 300));
            dialog.setCanceledOnTouchOutside(cancelAble);
            if (cancelListener != null) dialog.setOnCancelListener(cancelListener);
        } else if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button1:
                if (null != listener)
                    listener.onCancel();
                break;
            case R.id.button2:
            case R.id.button3:
                if (null != listener)
                    listener.onConfirm();
                break;
        }
        dialog.dismiss();
    }

    public void setTitleCompoundDrawables(int left) {
        titleView.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
    }

    public void setTitleColor(int color) {
        titleView.setTextColor(color);
    }

    public void setOnTipsListener(OnTipsListener listener) {
        this.listener = listener;
    }

    OnTipsListener listener;

    public interface OnTipsListener {
        public void onConfirm();

        public void onCancel();
    }

    public static TipsDialog popup(Context ctx, String msg, String btnCancel, String btnOk, OnTipsListener listener) {
        TipsDialog dialog = new TipsDialog(ctx, ctx.getString(R.string.tip), msg, btnCancel, btnOk);
        dialog.setOnTipsListener(listener);
        dialog.show();
        return dialog;
    }

    public static TipsDialog popup(Context ctx, int msg, int btnCancel, int btnOk, OnTipsListener listener) {
        return popup(ctx, ctx.getString(R.string.tip), ctx.getString(msg), ctx.getString(btnCancel), ctx.getString(btnOk), listener);
        //TipsDialog dialog = new TipsDialog(ctx, "提示", ctx.getString(msg), ctx.getString(btnCancel), ctx.getString(btnOk));
        //dialog.setOnTipsListener(listener);
        //dialog.show();
        //return dialog;
    }

    public static TipsDialog popup(Context ctx, String title, String msg, String btnCancel, String btnOk, OnTipsListener listener) {
        TipsDialog dialog = new TipsDialog(ctx, title, msg, btnCancel, btnOk);
        dialog.setOnTipsListener(listener);
        dialog.show();
        return dialog;
    }

    public static TipsDialog popup(Context ctx, String msg, String btnOk, OnTipsListener listener) {
        TipsDialog dialog = new TipsDialog(ctx, ctx.getString(R.string.tip), msg, btnOk);
        dialog.setOnTipsListener(listener);
        dialog.show();
        return dialog;
    }


    public static TipsDialog popup(Context ctx, int msg, int btnOk) {
        return popup(ctx, ctx.getString(R.string.tip), ctx.getString(msg), ctx.getString(btnOk));
    }


    public static TipsDialog popup(Context ctx, String msg, String btnOk) {
        TipsDialog dialog = new TipsDialog(ctx, ctx.getString(R.string.tip), msg, btnOk);
        dialog.show();
        return dialog;
    }

    public static TipsDialog popup(Context ctx, String title, CharSequence msg, String btnOk) {
        TipsDialog dialog = new TipsDialog(ctx, title, msg, btnOk);
        dialog.show();
        return dialog;
    }

    /**
     * 展示订单相关的人工介入
     *
     * @param ctx
     */
    public static void showOrderHumanHelp(final Context ctx) {

        popup(ctx, ctx.getString(R.string.service_hotline), ctx.getString(R.string.is_dial_service_phone), ctx.getString(R.string.cancel), ctx.getString(R.string.queding), new OnTipsListener() {

            @Override
            public void onConfirm() {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ctx.getString(R.string.orderHumanPhone)));
                ctx.startActivity(intent);
            }

            @Override
            public void onCancel() {
            }
        });
    }
}
