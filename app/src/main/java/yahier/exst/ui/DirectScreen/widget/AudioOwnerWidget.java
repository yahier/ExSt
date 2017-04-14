package yahier.exst.ui.DirectScreen.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.api.data.directScreen.RoomInfo;
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
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.widget.RoundImageView;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;

/**
 * Created by meteorshower on 16/3/10.
 */
public class AudioOwnerWidget extends RelativeLayout implements ReceiverUtils.MessageReceiver, FinalHttpCallback,SendGiftDialog.OnSendGiftCallBack{

    private Logger logger = new Logger("AudioMemberWidget");
    private ImageView iv,ivClose;
    private RoundImageView riv;
    private TextView tv;

    private int defaultSrc;
    private RoomInfo detailsInfo;
    private STProgressDialog dialog;

    private AnimationDrawable animationDrawable;
    private LivePushSendGiftInfo sendGiftInfo;

    @Override
    public void onMessage(int receiverType, Bundle bundle) {
        if(receiverType == ReceiverUtils.DIRECT_OWNER_CLICK_TYPE){
            onOwnerClick();
        }
    }

    public AudioOwnerWidget(Context context, AttributeSet attSet){
        super(context, attSet);
        init();
    }

    public AudioOwnerWidget(Context context, AttributeSet attSet, int defStyleAtt){
        super(context, attSet, defStyleAtt);
        init();
    }

    private void init(){

        LayoutInflater.from(getContext()).inflate(R.layout.widget_audio_owner, this);

        ReceiverUtils.addReceiver(this);
        iv = (ImageView)findViewById(R.id.audio_anim_iv);
        riv = (RoundImageView)findViewById(R.id.audio_avatar);
        tv = (TextView)findViewById(R.id.audio_name);
        ivClose = (ImageView) findViewById(R.id.audio_close_micephone);

        defaultSrc = R.drawable.icon_screen_ordinary;

        riv.setImageResource(defaultSrc);

        animationDrawable = (AnimationDrawable) iv.getDrawable();
        animationDrawable.stop();
    }

    public void setAudioMemberInfo(RoomInfo info){
        this.detailsInfo = info;
        PicassoUtil.load(getContext(), info.getImgurl(), riv, defaultSrc);
        tv.setText(info.getNickname());
        if(isOwner()){
            QavsdkManger.getInstance().setAudioEnableMic(true);
            ReceiverUtils.sendReceiver(ReceiverUtils.OPEN_MICEPHONE_TYPE, null);
        }
    }

    /** 重置数据*/
    public void resetAudioMember(){
        riv.setImageResource(defaultSrc);
        tv.setText("");
    }

//    public void updateCloseMicrePhone(boolean modelType){
//        if(isMine()){
//            ivClose.set
//        }
//    }

    public void onOwnerClick() {
        boolean isMineFlag = isOwner();
        logger.i("is Mine Flag : " + isMineFlag);
        if(!isMineFlag) {//非点击自己的头像
            showMenuDialog(false);
            return;
        }
    }

    public boolean isOwner(){
        long id = NumUtils.getObjToLong(STBLWession.getInstance().readIdentifier());

        if(detailsInfo == null)
            return false;
        boolean owner = id == detailsInfo.getUserid();
        Log.e("AudioMemberWidget", "Owner Flag : " + owner + " id: " + id + " UserId: " + detailsInfo.getUserid());
        return owner;
    }

    private void showMenuDialog(boolean isOwner){
        QavScreenMenuDialog dialog = new QavScreenMenuDialog((Activity)getContext(), isOwner);
        dialog.setOnMenuDialogClickListener(new QavScreenMenuDialog.MenuDialogClickListener() {
            @Override
            public void onMenuDialogClick(int position, boolean isOwner) {

                switch (position) {
                    case R.string.report://举报
                        Intent intent = new Intent(getContext(), ReportStatusesOrUserAct.class);
                        intent.putExtra("type",4);
                        intent.putExtra("referenceid",detailsInfo.getUserid());
                        getContext().startActivity(intent);
                        break;
                    case R.string.follow: //关注
//                        Intent intentAtten = new Intent(getContext(), TribeMainAct.class);
//                        intentAtten.putExtra("userId",detailsInfo.getUserid());
//                        getContext().startActivity(intentAtten);
                        Params params = new Params();
                        params.put("target_userid", detailsInfo.getUserid());
                        new HttpEntity(getContext()).commonPostData(Method.userFollow, params, AudioOwnerWidget.this);
                        break;
                    case R.string.apply_friend: //加朋友
//                        addFriend(detailsInfo.getUserid(),"");
                        //showAddWindow(detailsInfo.getUserid(),detailsInfo.getNickname());
                        new AddFriendUtil(getContext(),null).addFriendDirect(detailsInfo.getUserid(),detailsInfo.getNickname());
                        break;
                    case R.string.send_gift: //送礼
                        SendGiftDialog giftDialog = new SendGiftDialog((Activity) getContext());
                        giftDialog.setOnSendGiftCallBack(AudioOwnerWidget.this);
                        giftDialog.showPopupWindow();
                        break;
                }
            }
        });
        dialog.showPopupWindow();
    }

    @Override
    public void callback(final Gift info) {
        //送礼，先获取支付密码
        Payment.getPassword(getContext(),0, new PayingPwdDialog.OnInputListener() {
            @Override
            public void onInputFinished(String pwd) {
                sendGiftInfo = null;
                if (detailsInfo == null) return;

                sendGiftInfo = new LivePushSendGiftInfo();
                sendGiftInfo.setGiftid(info.getGiftid());
                sendGiftInfo.setGiftname(info.getGiftname());
                sendGiftInfo.setGiftimgurl(info.getGiftimg());
                sendGiftInfo.setTouserid(detailsInfo.getUserid());
                sendGiftInfo.setTousername(detailsInfo.getNickname());

                Params params = new Params();
                params.put("businessid", detailsInfo.getUserid());
                params.put("giftid", info.getGiftid());
                params.put("moduletype", 8);
                if (pwd != null && !pwd.equals(""))
                    params.put("paypwd", pwd);
                params.put("moduleid",detailsInfo.getRoomid());
                new HttpEntity(getContext()).commonPostData(Method.imSendGift, params, AudioOwnerWidget.this);
            }
        });
    }

    @Override
    public void parse(String methodName, String result) {
        LogUtil.logD("LogUtil","methodName:--"+methodName+"-result-:"+result);
        dialogDismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(getContext(), item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName){
            case Method.imSendGift: //送礼
                ToastUtil.showToast(getContext(), "送礼成功");
                if (sendGiftInfo != null)
                    AssembleAudioDataApi.getInstance().sendMicplaceGift(sendGiftInfo);
                break;
            case Method.userFollow: //关注
                ToastUtil.showToast(getContext(), "关注成功");
                break;
        }
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

    public void onResume(){//重建连接

    }

    public void setStartAnim(long[] speakIds){

        if(detailsInfo != null && isSpeakUserFlag(speakIds, detailsInfo.getUserid())){
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

    public void onDestory(){
        ReceiverUtils.removeReceiver(this);
    }
}
