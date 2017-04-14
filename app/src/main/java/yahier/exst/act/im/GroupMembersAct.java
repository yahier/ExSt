package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.mine.FansListAdapter;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.swipe.SwipeMenu;
import com.stbl.stbl.widget.swipe.SwipeMenuCreator;
import com.stbl.stbl.widget.swipe.SwipeMenuItem;
import com.stbl.stbl.widget.swipe.SwipeMenuListView;
import com.stbl.stbl.widget.swipe.SwipeMenuListView.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

public class GroupMembersAct extends ThemeActivity implements FinalHttpCallback {
    SwipeMenuListView listView;
    GroupTeam group;
    //TextView theme_top_banner_middle, theme_top_banner_left, theme_top_banner_right;
    Adapter adapter;
    final int isShutupYes = 0;
    final int isShutupNo = 1;

    final int typeRequestShutYes = 1;// 禁言
    final int typeRequestShutNo = 0;// 解禁
    int typeRequestShut = 1;
    Context mContext;
    private List<UserItem> list;//成员数据


    int operatePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_group_members);
        setLabel(R.string.group_members);
        EventBus.getDefault().register(this);
        mContext = this;
        listView = (SwipeMenuListView) findViewById(R.id.list);
        adapter = new Adapter(this);
        listView.setAdapter(adapter);
//        theme_top_banner_left = (TextView) findViewById(R.id.theme_top_banner_left);
//        theme_top_banner_middle = (TextView) findViewById(R.id.theme_top_banner_middle);
//        theme_top_banner_right = (TextView) findViewById(R.id.theme_top_banner_right);
        group = (GroupTeam) getIntent().getSerializableExtra("group");
        list = (List<UserItem>) getIntent().getSerializableExtra("members");
        if (group == null && list == null)
            return;
        if (list != null) {
            setLabel(R.string.members_count);
            adapter.setData(list);
        } else {
            getMembers();
        }
//        theme_top_banner_middle.setText("群成员");
//        theme_top_banner_left.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                finish();
//
//            }
//        });
        if (group != null)
            if (String.valueOf(group.getGroupmasterid()).equals(SharedToken.getUserId())) {
                listView.setMenuCreator(creatorMaster);
            } else {
                listView.setMenuCreator(creatorMember);
            }

        listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                operatePosition = position;
                //成员没有禁言功能
                if (index == 0 && String.valueOf(group.getGroupmasterid()).equals(SharedToken.getUserId())) {
                    int viewType = adapter.getItemViewType(position);
                    if (viewType == isShutupYes) {
                        typeRequestShut = typeRequestShutNo;
                        //ToastUtil.showToast(GroupMembersAct.this, "解禁");
                    } else {
                        // shut
                        typeRequestShut = typeRequestShutYes;
                        //ToastUtil.showToast(GroupMembersAct.this, "禁言");
                    }
                    shut(group.getGroupid(), adapter.getItem(position).getUserid());
                } else {
                    report(String.valueOf(adapter.getItem(position).getUserid()));

                }

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                UserItem user = adapter.getItem(arg2);
                Intent intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", user.getUserid());
                startActivity(intent);

            }
        });
    }

    public void onEvent(EventUpdateAlias event) {
        if (event != null && list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (event.getUserid() != 0 && event.getUserid() == list.get(i).getUserid()) {
                    list.get(i).setAlias(event.getAlias());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //成员的
    SwipeMenuCreator creatorMember = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(GroupMembersAct.this);
            deleteItem.setBackground(R.color.theme_red_bg);
            deleteItem.setWidth((int) GroupMembersAct.this.getResources().getDimension(R.dimen.cart_delete_width));
            deleteItem.setTitle(R.string.jubao);
            deleteItem.setTitleSize(16);
            deleteItem.setTitleColor(GroupMembersAct.this.getResources().getColor(R.color.white));
            menu.addMenuItem(deleteItem);
        }
    };

    //群主的
    SwipeMenuCreator creatorMaster = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            int viewType = menu.getViewType();
            //LogUtil.logE("creator", viewType);
            SwipeMenuItem item2 = new SwipeMenuItem(GroupMembersAct.this);
            item2.setBackground(R.color.black7);
            item2.setWidth((int) GroupMembersAct.this.getResources().getDimension(R.dimen.cart_delete_width));
            item2.setTitle(R.string.freeChat_shutUp);
            item2.setTitleSize(16);
            item2.setTitleColor(GroupMembersAct.this.getResources().getColor(R.color.white));
            menu.addMenuItem(item2);

            SwipeMenuItem deleteItem = new SwipeMenuItem(GroupMembersAct.this);
            deleteItem.setBackground(R.color.theme_red_bg);
            deleteItem.setWidth((int) GroupMembersAct.this.getResources().getDimension(R.dimen.cart_delete_width));
            deleteItem.setTitle(R.string.jubao);
            deleteItem.setTitleSize(16);
            deleteItem.setTitleColor(GroupMembersAct.this.getResources().getColor(R.color.white));
            menu.addMenuItem(deleteItem);
        }
    };

    /**
     * 获取群成员列表
     */
    void getMembers() {
        Params params = new Params();
        params.put("groupid", group.getGroupid());
        new HttpEntity(this).commonPostData(Method.imShowMembers, params, this);
    }

    /**
     * 禁言和解禁
     *
     * @param groupid
     * @param userId
     */
    private void shut(long groupid, long userId) {
        Params params = new Params();
        params.put("groupid", groupid);
        params.put("memberids", userId);
        params.put("type", typeRequestShut);
        new HttpEntity(this).commonPostData(Method.imShutup, params, this);
    }

    /**
     * 举报用户
     *
     * @param userId
     */
    private void report(String userId) {
        Intent intent = new Intent(this, ReportStatusesOrUserAct.class);
        intent.putExtra("userId", userId);
        intent.putExtra("type", ReportStatusesOrUserAct.typeUser);
        startActivity(intent);
    }

    class Adapter extends CommonAdapter {
        Context mContext;
        List<UserItem> list;

        public Adapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<UserItem>();
        }

        class Holder {
            ImageView user_img;
            TextView name;
            TextView user_gender_age;
            TextView user_city;
            TextView tvShutUp;
            ImageView ivGender;//性别
            View linUserInfo;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public UserItem getItem(int position) {
            return list.get(position);
        }

        public void setData(List<UserItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }


        public void setItem(int position, UserItem user) {
            list.set(position, user);
            notifyDataSetChanged();
        }


        @Override
        public int getItemViewType(int position) {
            final UserItem user = list.get(position);
            if (user.getIsshutup() == UserItem.isshutupYes) {
                return isShutupYes;// 0表示已经禁言
            } else {
                return isShutupNo;// 没有禁言
            }
        }

        @Override
        public View getView(final int i, View con, ViewGroup arg2) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.im_group_members_list_item, null);
                ho.user_img = (ImageView) con.findViewById(R.id.user_img);
                ho.name = (TextView) con.findViewById(R.id.name);
                ho.user_gender_age = (TextView) con.findViewById(R.id.user_gender_age);
                ho.user_city = (TextView) con.findViewById(R.id.user_city);
                ho.tvShutUp = (TextView) con.findViewById(R.id.tvShutUp);
                ho.ivGender = (ImageView) con.findViewById(R.id.iv_gender);
                ho.linUserInfo = con.findViewById(R.id.linUserInfo);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();

            final UserItem user = list.get(i);
            PicassoUtil.load(mContext, user.getImgmiddleurl(), ho.user_img);
