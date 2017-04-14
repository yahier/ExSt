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
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;

import io.rong.eventbus.EventBus;

/**
 * 修改备注:需要传入targetUserId 和 alias两个字段值
 *
 * @author lenovo
 */
public class InputAct extends ThemeActivity implements
        OnClickListener, FinalHttpCallback {

    public final static String key = "type";
    public final static int type_alias = 1;// 修改备注
    int type = type_alias;
    private EditText input;
    private TextView tvTextCount;

    GroupTeam group;
    private String mContent;
    private int mMaxLength = 10; //最大数字
    String targetUserId;
    String alias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_input_simple);

        Intent intent = getIntent();
        targetUserId = intent.getStringExtra("targetUserId");
        alias = intent.getStringExtra("alias");
        type = intent.getIntExtra(key, type_alias);
        mMaxLength = intent.getIntExtra("max_length", mMaxLength);

        if (alias == null) alias = "";
        input = (EditText) findViewById(R.id.input);
        tvTextCount = (TextView) findViewById(R.id.tv_text_count);


        group = (GroupTeam) intent.getSerializableExtra("group");
        switch(type) {
            case type_alias:
                setLabel(R.string.im_update_alias);
                input.setText(alias);
                input.setSelection(alias.length());
                break;
        }
        input.requestFocus();

        findViewById(R.id.delete).setOnClickListener(this);

        setRightText(R.string.save, new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = input.getText().toString().trim();
                if (TextUtils.isEmpty(text) && type != type_alias) {
                    ToastUtil.showToast(R.string.input_nothing);
                    return;
                }
                update();
            }
        });
        tvTextCount.setText(input.getText().toString().length() + "/" + mMaxLength);
        input.addTextChangedListener(new TextListener() {
            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.toString().length() > mMaxLength) {
                    String value = edit.toString();
                    input.setText(value.substring(0, mMaxLength));
                    input.setSelection(input.length());
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.delete:
                input.setText("");
                break;
        }
    }


    public void update() {
        String value = input.getText().toString();//.trim();
        if(value.contains(" ")){
            ToastUtil.showToast("备注名不能有空格");
            return;
        }
        mContent = value;
        Params params = new Params();
        params.put("targetuserid", targetUserId);
        params.put("remark", value);
        new HttpEntity(this).commonPostData(Method.updateAlias, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        String con = JSONHelper.getStringFromObject(item.getResult());
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        //UserItem user = app.getUserItem();
        switch(methodName) {
            case Method.updateAlias:
                switch(type) {
                    case type_alias:
                        ToastUtil.showToast(R.string.im_set_alias_success);
                        RongDB db = new RongDB(this);
                        IMAccount account = db.get(RongDB.typeUser, targetUserId);
                        if (account != null) {
                            account.setAlias(mContent);
                            db.update(account);
                        }else{
                            LogUtil.logE("input account is null");
                        }
                        try {
                            EventBus.getDefault().post(new EventUpdateAlias(mContent, Long.parseLong(targetUserId)));
                        } catch(NumberFormatException e) {
                            e.printStackTrace();
                        }
                        finish();
                        break;
                }
                break;
        }

    }
}
