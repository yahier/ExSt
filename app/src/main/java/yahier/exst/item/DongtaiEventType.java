package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态新消息看完后清除红点
 * Created by Administrator on 2016/6/16 0016.
 */
public class DongtaiEventType implements Serializable {

    private int type;//推送类型
    private int messageId;//新消息id

    public static final int NEW_MESSAGE = 0x0001;//新消息

    public DongtaiEventType(int type) {
        this.type = type;
    }

    public DongtaiEventType(int type, int messageId) {
        this.type = type;
        this.messageId = messageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
