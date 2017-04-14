package yahier.exst.act.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.SearchUserAct;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.barcoe.CaptureActivity;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;

import io.rong.eventbus.EventBus;


public class ChooseMasterAct extends ThemeActivity implements View.OnClickListener, FinalHttpCallback {
    ImageView imgMaster;
    TextView tvTip;
    TextView btnOk;
    final int requestSearchCode = 101;
    final int requestScanCode = 102;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_choose_master2);
        setLabel(R.string.ensure_master);
        act = ChooseMasterAct.this;
        EventBus.getDefault().register(this);
        imgMaster = (ImageView) findViewById(R.id.imgMaster);
        tvTip = (TextView) findViewById(R.id.tvTip);
        btnOk = (TextView) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        findViewById(R.id.tvRecommend).setOnClickListener(this);
        findViewById(R.id.tvScan).setOnClickListener(this);
        findViewById(R.id.tvSearch).setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(EventTypeCommon type) {

        if (type.getType() == EventTypeCommon.typeGetRecommendMaster) {
            resultUser = type.getUser();
            if (resultUser == null) {
                btnOk.setEnabled(false);
                return;
            }
            showUser();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tvRecommend:
                intent = new Intent(this, ChooseIndustry.class);//MasterRecommendAct
                intent.putExtra("requestTag", ChooseIndustry.requestTagRegister);
                startActivity(intent);
                break;
            case R.id.tvScan:
                intent = new Intent(this, CaptureActivity.class);
                intent.putExtra(CaptureActivity.TagIsScanNoAction, true);
                startActivityForResult(intent, requestScanCode);
                break;
            case R.id.tvSearch:
                intent = new Intent(this, SearchUserAct.class);
                intent.putExtra("typeEntry", SearchUserAct.typeEntrySelect);
                intent.putExtra("isNoToken", true);
                startActivityForResult(intent, requestSearchCode);
                break;
            case R.id.btnOk:
                baishi();
                break;
        }

    }

    void baishi() {
        Params params = new Params();
        params.put("toinvitecode", resultUser.getInvitecode());
        if (resultUser != null) {
            params.put("touserid", resultUser.getUserid());
        }

        WaitingDialog.show(this, getString(R.string.please_wait), false);
        new HttpEntity(this).commonPostData(Method.baishi, params, this);

    }

    UserItem resultUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.logE("hello", "onActivityResult  " + requestCode + "  " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestScanCode:
                if (resultCode != Activity.RESULT_OK) return;
                String result = data.getStringExtra("result");
                LogUtil.logE("ChooseMasterAct result:" + result);
                Uri uri = Uri.parse(result);
                getUserInfo(uri.getQueryParameter("v"));
                break;
            case requestSearchCode:
                if (resultCode != Activity.RESULT_OK) return;
                resultUser = (UserItem) data.getSerializableExtra("user");
                if (resultUser == null) {
                    btnOk.setEnabled(false);
                    return;
                }
                showUser();
                break;
        }

    }

    void showUser() {
        PicassoUtil.load(this, resultUser.getImgurl(), imgMaster);
        tvTip.setText(resultUser.getNickname());
        tvTip.setTextColor(Color.parseColor("#454545"));
        btnOk.setEnabled(true);
    }

    void getUserInfo(String userId) {
        Params params = new Params();
        params.put("userId", userId);
        new HttpEntity(this).commonPostData(Method.getUserInfoInRegister, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }

            switch (methodName) {
                case Method.baishi:
                    WaitingDialog.dismiss();
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.baishi:
                ToastUtil.showToast(R.string.baishi_success);
                WaitingDialog.dismiss();
                loginSuccess(obj);
                break;
            case Method.getUserInfoInRegister:
                IMAccount userItem = JSONHelper.getObject(obj, IMAccount.class);
                if (userItem != null) {
                    resultUser = new UserItem();
                    resultUser.setUserid(Long.valueOf(userItem.getUserid()));
                    resultUser.setImgurl(userItem.getImgurl());
                    resultUser.setNickname(userItem.getNickname());
                    showUser();
                }
                break;


        }
    }

    private void loginSuccess(String obj) {
        AuthToken token = JSONHelper.getObject(obj, AuthToken.class);
        SharedToken.putTokenValue(token);
        //SharedToken.putValue(act, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRongyuntoken(),
        //        token.getRoleflag(), "" + token.getUserinfo().getMasterid(), token.getExpiriestime());
        STBLWession.getInstance().writeLiveRoomToken(token.getLiveRoomToken());
        //MyApplication.getContext().setUserItem(token.getUserinfo());
        int roleFlag;
        try {
            roleFlag = Integer.valueOf(token.getUserinfo().getRoleflag());
            if (UserRole.isNotMaster(roleFlag)) {
                Intent intent = new Intent(act, ChooseMasterAct.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(act, TabHome.class));
            }
        } catch (Exception e) {

        }
        RegisterActList.clearAll();
        finish();
    }
}
