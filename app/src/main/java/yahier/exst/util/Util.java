package yahier.exst.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 转换dp和px的工具类
 *
 * @author lenovo
 */
public class Util {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());

        return (int) scale;
    }

    /**
     * 获取屏幕分辨率：高
     */
    public static int getScreenPixHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    // 功能：字符串半角转换为全角
    // 说明：半角空格为32,全角空格为12288.
    // 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
    // 输入参数：input -- 需要转换的字符串
    // 输出参数：无：
    // 返回值: 转换后的字符串
    public static String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) // 半角空格
            {
                //c[i] = (char) 12288;//不转换空格 by yahier
                continue;
            }

            // 根据实际情况，过滤不需要转换的符号
            if (c[i] == 46) // 半角点号，不转换
                continue;
            if ('0' <= c[i] && c[i] <= '9')
                continue;
            if (c[i] >= 'a' && c[i] <= 'z' || c[i] >= 'A' && c[i] <= 'Z')
                continue;
            if (c[i] == '/' || c[i] == '&' || c[i] == ':' || c[i] == '=' || c[i] == '?' || c[i] =='-') {
                continue;
            }

            if (c[i] > 32 && c[i] < 127) // 其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    // 功能：字符串全角转换为半角
    // 说明：全角空格为12288，半角空格为32
    // 其他字符全角(65281-65374)与半角(33-126)的对应关系是：均相差65248
    // 输入参数：input -- 需要转换的字符串
    // 输出参数：无：
    // 返回值: 转换后的字符串
    public static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) // 全角空格
            {
                c[i] = (char) 32;
                continue;
            }

            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


}
