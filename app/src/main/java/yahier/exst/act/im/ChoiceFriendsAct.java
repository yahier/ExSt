package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupMemberList;
import com.stbl.stbl.item.im.UserList;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 弃用.请使用SelectFriendActivity
 */
public class ChoiceFriendsAct extends ThemeActivity implements FinalHttpCallback, OnClickListener {
    ExpandableListView listView;
    List<GroupMemberList> list;
    AppAdapter adapter;
    // 选择模式
    public static final int choiceModeSingle = 1;
    public static final int choiceModeMult = 2;
    public static final int choiceModeMultSend = 3;
    public static final int choiceModeMultShare = 4;
    int choiceMode = choiceModeMult;// 默认是多选模式
    String content;

    int hasself;

    Statuses statuses;

    Button btnOk;
    TextView tvChooseAll;
    boolean isAllChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_friends);
        setLabel(getString(R.string.im_select_friend));
        listView = (ExpandableListView) findViewById(R.id.list);
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
        // findViewById(R.id.tvChooseAll).setOnClickListener(this);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        choiceMode = getIntent().getIntExtra("choiceMode", choiceModeMult);
        hasself = getIntent().getIntExtra("hasself", GroupMemberList.hasselfNo);
        content = getIntent().getStringExtra("content");
        LogUtil.logE("choiceMode:" + choiceMode);
        if(choiceMode==choiceModeMult){
            setRightText(getString(R.string.im_all_choise), this);
        }
        getData();
    }

    void getData() {
        Params params = new Params();
        params.put("grouptype", GroupMemberList.typeRequestRelation);// 0-按用户关系分组，1-按用户昵称首字母分组
        params.put("hasself", hasself);// 0-按用户关系分组，1-按用户昵称首字母分组
        new HttpEntity(this).commonPostData(Method.getAppContacts, params, this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.theme_top_banner_left:
                finish();
                break;
//            case R.id.tvChooseAll:
//                adapter.setAllUnChecked();
//                break;
            case R.id.theme_top_tv_right:
                if (adapter == null) return;
                adapter.setAllUnChecked();
                break;
            case R.id.btnOk:
                if (adapter == null) {
                    finish();
                    return;
                }
                Intent intent;
                UserList users = new UserList();
                List<UserItem> list = adapter.getCheckedUsers();
                if (list.size() == 0) {
                    ToastUtil.showToast(this, getString(R.string.im_no_select_friend));
                    return;
                }
                users.setList(list);
                switch (choiceMode) {
                    case choiceModeMultSend:
                        intent = new Intent(this, GroupSendAct.class);
                        intent.putExtra("users", users);
                        startActivity(intent);
                        break;
                    case choiceModeMultShare:
                        intent = new Intent(this, GroupSendAct.class);
                        intent.putExtra("users", users);
                        intent.putExtra("content", content);
                        startActivity(intent);
                        break;
                    case choiceModeMult:
                    case choiceModeSingle:
                        intent = new Intent();
                        intent.putExtra("users", users);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }

                break;
        }
    }

    class AppAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        List<GroupMemberList> list;
        ArrayList<ArrayList<Boolean>> listCheck = new ArrayList<ArrayList<Boolean>>();

        public AppAdapter(Context mContext, List<GroupMemberList> list) {
            this.mContext = mContext;
            this.list = list;
            // 添加选中的默认值 false
            for (int i = 0; i < list.size(); i++) {
                ArrayList<Boolean> childList = new ArrayList<Boolean>();
                GroupMemberList group = list.get(i);
                for (int j = 0; j < group.getGroupmembers().size(); j++) {//nullpointet
                    childList.add(false);
                }
                listCheck.add(childList);
            }
        }

        /**
         * 获取选中的user列表
         */
        public List<UserItem> getCheckedUsers() {
            List<UserItem> userList = new ArrayList<UserItem>();
            for (int i = 0; i < listCheck.size(); i++) {
                List<UserItem> listUser = list.get(i).getGroupmembers();
                ArrayList<Boolean> listChildCheck = listCheck.get(i);
                for (int j = 0; j < listChildCheck.size(); j++) {
                    LogUtil.logE(i + "——" + j + " " + listChildCheck.get(j));
                    if (listChildCheck.get(j)) {
                        userList.add(listUser.get(j));
                    } else {

                    }
                }

            }
            return userList;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            return childPosition % 3;
        }

        @Override
        public int getChildTypeCount() {
            return 3;
        }

        @Override
        public int getGroupType(int groupPosition) {
            return groupPosition % 3;
        }

        @Override
        public int getGroupTypeCount() {
            return 3;
        }

        class ViewHolder {
            ImageView imgUser;
            TextView tvName;
            TextView tvStatus;
            CheckBox check;

            public ViewHolder(View view) {
                imgUser = (ImageView) view.findViewById(R.id.imgUser);
                tvName = (TextView) view.findViewById(R.id.tvName);
                tvStatus = (TextView) view.findViewById(R.id.tvStatus);
                check = (CheckBox) view.findViewById(R.id.check);
                view.setTag(this);
            }
        }

        class GroupViewHolder {
            ImageView ivArrow;
            TextView tv_name;

            public GroupViewHolder(View view) {
                ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
                tv_name = (TextView) view.findViewById(R.id.tvName);

                view.setTag(this);
            }
        }

        public void setAllUnChecked(int groupIndex, int childIndex, boolean checkState) {
            for (int i = 0; i < listCheck.size(); i++) {
                ArrayList<Boolean> childList = listCheck.get(i);
                for (int j = 0; j < childList.size(); j++) {
                    childList.set(j, false);
                }
                listCheck.set(i, childList);
            }
            ArrayList<Boolean> childCheckeds = listCheck.get(groupIndex);
            childCheckeds.set(childIndex, checkState);
            listCheck.set(groupIndex, childCheckeds);
            notifyDataSetChanged();
        }

        public void setAllUnChecked() {
            isAllChecked = !isAllChecked;
            for (int i = 0; i < listCheck.size(); i++) {
                ArrayList<Boolean> childList = listCheck.get(i);
                for (int j = 0; j < childList.size(); j++) {
                    childList.set(j, isAllChecked);
                }
                listCheck.set(i, childList);
            }
            // ArrayList<Boolean> childCheckeds = listCheck.get(groupIndex);
            // childCheckeds.set(childIndex, checkState);
            // listCheck.set(groupIndex, childCheckeds);
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return list.get(groupPosition).getGroupmembers().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;// mAppList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return "The " + childPosition + "'th child in " + groupPosition + "'th group.";
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.contact_list_group_item, null);
                convertView.setTag(new GroupViewHolder(convertView));
            }
            GroupViewHolder holder = (GroupViewHolder) convertView.getTag();
            holder.tv_name.setText(list.get(groupPosition).getGroupname());
            if (isExpanded) {
                holder.ivArrow.setImageResource(R.drawable.icon_arrow_up);
            } else {
                holder.ivArrow.setImageResource(R.drawable.icon_arrow_down);
            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            boolean reUseable = true;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.choice_friends_list_item, null);
                convertView.setTag(new ViewHolder(convertView));
                reUseable = false;
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (null == holder) {
                holder = new ViewHolder(convertView);
            }

            UserItem user = list.get(groupPosition).getGroupmembers().get(childPosition);
            PicassoUtil.load(mContext, user.getImgmiddleurl(), holder.imgUser);
