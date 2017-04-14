package yahier.exst.act.im;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.im.rong.MessageSettingDB;
import com.stbl.stbl.act.im.rong.TopSettingDB;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.IMSetting;
import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupTeam;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;

import io.rong.eventbus.EventBus;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class GroupInfoAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
    TextView tvName, tvRank, tvDesc, tvMemberCount;
    TextView tvCountDou;

    RelativeLayout linDesc;
    RelativeLayout mNameLayout;
    RelativeLayout mMasterLayout;

    TextView mEditNameTv;

    ImageView imgGroup;
    TextView tvDescMemberSee;
    //private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    // private final int CAPTURE_GALLERY_ACTIVITY_REQUEST_CODE = 200;
    GroupTeam group;
    String groupId;
    // final int maxWidth = 500;
    //final int maxHeight = 500;
    private Dialog mActionSheet;
    ImageView imgNotify;
    ImageView imgTop;
    View linInfo;
    View vMemberLine;//群成员上面的间隔线，群成员看的时候需隐藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_act);
        group = (GroupTeam) getIntent().getSerializableExtra("group");
        groupId = getIntent().getStringExtra("groupid");
        LogUtil.logE("groupId:" + groupId);
        if (group == null && groupId == null) {
            showToast(getString(R.string.im_no_transmit_data));
            return;
        }
        initView();
        if (group == null) {
            getInfo();
        } else {
            groupId = String.valueOf(group.getGroupid());
            setValue();
        }
        initSilenceSetting();
        EventBus.getDefault().register(this);
        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.choose_from_album));
        actionList.add(getString(R.string.take_photo));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(GroupInfoAct.this, position);
            }
        });
        showIsTop();
    }

    boolean isNotNotify;

    void initView() {
        linInfo = findViewById(R.id.linInfo);
        tvDescMemberSee = (TextView) findViewById(R.id.tvDescMemberSee);
        imgGroup = (ImageView) findViewById(R.id.imgGroup);
        tvCountDou = (TextView) findViewById(R.id.tvCountDou);
        tvName = (TextView) findViewById(R.id.tvName);
        tvRank = (TextView) findViewById(R.id.tvRank);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        tvMemberCount = (TextView) findViewById(R.id.tvMemberCount);

        findViewById(R.id.linDesc).setOnClickListener(this);
        findViewById(R.id.linMemberCount).setOnClickListener(this);
        linDesc = (RelativeLayout) findViewById(R.id.linDesc);
        linDesc.setOnClickListener(this);
        findViewById(R.id.linMaster).setOnClickListener(this);

        mNameLayout = (RelativeLayout) findViewById(R.id.linName);
        mNameLayout.setOnClickListener(this);
        mMasterLayout = (RelativeLayout) findViewById(R.id.linMaster);

        mEditNameTv = (TextView) findViewById(R.id.tv_edit_name);
        imgNotify = (ImageView) findViewById(R.id.imgNotify);
        imgTop = (ImageView) findViewById(R.id.imgTop);

        vMemberLine = findViewById(R.id.v_member_line);
        findViewById(R.id.linNoticeBoard).setOnClickListener(this);

    }

    void setValue() {
        setLabel(R.string.group_info);
        tvName.setText(group.getGroupname());
        mEditNameTv.setText(group.getGroupname());
        tvDesc.setText(group.getGroupdesc());
        tvMemberCount.setText(String.valueOf(group.getMemberscount() + getString(R.string.person)));
        tvDescMemberSee.setText(group.getGroupdesc());
        tvRank.setText(getString(R.string.group_rank_in_all) + group.getRanking());
        int width = (int) getResources().getDimension(R.dimen.im_group_info_img);
        PicassoUtil.loadGroupLogo(this, group.getIconoriurl(), width, width, imgGroup);
        adjustView();
    }

    void initSilenceSetting() {
        final MessageSettingDB db = new MessageSettingDB(this);
        isNotNotify = db.isNotNotify(MessageSettingDB.typeGroup, groupId);

        LogUtil.logE("group", isNotNotify + "");
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
                    db.update(MessageSettingDB.typeGroup, groupId, MessageSettingDB.stateOn);
                } else {
                    imgNotify.setImageResource(R.drawable.icon_open);
                    db.update(MessageSettingDB.typeGroup, groupId, MessageSettingDB.stateOff);
                }
                isNotNotify = !isNotNotify;
                EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, group.getGroupname()));

            }
        });
    }


    void showIsTop() {
        Params params = new Params();
        params.put("businessid", groupId);
        params.put("businesstype", IMSetting.topTypeGroup);
        new HttpEntity(this).commonPostData(Method.getTargetTopStatus, params, this);
    }

    void setTop() {
        Params params = new Params();
        params.put("businessid", groupId);
        params.put("businesstype", IMSetting.topTypeGroup);
        params.put("updatetype", IMSetting.updateTop);
        if (isTop)
            params.put("opeatetype", IMSetting.topOperateDelete);
        else
            params.put("opeatetype", IMSetting.topOperateAdd);
        new HttpEntity(this).commonPostData(Method.setTargetTop, params, this);
    }


    boolean isTop = false;


    void adjustView() {
        // 我是群主
        if (String.valueOf(group.getGroupmasterid()).equals(SharedToken.getUserId())) {
            tvDescMemberSee.setVisibility(View.GONE);
            mNameLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.imgLin1Right).setVisibility(View.VISIBLE);
            mMasterLayout.setVisibility(View.GONE);
            linDesc.setVisibility(View.VISIBLE);
            linInfo.setOnClickListener(this);
            findViewById(R.id.linNoticeBoard).setVisibility(View.VISIBLE);
        } else {
            // 我是群员
            findViewById(R.id.imgLin1Right).setVisibility(View.GONE);
            mNameLayout.setVisibility(View.GONE);
            tvDescMemberSee.setVisibility(View.VISIBLE);
            mMasterLayout.setVisibility(View.VISIBLE);
            linDesc.setVisibility(View.GONE);
            vMemberLine.setVisibility(View.GONE);
            findViewById(R.id.linNoticeBoard).setVisibility(View.GONE);
        }
    }

    void getInfo() {
        Params params = new Params();
        params.put("groupid", groupId);
        new HttpEntity(this).commonPostData(Method.showGroupInfo, params, this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.linName:
                enterAct(GroupInfoInputAct.type_name, 24);
                break;
            case R.id.linDesc:
                enterAct(GroupInfoInputAct.type_desc, 48);
                break;
            case R.id.linMemberCount:
                if (group == null)
                    return;
                intent = new Intent(this, GroupMembersAct.class);
                intent.putExtra("group", group);
                startActivity(intent);
                break;

            case R.id.linMaster:
                intent = new Intent(this, TribeMainAct.class);
                intent.putExtra("userId", group.getGroupmasterid());
                startActivity(intent);
                break;
            case R.id.imgTop:
                setTop();
                break;
            case R.id.linInfo:
                mActionSheet.show();
                break;
            case R.id.linNoticeBoard:
                enterAct(GroupInfoInputAct.type_message_board, 100);
                break;
        }
    }


    void enterAct(int type, int maxLength) {
        Intent intent = new Intent(this, GroupInfoInputAct.class);
        intent.putExtra("type", type);
        intent.putExtra("group", group);
        intent.putExtra("max_length", maxLength);
        startActivity(intent);
    }

    public void uploadImg(File file) {
        Params params = new Params();
        try {
            params.put("pic", file);
        } catch(Exception e) {
            TipsDialog.popup(this, getString(R.string.picture_file_not_exist), getString(R.string.queding));
            return;
        }
        new HttpEntity(this).commonPostImg(Method.imUploadGroupIcon, params, this);
    }

    public void updateImgUrl(String imgUrlName) {
        LogUtil.logE("imgUrl:" + imgUrlName);
        Params params = new Params();
        params.put("groupid", group.getGroupid());
        params.put("edittype", 3);// 1-标题，2-介绍，3-帮群图标变更
        params.put("editmsg", imgUrlName);
        new HttpEntity(this).commonPostData(Method.imEditGroup, params, this);
    }

    ImgUrl imgUrl;

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.showGroupInfo:
                group = JSONHelper.getObject(obj, GroupTeam.class);
                RongDB userDB = new RongDB(this);
                IMAccount acount = new IMAccount(RongDB.typeGroup, String.valueOf(group.getGroupid()),group.getGroupname(), group.getIconoriurl(), UserItem.certificationNo, "");
                acount.setPeopleNum(group.getMemberscount());
                userDB.update(acount);
                //刷新
                EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, group.getGroupname()));
                setValue();
                break;
            case Method.imUploadGroupIcon:
                imgUrl = JSONHelper.getObject(obj, ImgUrl.class);
                if (imgUrl == null)
                    break;
                int width = (int) getResources().getDimension(R.dimen.im_group_info_img);
                PicassoUtil.loadGroupLogo(this, imgUrl.getOri(), width, width, imgGroup);
                updateImgUrl(imgUrl.getFilename());
                break;
            case Method.imEditGroup:
                ToastUtil.showToast(R.string.upload_success);
                EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateGroupInfo, group.getGroupname()));
                //以下新加
                RongDB userDB2 = new RongDB(this);
                userDB2.update(new IMAccount(RongDB.typeGroup, String.valueOf(group.getGroupid()), null, imgUrl.getLarge(), UserItem.certificationNo, ""));
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
                imgTop.setOnClickListener(GroupInfoAct.this);
                break;
            case Method.setTargetTop:
                isTop = !isTop;
                TopSettingDB settingDB = new TopSettingDB(this);
                if (isTop) {
                    imgTop.setImageResource(R.drawable.icon_open);
                    settingDB.insert(IMSettingStatus.businesstypeGroup, groupId, TopSettingDB.stateTopYes);
                } else {
                    imgTop.setImageResource(R.drawable.icon_shut);
                    settingDB.delete(IMSettingStatus.businesstypeGroup, groupId);
                }
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File file = CropPhotoUtils.onActivityResult(this, requestCode, resultCode, data);
        if (file != null) {
            uploadImg(file);
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(GroupInfo info) {
        if (info.name != null) {
            tvName.setText(info.name);
            mEditNameTv.setText(info.name);
            group.setGroupname(info.name);
        }
        if (info.intro != null) {
            tvDesc.setText(info.intro);
            group.setGroupdesc(info.intro);
        }
    }

    public static class GroupInfo {
        public String name;
        public String intro;

        public GroupInfo(String name, String intro) {
            this.name = name;
            this.intro = intro;
        }
    }
}
