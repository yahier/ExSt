package yahier.exst.task.mine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/5/7.
 */
public class CollectionTask {

    public static SimpleTask<ArrayList<Goods>> getCollectGoodsList(final int page, final int count) {
        return new SimpleTask<ArrayList<Goods>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getCollectedGoods, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Goods> dataList = (ArrayList<Goods>) JSON.parseArray(response.result, Goods.class);
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

    public static SimpleTask<ArrayList<StatusesCollect>> getCollectDongtaiList(final long lastid, final int count) {
        return new SimpleTask<ArrayList<StatusesCollect>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.statusesCollectList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<StatusesCollect> dataList = (ArrayList<StatusesCollect>) JSON.parseArray(response.result, StatusesCollect.class);
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

    public static SimpleTask<Long> deleteCollectGoods(final long goodsid) {
        return new SimpleTask<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("goodsid", goodsid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.goodsCollectOrCancel, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(goodsid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Long> deleteCollectDongtai(final long statusesid) {
        return new SimpleTask<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboCancelCollect, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(statusesid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<Goods>> getAddGoodsList(final int page, final int count) {
        return new Task<ArrayList<Goods>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getCollectedGoods, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Goods> dataList = (ArrayList<Goods>) JSON.parseArray(response.result, Goods.class);
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

    public static Task<Long> deleteAddGoods(final long goodsid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("goodsid", goodsid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.goodsCollectOrCancel, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(goodsid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

}
