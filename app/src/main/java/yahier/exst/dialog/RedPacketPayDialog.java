package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.redpacket.SendRedPacketActivity;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;

public class RedPacketPayDialog extends Dialog {

    private TextView mMoneyTv;
    private LinearLayout mPayTypeLayout;
    private ImageView mPayTypeIv;
    private TextView mPayTypeTv;
    private Button mGoPayBtn;

    private SelectPayDialog mSelectPayDialog;

    private SendRedPacketActivity mActivity;

    private int paytype;

    public RedPacketPayDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_redpacket_pay);

        mActivity = (SendRedPacketActivity) context;

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mMoneyTv = (TextView) findViewById(R.id.tv_money);
        mPayTypeTv = (TextView) findViewById(R.id.tv_pay_type);
        mPayTypeLayout = (LinearLayout) findViewById(R.id.layout_pay_type);
        mPayTypeIv = (ImageView) findViewById(R.id.iv_pay_type);
        mGoPayBtn = (Button) findViewById(R.id.btn_go_pay);

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mGoPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mActivity.goPay(paytype);
            }
        });

        mPayTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mSelectPayDialog.show();
            }
        });

        mSelectPayDialog = new SelectPayDialog(context, this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setMoney(double money) {
        mMoneyTv.setText("¥ " + money);
    }

    public void setPayType(int type) {
        paytype = type;
        SharedPrefUtils.putToPublicFile(KEY.REDPACKET_PAY_TYPE, paytype);
        switch (type) {
            case Payment.TYPE_WEIXIN:
                mPayTypeIv.setImageResource(R.drawable.redpacket_wx_pay);
                mPayTypeTv.setText(R.string.me_weixin_pay);
                break;
            case Payment.TYPE_ALIPAY:
                mPayTypeIv.setImageResource(R.drawable.redpacket_ali_pay);
                mPayTypeTv.setText(R.string.me_ali_pay);
                break;
        }
    }

}
