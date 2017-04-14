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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.widget.avsdk.QavsdkManger;

/**
 *
 * Created by Administrator on 2016/3/9 0009.
 */
public class InviteTipsDialog extends Dialog {
    private Context mContext;
    private Button btnOk;
    private boolean bgHide;
    private ImageView rivUserImg;
    private TextView tvTitle;
    private TextView tvContent;
    private ImageView ivBg;//背景图
    private OkClickListener listener;

    private String title;
    private String headImg;
    private String content;
    private String btnText;

    public InviteTipsDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.mContext = context;
    }

    public InviteTipsDialog(Context context, int theme) {
        super(context,theme);
        this.mContext = context;
    }

    protected InviteTipsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public InviteTipsDialog(Context context,String title,String headImg,String content,String btnText){
        super(context, R.style.Translucent_NoTitle);
        this.mContext = context;
        this.title = title;
        this.headImg = headImg;
        this.content = content;
        this.btnText = btnText;
    }

    public InviteTipsDialog(Context context,String title,boolean bgHide,String headImg,String content,String btnText,OkClickListener listener){
        super(context, R.style.Translucent_NoTitle);
        this.mContext = context;
        this.bgHide = bgHide;
        this.title = title;
        this.headImg = headImg;
        this.content = content;
        this.btnText = btnText;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.invite_tips_dialog_layout,null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        lp.width = dm.widthPixels;
        lp.height = dm.heightPixels;
        window.setAttributes(lp);

        btnOk = (Button) view.findViewById(R.id.btn_ok);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        rivUserImg = (ImageView) view.findViewById(R.id.riv_user_img);
        ivBg = (ImageView) view.findViewById(R.id.iv_bg);

        if (bgHide) ivBg.setVisibility(View.INVISIBLE);
        if(title != null) tvTitle.setText(title);
        if(content != null) tvContent.setText(content);
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
                    listener.onClick();
                }
                if (!bgHide){

                }
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    public void setHeadImg(String headImg){
        this.headImg = headImg;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setBtnText(String btnText){
        this.btnText = btnText;
    }
    public void setOkListener(OkClickListener listener){
       this.listener = listener;
    }

    public interface OkClickListener{
        void onClick();
    }

}
