package yahier.exst.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stbl.stbl.R;
import com.stbl.stbl.api.http.AsyncOkHttpRequest;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ServerError;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/*请求后台的实体方法*/

public class HttpEntity extends AsyncOkHttpRequest {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/jpeg");

    final static String TAG = "HttpEntity";
    public Context mContext;
    private ProgressDialog loadingDialog;
    final int timeOut = 30 * 1000;

    private FinalHttpCallback httpListener;
    private StringBuffer url = new StringBuffer();

    public HttpEntity(Context mContext) {
        init(mContext, true);
    }

    public HttpEntity(Context mContext, boolean ifShowLoading) {
        init(mContext, ifShowLoading);
    }

    private void init(Context mContext, boolean ifShowLoading) {
        this.mContext = mContext;
        // if (ifShowLoading) {
        // if (null == loadingDialog || loadingDialog.getContext() != mContext)
        // {
        // this.loadingDialog = LoadingView.createDefLoading(mContext);
        // loadingDialog.setCanceledOnTouchOutside(false);
        // loadingDialog.setCancelable(true);
        // }
        // }
    }


    private void showLoading() {
        if (null == loadingDialog)
            return;
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void hideLoading() {
        if (null != loadingDialog)
            loadingDialog.dismiss();
    }


    public static Bitmap getBitmap(String pic) {
        Bitmap bitmap = null;
        bitmap = HttpGetConnection.getImageByURI(pic);
        if (bitmap == null) {
            // TODO 由于超时或pic不存在等造成
        } else {

        }
        return bitmap;
    }


    int triedTimes = 0;
    final int tryMaxTimes = 2;

    BaseItem getNoNetBaseItem() {
        BaseItem item = new BaseItem();
        item.setIssuccess(BaseItem.errorNoTaostTag);
        ServerError error = new ServerError();
//        error.setMsg("请求失败");
        error.setMsg("");
        item.setErr(error);
        return item;
    }


    //红包新域名的请求
    public void commonRedpacketPostData(String methodName, Params params, FinalHttpCallback m) {
        ParamsJson.parse(methodName, params);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            httpListener.parse(methodName, str);
            return;
        }
        try {
            showLoading();
            url.setLength(0);
            url.append(Config.hostMainRedPacket);
            url.append(methodName);

            if (params == null) {
                params = new Params();
            }
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params.json.toString());

            Headers.Builder headers = new Headers.Builder();
            headers.add("x-stbl-token", SharedToken.getToken(mContext));
            headers.add("User-Agent", SharedDevice.toDeviceString());
            headers.add("x-stbl-lang", AppUtils.getCurrLang());
            headers.add("x-stbl-guid", AppUtils.getGUID());

            registerPostRequest(methodName, headers.build(), body);
        } catch(Exception e) {
            e.printStackTrace();
            LogUtil.logE("commonPostData e", e.getLocalizedMessage());
        }
    }


    // 统一获取数据的方式
    public void commonPostData(String methodName, Params params, FinalHttpCallback m) {
        ParamsJson.parse(methodName, params);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            httpListener.parse(methodName, str);
            return;
        }
        try {
            showLoading();
            url.setLength(0);
            url.append(Config.hostMain);
            url.append(methodName);

            if (params == null) {
                params = new Params();
            }
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params.json.toString());

            Headers.Builder headers = new Headers.Builder();
            headers.add("x-stbl-token", SharedToken.getToken(mContext));
            headers.add("User-Agent", SharedDevice.toDeviceString());
            headers.add("x-stbl-lang", AppUtils.getCurrLang());
            headers.add("x-stbl-guid", AppUtils.getGUID());

            registerPostRequest(methodName, headers.build(), body);
        } catch(Exception e) {
            e.printStackTrace();
            LogUtil.logE("commonPostData e", e.getLocalizedMessage());
        }
    }


    //okhttp中也有类似请求 postYunZhangHu
    public void commonYunZhangHuPostData(String methodName, Params params, FinalHttpCallback m) {
        ParamsJson.parse(methodName, params);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            httpListener.parse(methodName, str);
            return;
        }
        try {
            showLoading();
            url.setLength(0);
            url.append(Config.hostMainYunZhangHu);
            url.append(methodName);

            if (params == null) {
                params = new Params();
            }
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params.json.toString());

            Headers.Builder headers = new Headers.Builder();
            headers.add("device-id", SharedDevice.getPhoneIEME());
            headers.add("x-auth-token", (String) SharedPrefUtils.getFromPublicFile(KEY.yunZhangHuToken, ""));
            headers.add("version", "3.1.2");
            headers.add("request-id", System.currentTimeMillis() + "");

            registerPostRequest(methodName, headers.build(), body);
        } catch(Exception e) {
            e.printStackTrace();
            LogUtil.logE("commonPostData e", e.getLocalizedMessage());
        }
    }


    /**
     * 云账户通用Get请求
     *
     * @param methodName
     * @param params
     * @param m
     */
    public void commonYunZhangHuGetData(String methodName, Params params, FinalHttpCallback m) {
        ParamsJson.parse(methodName, params);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            httpListener.parse(methodName, str);
            return;
        }
        try {
            showLoading();

            String url = Config.hostMainYunZhangHu + methodName + "?" + getGetRequestParams(params);
            LogUtil.logE("get url", url);
            Headers.Builder headers = new Headers.Builder();
            headers.add("device-id", SharedDevice.getPhoneIEME());
            headers.add("x-auth-token", (String) SharedPrefUtils.getFromPublicFile(KEY.yunZhangHuToken, ""));
            headers.add("version", "android_rp_3.1.0");
            headers.add("request-id", System.currentTimeMillis() + "");

            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers.build()).build();

            registerGetRequest(methodName, request);
        } catch(Exception e) {
            e.printStackTrace();
            LogUtil.logE("commonPostData e", e.getLocalizedMessage());
        }
    }


    /**
     * 获取get请求的参数。不包含前面的?
     *
     * @param params
     * @return
     */
    private String getGetRequestParams(Params params) {
        if (params == null) return "";
        StringBuffer sb = new StringBuffer();
        JSONObject json = params.json;
        Iterator<String> keys = json.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            sb.append(key + "=" + json.getString(key));
            sb.append("&");
        }
        if (!json.toString().equals("")) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }


    /**
     * 处理异常之token异常.返回true.表示已经处理。返回false 表示没有处理。重新登录是返回false
     *
     * @param t
     */
    private boolean dealTokenCode(String t) {
        BaseItem item = JSONHelper.getObject(t, BaseItem.class);
        if (item == null) {
            return true;
        }
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getErr() != null) {
                int errorCode = item.getErr().getErrcode();
                String msg = item.getErr().getMsg();
                return ServerError.checkError(mContext, errorCode, msg);
            }
            return true;
        }
        return true;
    }

    public void commonPostJson(final String methodName, final String json, FinalHttpCallback m) {
        ParamsJson.parse(methodName, json);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            httpListener.parse(methodName, str);
            return;
        }
        showLoading();

        url.setLength(0);
        url.append(Config.hostMain);
        url.append(methodName);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Headers.Builder headers = new Headers.Builder();
        headers.add("x-stbl-token", SharedToken.getToken(mContext));
        headers.add("User-Agent", SharedDevice.toDeviceString());
        headers.add("x-stbl-lang", AppUtils.getCurrLang());
        headers.add("x-stbl-guid", AppUtils.getGUID());
        registerPostRequest(methodName, headers.build(), requestBody);
    }


    // 发图
    public void commonPostImg(final String methodName, Params params, final FinalHttpCallback m) {
        ParamsJson.parse(methodName, params);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            m.parse(methodName, str);
            return;
        }

        showLoading();
        url.setLength(0);
        url.append(Config.hostUploadWeiboImg);
        url.append(methodName);

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        JSONObject json = params.json;
        for (String key : json.keySet()) {
            builder.addFormDataPart(key, json.getString(key));
        }
        if (params.file != null) {
            builder.addFormDataPart(params.key, FileUtils.getPhotoName(params.file.getAbsolutePath()), RequestBody.create(MEDIA_TYPE_IMAGE, params.file));
        }

        Headers.Builder headers = new Headers.Builder();
        headers.add("x-stbl-token", SharedToken.getToken(mContext));
        headers.add("User-Agent", SharedDevice.toDeviceString());
        headers.add("x-stbl-lang", AppUtils.getCurrLang());
        headers.add("x-stbl-guid", AppUtils.getGUID());

        registerPostRequest(methodName, headers.build(), builder.build());
    }

