package yahier.exst.util;

import android.util.Log;

/**
 * Created by yahier on 17/3/13.
 */

public class CacheUtil {


    /**
     * 输出当前内存使用的情况
     * @param methodName
     */
    public static void logCache(String methodName) {
        Runtime runtime = Runtime.getRuntime();
        float totalMemory = runtime.totalMemory() / (1024.0f * 1024.0f);
        String value = StringUtil.get2ScaleString(totalMemory);
        Log.e("logCache-" + methodName, "totalMemory:" + value);

    }
}
