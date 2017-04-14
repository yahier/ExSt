package yahier.exst.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.im.SharedGroups;
import com.stbl.stbl.act.im.rong.AdOtherPayMessage;
import com.stbl.stbl.act.im.rong.BusinessCardMessage;
import com.stbl.stbl.act.im.rong.CastBeanMessage;
import com.stbl.stbl.act.im.rong.ConversationActivity;
import com.stbl.stbl.act.im.rong.DiscussionInviteMessage;
import com.stbl.stbl.act.im.rong.GoodsMessage;
import com.stbl.stbl.act.im.rong.MessageSettingDB;
import com.stbl.stbl.act.im.rong.MyNotiMessage;
import com.stbl.stbl.act.im.rong.NotificationType;
import com.stbl.stbl.act.im.rong.OrderMessage;
import com.stbl.stbl.act.im.rong.RedPackectMessage;
import com.stbl.stbl.act.im.rong.StatusesMessage;
import com.stbl.stbl.act.im.rong.TopSettingDB;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.EmojiParseThread;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedOfficeAccount;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmojiTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * 消息主页的聊天item
 */
public class ContactAdapter extends BaseSwipeAdapter {
    Context mContext;
    List<Conversation> list;
    RongDB userDB;

    public final static int viewtypeCommon = 0;// 普通会话
    public final static int viewtypeSecretary = 1;// 小秘书
    public final static int headerViewCount = 1;
    private List<Boolean> listRequest = new ArrayList<Boolean>();


    public ContactAdapter(Context mContext) {
        this.mContext = mContext;
        this.list = new ArrayList<Conversation>();
        userDB = new RongDB(mContext);
    }


    public void setData(List<Conversation> list) {
        this.list.clear();
        this.listRequest.clear();
        this.list = list;
        TopSettingDB settingDb = new TopSettingDB(mContext);

        for (int i = 0; i < list.size(); i++) {
            listRequest.add(false);
            Conversation conversation = list.get(i);
            int type = 0;
            switch(conversation.getConversationType()) {
                case PRIVATE:
                    type = IMSettingStatus.businesstypePrivate;
                    break;
                case GROUP:
                    if (SharedGroups.isGroupMineOrMaster(conversation.getTargetId(), mContext)) {
                        type = IMSettingStatus.businesstypeGroup;
                    } else {
                        type = IMSettingStatus.businesstypeDiscussion;
                    }
                    break;
            }


            boolean isTop = settingDb.isAdded(type, conversation.getTargetId());
            conversation.setTop(isTop);
            list.set(i, conversation);

        }
        sortConversationList();
    }

    public void addGroupItem(Conversation con) {
        String targetId = con.getTargetId();
        TopSettingDB settingDb = new TopSettingDB(mContext);

        //判断列表中 是否已经加入
        for (Conversation con1 : list) {
            if (con1.getTargetId().equals(targetId)) {
                return;
            }
        }

        boolean isTop = settingDb.isAdded(IMSettingStatus.businesstypeGroup, targetId);
        con.setTop(isTop);
        this.list.add(con);
        this.listRequest.add(false);
        sortConversationList();
    }

    //加入置顶项
    public void addTopItem(Conversation con) {
        String targetId = con.getTargetId();
        //判断列表中 是否已经加入
        for (Conversation con1 : list) {
            if (con1.getTargetId().equals(targetId)) {
                return;
            }
        }

        this.list.add(con);
        this.listRequest.add(false);
        sortConversationList();
    }

    public void delete(int position) {
        this.list.remove(position);
        notifyDataSetChanged();
    }

    private void sortConversationList() {
        //LogUtil.logE("adapter", "排序");
        //Collections.sort(list, new CalendarComparator());
        int size = list.size();
        List<Conversation> listNew = new ArrayList<Conversation>(size);

        for (int i = 0; i < size; i++) {
            if (list.get(i).isTop()) {
                listNew.add(list.get(i));
            }
        }
        for (int i = 0; i < size; i++) {
            if (list.get(i).isTop() == false) {
                listNew.add(list.get(i));
            }
        }

        list = listNew;
        notifyDataSetChanged();
    }


