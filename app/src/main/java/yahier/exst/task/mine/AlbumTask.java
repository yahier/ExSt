package yahier.exst.task.mine;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.ImageBucket;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/8.
 */
public class AlbumTask {

    public static Task<ArrayList<Photo>> getPhotoList(final long objuserid, final int page, final int count) {
        return new Task<ArrayList<Photo>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetAlbumPhotos, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Photo> dataList = (ArrayList<Photo>) JSON.parseArray(response.result, Photo.class);
                    if (dataList != null) {
                        onSuccess(dataList);
                    } else {
                        onError(Res.getString(R.string.me_data_error));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Integer> delete(final String photoids) {
        return new Task<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("photoids", photoids);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userDeleteAlbumPhotos, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Integer> upload() {
        return new Task<Integer>() {
            @Override
            protected void call() {
                try {
                    int size = Bimp.tempSelectBitmap.size();
                    if (size == 0) {
                        onError(Res.getString(R.string.me_not_yet_select_image));
                        return;
                    }
                    for (int i = 0; i < size; i++) {
                        ImageItem item = Bimp.tempSelectBitmap.get(i);
                        File temp = BitmapUtil.createUploadTempFile(item.file, "upload_album_photo_temp");

                        onMessage((i + 1), 0, "正在上传第" + (i + 1) + "张图");
                        JSONObject json = new JSONObject();
                        HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.userUploadAlbumPhoto, json, temp);
                        if (response.error != null) {
                            onError(response.error);
                            return;
                        }
                    }
                    onSuccess(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<ImageItem>> getImageItemList() {
        return new Task<ArrayList<ImageItem>>() {
            @Override
            protected void call() {
                ArrayList<ImageItem> list = new ArrayList<>();
                String columns[] = new String[]{
                        MediaStore.Images.Media.DATA, //图片路径
                        MediaStore.Images.Media.DATE_TAKEN //创建日期
                };
                Cursor cursor = null;
                try {
                    cursor = MediaStore.Images.Media.query(MyApplication.getContext().getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            , columns, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
                    if (cursor == null) {
                        throw new NullPointerException("cursor is null");
                    }
                    while (cursor.moveToNext()) {
                        int photoPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String path = cursor.getString(photoPathIndex);
                        File file = new File(path);
                        if (file.exists() && file.length() > 0) {
                            ImageItem imageItem = new ImageItem();
                            imageItem.imagePath = path;
                            imageItem.file = file;
                            list.add(imageItem);
                        }
                    }
                    onSuccess(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        };
    }

    public static Task<ArrayList<ImageBucket>> getImageBucketList() {
        return new Task<ArrayList<ImageBucket>>() {
            @Override
            protected void call() {
                ArrayList<ImageBucket> list = new ArrayList<>();
                SparseArray<ImageBucket> array = new SparseArray<>();
                String columns[] = new String[]{
                        MediaStore.Images.Media.DATA, //图片路径
                        MediaStore.Images.Media.BUCKET_ID, //文件夹ID
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME, //文件夹名称
                        MediaStore.Images.Media.DATE_TAKEN //创建日期
                };
                Cursor cursor = null;
                try {
                    cursor = MediaStore.Images.Media.query(MyApplication.getContext().getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            , columns, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
                    if (cursor == null) {
                        throw new NullPointerException("cursor is null");
                    }
                    while (cursor.moveToNext()) {
                        int photoPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        int bucketIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                        int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                        String path = cursor.getString(photoPathIndex);
                        int bucketId = cursor.getInt(bucketIdIndex);
                        String bucketName = cursor.getString(bucketDisplayNameIndex);

                        File file = new File(path);
                        if (file.exists() && file.length() > 0) {
                            ImageBucket bucket = array.get(bucketId);
                            if (bucket == null) {
                                bucket = new ImageBucket();
                                bucket.bucketName = bucketName;
                                array.put(bucketId, bucket);
                                list.add(bucket);
                            }
                            ImageItem imageItem = new ImageItem();
                            imageItem.imagePath = path;
                            imageItem.file = file;
                            bucket.imageList.add(imageItem);
                        }
                    }
                    onSuccess(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        };
    }


}
