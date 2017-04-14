package yahier.exst.ui.DirectScreen.service.floating;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.stbl.stbl.R;

/**
 * 自定义悬浮球
 * 可自由拖动，松手的时候悬浮球靠边，靠左边还是右边取决于松手的瞬间悬浮球位于左半屏幕还是右半屏幕；
 * 靠边后如果悬浮球没有被触摸，3000ms后变暗，如果没有被触摸，600ms后悬浮球隐藏一部分
 * @author yukai 2015-09-14
 */
public class FloatView extends ImageView{
	
	private float mX;
	private float mTouchX;
	private float mTouchY;
	private float x;
	private float y;
	private float xStart;
	private NoDuplicateClickListener mClickListener;
	//保存当前是否为移动模式
	private boolean  isMove = false;
	//保存悬浮球在左边还是右边
	private boolean  isRight = false;
	//是否触摸悬浮窗
	private boolean isTouch = false;
	//第一个定时器是否取消
	private boolean isCancel;
	//是否靠边隐藏
	private boolean isHide;
	//第二个定时器是否取消
	private boolean isSecondCancel;
	//手指是否离开悬浮球
	private boolean isUp;
	private boolean canClick = true;
	//默认的悬浮球
	private int defaultResource;
	//变暗的悬浮球
	private int darkResource ;
	//变暗而且只有右半部分的悬浮球
	private int leftResource ;
	//变暗而且只有左半部分的悬浮球
	private int rightResource;
	
	private static final int KEEP_TO_SIDE = 0;
	private static final int HIDE = 1;
	private static final int MOVE_SLOWLY = 2;
	
	private Timer timer;
	private TimerTask timerTask;
	private Timer secondTimer;
	private TimerTask secondTask;
	
	private View image;
	private int count;
	//松开手悬浮球移动的频率
	private static final int FREQUENCY = 16;
	//松开手后悬浮球移动的步数
	private int step;
	
	private int j ;
	private float distance;
	private int screenWidth;
	private int statusBarHeight;

	private Point mPoint;

	private OnUpdateViewLayoutListener listener;
	
	public FloatView(Context context){
		super(context);
		init();
	}
	
	public FloatView(Context context, AttributeSet att){
		super(context, att);
		init();
	}
	
	public FloatView(Context context, AttributeSet att, int dis){
		super(context, att, dis);
		init();
	}
		
	private void init(){
		image = this;
		
		defaultResource = R.drawable.float_light;
	    darkResource = R.drawable.float_dark;
		leftResource = R.drawable.float_dark_left;
		rightResource = R.drawable.float_dark_right;
		
		isMove = false;
		
		if(getContext().getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
			//横屏，规定移动的步数最多为20步
			step = 20;
		}else if(getContext().getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
			//竖屏，规定移动的步数最多为12步
			step = 12;
		}
		
		statusBarHeight = getStatusHeight(getContext());
		screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		setImageResource(defaultResource);
		startTimerCount();
		mPoint = new Point();
		mPoint.x = 0;
		mPoint.y = 500;
	}

	private Handler handler = new Handler(Looper.getMainLooper()){
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case KEEP_TO_SIDE:
				setImageResource(darkResource);
				cancelTimerCount();
				startSecondTimerCount();
				break;
			case HIDE:
				cancelSecondTimerCount();
				if(isRight){
					isHide = true;
					setImageResource(rightResource);
				} else {
					setImageResource(leftResource);
				}
				break;
			case MOVE_SLOWLY:
				if(j==count+1){
					canClick = true;
				}
				//根据悬浮球离最近的屏幕边缘的距离来定移动的步数
				count = (int) (2 * step * Math.abs(distance) / screenWidth); //count/step = distance/(screenWidth/2)
				if(j <= count){
					mPoint.x = (int) (xStart-j * distance / count);
					if(listener != null)
						listener.updateViewLayout(mPoint);
					j++;
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							handler.sendEmptyMessage(MOVE_SLOWLY);
						}
					}, FREQUENCY);
				}
				break;
			}
		};
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isTouch = true;
		isUp = false;
		xStart = 0;
		//System.out.println("statusBarHeight:"+statusBarHeight);
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY() ; // statusBarHeight是系统状态栏的高度
		Log.i("tag", "currX" + x + "====currY" + y);

