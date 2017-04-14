package yahier.exst.ui.DirectScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.stbl.stbl.R;
import com.stbl.stbl.api.data.directScreen.GuestInviteInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.DirectScreenPushServer;
import com.stbl.stbl.api.utils.preferences.LoadStore;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.ui.BaseClass.fragment.STBLBaseFragmentActivity;
import com.stbl.stbl.ui.DirectScreen.dialog.InviteTipsDialog;
import com.stbl.stbl.ui.DirectScreen.homeNotify.IMError;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomGroupManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomGroupCallBack;
import com.stbl.stbl.ui.DirectScreen.service.QavsdkServiceApi;
import com.stbl.stbl.ui.DirectScreen.widget.AssembleAudioDataApi;
import com.stbl.stbl.ui.DirectScreen.widget.IMPushControl;
import com.stbl.stbl.ui.ItemAdapter.vp.ViewPagerStateFragmentAdapter;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UISkipUtils;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.a.a.a.a.g;

/**
 * @author meteorshower
 * 直播管理类
 */
public class DirectScreenControlActivity extends STBLBaseFragmentActivity implements View.OnClickListener, ReceiverUtils.MessageReceiver{
	private String flag = DirectScreenControlActivity.this.getClass().getSimpleName();
	private String flagGuest = flag + "Guest";
	private ViewPager vp;
	private int roomId;
	private String roomPwd;

