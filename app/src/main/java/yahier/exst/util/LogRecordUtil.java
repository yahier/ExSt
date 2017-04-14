package yahier.exst.util;

import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by lenovo on 2016/4/8.
 * 记录log到本地文件。
 */
public class LogRecordUtil {
    static RandomAccessFile access;

    static {
        String fileName = DateUtil.getTodayDate() + ".txt";
        File file = new File(Config.localFilePathLog, fileName);
        try {
            access = new RandomAccessFile(file, "rw");
        } catch (Exception e) {

        }
    }

    /**
     * 测试发现,有的数据有重复。可能的原因包括app有两个进程。
     */
    public static void init() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line).append("\n");
            }
            write(log.toString());
            Runtime.getRuntime().exec("logcat -c");//清除日志.
        } catch (IOException e) {
            LogUtil.logE("LogRecordUtil init", e.getLocalizedMessage());
        }
    }


    private static void write(String test) {
        if (access == null) return;
        try {
            access.write(test.getBytes("UTF-8"));
        } catch (Exception e) {

        }

    }


}
