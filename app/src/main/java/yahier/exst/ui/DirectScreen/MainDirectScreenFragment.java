package yahier.exst.ui.DirectScreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.stbl.base.library.danmaku.DanmaKuWidget;
import com.stbl.base.library.pushheart.PointHeartView;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.SingleSelectFriendActivity;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.DirectScreenPushServer;
import com.stbl.stbl.api.pushServer.data.LivePushInfo;
import com.stbl.stbl.api.pushServer.data.LivePushSendGiftInfo;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.ui.BaseClass.fragment.STBLBaseFragment;
import com.stbl.stbl.ui.DirectScreen.dialog.BulletScreenSelectDialog;
import com.stbl.stbl.ui.DirectScreen.dialog.GiftAnimDialog;
import com.stbl.stbl.ui.DirectScreen.dialog.SendBulletScreenEditDialog;
import com.stbl.stbl.ui.DirectScreen.service.QavsdkServiceApi;
import com.stbl.stbl.ui.DirectScreen.widget.AssembleAudioDataApi;
import com.stbl.stbl.ui.DirectScreen.widget.AudioMultiMembersControlsWidget;
import com.stbl.stbl.ui.DirectScreen.widget.IMPushControl;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FaceConversionUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StringUtils;
import com.stbl.stbl.utils.UISkipUtils;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.stbl.stbl.widget.jpush.JPushManager;
import com.stbl.stbl.widget.risenumber.RiseNumberTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author meteorshower
 *         直播主界面
 */
public class MainDirectScreenFragment extends STBLBaseFragment implements View.OnClickListener, ReceiverUtils.MessageReceiver {

    private RiseNumberTextView tvHeartNum;
    private ImageButton ibtnOwner, ibtnVoice, ibtnTake, ibtnSpeaker;
    private AudioMultiMembersControlsWidget audioMultiWidget;
    private TextView tvBulletScreen;
    private PointHeartView mPointHeartView;
    private int heartNum = 0;//送心个数
    private int roomId;
    private long heartTotalNum = 0;//总送心个数

    int mRotationAngle = 0;
    private Context ctx;

    private boolean isOpenMicrePhone = false;
    private DanmaKuWidget mDanmakuWidget;

    private BulletScreenSelectDialog bulletScreenSelectDialog;
    private static final String BULLETSCREEN_SEND = "发弹幕";
    private static final String BULLETSCREEN_BAN = "禁止弹幕";
    private static final String BULLETSCREEN_CLOSE = "关闭弹幕";
    private static final String BULLETSCREEN_OPEN = "开启弹幕";

    private static final int REQUEST_INVITE_FRIEND = 0x1222;//邀请朋友
    private boolean isOpenBulletScreen = true; // 弹幕的状态，由房主控制的
    private String userId = "";//当前帐号用户id
    private Timer mTimer;
    private TimerTask mTimerTask;

    private Queue<LivePushSendGiftInfo> giftQueue = new LinkedList<>();
    private long giftShowTime = 0;
    private UserItem guestUserItemInfo = null;

