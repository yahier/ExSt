package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.ConversationActivity;
import com.stbl.stbl.act.im.rong.MyNotiMessage;
import com.stbl.stbl.act.im.rong.NotificationType;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.UserList;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.model.Conversation.ConversationType;

/**
 * 创建讨论组--输入名称
 *
 * @author lenovo
 */
public class CreateDiscussionTeamAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
    EditText input;
    GridView gridMembers;
    Adapter adapter;
    final int requestChooseFriendCode = 101;
    Context mContext;
    final int maxSelected = 500;
    List<UserItem> listUser = new ArrayList<UserItem>();
    public final static int maxNameLength = 24;
    final String tag = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_group_create_act);
        setLabel(R.string.create_discussion);
        mContext = this;
        input = (EditText) findViewById(R.id.input);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
        //findViewById(R.id.theme_top_banner_right).setOnClickListener(this);
        gridMembers = (GridView) findViewById(R.id.gridMembers);
        adapter = new Adapter(this);
        gridMembers.setAdapter(adapter);
        setRightText(R.string.completed, this);

        input.addTextChangedListener(new TextListener() {
            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.toString().length() > maxNameLength) {
                    String value = edit.toString();
                    input.setText(value.substring(0, maxNameLength));
                    input.setSelection(input.length());
                }

            }
        });
    }

    @Override
    public void onClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, Config.interClickTime);
        switch(view.getId()) {
            case R.id.theme_top_banner_left:
                finish();
                break;
            case R.id.theme_top_tv_right:
                createTeam();
                break;
            case R.id.delete:
                input.setText("");
                break;
        }

    }

    // 服务器创建讨论组，其实是群
    void createTeam() {
        String groupname = input.getText().toString();
        if (groupname.trim().equals("")) {
            ToastUtil.showToast(R.string.input_discussion_name_first);
            return;
        }

        if (groupname.length() > maxNameLength) {
            ToastUtil.showToast(R.string.input_discussion_name_first);
            return;
        }
        WaitingDialog.show(this, false);
        JSONObject json = new JSONObject();
        json.put("groupname", groupname);
        json.put("invateids", adapter.getFriendsid());
        new HttpEntity(this).commonPostJson(Method.createDiscussionTeam, json.toString(), this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestChooseFriendCode) {
            switch(resultCode) {
                case RESULT_OK:
                    UserList users = (UserList) data.getSerializableExtra("users");
                    listUser = users.getList();
                    adapter.setData(listUser);
                    break;

            }
            // 如果进入转发页面再返回
        }
    }

    class Adapter extends CommonAdapter {
        Context mContext;
        List<UserItem> list;

        public Adapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<UserItem>();
        }

        public List<String> getFriendsid() {
            List<String> listIds = new ArrayList<String>(list.size());
            for (int i = 0; i < list.size(); i++) {
                listIds.add(String.valueOf(list.get(i).getUserid()));
            }
            return listIds;

        }


        public String getFriendsName() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                //String name = list.get(i).getAlias() == null || list.get(i).getAlias().equals("") ? list.get(i).getNickname() : list.get(i).getAlias();
                String name = list.get(i).getNickname();
                sb.append(String.valueOf(name + ","));
            }
            if (list.size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();

        }

        public void setData(List<UserItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 2 : list.size() + 2;
        }

        class Holder {
            ImageView imgUser, imgDelete;
            TextView tvName;
        }

        @Override
        public View getView(final int i, View con, ViewGroup parent) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.team_create_member_item, null);
                ho.imgUser = (ImageView) con.findViewById(R.id.imgUser);
                ho.imgDelete = (ImageView) con.findViewById(R.id.imgDelete);
                ho.tvName = (TextView) con.findViewById(R.id.tvName);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();

            if (i == 0) {
                PicassoUtil.load(mContext, R.drawable.icon_jiashangpin, ho.imgUser);
                ho.tvName.setText(R.string.invite_members);
                ho.imgDelete.setVisibility(View.GONE);
                ho.imgUser.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        choiceFriends();
                    }
                });
            } else if (i == 1){
                PicassoUtil.load(mContext, SharedUser.getUserItem().getImgurl(), ho.imgUser);
                ho.tvName.setText(SharedUser.getUserItem().getNickname());
                ho.imgDelete.setVisibility(View.GONE);
                ho.imgUser.setOnClickListener(null);
            } else {
                UserItem user = list.get(i - 2);
                PicassoUtil.load(mContext, user.getImgurl(), ho.imgUser);
                ho.tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//                ho.tvName.setText(user.getNickname());
                ho.imgDelete.setVisibility(View.VISIBLE);
                ho.imgUser.setOnClickListener(null);
                ho.imgDelete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        list.remove(i - 2);
                        notifyDataSetChanged();
                    }
                });
            }
            return con;
        }
    }

    void choiceFriends() {
        Intent intent = new Intent(this, SelectFriendActivity.class);
        intent.putExtra("maxSelected", maxSelected);
        UserList userList = new UserList();
        userList.setList(listUser);
        intent.putExtra(SelectFriendActivity.EXTRA_SELECTED_FRIEND_LIST, userList);
        startActivityForResult(intent, requestChooseFriendCode);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        switch(methodName) {
            case Method.createDiscussionTeam:
                LogUtil.logE(tag,"接口调用成功:createDiscussionTeam");
                String obj = JSONHelper.getStringFromObject(item.getResult());
                final DiscussionTeam discussion = JSONHelper.getObject(obj, DiscussionTeam.class);
                //这里要更新人数 add 0909
                RongDB userDB = new RongDB(this);

                IMAccount account = new IMAccount(RongDB.typeDiscussion, String.valueOf(discussion.getGroupid()), discussion.getGroupname(), "", UserItem.certificationNo, "");
                account.setPeopleNum(discussion.getMemberscount() + listUser.size());
                userDB.insert(account);
                RongIM.getInstance().getRongIMClient().joinGroup(String.valueOf(discussion.getGroupid()), discussion.getGroupname(), new RongIMClient.OperationCallback() {

                    @Override
                    public void onSuccess() {
                        ToastUtil.showToast(R.string.create_success);
                        LogUtil.logE(tag,"讨论组创建成功");
                        //RongIM.getInstance().startConversation(mContext, ConversationType.GROUP, String.valueOf(discussion.getGroupid()), discussion.getGroupname());
                        // 在这里用数据库存储讨论组的名称。
                        chatDiscussion(String.valueOf(discussion.getGroupid()), discussion.getGroupname());


                        finish();
                    }

                    @Override
                    public void onError(ErrorCode arg0) {
                        LogUtil.logE(tag,"讨论组创建失败"+arg0.getValue());
                    }
                });
                break;
        }

    }

    void chatDiscussion(final String targetUserId, String title) {
        Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(ConversationType.GROUP.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("typeLocal", ConversationActivity.typeLocalDiscussion).appendQueryParameter("title", title).build();
        startActivity(new Intent("android.intent.action.VIEW", uri));



        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNoti(targetUserId);
            }
        }, 100);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotiInvire(targetUserId);
            }
        }, 200);

    }


    //发送 创建讨论组通知
    public void sendNoti(String targetId) {
        MyNotiMessage mesage = MyNotiMessage.obtain(String.valueOf(NotificationType.typeCreateDiscussion));
        mesage.setOpid(SharedToken.getUserId());
        mesage.setOpname(SharedUser.getUserNick());
        RongIM.getInstance().getRongIMClient().sendMessage(ConversationType.GROUP, targetId, mesage, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Log.e(tag+" 发送创建", "-----onError--" + errorCode);
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.e(tag+" 发送创建", "-----onSuccess--" + integer);
            }
        });
    }


    //还没调用 发送 讨论组邀请加入通知
    public void sendNotiInvire(String targetId) {
        if (adapter.getFriendsName().trim().equals("")) return;
        MyNotiMessage message = MyNotiMessage.obtain(String.valueOf(NotificationType.typeInviteJoin));
        message.setOpname(SharedUser.getUserNick());
        message.setOpid(SharedToken.getUserId());
        message.setName(adapter.getFriendsName());
        List<String> listUserId = adapter.getFriendsid();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < listUserId.size(); i++) {
            sb.append(listUserId.get(i)).append(",");
        }
        if (listUserId.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        message.setOpedid(sb.toString());
        RongIM.getInstance().getRongIMClient().sendMessage(ConversationType.GROUP, targetId, message, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Log.e(tag+" 发送邀请语", "-----onError--" + errorCode);
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.e(tag+" 发送邀请语", "-----onSuccess--" + integer);
            }
        });
    }
}
