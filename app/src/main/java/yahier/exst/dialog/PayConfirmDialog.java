package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.stbl.stbl.R;

public class PayConfirmDialog extends Dialog {

    private ImageView mIv;
    private AnimationDrawable mRotateAnim;

    public PayConfirmDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_pay_confirm);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mIv = (ImageView) findViewById(R.id.iv_loading);
        mRotateAnim = (AnimationDrawable) mIv.getDrawable();
//        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    @Override
    public void show() {
        super.show();
        mRotateAnim.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mRotateAnim.stop();
    }
}
