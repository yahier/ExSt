package yahier.exst.ui.DirectScreen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.PicassoUtil;

/**
 *
 * Created by Administrator on 2016/3/9 0009.
 */
public class InRoomPasswordDialog extends Dialog {
    private Context mContext;
    private Button btnOk;
    private ImageView rivUserImg;
    private TextView tvTitle;
    private EditText tvInputPwd;
    private OkClickListener listener;

    private String title;
    private String headImg;
    private String btnText;

    public InRoomPasswordDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.mContext = context;
    }

    public InRoomPasswordDialog(Context context, int theme) {
        super(context,theme);
        this.mContext = context;
    }

    protected InRoomPasswordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public InRoomPasswordDialog(Context context, String title, String headImg, String btnText){
        super(context, R.style.Translucent_NoTitle);
        this.mContext = context;
        this.title = title;
        this.headImg = headImg;
        this.btnText = btnText;
    }

    public InRoomPasswordDialog(Context context, String title, String headImg, String btnText, OkClickListener listener){
        super(context, R.style.Translucent_NoTitle);
        this.mContext = context;
        this.title = title;
        this.headImg = headImg;
        this.btnText = btnText;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.inroom_password_dialog_layout,null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        lp.width = dm.widthPixels;
//        lp.height = dm.heightPixels;
        window.setAttributes(lp);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        btnOk = (Button) view.findViewById(R.id.btn_ok);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        rivUserImg = (ImageView) view.findViewById(R.id.riv_user_img);
        tvInputPwd = (EditText) view.findViewById(R.id.et_input_password);

        if(title != null) tvTitle.setText(title);
        if(btnText != null) btnOk.setText(btnText);
        if (headImg != null){
            PicassoUtil.load(mContext,headImg,rivUserImg);
        }

        ImageButton imgButton = (ImageButton) view.findViewById(R.id.btn_cancel);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null){
                    listener.onClick(tvInputPwd.getText().toString());
                }
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    private void showSoftInput(EditText view){
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view,InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideSoftInput(){

    }

    public void setHeadImg(String headImg){
        this.headImg = headImg;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public void setBtnText(String btnText){
        this.btnText = btnText;
    }
    public void setOkListener(OkClickListener listener){
       this.listener = listener;
    }

    public interface OkClickListener{
        void onClick(String password);
    }

}
