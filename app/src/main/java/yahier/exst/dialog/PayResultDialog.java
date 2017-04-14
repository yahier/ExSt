package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.redpacket.SendRedPacketActivity;

public class PayResultDialog extends Dialog {

    private TextView mTv;
    private boolean mIsSuccess;

    private SendRedPacketActivity mActivity;

    public PayResultDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_pay_result);

        mActivity = (SendRedPacketActivity) context;
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mTv = (TextView) findViewById(R.id.tv);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    public void setResult(boolean isSuccess) {
        mIsSuccess = isSuccess;
        if (mIsSuccess) {
            mTv.setText(R.string.me_pay_success);
            mTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pay_success, 0, 0);
        } else {
            mTv.setText(R.string.me_pay_fail);
            mTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pay_fail, 0, 0);
        }
    }

    @Override
    public void show() {
        super.show();
        mTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
                if (mIsSuccess) {
                    mActivity.afterPaySuccess();
                }
            }
        }, 3000);
    }
}
