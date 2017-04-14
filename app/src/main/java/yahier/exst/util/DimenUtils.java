package yahier.exst.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.stbl.stbl.common.MyApplication;

/**
 * Created by lr on 2015/12/9.
 */
public class DimenUtils {

	/**
	 * dp转px
	 */
	public static int dp2px(float dpVal) {
		final Context context = MyApplication.getContext();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * sp转px
	 */
	public static int sp2px(float spVal) {
		final Context context = MyApplication.getContext();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * px转dp
	 */
	public static float px2dp(float pxVal) {
		final Context context = MyApplication.getContext();
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/**
	 * px转sp
	 */
	public static float px2sp(float pxVal) {
		final Context context = MyApplication.getContext();
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

	/**
	 * 屏幕宽度
	 */
	public static int getScreenWidth() {
		WindowManager wm = (WindowManager) MyApplication.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth();
	}

	/**
	 * 屏幕高度
	 */
	public static int getScreenHeight() {
		WindowManager wm = (WindowManager) MyApplication.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getHeight();
	}

}
