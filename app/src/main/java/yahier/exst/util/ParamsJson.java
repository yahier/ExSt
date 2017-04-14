package yahier.exst.util;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by lenovo on 2016/4/8.
 * 记录请求的方法和参数
 */
public class ParamsJson {
    static RandomAccessFile access;

    static {
        File file = new File(Config.localFilePath, "data.txt");
        try {
            access = new RandomAccessFile(file, "rw");
        } catch (Exception e) {

        }
    }

    public static void parse(String methondName, Params params) {
        StringBuffer sb = new StringBuffer();
        sb.append(methondName + "\t");
        if (params != null) {
            JSONObject json = params.json;
            sb.append(json.toString());
        }
        write(sb.toString());
    }

    public static void parse(String methondName, String json) {
        methondName = methondName.replace(Config.hostMain, "");
        StringBuffer sb = new StringBuffer();
        sb.append(methondName + "\t");
        if (json != null) {
            sb.append(json);
        }
        write(sb.toString());
    }


    public static void parse(String json) {
        json = json.replace(Config.hostMain, "");
        StringBuffer sb = new StringBuffer();
        if (json != null) {
            sb.append(json);
        }
        write(sb.toString());
    }

    static void write(String test) {
        if (!ConfigControl.logable) return;
        test = test + "\n";
        if (access == null) return;
        try {
            access.write(test.getBytes("UTF-8"));
        } catch (Exception e) {

        }

    }


}