	@Override
	public void onMessage(int receiverType, Bundle bundle) {
		switch(receiverType){
			case ReceiverUtils.DIRECT_UPDATE_TITLE_TYPE:
				String nickName = bundle.getString("nickName","");
				if (nickName != null && !nickName.equals(""))
				navigationView.setTitleBar(nickName + "的直播间");
				break;
			case ReceiverUtils.DIRECT_UPDATE_ONLINE_NUM_TYPE:
				long memberNum = bundle.getLong("member_num", 0);
				navigationView.setTvMemberNum(memberNum + "人旁观中");
			case ReceiverUtils.DIRECT_PUSH_SERVER_TYPE:{
				int pushModelType = bundle.getInt("modelType", -1);
				switch(pushModelType){
					case DirectScreenPushServer.DIRECT_PUSH_GUEST_DETAILS: {//嘉宾邀请
						final GuestInviteInfo inviteInfo = (GuestInviteInfo) bundle.getSerializable("pushinfo");
						if (inviteInfo == null) break;
						String userId = SharedToken.getUserId(this);
						String guestid = String.valueOf(LoadStore.getInstance().getRoomGuest());
						String ownerId = String.valueOf(LoadStore.getInstance().getRoomOwner());
						if (userId != null && userId.equals(ownerId)) return;
						if (userId != null && userId.equals(guestid)) return;

						InviteTipsDialog dialog = new InviteTipsDialog(this, "直播邀请", false, inviteInfo.getImgurl(), inviteInfo.getUsername() + "邀请您进入直播", "进入直播", new InviteTipsDialog.OkClickListener() {
							@Override
							public void onClick() {
								TipsDialog tipsDialog = new TipsDialog(DirectScreenControlActivity.this, "温馨提示", "确定关闭当前房间，进入新房间吗？", "取消", "确认");
								tipsDialog.setOnTipsListener(new TipsDialog.OnTipsListener() {
									@Override
									public void onConfirm() {
										Bundle tabhomeBundle = new Bundle();
										tabhomeBundle.putInt("roomId", inviteInfo.getRoomid());
										ReceiverUtils.sendReceiver(ReceiverUtils.TAB_HOME_IN_ROOM, tabhomeBundle);
										QavsdkServiceApi.stopQavsdkScreenService(DirectScreenControlActivity.this);
										UISkipUtils.stopFloatingDirectScreenService(DirectScreenControlActivity.this);
										finish();
										//等房间关闭的广播回调
									}

									@Override
									public void onCancel() {
									}
								});
								tipsDialog.show();
								}
						});
						dialog.show();
					}
					break;
				}
			}
			break;
			case ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE:{
				int imModelType = bundle.getInt("ImModelType", -1);
				logger.e(" --------------- ImModelType : "+imModelType+" ---------------------- ");
				switch(IMPushControl.getIMPushEnum(imModelType)) {
					case IM_PUSH_CLOSE_ROOM:
						int closeroomid = bundle.getInt("roomId");
						int roomid = LoadStore.getInstance().getRoomId();//每次创建完房间会保存
						if (roomid == closeroomid)
							finish();
						break;
					case IM_PUSH_SHOT_OFF_ROOM:
						boolean isMine = bundle.getBoolean("isMine", false);
						if(isMine) {
							finish();
						}
						break;
				}
			}
				break;

		}
	}

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main_group_sreen);

		ReceiverUtils.addReceiver(this);

		roomId = getIntent().getIntExtra("roomId", 0);
		roomPwd = getIntent().getStringExtra("roomPwd");

		if(roomId == 0){
			ToastUtil.showToast(this, "数据初始化错误");
			finish();
		}

		AssembleAudioDataApi.getInstance().initAssemble(roomId);
		logger.i(" -------------------- Create Direct Screen Room Success : "+ roomId +" -------------------------- ");

		navigationView.setTitleBar("直播间",true);

		navigationView.setClickLeftListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeControl();
			}
		});

		vp = (ViewPager)findViewById(R.id.view_pager);

		MainDirectScreenFragment mainScreeFragment = new MainDirectScreenFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("roomId", roomId);
		bundle.putString("roomPwd", roomPwd);
		mainScreeFragment.setArguments(bundle);
		fragmentList.add(mainScreeFragment);

		DirectScreenOnLineFragment onLineFragment = new DirectScreenOnLineFragment();
		Bundle onlineBundle = new Bundle();
		onlineBundle.putInt("roomId", roomId);
		onLineFragment.setArguments(onlineBundle);
		fragmentList.add(onLineFragment);

		vp.setAdapter(new ViewPagerStateFragmentAdapter(getSupportFragmentManager(), null, fragmentList));
		vp.setOffscreenPageLimit(1);
		vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
				if (i == 0) {
					navigationView.setTextClickRight("关闭", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							TipsDialog.popup(DirectScreenControlActivity.this, "直播间正在进行,\n是否关闭直播间?", "取消", "确定", new TipsDialog.OnTipsListener() {
								@Override
								public void onConfirm() {
									exitRoom();
								}

								@Override
								public void onCancel() {

								}
							});
						}
					});
				} else {
					navigationView.getTextClickRight().setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageSelected(int i) {

			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
		vp.setCurrentItem(0);


		boolean isGuest = getIntent().getBooleanExtra("isGuest", false);
		if(isGuest){
			AssembleAudioDataApi.getInstance().sendMicplaceData(5, 1, 1, IMPushControl.IM_PUSH_GUEST_JOIN_ROOM);
		}else {
			boolean firstJoinRoom = getIntent().getBooleanExtra("firstJoinRoom", false);
			if (firstJoinRoom)
				AssembleAudioDataApi.getInstance().sendJoinRoomMsg();
		}
	}

	@Override
	public void httpParseResult(String methodName, String result, String valueObj) {
		Log.e("MethodName", "DirectScreenControlActivity");
	}

	@Override
	public void httpParseError(String methodName, BaseItem baseItme) {

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == KeyEvent.KEYCODE_BACK){
			closeControl();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//直播页面点击侧边图标事件
	public void showOnLinePager(){
		if (vp != null){
			vp.setCurrentItem(1);
		}
	}
	private void closeControl(){
		if(vp.getCurrentItem() != 0){
			vp.setCurrentItem(0);
		}else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					QavsdkServiceApi.runingBackground(getApplicationContext());
					UISkipUtils.startFloatingDirectScreenService(DirectScreenControlActivity.this);
					finish();
				}
			});
		}
	}

	private void exitRoom(){
		RoomGroupManager.getInstance().getSelfInfo(RoomGroupManager.getInstance().getRoomGroupId(), new OnRoomGroupCallBack<TIMGroupSelfInfo, Integer>() {
			@Override
			public void onRoomGroupError(int errorValue) {

				QavsdkServiceApi.stopQavsdkScreenService(getApplicationContext());
				finish();
			}

			@Override
			public void onRoomGroupSuccess(String groupId, TIMGroupSelfInfo timGroupSelfInfo, Integer integer) {

				switch(timGroupSelfInfo.getRole()){
					case Owner://关闭房间
						AssembleAudioDataApi.getInstance().sendCloseRoomMsg();
						break;
					case Admin:
					case Normal://退出房间
						AssembleAudioDataApi.getInstance().sendQuiteRoomMsg();
						break;
					default:
						break;
				}

				QavsdkServiceApi.stopQavsdkScreenService(getApplicationContext());
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ReceiverUtils.removeReceiver(this);
	}
}
