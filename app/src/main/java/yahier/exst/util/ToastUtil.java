package yahier.exst.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.widget.AppToast;

public class ToastUtil {
	static Toast toast;
	static TextView textView;
	
	private static Toast createToast(Context context, String text) {
		if (context == null) context = MyApplication.getContext();
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		try {
			View view = LayoutInflater.from(context).inflate(R.layout.toast, null);
			TextView textView = (TextView) view.findViewById(R.id.text);
			toast.setView(view);
			textView.setText(text);
		}catch(InflateException e){
			//频繁调用会导致这个错误发生
			e.printStackTrace();
		}
		return toast;
	}

	public static void showToast(Context context, String text) {
//		if (toast != null) {
//			toast.cancel();
//			toast = null;
//		}
		if (TextUtils.isEmpty(text)){
			return;
		}
//		toast = createToast(context, text);
//		toast.show();
		AppToast.show(text);
	}

	public static void showToast(int textId) {
		AppToast.show(textId);
	}

	public static void showToast(String text) {
//		if (toast != null) {
//			toast.cancel();
//			toast = null;
//		}
		if (TextUtils.isEmpty(text)){
			return;
		}
//		toast = createToast(MyApplication.getContext(), text);
//		toast.show();
		AppToast.show(text);
	}

	public static void showLong(String text) {
//		if (toast != null) {
//			toast.cancel();
//			toast = null;
//		}
		if (TextUtils.isEmpty(text)){
			return;
		}
//		toast = createToast(MyApplication.getContext(), text);
//		toast.setDuration(Toast.LENGTH_LONG);
//		toast.show();
		AppToast.showLong(text);
	}

}
