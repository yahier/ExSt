package yahier.exst.ui.DirectScreen.widget;

import android.os.Bundle;
import android.text.TextUtils;

import com.stbl.stbl.api.data.directScreen.RoomPlaceInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.data.LivePushInfo;
import com.stbl.stbl.api.pushServer.data.LivePushSendGiftInfo;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.ui.DirectScreen.homeNotify.IMError;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomGroupManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomMsgManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.SendGroupChatApi;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomGroupCallBack;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.SendMsgStateListener;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meteorshower on 16/4/12.
 */
public class AssembleAudioDataApi implements SendMsgStateListener{

    private static AssembleAudioDataApi assembleAudioDataApi = null;

    public static AssembleAudioDataApi getInstance(){
        if(assembleAudioDataApi == null)
            assembleAudioDataApi = new AssembleAudioDataApi();
        return assembleAudioDataApi;
    }

    private int roomId;
    private String userName;
    private String avatarUrl;
    private String userId;

    public void initAssemble(int roomId){
        this.roomId = roomId;
        UserItem user = SharedUser.getUserItem();
        this.userName = TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias();
//        this.userName = user.getNickname();
        this.avatarUrl = user.getImgmiddleurl();
        this.userId = String.valueOf(user.getUserid());
    }

    /** 发送加入房间 */
    public void sendJoinRoomMsg(){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_JOIN_ROOM.getValue()));
            json.put("username", userName);
            json.put("userid", String.valueOf(userId));
            json.put("imgurl", avatarUrl);
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 发送麦位数据 */
    public void sendMicplaceData(int placeindex, int placetype, int micstatus,IMPushControl imPushControl){
        try {
            if(placeindex < 1)
                return;
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(imPushControl.getValue()));
            json.put("placeindex", String.valueOf(placeindex));
            json.put("placetype", String.valueOf(placetype));
            json.put("micstatus", String.valueOf(micstatus));
            switch(imPushControl){
                case IM_PUSH_UP_MICPLACE:
                case IM_PUSH_GUEST_JOIN_ROOM:
                    json.put("memberid", String.valueOf(userId));
                    json.put("nickname", userName);
                    json.put("imgurl", avatarUrl);
                    break;
                case IM_PUSH_DOWN_MICPLACE:
                    break;
                case IM_PUSH_MICPLACE_MODEL:
                    break;
            }
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
            RoomPlaceInfo info = new RoomPlaceInfo(json);
            Bundle bundle = new Bundle();
            bundle.putInt("ImModelType", imPushControl.getValue());
            bundle.putSerializable("roomPlaceInfo", info);
            ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
            setPlaceindex(-1);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 发送弹幕消息 */
    public void sendDanmakuMsg(String msg){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_DANMAKU.getValue()));
            json.put("username", userName);
            json.put("userid", String.valueOf(userId));
            json.put("imgurl", avatarUrl);
            json.put("msg", msg);
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
            LivePushInfo info = new LivePushInfo(json);
            Bundle bundle = new Bundle();
            bundle.putInt("ImModelType", IMPushControl.IM_PUSH_DANMAKU.getValue());
            bundle.putSerializable("livePushInfo", info);
            ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 发送退出房间消息 */
    public void sendQuiteRoomMsg(){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_QUITE_ROOM.getValue()));
            json.put("username", userName);
            json.put("userid", String.valueOf(userId));
            json.put("imgurl", avatarUrl);
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 发送关闭房间消息 */
    public void sendCloseRoomMsg(){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_CLOSE_ROOM.getValue()));
            json.put("username", userName);
            json.put("userid", String.valueOf(userId));
            json.put("imgurl", avatarUrl);
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 踢出房间 */
    public void sendShotRoomMsg(long userId, String userNameValue){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_SHOT_OFF_ROOM.getValue()));
            json.put("username", userNameValue);
            json.put("userid", String.valueOf(userId));
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 踢下麦位 */
    public void sendRemoveMicplace(RoomPlaceInfo info){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_REMOVE_MICPLACE.getValue()));
            json.put("placeindex", String.valueOf(info.getPlaceindex()));
            json.put("placetype", String.valueOf(info.getPlacetype()));
            json.put("micstatus", String.valueOf(info.getMicstatus()));
            json.put("memberid", String.valueOf(info.getMemberid()));
            json.put("nickname", info.getNickname());
            json.put("imgurl", info.getImgurl());
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 送礼物 */
    public void sendMicplaceGift(LivePushSendGiftInfo info){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_SEND_GIFT.getValue()));
            json.put("username", userName);
            json.put("userid", String.valueOf(userId));
            json.put("touserid", String.valueOf(info.getTouserid()));
            json.put("tousername", info.getTousername());
            json.put("giftid", String.valueOf(info.getGiftid()));
            json.put("giftname", info.getGiftname());
            json.put("giftimgurl", info.getGiftimgurl());
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 发送弹幕状态 */
    public void sendDanmakuModel(int danmakuStatus){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_DANMAKU_MODEL.getValue()));
            json.put("danmakuStatus", String.valueOf(danmakuStatus));
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 邀请嘉宾（已在房间用户上麦通知） */
    public void sendGuestMicplace(UserItem info){
        try{
            JSONObject json = new JSONObject();
            json.put("imPushEnum", String.valueOf(IMPushControl.IM_PUSH_UP_MICPLACE.getValue()));
            json.put("placeindex", String.valueOf(5));
            json.put("placetype", String.valueOf(1));
            json.put("micstatus", String.valueOf(1));
            json.put("memberid", String.valueOf(info.getUserid()));
            json.put("nickname", info.getNickname());
            json.put("imgurl", info.getImgurl());
            SendGroupChatApi.getInstance().sendTextMsg(RoomGroupManager.getInstance().getRoomGroupId(), assembleJson(json), this);
            RoomPlaceInfo infoPlace = new RoomPlaceInfo(json);
            Bundle bundle = new Bundle();
            bundle.putInt("ImModelType", IMPushControl.IM_PUSH_UP_MICPLACE.getValue());
            bundle.putSerializable("roomPlaceInfo", infoPlace);
            ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 更新在线人数 */
    public void updateOnLineNumMembers(OnRoomGroupCallBack<List<TIMGroupDetailInfo>, Integer> callBack){
        List<String> groupIdList = new ArrayList<>();
        groupIdList.add(RoomGroupManager.getInstance().getRoomGroupId());
        RoomGroupManager.getInstance().getGroupPublicInfo(groupIdList, callBack);
    }

    private String assembleJson(JSONObject jsonObject) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("modelType", String.valueOf(1));
        json.put("roomId", String.valueOf(roomId));
        json.put("roomGroupId", RoomGroupManager.getInstance().getRoomGroupId());
        json.put("result", jsonObject);
        return json.toString();
    }

    @Override
    public void sendMsgError(int errorValue) {
        switch(errorValue){
            case 6013:
            case 6014:
            case 6018:
                RoomMsgManager.getInstance().loginIM();
                return;
        }
        ToastUtil.showToast(IMError.getErrorValue(errorValue));
    }

    @Override
    public void sendMsgSuccess(TIMMessage timMessage) {

    }

    private int placeindex;//麦位标记

    public int getPlaceindex() {
        return placeindex;
    }

    public void setPlaceindex(int placeindex) {
        this.placeindex = placeindex;
    }
}
