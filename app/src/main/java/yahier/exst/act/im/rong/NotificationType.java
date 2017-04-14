package yahier.exst.act.im.rong;

/**
 * Created by lenovo on 2016/3/17.
 */
public class NotificationType {
    String type;
    public final static String typeCreateDiscussion = "1";//创建讨论组
    public final static String typeOpenRedPackect = "2";//打开红包
    public final static String typeOpenCastBean = "3";//打开撒豆
    public final static String typeAddDis = "4";//加入讨论组
    public final static String typeInviteJoin = "5";//邀请XXX加入讨论组

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
