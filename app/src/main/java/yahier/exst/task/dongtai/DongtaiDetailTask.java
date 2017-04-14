package yahier.exst.task.dongtai;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.PraiseItem;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiDetailTask {

    public static Task<Statuses> getDongtaiDetail(final long statusesid) {
        return new Task<Statuses>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboGetOneDetail, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Statuses data = JSON.parseObject(response.result, Statuses.class);
                    if (data != null) {
                        onSuccess(data);
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


    public static Task<ArrayList<StatusesComment>> getCommentList(final long statusesid, final int lastid, final int count) {
        return new Task<ArrayList<StatusesComment>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboRemarkList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<StatusesComment> dataList = (ArrayList<StatusesComment>) JSON.parseArray(response.result, StatusesComment.class);
                    if (dataList != null) {
                        onSuccess(dataList);
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

    public static Task<ArrayList<PraiseItem>> getPraiseList(final long statusesid, final int lastid, final int count) {
        return new Task<ArrayList<PraiseItem>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                json.put("lastid", lastid);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboGetPraiseList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<PraiseItem> dataList = (ArrayList<PraiseItem>) JSON.parseArray(response.result, PraiseItem.class);
                    if (dataList != null) {
                        onSuccess(dataList);
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

    public static Task<PraiseResult> praiseOrUnpraiseDongtai(final long statusesid) {
        return new Task<PraiseResult>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboPraise, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    PraiseResult data = JSON.parseObject(response.result, PraiseResult.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<PraiseResult> praiseOrUnpraiseComment(final long commentid) {
        return new Task<PraiseResult>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("commentid", commentid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboCommentPraise, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    PraiseResult data = JSON.parseObject(response.result, PraiseResult.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<StatusesComment> addComment(final long statusesid, final String content, final long commentid) {
        return new Task<StatusesComment>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                json.put("content", content);
                if (commentid > 0) {
                    json.put("commentid", commentid);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboRemark, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    StatusesComment data = JSON.parseObject(response.result, StatusesComment.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<Collect> collectDongtai(final long statusesid) {
        return new Task<Collect>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboCollect, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Collect data = JSON.parseObject(response.result, Collect.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<Collect> uncollectDongtai(final long statusesid) {
        return new Task<Collect>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboCancelCollect, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    Collect data = JSON.parseObject(response.result, Collect.class);
                    if (data != null) {
                        onSuccess(data);
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

    public static Task<Long> deleteComment(final long commentid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("commentid", commentid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboDeleteRemark, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(commentid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<Long> deleteDongtai(final long statusesid) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboDel, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(statusesid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<Gift>> getGiftList() {
        return new Task<ArrayList<Gift>>() {
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
                        onSuccess(dataList);
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

    public static Task<Long> sendGift(final long businessid, final int giftid, final String paypwd) {
        return new Task<Long>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("businessid", businessid);
                json.put("giftid", giftid);
                if (!TextUtils.isEmpty(paypwd)) {
                    json.put("paypwd", paypwd);
                }
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.userReward, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onSuccess(businessid);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<StatusesReward>> getRewardList(final long statusesid, final int page, final int count) {
        return new Task<ArrayList<StatusesReward>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("statusesid", statusesid);
                json.put("page", page);
                json.put("count", count);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.weiboRewardList, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    ArrayList<StatusesReward> dataList = (ArrayList<StatusesReward>) JSON.parseArray(response.result, StatusesReward.class);
                    if (dataList != null) {
                        onSuccess(dataList);
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
