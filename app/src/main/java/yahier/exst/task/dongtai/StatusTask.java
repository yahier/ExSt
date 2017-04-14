package yahier.exst.task.dongtai;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.model.ShoppingStatus;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tnitf on 2016/7/25.
 */
public class StatusTask {

    public static Task<Statuses> publishShortStatus(final int statusestype, final String content, final int imgcount, final long attachid, final int linktype, final String linkid) {
        return new Task<Statuses>() {
            @Override
            protected void call() {
                try {
                    if (Bimp.tempSelectBitmap.size() > 0) {
                        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                            ImageItem item = Bimp.tempSelectBitmap.get(i);
                            File temp = BitmapUtil.createUploadTempFile(item.file, "publish_short_status_temp");

                            JSONObject json = new JSONObject();
                            json.put("attachid", attachid);
                            json.put("index", i);
                            onMessage((i + 1), 0, "正在上传第" + (i + 1) + "张图");
                            HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.weiboUploadImg, json, temp);
                            if (response.error != null) {
                                onError(response.error);
                                return;
                            }
                        }
                    }
                    JSONObject json = new JSONObject();
                    json.put("statusestype", statusestype);
                    json.put("content", content);
                    json.put("imgcount", imgcount);
                    json.put("attachid", attachid);
                    if (linktype != 0 && !TextUtils.isEmpty(linkid)) {
                        json.put("linktype", linktype);
                        json.put("linkid", linkid);
                    }
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboPush, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Statuses data = JSON.parseObject(response.result, Statuses.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<String> getShareUrl(final String mi, final String bi) {
        return new Task<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("mi", mi);
                json.put("bi", bi);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.common_share_url, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    String data = response.result;
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<ShoppingStatus> publishShoppingCircle(final String content, final int imgcount, final long attachid, final int linktype, final String linkid,
                                                             final String rpid, final String rpamount, final String rptype) {
        return new Task<ShoppingStatus>() {
            @Override
            protected void call() {
                try {
                    if (Bimp.tempSelectBitmap.size() > 0) {
                        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                            ImageItem item = Bimp.tempSelectBitmap.get(i);
                            File temp = BitmapUtil.createUploadTempFile(item.file, "publish_shopping_circle_temp");

                            JSONObject json = new JSONObject();
                            json.put("attachid", attachid);
                            json.put("index", i);
                            onMessage((i + 1), 0, "正在上传第" + (i + 1) + "张图");
                            HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.shoppingCircleUpload, json, temp);
                            if (response.error != null) {
                                onError(response.error);
                                return;
                            }
                        }
                    }
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    json.put("imgcount", imgcount);
                    json.put("attachid", attachid);
                    if (linktype != 0 && !TextUtils.isEmpty(linkid)) {
                        json.put("linktype", linktype);
                        json.put("linkid", linkid);
                    }
                    json.put("rpid", rpid);
                    json.put("rpamount", rpamount);
                    json.put("rptype", rptype);
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.shoppingCircieCreate, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ShoppingStatus data = JSON.parseObject(response.result, ShoppingStatus.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<ShoppingStatus> publishRedPacketStatus(final String content, final int linktype, final String linkid, final JSONArray imgarr) {
        return new Task<ShoppingStatus>() {
            @Override
            protected void call() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("content", content);
                    if (linktype != 0 && !TextUtils.isEmpty(linkid)) {
                        json.put("linktype", linktype);
                        json.put("linkid", linkid);
                    }
                    json.put("imgarr", imgarr);
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.adsysV2SquareAdd, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ShoppingStatus data = JSON.parseObject(response.result, ShoppingStatus.class);
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

    //获取动态默认文案
    public static Task<String> getDynamicDFcontent() {
        return new Task<String>() {
            @Override
            protected void call() {
                try {
                    JSONObject json = new JSONObject();
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getDynamicDFcontent, json);
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
}
