package yahier.exst.task;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Response;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.dialog.UpdateDialog;
import com.stbl.stbl.model.UpdateInfo;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.MainHandler;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.PkgUtils;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tnitf on 2016/3/21.
 */
public class UpdateTask {

    public static SimpleTask<UpdateInfo> getUpdateInfo() {
        return new SimpleTask<UpdateInfo>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.checkUpdateInfo, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    UpdateInfo info = JSON.parseObject(response.result, UpdateInfo.class);
                    if (info != null) {
                        if (!TextUtils.isEmpty(info.getDownloadurl())) {
                            SharedPrefUtils.putToPublicFile(KEY.LATEST_VERSION_CODE, info.getVersioncode());
                            SharedPrefUtils.putToPublicFile(KEY.LATEST_APK_URL, info.getDownloadurl());
                        }
                        onCompleted(info);

                        String apkName = "stbl_latest_version_code_" + PkgUtils.getVersionCode() + ".apk";
                        File file = new File(FileUtils.getDownloadDir(), apkName);
                        if (file.exists()) {
                            file.delete();
                        }
                    } else {
                        onError("数据返回错误");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static void download(final String url, final UpdateDialog.OnDownloadCallback callback) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpHelper.getInstance().getResponse(url);
                    InputStream input = response.body().byteStream();
                    long contentLength = response.body().contentLength();
                    long current = 0;
                    final File file = new File(FileUtils.getDownloadDir(), FileUtils.UPDATE_APK_NAME);

                    FileOutputStream buffer = new FileOutputStream(file, false);
                    if (input != null) {
                        try {
                            byte[] tmp = new byte[4096];
                            int l = 0;
                            long old = System.currentTimeMillis();
                            while ((l = input.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                                current += l;
                                buffer.write(tmp, 0, l);

                                long curr = System.currentTimeMillis();
                                if (curr - old >= 500) {
                                    Log.i("dl", (current * 100 / contentLength) + "%");
                                    old = curr;
                                }
                            }
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDownloadSuccess(file);
                                }
                            });
                        } finally {
                            input.close();
                            buffer.flush();
                            buffer.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDownloadError();
                        }
                    });
                }
            }
        });
    }


}
