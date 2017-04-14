package yahier.exst.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.ad.ShoppingCircleComment;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/29.
 */

public class shoppingCircleTask {

    public static Task<ShoppingCircleDetail> getDetail(final String statusesid) {
        return new Task<ShoppingCircleDetail>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("squareid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().postRedpacket(Method.getV2ShoppingCircieDetail, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ShoppingCircleDetail data = JSON.parseObject(response.result, ShoppingCircleDetail.class);
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

    public static Task<ArrayList<ShoppingCircleComment>> getCommentList(final String statusesid, final int lastid, final int count) {
        return new Task<ArrayList<ShoppingCircleComment>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("squareid", statusesid);
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getShoppingCircleCommentList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<ShoppingCircleComment> dataList = (ArrayList<ShoppingCircleComment>) JSON.parseArray(response.result, ShoppingCircleComment.class);
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

    public static Task<ShoppingCircleComment> addComment(final String statusesid, final String content, final long commentid) {
        return new Task<ShoppingCircleComment>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("squareid", statusesid);
                json.put("content", content);
                if (commentid > 0) {
                    json.put("commentid", commentid);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.addShoppingCircleComment, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ShoppingCircleComment data = JSON.parseObject(response.result, ShoppingCircleComment.class);
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

    public static Task<Long> deleteComment(final long commentid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("commentid", commentid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.deleteShoppingCircleComment, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(commentid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }
}
