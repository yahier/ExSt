package yahier.exst.act.dongtai;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.LogUtil;

/**
 * Created by lenovo on 2016/6/15.
 */
public class StatusesAnimUtil {

    //展示收藏动画
    public static void showAnimCollect(Context mContext, TextView tvFovor, ImageView img, int count) {
        float scaleValue = 1.25f;
        final int duration = 200;

        tvFovor.setText(String.valueOf(count));
        img.setImageResource(R.drawable.dongtai_collect_pressed);
        //图标放大
        ObjectAnimator anim11 = ObjectAnimator.ofFloat(img, "scaleX", 1, scaleValue);
        ObjectAnimator anim12 = ObjectAnimator.ofFloat(img, "scaleY", 1, scaleValue);
        AnimatorSet set1 = new AnimatorSet();
        set1.setDuration(duration).playTogether(anim11, anim12);
        //缩小
        ObjectAnimator anim21 = ObjectAnimator.ofFloat(img, "scaleX", scaleValue, 1f);
        ObjectAnimator anim22 = ObjectAnimator.ofFloat(img, "scaleY", scaleValue, 1f);
        AnimatorSet set2 = new AnimatorSet();
        set2.setDuration(duration).playTogether(anim21, anim22);

        AnimatorSet setAll = new AnimatorSet();
        setAll.playSequentially(set1, set2);
        setAll.start();

        //文字颜色渐变
        ValueAnimator colorAnim = ObjectAnimator.ofInt(tvFovor, "textColor", mContext.getResources().getColor(R.color.black7), mContext.getResources().getColor(R.color.theme_brown));
        colorAnim.setDuration(duration);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
        if (tvFovor == null) {
            LogUtil.logE("testUpdateSingle", "viewCollect is null");
            return;
        }

    }

    //展示 取消收藏动画
    public static void showAnimCancelCollect(Context mContext, TextView tvFovor, final ImageView img, int count) {
        float scaleSmall = 0.5f;
        final int duration = 200;
        tvFovor.setText(String.valueOf(count));
        //缩小
        ObjectAnimator anim21 = ObjectAnimator.ofFloat(img, "scaleX", 1, scaleSmall);
        ObjectAnimator anim22 = ObjectAnimator.ofFloat(img, "scaleY", 1, scaleSmall);
        AnimatorSet set1 = new AnimatorSet();
        set1.setDuration(duration).playTogether(anim21, anim22);
        set1.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                img.setImageResource(R.drawable.dongtai_item4);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        //放大
        ObjectAnimator anim11 = ObjectAnimator.ofFloat(img, "scaleX", scaleSmall, 1);
        ObjectAnimator anim12 = ObjectAnimator.ofFloat(img, "scaleY", scaleSmall, 1);
        AnimatorSet set2 = new AnimatorSet();
        set2.setDuration(duration).playTogether(anim11, anim12);

        AnimatorSet setAll = new AnimatorSet();
        setAll.playSequentially(set1, set2);
        setAll.start();

        //文字颜色渐变
        ValueAnimator colorAnim = ObjectAnimator.ofInt(tvFovor, "textColor", mContext.getResources().getColor(R.color.theme_brown), mContext.getResources().getColor(R.color.black7));
        colorAnim.setDuration(duration);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
        if (tvFovor == null) {
            LogUtil.logE("testUpdateSingle", "viewCollect is null");
            return;
        }

    }


