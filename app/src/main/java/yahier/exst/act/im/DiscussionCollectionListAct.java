package yahier.exst.act.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.ConversationActivity;
import com.stbl.stbl.adapter.msg.DiscussionListAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * 收藏的讨论组列表
 * Created by Administrator on 2016/8/31 0031.
 */

public class DiscussionCollectionListAct extends ThemeActivity implements FinalHttpCallback,XListView.OnXListViewListener{
    private XListView lvDiscussionList; //讨论组列表
    private DiscussionListAdapter mAdapter;//讨论组适配器
    private int page = 1;//页码
    private int count = 20; //每页显示条数
    private List<DiscussionTeam> mData;//讨论组列表数据
    private View emptyView;//无数据时提示


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_discussion_collection_list_layout);
        setLabel(getString(R.string.im_discussion));
        lvDiscussionList = (XListView) findViewById(R.id.lv_discussion_list);
        emptyView = findViewById(R.id.empty_view);
        lvDiscussionList.setPullRefreshEnable(false);
        mData = new ArrayList<>();
        mAdapter = new DiscussionListAdapter(this,mData);
        lvDiscussionList.setAdapter(mAdapter);
        lvDiscussionList.setOnXListViewListener(this);
        getDiscussionList();
        lvDiscussionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > mData.size()) return;
                DiscussionTeam item = mData.get(position-1);
                chatDiscussion(String.valueOf(item.getGroupid()), item.getGroupname(), ConversationActivity.typeLocalDiscussion);
            }
        });
    }

    void chatDiscussion(String targetUserId, String title, String typeLocal) {
        Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("typeLocal", typeLocal).appendQueryParameter("title", title).build();
        this.startActivity(new Intent("android.intent.action.VIEW", uri));
    }

    @Override
    public void onRefresh(XListView v) {
    }

    @Override
    public void onLoadMore(XListView v) {
        page ++;
        getDiscussionList();
    }

    private void getDiscussionList(){
        Params params = new Params();
        params.put("page",page);
        params.put("count",count);
        new HttpEntity(this,true).commonPostData(Method.imDiscussionCollectionList,params,this);
    }

    @Override
    public void parse(String methodName, String result) {
        if(lvDiscussionList != null) {
            lvDiscussionList.onLoadMoreComplete();
            lvDiscussionList.onRefreshComplete();
        }
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            if (page > 1){ //失败页码回滚
                page --;
            }
            return;
        }

        switch (methodName){
            case Method.imDiscussionCollectionList:
                String obj = JSONHelper.getStringFromObject(item.getResult());
                List<DiscussionTeam> list = JSONHelper.getList(obj, DiscussionTeam.class);
                if (list != null){
                    mData.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    if (list.size() < count){
                        lvDiscussionList.EndLoad();
                        lvDiscussionList.setFooterViewText(getString(R.string.no_more));
                    }
                }
                if (page == 1 && (list == null || list.size() == 0)){
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
                break;
        }
    }
}
