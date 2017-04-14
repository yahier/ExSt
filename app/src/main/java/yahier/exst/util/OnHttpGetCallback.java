package yahier.exst.util;

import android.graphics.Bitmap;

public interface OnHttpGetCallback {
	
	public void onHttpGetImgVerify(String methodName, Bitmap bm, String verifyCode);
}
