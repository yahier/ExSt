package yahier.exst.task.mine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/12.
 */
public class GiftTask {

    public static SimpleTask<ArrayList<MineGiftItem>> getGiftList(final int selecttype, final long objuserid, final int page, final int count) {
        return new SimpleTask<ArrayList<MineGiftItem>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("selecttype", selecttype);
                json.put("objuserid", objuserid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetMineGift, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<MineGiftItem> dataList = (ArrayList<MineGiftItem>) JSON.parseArray(response.result, MineGiftItem.class);
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
