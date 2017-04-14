package yahier.exst.util;

public class TimeUtil {

//	static {
//		//加载库文件
//		System.loadLibrary("stblJni");
//	}
//	private native String getSystemTime(String inputStr);
	
	public static long getSystemTime() {
		return System.currentTimeMillis();
	}
}
