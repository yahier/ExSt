package yahier.exst.task.mine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.R;
import com.stbl.stbl.item.Currency;
import com.stbl.stbl.item.ExchargeRate;
import com.stbl.stbl.item.WealthInfo;
import com.stbl.stbl.model.ChargeAmount;
import com.stbl.stbl.model.FanliInfo;
import com.stbl.stbl.model.HongBaoWallet;
import com.stbl.stbl.model.Recharge;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.MainHandler;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ThreadPool;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/16.
 */
public class WalletTask {
    /**
     * 获取个人钱包信息
     */
    public static void getWalletBalanceBackground(final SimpleTask.Callback callback) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetWealth, json);
                    if (response.error != null) {
                        LogUtil.logE(response.error.msg);
                        SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                        SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                        SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                        SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                        if (callback != null)
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(new TaskError(""));
                                }
                            });
                        return;
                    }
                    final WealthInfo info = JSON.parseObject(response.result, WealthInfo.class);
                    if (info != null) {
                        SharedPrefUtils.putToUserFile(KEY.amounts, info.getAmounts());
                        SharedPrefUtils.putToUserFile(KEY.jindou, info.getJindou());
                        SharedPrefUtils.putToUserFile(KEY.lvdou, info.getLvdou());
                        SharedPrefUtils.putToUserFile(KEY.jifen, info.getJifen());
                        ExchargeRate rate = info.getExchargerate();
                        if (rate != null) {
                            SharedPrefUtils.putToPublicFile(KEY.yue2rmb, rate.getYue2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.jindou2rmb, rate.getJindou2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.jindou2yue, rate.getJindou2yue());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2rmb, rate.getLvdou2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2yue, rate.getLvdou2yue());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2jindou, rate.getLvdou2jindou());
                        }
                        if (callback != null) {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onCompleted(info);
                                }
                            });
                        }
                    } else {
                        if (callback != null) {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(new TaskError(""));
                                }
                            });
                        }
                        SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                        SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                        SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                        SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(new TaskError(""));
                            }
                        });
                    }
                    SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                    SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                    SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                    SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                }
            }
        });
    }

    public static SimpleTask<WealthInfo> getWalletBalance() {
        return new SimpleTask<WealthInfo>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetWealth, json);
                    if (response.error != null) {
                        onError(response.error);
                        SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                        SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                        SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                        SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                        return;
                    }
                    WealthInfo info = JSON.parseObject(response.result, WealthInfo.class);
                    if (info != null) {
                        SharedPrefUtils.putToUserFile(KEY.amounts, info.getAmounts());
                        SharedPrefUtils.putToUserFile(KEY.jindou, info.getJindou());
                        SharedPrefUtils.putToUserFile(KEY.lvdou, info.getLvdou());
                        SharedPrefUtils.putToUserFile(KEY.jifen, info.getJifen());
                        ExchargeRate rate = info.getExchargerate();
                        if (rate != null) {
                            SharedPrefUtils.putToPublicFile(KEY.yue2rmb, rate.getYue2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.jindou2rmb, rate.getJindou2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.jindou2yue, rate.getJindou2yue());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2rmb, rate.getLvdou2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2yue, rate.getLvdou2yue());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2jindou, rate.getLvdou2jindou());
                        }
                        onCompleted(info);
                    } else {
                        onError(Res.getString(R.string.me_data_error));
                        SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                        SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                        SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                        SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                    SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                    SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                    SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                    SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                }
            }
        };
    }

    public static SimpleTask<ArrayList<Currency>> getBalanceCurrencyList(final int lastid, final int count) {
        return new SimpleTask<ArrayList<Currency>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userCurrencyBillList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Currency> currencyList = (ArrayList<Currency>) JSON.parseArray(JSONObject.parseObject(response.result).getString("currencys"), Currency.class);
                    if (currencyList != null) {
                        onCompleted(currencyList);
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

    public static SimpleTask<ArrayList<Currency>> getBeanCurrencyList(final int currencytype, final int lastid, final int count) {
        return new SimpleTask<ArrayList<Currency>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("currencytype", currencytype);
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetPeaList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Currency> currencyList = (ArrayList<Currency>) JSON.parseArray(JSONObject.parseObject(response.result).getString("currencys"), Currency.class);
                    if (currencyList != null) {
                        onCompleted(currencyList);
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

    public static SimpleTask<Recharge> chargeBalance(final int paytype, final float amount) {
        return new SimpleTask<Recharge>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("paytype", paytype);
                json.put("amount", amount);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userRecharge, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Recharge info = JSON.parseObject(response.result, Recharge.class);
                    if (info != null) {
                        onCompleted(info);
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

    public static SimpleTask<Recharge> buyGoldBean(final int paytype, final int amount) {
        return new SimpleTask<Recharge>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("paytype", paytype);
                json.put("amount", amount);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.buyGoldPea, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Recharge info = JSON.parseObject(response.result, Recharge.class);
                    if (info != null) {
                        onCompleted(info);
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

    public static SimpleTask<Recharge> buyGreenBean(final int paytype, final int amount) {
        return new SimpleTask<Recharge>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("paytype", paytype);
                json.put("amount", amount);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.buyGreenPea, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Recharge info = JSON.parseObject(response.result, Recharge.class);
                    if (info != null) {
                        onCompleted(info);
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

    public static SimpleTask<Integer> transferBean(final int paytype, final long amount, final long touserids, final String paypwd) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("paytype", paytype);
                json.put("amount", amount);
                json.put("touserids", touserids);
                if (!TextUtils.isEmpty(paypwd)) {
                    json.put("paypwd", paypwd);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userTransferPea, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> exchangeGoldBean(final int paytype, final long amount, final String paypwd) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("paytype", paytype);
                json.put("amount", amount);
                if (!TextUtils.isEmpty(paypwd)) {
                    json.put("paypwd", paypwd);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.buyGoldPea, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> exchangeGreenBean(final int paytype, final long amount, final String paypwd) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("paytype", paytype);
                json.put("amount", amount);
                if (!TextUtils.isEmpty(paypwd)) {
                    json.put("paypwd", paypwd);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.buyGreenPea, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> chargeBalanceVerify(final long orderpayno) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("orderpayno", orderpayno);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.rechargePayVerify, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> buyBeanVerify(final long orderpayno) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("orderpayno", orderpayno);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.buyBeanPayVerify, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<ArrayList<ChargeAmount>> getChargeAmountList() {
        return new SimpleTask<ArrayList<ChargeAmount>>() {
            @Override
            protected void call() {
                ArrayList<ChargeAmount> dataList = null;
                String rechargejindou = (String) SharedPrefUtils.getFromPublicFile(KEY.rechargejindou, "");
                if (TextUtils.isEmpty(rechargejindou)) {
                    JSONObject json = new JSONObject();
                    try {
                        HttpResponse response = OkHttpHelper.getInstance().post(Method.common_dic, json);
                        if (response.error != null) {
                            onError(response.error);
                            return;
                        }
                        JSONObject result = JSONObject.parseObject(response.result);
                        if (result.containsKey("user")) {
                            JSONObject user = result.getJSONObject("user");
                            //我的钱包充值金额
                            if (user.containsKey("rechargejindou")) {
                                rechargejindou = user.getString("rechargejindou");
                                SharedPrefUtils.putToPublicFile(KEY.rechargejindou, rechargejindou);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        onError(e);
                        return;
                    }
                }
                if (!TextUtils.isEmpty(rechargejindou)) {
                    dataList = (ArrayList<ChargeAmount>) JSON.parseArray(rechargejindou, ChargeAmount.class);
                }
                if (dataList != null) {
                    onCompleted(dataList);
                } else {
                    onError(Res.getString(R.string.me_data_error));
                }
            }
        };
    }

    public static Task<ArrayList<Currency>> getWalletDetailList(final int currencytype, final int lastid, final int count) {
        return new Task<ArrayList<Currency>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("currencytype", currencytype);
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetPeaList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Currency> currencyList = (ArrayList<Currency>) JSON.parseArray(JSONObject.parseObject(response.result).getString("currencys"), Currency.class);
                    if (currencyList != null) {
                        onSuccess(currencyList);
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

    public static Task<WealthInfo> getWalletInfo() {
        return new Task<WealthInfo>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetWealth, json);
                    if (response.error != null) {
                        onError(response.error);
                        SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                        SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                        SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                        SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                        return;
                    }
                    WealthInfo info = JSON.parseObject(response.result, WealthInfo.class);
                    if (info != null) {
                        SharedPrefUtils.putToUserFile(KEY.amounts, info.getAmounts());
                        SharedPrefUtils.putToUserFile(KEY.jindou, info.getJindou());
                        SharedPrefUtils.putToUserFile(KEY.lvdou, info.getLvdou());
                        SharedPrefUtils.putToUserFile(KEY.jifen, info.getJifen());
                        ExchargeRate rate = info.getExchargerate();
                        if (rate != null) {
                            SharedPrefUtils.putToPublicFile(KEY.yue2rmb, rate.getYue2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.jindou2rmb, rate.getJindou2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.jindou2yue, rate.getJindou2yue());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2rmb, rate.getLvdou2rmb());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2yue, rate.getLvdou2yue());
                            SharedPrefUtils.putToPublicFile(KEY.lvdou2jindou, rate.getLvdou2jindou());
                        }
                        onSuccess(info);
                    } else {
                        onError(Res.getString(R.string.me_data_error));
                        SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                        SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                        SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                        SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                    SharedPrefUtils.putToUserFile(KEY.amounts, -1);
                    SharedPrefUtils.putToUserFile(KEY.jindou, -1);
                    SharedPrefUtils.putToUserFile(KEY.lvdou, -1);
                    SharedPrefUtils.putToUserFile(KEY.jifen, -1);
                }
            }
        };
    }

    public static Task<FanliInfo> getFanliInfo() {
        return new Task<FanliInfo>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetFanli, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    FanliInfo info = JSON.parseObject(response.result, FanliInfo.class);
                    if (info != null) {
                        onSuccess(info);
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

    public static Task<HongBaoWallet> getHongBaoWallet() {
        return new Task<HongBaoWallet>() {
            @Override
            protected void call() {
                try {
                    HttpResponse response = OkHttpHelper.getInstance().redPacketGet(Method.getHongBaoWallet);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    HongBaoWallet item = JSON.parseObject(response.result, HongBaoWallet.class);
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

    public static Task<Boolean> checkPayPassword() {
        return new Task<Boolean>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userPasswordCheck, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Boolean info = JSON.parseObject(response.result, Boolean.class);
                    if (info != null) {
                        onSuccess(info);
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