    private BroadcastReceiver receiverHomeClick = new BroadcastReceiver() {

        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {

                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    QavsdkServiceApi.runingBackground(getActivity().getApplicationContext());
                    UISkipUtils.startFloatingDirectScreenService(getActivity());
                    getActivity().finish();
                }
            }
        }
    };

    @Override
    public void onMessage(int receiverType, Bundle bundle) {
        switch (receiverType) {
            case ReceiverUtils.DIRECT_GUEST_CHOOSE_TYPE:
                jumpToFriendInvite();
                break;
            case ReceiverUtils.OPEN_MICEPHONE_TYPE: //上麦
                ibtnVoice.setVisibility(View.VISIBLE);
                ibtnVoice.setSelected(true);
                ibtnVoice.setImageResource(R.drawable.icon_jinmai);
                break;
            case ReceiverUtils.CLOSE_MICEPHONE_TYPE: //下麦
                ibtnVoice.setVisibility(View.GONE);
                ibtnVoice.setSelected(false);
                ibtnVoice.setImageResource(R.drawable.icon_jinmai_hui);
                break;
            case ReceiverUtils.DIRECT_PUSH_SERVER_TYPE:
                int pushModelType = bundle.getInt("modelType", -1);
                switch (pushModelType) {
                    case DirectScreenPushServer.DIRECT_PUSH_CLICK_LIKE: //点赞
                        tvHeartNum.setText(String.valueOf(bundle.getInt("likecount", 0)));
                        break;
                }
                break;
            case ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE:{
                int imModelType = bundle.getInt("ImModelType", -1);
                switch(IMPushControl.getIMPushEnum(imModelType)) {
                    case IM_PUSH_DANMAKU: {//弹幕消息
                        LivePushInfo info = (LivePushInfo) bundle.getSerializable("livePushInfo");
                        if (info != null) {
                            String userName = info.getUsername() + ":  ";
                            SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(ctx, userName + info.getMsg());
                            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_yellow)), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mDanmakuWidget.pushDanmakuTextMsg(spannableString, false);
                        }
                    }
                        break;
                    case IM_PUSH_SEND_GIFT:{//送礼
                        boolean isMine = bundle.getBoolean("isMine", false);
                        if(isMine){
                            LivePushSendGiftInfo info = (LivePushSendGiftInfo) bundle.getSerializable("info");
                            if (info != null) {
                                giftQueue.add(info);
                                mHandler.sendEmptyMessage(0x12);
                            }
                        }
                    }
                        break;
                    case IM_PUSH_DANMAKU_MODEL:{//弹幕状态
                        int status = bundle.getInt("danmakuStatus", 1);
                        mDanmakuWidget.setIsOpen(status == 1);
                    }
                        break;
                }
            }
                    break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_group_sreen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ctx = getActivity();
        userId = SharedToken.getUserId(ctx);
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getActivity().registerReceiver(receiverHomeClick, filter);
        ReceiverUtils.addReceiver(this);

        audioMultiWidget = (AudioMultiMembersControlsWidget) getView().findViewById(R.id.audio_place_widget);
        ibtnVoice = (ImageButton) getView().findViewById(R.id.close_microphone_ibtn);
        ibtnTake = (ImageButton) getView().findViewById(R.id.take_heart_ibtn);
        tvHeartNum = (RiseNumberTextView) getView().findViewById(R.id.take_heart_num_tv);
        mDanmakuWidget = (DanmaKuWidget) getView().findViewById(R.id.danmaku_widget);
        tvBulletScreen = (TextView) getView().findViewById(R.id.tv_bullet_screen);
        ibtnSpeaker = (ImageButton) getView().findViewById(R.id.close_speaker_ibtn);
        ibtnOwner = (ImageButton) getView().findViewById(R.id.audio_avatar_btn);
        mPointHeartView = (PointHeartView) getView().findViewById(R.id.point_heart_view);

        ibtnTake.setOnClickListener(this);
        ibtnVoice.setOnClickListener(this);
        tvBulletScreen.setOnClickListener(this);
        ibtnSpeaker.setOnClickListener(this);
        ibtnOwner.setOnClickListener(this);

        getView().findViewById(R.id.btn_change_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctx instanceof DirectScreenControlActivity) {
                    ((DirectScreenControlActivity) ctx).showOnLinePager();
                }
            }
        });

        QavsdkManger.getInstance().setNetType(ctx);

        Bundle bundle = getArguments();
        roomId = bundle.getInt("roomId", 0);
        if (!QavsdkManger.getInstance().onCreate(getView()) || roomId == 0) {
            ToastUtil.showToast(ctx, "房间号不存在!!");
            getActivity().finish();
        }

        audioMultiWidget.setLiveRoomId(roomId);

        JPushManager.getInstance().setTag(ctx, roomId);

        QavsdkServiceApi.startQavsdkScreenService(getActivity().getApplicationContext(), roomId);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendPraise(heartNum);
                heartNum = 0;
                if (mHandler != null) mHandler.postDelayed(this, 5000);
            }
        }, 5000);

        mTimer = new Timer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_INVITE_FRIEND) {//邀请嘉宾
                guestUserItemInfo  = (UserItem) data.getSerializableExtra(EXTRA.USER_ITEM);
                if (guestUserItemInfo != null) {
                    inviteFriend(guestUserItemInfo);
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    if (giftQueue.size() > 0) {
                        showGiftDialog(giftQueue.poll());
                    }
                    break;
                case 0x12:
                    if (mHandler == null) break;
                    if (System.currentTimeMillis() - giftShowTime > 2000) {
                        mHandler.sendEmptyMessage(0x11);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mHandler != null)
                                    mHandler.sendEmptyMessage(0x11);
                            }
                        }, 2000);
                    }
                    break;
                case 0x13:
                    if (msg.arg1 == 0)
                        mPointHeartView.sendPushHeartArray();
                    else
                        mPointHeartView.sendPushHeart();
                    break;
            }
        }
    };

    private void jumpToFriendInvite() {
        Intent intent = new Intent(ctx, SingleSelectFriendActivity.class);
        //intent.putExtra("choiceMode", ChoiceFriendsAct.choiceModeSingle);
        startActivityForResult(intent, REQUEST_INVITE_FRIEND);
    }

    private void inviteFriend(UserItem userItem) {
        LogUtil.logD("LogUtil", "roomId-：" + roomId + "_touserid-:" + userItem.getUserid());
        Params params = new Params();
        params.put("roomid", roomId);
        params.put("touserid", userItem.getUserid());
        new HttpEntity(ctx).commonPostData(Method.liveRoomInviteFriend, params, this);
    }

    private long sendPraiseTime;
    private boolean enableSpeaker = true; //扬声器、耳机的开关状态

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_heart_ibtn://送心

                heartNum++;

                tvHeartNum.setText(String.valueOf(++heartTotalNum));
                if (System.currentTimeMillis() - sendPraiseTime >= 5000) {
                    sendPraiseTime = System.currentTimeMillis();
                    sendPraise(heartNum);
                    heartNum = 0;
                }
                mPointHeartView.sendPushHeart();
                //打开\关闭麦克风
