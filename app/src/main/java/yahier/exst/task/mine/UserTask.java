package yahier.exst.task.mine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.item.AttendCon;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.Level;
import com.stbl.stbl.item.TudiItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserSign;
import com.stbl.stbl.item.UserTag;
import com.stbl.stbl.model.TinyTribeInfo;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SimpleTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/19.
 */
public class UserTask {

    public static SimpleTask<String> getQrcodeUrl(final long objuserid) {
        return new SimpleTask<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                if (objuserid != 0) {
                    json.put("objuserid", objuserid);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getUserQrcode, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    String url = JSONObject.parseObject(response.result).getString("qrimgurl");
                    if (url != null) {
                        onCompleted(url);
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


    public static SimpleTask<String> getQrcodeInfoUrl(final long objuserid) {
        return new SimpleTask<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                if (objuserid != 0) {
                    json.put("objuserid", objuserid);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getUserQrInfocode, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }

                    String url = response.result;
                    if (url != null) {
                        onCompleted(url);
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

    public static SimpleTask<String> getVoiceUrl(final long userid) {
        return new SimpleTask<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                if (userid != 0) {
                    json.put("userid", userid);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getUserVoice, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    String url = JSONObject.parseObject(response.result).getString("phonetic");
                    if (url != null) {
                        onCompleted(url);
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

    public static SimpleTask<ArrayList<UserSign>> getSignUserList(
            final long target_userid, final int page, final int count) {
        return new SimpleTask<ArrayList<UserSign>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userSignUserList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<UserSign> dataList = (ArrayList<UserSign>) JSONArray.parseArray(response.result, UserSign.class);
                    if (dataList != null) {
                        onCompleted(dataList);
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

    public static SimpleTask<ArrayList<TudiItem>> getStudentList(
            final long objuserid, final int page, final int count) {
        return new SimpleTask<ArrayList<TudiItem>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetTudis, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<TudiItem> dataList = (ArrayList<TudiItem>) JSONArray.parseArray(response.result, TudiItem.class);
                    if (dataList != null) {
                        onCompleted(dataList);
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

    public static SimpleTask<ArrayList<AttendCon>> getAttendList(
            final long objuserid, final int page, final int count) {
        return new SimpleTask<ArrayList<AttendCon>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetAttendList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<AttendCon> dataList = (ArrayList<AttendCon>) JSONArray.parseArray(response.result, AttendCon.class);
                    if (dataList != null) {
                        onCompleted(dataList);
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

    public static SimpleTask<ArrayList<AttendCon>> getFansList(
            final long objuserid, final int page, final int count) {
        return new SimpleTask<ArrayList<AttendCon>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetFans, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<AttendCon> dataList = (ArrayList<AttendCon>) JSONArray.parseArray(response.result, AttendCon.class);
                    if (dataList != null) {
                        onCompleted(dataList);
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

    public static SimpleTask<Integer> addRelation(final long touserid, final String msg, final int applytype) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("touserid", touserid);
                json.put("msg", msg);
                json.put("applytype", applytype);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.imAddRelation, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<ImgUrl> uploadHead(final String path) {
        return new SimpleTask<ImgUrl>() {
            @Override
            protected void call() {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    options.inSampleSize = BitmapUtil.computeScale(options, 600, 600);
                    options.inJustDecodeBounds = false;
                    Bitmap bmp = BitmapFactory.decodeFile(path, options);
                    FileUtils.saveBitmap(path, bmp);

                    SystemClock.sleep(200);

                    HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.userUploadHeadImg, path);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ImgUrl data = JSONObject.parseObject(response.result, ImgUrl.class);
                    if (data != null) {
                        onCompleted(data);
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

    public static Task<String> uploadRecord(final String path) {
        return new Task<String>() {
            @Override
            protected void call() {
                try {
                    SystemClock.sleep(1000);
                    HttpResponse response = OkHttpHelper.getInstance().uploadFile(Method.uploadSuperCardVoice, path);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    String data = response.result;
                    if (data != null) {
                        onSuccess(data);
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

    public static SimpleTask<Long> focus(final long target_userid) {
        return new SimpleTask<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userFollow, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(target_userid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<TinyTribeInfo> getTinyTribeInfo(final long objuserid) {
        return new Task<TinyTribeInfo>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userTinyTribeInfo, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    TinyTribeInfo data = JSON.parseObject(response.result, TinyTribeInfo.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<Long> shield(final long target_userid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userIgnore, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(target_userid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Long> unshield(final long target_userid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userCancelgnore, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(target_userid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Long> conceal(final long target_userid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.statusesNotSee, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(target_userid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Long> unconceal(final long target_userid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.statusesYesSee, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(target_userid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Long> deleteFriend(final long touserid, final int breaktype) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("touserid", touserid);
                json.put("breaktype", breaktype);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.imDeleteRelation, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(touserid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Long> unfollow(final long target_userid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("target_userid", target_userid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userCancelFollow, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(target_userid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<UserItem> getMyInfo() {
        return new Task<UserItem>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userLoginInfo, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    UserItem data = JSON.parseObject(response.result, UserItem.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<UserTag> getUserTag() {
        return new Task<UserTag>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getMyTags, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    UserTag data = JSON.parseObject(response.result, UserTag.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<Level> getUserLevel() {
        return new Task<Level>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetLevel, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Level data = JSON.parseObject(response.result, Level.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<Boolean> updateInfo(final JSONObject json) {
        return new Task<Boolean>() {
            @Override
            protected void call() {
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userUpdate, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ImgUrl> updateHead(final File file) {
        return new Task<ImgUrl>() {
            @Override
            protected void call() {
                try {
                    File temp = BitmapUtil.createUploadTempFile(file, "update_head_temp", Config.MAX_HEAD_WIDTH, Config.MAX_HEAD_HEIGHT);
                    JSONObject json = new JSONObject();
                    HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.userUploadHeadImg, json, temp);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ImgUrl data = JSON.parseObject(response.result, ImgUrl.class);
                    if (data != null) {
                        onSuccess(data);
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


}
