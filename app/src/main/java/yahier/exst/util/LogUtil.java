package yahier.exst.util;

import android.util.Log;

/**
 * log控制类
 *
 * @author lenovo
 */
public class LogUtil {


    public static void logE(Object msg) {
        if (ConfigControl.logable) {
            if (msg == null) {
                Log.e("LogUtil", "对象为null");
            } else {
                Log.e("LogUtil", msg.toString());
            }

        }
    }

    public static void logE(String tag, String msg) {
        if (ConfigControl.logable) {
            if (msg == null) {
                Log.e(tag, "对象为null");
            } else {
                Log.e(tag, msg);
            }

        }
    }

    public static void logE(String tag, int msg) {
        logE(tag, "" + msg);
    }

    public static void logD(String tag, String msg) {
        if (ConfigControl.logable) {
            if (msg == null) {
                Log.d(tag, "对象为null");
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void logLine(Object msg) {
        if (ConfigControl.logable) {
            if (msg == null) {
                Log.e("line", "对象为null");
            } else {
                Log.e("line", getLineNumber(new Exception()) + "\n" + msg.toString());
            }
        }
    }

    /**
     * 得到Exception所在代码的行数
     * 如果没有行信息,返回-1
     */
    public static String getLineNumber(Exception e) {
        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0) return "";
        String className = trace[1].getFileName();
        String methodName = trace[1].getMethodName();
        int line = trace[1].getLineNumber();
        return "class= " + className + ", method= " + methodName + ", line= " + line;
    }
}
