package yahier.exst.task.home;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.model.Industry;
import com.stbl.stbl.model.bangyibang.BangYiBangItem;
import com.stbl.stbl.model.bangyibang.Invited;
import com.stbl.stbl.model.bangyibang.Recommend;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;

public class HelpTask {

    public static SimpleTask<ArrayList<BangYiBangItem>> getHomeHelpList(final String keyword,
                                                                        final int selecttype, final int page) {
        return new SimpleTask<ArrayList<BangYiBangItem>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                if (keyword != null) {
                    json.put("keyword", keyword);
                }
                json.put("selecttype", selecttype);
                json.put("page", page);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_show, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<BangYiBangItem> dataList = (ArrayList<BangYiBangItem>) JSONArray.parseArray(response.result, BangYiBangItem.class);
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

    public static SimpleTask<Integer> getUndoNumber() {
        return new SimpleTask<Integer>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_recommend_hasundo, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    int hasundo = JSONObject.parseObject(response.result).getIntValue(
                            "hasundo");
                    onCompleted(hasundo);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> closeHelp(final int position,
                                                final long issueid) {
        return new SimpleTask<Integer>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("issueid", issueid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_close_xuqiu, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(position);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> deleteHelp(final int position,
                                                 final long issueid) {
        return new SimpleTask<Integer>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("issueid", issueid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_delete_xuqiu, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(position);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<ArrayList<BangYiBangItem>> searchHelpList(
            final String keyword, final int page) {
        return new SimpleTask<ArrayList<BangYiBangItem>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("keyword", keyword);
                json.put("page", page);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_show, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<BangYiBangItem> dataList = (ArrayList<BangYiBangItem>) JSONArray.parseArray(response.result, BangYiBangItem.class);
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

    public static SimpleTask<ArrayList<Invited>> getReceiveInviteList(
            final int page) {
        return new SimpleTask<ArrayList<Invited>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("page", page);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_invited_show, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Invited> dataList = (ArrayList<Invited>) JSONArray.parseArray(response.result, Invited.class);
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

    public static SimpleTask<ArrayList<Recommend>> getReceiveRecommList(
            final long issueid, final int page) {
        return new SimpleTask<ArrayList<Recommend>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("issueid", issueid);
                json.put("page", page);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_recommend_show, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Recommend> dataList = (ArrayList<Recommend>) JSONArray.parseArray(response.result, Recommend.class);
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

    public static SimpleTask<Integer> contact(final int position,
                                              final int recommendid) {
        return new SimpleTask<Integer>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("recommendid", recommendid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_contact, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(position);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> adopt(final int position,
                                            final int recommendid) {
        return new SimpleTask<Integer>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("recommendid", recommendid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_recommend_select, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(position);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<ArrayList<Industry>> getIndustryList() {
        return new SimpleTask<ArrayList<Industry>>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getIndustrys, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Industry> industryList = (ArrayList<Industry>) JSONArray.parseArray(response.result, Industry.class);
                    if (industryList != null) {
                        onCompleted(industryList);
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

    public static SimpleTask<Long> publish(final String title, final String description, final int typeid, final int goldvalue, final String paypwd) {
        return new SimpleTask<Long>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("title", title);
                json.put("description", description);
                json.put("typeid", typeid);
                json.put("goldvalue", goldvalue);
                if (!TextUtils.isEmpty(paypwd)) {
                    json.put("paypwd", paypwd);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_create_xuqiu, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    long issueid = JSONObject.parseObject(response.result).getIntValue("issueid");
                    onCompleted(issueid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<Integer> recommend(final long issueid,
                                                final long shareuserid, final String sharereason) {
        return new SimpleTask<Integer>() {

            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("issueid", issueid);
                json.put("shareuserid", shareuserid);
                json.put("sharereason", sharereason);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.bangyibang_recommend_create, json);
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

    public static SimpleTask<ArrayList<String>> querySearchHistory() {
        return new SimpleTask<ArrayList<String>>() {
            @Override
            protected void call() {
                String array = (String) SharedPrefUtils.getFromUserFile(KEY.HELP_SEARCH_HISTORY, "[]");
                ArrayList<String> dataList = (ArrayList<String>) JSON.parseArray(array, String.class);
                if (dataList != null) {
                    onCompleted(dataList);
                } else {
                    onError("数据错误");
                }
            }
        };
    }

    public static SimpleTask<ArrayList<Integer>> getBountyList() {
        return new SimpleTask<ArrayList<Integer>>() {
            @Override
            protected void call() {
                ArrayList<Integer> dataList = null;
                String beandic = (String) SharedPrefUtils.getFromPublicFile(KEY.bangbeandic, "");
                if (TextUtils.isEmpty(beandic)) {
                    JSONObject json = new JSONObject();
                    try {
                        HttpResponse response = OkHttpHelper.getInstance().post(Method.common_dic, json);
                        if (response.error != null) {
                            onError(response.error);
                            return;
                        }
                        JSONObject result = JSONObject.parseObject(response.result);
                        if (result.containsKey("bang")) {
                            JSONObject bang = result.getJSONObject("bang");
                            if (bang.containsKey("beandic")) {
                                beandic = bang.getString("beandic");
                                SharedPrefUtils.putToPublicFile(KEY.bangbeandic, beandic);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        onError(e);
                        return;
                    }
                }
                if (!TextUtils.isEmpty(beandic)) {
                    dataList = (ArrayList<Integer>) JSON.parseArray(beandic, int.class);
                }
                if (dataList != null) {
                    onCompleted(dataList);
                } else {
                    onError("数据返回错误");
                }
            }
        };
    }

}
