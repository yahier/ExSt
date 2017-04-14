package yahier.exst.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ServerError;

import java.io.IOException;
import java.io.InputStream;

/**
 * 封装http操作
 *
 * @author ruilin
 */
public class HttpUtil {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/jpeg");

    private Object mHandle;
    private Dialog loadingDialog;
    private Context mContext;
    private int triedTimes = 0;
    private final int tryMaxTimes = 2;

    public HttpUtil(Context mContext, Object handle) {
        this.mContext = mContext;
        mHandle = handle;
    }

    public HttpUtil(Activity act, Object handle, Dialog dialog) {
        this.mContext = act;
        this.loadingDialog = dialog;
        if (null != loadingDialog) {
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(true);
        }
    }

    private void showLoading() {
        if (null == loadingDialog || ((Activity) mContext).isFinishing())
            return;
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    public void hideLoading() {
        if (null != loadingDialog)
            loadingDialog.dismiss();
    }

    public static void getHttpGetBitmap(final Activity act, final String methodName, final OnHttpGetCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(Config.hostMain + methodName)
                            .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                            .addHeader("User-Agent", SharedDevice.toDeviceString())
                            .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                            .addHeader("x-stbl-guid", AppUtils.getGUID())
                            .build();
                    new OkHttpClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            act.runOnUiThread(new Runnable() {
                                public void run() {
                                    ToastUtil.showToast(act, act.getString(R.string.common_connection_err));
                                }
                            });
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            InputStream is = response.body().byteStream();
                            final Bitmap bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            final String verifyCode = response.headers().get("stbl_imgvertify_id");
                            act.runOnUiThread(new Runnable() {
                                public void run() {
                                    callback.onHttpGetImgVerify(methodName, bitmap, verifyCode);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            ToastUtil.showToast(act, act.getString(R.string.common_connection_err));
                        }
                    });
                }
            }
        }).start();
    }

    public void post(final String methodName, final OnFinalHttpCallback m) {
        ParamsJson.parse(methodName, "");
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            return;
        }
        showLoading();
        String url = Config.hostMain + methodName;
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, new JSONObject().toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        LogUtil.logE("LogUtil", methodName + "--------" + e.toString());
                        m.onHttpError(methodName, e.toString(), mHandle);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    final String result = response.body().string();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();

                            if (TextUtils.isEmpty(result)) {
                                ToastUtil.showToast(mContext, mContext.getString(R.string.common_operate_err));
                                return;
                            }
                            LogUtil.logE(methodName, result);

                            BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                            if (item == null) {
                                return;
                            }
                            if (item.getIssuccess() != BaseItem.successTag) {
                                ToastUtil.showToast(mContext, item.getErr().getMsg());
                                return;
                            }

                            m.onHttpResponse(methodName, JSONHelper.getStringFromObject(item.getResult()), mHandle);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();
                            m.onHttpError(methodName, "连接超时", mHandle);
                            LogUtil.logE("LogUtil", methodName + "--------" + "连接超时");
                        }
                    });
                }
            }
        });

    }

    public void postJson(final String methodName, final String json, final OnFinalHttpCallback m) {
        ParamsJson.parse(methodName, json);
        if (!NetUtil.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.common_network_not_available));
            return;
        }
        showLoading();
        String token = SharedToken.getToken(mContext);
        if (null == token)
            return;
        triedTimes++;

        String url = Config.hostMain + methodName;
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        if (e == null || e.getMessage() == null){
                            // 错误回调
                            m.onHttpError(methodName, mContext.getString(R.string.common_response_timeout), mHandle);
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_response_timeout));
                            return;
                        }
                        LogUtil.logE(methodName + " onFailure " + e.getMessage());
                        StackTraceElement[] tracks = e.getStackTrace();
                        for (StackTraceElement el : tracks) {
                            LogUtil.logE("error:", el.getClassName());
                        }

                        // 响应超时， 应该不再提交
                        if (e.getMessage().contains("SocketTimeoutException")) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_response_timeout));
                            // return;
                        }
                        if (triedTimes < tryMaxTimes) {
                            postJson(methodName, json, m);
                            return;
                        }
                        if (e.getMessage().contains("ConnectTimeoutException")) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_connection_timeout));
                        } else if (e.getMessage().contains("HttpResponseException")) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_service_busyness));
                        }
                        // 错误回调
                        m.onHttpError(methodName, e.getMessage(), mHandle);
                        LogUtil.logE("LogUtil", methodName + "--------" + e.getMessage());
                    }
                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    final String result = response.body().string();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();

                            if (result == null || result.equals("")) {
                                if (triedTimes < tryMaxTimes)
                                    postJson(methodName, json, m);
                                else
                                    ToastUtil.showToast(mContext, mContext.getString(R.string.common_operate_err));
                                return;
                            }
                            Log.e(methodName, result);
                            BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                            if (item == null) {
                                m.onHttpError(methodName, mContext.getString(R.string.common_service_busyness), mHandle);
                                LogUtil.logE("LogUtil", methodName + "--------" + "服务繁忙,请稍候重试");
                                return;
                            }
                            String json = JSONHelper.getStringFromObject(item.getResult());
                            if (item == null) {
                                return;
                            }
                            if (item.getIssuccess() == BaseItem.successTag) {
                                m.onHttpResponse(methodName, json, mHandle);
                            } else {
                                if (item.getErr() == null) {
                                    m.onHttpError(methodName, mContext.getString(R.string.common_operate_err), mHandle);
                                    return;
                                }
                                if (!ServerError.checkError(mContext, item.getErr().getErrcode(), item.getErr().getMsg()))
                                    return;
                                ToastUtil.showToast(mContext, item.getErr().getMsg());
                                m.onHttpError(methodName, item.getErr().getErrcode() + "", mHandle);
                                LogUtil.logE("LogUtil", methodName + "--------" + item.getErr().getErrcode());
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();
                            m.onHttpError(methodName, "连接超时", mHandle);
                            LogUtil.logE("LogUtil", methodName + "--------" + "连接超时");
                        }
                    });
                }
            }
        });
    }

    public void postImg(final String methodName, Params params, final OnFinalHttpCallback m) {
        ParamsJson.parse(methodName, params);
        showLoading();

        String url = Config.hostUploadWeiboImg + methodName;
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        JSONObject json = params.json;
        for (String key : json.keySet()) {
            builder.addFormDataPart(key, json.getString(key));
        }
        if (params.file != null) {
            builder.addFormDataPart(params.key, FileUtils.getPhotoName(params.file.getAbsolutePath()), RequestBody.create(MEDIA_TYPE_IMAGE, params.file));
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-stbl-token", SharedToken.getToken(MyApplication.getContext()))
                .addHeader("User-Agent", SharedDevice.toDeviceString())
                .addHeader("x-stbl-lang", AppUtils.getCurrLang())
                .addHeader("x-stbl-guid", AppUtils.getGUID())
                .post(builder.build())
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        if (e.getMessage().contains("ConnectTimeoutException")) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_connection_timeout));
                        } else if (e.getMessage().contains("HttpResponseException")) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.common_service_busyness));
                        }

                        LogUtil.logE("http onFailure", e.toString());
                        StackTraceElement[] tracks = e.getStackTrace();
                        for (StackTraceElement el : tracks) {
                            LogUtil.logE("error:", el.getClassName());
                        }
                        m.onHttpError(methodName, e.getMessage(), mHandle);
                        LogUtil.logE("LogUtil", methodName + "--------" + e.getMessage());
                    }
                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    final String t = response.body().string();

                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();

                            if (t == null || t.equals("")) {
                                ToastUtil.showToast(mContext, mContext.getString(R.string.common_operate_err));
                                return;
                            }
                            Log.e(methodName, t);
                            BaseItem item = JSONHelper.getObject(t, BaseItem.class);
                            if (item == null) {
                                m.onHttpError(methodName, mContext.getString(R.string.common_service_busyness), mHandle);
                                LogUtil.logE("LogUtil", methodName + "--------" + "服务繁忙,请稍候重试");
                                return;
                            }
                            if (item.getIssuccess() != BaseItem.successTag) {
                                if (!ServerError.checkError(mContext, item.getErr().getErrcode(), item.getErr().getMsg()))
                                    return;
                                ToastUtil.showToast(mContext, item.getErr().getMsg());
                                m.onHttpError(methodName, item.getErr().getMsg(), mHandle);
                                LogUtil.logE("LogUtil", methodName + "--------" + item.getErr().getMsg());
                                return;
                            }
                            String json = JSONHelper.getStringFromObject(item.getResult());
                            m.onHttpResponse(methodName, json, mHandle);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoading();
                            m.onHttpError(methodName, "连接超时", mHandle);
                            LogUtil.logE("LogUtil", methodName + "--------" + "连接超时");
                        }
                    });
                }
            }
        });
    }
}
