package yahier.exst.task.home;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.model.Headmen;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.database.DataCacheDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/26.
 */

public class HomeTask {

    public static Task<HashMap<String, Object>> getHomeData(final boolean isFirst) {
        return new Task<HashMap<String, Object>>() {
            @Override
            protected void call() {
                DataCacheDB cache = new DataCacheDB(MyApplication.getContext());
                if (isFirst) {
                    try {
                        HashMap<String, Object> map = new HashMap<>();
                        String json = cache.getHomePageCacheJson();
                        if (!TextUtils.isEmpty(json)) {
                            JSONObject obj = JSON.parseObject(json);
                            ArrayList<Banner> bannerList = (ArrayList<Banner>) JSON.parseArray(obj.getString("bannerview"), Banner.class);
                            if (bannerList != null) {
                                map.put("bannerList", bannerList);
                            }
                            ArrayList<Headmen> menList = (ArrayList<Headmen>) JSON.parseArray(obj.getString("recommandheadview"), Headmen.class);
                            if (menList != null) {
                                map.put("menList", menList);
                            }
                        }
                        json = cache.getHomeAdCacheJson();
                        if (!TextUtils.isEmpty(json)) {
                            ArrayList<Ad> adList = (ArrayList<Ad>) JSON.parseArray(json, Ad.class);
                            if (adList != null) {
                                map.put("adList", adList);
                            }
                        }
                        onSuccess(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getHttpData(cache);
            }

            private void getHttpData(DataCacheDB cache) {
                JSONObject json = new JSONObject();
                try {
                    HashMap<String, Object> map = new HashMap<>();
                    boolean hasResult = false;
                    TaskError err = new TaskError(Res.getString(R.string.me_request_fail));
                    //获取首页banner和热门大酋长
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.homePage, json);
                    if (response.error == null) {
                        JSONObject obj = JSON.parseObject(response.result);
                        cache.saveHomePageCacheJson(response.result);
                        ArrayList<Banner> bannerList = (ArrayList<Banner>) JSON.parseArray(obj.getString("bannerview"), Banner.class);
                        if (bannerList != null) {
                            map.put("bannerList", bannerList);
                        }
                        ArrayList<Headmen> menList = (ArrayList<Headmen>) JSON.parseArray(obj.getString("recommandheadview"), Headmen.class);
                        if (menList != null) {
                            map.put("menList", menList);
                        }
                        hasResult = true;
                    } else {
                        err = response.error;
                    }
                    //获取首页广告
                    response = OkHttpHelper.getInstance().post(Method.userGetHomeAdNew, json);
                    if (response.error == null) {
                        ArrayList<Ad> adList = (ArrayList<Ad>) JSON.parseArray(response.result, Ad.class);
                        if (adList != null) {
                            cache.saveHomeAdCacheJson(response.result);
                            map.put("adList", adList);
                        }
                        hasResult = true;
                    }
                    if (hasResult) {
                        onSuccess(map);
                    } else {
                        onError(err);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<Banner>> getBannerList() {
        return new Task<ArrayList<Banner>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.homePage, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    JSONObject obj = JSON.parseObject(response.result);
                    ArrayList<Banner> list = (ArrayList<Banner>) JSON.parseArray(obj.getString("bannerview"), Banner.class);
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

    public static Task<ArrayList<Headmen>> getBigChiefList() {
        return new Task<ArrayList<Headmen>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.homePageHeanMen, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Headmen> list = (ArrayList<Headmen>) JSON.parseArray(response.result, Headmen.class);
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

    public static Task<ArrayList<Ad>> getAdList() {
        return new Task<ArrayList<Ad>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userGetHomeAdList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<Ad> list = (ArrayList<Ad>) JSON.parseArray(response.result, Ad.class);
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

}
