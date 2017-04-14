package yahier.exst.utils;

import android.widget.TextView;

import com.stbl.stbl.common.MyApplication;

/**
 * Created by meteorshower on 16/3/2.
 */
public class UIUtils {

    /** 获取文本数据 */
    public static String getResString(int strResId){
        return MyApplication.getStblContext().getResources().getString(strResId);
    }

    /** 获取颜色数据 */
    public static int getResColor(int colorResId){
        return MyApplication.getStblContext().getResources().getColor(colorResId);
    }

    /** 获取控件文本数据 */
    public static String getViewContent(TextView tv){
        return tv.getText().toString().trim();
    }
}
