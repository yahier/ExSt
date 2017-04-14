package yahier.exst.ui.DirectScreen.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.api.data.directScreen.RoomInfo;
import com.stbl.stbl.api.data.directScreen.RoomPlaceInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.data.LivePushSendGiftInfo;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.ui.DirectScreen.dialog.QavScreenMenuDialog;
import com.stbl.stbl.ui.DirectScreen.dialog.SendGiftDialog;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.widget.RoundImageView;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;

/**
 * Created by meteorshower on 16/3/10.
 */
public class AudioMemberWidget extends RelativeLayout implements View.OnClickListener, FinalHttpCallback,SendGiftDialog.OnSendGiftCallBack{

    private Logger logger = new Logger("AudioMemberWidget");
    private ImageView iv,ivClose;
    private RoundImageView riv;
    private TextView tv;

    private boolean isGuest = false;//嘉宾

    private int defaultSrc;
    private int defaultGuestSrc;
    private int defaultCloseSrc;
    private RoomPlaceInfo placeInfo;
    private int roomId;
    private STProgressDialog dialog;
    private OnUpdateAudioMemberListener listener;

    private AnimationDrawable animationDrawable;
    private LivePushSendGiftInfo sendGiftInfo;

    private boolean isOwnerFlag = false;//当前操作识别是群主还是其他人

    public AudioMemberWidget(Context context, AttributeSet attSet){
        super(context, attSet);
        init(attSet);
    }

    public AudioMemberWidget(Context context, AttributeSet attSet, int defStyleAtt){
        super(context, attSet, defStyleAtt);
        init(attSet);
    }

    private void init(AttributeSet attrs){

        LayoutInflater.from(getContext()).inflate(R.layout.widget_audio_member, this);

        iv = (ImageView)findViewById(R.id.audio_anim_iv);
        riv = (RoundImageView)findViewById(R.id.audio_avatar);
        tv = (TextView)findViewById(R.id.audio_name);
        ivClose = (ImageView) findViewById(R.id.audio_close_micephone);

        defaultSrc = R.drawable.icon_screen_ordinary;
        defaultGuestSrc = R.drawable.icon_screen_invitation;
        defaultCloseSrc = R.drawable.icon_screen_ordinary_close;

        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AudioMemberAttrs);

        try{
            isGuest = mTypedArray.getBoolean(R.styleable.AudioMemberAttrs_guest, isGuest);
        }finally{
            mTypedArray.recycle();
        }

        riv.setOnClickListener(this);
        riv.setImageResource(isGuest ? defaultGuestSrc : defaultSrc);

        animationDrawable = (AnimationDrawable) iv.getDrawable();
        animationDrawable.stop();
        iv.setVisibility(View.INVISIBLE);
    }

    //是否有人在麦位
    public boolean isEmpty(){
        if (placeInfo == null || placeInfo.getMemberid() == 0){
            return true;
        }
        return false;
    }

    //麦位状态 0:禁用 1:正常 2:禁麦
    public void setAudioMemberInfo(RoomPlaceInfo info, int roomId, boolean isOwnerFlag){
        this.placeInfo = info;
        this.roomId = roomId;
        this.isOwnerFlag = isOwnerFlag;

        if(info.getMicstatus() != 1){
            riv.setImageResource(defaultCloseSrc);
            iv.setVisibility(View.INVISIBLE);
            animationDrawable.stop();
            tv.setText("");
            return;
        }

        switch(IMPushControl.getIMPushEnum(info.getImPushEnum())){
            case IM_PUSH_DOWN_MICPLACE://下麦
            case IM_PUSH_REMOVE_MICPLACE://踢下麦位
                PicassoUtil.load(getContext(), "", riv, isGuest? defaultGuestSrc : defaultSrc);
                tv.setText("");
                iv.setVisibility(View.INVISIBLE);
                break;
            case IM_PUSH_MICPLACE_MODEL:
                riv.setImageResource(defaultSrc);
                break;
            default:
                iv.setVisibility(View.VISIBLE);
                PicassoUtil.load(getContext(), info.getImgurl(), riv, isGuest? defaultGuestSrc : defaultSrc);
                tv.setText(info.getNickname());
                if (isMine()){ //自己在麦位，开起麦克风
                    QavsdkManger.getInstance().setAudioEnableMic(true);
                    ReceiverUtils.sendReceiver(ReceiverUtils.OPEN_MICEPHONE_TYPE, null);
                }
                break;
        }

        animationDrawable.stop();
    }

    /** 是否禁位*/
    public void closeAudioMember(boolean closeAudioMember){
        if(closeAudioMember) {
            riv.setImageResource(defaultCloseSrc);
            riv.setEnabled(false);
            tv.setText("");
        }else{
            riv.setImageResource(defaultSrc);
            riv.setEnabled(true);
            tv.setText("");
        }
    }

    /** 重置数据*/
    public void resetAudioMember(){
        if (placeInfo != null) placeInfo.setMemberid(0);
        riv.setImageResource(isGuest ? defaultGuestSrc : defaultSrc);
        tv.setText("");
    }

