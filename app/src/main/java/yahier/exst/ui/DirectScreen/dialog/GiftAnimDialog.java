package yahier.exst.ui.DirectScreen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.data.GiftAnimInfo;

import java.util.List;

/**
 * 播放礼物动画
 * Created by Administrator on 2016/3/9 0009.
 */
public class GiftAnimDialog  extends Dialog{
//    private List<GiftAnimInfo> giftAnimEntityList;//资源图片、每帧间隔时间
    private Context mContext;
    private String name;//送礼人昵称
    private String countGift;//如：一个许愿瓶
    private ImageView giftImg;
//    private AnimationDrawable animationDrawable;
    private long dissmissTime;//播放总时间
    private int giftRes;//礼物资源图
    private Bitmap bmp;//服务器推送的图片url获取位图

    public GiftAnimDialog(Context context){
        super(context,R.style.Translucent_NoTitle);
    }

//    public GiftAnimDialog(Context context,List<GiftAnimInfo> giftAnimEntityList,String name,String countGift){
//        super(context,R.style.Translucent_NoTitle);
//        this.mContext = context;
//        this.name = name;
//        this.countGift = countGift;
//        this.giftAnimEntityList = giftAnimEntityList;
//    }

    public GiftAnimDialog(Context context,int giftRes,long dissmissTime,String name,String countGift){
        super(context,R.style.Translucent_NoTitle);
        this.mContext = context;
        this.name = name;
        this.countGift = countGift;
        this.dissmissTime = dissmissTime;
        this.giftRes = giftRes;
    }

    public GiftAnimDialog(Context context,Bitmap bmp,long dissmissTime,String name,String countGift){
        super(context,R.style.Translucent_NoTitle);
        this.mContext = context;
        this.name = name;
        this.countGift = countGift;
        this.dissmissTime = dissmissTime;
        this.bmp = bmp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.gift_anim_dialog_layout,null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = d.widthPixels; // 宽度设置为屏幕的宽
        lp.height = d.heightPixels;//
        dialogWindow.setAttributes(lp);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        giftImg = (ImageView) view.findViewById(R.id.iv_gift_anim);
//        animationDrawable = getAnimationDrawable(giftAnimEntityList);
//        giftImg.setImageDrawable(animationDrawable);
//        animationDrawable.start();
        if (giftRes != 0) giftImg.setImageResource(giftRes);
        if (bmp != null){
            giftImg.setImageBitmap(bmp);
        }

        TextView giftTips = (TextView) view.findViewById(R.id.tv_sendgift_tips);
        SpannableString spannableString = new SpannableString(name + "送了" + countGift + "给你");
        spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.theme_yellow)),0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        giftTips.setText(spannableString);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private AnimationDrawable getAnimationDrawable(List<GiftAnimInfo> giftAnimEntityList){
        AnimationDrawable animationDrawable = new AnimationDrawable();
        if (mContext == null || giftAnimEntityList == null) return animationDrawable;
        for(int i=0; i<giftAnimEntityList.size(); i++) {
            int res = giftAnimEntityList.get(i).getRes();
            int duration = giftAnimEntityList.get(i).getDuration();
            animationDrawable.addFrame(mContext.getResources().getDrawable(res), duration);
            dissmissTime += duration;
        }
        return animationDrawable;
    }
    //动画总时长
    public long getDissmissTime(){
        return dissmissTime;
    }

    public void showDialog( Handler handler) {
        super.show();
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, dissmissTime);
        }
    }

    @Override
    public void dismiss() {
        if(giftImg != null)
        giftImg.clearAnimation();
        super.dismiss();
    }

}
