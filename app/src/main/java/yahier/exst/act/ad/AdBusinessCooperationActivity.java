package yahier.exst.act.ad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.MineTudiListAct;
import com.stbl.stbl.adapter.ad.AdBusinessCooperationAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ad.AdBusinessItem;
import com.stbl.stbl.item.im.ApplyGotItem;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * 商务合作
 * Created by Administrator on 2016/10/1 0001.
 */

public class AdBusinessCooperationActivity extends ThemeActivity implements FinalHttpCallback {
    private XListView listView;
    private AdBusinessCooperationAdapter mAdapter;
    private View empty;

    private int lastid = 0;
    private final int count = 15;
    boolean isRefresh = true; //是否下拉刷新
    private List<AdBusinessItem> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_business_cooperation_layout);

        setLabel(getString(R.string.ad_business_cooperation));
        empty = findViewById(R.id.empty);
        listView = (XListView) findViewById(R.id.list);
        mAdapter = new AdBusinessCooperationAdapter(this,mData);
        mAdapter.setAdBusinessAdapterListener(listener);
        listView.setAdapter(mAdapter);
        listView.setPullLoadEnable(true);
        listView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
                isRefresh = true;
                listView.setPullLoadEnable(true);
                lastid = 0;
                getData();
            }

            @Override
            public void onLoadMore(XListView v) {
                if (mData.get(mData.size() - 1) != null) {
                    lastid = mData.get(mData.size() - 1).getId();
                }
                isRefresh = false;
                getData();
            }
        });
        getData();
    }
    AdBusinessCooperationAdapter.AdBusinessAdapterListener listener = new AdBusinessCooperationAdapter.AdBusinessAdapterListener() {
        @Override
        public void addFriend(long userid,String name) {
            new AddFriendUtil(AdBusinessCooperationActivity.this, null).setSendAddStatusListener(new AddFriendUtil.SendAddStatusListener() {
                @Override
                public void isSuccess(boolean isSuccess) {
                    if (isSuccess){ //刷新
                        getData();
                    }
                }
            }).addFriendDirect(userid,name);
        }

        @Override
        public void toChat(long userid,String name) {
            chatPrivate(String.valueOf(userid),name);
        }
    };
    void chatPrivate(String targetUserId, String title) {
        ThemeActivity.isMerchant(targetUserId);
        Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
        startActivity(new Intent("android.intent.action.VIEW", uri));
    }

    //获取申请列表
    void getData() {
        Params params = new Params();
        params.put("count", count);
        params.put("lastid", lastid);
        new HttpEntity(this).commonPostData(Method.adsysBusinessQuery, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        listView.onLoadMoreComplete();
        listView.onRefreshComplete();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.adsysBusinessQuery:
                List<AdBusinessItem> list = JSONHelper.getList(obj, AdBusinessItem.class);

                empty.setVisibility(View.GONE);
                if (list != null) {
                    if (list.size() < count) {
                        listView.EndLoad();
                    }
                    LogUtil.logE("LogUtil", list.size());
                    if (isRefresh) {
                        mData.clear();
                        mData.addAll(list);
                    } else {
                        mData.addAll(list);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                if (mData.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                    return;
                }


        }
    }
}
