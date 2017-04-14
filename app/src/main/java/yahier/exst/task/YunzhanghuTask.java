package yahier.exst.task;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.ThreadPool;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.yunzhanghu.redpacketsdk.RPRefreshSignListener;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.bean.TokenData;

import java.io.IOException;

/**
 * Created by Administrator on 2016/9/23 0023.
 * 获取云账户token所需签名
 */

public class YunzhanghuTask {

    public static void setRefreshSignListener() {
        final String userid = SharedToken.getUserId();
        //这段代码必须执行，否则云账户sdk会报错
        RedPacket.getInstance().setRefreshSignListener(new RPRefreshSignListener() {
            @Override
            public void onRefreshSign(RPValueCallback<TokenData> callback) {
                String authPartner = (String) SharedPrefUtils.getFromPublicFile(KEY.PARTNER, "");
                String timestamp = (String) SharedPrefUtils.getFromPublicFile(KEY.TIMESTAMP, "");
                String authSign = (String) SharedPrefUtils.getFromPublicFile(KEY.SIGN, "");
                //获取token的信息,在空时候去服务端获取
                if (TextUtils.isEmpty(authPartner) || TextUtils.isEmpty(timestamp) || TextUtils.isEmpty(authSign)) {
                    getYunzhanghuSign(SharedToken.getUserId());
                }
                // TokenData中的四个参数需要开发者向自己的Server获取
                TokenData tokenData = new TokenData();
                tokenData.authPartner = TextUtils.isEmpty(authPartner) ? "" : authPartner; //商户代码
                tokenData.appUserId = userid;  //商户方用户唯一标识
                tokenData.timestamp = TextUtils.isEmpty(timestamp) ? "" : timestamp; //签名使用的时间戳
                tokenData.authSign = TextUtils.isEmpty(authSign) ? "" : authSign; //签名
                // 请求签名参数成功后 设置回调方法 传入获取后的TokenData
                callback.onSuccess(tokenData);
            }
        });
    }

    /**
     * 获取云账户所需签名
     */
    public static void getYunzhanghuSign(final String userid) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                json.put("signuserid", userid);
                json.put("isautoreg", "1");
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.redpacketAuthSignGet, json);
                    if (response.error != null) {
                        return;
                    }
                    LogUtil.logE("LogUtil", "redpacket sign result ----" + response.result);
                    final JSONObject jsonobj = JSONObject.parseObject(response.result);

                    if (jsonobj != null) {
                        if (jsonobj.containsKey("sign")) {
                            String authSign = jsonobj.getString("sign");
                            SharedPrefUtils.putToPublicFile(KEY.SIGN, authSign);
                        }
                        if (jsonobj.containsKey("timestamp")) {
                            String timestamp = jsonobj.getString("timestamp");
                            SharedPrefUtils.putToPublicFile(KEY.TIMESTAMP, timestamp);
                        }
                        if (jsonobj.containsKey("partner")) {
                            String authPartner = jsonobj.getString("partner");
                            SharedPrefUtils.putToPublicFile(KEY.PARTNER, authPartner);
                        }
                        getYunZhangHuToken();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void getYunZhangHuToken() {
        LogUtil.logE("YunzhanghuTask", "getYunZhangHuToken");
        //String autoRegisterNot = "0";//表示不自动注册
        String autoRegisterYes = "1";//表示自动注册
        JSONObject params = new JSONObject();
        params.put("partner", (String) SharedPrefUtils.getFromPublicFile(KEY.PARTNER, ""));
        params.put("user_id", SharedToken.getUserId());
        params.put("timestamp", (String) SharedPrefUtils.getFromPublicFile(KEY.TIMESTAMP, ""));
        params.put("reg_hongbao_user", autoRegisterYes);
        params.put("sign", (String) SharedPrefUtils.getFromPublicFile(KEY.SIGN, ""));
        try {
            HttpResponse response = OkHttpHelper.getInstance().postYunZhangHu(Method.getTokenByYunZhangHu, params);
            if (response.error != null) {
                return;
            }
            LogUtil.logE("YunzhanghuTask", "getYunZhangHuToken result ----" + response.result);
            final JSONObject jsonobj = JSONObject.parseObject(response.result);

            if (jsonobj != null && jsonobj.getString("code").equals("0000")) {
                if (jsonobj.containsKey("data")) {
                    String token = jsonobj.getJSONObject("data").getString("token");
                    LogUtil.logE("YunzhanghuTask", "存入的token是:" + token);
                    SharedPrefUtils.putToPublicFile(KEY.yunZhangHuToken, token);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
