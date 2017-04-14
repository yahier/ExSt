package yahier.exst.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.Res;

public class SelectPayDialog extends Dialog {

    private TextView mWxPayTv;
    private LinearLayout mWxPayLayout;
    private LinearLayout mAliPayLayout;

    private RedPacketPayDialog mPayDialog;

    public SelectPayDialog(Context context, RedPacketPayDialog dialog) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_select_pay);
        mPayDialog = dialog;

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mWxPayTv = (TextView) findViewById(R.id.tv_wx_pay);
        mWxPayLayout = (LinearLayout) findViewById(R.id.layout_weixin_pay);
        mAliPayLayout = (LinearLayout) findViewById(R.id.layout_ali_pay);

        if (!CommonShare.isWechatInstalled((Activity) context)) {
            mWxPayTv.setText(R.string.me_please_install_wechat);
            mWxPayTv.setTextColor(Res.getColor(R.color.f_gray));
        } else {
            mWxPayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    mPayDialog.setPayType(Payment.TYPE_WEIXIN);
                    mPayDialog.show();
                }
            });
        }

        findViewById(R.id.iv_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mPayDialog.show();
            }
        });

        mAliPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mPayDialog.setPayType(Payment.TYPE_ALIPAY);
                mPayDialog.show();
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
