package yahier.exst.act.im;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.DiscussionMember;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 讨论组页member页面，现在用做了勾选删除
 *
 * @author lenovo
 */

public class DiscussionDeleteMembersAct extends ThemeActivity implements FinalHttpCallback {
    ListView listView;

    DiscussionMember discussionTeam;
    DiscussionTeam discussionInfo;
    List<UserItem> userList;
    List<Boolean> checkList;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion_members);
        listView = (ListView) findViewById(R.id.list);
        discussionTeam = (DiscussionMember) getIntent().getSerializableExtra("members");
        discussionInfo = (DiscussionTeam) getIntent().getSerializableExtra("discussionInfo");
        if (discussionTeam == null || discussionInfo == null)
            return;
        userList = discussionTeam.getMembers();
        if (userList.size() == 0) {
            return;
        }
        setLabel(R.string.discussion_member);
        setRightText(R.string.remove, new OnClickListener() {

            @Override
            public void onClick(View view) {
                removeMembers();
            }
        });
        adapter = new Adapter(this);
        listView.setAdapter(adapter);
        adapter.setData(userList);

    }

    void getMemeberInfos() {
        Params params = new Params();
        params.put("groupid", discussionInfo.getGroupid());
        new HttpEntity(this).commonPostData(Method.imShowDiscussionMembers, params, this);
    }

    public void removeMembers() {
        List<Long> listIds = adapter.getSelectedIds();
        if (listIds.size() == 0) {
            TipsDialog.popup(this, R.string.not_choose_member_yet, R.string.queding);
            return;
        }
        JSONObject json = new JSONObject();
        json.put("groupid", discussionInfo.getGroupid());
        json.put("memberids", listIds);
        new HttpEntity(this).commonPostJson(Method.imRemoveDiscussionMembers, json.toString(), this);
    }

    class Adapter extends CommonAdapter {
        Context mContext;
        List<UserItem> list;
        final int typeCommon = 0;
        final int typeCreater = 1;


        public Adapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<UserItem>();
        }
//
//        public int getItemViewType(int position) {
//            UserItem user = list.get(position);
//            if(discussionInfo.getGroupmasterid()==user.getUserid()){
//                return typeCreater;
//            }else{
//                return typeCommon;
//            }
//
//        }

        /**
         * 获取选中的userId列表
         *
         * @return
         */
        public List<Long> getSelectedIds() {
            List<Long> listIds = new ArrayList<Long>();
            for (int i = 0; i < list.size(); i++) {
                //LogUtil.logE(i + ":" + checkList.get(i));
                if (checkList.get(i)) {
                    listIds.add(list.get(i).getUserid());
                } else {

                }
            }
            return listIds;

        }

        public void setData(List<UserItem> list) {
            this.list = list;
            checkList = new ArrayList<Boolean>();
            for (int i = 0; i < list.size(); i++) {
                checkList.add(false);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        class Holder {
            ImageView imgUser;
            TextView tvName;
            TextView tvStatus;
            CheckBox check;
        }

        @Override
        public View getView(final int i, View con, ViewGroup parent) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.choice_friends_list_item, null);
                ho.imgUser = (ImageView) con.findViewById(R.id.imgUser);
                ho.tvName = (TextView) con.findViewById(R.id.tvName);
                ho.tvStatus = (TextView) con.findViewById(R.id.tvStatus);
                ho.check = (CheckBox) con.findViewById(R.id.check);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();
            UserItem user = list.get(i);
            if(user.getUserid()==discussionInfo.getGroupmasterid()){
                ho.check.setVisibility(View.GONE);
            }else{
                ho.check.setVisibility(View.VISIBLE);
            }
            PicassoUtil.load(mContext, user.getImgmiddleurl(), ho.imgUser);
//            ho.tvName.setText(user.getNickname());
            ho.tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
            ho.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    checkList.set(i, checked);

                }
            });

            if (checkList.get(i)) {
                ho.check.setChecked(true);
            } else {
                ho.check.setChecked(false);
            }
            return con;
        }
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.imRemoveDiscussionMembers:
                ToastUtil.showToast(R.string.remove_success);
                //刷新
                getMemeberInfos();
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeGetDiscussionMember));
                break;
            case Method.imShowDiscussionMembers:
                DiscussionMember members = JSONHelper.getObject(obj, DiscussionMember.class);
                LogUtil.logE("size:" + members.getMembers().size());
                if (members != null && members.getMembers() != null) {
                    adapter.setData(members.getMembers());

                    //刷新

                    RongDB userDB = new RongDB(this);
                    IMAccount account =  new IMAccount(RongDB.typeDiscussion, String.valueOf(discussionInfo.getGroupid()), discussionInfo.getGroupname(), "", UserItem.certificationNo, "");
                    account.setPeopleNum(members.getMembers().size());
                    userDB.insert(account);
                }
                break;
        }

    }


}