//				isOpenMicrePhone = !isOpenMicrePhone;
                break;
            case R.id.close_microphone_ibtn://静麦
                QavsdkManger.getInstance().setAudioEnableMic(!ibtnVoice.isSelected());
                ibtnVoice.setSelected(!ibtnVoice.isSelected());
                ibtnVoice.setImageResource(ibtnVoice.isSelected() ? R.drawable.icon_jinmai : R.drawable.icon_jinmai_hui);
                break;
            case R.id.close_speaker_ibtn: //扬声器  --
//                QavsdkManger.getInstance().setAudioOutputMode();
                enableSpeaker = !enableSpeaker;
                QavsdkManger.getInstance().enableSpeaker(enableSpeaker);
                ibtnSpeaker.setImageResource(enableSpeaker ?
                        R.drawable.icon_chengyuan_jinyin : R.drawable.icon_chengyuan_yijinyin);
                break;
            case R.id.tv_bullet_screen: //弹幕
                if (bulletScreenSelectDialog == null || !bulletScreenSelectDialog.isShowing()) {
                    tvBulletScreen.setTextColor(ctx.getResources().getColor(R.color.gray2));
                    tvBulletScreen.setBackgroundResource(R.drawable.gray_danmu_bg_shape2); //上面2角直角

                    List<String> datas = getPopData();
                    int width = tvBulletScreen.getWidth();
                    bulletScreenSelectDialog = new BulletScreenSelectDialog(ctx, datas, width, new BulletScreenSelectDialog.OnBulletScreenListener() {
                        @Override
                        public void onItemClick(String item) {
                            bulletScreenSelectDialog.dismissPopup();
                            bulletScreenSelectDialog = null;
                            tvBulletScreen.setBackgroundResource(R.drawable.gray_danmu_bg_shape);
                            tvBulletScreen.setTextColor(ctx.getResources().getColor(R.color.gray1));
                            long ownerUserId = audioMultiWidget.getOwnerUserId();
                            boolean isOwner = userId.equals(String.valueOf(ownerUserId));
                            if (isOpenBulletScreen || isOwner) { //房主控制优先
                                bulletScreenStatus(item, isOwner);
                            } else {
                                ToastUtil.showToast(ctx, "房主已经禁止了弹幕");
                            }
                        }

                        @Override
                        public void onDissmiss() {
                            tvBulletScreen.setBackgroundResource(R.drawable.gray_danmu_bg_shape);
                            tvBulletScreen.setTextColor(ctx.getResources().getColor(R.color.gray1));
                        }
                    });
                    bulletScreenSelectDialog.showPopup(tvBulletScreen);
                } else {
                    tvBulletScreen.setTextColor(ctx.getResources().getColor(R.color.gray1));
                    tvBulletScreen.setBackgroundResource(R.drawable.gray_danmu_bg_shape);
                    bulletScreenSelectDialog.dismissPopup();
                    bulletScreenSelectDialog = null;
                }

                break;
            case R.id.audio_avatar_btn://群主头像
                ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_OWNER_CLICK_TYPE, null);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (bulletScreenSelectDialog != null && bulletScreenSelectDialog.isShowing()) {
                bulletScreenSelectDialog.dismissPopup();
            }
        }
    }

    //点赞
    private void sendPraise(int num) {
        Params params = new Params();
        params.put("roomid", roomId);
        params.put("likecount", num);
        new HttpEntity(ctx).commonPostData(Method.imLiveRoomMemberClickLike, params, this);
    }

    //发弹幕
    private void bulletScreenSend(String text) {
        Params params = new Params();
        params.put("roomid", roomId);
        params.put("msg", text);
        new HttpEntity(ctx).commonPostData(Method.liveRoomPopmessageSend, params, this);
    }

    private void bulletScreenStatus(String text, boolean isOwner) {
        switch (text) {
            case BULLETSCREEN_BAN://禁止弹幕
                mDanmakuWidget.setIsOpen(false);
                tvBulletScreen.setText(BULLETSCREEN_BAN);
                changeBulletScreen(false);
                AssembleAudioDataApi.getInstance().sendDanmakuModel(0);
                break;
            case BULLETSCREEN_CLOSE://关闭弹幕
                mDanmakuWidget.setIsOpen(false);
                tvBulletScreen.setText(BULLETSCREEN_CLOSE);
                break;
            case BULLETSCREEN_OPEN://打开弹幕
                mDanmakuWidget.setIsOpen(true);
                tvBulletScreen.setText(BULLETSCREEN_OPEN);
                if (isOwner && !isOpenBulletScreen) {
                    changeBulletScreen(true);
                    AssembleAudioDataApi.getInstance().sendDanmakuModel(1);
                }
                break;
            case BULLETSCREEN_SEND://发弹幕
                if (isOwner && !isOpenBulletScreen)
                    changeBulletScreen(true);
                tvBulletScreen.setText(BULLETSCREEN_OPEN);
                SendBulletScreenEditDialog dialog = new SendBulletScreenEditDialog(ctx, new SendBulletScreenEditDialog.OnSendListener() {
                    @Override
                    public void onSend(String input) {
                        if(!StringUtils.isEmpty(input)) {
                            if (input.length() > 50)
                                input = input.substring(0, 50) + "...";
                            AssembleAudioDataApi.getInstance().sendDanmakuMsg(input);
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    //根据状态添加需要的选项
    private List<String> getPopData() {
        List<String> datas = new ArrayList<>();
        if (mDanmakuWidget.isOpen()) {
            datas.add(BULLETSCREEN_CLOSE);
        } else {
            datas.add(BULLETSCREEN_OPEN);
        }
        long ownerUserId = audioMultiWidget.getOwnerUserId();
        if (userId.equals(String.valueOf(ownerUserId))) { //房主
            datas.add(BULLETSCREEN_BAN);
        }
        datas.add(BULLETSCREEN_SEND);
        return datas;
    }

    //开启/关闭弹幕
    private void changeBulletScreen(final boolean flag) {
        Params params = new Params();
        params.put("roomid", roomId);
        params.put("status", flag ? 1 : 0);
        new HttpEntity(ctx).commonPostData(Method.imLiveBulletScreenModify, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                        ToastUtil.showToast(getActivity(), item.getErr().getMsg());
                        return;
                    }
                }
//				String obj = JSONHelper.getStringFromObject(item.getResult());
                if (methodName.equals(Method.imLiveBulletScreenModify)) { //弹幕状态改变
                    if (!flag) {
                        ToastUtil.showToast(ctx, "禁止弹幕成功");
                    } else {
                        ToastUtil.showToast(ctx, "开启弹幕成功");
                    }
                    isOpenBulletScreen = flag;
                }
            }
        });
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {

    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        LogUtil.logD("LogUtil", "methodName-:" + methodName + "--result-:" + result);
        switch (methodName) {
            case Method.liveRoomInviteFriend:
                try {
                    JSONObject json = new JSONObject(valueObj);
                    int usertype = json.optInt("usertype", 0);
                    if (usertype == 1) {
                        AssembleAudioDataApi.getInstance().sendGuestMicplace(guestUserItemInfo);
                    }
                    ToastUtil.showToast("邀请成功");
                    guestUserItemInfo = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Method.liveRoomPopmessageSend:
                try {
                    if (Integer.parseInt(valueObj) == 1) {
//						ToastUtil.showToast(ctx,"发送成功");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case Method.imLiveRoomMemberClickLike:
                try {
                    JSONObject obj = new JSONObject(valueObj);

                    long newLikeCount = obj.optLong("totallikecount", 0);
                    logger.e("NewLikeCount : " + newLikeCount + " -- LastLikeCount : " + heartNum);
                    if (newLikeCount > heartTotalNum) {
                        mPointHeartView.sendPushHeartArray(10);
                        if (newLikeCount > Integer.MAX_VALUE) {  //不支持long类型
                            tvHeartNum.setText(String.valueOf(heartTotalNum));
                        } else {
                            tvHeartNum.setDuration(4900);
                            tvHeartNum.withNumber((int) heartTotalNum, (int) newLikeCount).start();
                        }
                        heartTotalNum = newLikeCount;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void showDialog(String message) {
        if (dialog == null)
            dialog = new STProgressDialog(ctx);

        if (dialog.isShowing())
            dialog.dismiss();
        else {
            dialog.setMsgText(message);
            dialog.show();
        }
    }

    private class TimerPushHeartTask extends TimerTask {
        @Override
        public void run() {
            Random random = new Random();
            int number = random.nextInt(100);
            Message msg = new Message();
            msg.what = 0x13;
            msg.arg1 = number % 8;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public int getNavigationResId() {
        return 0;
    }


    //显示别人送自己礼物的弹框
    private void showGiftDialog(final LivePushSendGiftInfo info) {
//		int giftId = info.getGiftid();
        if (info == null) return;
        LogUtil.logD("LogUtil", "giftInfo--：" + info.toString());
        ImageUtils.loadBitmap(info.getGiftimgurl(), new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                GiftAnimDialog dialog = new GiftAnimDialog(ctx, bitmap, 2000, info.getUsername(), "一个" + info.getGiftname());
                dialog.showDialog(mHandler);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mDanmakuWidget.onResume();
        QavsdkServiceApi.onResume(getActivity().getApplicationContext());
        UISkipUtils.stopFloatingDirectScreenService(getActivity());
        QavsdkServiceApi.cancelNoticeService(getActivity().getApplicationContext());
        audioMultiWidget.onResume();
        mPointHeartView.resetPushHeart();
        mTimer = new Timer();
        mTimerTask = new TimerPushHeartTask();
        mTimer.schedule(mTimerTask, 3000, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDanmakuWidget.onPause();
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioMultiWidget.onDestory();
        getActivity().unregisterReceiver(receiverHomeClick);
        ReceiverUtils.removeReceiver(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

}
