package yahier.exst.item;

/**
 * Created by Administrator on 2016/9/1.
 * 列表使用
 */

public class IMSettingStatus {
    long businessid;
    int businesstype;
    int type;

    public final static int businesstypePrivate = 1;
    public final static int businesstypeGroup = 2;
    public final static int businesstypeDiscussion = 3;

    public final static int typeTop = 1;
    public final static int typeDistub = 2;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(int businesstype) {
        this.businesstype = businesstype;
    }

    public long getBusinessid() {
        return businessid;
    }

    public void setBusinessid(long businessid) {
        this.businessid = businessid;
    }
}
