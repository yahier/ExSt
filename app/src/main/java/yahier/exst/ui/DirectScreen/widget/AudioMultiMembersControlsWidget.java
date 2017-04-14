package yahier.exst.ui.DirectScreen.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.api.data.directScreen.PlaceIndexPickInfo;
import com.stbl.stbl.api.data.directScreen.RoomInfo;
import com.stbl.stbl.api.data.directScreen.RoomPlaceInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.DirectScreenPushServer;
import com.stbl.stbl.api.pushServer.data.LivePushInfo;
import com.stbl.stbl.api.pushServer.data.LivePushSendGiftInfo;
import com.stbl.stbl.api.pushServer.data.RoomPushPlaceInfo;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.LoadStore;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.ui.DirectScreen.dialog.UpdateChatTopicDialog;
import com.stbl.stbl.ui.DirectScreen.homeNotify.IMError;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomGroupCallBack;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.utils.SpannableUtils;
import com.stbl.stbl.widget.avsdk.MemberInfo;
import com.stbl.stbl.widget.avsdk.QavsdkContacts;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.av.sdk.AVView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AudioMultiMembersControlsWidget extends RelativeLayout implements OnClickListener,AudioMemberWidget.OnUpdateAudioMemberListener, ReceiverUtils.MessageReceiver,FinalHttpCallback{

	private Logger logger = new Logger("AudioMultiMembersControlsWidget");

	private OnAudioMultiMembersClickListener mMemberClickListener = null;

	private int mSelectedVideoSrcType = AVView.VIDEO_SRC_TYPE_NONE;
	boolean mAttached = false;

	private AudioOwnerWidget audioOwner;
	private AudioMemberWidget amOne,amTwo,asOne,asTwo,asThree;
	private TextView tvTopicOwner,tvPushTips;
	private int roomId;
	private RoomInfo roomInfo;
	private STProgressDialog dialog;

	private Queue<SpannableString> spannableStringQueue = new LinkedList<>();
	private long commonInfoTime = 0; //普通消息停留时间，超过20条，每条一秒
	private static final int SHOW_INFO = 0x1111;
	private static final int START_CHECK = 0x2222;
	private boolean isOpenBulletScreen = true; // 弹幕的状态，由房主控制的
	private ReloadAudioTimer reloadAudioTimer;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(QavsdkContacts.ACTION_MEMBER_CHANGE)) {
				notifyDataSetChanged(QavsdkManger.getInstance().getAtAudioMemberList());
			}
		}
	};

	@Override
	public void onMessage(int receiverType, Bundle bundle) {
		if(receiverType == ReceiverUtils.CLOSE_MICEPHONE_TYPE){//下麦
			reloadAudioTimer.setDownMicrePhone(true);
		}else if(receiverType == ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE){//IM消息
			int imModelType = bundle.getInt("ImModelType", -1);
			switch(IMPushControl.getIMPushEnum(imModelType)){
				case IM_PUSH_JOIN_ROOM: {//加入房间
					String userName = bundle.getString("userName", "");
					spannableStringQueue.add(SpannableUtils.formatSpannable(userName + " 加入了房间", 0,
							userName.length(), getResources().getColor(R.color.theme_yellow), 0f));
					mHandler.sendEmptyMessage(START_CHECK);
					updateRoomMemberNum();
				}
					break;
				case IM_PUSH_UP_MICPLACE://上麦
				case IM_PUSH_DOWN_MICPLACE://下麦
				case IM_PUSH_MICPLACE_MODEL: {//麦位状态
					RoomPlaceInfo info = (RoomPlaceInfo) bundle.getSerializable("roomPlaceInfo");
					if (info != null) {
						pushPlaceInfo(info);
					}
				}
					break;
				case IM_PUSH_SHOT_OFF_ROOM: {//踢出房间
					boolean isMine = bundle.getBoolean("isMine", false);
					int micPlaceIndex = isAtMicPlace();
					if (isMine && micPlaceIndex > 0) {
						RoomPlaceInfo info = getMicplaceIndexInfo(micPlaceIndex);
						if(info != null){
							AssembleAudioDataApi.getInstance().sendMicplaceData(info.getPlaceindex(), info.getPlacetype(), info.getMicstatus(), IMPushControl.IM_PUSH_DOWN_MICPLACE);
						}
					}else if(!isMine){
						String userName = bundle.getString("userName", "");
						spannableStringQueue.add(SpannableUtils.formatSpannable(userName + " 被房主踢出了房间", 0,
								userName.length(), getResources().getColor(R.color.theme_yellow), 0f));
						mHandler.sendEmptyMessage(START_CHECK);
						updateRoomMemberNum();
					}
				}
					break;
				case IM_PUSH_QUITE_ROOM: {//关闭房间
					String userName = bundle.getString("userName", "");
					spannableStringQueue.add(SpannableUtils.formatSpannable(userName + " 退出了房间", 0,
							userName.length(), getResources().getColor(R.color.theme_yellow), 0f));
					mHandler.sendEmptyMessage(START_CHECK);
					updateRoomMemberNum();
				}
					break;
				case IM_PUSH_SEND_GIFT:{//送礼物
					boolean isMine = bundle.getBoolean("isMine", false);
					if(!isMine){
						LivePushSendGiftInfo info = (LivePushSendGiftInfo) bundle.getSerializable("info");
						if (info != null) {
							SpannableString spannableString = new SpannableString(info.getUsername() + "送了" + info.getTousername() +"礼物  "+ info.getGiftname());
							spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_yellow)),0, info.getUsername().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_yellow)),
									info.getUsername().length() + 2, info.getUsername().length() + 2 + info.getTousername().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_yellow)),
									spannableString.length() - info.getGiftname().length(),spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							spannableStringQueue.add(spannableString);
							mHandler.sendEmptyMessage(START_CHECK);
						}
					}
				}
					break;
				case IM_PUSH_REMOVE_MICPLACE:{//踢下麦位
					boolean isMine = bundle.getBoolean("isMine", false);
					if(isMine){
						QavsdkManger.getInstance().setAudioEnableMic(false);
					}
					RoomPlaceInfo info = (RoomPlaceInfo) bundle.getSerializable("info");
					if(info != null)
						pushPlaceInfo(info);
				}
					break;
				case IM_PUSH_DANMAKU_MODEL:{//弹幕状态
					int status = bundle.getInt("danmakuStatus", 1);
					logger.e(" ----------------------------- " +status+ " ------------------------------ ");
					boolean danmuStatus = status == 1;
					if (isOpenBulletScreen != danmuStatus) {
						logger.e(" -----------------------------2 " +danmuStatus+ " ------------------------------ ");
						String info = danmuStatus ? "房主开启了弹幕" : "房主关闭了弹幕";
						SpannableString danmuSpannableString = new SpannableString(info);
						danmuSpannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_yellow)),
								0, danmuSpannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						spannableStringQueue.add(danmuSpannableString);
						mHandler.sendEmptyMessage(START_CHECK);
						isOpenBulletScreen = danmuStatus;
					}
				}
					break;
				case IM_PUSH_GUEST_JOIN_ROOM: {//嘉宾加入房间
					RoomPlaceInfo info = (RoomPlaceInfo) bundle.getSerializable("roomPlaceInfo");
					if (info != null) {
						pushPlaceInfo(info);
						//保存当前嘉宾id，用于判断直播不能邀请嘉宾
						LoadStore.getInstance().setRoomGuest(info.getMemberid());
						String guestName = info.getNickname();
						SpannableString spannableString = new SpannableString("房主邀请了嘉宾  " + guestName);
						spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_yellow)),
								spannableString.length() - guestName.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						spannableStringQueue.add(spannableString);
						mHandler.sendEmptyMessage(START_CHECK);

						int otherMicPlace = isAtMicPlace();
						if(otherMicPlace > 0 && otherMicPlace < 5){
							AssembleAudioDataApi.getInstance().sendMicplaceData(otherMicPlace, 0, 1,IMPushControl.IM_PUSH_DOWN_MICPLACE);
						}

						updateRoomMemberNum();
					}
				}
					break;
			}
		}
	}

	public AudioMultiMembersControlsWidget(Context context) {
		super(context);
		init();
	}

	public AudioMultiMembersControlsWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AudioMultiMembersControlsWidget(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.widget_audio_place_screen, this);

		ReceiverUtils.addReceiver(this);

		audioOwner = (AudioOwnerWidget) findViewById(R.id.audio_owner_view);
		amOne = (AudioMemberWidget)findViewById(R.id.audio_member_one);
		amTwo = (AudioMemberWidget)findViewById(R.id.audio_member_two);
		asOne = (AudioMemberWidget)findViewById(R.id.audio_second_one);
		asTwo = (AudioMemberWidget)findViewById(R.id.audio_second_two);
		asThree = (AudioMemberWidget)findViewById(R.id.audio_second_three);

		tvPushTips = (TextView) findViewById(R.id.tv_push_tips);
		tvTopicOwner = (TextView) findViewById(R.id.owner_topic_tv);
		tvTopicOwner.setOnClickListener(this);

		amOne.setOnUpdateAudioMemberListener(this);
		amTwo.setOnUpdateAudioMemberListener(this);
		asOne.setOnUpdateAudioMemberListener(this);
		asTwo.setOnUpdateAudioMemberListener(this);
		asThree.setOnUpdateAudioMemberListener(this);

		reloadAudioTimer = new ReloadAudioTimer();

		updateRoomMemberNum();
	}

	//刷新UI
	public void notifyDataSetChanged(ArrayList<MemberInfo> memberList) {

		if(memberList == null || memberList.size() == 0) {
			Log.e("AudioMemberWidget", " MemeberList Size : 0");
			return;
		}

		Log.e("AudioMemberWidget", " MemeberList Size : "+memberList.size());
		long[] speakIds = new long[memberList.size()];

		for(int i = 0 ; i < memberList.size() ; i++){
			MemberInfo itemInfo = memberList.get(i);
			speakIds[i] = NumUtils.getObjToLong(itemInfo.identifier);
		}

		audioOwner.setStartAnim(speakIds);
		amOne.setStartAnim(speakIds);
		amTwo.setStartAnim(speakIds);
		asOne.setStartAnim(speakIds);
		asTwo.setStartAnim(speakIds);
		asThree.setStartAnim(speakIds);
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
			case R.id.owner_topic_tv://话题
				UpdateChatTopicDialog dialog = new UpdateChatTopicDialog((Activity)getContext());
				dialog.setOnUpdateTopicListener(new UpdateChatTopicDialog.OnUpdateTopicListener() {

					@Override
					public void onUpdateTopic(String topic) {

					}

					@Override
					public void onCanelTopic() {

					}
				});
				dialog.showPopupWindow();
				break;
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mMemberClickListener != null) {
			mMemberClickListener.onMembersHolderTouch();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			return true;
		} else {
			return false;
		}

	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case SHOW_INFO:
					if (spannableStringQueue.size() > 0){
						tvPushTips.setVisibility(View.VISIBLE);
						tvPushTips.setText(spannableStringQueue.poll());
					}else{
						tvPushTips.setVisibility(View.GONE);
					}
					mHandler.sendEmptyMessage(START_CHECK);
					break;
				case START_CHECK:
					if (mHandler == null) return;
					long curTime = System.currentTimeMillis();
					if (spannableStringQueue.size() > 20 && curTime - commonInfoTime > 1000 || curTime - commonInfoTime > 2000) {
						commonInfoTime = curTime;
						mHandler.sendEmptyMessage(SHOW_INFO);
					} else {
						mHandler.sendEmptyMessageDelayed(START_CHECK, 2000);
					}
					break;
			}
		}
	};

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction(QavsdkContacts.ACTION_MEMBER_CHANGE);
			getContext().registerReceiver(mBroadcastReceiver, filter);
			notifyDataSetChanged(QavsdkManger.getInstance().getAtAudioMemberList());
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getContext().unregisterReceiver(mBroadcastReceiver);
			mAttached = false;
		}
	}

	public void setLiveRoomId(int roomId){
		this.roomId = roomId;
		JSONObject json = new JSONObject();
		json.put("roomid", roomId);
		new HttpEntity(getContext()).commonPostJson(Method.imLiveRoomGet, json.toJSONString(), this);
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

		switch (methodName) {
			case Method.imLiveRoomGet://进入房间
				roomInfo = JSONHelper.getObject(obj, RoomInfo.class);
				resetAllMembers();
				pushMembersInfo();

				int num = roomInfo.getMemebertotalcount() - roomInfo.getPickmiccount();
				if (num <= 0) num = 0;
				Bundle bundle = new Bundle();
				bundle.putString("nickName", roomInfo.getNickname());
				bundle.putString("member_num",String.valueOf(num));
				ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_UPDATE_TITLE_TYPE, bundle);
				tvTopicOwner.setText(roomInfo.getTopic());
				SharedToken.putOwnerId(getContext(),roomInfo.getUserid());
				break;
			case Method.imLiveroomPlacePick: {//抢麦
				PlaceIndexPickInfo pickInfo = JSONHelper.getObject(obj, PlaceIndexPickInfo.class);
				if (pickInfo == null) {
					ToastUtil.showToast(getContext(), "数据错误!!");
					return;
				}

				if (pickInfo.getStatus() == 1) {
					QavsdkManger.getInstance().setAudioEnableMic(true);
					ReceiverUtils.sendReceiver(ReceiverUtils.OPEN_MICEPHONE_TYPE, null);

					this.roomInfo = pickInfo.getRoominfo();
					resetAllMembers();
					pushMembersInfo();
					AssembleAudioDataApi.getInstance().sendMicplaceData(AssembleAudioDataApi.getInstance().getPlaceindex(), 0, 1, IMPushControl.IM_PUSH_UP_MICPLACE);
				} else {
					ToastUtil.showToast(getContext(), "抢麦失败!!");
				}
			}
			break;
			case Method.imLiveRoomPlaceChange://切换麦位

				break;
		}
	}

	//重置所有在线数据
	private void resetAllMembers(){

		audioOwner.resetAudioMember();
		amOne.resetAudioMember();
		amTwo.resetAudioMember();
		asOne.resetAudioMember();
		asTwo.resetAudioMember();
		asThree.resetAudioMember();
	}

	private void pushMembersInfo(){
		if (roomInfo != null) LoadStore.getInstance().setRoomOwner(roomInfo.getUserid());
		audioOwner.setAudioMemberInfo(roomInfo);

		boolean isOwnerFlag = audioOwner.isOwner();
		List<RoomPlaceInfo> micplacesList = roomInfo.getMicplaces();
		for(RoomPlaceInfo info : micplacesList){
			pushPlaceInfo(info);
		}

		asTwo.setAudioMemberInfo(roomInfo.getGuestplaces().get(0), roomInfo.getRoomid(), isOwnerFlag);
	}

	/** 房主的UserId */
	public long getOwnerUserId(){
		return roomInfo.getUserid();
	}

	@Override
	public void updateAudioMember(RoomInfo info) {
	}

	@Override
	public void robMicrePhone(int placeIndex) {

		if(isAtMicPlace() > 0){
			ToastUtil.showToast(getContext(), "已在麦位,无法执行其他抢麦操作!!");
//			showDialog("正在切换麦位,请稍候...");
//			JSONObject json = new JSONObject();
//			json.put("roomid", roomId);
//			json.put("placeindex", placeIndex);
//			new HttpEntity(getContext()).commonPostJson(Method.imLiveRoomPlaceChange, json.toJSONString(), this);
			return;
		}

		if(reloadAudioTimer.getReloadFlag()) {
			showDialog("正在抢麦,请稍候...");
			sendJoinDownMicreHandler(placeIndex);
		}
	}

	public void onResume(){//防止因为后台过久,导致断线,发起重连
		audioOwner.onResume();
	}

	public void setOnAudioMultiMembersClickListener(OnAudioMultiMembersClickListener lisnter) {
		if (lisnter == null) {
			return;
		}
		mMemberClickListener = lisnter;
	}

	public interface OnAudioMultiMembersClickListener {

		public void onMembersClick(final String identifier, final int videoSrcType);

		public void onMembersHolderTouch();
	}

	public void onDestory(){
		ReceiverUtils.removeReceiver(this);
		audioOwner.onDestory();
		if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
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

	private Handler handler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case 1:
					AssembleAudioDataApi.getInstance().setPlaceindex(msg.arg1);
					JSONObject json = new JSONObject();
					json.put("roomid", roomId);
					json.put("placeindex", msg.arg1);
					new HttpEntity(getContext()).commonPostJson(Method.imLiveroomPlacePick, json.toJSONString(), AudioMultiMembersControlsWidget.this);
					break;
			}
		}
	};

	private void sendJoinDownMicreHandler(final int plcaceIndex){
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = plcaceIndex;
				handler.sendMessage(msg);
			}
		}, 500);
	}

	private void pushPlaceInfo(RoomPlaceInfo info){
		boolean isOwnerFlag = audioOwner.isOwner();
		switch (info.getPlaceindex()) {
			case 1:
				amOne.setAudioMemberInfo(info, roomId, isOwnerFlag);
				break;
			case 2:
				amTwo.setAudioMemberInfo(info, roomId, isOwnerFlag);
				break;
			case 3:
				asOne.setAudioMemberInfo(info, roomId, isOwnerFlag);
				break;
			case 4:
				asThree.setAudioMemberInfo(info, roomId, isOwnerFlag);
				break;
			case 5:
				asTwo.setAudioMemberInfo(info, roomId, isOwnerFlag);
				break;
		}
	}

	/** 是否在麦位及其位置 */
	private int isAtMicPlace(){
		if(amOne.isMine()){
			return 1;
		}else if(amTwo.isMine()){
			return 2;
		}else if(asOne.isMine()){
			return 3;
		}else if(asThree.isMine()){
			return 4;
		}else if(asTwo.isMine()){
			return 5;
		}
		return 0;
	}

	private RoomPlaceInfo getMicplaceIndexInfo(int micplaceIndex){
		switch(micplaceIndex){
			case 1:
				return amOne.getMicplaceInfo();
			case 2:
				return amTwo.getMicplaceInfo();
			case 3:
				return asOne.getMicplaceInfo();
			case 4:
				return asThree.getMicplaceInfo();
			case 5:
				return asTwo.getMicplaceInfo();
			default:
				return null;
		}
	}

	private void updateRoomMemberNum(){
//		AssembleAudioDataApi.getInstance().updateOnLineNumMembers(new OnRoomGroupCallBack<List<TIMGroupDetailInfo>, String>() {
//			@Override
//			public void onRoomGroupError(int errorValue) {
//				logger.e(" --------------------------------------------------------------- ");
//				logger.e(" ------------------- TIM Login Error : "+ IMError.getErrorValue(errorValue)+" ------------------- ");
//				logger.e(" --------------------------------------------------------------- ");
//			}
//
//			@Override
//			public void onRoomGroupSuccess(String groupId, List<TIMGroupDetailInfo> timGroupDetailInfos, String s) {
//				if(timGroupDetailInfos != null && timGroupDetailInfos.size() > 0){
//					TIMGroupDetailInfo info = timGroupDetailInfos.get(0);
//					Bundle bundle = new Bundle();
//					bundle.putLong("member_num", info.getMemberNum());
//					ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_UPDATE_ONLINE_NUM_TYPE, bundle);
//
//					logger.e(" --------------------------------------------------------------- ");
//					logger.e(" ------------------- 最大群人数 : "+info.getMaxMemberNum()+" ------------------- ");
//					logger.e(" --------------------------------------------------------------- ");
//				}
//			}
//		});
	}
}
