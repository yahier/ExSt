package yahier.exst.api.imChatPush;

import android.os.Bundle;

import com.stbl.stbl.api.data.directScreen.RoomPlaceInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.data.LivePushInfo;
import com.stbl.stbl.api.pushServer.data.LivePushSendGiftInfo;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomGroupManager;
import com.stbl.stbl.ui.DirectScreen.widget.IMPushControl;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by meteorshower on 16/4/12.
 */
public class ImDirectScreenPushServer {

    public void initDirectScreenPushData(JSONObject json, int roomId, String roomGroupId) throws JSONException{

        if(StringUtils.isEmpty(roomGroupId) || !roomGroupId.equals(RoomGroupManager.getInstance().getRoomGroupId())){
            return;
        }

        int imPushEnumValue = json.optInt("imPushEnum", -1);
        switch(IMPushControl.getIMPushEnum(imPushEnumValue)){
            case IM_PUSH_JOIN_ROOM:
                pushJoinRoom(json, imPushEnumValue);
                break;
            case IM_PUSH_UP_MICPLACE://上麦消息
            case IM_PUSH_DOWN_MICPLACE://下麦消息
            case IM_PUSH_MICPLACE_MODEL://麦位状态：开启\关闭
            case IM_PUSH_GUEST_JOIN_ROOM://嘉宾进入房间
                pushMicplace(json, imPushEnumValue);
                break;
            case IM_PUSH_DANMAKU://弹幕消息
                pushDanmaku(json, imPushEnumValue);
                break;
            case IM_PUSH_CLOSE_ROOM://关闭房间
                pushCloseRoom(roomId, imPushEnumValue);
                break;
            case IM_PUSH_QUITE_ROOM://退出房间
                pushQuitRoom(json, imPushEnumValue);
                break;
            case IM_PUSH_SHOT_OFF_ROOM://踢出房间
                pushShotOffRoom(json, imPushEnumValue);
                break;
            case IM_PUSH_SEND_GIFT://送礼物
                pushSendGift(json, imPushEnumValue);
                break;
            case IM_PUSH_REMOVE_MICPLACE://提下麦位
                pushRemoveMicplace(json, imPushEnumValue);
                break;
            case IM_PUSH_DANMAKU_MODEL://弹幕状态
                pushDanmakuModel(json, imPushEnumValue);
                break;
            default:
                break;
        }
    }

    /** 加入房间 */
    private void pushJoinRoom(JSONObject json, int imModelType) throws JSONException{
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putString("userName", json.optString("username", ""));
        bundle.putLong("userId", json.optLong("userid", 0));
        bundle.putString("avatarUrl", json.optString("imgurl", ""));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 麦位消息分发 */
    private void pushMicplace(JSONObject json, int imModelType) throws JSONException{
        RoomPlaceInfo info = new RoomPlaceInfo(json);
        Bundle bundle = new Bundle();
        bundle.putSerializable("roomPlaceInfo", info);
        bundle.putInt("ImModelType", imModelType);
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 弹幕消息 */
    private void pushDanmaku(JSONObject json, int imModelType) throws JSONException{
        LivePushInfo info = new LivePushInfo(json);
        Bundle bundle = new Bundle();
        bundle.putSerializable("livePushInfo", info);
        bundle.putInt("ImModelType", imModelType);
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 关闭房间 */
    private void pushCloseRoom(int roomId, int imModelType) throws JSONException{
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putInt("roomId", roomId);
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 退出房间消息 */
    private void pushQuitRoom(JSONObject json, int imModelType) throws JSONException{
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putString("userName", json.optString("username", ""));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 踢出房间 */
    private void pushShotOffRoom(JSONObject json, int imModelType) throws JSONException{
        long userId = json.optLong("userid", 0);
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putString("userName", json.optString("username", ""));
        bundle.putLong("userId", userId);
        bundle.putBoolean("isMine", isMine(userId));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 送礼物 */
    private void pushSendGift(JSONObject json, int imModelType) throws JSONException{
        LivePushSendGiftInfo info = new LivePushSendGiftInfo(json);
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putSerializable("info", info);
        bundle.putBoolean("isMine", isMine(json.optLong("touserid", 0)));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 踢下麦位 */
    private void pushRemoveMicplace(JSONObject json, int imModelType) throws JSONException{
        RoomPlaceInfo info = new RoomPlaceInfo(json);
        info.setMemberid(0);
        info.setNickname("");
        info.setImgurl("");
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putSerializable("info", info);
        bundle.putBoolean("isMine", isMine(json.optLong("memberid", 0)));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /** 弹幕状态 */
    private void pushDanmakuModel(JSONObject json, int imModelType) throws JSONException{
        Bundle bundle = new Bundle();
        bundle.putInt("ImModelType", imModelType);
        bundle.putInt("danmakuStatus", json.optInt("danmakuStatus", 0));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_IMSDK_SERVER_TYPE, bundle);
    }

    /**********************************************************************************************/
    /** 是否为自己 */
    private boolean isMine(Object value){
        return SharedToken.getUserId(MyApplication.getContext()).equals(String.valueOf(value));
    }
}
