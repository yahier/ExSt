package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.stbl.stbl.R;

public class HandlingDialog extends Dialog {

    private ImageView mIv;
    private AnimationDrawable mRotateAnim;

    public HandlingDialog(Context context) {
        super(context, R.style.Common_Dialog2);
        setContentView(R.layout.dialog_handling);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mIv = (ImageView) findViewById(R.id.iv_loading);
        mRotateAnim = (AnimationDrawable) mIv.getDrawable();

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
