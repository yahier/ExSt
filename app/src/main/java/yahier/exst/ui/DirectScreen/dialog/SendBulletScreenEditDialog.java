package yahier.exst.ui.DirectScreen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.FaceRelativeLayout;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class SendBulletScreenEditDialog extends Dialog {
    private FaceRelativeLayout faceLin;
    private EditText bulletscreen_input;
    private ImageView bulletscreen_add;
    private Context mContext;
    private OnSendListener listener;
    private static final int maxLen = 20;

    public SendBulletScreenEditDialog(Context context, final OnSendListener listener) {
        super(context, R.style.Translucent_NoTitle2);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.send_bulletscreen_edit_layout, null);
        setContentView(view);
        final Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = d.widthPixels; // 宽度设置为屏幕的宽
//        lp.height = d.heightPixels;//
        dialogWindow.setAttributes(lp);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        faceLin = (FaceRelativeLayout) view.findViewById(R.id.input_lin);

        view.findViewById(R.id.btn_face).setVisibility(View.GONE);//隐藏表情输入
        bulletscreen_input = (EditText) view.findViewById(R.id.et_sendmessage);
        bulletscreen_input.requestFocus();
        bulletscreen_input.requestFocusFromTouch();
        bulletscreen_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();

                if (len > maxLen) {
                    ToastUtil.showToast(mContext, "字数不能超过" + maxLen);
                    int selEndIndex = Selection.getSelectionEnd(s);
                    String str = s.toString();
                    //截取新字符串
                    String newStr = str.substring(0, maxLen);
                    bulletscreen_input.setText(newStr);
                    s = bulletscreen_input.getText();

                    //新字符串的长度
                    int newLen = s.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = s.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(s, selEndIndex);
                }
            }
        });

        bulletscreen_add = (ImageView) view.findViewById(R.id.btn_send);
        bulletscreen_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String content = bulletscreen_input.getText().toString();
                    listener.onSend(content);
                }
                dismiss();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideInputMethod(bulletscreen_input);
            }
        });
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                showInputMethod(bulletscreen_input);
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public interface OnSendListener {
        void onSend(String input);
    }
}