//            holder.tvName.setText(user.getNickname());
            holder.tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
            //
            //
            if (listCheck.get(groupPosition).get(childPosition)) {
                holder.check.setChecked(true);
            } else {
                holder.check.setChecked(false);
            }

            // LogUtil.logE("getChildViewAndReUsable "+groupPosition+" "+childPosition);

            holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checkState) {
                    LogUtil.logE("childPosition:" + childPosition + " arg1:" + checkState);
                    // 如果是选中，并且是单选模式
                    if (checkState && choiceMode == choiceModeSingle) {
                        setAllUnChecked(groupPosition, childPosition, checkState);
                    } else if (choiceMode == choiceModeMult || choiceMode == choiceModeMultSend || choiceMode == choiceModeMultShare) {
                        ArrayList<Boolean> listChild = listCheck.get(groupPosition);
                        listChild.set(childPosition, checkState);
                        listCheck.set(groupPosition, listChild);
                    }

                    List list = getCheckedUsers();
                    btnOk.setText(getString(R.string.im_confirm)+"     (" + list.size() + ")");

                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getAppContacts:
                list = JSONHelper.getList(obj, GroupMemberList.class);
                if (list == null || list.size() == 0) {
                    ToastUtil.showToast(this, getString(R.string.im_no_friend));
                    return;
                }

                adapter = new AppAdapter(this, list);
                listView.setAdapter(adapter);
                for (int i = 0; i < list.size(); i++) {
                    listView.expandGroup(i);
                }
                break;

        }

    }
}
