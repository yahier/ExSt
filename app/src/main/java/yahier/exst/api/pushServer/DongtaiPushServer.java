package yahier.exst.api.pushServer;

import android.os.Bundle;
import android.util.Log;

import com.stbl.stbl.api.data.directScreen.GuestInviteInfo;
import com.stbl.stbl.api.pushReceiver.ReceiverUtils;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.DongtaiNewMsgItem;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.utils.NumUtils;

import org.json.JSONObject;

/**
 * 动态新消息通知推送处理
 * Created by meteorshower on 16/3/10.
 */
public class DongtaiPushServer {

    private final String MODEL_TYPE = "pushmodeltype";//分类

    public static final int PUTSH_DONGTAI_NEW_MESSAGE = 1;//动态新消息


    //allJson,
    public DongtaiPushServer(JSONObject json, long pushtargetid) throws Exception{

        int pushmodeltype = json.optInt(MODEL_TYPE, -1);

        LogUtil.logE("JPushReceiver", "Dongtai Model Type : " + pushmodeltype);
//        switch(pushmodeltype){
//            case PUTSH_DONGTAI_NEW_MESSAGE:
        dongtaiNewMsgPush(json.toString());
//                break;
//        }
    }

    //动态新消息
    private void dongtaiNewMsgPush(String json){
        if (json == null) return;
        DongtaiNewMsgItem info = JSONHelper.getObject(json,DongtaiNewMsgItem.class);
        if (info == null){
            LogUtil.logD("JPushReceiver","GiftInfo-: null");
            return;
        }
        if (!isMine(info.getUserid())) {
            LogUtil.logD("JPushReceiver","接受者不是自己");
            return;
        }
        LogUtil.logD("JPushReceiver","PushInfo-: "+info.toString());
        Bundle bundle = new Bundle();
        bundle.putInt("modelType",PUTSH_DONGTAI_NEW_MESSAGE);
        bundle.putSerializable("pushinfo", info);
        ReceiverUtils.sendReceiver(ReceiverUtils.PUSH_TYPE_DONGTAI_NEW_MESSAGE, bundle);
        //LogUtil.logE("DongtaiPushServer","dongtaiNewMsgPush");
        SharedCommon.putStatusesNotify(true);
    }

    //判断是不是自己
    private boolean isMine(long userId){
        long nativeUserId = NumUtils.getObjToLong(SharedToken.getUserId(MyApplication.getStblContext()));
        return nativeUserId == 0 ? false : (userId == nativeUserId);
    }
}
