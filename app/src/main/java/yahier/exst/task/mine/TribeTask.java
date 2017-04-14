package yahier.exst.task.mine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.item.TribeAllItem;
import com.stbl.stbl.model.TribeAd;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/27.
 */

public class TribeTask {

    public static Task<HashMap<String, Object>> getTribeAllData(final long objuserid, final int page, final int count) {
        return new Task<HashMap<String, Object>>() {
            @Override
            protected void call() {
                HashMap<String, Object> map = new HashMap<>();
                try {
                    boolean hasResult = false;
                    TaskError err = null;
                    JSONObject json = new JSONObject();
                    json.put("objuserid", objuserid);
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetTribeInfo, json);
                    if (response.error == null) {
                        TribeAllItem tribe = JSON.parseObject(response.result, TribeAllItem.class);
                        if (tribe != null) {
                            map.put("tribe", tribe);
                        }
                        hasResult = true;
                    } else {
                        err = response.error;
                    }
                    json = new JSONObject();
                    json.put("objuserid", objuserid);
                    json.put("page", page);
                    json.put("count", count);
                    response = OkHttpHelper.getInstance().post(Method.userGetTribeAdList, json);
                    if (response.error == null) {
                        TribeAd ad = JSON.parseObject(response.result, TribeAd.class);
                        if (ad != null) {
                            map.put("ad", ad);
                        }
                        hasResult = true;
                    }
                    if (hasResult) {
                        onSuccess(map);
                    } else {
                        onError(err);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<TribeAd> getTribeAd(final long objuserid, final int page, final int count, final int sortno) {
        return new Task<TribeAd>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                if (page > 1)
                    json.put("sortno", sortno);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetTribeAdList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    TribeAd ad = JSON.parseObject(response.result, TribeAd.class);
                    if (ad != null) {
                        onSuccess(ad);
                    } else {
                        onError(Res.getString(R.string.me_data_error));
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

}
