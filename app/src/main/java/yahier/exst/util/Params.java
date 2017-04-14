package yahier.exst.util;

import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONObject;

import java.io.File;

/**
 * post请求参数格式
 *
 * @author lenovo
 */
public class Params {

    public JSONObject json = new JSONObject();
    public String key;
    public File file;

    public void put(String key, int value) {
        json.put(key, String.valueOf(value));
    }

    public void put(String key, long value) {
        json.put(key, String.valueOf(value));
    }

    public void put(String key, float value) {
        json.put(key, String.valueOf(value));
    }

    public void put(String key, String value) {
        json.put(key, value);
    }

    public void put(String key, Bitmap bitmap) {
        this.key = key;
        File temp = FileUtils.saveTempBitmap(bitmap);
        if (temp != null) {
            file = temp;
        }
    }

    public void put(String key, File file) {
        this.key = key;
        this.file = file;
    }
}

