package yahier.exst.ui.DirectScreen.homeNotify.callback;

import com.stbl.stbl.api.imChatPush.ImChatPushServer;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.util.ToastUtil;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFileElem;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMMessageUpdateListener;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMSNSSystemElem;
import com.tencent.TIMSNSSystemType;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import java.util.List;

/**
 * Created by meteorshower on 16/4/10.
 * <p/>
 * 新消息监听
 */
public class NewMsgNofityListener implements TIMMessageListener, TIMMessageUpdateListener {

    private Logger logger = new Logger(this.getClass().getSimpleName());
    private ImChatPushServer imChatPushServer = null;

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        logger.e(" ----------------- NewMsgNofityListener : onNewMessages ---------------------- ");
        if (list == null || list.size() == 0)
            return false;

        for (TIMMessage msg : list) {
            analysisMsg(msg);
        }
        return false;
    }

    @Override
    public boolean onMessagesUpdate(List<TIMMessage> list) {
        logger.e(" ----------------- NewMsgNofityListener : onMessagesUpdate ---------------------- ");
//        if (list == null || list.size() == 0)
//            return false;
//
//        for (TIMMessage msg : list) {
//            analysisMsg(msg);
//        }
        return false;
    }

    /** 解析TIMMessage */
    private void analysisMsg(TIMMessage msg) {
        logger.e("isRead: " + msg.isRead() + //消息是否已读
                " isSelf: " + msg.isSelf() +   //消息是否为自己发出
                " timestamp: " + msg.timestamp() +  //消息时间戳(服务器时间，单位秒)
                " sender: " + msg.getSender());  //消息发送方id

        if(msg.isRead())
            return;

        //遍消息的元素列表
        for (int i = 0; i < msg.getElementCount(); ++i) {
            TIMElem elem = msg.getElement(i);

            //获取当前元素的类型
            TIMElemType elemType = elem.getType();
            logger.e("elem type: " + elemType.name());
            switch(elemType){
                case SNSTips://系统消息
                    analysisSNSTipsMsg(elem, msg.isSelf());
                    break;
                case GroupSystem://群组系统消息
                    analysisGroupSystemMsg((TIMGroupSystemElem) elem);
                    break;
                case Text://文本消息
                    analysisTextMsg((TIMTextElem) elem);
                    break;
                case Image://图片消息
                    analysisImageMsg((TIMImageElem) elem);
                    break;
                case File://文件消息
                    analysisFileMsg((TIMFileElem) elem);
                    break;
                case Sound://声音消息
                    analysisSoundMsg((TIMSoundElem) elem);
                    break;
                case Face://表情消息
                    break;
                case Custom://自定义消息
                    break;
                case GroupTips://
                    break;
                case ProfileTips://
                    break;
                case Invalid://
                    break;
                case Location://
                    break;
                case Video://视频消息
                    break;
            }
        }

        msg.getConversation().setReadMessage(msg);

    }

    /** 系统消息 */
    private void analysisSNSTipsMsg(TIMElem elem, boolean isSelf){
        TIMSNSSystemElem tipsElem = (TIMSNSSystemElem )elem;
        for(TIMSNSChangeInfo tmp :tipsElem.getChangeInfoList()){
            logger.e("add friend req:"+ isSelf + ":"+ tipsElem.getSubType() + "id:" + tmp.getIdentifier() + ":source:" + tmp.getSource());
            //删除的消息通知不需要展现
            //为了多终端同步的问题，自己发送的请求也会自己收到通知，这里简单的过滤
            if(tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND_REQ
                    //|| tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_DEL_FRIEND
                    || (tipsElem.getSubType() == TIMSNSSystemType.TIM_SNS_SYSTEM_ADD_FRIEND_REQ && isSelf)) {
                continue;
            }
            logger.e("snsTips : "+ tipsElem.getSubType() + "id:" + tmp.getIdentifier() + ":source:" + tmp.getSource());
        }
    }

    /** 群组系统消息 */
    private void analysisGroupSystemMsg(TIMGroupSystemElem elem){
        logger.e("group system: "+ elem.getSubtype() + "id:" + elem.getOpUser() + ":source:" + elem.getOpReason());
    }

    /** 文本消息 */
    private void analysisTextMsg(TIMTextElem elem){
        //文本元素, 获取文本内容
        logger.e("msg: " + elem.getText());
//        ToastUtil.showToast("接收到文本消息："+elem.getText());
        if (imChatPushServer == null)
            imChatPushServer = new ImChatPushServer();
        imChatPushServer.initChatPushData(elem.getText());
    }

    /** 图片消息 */
    private void analysisImageMsg(TIMImageElem elem) {
        for (TIMImage image : elem.getImageList()) {
            //获取图片类型, 大小, 宽高
            logger.e("image type: " + image.getType() +
                    " image size " + image.getSize() +
                    " image height " + image.getHeight() +
                    " image width " + image.getWidth());
            image.getImage(new TIMValueCallBack<byte[]>() {
                @Override
                public void onError(int code, String desc) {//获取图片失败
                    //错误码code和错误描述desc，可用于定位请求失败原因
                    //错误码code含义请参见错误码表
                    logger.e("getImage failed. code: " + code + " errmsg: " + desc);
                }

                @Override
                public void onSuccess(byte[] data) {//成功，参数为图片数据
                    //doSomething
                    logger.e("getImage success. data size: " + data.length);
                }
            });
        }
    }

    /** 文件消息 */
    private void analysisFileMsg(TIMFileElem elem){
        logger.e("file name: " + elem.getFileName()); //获取文件名
        //下载文件
        elem.getFile(new TIMValueCallBack<byte[]>() {
            @Override
            public void onError(int code, String desc) {//获取文件失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                logger.e("getFile failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(byte[] data) {//成功，参数为文件byte[]
                //doSomething
                logger.e("getFile success. data size: " + data.length);
            }
        });
    }

    /** 声音消息 */
    private void analysisSoundMsg(TIMSoundElem elem){
        //下载语音
        elem.getSound(new TIMValueCallBack<byte[]>() {
            @Override
            public void onError(int code, String desc) {//获取声音失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                logger.e("getSound failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(byte[] data) {//成功，参数为声音byte[]
                //doSomething
                logger.e("getSound success. data size: " + data.length);
            }
        });
    }

}
