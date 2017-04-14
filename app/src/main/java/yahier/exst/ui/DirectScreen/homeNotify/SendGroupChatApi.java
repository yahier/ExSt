package yahier.exst.ui.DirectScreen.homeNotify;

import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.SendMsgStateListener;
import com.tencent.TIMCustomElem;
import com.tencent.TIMFaceElem;
import com.tencent.TIMImageElem;
import com.tencent.TIMLocationElem;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;

/**
 * Created by meteorshower on 16/4/10.
 * 发送群组消息API
 */
public class SendGroupChatApi {

    private Logger logger = new Logger(this.getClass().getSimpleName());
    private static SendGroupChatApi sendGroupChatApi = null;

    public static SendGroupChatApi getInstance(){
        if(sendGroupChatApi == null)
            sendGroupChatApi = new SendGroupChatApi();
        return sendGroupChatApi;
    }

    /** 文本消息 */
    public void sendTextMsg(String groupId, String value, SendMsgStateListener listener){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        try{
            elem.setText(value);
            msg.addElement(elem);
            RoomMsgManager.getInstance().sendMsgChat(groupId, msg, listener, RoomMsgManager.SEND_MSG_GROUP_CHAT_TYPE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 图片消息 */
    public void sendImageMsg(String groupId, String fileData, SendMsgStateListener listener){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        TIMMessage msg = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        try{
            elem.setPath(fileData);
            elem.setLevel(TIMImageElem.TIM_IMAGE_FORMAT_JPG);
            msg.addElement(elem);
            RoomMsgManager.getInstance().sendMsgChat(groupId, msg, listener, RoomMsgManager.SEND_MSG_GROUP_CHAT_TYPE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 表情消息 */
    public void sendFaceMsg(String groupId,byte[] faceByte, int index, SendMsgStateListener listener){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        TIMMessage msg = new TIMMessage();
        TIMFaceElem elem = new TIMFaceElem();
        try{
            elem.setData(faceByte);
            elem.setIndex(index);
            msg.addElement(elem);
            RoomMsgManager.getInstance().sendMsgChat(groupId, msg, listener, RoomMsgManager.SEND_MSG_GROUP_CHAT_TYPE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 语音消息 */
    public void sendSoundMsg(String groupId, String soundFile, long duration, SendMsgStateListener listener){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        TIMMessage msg = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        try{
            //需要转换为Byte及获取时长
            elem.setData(null);
            elem.setDuration(duration);
            msg.addElement(elem);
            RoomMsgManager.getInstance().sendMsgChat(groupId, msg, listener, RoomMsgManager.SEND_MSG_GROUP_CHAT_TYPE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 地理位置消息 */
    public void sendLocationMsg(String groupId, String desc, double latitude, double longitude, SendMsgStateListener listener){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        TIMMessage msg = new TIMMessage();
        TIMLocationElem elem = new TIMLocationElem();
        try{
            elem.setLatitude(latitude);//设置经度
            elem.setLongitude(longitude);//设置纬度
            elem.setDesc(desc);
            msg.addElement(elem);
            RoomMsgManager.getInstance().sendMsgChat(groupId, msg, listener, RoomMsgManager.SEND_MSG_GROUP_CHAT_TYPE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 发送自定义消息 */
    public void sendCustomMsg(String groupId, byte[] value, String desc, SendMsgStateListener listener){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        try{
            elem.setDesc(desc);
            elem.setData(value);
            msg.addElement(elem);
            RoomMsgManager.getInstance().sendMsgChat(groupId, msg, listener, RoomMsgManager.SEND_MSG_GROUP_CHAT_TYPE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
