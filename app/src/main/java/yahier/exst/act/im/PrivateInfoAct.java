package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.im.rong.MessageSettingDB;
import com.stbl.stbl.act.im.rong.TopSettingDB;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.IMSetting;
import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedOfficeAccount;
import com.stbl.stbl.util.ToastUtil;

import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class PrivateInfoAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
    TextView tvName;
    TextView tvAge, tvLocation, tvAlias;

    ImageView imgUser, imgGender;
    UserItem userItem;
    String targetId;
    ImageView imgNotify;
    ImageView imgTop;
    Context mContext;
    boolean isTop = false;
    boolean isNotNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_private_info_act);
        setLabel(R.string.im_chat);
        mContext = PrivateInfoAct.this;
        targetId = getIntent().getStringExtra("targetId");


        //小秘书和支付助手有特别之处

        if (SharedOfficeAccount.isOfficeAccount(targetId)) {
            findViewById(R.id.linUserInfo).setVisibility(View.GONE);
            findViewById(R.id.lin1).setVisibility(View.GONE);
            findViewById(R.id.line2).setVisibility(View.GONE);
        }

        if (targetId == null) {
            showToast(getString(R.string.im_no_transmit_data));
            return;
        }
        EventBus.getDefault().register(this);
        initView();
        initSilenceSetting();
        showIsTop();
        getInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void initView() {
        imgUser = (ImageView) findViewById(R.id.imgUser);
        imgGender = (ImageView) findViewById(R.id.imgGender);
        tvName = (TextView) findViewById(R.id.tvName);
        tvAge = (TextView) findViewById(R.id.tvAge);
        tvAlias = (TextView) findViewById(R.id.tvAlias);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        imgNotify = (ImageView) findViewById(R.id.imgNotify);
        imgTop = (ImageView) findViewById(R.id.imgTop);
        findViewById(R.id.lin1).setOnClickListener(this);
        findViewById(R.id.linUserInfo).setOnClickListener(this);


    }


    void initSilenceSetting() {
        final MessageSettingDB db = new MessageSettingDB(this);
        isNotNotify = db.isNotNotify(MessageSettingDB.typePrivate, targetId);
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
                    db.update(MessageSettingDB.typePrivate, targetId, MessageSettingDB.stateOn);
                } else {
                    imgNotify.setImageResource(R.drawable.icon_open);
                    db.update(MessageSettingDB.typePrivate, targetId, MessageSettingDB.stateOff);
                }
                isNotNotify = !isNotNotify;
                //EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, group.getGroupname()));

            }
        });
    }


    void getInfo() {
        Params params = new Params();
        params.put("userids", targetId);
        new HttpEntity(this).commonPostData(Method.getUserSimpleInfo, params, this);
    }


    void showIsTop() {
        Params params = new Params();
        params.put("businessid", targetId);
        params.put("businesstype", IMSetting.topTypePrivate);
        new HttpEntity(this).commonPostData(Method.getTargetTopStatus, params, this);
    }

    void setTop() {
        Params params = new Params();
        params.put("businessid", targetId);
        params.put("businesstype", IMSetting.topTypePrivate);
        params.put("updatetype", IMSetting.updateTop);
        if (isTop)
            params.put("opeatetype", IMSetting.topOperateDelete);
        else
            params.put("opeatetype", IMSetting.topOperateAdd);
        new HttpEntity(this).commonPostData(Method.setTargetTop, params, this);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.lin1:
                intent = new Intent(this, InputAct.class);
                intent.putExtra("targetUserId", targetId);
                intent.putExtra("alias", tvAlias.getText().toString());
                startActivity(intent);
                break;
            case R.id.imgTop:
                setTop();
                break;
            case R.id.linUserInfo:
                intent = new Intent(this, TribeMainAct.class);
                intent.putExtra("userId", Long.valueOf(targetId));
                startActivity(intent);
                break;
        }
    }


    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.getUserSimpleInfo:
                List<UserItem> list = JSONHelper.getList(obj, UserItem.class);
                if (list.size() > 0) userItem = list.get(0);
                tvName.setText(userItem.getNickname());
//                tvName.setText(userItem.getNickname());
                if (userItem.getAge() != 0) {
                    tvAge.setText(userItem.getAge() + "岁");
                    tvAge.setVisibility(View.VISIBLE);
                }
                tvLocation.setText(userItem.getCityname());
                if (userItem.getAlias() != null) {
                    tvAlias.setText(userItem.getAlias());
                }
                int width = (int) getResources().getDimension(R.dimen.im_group_info_img);
                ImageUtils.loadCircleHead(userItem.getImgurl(), imgUser, width);
                imgGender.setVisibility(View.VISIBLE);
                if (userItem.getGender() == UserItem.gender_boy) {
                    imgGender.setImageResource(R.drawable.icon_male);
                } else if (userItem.getGender() == UserItem.gender_girl) {
                    imgGender.setImageResource(R.drawable.icon_female);
                } else {
                    imgGender.setVisibility(View.GONE);
                }

                imgTop.setOnClickListener(PrivateInfoAct.this);

                if(userItem.getCertification()==UserItem.certificationYes){
                    findViewById(R.id.imgAuthorized).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.imgAuthorized).setVisibility(View.GONE);
                }

                //不是好友，不显示设置备注
                //if (!Relation.isFriend(userItem.getRelationflag())){
                //    findViewById(R.id.lin1).setVisibility(View.GONE);
                //    findViewById(R.id.line2).setVisibility(View.GONE);
                //     findViewById(R.id.ll_setting_root).setBackgroundResource(R.color.theme_bg);
                // }
                //刷新
                //EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, group.getGroupname()));

                //刷新融云
                String nameValue = userItem.getAlias();
                if (nameValue == null || nameValue.equals("")) {
                    nameValue = userItem.getNickname();
                }
                Uri uri = null;
                if (userItem.getImgurl() != null) {
                    uri = Uri.parse(userItem.getImgurl());
                }
                UserInfo user = new UserInfo(String.valueOf(userItem.getUserid()), nameValue, uri);
                RongIM.getInstance().refreshUserInfoCache(user);
                break;
            case Method.getTargetTopStatus:
                IMSetting settings = JSONHelper.getObject(obj, IMSetting.class);
                if (settings.getIstop() == IMSetting.topYes) {
                    imgTop.setImageResource(R.drawable.icon_open);
                    isTop = true;
                } else {
                    imgTop.setImageResource(R.drawable.icon_shut);
                    isTop = false;
                }
                break;
            case Method.setTargetTop:
                isTop = !isTop;
                TopSettingDB settingDB = new TopSettingDB(this);
                if (isTop) {
                    imgTop.setImageResource(R.drawable.icon_open);
                    settingDB.insert(IMSettingStatus.businesstypePrivate, targetId, TopSettingDB.stateTopYes);
                } else {
                    imgTop.setImageResource(R.drawable.icon_shut);
                    settingDB.delete(IMSettingStatus.businesstypePrivate, targetId);
                }
                break;

        }

    }


    public void onEvent(EventUpdateAlias event) {
        tvAlias.setText(event.getAlias());
    }

}
