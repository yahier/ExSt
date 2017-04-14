package yahier.exst.ui.DirectScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.api.data.LiveRoomCreateInfo;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.ui.BaseClass.STBLBaseActivity;
import com.stbl.stbl.ui.DirectScreen.homeNotify.IMError;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomGroupManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomMsgManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomGroupCallBack;
import com.stbl.stbl.ui.DirectScreen.service.QavsdkServiceApi;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StringUtils;
import com.stbl.stbl.utils.UISkipUtils;
import com.stbl.stbl.utils.UIUtils;
import com.stbl.stbl.widget.avsdk.QavsdkContacts;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.avsdk.control.QavsdkControl;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVRoom;

import java.util.ArrayList;

/**
 * Created by meteorshower on 16/3/2.
 *
 * 开启我的直播(新)
 */
public class OpenNewDirectScreenActivity extends STBLBaseActivity implements QavsdkManger.OnQavsdkUpdateListener{

    private String flag = OpenNewDirectScreenActivity.this.getClass().getSimpleName();
    private Context ctx;
    private EditText edtTopic,edtContent,etEncryptPassword;
    private RelativeLayout llInputPassword; //输入密码项
    private RadioGroup rdgRoot; //
    private RadioButton rbEncryptPwd; //加密选项
    private LiveRoomCreateInfo info = null;
    private String topicContent, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_new_direct_screen);
        ctx = this;

        QavsdkManger.getInstance().registerCreateRoom(this, flag);

        navigationView.setTitleBar(R.string.title_open_new_direct_screen);
        navigationView.setClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtTopic = (EditText) findViewById(R.id.input_topic_edit);
        edtContent = (EditText) findViewById(R.id.input_content_edit);
        llInputPassword = (RelativeLayout) findViewById(R.id.ll_input_password);
        rdgRoot = (RadioGroup) findViewById(R.id.rdg_root);
        rbEncryptPwd = (RadioButton) findViewById(R.id.rd_encrypt);
        etEncryptPassword = (EditText) findViewById(R.id.et_encrypt_password);

        edtTopic.requestFocus();
        edtTopic.requestFocusFromTouch();

        rdgRoot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_encrypt){
                    llInputPassword.setVisibility(View.VISIBLE);
                }else{
                    llInputPassword.setVisibility(View.GONE);
                }
            }
        });

        RoomGroupManager.getInstance().initGroupData();
    }

    /**
     * Xml Click Enter Start Button
     * */
    public void onEnterStart(View v){

        switch(v.getId()) {
            case R.id.btn_enter_start:
                final String inputTopic = UIUtils.getViewContent(edtTopic);
                if (StringUtils.isEmpty(inputTopic)) {
                    ToastUtil.showToast(ctx, UIUtils.getResString(R.string.msg_input_screen_topic));
                    return;
                }

                topicContent = UIUtils.getViewContent(edtContent);
                if (StringUtils.isEmpty(topicContent)) {
                    topicContent = UIUtils.getResString(R.string.msg_screen_dynamic_content);
                }
                password = UIUtils.getViewContent(etEncryptPassword);
                if (rbEncryptPwd.isChecked() && (StringUtils.isEmpty(password) || password.length() < 6)){
                    ToastUtil.showToast(ctx,UIUtils.getResString(R.string.msg_encrypt_password_tips));
                    return;
                }
                if (!rbEncryptPwd.isChecked()) password = null;

                showDialog("正在创建房间,请稍后...");

                if(StringUtils.isEmpty(STBLWession.getInstance().getGroupId())) {
                    RoomGroupManager.getInstance().createGroup(SharedToken.getUserId(this), new ArrayList<String>(), new OnRoomGroupCallBack<String, Integer>() {
                        @Override
                        public void onRoomGroupError(int errorValue) {
                            switch(errorValue) {
                                case 6013:
                                case 6014:
                                case 6018:
                                    RoomMsgManager.getInstance().loginIM();
                                    break;
                                default:
                                    dissmissDialog();
                                    ToastUtil.showToast(IMError.getErrorValue(errorValue));
                                    break;
                            }
                        }

                        @Override
                        public void onRoomGroupSuccess(String groupId, String s, Integer integer) {
                            STBLWession.getInstance().setGroupId(groupId);
                            getCreateRoomId(inputTopic, topicContent, password);
                        }
                    });
                }else{
                    getCreateRoomId(inputTopic, topicContent, password);
                }
                break;
        }
    }

    private void getCreateRoomId(String inputTopic, String topicContent, String password){
        logger.e(" ----------------------- GroupId : "+STBLWession.getInstance().getGroupId()+" ----------------------- ");
        String groupId = STBLWession.getInstance().getGroupId();
        RoomGroupManager.getInstance().setRoomGroupId(groupId);
        RoomGroupManager.getInstance().modifyGroupAddOpt(groupId, null);
        if(info == null || info.getRoomid() == 0){
            JSONObject json = new JSONObject();
            json.put("topic", inputTopic);
            json.put("desc", topicContent);
            json.put("RoomGroupId", groupId);
            LogUtil.logE("LogUtil","password-:"+password);
            if (password != null) json.put("pwd",password);

            new HttpEntity(this).commonPostJson(Method.imLiveRoomCreate, json.toJSONString(), this);
        }else{
            QavsdkManger.getInstance().createRoomEnter(info.getRoomid(), OpenNewDirectScreenActivity.this);
        }
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItem) {
        closeProgress();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        if(methodName.equals(Method.imLiveRoomCreate)){
            closeProgress();
            info = JSONHelper.getObject(valueObj, LiveRoomCreateInfo.class);
            logger.i(info.toString());
            QavsdkManger.getInstance().createRoomEnter(info.getRoomid(), OpenNewDirectScreenActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QavsdkManger.getInstance().unregisterCreateRoom(this, flag);
        dissmissDialog();
    }

    @Override
    public void closeProgress() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void startSdkSuccess() {
        finish();
    }
}
