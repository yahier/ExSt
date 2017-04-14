/**
 * 
 */
package yahier.exst.widget.dongtai;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.utils.NumUtils;

/**
 * @Title: SuspendView.java 
 * @Package com.stbl.stbl.widget.dongtai 
 * @Description: TODO(动态界面 悬浮按钮) 
 * @author 李全青   
 * @date 2016-1-12 下午2:20:01 
 * @version V1.0   
 */
public class SuspendView extends LinearLayout{

	private LinearLayout layout_menu;
	private TextView tview_title;
	private TextView tvCount;
	private ImageView iview_icon;
	private boolean isAnimation = false;
	private int layout_menu_width;
	private int iview_icon_width_height;
	private final int durationTime = 200;
	private ValueAnimator showLayoutAnimator,closeLayoutAnimator;//布局宽度变化动画
	private RotateAnimation showIconAnimation,closeIconAnimation;//图标旋转动画
	private TranslateAnimation showDownAnimation,closeUpAnimation;//布局下走上走动画
	
	public SuspendView(Context context) {
		super(context);
		init(context);
	}

	public SuspendView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SuspendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		LayoutInflater.from(context).inflate(R.layout.layout_dongtai_xuanfu_menu, this);
		layout_menu = (LinearLayout)findViewById(R.id.dongtai_xuanfu_menu_layout);
		tview_title = (TextView)findViewById(R.id.dongtai_xuanfu_menu_tview_title);
		tvCount = (TextView)findViewById(R.id.tvCount);
		iview_icon = (ImageView)findViewById(R.id.dongtai_xuanfu_menu_icon);
		
		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        layout_menu.measure(w, h);
        layout_menu_width = layout_menu.getMeasuredWidth();
        iview_icon.measure(w, h);
        iview_icon_width_height = iview_icon.getMeasuredHeight();
        Log.e("SuspendView", "layout_menu_width===="+layout_menu_width);
        Log.e("SuspendView", "iview_icon_width_height===="+iview_icon_width_height);
        
		LayoutParams flParams = (LayoutParams) layout_menu.getLayoutParams();
		flParams.width = iview_icon_width_height;
		flParams.height = iview_icon_width_height;
		layout_menu.requestLayout();
		
        float r = iview_icon_width_height/2.0f;
        float[] outerRadii = {r, r, r, r, r, r, r, r};//外矩形 左上、右上、右下、左下 圆角半径  
        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null); 
        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);  
        drawable.getPaint().setColor(Color.parseColor("#fcd532"));  
        drawable.getPaint().setAntiAlias(true);  
        drawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);//描边  
        layout_menu.setBackgroundDrawable(drawable);
        layout_menu.setMinimumWidth(iview_icon_width_height);
		
		LayoutParams ivParams = (LayoutParams)iview_icon.getLayoutParams();
		ivParams.width = iview_icon_width_height;
		ivParams.height = iview_icon_width_height;
		iview_icon.requestLayout();
		
		getInitAnimations();
	}
	
	public SuspendView setTitle(CharSequence text){
		if (tview_title!=null) {
			tview_title.setText(text);
		}
		return this;
	}
	
	public SuspendView setInfo(CharSequence text){
		if (tvCount!=null) {
			tvCount.setText(text);
		}
		
		return this;
	}
	
	
	/**
	 * 展开悬浮按钮动画
	 * */
	public final void showMenu(){
		if (isAnimation||getVisibility() == View.VISIBLE) {
			return ;
		}
		setVisibility(View.VISIBLE);
		LayoutParams menuParams = (LayoutParams) layout_menu.getLayoutParams();
		menuParams.width = iview_icon_width_height;
		layout_menu.requestLayout();
		startAnimation(showDownAnimation);
		iview_icon.startAnimation(showIconAnimation);
	}
	
	/**
	 * 关闭悬浮按钮动画
	 * */
	public final void closeMenu(){
		if (isAnimation||getVisibility() == View.GONE) {
			return ;
		}
		closeLayoutAnimator.start();
	}
	

	private void getInitAnimations(){
		getShowIconAnimation();
		getCloseIconAnimation();
		getShowLayoutAnimator();
		getShowDownAnimation();
		getCloseUpAnimation();
		getCloseLayoutAnimator();
	}
	
	/**
	 * 获取图标展示动画
	 * */
	private void getShowIconAnimation(){
		showIconAnimation = new RotateAnimation(
				-180, 
				0, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		showIconAnimation.setDuration(durationTime);
		showIconAnimation.setFillAfter(true);
	}
	
	/**
	 * 获取图标关闭动画
	 * */
	private void getCloseIconAnimation(){
		closeIconAnimation = new RotateAnimation(
				0, 
				-180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		closeIconAnimation.setDuration(durationTime);
		closeIconAnimation.setFillAfter(true);
	}
	
	/**
	 * 获取布局展开动画
	 * */
	private void getShowLayoutAnimator(){
		showLayoutAnimator = ValueAnimator.ofInt(iview_icon_width_height,layout_menu_width);
		showLayoutAnimator.setDuration(durationTime);
		showLayoutAnimator.setInterpolator(new LinearInterpolator());
		showLayoutAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				layout_menu.getLayoutParams().width = NumUtils.getObjToInt(animation.getAnimatedValue());
				layout_menu.requestLayout();
			}
		});
		showLayoutAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				isAnimation = false;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				isAnimation = false;
			}
		});
	}
	
	/**
	 * 获取下走动画
	 * */
	private void getShowDownAnimation(){
		showDownAnimation = new TranslateAnimation(0, 0, -iview_icon_width_height, 0);
		showDownAnimation.setDuration(durationTime);
		showDownAnimation.setInterpolator(new BounceInterpolator()); 
		showDownAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				isAnimation = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				showLayoutAnimator.start();
			}
		});
	}
	
	/**
	 * 获取上走动画
	 * */
	public void getCloseUpAnimation(){
		closeUpAnimation = new TranslateAnimation(0, 0, 0, -iview_icon_width_height);
		closeUpAnimation.setDuration(durationTime);
		closeUpAnimation.setInterpolator(new LinearInterpolator()); 
		closeUpAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				isAnimation = false;
				setVisibility(View.GONE);
			}
			
		});
	}
	
	/**
	 * 获取关闭布局动画
	 * */
	private void getCloseLayoutAnimator(){
		closeLayoutAnimator = ValueAnimator.ofInt(layout_menu_width,iview_icon_width_height);
		closeLayoutAnimator.setDuration(durationTime);
		closeLayoutAnimator.setInterpolator(new LinearInterpolator());
		closeLayoutAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				layout_menu.getLayoutParams().width = NumUtils.getObjToInt(animation.getAnimatedValue());
				layout_menu.requestLayout();
			}
		});
		closeLayoutAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				isAnimation = true;
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				
				iview_icon.startAnimation(closeIconAnimation);
				
				LayoutParams menuParams = (LayoutParams) layout_menu.getLayoutParams();
				menuParams.width = iview_icon_width_height;
				layout_menu.requestLayout();
				startAnimation(closeUpAnimation);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
			}
		});
	}
}
