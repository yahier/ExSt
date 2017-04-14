package yahier.exst.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class CropPhotoUtils {

    // startActivityForResult的requestCode
    public static final int RESULT_CODE_NONE = 0;
    public static final int REQUEST_CODE_PHOTOGRAPH = 1;
    public static final int REQUEST_CODE_ALBUM = 2;
    public static final int REQUEST_CODE_PHOTOCROP = 3;
    public static final String PHOTO_FILE_NAME = "head.jpg";
    public static final String CROP_PHOTO_FILE_NAME = "crop_head.jpg";
    public static final String IMAGE_UNSPECIFIED = "image/*";

    static boolean isSquare = true;

    public static int typeUploadAd = 1;
    static int type;

    public static void select(Activity activity, int position) {
        isSquare = true;
        Intent intent = null;
        switch (position) {
            case 0:
                // 以下只會取相簿中的圖片
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_UNSPECIFIED);
                activity.startActivityForResult(intent,
                        REQUEST_CODE_ALBUM);
                break;
            case 1:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(new File(FileUtils.getAppDir(),
                                PHOTO_FILE_NAME)));
                Log.i("拍照保存路径", FileUtils.getAppDir() + "/"
                        + PHOTO_FILE_NAME);
                activity.startActivityForResult(intent,
                        REQUEST_CODE_PHOTOGRAPH);
                break;
            default:
                break;
        }
    }

    public static void select(Activity activity, int position, boolean isSquare) {
        select(activity, position);
        CropPhotoUtils.type = 0;
        CropPhotoUtils.isSquare = isSquare;
    }

    public static void select(Activity activity, int position, int type) {
        select(activity, position);
        CropPhotoUtils.type = type;
    }

    public static void startPhotoZoom(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");

        // intent携带的图片大于1M，会崩溃，所以不携带图片回来了，保存在我们指定的目录
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        //如果是头像
        if (isSquare) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //intent.putExtra("outputX", 500);
            //intent.putExtra("outputY", 500);
        } else {
            intent.putExtra("aspectX", Config.statusesCoverWidthScale);
            intent.putExtra("aspectY", Config.statusesCoverHeightScale);
        }

        if (type == typeUploadAd) {
            intent.putExtra("aspectX", Config.uploadAdWidthScale);
            intent.putExtra("aspectY", Config.uploadAdHeightScale);
        }


        activity.startActivityForResult(intent, REQUEST_CODE_PHOTOCROP);
    }

    public static Uri getTempUri() {
        return Uri
                .fromFile(new File(FileUtils.getAppDir(), CROP_PHOTO_FILE_NAME));
    }

    public static File onActivityResult(Activity activity, int requestCode,
                                        int resultCode, Intent data) {
        File file = null;
        if (resultCode == RESULT_CODE_NONE) {
            return file;
        }
        switch (requestCode) {
            case REQUEST_CODE_PHOTOGRAPH:
                File picture = new File(FileUtils.getAppDir(), PHOTO_FILE_NAME);
                send(activity, picture);
                Log.i("照片保存路径", "------------------------" + picture.getPath());
                startPhotoZoom(activity, Uri.fromFile(picture));
                break;
            case REQUEST_CODE_ALBUM:
                if (data != null) {
                    startPhotoZoom(activity, data.getData());
                }
                break;
            case REQUEST_CODE_PHOTOCROP:
                if (data == null) {
                    return file;
                }
                file = new File(FileUtils.getAppDir(), CROP_PHOTO_FILE_NAME);
                send(activity, file);

                break;
        }
        return file;
    }

    static void send(Activity act, File file) {
        act.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
}
