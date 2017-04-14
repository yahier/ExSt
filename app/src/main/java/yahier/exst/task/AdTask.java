package yahier.exst.task;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ad.AdSub;
import com.stbl.stbl.model.MoneyFlowItem;
import com.stbl.stbl.model.UserAd;
import com.stbl.stbl.model.UserCard;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/22.
 */

public class AdTask {


    public static Task<AdSub> getAdList(final String selecttype, final int page, final int count, final int sortno) {
        return new Task<AdSub>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("selecttype", selecttype);
                json.put("page", page);
                json.put("count", count);
                if (page > 1) {
                    json.put("sortno", sortno);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetBrandAdList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }

                    //ArrayList<Ad> list = (ArrayList<Ad>) JSON.parseArray(response.result, Ad.class);
                    AdSub ad = JSON.parseObject(response.result, AdSub.class);
                    //ArrayList<Ad> list = ad.getAdview();
                    if (ad != null) {
                        onSuccess(ad);
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

    public static Task<ArrayList<MoneyFlowItem>> getFanliDetailList(final int page, final int count) {
        return new Task<ArrayList<MoneyFlowItem>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getFanliDetail, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<MoneyFlowItem> list = (ArrayList<MoneyFlowItem>) JSON.parseArray(response.result, MoneyFlowItem.class);
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

    /**
     * 是否广告主
     */
    public static void getIsAdvertiser(final Context context) {
        new HttpEntity(context).commonPostData(Method.adsysIsAderGet, new Params(), new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                LogUtil.logE("AdTask", methodName + "------" + result);
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                        if (item.getErr() != null)
                        ToastUtil.showToast(context, item.getErr().getMsg());
                    }
                    return;
                }
                String obj = JSONHelper.getStringFromObject(item.getResult());
                switch (methodName) {
                    case Method.adsysIsAderGet:
                        //{"result":{"isader":0},"issuccess":1}
                        JSONObject json = JSONObject.parseObject(obj);
                        if (json != null && json.containsKey("isader")) {
                            int isader = json.getInteger("isader");
                            {
                                SharedPrefUtils.putToPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), isader);
                                //LogUtil.logE("保存","isader:"+isader);
                            }
                        }
                        break;
                }
            }
        });
    }

    public static Task<UserAd> getAdInfo() {
        return new Task<UserAd>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetAdInfo, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    UserAd data = JSON.parseObject(response.result, UserAd.class);
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

    public static Task<ArrayList<MoneyFlowItem>> getWithdrawDetailList(final int page, final int count) {
        return new Task<ArrayList<MoneyFlowItem>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getWithdrawDetail, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<MoneyFlowItem> list = (ArrayList<MoneyFlowItem>) JSON.parseArray(response.result, MoneyFlowItem.class);
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

    public static Task<UserCard> getUserCard(final long objuserid) {
        return new Task<UserCard>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("objuserid", objuserid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userCardQuery, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    UserCard data = JSON.parseObject(response.result, UserCard.class);
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
