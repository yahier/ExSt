package yahier.exst.util;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class VoicePlayer extends MediaPlayer {

    public boolean pauseOrResume() {
        return pauseOrResume(isPlaying());
    }

    private boolean pauseOrResume(boolean isPlaying) {
        if (isPlaying) {
            pause();
            return false;
        } else {
            start();
            return true;
        }
    }

    public void downloadOnlineVoice(final String url, final OnDownloadSuccessListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String name = StringUtil.hashKeyForDisk(url) + ".amr";
                    final File file = new File(FileUtils.getDownloadDir(), name);
                    if (file.exists()) {
                        postDownloadSuccess(file.getAbsolutePath(), listener);
                        return;
                    }
                    Response response = OkHttpHelper.getInstance().getResponse(url);
                    InputStream input = response.body().byteStream();
                    long contentLength = response.body().contentLength();
                    long current = 0;

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
                            postDownloadSuccess(file.getAbsolutePath(), listener);
                        } finally {
                            input.close();
                            buffer.flush();
                            buffer.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void postDownloadSuccess(final String path, final OnDownloadSuccessListener listener) {
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                listener.onDownloadSuccess(path);
            }
        });
    }

    public interface OnDownloadSuccessListener {
        void onDownloadSuccess(String path);
    }
}
