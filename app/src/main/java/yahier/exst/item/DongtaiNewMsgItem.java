package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态新消息推送实体
 * Created by Administrator on 2016/6/16 0016.
 */
public class DongtaiNewMsgItem implements Serializable {

    private int pushmodeltype;//内部类型：1 通知
    private long userid;//用户ID
    private int newnoticecount;//最新数量

    public int getPushmodeltype() {
        return pushmodeltype;
    }

    public void setPushmodeltype(int pushmodeltype) {
        this.pushmodeltype = pushmodeltype;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public int getNewnoticecount() {
        return newnoticecount;
    }

    public void setNewnoticecount(int newnoticecount) {
        this.newnoticecount = newnoticecount;
    }

    @Override
    public String toString() {
        return "DongtaiNewMsgItem{" +
                "pushmodeltype=" + pushmodeltype +
                ", userid=" + userid +
                ", newnoticecount=" + newnoticecount +
                '}';
    }
}