/*
    // 新的发图
    public void commonPostImg2(final String methodName, Params params, final FinalHttpCallback m, InputStream... objs) {
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        LogUtil.logE(TAG, "commonPostImg2");
        ParamsJson.parse(methodName, params);
        setFinalHttpCallback(m);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
            httpListener.parse(methodName, str);
            return;
        }
        try {
            showLoading();
            url.setLength(0);
            url.append(SharedCommon.getIp(mContext));//Config.hostMain
            url.append(methodName);
            setFinalHttpCallback(m);
            // 下面一段新加
            String path = "";
            MultipartBuilder builder2 = new MultipartBuilder();
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("userid", SharedToken.getUserId(MyApplication.getContext()))
                    .addFormDataPart("pic", "filename", RequestBody.create(MEDIA_TYPE_PNG, new File(path)))
                    .build();

            byte[] data = null;
            if (objs.length > 0) {
                InputStream input = (objs[0]);
                data = IOUtils.getBytes(input);
                LogUtil.logE("commonPostData", "length:" + data.length);
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), data);//multipart/form-data__________text/x-markdown; charset=utf-8
                //builder2.addPart(Headers.of("Content-Disposition", "form-data; name=\"mFile\"; filename=\"wjd.mp4\""), fileBody);
                builder2.addPart(fileBody);
            }

            if (params != null) {
                LogUtil.logE(TAG, "size:" + params.getList().size());
                for (BasicNameValuePair value : params.getList()) {
                    if (value.getName() == null || value.getValue() == null)
                        continue;
                    //builder.add(value.getName(), value.getValue());//旧
                    LogUtil.logE(TAG, "value:name:" + value.getName());
                    builder2.addFormDataPart(value.getName(), value.getValue());
                }
            }

            Headers.Builder headers = new Headers.Builder();
            headers.add("x-stbl-token", SharedToken.getToken(mContext));
            headers.add("User-Agent", SharedDevice.toString(mContext));
            headers.add("Host", Config.reserveHostIp);//新加
            registerPostRequest(methodName, headers.build(), builder2.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/

    @Override
    public String getReuqestUrl() {
        return url.toString();
    }

    @Override
    public void onFailture(Request request, IOException error, final String methodnName) {
        hideLoading();
        String value = "";
        if (request.body() == null) {
            value = "body is null";
        } else {
            value = request.body().toString();
        }
        LogUtil.logE(TAG + "onFailture  Error:", value + error.toString() + Thread.currentThread().getId());
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                //ToastUtil.showToast(mContext, "请求失败");
                LogUtil.logE(TAG + "onFailture  Error:", "run()   "+"httpListener =" +httpListener);
                String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
                if (httpListener != null){
                    LogUtil.logE(TAG + "onFailture  Error:", "httpListener");
                    httpListener.parse(methodnName, str);
                }
            }
        });
    }

    @Override
    public void onSuccess(Response response, final String methodName) {
        hideLoading();
        try {
            final String value = response.body().string();
            if (value != null && !TextUtils.isEmpty(value)) {
                LogUtil.logE(methodName + " OnSuccess : ", value);
                if (httpListener == null)
                    return;
                if (mContext instanceof Activity) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            boolean isHandled = dealTokenCode(value);
                            //LogUtil.logE(methodName, "IsHandled : " + isHandled + " httpListener : " + httpListener.toString());
                            if (isHandled) {
                                BaseItem item = JSONHelper.getObject(value, BaseItem.class);
                                if (item == null) {
//                                    ToastUtil.showToast(mContext, "请求失败");
                                    String str = JSONHelper.getStringFromObject(getNoNetBaseItem());
                                    httpListener.parse(methodName, str);
                                } else {
                                    httpListener.parse(methodName, value);
                                }
                            }
                        }
                    });
                } else {
                    boolean isHandled = dealTokenCode(value);
                    // LogUtil.logE(methodName, "else IsHandled : " + isHandled + " httpListener : " + httpListener.toString());
                    if (isHandled) {
                        BaseItem item = JSONHelper.getObject(value, BaseItem.class);
                        if (item != null)
                            httpListener.parse(methodName, value);
                    }
                }
            } else {
                if (mContext instanceof Activity)
                    ((Activity) mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_operate_err));
                        }
                    });
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    protected void setFinalHttpCallback(FinalHttpCallback httpListener) {
        this.httpListener = httpListener;
    }
}
