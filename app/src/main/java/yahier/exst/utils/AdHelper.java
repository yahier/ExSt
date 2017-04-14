package yahier.exst.utils;

import android.content.Context;
import android.content.Intent;

import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.ApplyAdCooperateAct;
import com.stbl.stbl.act.ad.CommonResultAct;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ad.AdGoodsItem;
import com.stbl.stbl.item.ad.AdOrderItem;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;

/**
 * 广告辅助工具类
 * Created by Administrator on 2016/10/3 0003.
 */

public class AdHelper {

    /**
     * 读取是否有新返利信息
     */
    public static boolean isShowRebate() {
        return (boolean) SharedPrefUtils.getFromPublicFile(KEY.isShowRebate+SharedToken.getUserId(), false);
    }

    /**
     * 记录是否有新返利信息
     */
    public static void setShowRebate(boolean isShowRebate) {
        SharedPrefUtils.putToPublicFile(KEY.isShowRebate+SharedToken.getUserId(), isShowRebate);
    }

    /**
     * 读取是否有新商务合作信息
     */
    public static boolean isShowCooperater() {
        return (boolean) SharedPrefUtils.getFromPublicFile(KEY.isShowCooperater+SharedToken.getUserId(), false);
    }

    /**
     * 记录是否有新商务合作信息
     */
    public static void setShowCooperater(boolean isShowCooperater) {
        SharedPrefUtils.putToPublicFile(KEY.isShowCooperater+SharedToken.getUserId(), isShowCooperater);
    }

    /**
     * 记录是否有新徒弟
     */
    public static void setShowNewStudent(boolean isShowNewStudent) {
        SharedPrefUtils.putToPublicFile(KEY.isShowNewStudent+SharedToken.getUserId(), isShowNewStudent);
    }


    /**
     * 读取是否有新徒弟
     */
    public static boolean isShowNewStudent() {
        return (boolean) SharedPrefUtils.getFromPublicFile(KEY.isShowNewStudent+SharedToken.getUserId(), false);
    }


    /**
     * 判断是否自己，不是自己才可以跳到申请商务合作界面
     */
    public static void toApplyAdCooperateAct(final Ad ad, final Context context) {
        if (ad == null || context == null) return;
        if (ad.user != null) {
            if (String.valueOf(ad.user.getUserid()).equals(SharedToken.getUserId())) {
                ToastUtil.showToast(context.getString(R.string.ad_apply_self_cooperater_err));
                return;
            }
        }
        WaitingDialog.show(context, R.string.waiting);
        Params params = new Params();
        params.put("adid", ad.adid);//Statuses.requestCount
        new HttpEntity(context).commonPostData(Method.adBusinessApplyLeftTimes, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                WaitingDialog.dismiss();
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {

                    switch (methodName) {
                        case Method.adBusinessApplyLeftTimes:
                            if (item.getIssuccess() == BaseItem.errorNoTaostTag) {
                                break;
                            }
                            ToastUtil.showToast(item.getErr().getMsg());
                            break;
                    }
                    return;
                }
                switch (methodName) {
                    case Method.adBusinessApplyLeftTimes:
                        Intent intent = new Intent(context, ApplyAdCooperateAct.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ad", ad);
                        intent.putExtra("checkJson", result);
                        context.startActivity(intent);
                        break;
                }
            }
        });
    }
    //获取广告商品信息
    public static void getAdGoods(Context context, final OnGetGoodsListener listener){
        String jsonStr = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_goods,"");
        if (jsonStr != null && !jsonStr.equals("")){
            AdGoodsItem goodsItem = JSONHelper.getObject(jsonStr,AdGoodsItem.class);
            if (listener != null) listener.onSuccess(goodsItem);
            return;
        }
        Params params = new Params();
        new HttpEntity(context).commonPostData(Method.adsysGoodsGet, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                LogUtil.logE("LogUtil",methodName +"--"+result);
                BaseItem item = JSONHelper.getObject(result,BaseItem.class);
                if (item == null) return;
                if (item.getIssuccess() != BaseItem.successTag) {
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
//                ToastUtil.showToast(item.getErr().getMsg());
                        if (listener != null ) listener.onError();
                        return;
                    }
                }
                String json = JSONHelper.getStringFromObject(item.getResult());
                switch (methodName){
                    case Method.adsysGoodsGet: //查询商品信息
                        AdGoodsItem goodsItem = JSONHelper.getObject(json,AdGoodsItem.class);
                        if (goodsItem != null) SharedPrefUtils.putToPublicFile(KEY.adsys_goods,json);
                        if (listener != null) listener.onSuccess(goodsItem);
                        break;
                }
            }
        });
    }

    public interface OnGetGoodsListener{
        void onSuccess(AdGoodsItem item);
        void onError();
    }
}
