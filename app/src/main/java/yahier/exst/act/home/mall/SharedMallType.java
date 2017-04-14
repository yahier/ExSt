package yahier.exst.act.home.mall;

import com.stbl.stbl.util.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedMallType {

	private final static String name = "mall_type";
	private final static String typeKey = "groupId";
	public final static int typeSourceDefault = 0;
	public final static int typeSourceMaotai = 1;

	public static int getType(Context mContext) {
		SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		int tempgroupId = shared.getInt(typeKey, 0);
		LogUtil.logE("SharedIM tempgroupId" + tempgroupId);
		return tempgroupId;
	}

	public static void putType(Context mContext, int type) {
		SharedPreferences shared = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		shared.edit().putInt(typeKey, type).apply();
	}

}
