package yahier.exst.act.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.ConversationActivity;
import com.stbl.stbl.act.im.rong.MyNotiMessage;
import com.stbl.stbl.act.im.rong.NotificationType;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.DiscussionInviteShow;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;

@Deprecated
public class DiscussionInviteAct extends ThemeActivity implements FinalHttpCallback, OnClickListener {

    TextView tvInviter, tvDiscussionName, tvCreatime;
    TextView tvReceived, tvReject;

    long inviteid;
    int handleType;
    final int handleresultReceive = 1;
    final int handleresultRejecte = 2;

    DiscussionInviteShow discussionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion_invite_act);
        setLabel(R.string.invite_join_discussion_team);

        inviteid = getIntent().getLongExtra("inviteid", 0);
        LogUtil.logE("inviteid:" + inviteid);
        initViews();
        getDiscussionInfo();
    }

    void initViews() {
        tvInviter = (TextView) findViewById(R.id.tvInviter);
        tvDiscussionName = (TextView) findViewById(R.id.tvDiscussionName);
        tvCreatime = (TextView) findViewById(R.id.tvCreatime);
        tvReceived = (TextView) findViewById(R.id.tvReceived);
        tvReject = (TextView) findViewById(R.id.tvReject);
        tvReject.setOnClickListener(this);
        tvReceived.setOnClickListener(this);
    }

    void getDiscussionInfo() {
        Params params = new Params();
        params.put("inviteid", inviteid);
        new HttpEntity(this).commonPostData(Method.imShowDiscussionInvite, params, this);
    }

    /**
     * 处理请求
     *
     * @param
     */
    void handleInvite() {
        Params params = new Params();
        params.put("inviteid", inviteid);
        params.put("handleresult", handleType);
        new HttpEntity(this).commonPostData(Method.imDealDiscussionInvite, params, this);
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
            case Method.imShowDiscussionInvite:
                discussionInfo = JSONHelper.getObject(obj, DiscussionInviteShow.class);
                switch (discussionInfo.getStatus()) {
                    case DiscussionInviteShow.status_inviting:
                        tvReject.setVisibility(View.VISIBLE);
                        tvReceived.setVisibility(View.VISIBLE);
                        break;
                    case DiscussionInviteShow.status_received:
                        tvReceived.setVisibility(View.VISIBLE);
                        tvReceived.setText(R.string.accepted);
                        tvReceived.setOnClickListener(null);
                        break;
                    case DiscussionInviteShow.status_rejected:
                        tvReject.setVisibility(View.VISIBLE);
                        tvReject.setText(R.string.denied);
                        tvReject.setOnClickListener(null);
                        break;
                }
                tvInviter.setText(discussionInfo.getInviteusername());
                tvDiscussionName.setText(discussionInfo.getGroupname());
                tvCreatime.setText(DateUtil.getHmOrMdHm(String.valueOf(discussionInfo.getGroupcreatetime())));
                break;
            case Method.imDealDiscussionInvite:
                ToastUtil.showToast(R.string.handle_success);
                tvReject.setVisibility(View.GONE);
                tvReceived.setVisibility(View.GONE);
                if (handleType == handleresultReceive) {
                    tvReceived.setVisibility(View.VISIBLE);
                    tvReceived.setText(R.string.accepted);
                    tvReceived.setOnClickListener(null);
                    //RongIM.getInstance().startConversation(this, ConversationType.GROUP, String.valueOf(discussionInfo.getGroupid()), discussionInfo.getGroupname());
                    chatDiscussion(String.valueOf(discussionInfo.getGroupid()), discussionInfo.getGroupname());
                    // 在这里用数据库存储讨论组的名称。
                } else {
                    tvReject.setVisibility(View.VISIBLE);
                    tvReject.setText(R.string.denied);
                    tvReject.setOnClickListener(null);
                }
                break;
        }

    }

    void chatDiscussion(String targetUserId, String title) {
        Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(ConversationType.GROUP.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("typeLocal", ConversationActivity.typeLocalDiscussion).appendQueryParameter("title", title).build();
        startActivity(new Intent("android.intent.action.VIEW", uri));
        sendNoti(targetUserId);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvReject:
                handleType = handleresultRejecte;
                handleInvite();
                break;
            case R.id.tvReceived:
                handleType = handleresultReceive;
                handleInvite();
                break;
        }
    }

    //发送 创建讨论组通知
    public void sendNoti(String targetId) {
        MyNotiMessage mesage = MyNotiMessage.obtain(String.valueOf(NotificationType.typeAddDis));
        RongIM.getInstance().getRongIMClient().sendMessage(ConversationType.GROUP, targetId, mesage, null, null, new RongIMClient.SendMessageCallback() {
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