//		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
			// 获取相对View的坐标，即以此View左上角为原点
			isMove = false;
			mTouchX = event.getX();
			mTouchY = event.getY();
			Log.i("tag", "startX" + mTouchX + "====startY"+ mTouchY);
			cancelTimerCount();
			cancelSecondTimerCount();
			break;
		case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
			int xMove = Math.abs((int) (event.getX() - mTouchX));
			int yMove = Math.abs((int) (event.getY() - mTouchY));
			if(xMove > 5 || yMove > 5) {
				//x轴或y轴方向的移动距离大于5个像素，视为拖动，否则视为点击
				isMove = true;
				setImageResource(defaultResource);
				updateViewPosition();
			}
			break;
		case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
			isTouch = false;
			float halfScreen = screenWidth/2;
			if(isMove){
				isUp = true;
				mX = mTouchX;
				isMove = false;
				if(x <= halfScreen) {
					xStart = x-mTouchX;
					x = 0;
					isRight = false;
				} else {
					xStart = x;
					x = screenWidth + mTouchX + image.getWidth();//为了保证悬浮球靠边隐藏，而且在极限情况下（横屏）不会从屏幕右边突然跳到屏幕左边
					isRight = true;
				}
				updateViewPosition();
				mPoint.x = (int)x;
				mPoint.y = (int)(y - mTouchY);
				startTimerCount();
				
			}else {
				setImageResource(defaultResource);
				if(mClickListener!=null&&canClick) {
					mClickListener.onClick(this);
				}
			}
			mTouchX = mTouchY = 0;
			break;
		}
		return true;
	}
	
	public boolean isHide() {
		return isHide;
	}
	
	public void setNoDuplicateClickListener(NoDuplicateClickListener l) {
		this.mClickListener = l;
	}
	
	private void updateViewPosition() {
		if(isUp){
			canClick = false;
			//松开手后，悬浮球靠边速度太快，做了个延时，使靠边这个过程更平滑
			distance = xStart- x;
			j = 0;
			mPoint.y = (int) (y - mTouchY);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
						handler.sendEmptyMessage(MOVE_SLOWLY);
					}
			}, FREQUENCY);
			
		}else{
			mPoint.x = (int)(x - mTouchX);
			mPoint.y = (int)(y - mTouchY);
			if(listener != null)
				listener.updateViewLayout(mPoint);
		}
	}

	public void setOnUpdateViewLayoutListener(OnUpdateViewLayoutListener listener){
		this.listener = listener;
	}

	public interface OnUpdateViewLayoutListener{

		public void updateViewLayout(Point point);
	}
	
	//开启第一个定时器
	public void startTimerCount(){
		isCancel = false;
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if(!isTouch&&!isCancel){
					handler.sendEmptyMessage(KEEP_TO_SIDE);
				}
			}
		};
		timer.schedule(timerTask, 3000);
	}
	
	//关闭第一个定时器
	public void cancelTimerCount(){
		isCancel = true;
		if(timer!=null){
			timer.cancel();
			timer =null;
		}
		if(timerTask!=null){
			timerTask.cancel();
			timerTask = null;
		}
	}
	
	//开启第二个定时器
	public void startSecondTimerCount(){
		isSecondCancel = false;
		secondTimer = new Timer();
		secondTask = new TimerTask(){
			@Override
			public void run() {
				if(!isSecondCancel){
					handler.sendEmptyMessage(HIDE);
				}
			}
		};
		secondTimer.schedule(secondTask, 600);
	}
	
	//关闭第二个定时器
	public void cancelSecondTimerCount(){
		isSecondCancel = true;
		if(secondTimer!=null){
			secondTimer.cancel();
			secondTimer = null;
		}
		if(secondTask!=null){
			secondTask.cancel();
			secondTask = null;
		}
	}
	
	/**
     * 获得状态栏的高度
     * @param context
     * @return
     */  
    public static int getStatusHeight(Context context)  {  
        int statusHeight = -1;  
        try{  
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");  
            Object object = clazz.newInstance();  
            int height = Integer.parseInt(clazz.getField("status_bar_height")  
                    .get(object).toString());  
            statusHeight = context.getResources().getDimensionPixelSize(height);  
        } catch (Exception e){  
            e.printStackTrace();  
        }  
        return statusHeight;  
    }  
}