//            ho.name.setText(user.getNickname());
            ho.name.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//            ho.user_city.setText(user.getCityname());
            if (user.getCityname() == null || user.getCityname().equals("")) {
                ho.user_city.setVisibility(View.GONE);
            } else {
                ho.user_city.setVisibility(View.VISIBLE);
                ho.user_city.setText(user.getCityname());
            }
//            ho.user_gender_age.setText(user.getAge() + "");
            if (user.getAge() == 0) {
                ho.user_gender_age.setVisibility(View.GONE);
            } else {
                ho.user_gender_age.setVisibility(View.VISIBLE);
                ho.user_gender_age.setText(String.format(MyApplication.getContext().getString(R.string.im_age), user.getAge()));
            }
            ho.ivGender.setVisibility(View.VISIBLE);
            if (user.getGender() == UserItem.gender_boy) {
//                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
//                ho.user_gender_age.setBackgroundResource(R.drawable.shape_boy_bg);
                ho.ivGender.setImageResource(R.drawable.icon_male);
            } else if (user.getGender() == UserItem.gender_girl) {
//                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
//                ho.user_gender_age.setBackgroundResource(R.drawable.shape_girl_bg);
                ho.ivGender.setImageResource(R.drawable.icon_female);
            } else {
//                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                ho.user_gender_age.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
                ho.ivGender.setVisibility(View.GONE);
            }

            if (user.getIsshutup() == UserItem.isshutupYes) {
                ho.tvShutUp.setVisibility(View.VISIBLE);
            } else {
                ho.tvShutUp.setVisibility(View.GONE);
            }

            if (ho.ivGender.getVisibility() == View.GONE && ho.user_city.getVisibility() == View.GONE && ho.user_gender_age.getVisibility() == View.GONE) {
                ho.linUserInfo.setVisibility(View.GONE);
            } else {
                ho.linUserInfo.setVisibility(View.VISIBLE);
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
        switch(methodName) {
            case Method.imShowMembers:
                if (item.getIssuccess() == BaseItem.successTag) {
                    String obj = JSONHelper.getStringFromObject(item.getResult());
                    list = JSONHelper.getList(obj, UserItem.class);
                    adapter.setData(list);
                }
                break;
            case Method.imShutup:
                UserItem user = adapter.getItem(operatePosition);
                switch(typeRequestShut) {
                    case typeRequestShutYes:
                        user.setIsshutup(UserItem.isshutupYes);
                        break;
                    case typeRequestShutNo:
                        user.setIsshutup(UserItem.isshutupNo);
                        break;
                }
                //listView.setMenuCreator(creator);//仍然无法动态更改
                adapter.setItem(operatePosition, user);
                break;
        }

    }

}
