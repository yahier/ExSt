package yahier.exst.util;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.stbl.stbl.common.MyApplication;

public class Device {
	static private int statusBarHeight = 0;
	static private float density = 0.0f;
	static private int screen_width = 0;
	static private int screen_height = 0;

	public static int getStatusBasrHeight(Activity mActivity) {
		if (statusBarHeight == 0) {
			Rect frame = new Rect();
			mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			statusBarHeight = frame.top;
		}
		System.out.println("statusBarHeight is " + statusBarHeight);
		return statusBarHeight;
	}

	public static float getDensity(Activity mActivity) {
		if (density == 0.0f) {
			DisplayMetrics dm = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			density = dm.density;
		}
		// System.out.println("density is "+density);
		return density;
	}

	public static int getHeight(Activity mActivity) {
		if (screen_height == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screen_height = dm.heightPixels;
		}
		return screen_height;

	}

	public static int getWidth(Activity mActivity) {
		if (screen_width == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screen_width = dm.widthPixels;
		}
		return screen_width;
	}

	public static int getWidth() {
		return MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
	}

	public static int getHeight() {
		return MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
	}

	public static float getDensity() {
		return MyApplication.getContext().getResources().getDisplayMetrics().density;
	}
}
