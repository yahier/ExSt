package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.InputMethodUtils;

/**
 * 废弃，请调用AddFriendUtil
 */
public class AddFriendDialog extends Dialog {

    private TextView mTitleTv;
    private EditText mEt;

    private long mUserId;

    private IAddFriendDialog mInterface;

    public AddFriendDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_add_friend);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        initView();
    }

    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.window_title);
        mEt = (EditText) findViewById(R.id.input);

        findViewById(R.id.btnCancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mInterface != null) {
                    mInterface
                            .onConfirm(mUserId, mEt.getText().toString().trim());
                }
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        InputMethodUtils.hideInputMethod(mEt);
    }

    public void setUserInfo(long userId, String nick) {
        mUserId = userId;
        String title = "添加<font color='#DEA524'>" + nick + "</font>为好友";
        mTitleTv.setText(Html.fromHtml(title));
    }

    public void setInterface(IAddFriendDialog i) {
        mInterface = i;
    }

    public interface IAddFriendDialog {
        void onConfirm(long userId, String msg);
    }

}
