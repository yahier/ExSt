package yahier.exst.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.stbl.base.library.task.Task;
import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FileUtils {

    public static final String UPDATE_APK_NAME = "stbl.apk";
    public static final String QRCODE_IMAGE_NAME = "stbl_qrcode.jpg";
    public static final String UPLOAD_IMAGE_TEMP_FILE = "upload_image_temp_file.jpg";

    //文件夹名称
    private static final String APP_NAME = "stbl";
    private static final String DOWNLOAD_NAME = "download";
    private static final String IMAGE_NAME = "image";
    private static final String DATABASE_NAME = "database";
    private static final String LOG_NAME = "log";
    private static final String CACHE_NAME = "cache";
    private static final String TEMP_NAME = "temp";

    //文件夹路径
    private static final String APP_DIR;
    private static final String DOWNLOAD_DIR;
    private static final String IMAGE_DIR;
    private static final String DATABASE_DIR;
    private static final String LOG_DIR;
    private static final String CACHE_DIR;
    private static final String TEMP_DIR;

    static {
        String rootPath = getRootPath();
        APP_DIR = rootPath + File.separator + APP_NAME;
        DOWNLOAD_DIR = APP_DIR + File.separator + DOWNLOAD_NAME;
        IMAGE_DIR = APP_DIR + File.separator + IMAGE_NAME;
        DATABASE_DIR = APP_DIR + File.separator + DATABASE_NAME;
        LOG_DIR = APP_DIR + File.separator + LOG_NAME;
        CACHE_DIR = APP_DIR + File.separator + CACHE_NAME;
        TEMP_DIR = APP_DIR + File.separator + TEMP_NAME;
    }

    // 获取根目录
    public static String getRootPath() {
        String path = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = MyApplication.getContext().getCacheDir().getPath();
        }
        return path;
    }

    public static void initDirs() {
        File file = new File(APP_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(LOG_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(DOWNLOAD_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(IMAGE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(DATABASE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(CACHE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(TEMP_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getAppDir() {
        File file = new File(APP_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return APP_DIR;
    }

    public static String getLogDir() {
        File file = new File(LOG_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return LOG_DIR;
    }

    public static String getDownloadDir() {
        File file = new File(DOWNLOAD_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DOWNLOAD_DIR;
    }

    public static String getImageDir() {
        File file = new File(IMAGE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return IMAGE_DIR;
    }

    public static String getDatabaseName() {
        File file = new File(DATABASE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DATABASE_DIR;
    }

    public static String getCacheDir() {
        File file = new File(CACHE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return CACHE_DIR;
    }

    public static String getTempDir() {
        File file = new File(TEMP_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return TEMP_DIR;
    }

    public static void clearDir(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                clearDir(f);
            }
            file.delete();
        }
    }

    /**
     * 获取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");// �ļ�����Ϊrose.txt
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveBitmap(Bitmap bm, String picName) {
        String path = "";
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(Config.localFilePath, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            path = f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    //保存到本地，并返回地址
    public static String saveBitmapReturnFilePath(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            String fileName = Config.localFilePath + picName + ".JPEG";
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发广播通知系统图库刷新这个图片
     *
     * @param fileName
     */
    public static void sendBroadCastRefreshPhoto(Context context, String fileName) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(fileName));
        intent.setData(uri);
        context.sendBroadcast(intent);//这个广播的目的就是更新图库
    }

    /**
     * 按比例裁切图片
     * rate = w / h
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float rate) {
        if (null == bitmap)
            return null;
        float w = bitmap.getWidth();
        float h = bitmap.getHeight();
        float newW = w, newH = h;
        float startX = 0;
        float startY = 0;
        if (w > h) {
            newW = (int) (h * rate);
            startX = (w - newW) / 2;
        } else {
            newH = (int) (w / rate);
            startY = (h - newH) / 2;
        }
        return Bitmap.createBitmap(bitmap, (int) startX, (int) startY, (int) newW, (int) newH, null, false);
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(Config.localFilePath + dirName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(Config.localFilePath + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(Config.localFilePath + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir(file);
        }
        dir.delete();

    }

    /**
     * 拍照前和拍照后都要取得此文件
     */
    public static File getFile(String fileName) {
        String imgPath = Config.localFilePath + fileName + ".jpg";
        File imageFile = new File(imgPath);
        return imageFile;
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    public static boolean copyFile(File oldFile, File newFile) throws IOException, NullPointerException {
        if (oldFile == null || !oldFile.exists()) {
            return false;
        }
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(oldFile);
            output = new FileOutputStream(newFile);
            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
        return true;
    }

    public static void scanFile(File file) {
        MyApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    public static void saveImage(final String url, final String fileName) {
        if (!TextUtils.isEmpty(url)) {
            new Task<String>() {

                @Override
                protected void call() {
                    FutureTarget<File> future = Glide.with(MyApplication.getContext())
                            .load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    try {
                        File cacheFile = future.get();
                        if (cacheFile == null || !cacheFile.exists()) {
                            onError("");
                            return;
                        }
                        File desFile = new File(getAppDir(), fileName + ".jpg");
                        try {
                            FileUtils.copyFile(cacheFile, desFile);
                            FileUtils.scanFile(desFile);
                            onSuccess(desFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        onError(e);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        onError(e);
                    }
                }
            }.setCallback(new TaskCallback<String>() {
                @Override
                public void onError(TaskError e) {
                    ToastUtil.showToast(e.msg);
                }

                @Override
                public void onSuccess(String result) {
                    ToastUtil.showToast(R.string.common_photo_save_to_stbl);
                }
            }).start();
        }
    }


    /**
     * 保存图片url.返回保存后的本地文件
     *
     * @param imgUrl
     * @return
     */
    public static File getFileAfterSvae(String imgUrl) {
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(imgUrl);
            try {
                in = url.openStream();
                File desFile = new File(getAppDir(), System.currentTimeMillis() + ".jpg");
                out = new FileOutputStream(desFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                return desFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return null;
    }

    public static void save(final String imgUrl) {
        HandleAsync hand = new HandleAsync();
        hand.excute(new HandleAsync.Listener() {
            @Override
            public String getResult() throws ClientProtocolException, IOException {
                File file = getFileAfterSvae(imgUrl);
                return null;
            }

            @Override
            public void parse(String result) {

            }
        });
    }


    public static String getPhotoName(String path) {
        String name = "";
        String[] array = path.split(File.separator);
        name = array[array.length - 1];
        return name;
    }

    public static void saveBitmap(String path, Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveBitmap(File file, Bitmap bmp) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }

    public static File saveTempBitmap(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File file = new File(getTempDir(), "temp_" + System.currentTimeMillis());
            if (file.exists()) {
                file.delete();
            }
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void clearTempDir() {
        File file = new File(getTempDir());
        File[] childFile = file.listFiles();
        if (childFile == null || childFile.length == 0) {
            return;
        }
        for (File f : childFile) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }

}