    static class CalendarComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Conversation p1 = (Conversation) object1; // 强制转换
            Conversation p2 = (Conversation) object2;

//            int value = 0;
//            if (p1.isTop() && p2.isTop() == false)
//                value = -1;

//            int value = 0;
//            if (p1.isTop() && p2.isTop() == false)
//                value = 1;

            int value = 0;
            if (p1.isTop() == false && p2.isTop() == true)
                value = 1;
            else value = 0;
//
//            int value = 0;
//            if (p1.isTop() && p2.isTop() == false)
//                value = 1;
            return value;
        }
    }

    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }


    public int getUnReadCount() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            final Conversation conversation = list.get(i);
            MessageSettingDB db = new MessageSettingDB(mContext);
            if (conversation.getConversationType() == ConversationType.GROUP) {
                if (SharedGroups.isGroupMineOrMaster(conversation.getTargetId(), mContext)) {
                    boolean isSilence = db.isNotNotify(MessageSettingDB.typeGroup, conversation.getTargetId());
                    if (isSilence) continue;
                } else {
                    boolean isSilence = db.isNotNotify(MessageSettingDB.typeDiscussion, conversation.getTargetId());
                    if (isSilence) continue;
                }
            } else {
                boolean isSilence = db.isNotNotify(MessageSettingDB.typePrivate, conversation.getTargetId());
                if (isSilence) continue;
            }
            int unreadCount = conversation.getUnreadMessageCount();
            count += unreadCount;
        }
        return count;
    }


    @Override
    public int getItemViewType(int position) {
        Conversation conversation = getItem(position);
        if (conversation == null) {
            // LogUtil.logE(position + "  common");
            return viewtypeCommon;
        }
        if (SharedOfficeAccount.isOfficeAccount(conversation.getTargetId())) {
            return viewtypeSecretary;
        } else {
            //判断是不是群组 conversation。如果是群组也不能滑动删除
            if (conversation.getConversationType() == ConversationType.GROUP) {
                if (SharedGroups.isGroupMineOrMaster(conversation.getTargetId(), mContext)) {
                    return viewtypeSecretary;
                }

            }

            return viewtypeCommon;
        }
    }

    @Override
    public Conversation getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    class Holder {
        View linContent;
        ImageView userImg;
        TextView tvName;
        TextView tvTime;
        EmojiTextView tvContent;
        TextView tvCount;
        View item;
        SwipeLayout swipeLayout;
        TextView tvDelete;
        View imgSilence;
        View imgAuthorized;
        ImageView imgGroup;

    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        Holder ho = new Holder();
        View con = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, null);
        ho.item = con.findViewById(R.id.item);
        ho.swipeLayout = (SwipeLayout) con.findViewById(R.id.swipe_layout);
        ho.tvDelete = (TextView) con.findViewById(R.id.tv_delete);
        ho.tvName = (TextView) con.findViewById(R.id.tvName);
        ho.tvContent = (EmojiTextView) con.findViewById(R.id.tvContent);
        ho.tvTime = (TextView) con.findViewById(R.id.tvTime);
        ho.userImg = (ImageView) con.findViewById(R.id.imgUser);
        ho.linContent = con.findViewById(R.id.linContent);
        ho.tvCount = (TextView) con.findViewById(R.id.tvCount);
        ho.imgSilence = con.findViewById(R.id.imgSilence);
        ho.imgAuthorized = con.findViewById(R.id.imgAuthorized);
        ho.imgGroup = (ImageView) con.findViewById(R.id.imgGroup);
        con.setTag(ho);

        //  con.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,400));
        return con;
    }


    String getInviteString(String id, String name) {
        RongDB db = new RongDB(mContext);
        StringBuffer contentValue = new StringBuffer();
        if (id != null) {
            String[] names = name.split(",");
            String[] ids = id.split(",");
            try {
                for (int i = 0; i < names.length; i++) {
                    if (ids[i].endsWith(SharedToken.getUserId())) {
                        contentValue.append("您,");
                        continue;
                    }
                    IMAccount account = db.get(RongDB.typeUser, ids[i]);
                    if (account == null) {
                        contentValue.append(names[i]);
                    } else {
                        String value = account.getAlias();
                        if (value == null || value.equals("")) {
                            value = account.getNickname();
                        }

                        contentValue.append(value);
                    }
                    contentValue.append(",");
                }
                if (names.length > 0) {
                    contentValue.deleteCharAt(contentValue.length() - 1);
                }
            } catch(Exception e) {
                contentValue.append(name);
            }

        } else {
            contentValue.append(name);
        }

        return contentValue.toString();
    }

    @Override
    public void fillValues(final int position, View convertView) {
        Holder ho = (Holder) convertView.getTag();
        ho.swipeLayout.close();
        final Conversation conversation = list.get(position);//有越界
        if (conversation.isTop()) {
            ho.item.setBackgroundResource(R.color.im_top_bg);
        } else {
            ho.item.setBackgroundResource(R.color.white);
        }
        //新加
        ho.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        MessageContent message = conversation.getLatestMessage();
        final String targetId = conversation.getTargetId();
        int unreadCount = conversation.getUnreadMessageCount();
        //LogUtil.logE("position_______"+position+":"+unreadCount);
        if (unreadCount > 0) {
            ho.tvCount.setText(String.valueOf(unreadCount));
            ho.tvCount.setVisibility(View.VISIBLE);
        } else {
            ho.tvCount.setText(String.valueOf(0));
            ho.tvCount.setVisibility(View.GONE);
        }


        long receiveTime = conversation.getReceivedTime();
        if (receiveTime == 0) {
            ho.tvTime.setVisibility(View.GONE);
        } else {
            ho.tvTime.setVisibility(View.VISIBLE);
            ho.tvTime.setText(DateUtil.getHmOrMdHm(String.valueOf(receiveTime)));
        }


        String content;
        if (message instanceof TextMessage) {
            content = ((TextMessage) message).getContent();
            //ho.tvContent.setText(content);
        } else if (message instanceof BusinessCardMessage) {
            //ho.tvContent.setText(R.string.im_business_card);
            content = mContext.getString(R.string.im_business_card);
        } else if (message instanceof GoodsMessage) {
            //ho.tvContent.setText(R.string.im_goods);
            content = mContext.getString(R.string.im_goods);
        } else if (message instanceof StatusesMessage) {
            // ho.tvContent.setText(R.string.im_dynamic);
            content = mContext.getString(R.string.im_dynamic);
        } else if (message instanceof CastBeanMessage) {
            //ho.tvContent.setText(R.string.im_cast_bean2);
            content = mContext.getString(R.string.im_cast_bean2);
        } else if (message instanceof VoiceMessage) {
            //ho.tvContent.setText(R.string.im_voice);
            content = mContext.getString(R.string.im_voice);
        } else if (message instanceof ImageMessage) {
            //ho.tvContent.setText(R.string.im_photo);
            content = mContext.getString(R.string.im_photo);
        } else if (message instanceof RedPackectMessage) {
            //ho.tvContent.setText(R.string.im_red_packet);
            content = mContext.getString(R.string.im_red_packet);
        } else if (message instanceof OrderMessage) {
            //ho.tvContent.setText(R.string.im_order);
            content = mContext.getString(R.string.im_order);
        } else if (message instanceof DiscussionInviteMessage) {
            //ho.tvContent.setText(R.string.im_group_invite);
            content = mContext.getString(R.string.im_group_invite);
        }else if (message instanceof AdOtherPayMessage) {
            //ho.tvContent.setText(R.string.im_group_invite);
            content = mContext.getString(R.string.im_ad_other_pay);
        } else if (message instanceof MyNotiMessage) {
            //LogUtil.logE("MyNotiMessage");
            MyNotiMessage notiMessage = (MyNotiMessage) message;
            if (notiMessage.getType().equals(NotificationType.typeInviteJoin)) {
                String opname = notiMessage.getOpname();
                //String name = notiMessage.getName();
                String value = getInviteString(notiMessage.getOpedid(),notiMessage.getName());
                content = opname + String.format(mContext.getString(R.string.im_invite_to_group), value);
            } else {
                //ho.tvContent.setText("");
                content = "";
            }
        }else if (message instanceof InformationNotificationMessage) {
            content = ((InformationNotificationMessage) message).getMessage();
        }  else {
            //ho.tvContent.setText("");
            content = "";
        }

        ho.tvContent.setText(content);
        EmojiParseThread.getInstance().parseIm(content, ho.tvContent);


        MessageSettingDB db = new MessageSettingDB(mContext);
        boolean isSilence;
        IMAccount userInfo = null;
        switch(conversation.getConversationType()) {
            case PRIVATE:// 私聊
                ho.imgGroup.setVisibility(View.GONE);
                ho.userImg.setVisibility(View.VISIBLE);
                userInfo = userDB.get(RongDB.typeUser, targetId);
                if (userInfo != null) {
                    //LogUtil.logE("contactAdapter " + userInfo.getName() + ":" + userInfo.getPortraitUri().toString());
                    PicassoUtil.load(mContext, userInfo.getImgurl(), ho.userImg, R.drawable.icon_im_user);
                    // LogUtil.logE("1 alias", userInfo.getAlias());
                    if (userInfo.getAlias() != null && !userInfo.getAlias().equals("")) {
                        ho.tvName.setText(userInfo.getAlias());
                    } else {
                        ho.tvName.setText(userInfo.getNickname());
                    }

                    if (userInfo.getCertification() == UserItem.certificationYes) {
                        ho.imgAuthorized.setVisibility(View.VISIBLE);
                    } else {
                        ho.imgAuthorized.setVisibility(View.GONE);
                    }
                } else {
                    ho.tvName.setText(conversation.getTargetId());
                    PicassoUtil.load(mContext, R.drawable.icon_im_user, ho.userImg);
                    // getUserInfo(targetId, ho.tvName, ho.userImg);
                    if (listRequest.get(position) == false) {
                        listRequest.set(position, true);
                        getUserInfo(targetId, ho.tvName, ho.userImg);
                    }
                }
                final String titleName = ho.tvName.getText().toString();
                ho.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatPrivate(conversation.getTargetId());//userInfo.getName()
                    }
                });


                isSilence = db.isNotNotify(MessageSettingDB.typePrivate, targetId);
                if (isSilence) {
                    ho.imgSilence.setVisibility(View.VISIBLE);
                    ho.tvCount.setVisibility(View.GONE);
                } else {
                    ho.imgSilence.setVisibility(View.GONE);
                }

                break;
            case GROUP:
                String senderUserId = conversation.getSenderUserId();
                ho.imgAuthorized.setVisibility(View.GONE);
                if (SharedGroups.isGroupMineOrMaster(conversation.getTargetId(), mContext)) {
                    ho.imgGroup.setVisibility(View.VISIBLE);
                    ho.userImg.setVisibility(View.GONE);

                    if (senderUserId != null) {
                        IMAccount account = new RongDB(mContext).get(RongDB.typeUser, senderUserId);
                        if (account != null) {
                            CharSequence msgContent = ho.tvContent.getText();
                            //发送人 优先显示备注
                            String nameValue = account.getAlias();
                            if (nameValue == null || nameValue.equals("")) {
                                nameValue = account.getNickname();
                            }
                            //ho.tvContent.setText(nameValue + ":" + msgContent);
                            SpannableStringBuilder builder = new SpannableStringBuilder(nameValue);
                            builder.append(":").append(msgContent);
                            EmojiParseThread.getInstance().parseIm(builder, ho.tvContent);
                        }

                    }

                    String imgUrl = (String) SharedPrefUtils.getFromPublicFile(KEY.defaultimgGroup, "");
//                    if (!TextUtils.isEmpty(imgUrl)) {
//                        PicassoUtil.load(mContext, imgUrl, ho.userImg);
//                    } else {
//                        PicassoUtil.load(mContext, R.drawable.icon_im_discussion, ho.userImg);
//                    }
                    userInfo = userDB.get(RongDB.typeGroup, targetId);
                    if (userInfo != null) {
                        ho.tvName.setText(userInfo.getNickname());
                        PicassoUtil.loadGroupLogo(mContext, userInfo.getImgurl(), imgUrl, ho.imgGroup);
                    } else {
                        ho.tvName.setText(conversation.getTargetId());
                        //getDiscussionInfo(targetId, ho.tvName);
                        if (listRequest.get(position) == false) {
                            listRequest.set(position, true);
                            // getDiscussionInfo(targetId, ho.tvName);
                            getGroupInfo(targetId, ho.tvName, ho.imgGroup);
                        }
                    }
                    ho.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            chatDiscussion(conversation.getTargetId(), ConversationActivity.typeLocalGroup);
                        }
                    });

                    isSilence = db.isNotNotify(MessageSettingDB.typeGroup, targetId);
                    if (isSilence) {
                        ho.imgSilence.setVisibility(View.VISIBLE);
                        ho.tvCount.setVisibility(View.GONE);
                    } else {
                        ho.imgSilence.setVisibility(View.GONE);
                    }
                    break;

                } else {
                    if (senderUserId != null) {
                        IMAccount account = new RongDB(mContext).get(RongDB.typeUser, senderUserId);
                        if (account != null && message instanceof MyNotiMessage == false) {
                            CharSequence msgContent = ho.tvContent.getText();
                            String nameValue = account.getAlias();
                            if (nameValue == null || nameValue.equals("")) {
                                nameValue = account.getNickname();
                            }

                            //ho.tvContent.setText(nameValue + ":" + msgContent);
                            SpannableStringBuilder builder = new SpannableStringBuilder(nameValue);
                            builder.append(":").append(msgContent);
                            EmojiParseThread.getInstance().parseIm(builder, ho.tvContent);
                        }

                    }
                    ho.imgGroup.setVisibility(View.GONE);
                    ho.userImg.setVisibility(View.VISIBLE);
                    String disccusionImgUrl = (String) SharedPrefUtils.getFromPublicFile(KEY.defaultimgDiscussion, "");
                    if (!TextUtils.isEmpty(disccusionImgUrl)) {
                        PicassoUtil.load(mContext, disccusionImgUrl, ho.userImg);
                    } else {
                        PicassoUtil.load(mContext, R.drawable.icon_im_discussion, ho.userImg);
                    }
                    //PicassoUtil.load
                    userInfo = userDB.get(RongDB.typeDiscussion, targetId);
                    if (userInfo != null) {
                        //LogUtil.logE("讨论组name:" + userInfo.getName() + "   targetId:" + targetId);
                        ho.tvName.setText(userInfo.getNickname());
                    } else {
                        ho.tvName.setText(conversation.getTargetId());
                        //getDiscussionInfo(targetId, ho.tvName);
                        if (listRequest.get(position) == false) {
                            listRequest.set(position, true);
                            getDiscussionInfo(targetId, ho.tvName);
                        }
                    }
                    ho.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            chatDiscussion(conversation.getTargetId(), ConversationActivity.typeLocalDiscussion);//userInfo.getName()
                        }
                    });


                    isSilence = db.isNotNotify(MessageSettingDB.typeDiscussion, targetId);
                    if (isSilence) {
                        ho.imgSilence.setVisibility(View.VISIBLE);
                        ho.tvCount.setVisibility(View.GONE);
                    } else {
                        ho.imgSilence.setVisibility(View.GONE);
                    }
                    break;
                }


            default:
                ho.imgAuthorized.setVisibility(View.GONE);
                break;
        }

        //LogUtil.logE("adapter content", ho.tvContent.getText().toString());

        int viewType = getItemViewType(position);
        switch(viewType) {
            case viewtypeCommon:
                ho.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, ho.swipeLayout.findViewWithTag("Bottom2"));
                ho.swipeLayout.setRightSwipeEnabled(true);
                break;
            case viewtypeSecretary:
                ho.swipeLayout.setRightSwipeEnabled(false);
                break;
        }
        //小秘书就没有这一栏
        ho.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemDelete(conversation.getConversationType(), conversation.getTargetId(), position);
            }
        });
    }


    void getUserInfo(final String userId, final TextView tvName, final ImageView img) {
        final Params params = new Params();
        params.put("touserids", userId);
        new HttpEntity(mContext).commonPostData(Method.imGetSimpleUserInfo, params, new FinalHttpCallback() {

            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() == BaseItem.successTag) {
                    String obj = JSONHelper.getStringFromObject(item.getResult());
                    IMAccount userItem = null;
                    List<IMAccount> users = JSONHelper.getList(obj, IMAccount.class);
                    if (users != null && users.size() > 0) {
                        userItem = users.get(0);
                    } else {
                        return;
                    }
                    tvName.setText(userItem.getNickname());
                    if (userItem.getImgurl() != null) {
                        PicassoUtil.load(mContext, userItem.getImgurl().toString(), img);
                    } else {
                        PicassoUtil.load(mContext, R.drawable.def_head, img);
                    }


                    // Uri uri = Uri.parse(userItem.getImgurl());
                    //UserInfo user = new UserInfo(userId, userItem.getName(), uri);
                    // 加入本地数据库
                    userDB.insert(new IMAccount(RongDB.typeUser, userId, userItem.getNickname(), userItem.getImgurl(), userItem.getCertification(), userItem.getAlias()));
                    notifyDataSetChanged();
                }
            }
        });
    }

    void getDiscussionInfo(final String targetId, final TextView tvName) {
        final Params params = new Params();
        params.put("groupid", targetId);
        new HttpEntity(mContext).commonPostData(Method.imShowBaseDiscussion, params, new FinalHttpCallback() {

            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() == BaseItem.successTag) {
                    String obj = JSONHelper.getStringFromObject(item.getResult());
                    DiscussionTeam discussion = JSONHelper.getObject(obj, DiscussionTeam.class);
                    if (discussion == null) return;
                    tvName.setText(discussion.getGroupname());
                    IMAccount account = new IMAccount(RongDB.typeDiscussion, targetId, discussion.getGroupname(), "", UserItem.certificationNo, "");
                    account.setPeopleNum(discussion.getMemberscount());
                    userDB.insert(account);
                    notifyDataSetChanged();
                }

            }
        });
    }

    void getGroupInfo(final String targetId, final TextView tvName, final ImageView img) {
        final Params params = new Params();
        params.put("groupid", targetId);
        new HttpEntity(mContext).commonPostData(Method.showGroupInfo, params, new FinalHttpCallback() {

            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() == BaseItem.successTag) {
                    String obj = JSONHelper.getStringFromObject(item.getResult());
                    GroupTeam group = JSONHelper.getObject(obj, GroupTeam.class);
                    if (group == null) return;
                    tvName.setText(group.getGroupname());
                    PicassoUtil.load(mContext, group.getIconurl(), img);
                    IMAccount account = new IMAccount(RongDB.typeGroup, targetId, group.getGroupname(), group.getIconurl(), UserItem.certificationNo, "");
                    account.setPeopleNum(group.getMemberscount());
                    userDB.insert(account);
                    notifyDataSetChanged();
                }

            }
        });
    }

    void chatPrivate(String targetUserId) {
        ThemeActivity.isMerchant(targetUserId);
        Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).build();
        mContext.startActivity(new Intent("android.intent.action.VIEW", uri));
    }


    void chatDiscussion(String targetUserId, String typeLocal) {
        Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(ConversationType.GROUP.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("typeLocal", typeLocal).build();
        mContext.startActivity(new Intent("android.intent.action.VIEW", uri));
    }

    public OnItemListener listener;

    public void setOnItemListener(OnItemListener listener) {
        this.listener = listener;
    }

    public interface OnItemListener {
        void onItemDelete(Conversation.ConversationType type, String targetId, int position);
    }
}
