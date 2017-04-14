package yahier.exst.act.mine;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.login.ChooseIndustry;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.PeopleLevelDialog;
import com.stbl.stbl.dialog.RecordVoiceDialog;
import com.stbl.stbl.dialog.WealthLevelDialog;
import com.stbl.stbl.item.CityItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.Level;
import com.stbl.stbl.item.ProvinceItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserTag;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.VoicePlayer;
import com.stbl.stbl.util.WheelCity;
import com.stbl.stbl.util.WheelCity.OnCityWheelListener;
import com.stbl.stbl.util.WheelTime;
import com.stbl.stbl.util.WheelTime.OnTimeWheelListener;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

public class UserInfoMain extends ThemeActivity implements OnClickListener, OnTimeWheelListener, OnCityWheelListener {

    private static final int REQUEST_CODE_NICK = 101;
    private static final int REQUEST_CODE_SIGNATURE = 102;
    private static final int REQUEST_CODE_INDUSTRY = 103;

    private ImageView mHeadIv;
    private TextView mNickTv;
    private TextView mIndustryTv;
    private TextView mCityTv;
    private TextView mSignatureTv;
    private ImageView mPlayIv;
    private ImageView mRecordIv;
    private TextView mGenderTv;
    private TextView mAgeTv;
    private TextView mInviteCodeTv;
    private ProgressBar mWealthPb;
    private ProgressBar mConnectionPb;
    private TextView mWealthTv;
    private TextView mConnectionTv;

    private UserItem mUser;

    private Dialog mActionSheet;
    private RecordVoiceDialog mRecordDialog;

    private VoicePlayer mPlayer;
    private String mRecordPath;

    private List<ProvinceItem> listProvinceCity;
    private UserTag userTag; //行业
    private Level mLevel; //等级信息

    private boolean mFromH5Web;
    private boolean mIsSetIndustry;
    private boolean mIsUploadHead;
//    private ImageView mHeadAlertIv;
    private TextView mTvNoAvator;//没有头像提示
    private TextView mTvNoIndustry;//没有选择行业提示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (UserItem) getIntent().getSerializableExtra(KEY.USER_ITEM);
        if (mUser == null) {
            ToastUtil.showToast(R.string.me_network_unavailable);
            finish();
            return;
        }
        setContentView(R.layout.user_main);
        setLabel(getString(R.string.me_my_info));

        if (getIntent().getIntExtra("jumptype", 0) == 1) {
            mFromH5Web = true;
        }

