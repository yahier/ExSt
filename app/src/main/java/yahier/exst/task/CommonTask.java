package yahier.exst.task;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.im.SharedGroups;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CommonDict;
import com.stbl.stbl.item.CommonDictAdsys;
import com.stbl.stbl.item.CommonDictDefaultImg;
import com.stbl.stbl.item.CommonDictModuleSwitch;
import com.stbl.stbl.item.CommonDictWebItem;
import com.stbl.stbl.item.EnterAd;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.OfficeAccount;
import com.stbl.stbl.item.ProvinceItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.CommonDictH5;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.RongErrorItem;
import com.stbl.stbl.item.redpacket.RedpacketSettingAll;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ErrorRecodeDB;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.MainHandler;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedAd;
import com.stbl.stbl.util.SharedOfficeAccount;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ThreadPool;
import com.stbl.stbl.util.database.DataCacheDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;

/**
 * Created by tnitf on 2016/3/29.
 */
public class CommonTask {

    public static void getCommonDicBackground() {

        final String lastUpdateTime = (String) SharedPrefUtils.getFromPublicFile(KEY.BigCommonDictUpdateTime, "0");
        //final String cache = (String) SharedPrefUtils.getFromPublicFile(KEY.BigCommonDict, "");
        LogUtil.logE(Method.common_dic,"lastUpdateTime -"+lastUpdateTime);
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                json.put("updatetime", lastUpdateTime);
                HttpResponse response = null;
                try {
                    response = OkHttpHelper.getInstance().post(Method.common_dic, json);
                } catch(Exception e) {
                    e.printStackTrace();
                    //point2与上面point1相配合。如果出现异常。清除更新时间,以便下次可以请求全部数据
                    SharedPrefUtils.putToPublicFile(KEY.BigCommonDictUpdateTime, "0");
                }
                if (response == null) {
                    return;
                }
                //当借口没有更新的数据返回时，取缓存
                if (response.error != null) {
                    LogUtil.logE(response.error.msg);
                    // parseDictJson(cache);
                    return;
                }

                if (response.result == null || response.result.trim().equals("")) {
                    return;
                }
                //更新时间。并将最新结果 加入缓存
                parseDictJson(response.result);
//                String currentTime = String.valueOf(System.currentTimeMillis() / 1000);
//                SharedPrefUtils.putToPublicFile(KEY.BigCommonDictUpdateTime, currentTime);
                SharedPrefUtils.putToPublicFile(KEY.BigCommonDict, response.result);

            }
        });

    }

    static void parseDictJson(String stringResult) {
        parse(stringResult);
        JSONObject result = JSONObject.parseObject(stringResult);
        if (result.containsKey("modifytime")) {
            String modifytime = String.valueOf(result.getLong("modifytime"));
            SharedPrefUtils.putToPublicFile(KEY.BigCommonDictUpdateTime, modifytime);
        }
        if (result.containsKey("user")) {
            JSONObject user = result.getJSONObject("user");
            //我的钱包充值金额
            if (user.containsKey("rechargejindou")) {
                String rechargejindou = user.getString("rechargejindou");
                SharedPrefUtils.putToPublicFile(KEY.rechargejindou, rechargejindou);
            }
        }

        //帮一帮我要找人赏金
        if (result.containsKey("bang")) {
            JSONObject bang = result.getJSONObject("bang");
            if (bang.containsKey("beandic")) {
                String beandic = bang.getString("beandic");
                SharedPrefUtils.putToPublicFile(KEY.bangbeandic, beandic);
            }
        }

        //广告
        if (result.containsKey("adsys")) {
            JSONObject adsys = result.getJSONObject("adsys");
            if (adsys.containsKey("withdrawlimit")) {
                Float withdrawlimit = adsys.getFloat("withdrawlimit");
                SharedPrefUtils.putToPublicFile(KEY.withdrawlimit, withdrawlimit);
            }
            //广告商务合作
            if (adsys.containsKey("adbusinessestype")) {
                String adbusinessestype = adsys.getString("adbusinessestype");
                SharedPrefUtils.putToPublicFile(KEY.adbusinessestype, adbusinessestype);
            }

            //广告投诉
            if (adsys.containsKey("adreporttype")) {
                String adreporttype = adsys.getString("adreporttype");
                SharedPrefUtils.putToPublicFile(KEY.adreporttype, adreporttype);
            }

        }


        //抢红包次数信息
        if (result.containsKey("redpacket")) {
            String redpacket = result.getString("redpacket");
            SharedPrefUtils.putToPublicFile(KEY.redpacket, redpacket);
        }


        //品牌+地区
        if (result.containsKey("brandplusoption")) {
            String brandplusoption = result.getString("brandplusoption");
            SharedPrefUtils.putToPublicFile(KEY.brandplusoption, brandplusoption);
        }


        //新的h5网页格式解析
        if (result.containsKey("h5")) {
            CommonDictH5 h5 = JSONHelper.getObject(result.getString("h5"), CommonDictH5.class);
            if (h5 != null && h5.getDic() != null) {
                List<CommonDictWebItem> list = h5.getDic();
                LogUtil.logE("commonTask h5", "size:" + list.size());
                for (int i = 0; i < list.size(); i++) {
                    CommonDictWebItem item = list.get(i);
                    SharedPrefUtils.putToPublicFile(item.getKey(), item.getValue());
                }

            }
        }

        //h5网页地址
//        if (result.containsKey("sysintrod")) {
//
//            JSONObject intro = result.getJSONObject("sysintrod");
//
//            String contacts_level_introd = intro.getString("contacts_level_introd");
//            String wealth_level_introd = intro.getString("wealth_level_introd");
//            String sign_instrod = intro.getString("sign_instrod");
//            String bang_instrod = intro.getString("bang_instrod");
//            String regis_instrod = intro.getString("regis_instrod");
//            String invitecode_instrod = intro.getString("invitecode_instrod");
//            String problem_instrod = intro.getString("problem_instrod");
//            String func_instrod = intro.getString("func_instrod");
//            String account_update = intro.getString("upgradeaccount_entrance");
//            String stticket_mall_instrod = intro.getString("stticket_mall_instrod");
//            String adsys_main_open = intro.getString("adsys_main_open");
//            String adsys_main_close = intro.getString("adsys_main_close");
//            String adsys_server_introduce = intro.getString("adsys_server_introduce");
//            String stbl_brand_protocol = intro.getString("stbl_brand_protocol");
//            String adsys_brandplus_introd = intro.getString("adsys_brandplus_introd");
//            String adsys_drains_introd = intro.getString("adsys_drains_introd");
//
//            SharedPrefUtils.putToPublicFile(KEY.contacts_level_introd, contacts_level_introd);
//            SharedPrefUtils.putToPublicFile(KEY.wealth_level_introd, wealth_level_introd);
//            SharedPrefUtils.putToPublicFile(KEY.sign_instrod, sign_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.bang_instrod, bang_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.regis_instrod, regis_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.invitecode_instrod, invitecode_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.problem_instrod, problem_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.func_instrod, func_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.account_update, account_update);
//
//            LogUtil.logE("h5网页地址 update",account_update);
//
//            SharedPrefUtils.putToPublicFile(KEY.stticket_mall_instrod, stticket_mall_instrod);
//            SharedPrefUtils.putToPublicFile(KEY.adsys_main_open, adsys_main_open);
//            SharedPrefUtils.putToPublicFile(KEY.adsys_main_close, adsys_main_close);
//            SharedPrefUtils.putToPublicFile(KEY.adsys_server_introduce, adsys_server_introduce);
//            SharedPrefUtils.putToPublicFile(KEY.stbl_brand_protocol, stbl_brand_protocol);
//            SharedPrefUtils.putToPublicFile(KEY.adsys_brandplus_introd, adsys_brandplus_introd);
//            SharedPrefUtils.putToPublicFile(KEY.adsys_drains_introd, adsys_drains_introd);
//        }

        //兑换率
        if (result.containsKey("currencyexchangerate")) {
            JSONObject rate = result.getJSONObject("currencyexchangerate");
            float yue2rmb = rate.getFloatValue("yue2rmb");
            float jindou2rmb = rate.getFloatValue("jindou2rmb");
            float jindou2yue = rate.getFloatValue("jindou2yue");
            float lvdou2rmb = rate.getFloatValue("lvdou2rmb");
            float lvdou2yue = rate.getFloatValue("lvdou2yue");
            float lvdou2jindou = rate.getFloatValue("lvdou2jindou");

            SharedPrefUtils.putToPublicFile(KEY.yue2rmb, yue2rmb);
            SharedPrefUtils.putToPublicFile(KEY.jindou2rmb, jindou2rmb);
            SharedPrefUtils.putToPublicFile(KEY.jindou2yue, jindou2yue);
            SharedPrefUtils.putToPublicFile(KEY.lvdou2rmb, lvdou2rmb);
            SharedPrefUtils.putToPublicFile(KEY.lvdou2yue, lvdou2yue);
            SharedPrefUtils.putToPublicFile(KEY.lvdou2jindou, lvdou2jindou);
        }
        if (result.containsKey("mall")) {
            JSONObject obj = result.getJSONObject("mall");
            //退货类型
            if (obj.containsKey("refundgoodstype")) {
                JSONArray arr = obj.getJSONArray("refundgoodstype");
                SharedPrefUtils.putToPublicFile(KEY.refundgoodstype, arr.toString());
            }
            //退款类型
            if (obj.containsKey("refundamounttype")) {
                JSONArray arr = obj.getJSONArray("refundamounttype");
                SharedPrefUtils.putToPublicFile(KEY.refundamounttype, arr.toString());
            }
        }

        if (result.containsKey("stticketmall")) {
            JSONObject obj = result.getJSONObject("stticketmall");
            SharedPrefUtils.putToPublicFile(KEY.productquerytype, obj.toString());
        }

        //小秘书
//                    if (result.containsKey("assistant")) {
//                        JSONObject obj = result.getJSONObject("assistant");
//                        String userId = String.valueOf(obj.getLong("userid"));
//                        String name = obj.getString("username");
//                        String iconUrl = obj.getString("iconurl");
//                        RongDB rong = new RongDB(MyApplication.getContext());
//                        rong.insertOrUpdate(RongDB.typeUser, userId, name, iconUrl, UserItem.certificationYes);
//                        SharedGroups.putXiaoMishuId(MyApplication.getContext(), userId);
//                    }

        //模板开关
        if (result.containsKey("moduleswitch")) {
            int moduleswitch = result.getIntValue("showhongbao");
            SharedGroups.putHongbaoSwitch(MyApplication.getContext(), moduleswitch);
            String str  = result.getString("moduleswitch");
            LogUtil.logE("modul",str);
            SharedPrefUtils.putToPublicFile(KEY.STBL_MODUL_SWITCH, str);
            CommonDictModuleSwitch module = JSONHelper.getObject(str, CommonDictModuleSwitch.class);
        }


        if (result.containsKey("domainwhitelist")) {
            String domainwhitelist = result.getString("domainwhitelist");
            SharedPrefUtils.putToPublicFile(KEY.domainwhitelist, domainwhitelist);
        }

    }


    //新的解析都放在这里
    static void parse(String result) {
        CommonDict common = JSONHelper.getObject(result, CommonDict.class);
        if (common == null) return;
        CommonDictDefaultImg defaultImg = common.getDefaultimg();
        SharedPrefUtils.putToPublicFile(KEY.defaultimgDiscussion, defaultImg.getDiscussion());
        SharedPrefUtils.putToPublicFile(KEY.defaultimgAssistant, defaultImg.getAssistant());
        SharedPrefUtils.putToPublicFile(KEY.defaultimgGroup, defaultImg.getGroup());
        SharedPrefUtils.putToPublicFile(KEY.defaultimgUser, defaultImg.getUser());
        LogUtil.logE("img:" + SharedPrefUtils.getFromPublicFile(KEY.defaultimgUser, ""));


//        CommonDictSysIntrod h5Urls = common.getSysintrod();
//        SharedPrefUtils.putToPublicFile(KEY.contacts_level_introd, h5Urls.getContacts_level_introd());
//        SharedPrefUtils.putToPublicFile(KEY.wealth_level_introd, h5Urls.getWealth_level_introd());
//        SharedPrefUtils.putToPublicFile(KEY.sign_instrod, h5Urls.getSign_instrod());
//        SharedPrefUtils.putToPublicFile(KEY.bang_instrod, h5Urls.getBang_instrod());
//        SharedPrefUtils.putToPublicFile(KEY.regis_instrod, h5Urls.getRegis_instrod());
//        SharedPrefUtils.putToPublicFile(KEY.invitecode_instrod, h5Urls.getInvitecode_instrod());
//        SharedPrefUtils.putToPublicFile(KEY.problem_instrod, h5Urls.getProblem_instrod());
//        SharedPrefUtils.putToPublicFile(KEY.func_instrod, h5Urls.getFunc_instrod());
//        SharedPrefUtils.putToPublicFile(KEY.account_update, h5Urls.getUpgradeaccount_entrance());


        CommonDictAdsys adsys = common.getAdsys();


        //ObjectOutputStream out =new ObjectOutputStream();

    }


    //获取红包配置数据
    public static void getRedpacketSetting() {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                HttpResponse response = null;
                try {
                    response = OkHttpHelper.getInstance().postRedpacket(Method.getRedpacketSetting, json);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if (response == null) {
                    return;
                }
                LogUtil.logE("getRedpacketSetting", response.result);
                SharedPrefUtils.putToPublicFile(KEY.RedpacketSetting, response.result);

                RedpacketSettingAll setting = SharedPrefUtils.getRedpacketSettingAll();
                if (setting != null)
                    LogUtil.logE("hello", setting.getConfig().getPaycallbackloadingtime());
                else
                    LogUtil.logE("hello", "setting is null");
            }
        });

    }

    public static SimpleTask<Integer> tipOff(final int type, final long referenceid, final String reason) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("type", type);
                json.put("referenceid", referenceid);
                json.put("reason", reason);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.report, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    //获取官方账号.并保存
    public static void getOfficeAccount() {

        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getOfficeAccount, json);
                    if (response.error != null || response.result == null) {
                        return;
                    }
                    List<OfficeAccount> list = JSONHelper.getList(response.result, OfficeAccount.class);
                    if (list != null && list.size() > 0) {
                        SharedOfficeAccount.deleteData();
                        RongDB rong = new RongDB(MyApplication.getContext());
                        for (int i = 0; i < list.size(); i++) {
                            OfficeAccount account = list.get(i);
                            rong.insert(new IMAccount(RongDB.typeUser, String.valueOf(account.getUserid()), account.getUsername(), account.getImgurl(), UserItem.certificationYes, ""));
                            SharedOfficeAccount.putOfficeAccount(String.valueOf(account.getUserid()));
                        }

                    }


                    //以下废弃
