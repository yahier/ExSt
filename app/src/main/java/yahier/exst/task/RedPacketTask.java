package yahier.exst.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.item.ad.YunHongbaoData;
import com.stbl.stbl.model.RedpacketOrder;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;

import java.io.IOException;

/**
 * Created by Administrator on 2016/9/29.
 */

public class RedPacketTask {

    public static Task<YunHongbaoData> getHongBaoWallet(final String ID) {
        return new Task<YunHongbaoData>() {
            @Override
            protected void call() {
                try {
                    HttpResponse response = OkHttpHelper.getInstance().redPacketGet(Method.getHongbaoDetailByYunZhangHu +
                            "?ID=" + ID);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    YunHongbaoData item = JSON.parseObject(response.result, YunHongbaoData.class);
                    if (item != null) {
                        onSuccess(item);
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

    public static Task<RedpacketOrder> createRedpacketOrder(final String buid, final int redpackettype,
                                                            final int redpacketsize, final double redpacketmoney,
                                                            final String redpacketmsg, final int paytype) {
        return new Task<RedpacketOrder>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("buid", buid);
                json.put("redpackettype", redpackettype);
                json.put("redpacketsize", redpacketsize);
                json.put("redpacketmoney", redpacketmoney);
                json.put("redpacketmsg", redpacketmsg);
                json.put("paytype", paytype);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Config.hostMainRedPacket, Method.redpacketSquareCreate, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    RedpacketOrder order = JSON.parseObject(response.result, RedpacketOrder.class);
                    if (order == null) {
                        onError(Res.getString(R.string.me_data_error));
                        return;
                    }
                    onSuccess(order);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Integer> payVerify(final String redpacketid, final int islast) {
        return new Task<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("redpacketid", redpacketid);
                json.put("islast", islast);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Config.hostMainRedPacket, Method.redpacketPayVertify, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    JSONObject obj = JSON.parseObject(response.result);
                    if (obj.containsKey("paystatus")) {
                        int paystatus = obj.getIntValue("paystatus");
                        onSuccess(paystatus);
                        return;
                    }
                    onError(Res.getString(R.string.me_data_error));
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

}
