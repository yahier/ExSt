package yahier.exst.task;

import com.squareup.okhttp.Response;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.OkHttpHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tnitf on 2016/8/3.
 */
public class FileTask {

    public static Task<File> download(final String url, final String fileName) {
        return new Task<File>() {
            @Override
            protected void call() {
                try {
                    Response response = OkHttpHelper.getInstance().getResponse(url);
                    InputStream input = response.body().byteStream();
                    long contentLength = response.body().contentLength();
                    long current = 0;
                    File file = new File(FileUtils.getDownloadDir(), fileName);

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
                                    LogUtil.logE("progress= " + (current * 100 / contentLength) + "%");
                                    old = curr;
                                }
                            }
                            buffer.flush();
                            onSuccess(file);
                        } finally {
                            input.close();
                            buffer.close();
                        }
                    } else {
                        onError(new IOException());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

}
