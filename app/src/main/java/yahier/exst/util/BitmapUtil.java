package yahier.exst.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.stbl.stbl.common.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 操作Bitmap的工具类
 *
 * @author lenovo
 */
public class BitmapUtil {

    /**
     * 返回相对较小的比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);// 高度比例
            // 4
            final int widthRatio = Math.round((float) width / (float) reqWidth);// 宽度比例
            // 3
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;

            if (reqWidth == Config.maxUploadImgWidth && widthRatio >= heightRatio) {
                if (inSampleSize == 3) {//等于3时会降到2，误差太大；等于3的时候，960*3/4==720刚好
                    inSampleSize = 4;
                } else if (inSampleSize == 6) {//960*6/8==720
                    inSampleSize = 8;
                } else if (inSampleSize == 7) {//960*7/8==840
                    inSampleSize = 8;
                }
            }
        }
        LogUtil.logE("BitmapUtil.calculateInSampleSize  " + inSampleSize);
        return inSampleSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     */
    public static int computeScale(BitmapFactory.Options options, int objWidth, int objHeight) {
        int inSampleSize = 1;
        if (objWidth == 0 || objHeight == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > objWidth || bitmapHeight > objWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) objWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) objWidth);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    public static Bitmap decodeBitmapSafely(Resources resources, int resId) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(resources, resId);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            ImageUtils.clearMemoryCache();
            System.gc();
            try {
                bitmap = BitmapFactory.decodeResource(resources, resId);
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                bitmap = ImageUtils.decodeBitmapSafety(resources, resId, null);
            }
        }
        return bitmap;
    }

    public static Bitmap decodeBitmapSafely(String path, BitmapFactory.Options options) throws IOException {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            ImageUtils.clearMemoryCache();
            System.gc();
            try {
                bitmap = BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                FileInputStream inputStream = new FileInputStream(path);
                bitmap = ImageUtils.decodeBitmapSafety(MyApplication.getContext().getResources(), 0, inputStream);
            }
        }
        return bitmap;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }

            //LogUtil.logE("getBitmapDegree", degree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
//        Bitmap returnBm = null;
//        // 根据旋转角度，生成旋转矩阵
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        try {
//            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
//            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//        } catch (OutOfMemoryError e) {
//        }
//        if (returnBm == null) {
//            returnBm = bm;
//        }
//        if (bm != returnBm) {
//            bm.recycle();//这里的回收引发你一些问题
//        }
//        return returnBm;
        return rotateBitmapByDegree(bm, degree, true);
    }

    //与上面方法作用相同，多了isRecycle，判断是否需要回收
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree, boolean isRecycle) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (isRecycle && bm != returnBm) {
            bm.recycle();//这里的回收引发你一些问题
        }
        return returnBm;
    }


    public static Bitmap rotateUpright(Bitmap bitmap, String path) {
        int degree = BitmapUtil.getBitmapDegree(path);
        //LogUtil.logE("degree:" + degree);
        if (degree == 0) {
            return bitmap;
        }
        return rotateBitmapByDegree(bitmap, degree);
    }

    public static Bitmap rotateUpright(Bitmap bitmap, String path, boolean isRecycle) {
        int degree = BitmapUtil.getBitmapDegree(path);
        //LogUtil.logE("degree:" + degree);
        if (degree == 0) {
            return bitmap;
        }
        return rotateBitmapByDegree(bitmap, degree, isRecycle);
    }

    public static File createUploadTempFile(File origin, String tempName) throws IOException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(origin.getAbsolutePath(), opts);
        opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, Config.maxUploadImgWidth, Config.maxUploadImgHeight);
        opts.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(origin.getAbsolutePath(), opts);
        int degree = getImageDegree(origin.getAbsolutePath());
        Bitmap rotateBmp = bmp;
        if (degree != 0) {
            rotateBmp = rotateBitmap(degree, bmp);
        }
        File temp = new File(FileUtils.getTempDir(), tempName);
        FileUtils.saveBitmap(temp, rotateBmp);
        return temp;
    }

    public static File createUploadTempFile(File origin, String tempName, int width, int height) throws IOException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(origin.getAbsolutePath(), opts);
        opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, width, height);
        opts.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(origin.getAbsolutePath(), opts);
        int degree = getImageDegree(origin.getAbsolutePath());
        Bitmap rotateBmp = bmp;
        if (degree != 0) {
            rotateBmp = rotateBitmap(degree, bmp);
        }
        File temp = new File(FileUtils.getTempDir(), tempName);
        FileUtils.saveBitmap(temp, rotateBmp);
        return temp;
    }

    public static int getImageDegree(String path) throws IOException {
        int degree = 0;
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        LogUtil.logE("degree= " + degree);
        return degree;
    }

    public static Bitmap rotateBitmap(float degrees, Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

}
