package yahier.exst.api.pushServer;

import android.os.Bundle;
import android.util.Log;

import com.stbl.stbl.api.data.directScreen.GuestInviteInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.api.pushServer.data.LivePushInfo;
import com.stbl.stbl.api.pushServer.data.LivePushSendGiftInfo;
import com.stbl.stbl.api.pushServer.data.RoomPushPlaceInfo;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.utils.NumUtils;

import org.json.JSONObject;

/**
 * Created by meteorshower on 16/3/10.
 */
public class DirectScreenPushServer {

    private final String DIRECT_SCREEN_MODEL_TYPE = "pushmodeltype";//分类

    public static final int DIRECT_PUSH_CLICK_LIKE = 1;//点赞
    public static final int DIRECT_PUSH_GUEST_DETAILS = 5;//嘉宾:邀请 (暂时只有邀请) /*、进入、退出*/


    //allJson,
    public DirectScreenPushServer(JSONObject json,long pushtargetid) throws Exception{

        int pushmodeltype = json.optInt(DIRECT_SCREEN_MODEL_TYPE, -1);

        Log.d("JpushReceiver", "Direct Screen Model Type : " + pushmodeltype);
        switch(pushmodeltype){
            case DIRECT_PUSH_CLICK_LIKE:
                directPushClickLike(json);
                break;
            case DIRECT_PUSH_GUEST_DETAILS:
                if (isMine(pushtargetid)) {
                    directGuestInvite(json.toString());
                }
                break;
        }
    }

    //邀请嘉宾
    private void directGuestInvite(String json){
        if (json == null) return;
        GuestInviteInfo info = JSONHelper.getObject(json,GuestInviteInfo.class);
        if (info == null){
            LogUtil.logD("LogUtil","GiftInfo-: null");
            return;
        }
        LogUtil.logD("LogUtil","PushInfo-: "+info.toString());
        Bundle bundle = new Bundle();
        bundle.putInt("modelType",DIRECT_PUSH_GUEST_DETAILS);
        bundle.putSerializable("pushinfo", info);
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_PUSH_SERVER_TYPE, bundle);
    }

    //点赞
    private void directPushClickLike(JSONObject json) throws Exception{
        Bundle bundle = new Bundle();
        bundle.putInt("modelType", DIRECT_PUSH_CLICK_LIKE);
        bundle.putString("roomid", json.optString("roomid", ""));
        bundle.putInt("likecount", json.optInt("likecount", 0));
        ReceiverUtils.sendReceiver(ReceiverUtils.DIRECT_PUSH_SERVER_TYPE, bundle);
    }

    //判断是不是自己
    private boolean isMine(long userId){
        long nativeUserId = NumUtils.getObjToLong(SharedToken.getUserId(MyApplication.getStblContext()));
        return nativeUserId == 0 ? false : (userId == nativeUserId);
    }
}