        initView();
        setView();
        getProvinceCityList();
        getUserTag();
        getUserLevel();
    }

    private void initView() {
        mTvNoIndustry = (TextView) findViewById(R.id.tv_no_industry);
        mTvNoAvator = (TextView) findViewById(R.id.tv_no_avator);
        mHeadIv = (ImageView) findViewById(R.id.iv_head);
//        mHeadAlertIv = (ImageView) findViewById(R.id.iv_head_alert);
        mNickTv = (TextView) findViewById(R.id.tv_nick);
        mIndustryTv = (TextView) findViewById(R.id.tv_industry);
        mCityTv = (TextView) findViewById(R.id.tv_city);
        mSignatureTv = (TextView) findViewById(R.id.tv_sign);
        mGenderTv = (TextView) findViewById(R.id.tv_gender);
        mAgeTv = (TextView) findViewById(R.id.tv_age);
        mInviteCodeTv = (TextView) findViewById(R.id.tv_invite_code);

        mWealthPb = (ProgressBar) findViewById(R.id.pb_wealth_level);
        mConnectionPb = (ProgressBar) findViewById(R.id.pb_connection_level);
        mWealthTv = (TextView) findViewById(R.id.tv_wealth_level);
        mConnectionTv = (TextView) findViewById(R.id.tv_connection_level);

        findViewById(R.id.layout_head).setOnClickListener(this);
        findViewById(R.id.layout_nick).setOnClickListener(this);
        findViewById(R.id.layout_industry).setOnClickListener(this);
        findViewById(R.id.layout_city).setOnClickListener(this);
        findViewById(R.id.layout_sign).setOnClickListener(this);
        mPlayIv = (ImageView) findViewById(R.id.iv_play);
        mPlayIv.setOnClickListener(this);
        mRecordIv = (ImageView) findViewById(R.id.iv_record);
        mRecordIv.setOnClickListener(this);
        findViewById(R.id.iv_record).setOnClickListener(this);
        findViewById(R.id.layout_gender).setOnClickListener(this);
        findViewById(R.id.layout_age).setOnClickListener(this);
        findViewById(R.id.layout_wealth_level).setOnClickListener(this);
        findViewById(R.id.layout_connection_level).setOnClickListener(this);

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.me_choose_from_phone));
        actionList.add(getString(R.string.me_photograph));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(UserInfoMain.this, position);
            }
        });

        mPlayer = new VoicePlayer();
    }

    private void setView() {
        ImageUtils.loadCircleHead(mUser.getImgurl(), mHeadIv);
        if (!TextUtils.isEmpty(mUser.getImgurl()) && !(mUser.getImgurl().contains("default"))) {
            mIsUploadHead = true;
        } else {
//            mHeadAlertIv.setVisibility(View.VISIBLE);
            mTvNoAvator.setVisibility(View.VISIBLE);
        }
        mNickTv.setText(mUser.getNickname());
        mCityTv.setText(TextUtils.isEmpty(mUser.getCityname()) ? getString(R.string.me_unset) : mUser.getCityname());
        mSignatureTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
//                mSignatureTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mSignatureTv.getLineCount() > 1) {
                    mSignatureTv.setGravity(Gravity.LEFT);
                } else {
                    mSignatureTv.setGravity(Gravity.RIGHT);
                }
            }
        });
        mSignatureTv.setText(mUser.getSignature());


        if (mUser.getGender() == UserItem.gender_boy) {
            mGenderTv.setText(R.string.me_male);
        } else if (mUser.getGender() == UserItem.gender_girl) {
            mGenderTv.setText(R.string.me_female);
        } else {
            mGenderTv.setText(R.string.me_no_set);
        }
        mAgeTv.setText(mUser.getAge() <= 0 ? getString(R.string.me_unset) : (mUser.getAge() + ""));
        mInviteCodeTv.setText(mUser.getInvitecode());
        mInviteCodeTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StringUtil.copyToClipboard(mUser.getInvitecode());
                return false;
            }
        });

        mPlayer.downloadOnlineVoice(mUser.getPhonetic(), new VoicePlayer.OnDownloadSuccessListener() {
            @Override
            public void onDownloadSuccess(String path) {
                mRecordPath = path;
                setPlayerDataSource();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_head:
                mActionSheet.show();
                break;
            case R.id.layout_nick: {
                Intent intent = new Intent(mActivity, EditTextActivity.class);
                intent.putExtra(KEY.TEXT, mUser.getNickname());
                startActivityForResult(intent, REQUEST_CODE_NICK);
            }
            break;
            case R.id.layout_gender:
                showGenderWindow();
                break;
            case R.id.layout_age:
                WheelTime wheelTime = new WheelTime();
                wheelTime.setOnTimeWheelListener(this);
                wheelTime.chooseTime(this);
                break;
            case R.id.layout_city:
                if (listProvinceCity == null) {
                    return;
                }
                WheelCity wheelCity = new WheelCity();
                wheelCity.setOnCityWheelListener(this);
                wheelCity.setCurrentItem(getProvinceCurItem(), getCityCurItem());
                wheelCity.show(this, listProvinceCity);
                break;
            case R.id.layout_sign: {
                Intent intent = new Intent(mActivity, EditTextActivity.class);
                intent.putExtra(KEY.TEXT, mUser.getSignature());
                startActivityForResult(intent, REQUEST_CODE_SIGNATURE);
            }
            break;
            case R.id.layout_industry:
                Intent intent = new Intent(this, ChooseIndustry.class);
                intent.putExtra("requestTag", ChooseIndustry.requestTagSetting);
                intent.putExtra("userTag", userTag);
                startActivityForResult(intent, REQUEST_CODE_INDUSTRY);
                break;
            case R.id.iv_play:
                playRecord();
                break;
            case R.id.iv_record:
                showRecordDialog();
                break;
            case R.id.layout_wealth_level: //财富栏
                if (mLevel == null) return;
                WealthLevelDialog wealthDialog = new WealthLevelDialog(this);
                wealthDialog.setTitle(mLevel.getLevelrichtitle());
                wealthDialog.setContentText(this, String.format(getString(R.string.me_distance_to_next_level_need_consume_d), mLevel.getLevelrichnext()));
                wealthDialog.show();
                break;
            case R.id.layout_connection_level: //人脉栏
                if (mLevel == null) return;
                PeopleLevelDialog peopleDialog = new PeopleLevelDialog(this);
                peopleDialog.setTitle(mLevel.getLevelcontacttitle());
                peopleDialog.setContentText(this, String.format(getString(R.string.me_distance_to_next_level_need_score_d), mLevel.getLevelcontactnext()));
                peopleDialog.show();
                break;
        }
    }

    /**
     * 当前选中的省份
     */
    private int getProvinceCurItem() {
        if (listProvinceCity != null && listProvinceCity.size() > 0) {
            for (int i = 0; i < listProvinceCity.size(); i++) {
                List<CityItem> cityList = listProvinceCity.get(i).getCitys();
                for (int k = 0; k < cityList.size(); k++) {
                    CityItem cityItem = cityList.get(k);
                    if (cityItem != null && cityItem.getCitycode() != null &&
                            cityItem.getCitycode().equals(mUser.getCityid())) {
                        return i;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 当前选中的城市
     */
    private int getCityCurItem() {
        if (listProvinceCity != null && listProvinceCity.size() > 0) {
            for (int i = 0; i < listProvinceCity.size(); i++) {
                List<CityItem> cityList = listProvinceCity.get(i).getCitys();
                for (int k = 0; k < cityList.size(); k++) {
                    CityItem cityItem = cityList.get(k);
                    if (cityItem != null && cityItem.getCitycode() != null &&
                            cityItem.getCitycode().equals(mUser.getCityid())) {
                        return k;
                    }
                }
            }
        }
        return 0;
    }

    private void getProvinceCityList() {
        DataCacheDB cacheDB = new DataCacheDB(this);
        String obj = cacheDB.getCityTreeCacheJson();

        String updatetime = "0";
        if (obj != null) {
            listProvinceCity = JSONHelper.getList(obj, ProvinceItem.class);
            updatetime = SharedCommon.getCityTreeUpdateTime();//上次的更新时间
        }
        mTaskManager.start(CommonTask.getCityList(updatetime)
                .setCallback(new HttpTaskCallback<ArrayList<ProvinceItem>>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<ProvinceItem> result) {
                        listProvinceCity = result;
                        SharedCommon.putCityTreeUpdateTime();
                    }
                }));
    }

    private void getUserTag() {
        mTaskManager.start(UserTask.getUserTag()
                .setCallback(new HttpTaskCallback<UserTag>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(UserTag result) {
                        userTag = result;
                        if (result.getProfessionsid() == 0) {
//                            mIndustryTv.setTextColor(getResources().getColor(R.color.font_red));
                            mIndustryTv.setText(getString(R.string.me_unselect));
                            mTvNoIndustry.setVisibility(View.VISIBLE);
                        } else {
                            mTvNoIndustry.setVisibility(View.GONE);
                            mIndustryTv.setTextColor(getResources().getColor(R.color.f_gray));
                            mIndustryTv.setText(result.getProfessionsname());
                            mIsSetIndustry = true;
                        }
                    }
                }));
    }

    private void getUserLevel() {
        mTaskManager.start(UserTask.getUserLevel()
                .setCallback(new HttpTaskCallback<Level>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Level level) {
                        mLevel = level;
                        mWealthTv.setText(String.format("LV %s", level.getLevelrichname()));
                        mConnectionTv.setText(String.format("LV %s", level.getLevelcontactname()));

                        int progressWealth = level.getLevelrichcur() * 100 / (level.getLevelrichcur() + level.getLevelrichnext());
                        mWealthPb.setProgress(progressWealth);
                        int progressPeople = level.getLevelcontactcur() * 100 / (level.getLevelcontactnext() + level.getLevelcontactcur());
                        mConnectionPb.setProgress(progressPeople);
                    }
                }));
    }

    private void showRecordDialog() {
        mRecordDialog = new RecordVoiceDialog(this);
        mRecordDialog.setInterface(new RecordVoiceDialog.IRecordDialog() {

            @Override
            public void onConfirm(File file) {
                mRecordPath = file.getAbsolutePath();
                uploadRecord(mRecordPath);
            }
        });
        mRecordDialog.show();
    }

    public void showGenderWindow() {
        final Dialog dialog = new Dialog(this, R.style.Common_Dialog);
        View view = getLayoutInflater().inflate(R.layout.wheel_gender, null);
        LinearLayout boyLayout = (LinearLayout) view.findViewById(R.id.wheel_boy);
        LinearLayout girlLayout = (LinearLayout) view.findViewById(R.id.wheel_girl);
        boyLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                updateGender(UserItem.gender_boy);
            }
        });
        girlLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                updateGender(UserItem.gender_girl);
            }
        });
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        dialog.show();
    }

    // 更新性别
    public void updateGender(final int gender) {
        JSONObject json = new JSONObject();
        json.put("gender", gender);
        mTaskManager.start(UserTask.updateInfo(json)
                .setCallback(new HttpTaskCallback<Boolean>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        ToastUtil.showToast(R.string.me_update_success);
                        mGenderTv.setText(gender == UserItem.gender_boy ? R.string.me_male : R.string.me_female);
                        notifyUpdateInfo();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file = CropPhotoUtils.onActivityResult(this, requestCode, resultCode, data);
        if (file != null) {
            uoloadHeadImg(file);
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_INDUSTRY:
                userTag = (UserTag) data.getSerializableExtra("tag");
                mIndustryTv.setTextColor(getResources().getColor(R.color.f_gray));
                mIndustryTv.setText(userTag.getProfessionsname());
                mTvNoIndustry.setVisibility(View.GONE);
                mIsSetIndustry = true;
                if (mIsUploadHead) {
                    CommonTask.completeMissionCallback(CommonTask.MISSION_TYPE_COMPLETE_PROFILE);
                }
                break;
            case REQUEST_CODE_NICK:
                updateNick(data.getStringExtra(KEY.TEXT));
                break;
            case REQUEST_CODE_SIGNATURE:
                updateSignature(data.getStringExtra(KEY.TEXT));
                break;
        }
    }

    public void uoloadHeadImg(File file) {
        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setMessage(getString(R.string.me_uploading));
        dialog.show();
        mTaskManager.start(UserTask.updateHead(file)
                .setCallback(new HttpTaskCallback<ImgUrl>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(ImgUrl result) {
                        ToastUtil.showToast(getString(R.string.update_success));
                        String userImgUrl = result.getSmall();
                        ImageUtils.loadCircleHead(userImgUrl, mHeadIv);
                        mIsUploadHead = true;
//                        mHeadAlertIv.setVisibility(View.GONE);
                        mTvNoAvator.setVisibility(View.GONE);
                        mUser.setImgurl(userImgUrl);
                        mUser.setImgmiddleurl(userImgUrl);
                        notifyUpdateInfo();
                        if (mIsSetIndustry) {
                            CommonTask.completeMissionCallback(CommonTask.MISSION_TYPE_COMPLETE_PROFILE);
                        }
                        //更新im头像
                        RongDB rongdb = new RongDB(mActivity);
                        rongdb.update(new IMAccount(RongDB.typeUser, SharedToken.getUserId(mActivity), mUser.getNickname(), userImgUrl, mUser.getCertification(), mUser.getAlias()));
                    }
                }));
    }

    private void updateNick(final String nickname) {
        JSONObject json = new JSONObject();
        json.put("nickname", nickname);
        mTaskManager.start(UserTask.updateInfo(json)
                .setCallback(new HttpTaskCallback<Boolean>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        ToastUtil.showToast(R.string.me_update_success);
                        mNickTv.setText(nickname);
                        mUser.setNickname(nickname);
                        notifyUpdateInfo();
                        RongDB rongdb = new RongDB(mActivity);
                        rongdb.update(new IMAccount(RongDB.typeUser, SharedToken.getUserId(mActivity), nickname, mUser.getImgurl(), mUser.getCertification(), mUser.getAlias()));
                    }
                }));
    }

    private void updateSignature(final String signature) {
        JSONObject json = new JSONObject();
        json.put("signature", signature);
        mTaskManager.start(UserTask.updateInfo(json)
                .setCallback(new HttpTaskCallback<Boolean>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        ToastUtil.showToast(R.string.me_update_success);
                        mSignatureTv.setText(signature);
                        mUser.setSignature(signature);
                        notifyUpdateInfo();
                    }
                }));
    }

    private void notifyUpdateInfo() {
        LocalBroadcastHelper.getInstance().send(new Intent(ACTION.UPDATE_USER_INFO));
    }

    @Override
    public void onTimeOk(String ymd) {
        long birthday = DateUtil.getSeconds(ymd);
        updateBirthday(birthday);
    }

    @Override
    public void onCityChoosed(String cityCode, String cityName) {
        updateCity(cityCode, cityName);
    }

    public void updateBirthday(final long birthday) {
        JSONObject json = new JSONObject();
        json.put("birthday", birthday);
        mTaskManager.start(UserTask.updateInfo(json)
                .setCallback(new HttpTaskCallback<Boolean>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        ToastUtil.showToast(R.string.me_update_success);
                        mAgeTv.setText(DateUtil.getAge(birthday) + "");
                        notifyUpdateInfo();
                    }
                }));
    }

    public void updateCity(final String cityCode, final String cityName) {
        JSONObject json = new JSONObject();
        json.put("cityid", cityCode);
        json.put("cityname", cityName);
        mTaskManager.start(UserTask.updateInfo(json)
                .setCallback(new HttpTaskCallback<Boolean>(mActivity) {

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        ToastUtil.showToast(R.string.me_update_success);
                        mCityTv.setText(cityName);
                        mUser.setCityid(cityCode);
                        notifyUpdateInfo();
                    }
                }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayIv.setImageResource(R.drawable.voice_play);
        }
    }

    private void uploadRecord(String path) {
        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setMessage(getString(R.string.me_uploading));
        dialog.show();
        mTaskManager.start(UserTask.uploadRecord(path)
                .setCallback(new HttpTaskCallback<String>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(String result) {
                        ToastUtil.showToast(R.string.me_update_success);
                        mUser.setPhonetic(result);
                        notifyUpdateInfo();
                        setPlayerDataSource();
                    }
                }));
    }

    private void setPlayerDataSource() {
        if (TextUtils.isEmpty(mRecordPath)) {
            return;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mRecordPath);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayIv.setImageResource(R.drawable.voice_play);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playRecord() {
        if (TextUtils.isEmpty(mUser.getPhonetic())) {
            ToastUtil.showToast(getString(R.string.me_unset));
            return;
        }
        boolean playing = mPlayer.pauseOrResume();
        if (playing) {
            mPlayIv.setImageResource(R.drawable.voice_pause);
        } else {
            mPlayIv.setImageResource(R.drawable.voice_play);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFromH5Web && (!mIsUploadHead || !mIsSetIndustry)) {
            if (!mIsUploadHead) {
                showNotUploadHeadAlertDialog();
            } else {
                if (!mIsSetIndustry) {
                    showNotSetIndustryAlertDialog();
                }
            }
        } else {
            if (mFromH5Web) {
                ToastUtil.showToast(UserInfoMain.this,"已完善个人信息");
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshWeb));
            }
            super.onBackPressed();
        }
    }

    private void showNotUploadHeadAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_you_have_not_upload_head),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        ToastUtil.showToast(UserInfoMain.this,"未完善个人信息");
                        finish();
                    }
                }).show();
    }

    private void showNotSetIndustryAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_you_have_not_set_industry_info),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        finish();
                    }
                }).show();
    }
}