//                    OfficeAccount account = JSONHelper.getObject(response.result, OfficeAccount.class);
//                    User userAssistent = account.getAssistant();
//                    User userPayHelper = account.getPayhepler();
//                    //LogUtil.logE("getOfficeAccount username", account.getAssistant().getUsername());
//                    RongDB rong = new RongDB(MyApplication.getContext());
//                    //保存小秘书
//                    rong.insert(new IMAccount(RongDB.typeUser, String.valueOf(userAssistent.getUserid()), userAssistent.getUsername(), userAssistent.getIconurl(), UserItem.certificationYes, ""));
//                    SharedGroups.putXiaoMishuId(MyApplication.getContext(), String.valueOf(userAssistent.getUserid()));
//                    //保存支付助手
//                    rong.insert(new IMAccount(RongDB.typeUser, String.valueOf(userPayHelper.getUserid()), userPayHelper.getUsername(), userPayHelper.getIconurl(), UserItem.certificationYes, ""));
//                    SharedGroups.putPayHelper(MyApplication.getContext(), String.valueOf(userPayHelper.getUserid()));


                } catch(IOException e) {
                    e.printStackTrace();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static SimpleTask<ArrayList<Gift>> getGiftList() {
        return new SimpleTask<ArrayList<Gift>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetGiftList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Gift> dataList = (ArrayList<Gift>) JSON.parseArray(response.result, Gift.class);
                    if (dataList != null) {
                        onCompleted(dataList);
                    } else {
                        onError("数据返回错误");
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> sendGift(final long businessid, final int giftid, final String paypwd) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("businessid", businessid);
                json.put("giftid", giftid);
                if (!TextUtils.isEmpty(paypwd)) {
                    json.put("paypwd", paypwd);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.imSendGift, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<ArrayList<Banner>> getBannerList(final int moduletype) {
        return new SimpleTask<ArrayList<Banner>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("moduletype", moduletype);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.commonBannerQuery, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Banner> dataList = (ArrayList<Banner>) JSON.parseArray(response.result, Banner.class);
                    if (dataList != null) {
                        onCompleted(dataList);
                    } else {
                        onError("数据返回错误");
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    /**
     * 之前在MessageMain的onCreate和onEvent点击底部时调用.现在在Application的onCreate中调用
     *
     * @param grouptype
     * @param relationflag
     * @param hasself
     */
    public static void initFriendDB(final int grouptype, final int relationflag, final int hasself) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                json.put("grouptype", grouptype);
                if (relationflag != 0) {
                    json.put("relationflag", relationflag);
                }
                json.put("hasself", hasself);
                try {
                    //LogUtil.logE("getFriendList1 result", "开始请求");
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getAppContacts, json);
                    //LogUtil.logE("getFriendList1 result", response.result);
                    if (response.error != null) {
                        return;
                    }
                    final List<UserItem> userList = JSONArray.parseArray(response.result, UserItem.class);
                    if (userList == null) return;
                    //加好友数据库
//                    FriendsDB db = FriendsDB.getInstance(MyApplication.getInstance());
//                    db.deleteAllData();
//                    for (int i = 0; i < userList.size(); i++) {
//                        UserItem useritem = userList.get(i);
//                        db.insert(useritem);
//                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    //LogUtil.logE("getFriendList1 initFriendDB", "异常:"+e.getLocalizedMessage());

                }
            }
        });


    }

    public static void getLoginAd() {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getloginAd, json);
                    if (response.error != null) {
                        return;
                    }
                    EnterAd ad = JSONHelper.getObject(response.result, EnterAd.class);
                    if (ad != null) {
                        SharedAd.putAd(ad);
                        LocalBroadcastHelper.getInstance().send(new Intent(ACTION.SHOW_AD));
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void uploadIMSentError(final RongIM.SentMessageErrorCode sentMessageErrorCode, io.rong.imlib.model.Message message) {
        String rongToken = SharedToken.getRongyunToken(MyApplication.getContext());
        //组合item
        RongErrorItem item = new RongErrorItem();
        item.setConversationType(message.getConversationType().getName());
        item.setRongyunToken(rongToken);
        item.setErrorCode(sentMessageErrorCode.getValue());
        item.setErrorMessage(sentMessageErrorCode.getMessage());
        item.setMessageId(message.getMessageId());
        item.setTargetId(message.getTargetId());
        item.setObjectName(message.getObjectName());
        item.setSenderUserId(message.getSenderUserId());
        item.setSentTime(DateUtil.getTimeValue(message.getSentTime()));
        final String info = JSON.toJSONString(item);

        LogUtil.logE("uploadIMSentError info", info);
        uploadIMSentError(ErrorRecodeDB.typeIm, info);
//
//        ThreadPool.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject json = new JSONObject();
//                json.put("type", ErrorRecodeDB.typeIm);
//                json.put("msg", info);
//                try {
//                    HttpResponse response = OkHttpHelper.getInstance().post(Method.uploadMsgError, json);
//                    LogUtil.logE("result:" + response.result);
//                    ErrorRecodeDB db = new ErrorRecodeDB(MyApplication.getInstance());
//                    if (response.result.equals(String.valueOf(BaseItem.successTag))) {
//                        //pass
//                        db.delete(ErrorRecodeDB.typeIm, info);
//                    } else {
//                        //加入数据库
//                        db.insert(ErrorRecodeDB.typeIm, info);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


    public static void uploadIMSentError(final int type, final String info) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                json.put("type", ErrorRecodeDB.typeIm);
                json.put("msg", info);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.uploadMsgError, json);
                    LogUtil.logE("result:" + response.result);
                    ErrorRecodeDB db = new ErrorRecodeDB(MyApplication.getInstance());
                    if (response.result.equals(String.valueOf(BaseItem.successTag))) {
                        //pass
                        db.delete(ErrorRecodeDB.typeIm, info);
                    } else {
                        //加入数据库
                        db.insert(ErrorRecodeDB.typeIm, info);
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Task<String> getShareLink(final String mi, final String bi, final String ex) {
        return new Task<String>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                if (mi != null) {
                    json.put("mi", mi);
                }
                if (mi != null) {
                    json.put("bi", bi);
                }
                if (mi != null) {
                    json.put("ex", ex);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.common_share_url, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    String data = response.result;
                    if (data != null) {
                        onSuccess(data);
                    } else {
                        onError("数据返回错误");
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<ProvinceItem>> getCityList(final String updatetime) {
        return new Task<ArrayList<ProvinceItem>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("updatetime", updatetime);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getProvinceCityData, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<ProvinceItem> dataList = (ArrayList<ProvinceItem>) JSON.parseArray(response.result, ProvinceItem.class);
                    if (dataList != null) {
                        DataCacheDB cacheDB = new DataCacheDB(MyApplication.getContext());
                        cacheDB.saveCityTreeCacheJson(response.result);
                        onSuccess(dataList);
                    } else {
                        if (!TextUtils.isEmpty(response.result)) {
                            onError(Res.getString(R.string.me_data_error));
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static final int MISSION_TYPE_COMPLETE_PROFILE = 101;//完善个人信息
    public static final int MISSION_TYPE_PUBLISH_STATUS = 201;//发动态

    public static void completeMissionCallback(final int type) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                json.put("type", type);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.missionCallback, json);

                    if (response.error == null && response.result.equals("1")) {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshWeb));
                            }
                        });
                        return;
                    }
                    //服务器操作不成功，再调用一次
                    response = OkHttpHelper.getInstance().post(Method.missionCallback, json);
                    if (response.error == null && response.result.equals("1")) {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshWeb));
                            }
                        });
                        return;
                    }
                    if (response.error != null) {
                        LogUtil.logE(response.error.msg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
