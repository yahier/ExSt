package yahier.exst.item;

/**
 * Created by Administrator on 2016/9/1.
 * 消息设置。置顶和免打扰
 */


public class IMSetting {
    long businessid;
    int istop;
    int ispush;//暂时没有使用

    public final static int topTypePrivate = 1;
    public final static int topTypeGroup = 2;
    public final static int topTypeDiscussion = 3;

    public final static int topOperateAdd = 1;
    public final static int topOperateDelete = -1;

    public final static int updateTop = 1;
    public final static int updateNotDistub = 2;//免打扰设置。暂时不需要

    public final static int topYes = 1;
    public final static int topNo = 0;//

    public final static int queryTypeTop = 1;
    public final static int queryTypeDistub = 2;


    public long getBusinessid() {
        return businessid;
    }

    public void setBusinessid(long businessid) {
        this.businessid = businessid;
    }

    public int getIstop() {
        return istop;
    }

    public void setIstop(int istop) {
        this.istop = istop;
    }

    public int getIspush() {
        return ispush;
    }

    public void setIspush(int ispush) {
        this.ispush = ispush;
    }
}
