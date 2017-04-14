package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.im.rong.MessageSettingDB;
import com.stbl.stbl.act.im.rong.MyNotiMessage;
import com.stbl.stbl.act.im.rong.NotificationType;
import com.stbl.stbl.act.im.rong.TopSettingDB;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.IMSetting;
import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.User;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.DiscussionMember;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.item.im.UserList;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 讨论组信息页
 */
public class DiscussionInfoAct extends ThemeActivity implements FinalHttpCallback, OnClickListener {
    GridView grid;
    TextView tvName, tvAddTime, tvPeopleNum;
    TextView btnDelete;
    String discussionId;
    Adapter adapter;
    Context mContext;
    DiscussionMember members;
    DiscussionTeam discussionInfo;
    final int requestInvite = 101;
    public final static int requestUpdate = 102;
    ImageView imgMessageTop, imgNotify, imgSaveContacts;//消息置顶，消息免打扰，保存到通讯录
    RelativeLayout rlAllMember; //全部成员
    boolean isNotNotify = false;
    boolean isMessageTop = false;//是否消息置顶
    boolean isSaveContacts = false; //是否保存在通讯录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion_info);
        mContext = this;
        discussionId = getIntent().getStringExtra("discussionId");
        if (discussionId == null)
            return;
        setLabel(R.string.discussion_info);
        initViews();
        getBasicInfo();
        getMemeberInfos();
        EventBus.getDefault().register(this);
        showIsTop();
    }

    public void onEvent(EventUpdateAlias event) {
        if (event != null && members != null && members.getMembers() != null) {
            List<UserItem> data = members.getMembers();
            for (int i = 0; i < data.size(); i++) {
                if (event.getUserid() != 0 && event.getUserid() == data.get(i).getUserid()) {
                    data.get(i).setAlias(event.getAlias());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void onEvent(EventTypeCommon type) {
        getMemeberInfos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void initViews() {
        grid = (GridView) findViewById(R.id.gridMembers);
        adapter = new Adapter(this);
        grid.setAdapter(adapter);
        tvName = (TextView) findViewById(R.id.tvName);
        tvAddTime = (TextView) findViewById(R.id.tvAddTime);
        btnDelete = (TextView) findViewById(R.id.btnDelete);
        tvPeopleNum = (TextView) findViewById(R.id.tvPeopleNum);
        imgNotify = (ImageView) findViewById(R.id.imgNotify);
        imgMessageTop = (ImageView) findViewById(R.id.imgMessageTop);
        imgSaveContacts = (ImageView) findViewById(R.id.imgSaveContacts);
        rlAllMember = (RelativeLayout) findViewById(R.id.rl_all_member);

        imgMessageTop.setOnClickListener(this);
        imgSaveContacts.setOnClickListener(this);
        rlAllMember.setOnClickListener(this);
        final MessageSettingDB db = new MessageSettingDB(this);
        isNotNotify = db.isNotNotify(MessageSettingDB.typeDiscussion, discussionId);
        //初始化
        if (isNotNotify) {
            imgNotify.setImageResource(R.drawable.icon_open);
        } else {
            imgNotify.setImageResource(R.drawable.icon_shut);
        }

        imgNotify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNotNotify) {
                    imgNotify.setImageResource(R.drawable.icon_shut);
                    db.update(MessageSettingDB.typeDiscussion, discussionId, MessageSettingDB.stateOn);
                } else {
                    imgNotify.setImageResource(R.drawable.icon_open);
                    db.update(MessageSettingDB.typeDiscussion, discussionId, MessageSettingDB.stateOff);
                }
                isNotNotify = !isNotNotify;

            }
        });

        findViewById(R.id.linNoticeBoard).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgMessageTop:
                setTop();
                break;
            case R.id.imgSaveContacts:
                switchSaveContacts((ImageView) v);
                break;
            case R.id.rl_all_member:
                if (members != null && members.getMembers() != null) {
                    Intent intent = new Intent(this, GroupMembersAct.class);
                    intent.putExtra("members", (Serializable) members.getMembers());
                    startActivity(intent);
                }
                break;
            case R.id.linNoticeBoard:
                enterInputAct(DiscussionInputAct.type_message_board, 100);
                break;
        }
    }

    void enterInputAct(int type, int maxLength) {
        Intent intent = new Intent(this, DiscussionInputAct.class);
        intent.putExtra("type", type);
        intent.putExtra("discussionInfo", discussionInfo);
        startActivity(intent);
    }


    void showIsTop() {
        Params params = new Params();
        params.put("businessid", discussionId);
        params.put("businesstype", IMSetting.topTypeDiscussion);
        new HttpEntity(this).commonPostData(Method.getTargetTopStatus, params, this);
    }


    void setTop() {
        Params params = new Params();
        params.put("businessid", discussionId);
        params.put("businesstype", IMSetting.topTypeDiscussion);
        params.put("updatetype", IMSetting.updateTop);
        if (isMessageTop)
            params.put("opeatetype", IMSetting.topOperateDelete);
        else
            params.put("opeatetype", IMSetting.topOperateAdd);
        new HttpEntity(this).commonPostData(Method.setTargetTop, params, this);
    }


    //切换保存到通讯录图片
    private void switchSaveContacts(ImageView view) {
        if (!isSaveContacts) {
            view.setImageResource(R.drawable.icon_open);
            saveDiscussion();
        } else {
            view.setImageResource(R.drawable.icon_shut);
            removeDiscussion();
        }
        isSaveContacts = !isSaveContacts;
    }


    OnClickListener deleteListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            TipsDialog.popup(mContext, R.string.is_sure_to_discard_discussion_team, R.string.cancel, R.string.queding, new OnTipsListener() {

                @Override
                public void onConfirm() {
                    deleteDiscussion();

                }

                @Override
                public void onCancel() {

                }
            });
        }
    };


    OnClickListener leaveListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            TipsDialog.popup(mContext, R.string.is_sure_to_leave_discussion_team, R.string.cancel, R.string.queding, new OnTipsListener() {

                @Override
                public void onConfirm() {
                    quitDiscussion();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    };

    //添加到通讯录
    private void saveDiscussion() {
        Params params = new Params();
        params.put("groupid", discussionId);
        new HttpEntity(this).commonPostData(Method.imDiscussionCollectionAdd, params, this);
    }

    //从通讯录移除
    private void removeDiscussion() {
        Params params = new Params();
        params.put("groupid", discussionId);
        new HttpEntity(this).commonPostData(Method.imDiscussionCollectionRemove, params, this);

    }

    private void deleteDiscussion() {
        Params params = new Params();
        params.put("groupid", discussionId);
        new HttpEntity(this).commonPostData(Method.imDeleteDiscussions, params, this);

    }

    private void quitDiscussion() {
        Params params = new Params();
        params.put("groupid", discussionId);
        new HttpEntity(this).commonPostData(Method.imDiscussionQuit, params, this);

    }

    StringBuffer sbInvitedNames = new StringBuffer();
    StringBuffer sbInviteUserIds = new StringBuffer();

    void InviteJoinTeam(List<UserItem> listUser) {
        List<Long> listIds = new ArrayList<Long>(listUser.size());
        for (int i = 0; i < listUser.size(); i++) {
            listIds.add(listUser.get(i).getUserid());
            sbInvitedNames.append(listUser.get(i).getNickname() + ",");
            sbInviteUserIds.append(listUser.get(i).getUserid() + ",");
        }
        if (listUser.size() > 0) {
            sbInviteUserIds.deleteCharAt(sbInviteUserIds.length() - 1);
            sbInvitedNames.deleteCharAt(sbInvitedNames.length() - 1);
        }

        JSONObject json = new JSONObject();
        json.put("groupid", discussionId);
        json.put("memberids", listIds);
        new HttpEntity(this).commonPostJson(Method.imInviteMembers, json.toString(), this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == requestInvite) {
//            switch (resultCode) {
//                case RESULT_OK:
//                    UserList users = (UserList) data.getSerializableExtra("users");
//                    List<UserItem> listUser = users.getList();
//                    InviteJoinTeam(listUser);
//                    break;
//
//            }
//        }

        switch (requestCode) {
            case requestInvite:
                if (resultCode == RESULT_OK) {
                    UserList users = (UserList) data.getSerializableExtra("users");
                    List<UserItem> listUser = users.getList();
                    InviteJoinTeam(listUser);
                    //这里要更新人数 add 0909
                    RongDB userDB = new RongDB(this);

                    IMAccount account = new IMAccount(RongDB.typeDiscussion, String.valueOf(discussionInfo.getGroupid()), discussionInfo.getGroupname(), "", UserItem.certificationNo, "");
                    account.setPeopleNum(discussionInfo.getMemberscount() + listUser.size());
                    userDB.insert(account);
                }
                break;
            case requestUpdate:
                if (resultCode == RESULT_OK) {
                    discussionInfo = (DiscussionTeam) data.getSerializableExtra("discussionInfo");
                    //LogUtil.logE("返回名字:" + discussion.getGroupname());
                    tvName.setText(discussionInfo.getGroupname());
                    discussionInfo.setGroupname(discussionInfo.getGroupname());
                    RongDB userDB = new RongDB(this);
                    IMAccount account = new IMAccount(RongDB.typeDiscussion, String.valueOf(discussionInfo.getGroupid()), discussionInfo.getGroupname(), "", UserItem.certificationNo, "");
                    account.setPeopleNum(discussionInfo.getMemberscount());
                    userDB.insert(account);
                    EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateContactList));
                    EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateDiscussionName, discussionInfo.getGroupname()));

                }
                break;
        }


    }


    class Adapter extends CommonAdapter {
        Context mContext;
        List<UserItem> list;

        public Adapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<UserItem>();
        }

        public void setData(List<UserItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (list != null) {
                if (discussionInfo != null && discussionInfo.getGroupmasterid() == Long.valueOf(SharedToken.getUserId())) {
                    if (list.size() <= 4 * 4 - 2) {
                        return list.size() + 2;
                    }
                } else {
                    if (list.size() <= 4 * 4 - 1)
                        return list.size() + 1;
                }
                return 4 * 4;
            }
            return 0;
        }

        class Holder {
            ImageView imgUser;
            TextView tvName;
        }

        @Override
        public View getView(final int i, View con, ViewGroup parent) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.team_create_member_item, null);
                ho.imgUser = (ImageView) con.findViewById(R.id.imgUser);
                ho.tvName = (TextView) con.findViewById(R.id.tvName);
                con.findViewById(R.id.imgDelete).setVisibility(View.GONE);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();
            ho.tvName.setVisibility(View.VISIBLE);
            ho.imgUser.setVisibility(View.VISIBLE);
            if (i < getCount() - 2) {
                final UserItem user = list.get(i);
                PicassoUtil.load(mContext, user.getImgurl(), ho.imgUser);
                ho.tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//                ho.tvName.setText(user.getNickname());
                ho.imgUser.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mContext, TribeMainAct.class);
                        intent.putExtra("userId", user.getUserid());
                        startActivityForResult(intent, requestInvite);
                    }
                });
            } else {
                if (i == getCount() - 1) {
                    if (discussionInfo != null && discussionInfo.getGroupmasterid() != Long.valueOf(SharedToken.getUserId())) {
                        PicassoUtil.load(mContext, R.drawable.icon_jiashangpin, ho.imgUser);
                        ho.tvName.setText(R.string.invite_members);
                        ho.imgUser.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (discussionInfo == null)
                                    return;
                                Intent intent = new Intent(mContext, SelectFriendActivity.class);
                                intent.putExtra("members", members);
                                startActivityForResult(intent, requestInvite);
                            }
                        });
                    } else {
                        PicassoUtil.load(mContext, R.drawable.icon_im_delete, ho.imgUser);
                        ho.tvName.setText(R.string.delete_members);
                        ho.imgUser.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (members == null || discussionInfo == null)
                                    return;
                                Intent intent = new Intent(mContext, DiscussionDeleteMembersAct.class);
                                intent.putExtra("members", members);
                                intent.putExtra("discussionInfo", discussionInfo);
                                mContext.startActivity(intent);

                            }
                        });
                    }
                } else if (i == getCount() - 2) {
                    // 判断是否是组长
                    if (discussionInfo != null && discussionInfo.getGroupmasterid() == Long.valueOf(SharedToken.getUserId())) {
                        PicassoUtil.load(mContext, R.drawable.icon_jiashangpin, ho.imgUser);
                        ho.tvName.setText(R.string.invite_members);
                        ho.imgUser.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (discussionInfo == null)
                                    return;
                                Intent intent = new Intent(mContext, SelectFriendActivity.class);
                                intent.putExtra("members", members);
                                startActivityForResult(intent, requestInvite);
                            }
                        });
                    } else {
                        final UserItem user = list.get(i);
                        PicassoUtil.load(mContext, user.getImgurl(), ho.imgUser);
                        ho.tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//                        ho.tvName.setText(user.getNickname());
                        ho.imgUser.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent(mContext, TribeMainAct.class);
                                intent.putExtra("userId", user.getUserid());
                                startActivityForResult(intent, requestInvite);
                            }
                        });
                    }

                }
            }

            return con;
        }
    }

    /**
     * 获取基本信息
     */
    void getBasicInfo() {
        Params params = new Params();
        params.put("groupid", discussionId);
        new HttpEntity(this).commonPostData(Method.imShowDiscussion, params, this);
    }

    /**
     * 获取成员信息
     */
    void getMemeberInfos() {
        Params params = new Params();
        params.put("groupid", discussionId);
        new HttpEntity(this).commonPostData(Method.imShowDiscussionMembers, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item == null) {
            ToastUtil.showToast(this, getString(R.string.im_request_err));
            return;
        }
        //item is null
        if (item.getIssuccess() != BaseItem.successTag) {

            switch (methodName) { //失败后操作
                case Method.imDiscussionCollectionAdd://保存到通讯录
                    imgSaveContacts.setImageResource(R.drawable.icon_shut);
                    isSaveContacts = false;
                    break;
                case Method.imDiscussionCollectionRemove://从通讯录移除
                    imgSaveContacts.setImageResource(R.drawable.icon_open);
                    isSaveContacts = true;
                    break;
                case Method.imShowDiscussion:
                    if (item.getErr().getErrcode() == ServerError.ERR_DISCUSSION_NO_EXIST || item.getErr().getErrcode() == ServerError.ERR_DISCUSSION_NO_EXIST1 || item.getErr().getErrcode() == ServerError.ERR_DISCUSSION_NO_MEMBER) {
                        TipsDialog dialog = TipsDialog.popup(this, item.getErr().getMsg(), "确定", new OnTipsListener() {
                            @Override
                            public void onConfirm() {
                                finish();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                        dialog.setCancelable(false);
                    }
                    break;
                case Method.imShowDiscussionMembers:
                    //pass 为了多次重复提示
                    break;
                default:
                    ToastUtil.showToast(this, item.getErr().getMsg());
                    break;
            }

            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.imShowDiscussion:
                discussionInfo = JSONHelper.getObject(obj, DiscussionTeam.class);
                if (discussionInfo == null) return;
                RongDB userDB = new RongDB(this);
                IMAccount account = new IMAccount(RongDB.typeDiscussion, String.valueOf(discussionInfo.getGroupid()), discussionInfo.getGroupname(), "", UserItem.certificationNo, "");
                account.setPeopleNum(discussionInfo.getMemberscount());
                userDB.insert(account);
                EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateContactList));
                EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateDiscussionName, discussionInfo.getGroupname()));
                //以上刷新讨论组名称，以防可能名称有改变，。
                if (discussionInfo.getGroupmasterid() == Long.valueOf(SharedToken.getUserId())) {
                    findViewById(R.id.linNoticeBoard).setVisibility(View.VISIBLE);
                    findViewById(R.id.viewNoticeBoard).setVisibility(View.VISIBLE);
                    btnDelete.setText(R.string.dismiss_discussion_team);
                    btnDelete.setOnClickListener(deleteListener);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.test_right, 0);
                    findViewById(R.id.linDesc).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(DiscussionInfoAct.this, DiscussionInputAct.class);
                            intent.putExtra("discussionInfo", discussionInfo);
                            intent.putExtra("type", DiscussionInputAct.type_name);
                            DiscussionInfoAct.this.startActivityForResult(intent, requestUpdate);
                        }
                    });
                } else {
                    btnDelete.setText(R.string.leave_discussion_team);
                    btnDelete.setOnClickListener(leaveListener);
                }
                if (discussionInfo.getIscollect() != 0) {
                    isSaveContacts = true;
                    imgSaveContacts.setImageResource(R.drawable.icon_open);
                }
                tvName.setText(discussionInfo.getGroupname());
                tvAddTime.setText(DateUtil.getDateToFormat(String.valueOf(discussionInfo.getJointime())));
                adapter.notifyDataSetChanged();
                break;
            case Method.imShowDiscussionMembers:
                members = JSONHelper.getObject(obj, DiscussionMember.class);
                tvPeopleNum.setText("(" + members.getMembers().size() + ")");
                LogUtil.logE("size:" + members.getMembers().size());
                if (members != null && members.getMembers() != null) {
                    adapter.setData(members.getMembers());
//                    ViewUtils.setAdapterViewHeightNoMargin(grid, 4);
                }
                break;
            case Method.imDeleteDiscussions:
                ToastUtil.showToast(R.string.dismiss_success);
                EventBus.getDefault().post(new IMEventType(IMEventType.typeQuitDiscussion));
                finish();
                break;
            case Method.imDiscussionQuit:
                ToastUtil.showToast(R.string.leave_success);
                EventBus.getDefault().post(new IMEventType(IMEventType.typeQuitDiscussion));
                finish();
                break;
            case Method.imInviteMembers:
                getMemeberInfos();
                sendNotiInvire(discussionId);
                break;
            case Method.imDiscussionCollectionAdd://保存到通讯录

                break;
            case Method.imDiscussionCollectionRemove://从通讯录移除

                break;
            case Method.getTargetTopStatus:
                IMSetting settings = JSONHelper.getObject(obj, IMSetting.class);
                if (settings.getIstop() == IMSetting.topYes) {
                    imgMessageTop.setImageResource(R.drawable.icon_open);
                    isMessageTop = true;
                } else {
                    imgMessageTop.setImageResource(R.drawable.icon_shut);
                    isMessageTop = false;
                }
                break;
            case Method.setTargetTop:
                isMessageTop = !isMessageTop;
                TopSettingDB settingDB = new TopSettingDB(this);
                if (isMessageTop) {
                    imgMessageTop.setImageResource(R.drawable.icon_open);
                    settingDB.insert(IMSettingStatus.businesstypeDiscussion, discussionId, TopSettingDB.stateTopYes);
                } else {
                    imgMessageTop.setImageResource(R.drawable.icon_shut);
                    settingDB.delete(IMSettingStatus.businesstypeDiscussion, discussionId);
                }
                break;
        }
    }


    public void sendNotiInvire(String targetId) {
        MyNotiMessage message = MyNotiMessage.obtain(String.valueOf(NotificationType.typeInviteJoin));
        message.setOpname(SharedUser.getUserNick());
        message.setOpid(SharedToken.getUserId());
        message.setName(sbInvitedNames.toString());
        message.setOpedid(sbInviteUserIds.toString());
        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.GROUP, targetId, message, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Log.e("RongRedPacketProvider", "-----onError--" + errorCode);
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.e("RongRedPacketProvider", "-----onSuccess--" + integer);
            }
        });
    }
}
