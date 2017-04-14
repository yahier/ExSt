package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;

public class SystemTipDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContentTv;

    private ISystemTipDialog mInterface;

    public SystemTipDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_system_tip);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Device.getWidth() - DimenUtils.dp2px(48);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        initView();
    }


    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mContentTv = (TextView) findViewById(R.id.tv_content);

        findViewById(R.id.btn_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
        findViewById(R.id.btn_confirm).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (mInterface != null) {
                            mInterface.onConfirm();
                        }
                    }
                });
    }

    public void setTitle(CharSequence text) {
        mTitleTv.setText(text);
    }

    public void setContent(CharSequence text) {
        mContentTv.setText(text);
    }

    public void setInterface(ISystemTipDialog i) {
        mInterface = i;
    }

    public interface ISystemTipDialog {
        void onConfirm();
    }

}
