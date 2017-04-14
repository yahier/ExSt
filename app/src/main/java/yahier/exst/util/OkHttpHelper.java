package yahier.exst.util;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.model.TaskError;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpHelper {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType IMAGE = MediaType.parse("image/jpeg");
    public static final MediaType STREAM = MediaType.parse("application/octet-stream");

    private static final long TIMEOUT = 30000;

    private static volatile OkHttpHelper sInstance;

    public static OkHttpHelper getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpHelper.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpHelper();
                }
            }
        }
        return sInstance;
    }

    private OkHttpClient mClient;

    private OkHttpHelper() {
        mClient = new OkHttpClient();
        mClient.setConnectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        mClient.setReadTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        mClient.setWriteTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private String get(String url) throws IOException {
        ParamsJson.parse(url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token",
                        SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .build();

        Response response = mClient.newCall(request).execute();
        String str = response.body().string();
        LogUtil.logE("url=" + url + ",response=" + str);
        return str;
    }

    private String post(String url, String json) throws IOException {
        ParamsJson.parse(url, json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token",
                        SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(body).build();
        Response response = mClient.newCall(request).execute();
        String str = response.body().string();
        LogUtil.logE("url=" + url + ",response=" + str);
        return str;
    }

    public Response getResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token",
                        SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .build();
        return mClient.newCall(request).execute();
    }

    public HttpResponse post(String method, JSONObject o) throws IOException {
        LogUtil.logE("LogUtil",o != null ? o.toString() : "");
        return post(Config.hostMain, method, o);
    }

    public HttpResponse post(String host, String method, JSONObject o) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = host + method;
        String json = o.toString();
        ParamsJson.parse(url, json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url = " + url + ", response = " + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("url = " + url + ", response = " + res);
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        if (obj == null) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (obj.getIntValue("issuccess") != 1) {
            JSONObject err = obj.getJSONObject("err");
            int errcode = err.getIntValue("errcode");
            String msg = err.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("result")) {
                result = obj.getString("result");
            }
        }
        return new HttpResponse(error, result);
    }


    public HttpResponse postRedpacket(String method, JSONObject o) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostMainRedPacket + method;
        String json = o.toString();
        ParamsJson.parse(url, json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url = " + url + ", response = " + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("url = " + url + ", response = " + res);
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        if (obj == null) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (obj.getIntValue("issuccess") != 1) {
            JSONObject err = obj.getJSONObject("err");
            int errcode = err.getIntValue("errcode");
            String msg = err.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("result")) {
                result = obj.getString("result");
            }
        }
        return new HttpResponse(error, result);
    }

    /**
     * 通用的红包云账户请求
     *
     * @param method
     * @param o
     * @return
     * @throws IOException
     */
    public HttpResponse postYunZhangHu(String method, JSONObject o) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostMainYunZhangHu + method;
        String json = o.toString();
        ParamsJson.parse(url, json);
        RequestBody body = RequestBody.create(JSON, json);
        // .addHeader("x-auth-token", (String) SharedPrefUtils.getFromPublicFile(KEY.yunZhangHuToken,""))
        Request request = new Request.Builder()
                .url(url)
                .addHeader("device-id", SharedDevice.getPhoneIEME())
                .addHeader("request-id", System.currentTimeMillis() + "")
                .addHeader("version", "3.1.2")
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url = " + url + ", response = " + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("exception url = " + url + ", response = " + res);
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        if (obj == null) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        //如果不等于正确码
        if (!obj.getString("code").equals(Config.YunZhangHuSeccussCode)) {
            int errcode = Integer.parseInt(obj.getString("code"));
            String msg = obj.getString("message");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("data")) {
                result = res;
            }
        }
        return new HttpResponse(error, result);
    }

    public static boolean checkError(int errorCode) {
        //需要重新登录
        for (int i = 0; i < ServerError.errorTokenCodeArrays.length; i++) {
            if (errorCode == ServerError.errorTokenCodeArrays[i]) {
                return true;
            }
        }
        // 跳转充值界面
        for (int i = 0; i < ServerError.errorMoneyArrays.length; i++) {
            if (errorCode == ServerError.errorMoneyArrays[i]) {
                return true;
            }
        }
        return false;
    }

    public HttpResponse uploadImage(String method, String path) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostUploadWeiboImg + method;
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("userid", SharedToken.getUserId(MyApplication.getContext()))
                .addFormDataPart("pic", FileUtils.getPhotoName(path), RequestBody.create(IMAGE, new File(path)))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(requestBody)
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url=" + url + ", response=" + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (obj.getIntValue("issuccess") != 1) {
            JSONObject err = obj.getJSONObject("err");
            int errcode = err.getIntValue("errcode");
            String msg = err.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("result")) {
                result = obj.getString("result");
            }
        }
        return new HttpResponse(error, result);
    }

    public HttpResponse uploadFile(String method, String path) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostUploadWeiboImg + method;
        File file = new File(path);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("userid", SharedToken.getUserId(MyApplication.getContext()))
                .addFormDataPart("pic", FileUtils.getPhotoName(path), RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .addHeader("Content-Length", "0")
                .post(requestBody)
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url=" + url + ", response=" + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (obj.getIntValue("issuccess") != 1) {
            JSONObject err = obj.getJSONObject("err");
            int errcode = err.getIntValue("errcode");
            String msg = err.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("result")) {
                result = obj.getString("result");
            }
        }
        return new HttpResponse(error, result);
    }

    public HttpResponse uploadImage(String method, JSONObject json, String path) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostUploadWeiboImg + method;
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        for (String key : json.keySet()) {
            builder.addFormDataPart(key, json.getString(key));
        }
        builder.addFormDataPart("pic", FileUtils.getPhotoName(path), RequestBody.create(IMAGE, new File(path)));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(builder.build())
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url=" + url + ", response=" + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (obj.getIntValue("issuccess") != 1) {
            JSONObject err = obj.getJSONObject("err");
            int errcode = err.getIntValue("errcode");
            String msg = err.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("result")) {
                result = obj.getString("result");
            }
        }
        return new HttpResponse(error, result);
    }

    public HttpResponse uploadImage(String method, JSONObject json, File file) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostUploadWeiboImg + method;
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        for (String key : json.keySet()) {
            builder.addFormDataPart(key, json.getString(key));
        }
        builder.addFormDataPart("pic", file.getName(), RequestBody.create(IMAGE, file));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(builder.build())
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url=" + url + ", response=" + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (obj.getIntValue("issuccess") != 1) {
            JSONObject err = obj.getJSONObject("err");
            int errcode = err.getIntValue("errcode");
            String msg = err.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("result")) {
                result = obj.getString("result");
            }
        }
        return new HttpResponse(error, result);
    }

    public HttpResponse redPacketGet(String method) throws IOException {
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_network_unavailable)), "");
        }
        String url = Config.hostMainYunZhangHu + method;
        ParamsJson.parse(url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("device-id", SharedDevice.getPhoneIEME())
                .addHeader("x-auth-token", (String) SharedPrefUtils.getFromPublicFile(KEY.yunZhangHuToken, ""))
                .addHeader("version", "android_rp_3.1.0")
                .addHeader("request-id", System.currentTimeMillis() + "")
                .build();
        Response response = mClient.newCall(request).execute();
        String res = response.body().string();
        LogUtil.logE("url = " + url + ", response = " + res);

        JSONObject obj = null;
        try {
            obj = JSONObject.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE("url = " + url + ", response = " + res);
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        if (obj == null) {
            return new HttpResponse(new TaskError(Res.getString(R.string.me_data_error)), "");
        }

        TaskError error = null;
        if (!obj.getString("code").equals(Config.YunZhangHuSeccussCode)) {
            String errcode = obj.getString("code");
            String msg = obj.getString("msg");
            error = new TaskError(errcode, msg);
        }
        String result = "";
        if (error == null) {
            if (obj.containsKey("data")) {
                result = obj.getString("data");
            }
        }
        return new HttpResponse(error, result);
    }


}
