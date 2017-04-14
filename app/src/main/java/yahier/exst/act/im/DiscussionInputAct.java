package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.DiscussionTeam;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * 编辑讨论组信息
 *
 * @author lenovo
 */
public class DiscussionInputAct extends ThemeActivity implements
        OnClickListener, FinalHttpCallback {
    private EditText input;
    DiscussionTeam group;
    final int requestCodeUpdate = 101;

    public final static int type_name = 1;// 名称
    public final static int type_desc = 2;// 介绍
    public final static int type_message_board = 3;// 群公告
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_input_simple);
        input = (EditText) findViewById(R.id.input);

        Intent intent = getIntent();
        group = (DiscussionTeam) intent.getSerializableExtra("discussionInfo");
        type = getIntent().getIntExtra("type", type_name);
        if (group == null) {
            ToastUtil.showToast(R.string.data_error);
            finish();
        }

        switch (type) {
            case type_name:
                setLabel(R.string.modify_discussion_name);
                input.setText(group.getGroupname());
                input.setSelection(group.getGroupname().length());
                break;
            case type_message_board:
                setLabel("讨论组公告");
                break;

        }

        findViewById(R.id.delete).setOnClickListener(this);
        setRightText(R.string.completed, new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = input.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastUtil.showToast(R.string.input_nothing);
                    return;
                }
                update();
            }
        });

        input.addTextChangedListener(new TextListener() {
            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.toString().length() > CreateDiscussionTeamAct.maxNameLength) {
                    String value = edit.toString();
                    input.setText(value.substring(0, CreateDiscussionTeamAct.maxNameLength));
                    input.setSelection(input.length());
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                input.setText("");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeUpdate) {

        }


    }

    private void closeInputSoft() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void update() {
        String value = input.getText().toString().trim();
        switch (type) {
            case type_name:
                Params params = new Params();
                params.put("groupid", group.getGroupid());
                params.put("groupname", value);
                new HttpEntity(this).commonPostData(Method.imUpdateInviteInfo, params, this);
                break;
            case type_message_board:
                TextMessage textMessage = TextMessage.obtain(RongContext.getInstance().getString(R.string.group_notice_prefix) + value);
                MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.ALL, null, null);
                textMessage.setMentionedInfo(mentionedInfo);

                RongIM.getInstance().sendMessage(Message.obtain(String.valueOf(group.getGroupid()), Conversation.ConversationType.GROUP, textMessage), null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });

                InputMethodUtils.hideInputMethod(input);
                finish();
                break;
        }


    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        String con = JSONHelper.getStringFromObject(item.getResult());
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        switch (methodName) {
            case Method.imUpdateInviteInfo:
                closeInputSoft();
                ToastUtil.showToast(R.string.modify_name_success);
                group.setGroupname(input.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("discussionInfo", group);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }

    }
}
