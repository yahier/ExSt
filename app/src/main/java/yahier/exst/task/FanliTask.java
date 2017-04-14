package yahier.exst.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.model.WithdrawAccount;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/29.
 */

public class FanliTask {

    public static Task<ArrayList<WithdrawAccount>> getWithdrawAccountList() {
        return new Task<ArrayList<WithdrawAccount>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getWithdrawAccountList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<WithdrawAccount> list = (ArrayList<WithdrawAccount>) JSON.parseArray(response.result, WithdrawAccount.class);
                    if (list != null) {
                        onSuccess(list);
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

    public static Task<String> bindWithdrawAccount(final int accounttype, final String nickname, final String iconurl,
                                                   final String realname, final String idcard, final String openid,
                                                   final String unionid, final String paypwd, final ImageItem frontImg, final ImageItem backImg) {
        return new Task<String>() {

            private String uploadImg(ImageItem item, int index) throws IOException {
                File temp = BitmapUtil.createUploadTempFile(item.file, "bind_account_id_card_temp");
                JSONObject json = new JSONObject();
                json.put("type", "AdIdCard");
                onMessage(0, 0, "正在上传第" + index + "张图");
                HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.uploadAdImg, json, temp);
                if (response.error != null) {
                    onError(response.error);
                    return null;
                }
                ImgUrl imgUrl = JSON.parseObject(response.result, ImgUrl.class);
                return imgUrl.getFilename();
            }

            @Override
            protected void call() {
                try {
                    String picfront = uploadImg(frontImg, 1);
                    if (picfront == null) {
                        return;
                    }
                    String picback = uploadImg(backImg, 2);
                    if (picback == null) {
                        return;
                    }
                    JSONObject json = new JSONObject();
                    json.put("accounttype", accounttype);
                    json.put("nickname", nickname);
                    json.put("iconurl", iconurl);
                    json.put("realname", realname);
                    json.put("idcard", idcard);
                    json.put("picfront", picfront);
                    json.put("picback", picback);
                    json.put("openid", openid);
                    json.put("unionid", unionid);
                    json.put("paypwd", paypwd);
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bindWithdrawAccount, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(openid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    public static Task<String> unbindWithdrawAccount(final String bindingid, final String paypwd) {
        return new Task<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("bindingid", bindingid);
                json.put("paypwd", paypwd);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.unbindWithdrawAccount, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(bindingid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<String> applyWithdraw(final String withdrawamount, final String paypwd) {
        return new Task<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("withdrawamount", withdrawamount);
                json.put("paypwd", paypwd);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.applyWithdraw, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(withdrawamount);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }
    //查询用户提现账户状态
    public static Task<Integer> checkAccountStatus(final String openid, final int blltype) {
        return new Task<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                if (openid != null && !openid.equals(""))
                    json.put("openid", openid);
                json.put("blltype", blltype);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.redpacketWithdrawStatusCheck, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(0);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

}