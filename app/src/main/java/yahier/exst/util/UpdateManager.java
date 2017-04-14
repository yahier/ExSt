package yahier.exst.util;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.receiver.NotificationClickReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tnitf on 2016/7/7.
 */
public class UpdateManager {

    private static final int NOTIFY_ID = 0;
    private static final int WHAT_START = 1;
    private static final int WHAT_DONE = 2;
    private static final int WHAT_PROGRESS = 3;
    private static final int WHAT_ERROR = -1;

    private static UpdateManager sInstance;

    public static UpdateManager getInstance() {
        if (sInstance == null) {
            synchronized (UpdateManager.class) {
                if (sInstance == null) {
                    sInstance = new UpdateManager();
                }
            }
        }
        return sInstance;
    }

    private NotificationManagerCompat mNotifyManager;
    private Bitmap mLargeIcon;
    private Handler mHandler;

    private AtomicBoolean mDownloading = new AtomicBoolean(false);

    private String mApkName;

    private boolean mShowNotify = true;

    private UpdateManager() {
        mHandler = new MainHandler();
        mNotifyManager = NotificationManagerCompat.from(MyApplication.getContext());
        mLargeIcon = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon);
    }

    public void update(String url, int versionCode, boolean showNotify) {
        mApkName = "stbl_latest_version_code_" + versionCode + ".apk";
        mShowNotify = showNotify;
        if (!mShowNotify) {
            mNotifyManager.cancel(NOTIFY_ID);
        }
        if (isDownloadDone()) {
            if (mShowNotify) {
                installApk();
            }
            return;
        }
        if (!mDownloading.get()) {
            mDownloading.set(true);
            mHandler.obtainMessage(WHAT_START, 0, 0).sendToTarget();
            DlTask task = new DlTask(url);
            ThreadPool.getInstance().execute(task);
        }
    }

    private boolean isDownloadDone() {
        File file = new File(FileUtils.getDownloadDir(), mApkName);
        if (file.exists()) {
            try {
                PackageManager pm = MyApplication.getContext().getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                if (info != null && info.applicationInfo != null) {
                    return true;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void installApk() {
        File file = new File(FileUtils.getDownloadDir(), mApkName);
        if (file.exists()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                MyApplication.getContext().startActivity(intent);
            } catch (Throwable e) {
                e.printStackTrace();
                file.delete();
            }
        }
    }

    private class MainHandler extends Handler {

        public MainHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (!mShowNotify) {
                return;
            }
            switch (msg.what) {
                case WHAT_START:
                    notifyStart();
                    break;
                case WHAT_DONE:
                    notifyDone();
                    break;
                case WHAT_PROGRESS:
                    notifyProgress(msg.arg1);
                    break;
                case WHAT_ERROR:
                    notifyError();
                    break;
            }
        }
    }

    private void notifyStart() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext());
        builder.setSmallIcon(R.drawable.notify_update)
                .setLargeIcon(mLargeIcon)
                .setContentTitle(MyApplication.getContext().getString(R.string.common_version_upate))
                .setTicker(MyApplication.getContext().getString(R.string.common_download_start))
                .setContentText(MyApplication.getContext().getString(R.string.common_downloading) + "0%")
                .setProgress(100, 0, false);
        mNotifyManager.notify(NOTIFY_ID, builder.build());
    }

    private void notifyDone() {
        mNotifyManager.cancel(NOTIFY_ID);
        installApk();
    }

    private void notifyProgress(int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext());
        builder.setSmallIcon(R.drawable.notify_update)
                .setLargeIcon(mLargeIcon)
                .setContentTitle(MyApplication.getContext().getString(R.string.common_version_upate))
                .setTicker(MyApplication.getContext().getString(R.string.common_download_start))
                .setContentText(MyApplication.getContext().getString(R.string.common_downloading) + progress + "%")
                .setProgress(100, progress, false);
        mNotifyManager.notify(NOTIFY_ID, builder.build());
    }

    private void notifyError() {
        Intent clickIntent = new Intent(MyApplication.getContext(), NotificationClickReceiver.class); //点击通知之后要发送的广播
        clickIntent.setAction(ACTION.UPDATE_NOTIFICATION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), NOTIFY_ID,
                clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext());
        builder.setSmallIcon(R.drawable.notify_update)
                .setLargeIcon(mLargeIcon)
                .setContentTitle(MyApplication.getContext().getString(R.string.common_version_upate))
                .setTicker(MyApplication.getContext().getString(R.string.common_download_err))
                .setContentText(MyApplication.getContext().getString(R.string.common_download_err_reset))
                .setContentIntent(pendingIntent);
        mNotifyManager.notify(NOTIFY_ID, builder.build());
    }

    public class DlTask implements Runnable {

        public String url;

        private long current = 0;
        private boolean append = false;

        private boolean mSuccess = false;

        public DlTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                download();
                if (mSuccess) {
                    onDone();
                } else {
                    onError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError();
            }
        }

        private void download() throws Exception {
            File file = new File(FileUtils.getDownloadDir(), mApkName);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.canWrite()) {
                current = file.length();
            }
            if (current > 0) {
                append = true;
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Range", "bytes=" + current + "-");
                conn.setRequestProperty("x-stbl-token", SharedToken.getToken(MyApplication.getContext()));
                conn.setRequestProperty("User-Agent", SharedDevice.toDeviceString());
                conn.setRequestProperty("x-stbl-lang", AppUtils.getCurrLang());
                conn.setRequestProperty("x-stbl-guid", AppUtils.getGUID());
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                    getResponseData(conn, file);
                } else if (responseCode == HttpURLConnection.HTTP_OK) {
                    append = false;
                    current = 0;
                    getResponseData(conn, file);
                } else if (responseCode == 416) {
                    mSuccess = true;
                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        protected void getResponseData(HttpURLConnection conn, File file) throws IOException {
            if (conn != null) {
                InputStream inputStream = conn.getInputStream();
                if (inputStream != null) {
                    long contentLength = conn.getContentLength() + current;
                    FileOutputStream outputStream = new FileOutputStream(file, append);
                    try {
                        byte[] tmp = new byte[4096];
                        int l = 0;
                        long old = System.currentTimeMillis();
                        while ((l = inputStream.read(tmp)) != -1) {
                            current += l;
                            outputStream.write(tmp, 0, l);

                            long curr = System.currentTimeMillis();
                            if (curr - old >= 500) {
                                onProgress(current, contentLength);
                                old = curr;
                            }
                        }
                        outputStream.flush();
                        if (isDownloadDone()) {
                            mSuccess = true;
                        }
                    } finally {
                        inputStream.close();
                        outputStream.close();
                    }
                }
            }
        }

        private void onProgress(long bytesWritten, long totalSize) {
            int progress = (int) (bytesWritten * 100 / totalSize);
            mHandler.obtainMessage(WHAT_PROGRESS, progress, 0).sendToTarget();
            LogUtil.logE("progress = " + progress + "%");
        }

        private void onDone() {
            mDownloading.set(false);
            mHandler.obtainMessage(WHAT_DONE, 100, 0).sendToTarget();
        }

        private void onError() {
            mDownloading.set(false);
            mHandler.sendMessageDelayed(Message.obtain(mHandler, WHAT_ERROR, 0, 0), 500);
        }

    }

}
