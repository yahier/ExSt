package yahier.exst.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stbl.stbl.R;

public class LoadingTipsDialog extends Dialog {

    private Context mContext;
    private ImageView mImg;
    private TextView mMsgTv;

    public LoadingTipsDialog(Context context) {
        super(context, R.style.Common_Dialog2);
        setContentView(R.layout.dialog_loading_tips);
//        getWindow().getAttributes();
        this.mContext = context;
        initView();
    }

    private void initView() {
        mImg = (ImageView) findViewById(R.id.iv_img);
        mMsgTv = (TextView) findViewById(R.id.tv_msg);
    }

    public void setIconMsg(String msg,int res){
        mMsgTv.setText(msg);
        if (res > 0) {
            mImg.clearAnimation();
            mImg.setImageResource(res);
        }
    }

    public void setMessage(String msg) {
        mMsgTv.setText(msg);
    }

    public void setIcon(int res) {
        if (res > 0) {
            mImg.clearAnimation();
            mImg.setImageResource(res);
        }
    }
    //显示loading icon
    public void showLoading(String msg){
        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.anim_loading);
        mImg.setImageResource(R.drawable.icon_loading);
        mImg.startAnimation(animation);
        mMsgTv.setText(msg);
        showSelf();
    }
    //显示失败icon
    public void showFaild(String msg){
        mMsgTv.setText(msg);
        mImg.clearAnimation();
        mImg.setImageResource(R.drawable.icon_failure);
        showSelf();
    }
    //显示成功icon
    public void showSuccess(String msg){
        mMsgTv.setText(msg);
        mImg.clearAnimation();
        mImg.setImageResource(R.drawable.icon_success);
        showSelf();
    }
    //定时 dissmiss
    public void postDissmiss(){
        mImg.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isShowing() || mContext == null || mContext != null && mContext instanceof Activity && ((Activity)mContext).isFinishing()){
                    return;
                }
                dismiss();
            }
        },1500);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public void showSelf(){
        if (isShowing() || mContext == null || mContext != null && mContext instanceof Activity && ((Activity)mContext).isFinishing()){
            return;
        }
        show();
    }
}
