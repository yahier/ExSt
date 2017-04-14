package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.stbl.stbl.R;
import com.stbl.stbl.act.redpacket.SendRedPacketActivity;

public class PayUnconfirmedDialog extends Dialog {

    private SendRedPacketActivity mActivity;

    public PayUnconfirmedDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_pay_unconfirmed);

        mActivity = (SendRedPacketActivity) context;

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.btn_resend_redpacket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.btn_return_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mActivity.afterPaySuccess();
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

}
