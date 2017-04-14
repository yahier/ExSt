package yahier.exst.act.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;


/**
 * w
 * 编辑群组信息
 *
 * @author lenovo
 */
public class GroupInfoInputAct extends ThemeActivity implements
        OnClickListener, FinalHttpCallback {

    public final static String key = "type";
    public final static int type_name = 1;// 名称
    public final static int type_desc = 2;// 介绍
    public final static int type_message_board = 3;// 群公告
    int type = type_name;
    private EditText input;
    private TextView tvTextCount;
    MyApplication app;
    GroupTeam group;

    private String mContent;
    private int mMaxLength = 50; //最大数字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_input_simple);
        app = (MyApplication) getApplication();
        input = (EditText) findViewById(R.id.input);
        tvTextCount = (TextView) findViewById(R.id.tv_text_count);

        Intent intent = getIntent();
        type = intent.getIntExtra(key, 0);
        mMaxLength = intent.getIntExtra("max_length", mMaxLength);
        if (type == 0) {
            ToastUtil.showToast(R.string.data_error);
            finish();
        }
        group = (GroupTeam) intent.getSerializableExtra("group");
        if (group == null) {
            ToastUtil.showToast(R.string.data_error);
            finish();
        }
        switch (type) {
            case type_name:
                setLabel(R.string.modify_group_name);
                input.setText(group.getGroupname());
                input.setSelection(group.getGroupname().length());
                break;
            case type_desc:
                setLabel(R.string.edit_group_desc);
                input.setText(group.getGroupdesc());
                input.setSelection(group.getGroupdesc().length());
                break;
            case type_message_board:
                setLabel("群公告");
                //input.setText(group.getGroupdesc());
                //input.setSelection(group.getGroupdesc().length());
                break;
        }
        input.requestFocus();

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
        tvTextCount.setText(input.getText().toString().length() + "/" + mMaxLength);
        input.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = input.getSelectionStart();
                editEnd = input.getSelectionEnd();
                if (temp.length() > mMaxLength) {
                    switch (type) {
                        case type_name:
                            ToastUtil.showToast(GroupInfoInputAct.this, String.format(getString(R.string.im_group_name_can_not_exceed), mMaxLength));
                            break;
                        case type_desc:
                            ToastUtil.showToast(GroupInfoInputAct.this, String.format(getString(R.string.im_group_introduce_can_not_exceed), mMaxLength));
                            break;
                    }
                    try {
                        s.delete(editStart - 1, editEnd);
                        int tempSelection = editStart;
                        input.setText(s);
                        input.setSelection(tempSelection);
                    } catch (StackOverflowError e) {
                        e.printStackTrace();
                        System.gc();
                    }
                }
                tvTextCount.setText(s.toString().length() + "/" + mMaxLength);
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

    // 更新群组信息
    public void update() {
        String value = input.getText().toString().trim();
        mContent = value;
        switch (type) {
            case type_desc:
            case type_name:
                Params params = new Params();
                params.put("groupid", group.getGroupid());
                params.put("edittype", type);
                params.put("editmsg", value);
                new HttpEntity(this).commonPostData(Method.imEditGroup, params, this);
                break;
            case type_message_board:
                TextMessage textMessage = TextMessage.obtain(RongContext.getInstance().getString(R.string.group_notice_prefix) + mContent);
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
        UserItem user = app.getUserItem();
        switch (methodName) {
            case Method.imEditGroup:
                switch (type) {
                    case type_name:
                        ToastUtil.showToast(R.string.modify_group_name_success);
                        RongDB userDB = new RongDB(this);
                        userDB.update(new IMAccount(RongDB.typeGroup, String.valueOf(group.getGroupid()), input.getText().toString(), "", UserItem.certificationNo, ""));
                        //以上两条新加
                        EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, mContent));
                        EventBus.getDefault().post(new GroupInfoAct.GroupInfo(mContent, null));
                        finish();
                        break;
                    case type_desc:
                        ToastUtil.showToast(R.string.modify_group_desc_success);
                        EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, group.getGroupname()));
                        EventBus.getDefault().post(new GroupInfoAct.GroupInfo(null, mContent));
                        finish();
                        break;
                }
                break;
        }

    }
}