    //展示 点赞与取消点赞 动画
    public static void showAnimPraiseAnim(Context mContext, TextView tvPraise, final ImageView img, PraiseResult praiseItem, Statuses statuses) {
        float scaleValue = 1.25f;
        final int duration = 200;
        tvPraise.setText(String.valueOf(praiseItem.getCount()));
        if (praiseItem.getType() == PraiseResult.type_add) {
            statuses.setIspraised(Statuses.ispraisedYes);
            //点赞动画
            img.setImageResource(R.drawable.dongtai_praise_presed);
            //图标放大
            ObjectAnimator anim11 = ObjectAnimator.ofFloat(img, "scaleX", 1, scaleValue);
            ObjectAnimator anim12 = ObjectAnimator.ofFloat(img, "scaleY", 1, scaleValue);
            AnimatorSet set1 = new AnimatorSet();
            set1.setDuration(duration).playTogether(anim11, anim12);
            //缩小
            ObjectAnimator anim21 = ObjectAnimator.ofFloat(img, "scaleX", scaleValue, 1f);
            ObjectAnimator anim22 = ObjectAnimator.ofFloat(img, "scaleY", scaleValue, 1f);
            AnimatorSet set2 = new AnimatorSet();
            set2.setDuration(duration).playTogether(anim21, anim22);

            AnimatorSet setAll = new AnimatorSet();
            setAll.playSequentially(set1, set2);
            setAll.start();

            //文字颜色渐变
            ValueAnimator colorAnim = ObjectAnimator.ofInt(tvPraise, "textColor", mContext.getResources().getColor(R.color.black7), mContext.getResources().getColor(R.color.theme_brown));
            colorAnim.setDuration(duration);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.start();


        } else if (praiseItem.getType() == PraiseResult.type_cancel) {
            statuses.setIspraised(Statuses.ispraisedNo);
            //取消点赞动画
            float scaleSmall = 0.5f;
            //缩小
            ObjectAnimator anim21 = ObjectAnimator.ofFloat(img, "scaleX", 1, scaleSmall);
            ObjectAnimator anim22 = ObjectAnimator.ofFloat(img, "scaleY", 1, scaleSmall);
            AnimatorSet set1 = new AnimatorSet();
            set1.setDuration(duration).playTogether(anim21, anim22);
            set1.addListener(new AnimatorSet.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    img.setImageResource(R.drawable.dongtai_item3);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            //放大
            ObjectAnimator anim11 = ObjectAnimator.ofFloat(img, "scaleX", scaleSmall, 1);
            ObjectAnimator anim12 = ObjectAnimator.ofFloat(img, "scaleY", scaleSmall, 1);
            AnimatorSet set2 = new AnimatorSet();
            set2.setDuration(duration).playTogether(anim11, anim12);

            AnimatorSet setAll = new AnimatorSet();
            setAll.playSequentially(set1, set2);
            setAll.start();

            //文字颜色渐变
            ValueAnimator colorAnim = ObjectAnimator.ofInt(tvPraise, "textColor", mContext.getResources().getColor(R.color.theme_brown), mContext.getResources().getColor(R.color.black7));
            colorAnim.setDuration(duration);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.start();

        }

    }


    //原本用在Tabhome中的发布动画的左右动画
    public void showAnimationHorizontal(boolean isShow,View linAnima,View imgPulishLong,View imgPulishShort,View imgStatusesMore) {
        int duration = 300;
        int screenWidth = Device.getWidth();//tabHost.getWidth();
        int x1 = screenWidth / 7 * 4;
        int x2 = screenWidth / 7 * 2;
        if (isShow) {
            //linAnima.setBackgroundColor(getResources().getColor(R.color.statuses_main_anim_bg));
            ObjectAnimator.ofFloat(imgStatusesMore, "rotation", 0, -45).setDuration(duration).start();
            //新加
            ValueAnimator colorAnim = ObjectAnimator.ofInt(linAnima, "backgroundColor", 0x00000000, 0xdd333333);//.setDuration(duration).start();
            colorAnim.setDuration(duration);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.start();

            AnimatorSet setLong = new AnimatorSet();
            setLong.playTogether(
                    ObjectAnimator.ofFloat(imgPulishLong, "translationX", 0, -x1),
                    ObjectAnimator.ofFloat(imgPulishLong, "alpha", 0, 1, 1),

                    ObjectAnimator.ofFloat(imgPulishShort, "translationX", 0, -x2),
                    ObjectAnimator.ofFloat(imgPulishShort, "alpha", 0, 1, 1)
            );
            setLong.setDuration(duration).start();
            linAnima.setClickable(true);//展示动画之后
            LogUtil.logE("linAnima", "linAnima is false");
        } else {
            //linAnima.setBackgroundColor(getResources().getColor(R.color.transparent));
            ValueAnimator colorAnim = ObjectAnimator.ofInt(linAnima, "backgroundColor", 0xdd333333, 0x00000000);
            colorAnim.setDuration(duration);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.start();

            ObjectAnimator.ofFloat(imgStatusesMore, "rotation", -45, 0).setDuration(duration).start();

            AnimatorSet setLong = new AnimatorSet();
            setLong.playTogether(
                    ObjectAnimator.ofFloat(imgPulishLong, "translationX", -x1, 0),
                    ObjectAnimator.ofFloat(imgPulishLong, "alpha", 1, 0, 0),

                    ObjectAnimator.ofFloat(imgPulishShort, "translationX", -x2, 0),
                    ObjectAnimator.ofFloat(imgPulishShort, "alpha", 1, 0, 0)
            );
            setLong.setDuration(duration).start();

            linAnima.setClickable(false);
            LogUtil.logE("linAnima", "linAnima is true");
        }
    }


    //新的上下动画
    public static void showAnimation(boolean isShow,View linAnima,View imgPulishShoppingCircle,View imgPulishLong,View imgPulishShort,View imgStatusesMore,View imgShadow) {
        int duration = 150;//200
        int translationY1 = (int) Device.getDensity() * 100;
        LogUtil.logE("translationY1", translationY1);
        int translationY2 = 40;//30
        int translationY3 = 24;
        int translationY4 = 50;
        long delayTime = 50;//80
        int degree = 135;

        if (isShow) {
            //开始显示
            imgStatusesMore.setVisibility(View.INVISIBLE);
            imgShadow.setVisibility(View.INVISIBLE);
           // ObjectAnimator.ofFloat(imgStatusesMore, "rotation", 0, degree).setDuration(duration).start();
            //背景颜色动画
            ValueAnimator colorAnim = ObjectAnimator.ofInt(linAnima, "backgroundColor", linAnima.getResources().getColor(R.color.transparent), linAnima.getResources().getColor(R.color.statuses_pulish_window_bg));
            colorAnim.setDuration(duration+delayTime);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.start();

            //设置新的动画.向上动，同时改变可见值
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(imgPulishLong, "translationY", translationY1, -translationY2).setDuration(duration);
            ObjectAnimator anim11 = ObjectAnimator.ofFloat(imgPulishLong, "alpha", 0, 1, 1).setDuration(duration);
            anim1.setInterpolator(new AccelerateDecelerateInterpolator());//add new

            ObjectAnimator anim0 = ObjectAnimator.ofFloat(imgPulishShoppingCircle, "translationY", translationY1, -translationY2).setDuration(duration);
            ObjectAnimator anim10 = ObjectAnimator.ofFloat(imgPulishShoppingCircle, "alpha", 0, 1, 1).setDuration(duration);
            anim0.setInterpolator(new AccelerateDecelerateInterpolator());//add new


            ObjectAnimator animShort1 = ObjectAnimator.ofFloat(imgPulishShort, "translationY", translationY1, -translationY2).setDuration(duration);
            ObjectAnimator animShort11 = ObjectAnimator.ofFloat(imgPulishShort, "alpha", 0, 1, 1).setDuration(duration);
            animShort1.setInterpolator(new AccelerateDecelerateInterpolator());//add new

            AnimatorSet set1 = new AnimatorSet();
            set1.playTogether(anim1, anim0,anim10,anim11, animShort1, animShort11);

            //向下
            ObjectAnimator animLong0 = ObjectAnimator.ofFloat(imgPulishShoppingCircle, "translationY", -translationY2, 0).setDuration(150);//150
            ObjectAnimator animLong2 = ObjectAnimator.ofFloat(imgPulishLong, "translationY", -translationY2, 0).setDuration(150);//150
            ObjectAnimator animShort2 = ObjectAnimator.ofFloat(imgPulishShort, "translationY", -translationY2, 0).setDuration(100);//150
            AnimatorSet set2 = new AnimatorSet();
            set2.playTogether(animLong0,animLong2, animShort2);
            //向上
//            ObjectAnimator animLong3 = ObjectAnimator.ofFloat(imgPulishLong, "translationY", translationY3, 0).setDuration(150);//100
//            ObjectAnimator animShort3 = ObjectAnimator.ofFloat(imgPulishShort, "translationY", translationY3, 0).setDuration(150);
//            AnimatorSet set3 = new AnimatorSet();
//            set3.playTogether(animLong3, animShort3);
            //大动画

            //add new

            animShort1.setStartDelay(delayTime);
            animShort11.setStartDelay(delayTime);
            animShort2.setStartDelay(delayTime);
            //animShort3.setStartDelay(delayTime);

            AnimatorSet setAll = new AnimatorSet();
            setAll.playSequentially(set1, set2);
            setAll.start();


            linAnima.setClickable(true);//展示动画之后
            imgPulishShoppingCircle.setClickable(true);
            imgPulishLong.setClickable(true);
            imgPulishShort.setClickable(true);

        } else {
            imgShadow.setVisibility(View.VISIBLE);
            imgStatusesMore.setVisibility(View.VISIBLE);
            //ObjectAnimator.ofFloat(imgStatusesMore, "rotation", degree, 0).setDuration(duration).start();

            ValueAnimator colorAnim = ObjectAnimator.ofInt(linAnima, "backgroundColor", 0xdd333333, 0x00000000);
            colorAnim.setDuration(duration+delayTime);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.start();
            //向上
            ObjectAnimator animLong0 = ObjectAnimator.ofFloat(imgPulishShoppingCircle, "translationY", 0, -translationY4).setDuration(150);
            ObjectAnimator animLong1 = ObjectAnimator.ofFloat(imgPulishLong, "translationY", 0, -translationY4).setDuration(150);
            ObjectAnimator animShort1 = ObjectAnimator.ofFloat(imgPulishShort, "translationY", 0, -translationY4).setDuration(150);
            AnimatorSet set1 = new AnimatorSet();
            set1.playTogether(animLong0,animShort1,animLong1);


            //向下 改变可见度
            ObjectAnimator anim0 = ObjectAnimator.ofFloat(imgPulishShoppingCircle, "translationY", -translationY3, translationY1).setDuration(duration);
            ObjectAnimator anim01 = ObjectAnimator.ofFloat(imgPulishShoppingCircle, "alpha", 1, 0, 0).setDuration(duration);

            ObjectAnimator anim2 = ObjectAnimator.ofFloat(imgPulishLong, "translationY", -translationY3, translationY1).setDuration(duration);
            ObjectAnimator anim21 = ObjectAnimator.ofFloat(imgPulishLong, "alpha", 1, 0, 0).setDuration(duration);

            ObjectAnimator animShort2 = ObjectAnimator.ofFloat(imgPulishShort, "translationY", -translationY3, translationY1).setDuration(duration);
            ObjectAnimator animShort21 = ObjectAnimator.ofFloat(imgPulishShort, "alpha", 1, 0, 0).setDuration(duration);

            AnimatorSet set2 = new AnimatorSet();
            set1.playTogether(anim0,anim01,animShort2, animShort21,anim2, anim21 );


            //new add
            animLong1.setStartDelay(delayTime);
            anim2.setStartDelay(delayTime);
            anim21.setStartDelay(delayTime);

            AnimatorSet setAll = new AnimatorSet();
            setAll.playSequentially(set2, set1);
            setAll.start();

            linAnima.setClickable(false);
            linAnima.setFocusableInTouchMode(false);
            imgPulishShoppingCircle.setClickable(false);
            imgPulishLong.setClickable(false);
            imgPulishShort.setClickable(false);
        }
    }

}
