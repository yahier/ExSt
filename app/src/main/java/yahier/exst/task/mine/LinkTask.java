package yahier.exst.task.mine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.model.PhotoUploadResult;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SimpleTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/23.
 */
public class LinkTask {

    public static SimpleTask<ArrayList<LinkBean>> getLinkList(final long objuserid, final int page, final int count) {
        return new SimpleTask<ArrayList<LinkBean>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.mine_links_show, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<LinkBean> dataList = (ArrayList<LinkBean>) JSON.parseArray(response.result, LinkBean.class);
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

    public static SimpleTask<Integer> delete(final JSONArray linkids) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("linkids", linkids);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.mine_links_del, json);
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

    public static Task<ArrayList<LinkBean>> getListData(final long objuserid, final int page, final int count) {
        return new Task<ArrayList<LinkBean>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.mine_links_show, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<LinkBean> dataList = (ArrayList<LinkBean>) JSON.parseArray(response.result, LinkBean.class);
                    if (dataList != null) {
                        onSuccess(dataList);
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

    public static Task<LinkBean> addLink(final String linktitle, final String linkurl, final File file) {
        return new Task<LinkBean>() {
            @Override
            protected void call() {
                try {
                    File temp = BitmapUtil.createUploadTempFile(file, "add_link_temp");

                    JSONObject json = new JSONObject();
                    HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.mine_link_img_upload, json, temp);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    PhotoUploadResult photo = JSON.parseObject(response.result, PhotoUploadResult.class);
                    if (photo == null) {
                        onError("图片上传失败");
                        return;
                    }
                    String picurl = photo.filename;
                    json = new JSONObject();
                    json.put("picurl", picurl);
                    json.put("linktitle", linktitle);
                    json.put("linkurl", linkurl);

                    response = OkHttpHelper.getInstance().post(Method.mine_link_create, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    LinkBean data = JSON.parseObject(response.result, LinkBean.class);
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

}
