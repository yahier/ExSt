package yahier.exst.task.mine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.model.FootVisitor;
import com.stbl.stbl.model.Footprint;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/20.
 */
public class VisitTrackTask {

    public static SimpleTask<ArrayList<Footprint>> getMyTrackList(final int page, final int count) {
        return new SimpleTask<ArrayList<Footprint>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.mine_foot1, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Footprint> dataList = (ArrayList<Footprint>) JSON.parseArray(response.result, Footprint.class);
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

    public static SimpleTask<ArrayList<FootVisitor>> getMyVisitorList(final int page, final int count) {
        return new SimpleTask<ArrayList<FootVisitor>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.mine_foot2, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<FootVisitor> dataList = (ArrayList<FootVisitor>) JSON.parseArray(response.result, FootVisitor.class);
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

}
