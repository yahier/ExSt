package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stbl.stbl.R;

public class LoadingDialog extends Dialog {

    private ProgressBar mProgressBar;
    private TextView mMsgTv;

    public LoadingDialog(Context context) {
        super(context, R.style.Common_Dialog2);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mMsgTv = (TextView) findViewById(R.id.tv_msg);
    }

    public void setMessage(String msg) {
        mMsgTv.setText(msg);
    }

}