//    public void updateCloseMicrePhone(boolean modelType){
//        if(isMine()){
//            ivClose.set
//        }
//    }

    @Override
    public void onClick(View v) {
        logger.i(" --------------------- ON Click -------------------------- ");
        if(!isOwnerFlag && placeInfo != null && placeInfo.getMicstatus() != 1){
            ToastUtil.showToast(getContext(), "此麦位已被房主禁止抢麦!!");
            return;
        }

        boolean isMineFlag = isMine();
        logger.i("is Mine Flag : " + isMineFlag);
        if(isMineFlag) {//点击自己的头像
            showCloseMicrePhone();
            return;
        }

        logger.i(" --------------------- 抢麦位 -------------------------- ");
        //麦位无人操作
        if(placeInfo == null || placeInfo.getMemberid() == 0){
            if(isOwnerFlag && !isGuest){//群主禁位操作
                clickProhibitPlace(placeInfo == null ? 0 : placeInfo.getMicstatus() == 1 ? 0 : 1);
                return;
            }if(!isOwnerFlag && !isGuest){//抢麦操作
                if(listener != null)
                    listener.robMicrePhone(placeInfo.getPlaceindex());
                return;
            }else if(isOwnerFlag && isGuest){//嘉宾邀请（限房主）
                ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_GUEST_CHOOSE_TYPE, null);
                return;
            }
        }

        //麦位有人操作
        logger.i(" Owner Click ----------------- flag : " + isOwnerFlag);
        if(isGuest) {//邀请嘉宾头像
            if (placeInfo != null && placeInfo.getMemberid() != 0)
                showMenuDialog(isOwnerFlag);
        }else{//普通用户头像
            showMenuDialog(isOwnerFlag);
        }
    }

    public boolean isMine(){
        long id = NumUtils.getObjToLong(STBLWession.getInstance().readIdentifier());
        if(placeInfo == null)
            return false;
        return id == placeInfo.getMemberid();
    }

    private void showMenuDialog(boolean isOwner){
        Log.d("AudioMemberWidget", " showMenuDialog is Owner : "+isOwner);
        QavScreenMenuDialog dialog = new QavScreenMenuDialog((Activity)getContext(), isOwner);
        dialog.setOnMenuDialogClickListener(new QavScreenMenuDialog.MenuDialogClickListener() {
            @Override
            public void onMenuDialogClick(int position, boolean isOwner) {

                switch (position) {
                    case R.string.report://举报
                        Intent intent = new Intent(getContext(), ReportStatusesOrUserAct.class);
                        intent.putExtra("type",4);
                        intent.putExtra("referenceid",placeInfo.getMemberid());
                        getContext().startActivity(intent);
                        break;
                    case R.string.shot_off: //踢出。房主功能
                        kickMember(isGuest ? 1 : placeInfo.getPlaceindex(),placeInfo.getPlacetype());
                        break;
                    case R.string.follow://关注
//                        Intent intentAtten = new Intent(getContext(), TribeMainAct.class);
//                        intentAtten.putExtra("userId", placeInfo.getMemberid());
//                        getContext().startActivity(intentAtten);
                        Params params = new Params();
                        params.put("target_userid", placeInfo.getMemberid());
                        new HttpEntity(getContext()).commonPostData(Method.userFollow, params, AudioMemberWidget.this);
                        break;
                    case R.string.apply_friend://加朋友
//                        addFriend(placeInfo.getMemberid(),"");
                        //showAddWindow(placeInfo.getMemberid(),placeInfo.getNickname());
                        new AddFriendUtil(getContext(),null).addFriendDirect(placeInfo.getMemberid(),placeInfo.getNickname());
                        break;
                    case R.string.send_gift://送礼
                        SendGiftDialog giftDialog = new SendGiftDialog((Activity) getContext());
                        giftDialog.setOnSendGiftCallBack(AudioMemberWidget.this);
                        giftDialog.showPopupWindow();
                        break;
                }
            }
        });
        dialog.showPopupWindow();
    }



    /**
     * 踢出某个用户
     * @param placeindex 位置
     * @param placetype 位置类型 0:麦位 1:嘉宾位
     */
    private void kickMember(int placeindex,int placetype){
        showDialog("正在执行踢出麦位操作,请稍候...");
        Params params = new Params();
        params.put("roomid",roomId);
        params.put("placeindex",placeindex);
        params.put("placetype",placetype);
        new HttpEntity(getContext()).commonPostData(Method.imLiveRoomPlaceKickOut,params,this);
    }



    @Override
    public void callback(final Gift info) {
        //送礼，先获取支付密码
        Payment.getPassword(getContext(),0, new PayingPwdDialog.OnInputListener() {
            @Override
            public void onInputFinished(String pwd) {
                sendGiftInfo = null;
                if (placeInfo == null) return;

                sendGiftInfo = new LivePushSendGiftInfo();
                sendGiftInfo.setGiftid(info.getGiftid());
                sendGiftInfo.setGiftname(info.getGiftname());
                sendGiftInfo.setGiftimgurl(info.getGiftimg());
                sendGiftInfo.setTouserid(placeInfo.getMemberid());
                sendGiftInfo.setTousername(placeInfo.getNickname());

                Params params = new Params();
                params.put("businessid", placeInfo.getMemberid());
                params.put("giftid", info.getGiftid());
                params.put("moduletype", 8);
                params.put("paypwd",pwd);
                params.put("moduleid",roomId);
                new HttpEntity(getContext()).commonPostData(Method.imSendGift,params,AudioMemberWidget.this);
            }
        });
    }

    @Override
    public void parse(String methodName, String result) {
        dialogDismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(getContext(), item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());

        switch(methodName){
            case Method.imLiveroomPlaceDown: {//下麦
                QavsdkManger.getInstance().setAudioEnableMic(false);
                AssembleAudioDataApi.getInstance().sendMicplaceData(isGuest ? 5 : placeInfo.getPlaceindex(), isGuest ? 1 : 0, placeInfo.getMicstatus(), IMPushControl.IM_PUSH_DOWN_MICPLACE);
                resetAudioMember();
                ReceiverUtils.sendReceiver(ReceiverUtils.CLOSE_MICEPHONE_TYPE,null);
            }
                break;
            case Method.imSendGift: //送礼
                ToastUtil.showToast(getContext(),"送礼成功");
                if(sendGiftInfo != null)
                    AssembleAudioDataApi.getInstance().sendMicplaceGift(sendGiftInfo);
                break;
            case Method.imLiveRoomPlaceKickOut://踢下麦
                ToastUtil.showToast(getContext(), "成功踢下麦位!!");
                AssembleAudioDataApi.getInstance().sendRemoveMicplace(placeInfo);
                resetAudioMember();
                break;
            case Method.imLiveRoomMasterMicstatusModify://禁止麦位/开启麦位
                resetAudioMember();
                riv.setImageResource(defaultCloseSrc);
                iv.setVisibility(View.INVISIBLE);
                placeInfo.setMicstatus(placeInfo.getMicstatus() == 1 ? 0 : 1);
                AssembleAudioDataApi.getInstance().sendMicplaceData(placeInfo.getPlaceindex(), placeInfo.getPlacetype(),
                        placeInfo.getMicstatus(), IMPushControl.IM_PUSH_MICPLACE_MODEL);
                break;
            case Method.userFollow: //关注
                ToastUtil.showToast(getContext(), "关注成功");
                break;
        }
    }

    //禁位置提示
    private void clickProhibitPlace(final int status){
        TipsDialog.popup(getContext(), status == 0 ? "是否禁止此麦位?" : "是否取消此禁麦?" , "取消", "确定", new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
                showDialog("正在禁位,请稍候...");
                JSONObject json = new JSONObject();
                json.put("roomid", roomId);
                json.put("status", status);
                json.put("placeindex", placeInfo.getPlaceindex());
                json.put("placetype", isGuest ? 1 : 0);
                new HttpEntity(getContext()).commonPostJson(Method.imLiveRoomMasterMicstatusModify, json.toJSONString(), AudioMemberWidget.this);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void showDialog(String message){
        if(dialog == null)
            dialog = new STProgressDialog(getContext());

        if(dialog.isShowing())
            dialog.dismiss();
        else {
            dialog.setMsgText(message);
            dialog.show();
        }
    }

    private void dialogDismiss(){
        if(dialog != null)
            dialog.dismiss();
    }

    public void setOnUpdateAudioMemberListener(OnUpdateAudioMemberListener listener){
        this.listener = listener;
    }

    public interface OnUpdateAudioMemberListener{

        public void updateAudioMember(RoomInfo info);

        public void robMicrePhone(int placeIndex);
    }

    public void onResume(){//重建连接

    }

    public void setStartAnim(long[] speakIds){

        if(placeInfo != null && isSpeakUserFlag(speakIds, placeInfo.getMemberid())){
            iv.setVisibility(View.VISIBLE);
            animationDrawable.stop();
            animationDrawable.start();
            return;
        }
        iv.setVisibility(View.INVISIBLE);
        animationDrawable.stop();
        return;
    }

    private boolean isSpeakUserFlag(long[] speakIds, long nativeUserId){
        for(int i = 0 ; i < speakIds.length; i++){
            if(speakIds[i] == nativeUserId)
                return true;
        }
        return false;
    }

    //下麦
    private void showCloseMicrePhone(){
        TipsDialog.popup(getContext(), "正在操作下麦,是否继续?", "取消", "确定", new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
//                startHttpTime = System.currentTimeMillis();
                showDialog("正在下麦,请稍候...");
                sendJoinDownMicreHandler();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    JSONObject json = new JSONObject();
                    json.put("roomid", roomId);
                    json.put("placeindex", placeInfo.getPlaceindex());
                    new HttpEntity(getContext()).commonPostJson(Method.imLiveroomPlaceDown, json.toJSONString(), AudioMemberWidget.this);
                    break;
            }
        }
    };

    private void sendJoinDownMicreHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }, 500);
    }

    public RoomPlaceInfo getMicplaceInfo(){
        return placeInfo;
    }

}
