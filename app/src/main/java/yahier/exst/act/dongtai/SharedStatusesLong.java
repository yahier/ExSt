package yahier.exst.act.dongtai;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStatusesLong {

	/**
	 * 临时存储 长动态内容
	 */

	private final static String name = "tempContent";
	private final static String content = "X";

	public static String getContent(Context mContext) {
		SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		return shared.getString(content, "");
	}

	public static void putContent(Context mContext, String content) {
		SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		shared.edit().putString(content, content).apply();
	}

	public static void clear(Context mContext) {
		SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		shared.edit().clear().apply();

	}
}
