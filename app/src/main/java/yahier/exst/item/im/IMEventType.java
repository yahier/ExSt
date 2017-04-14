package yahier.exst.item.im;

import java.io.Serializable;

public class IMEventType implements Serializable {
    public IMEventType(int type) {
        this.type = type;
    }


    public final static int typeUpdateGroupInfo = 2;
    public final static int typeUpdateContactList = 3;//刷新好友列表，处理完申请消息之后调用
    public final static int typeRefreshIM = 4;//这
    public final static int typeRefreshAllUnreadCount = 5;//未读消息总数
    public final static int typeQuitDiscussion = 6;//退出讨论组
    public final static int typeUpdateDiscussionName = 7;//更新讨论组
    public final static int typeChangedContact = 8;
    public final static int typeIMOtherDevice = 9;
    public final static int typeIMViewedApply = 10;
    public final static int typeLoginOtherDevice = 11;//账号在其它设备登录

    public IMEventType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public IMEventType(int type, int count) {
        this.type = type;
        this.count = count;
    }

    int type;
    int count;
    String value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
