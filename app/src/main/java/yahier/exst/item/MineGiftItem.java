package yahier.exst.item;

import java.io.Serializable;

public class MineGiftItem implements Serializable {
    int rewardlogid;
    UserItem objuser;
    Gift giftinfo;
    long createtime;

    public final static int selecttype_get = 0;
    public final static int selecttype_sent = 1;

    public int getRewardlogid() {
        return rewardlogid;
    }

    public void setRewardlogid(int rewardlogid) {
        this.rewardlogid = rewardlogid;
    }

    public UserItem getObjuser() {
        return objuser;
    }

    public void setObjuser(UserItem objuser) {
        this.objuser = objuser;
    }

    public Gift getGiftinfo() {
        return giftinfo;
    }

    public void setGiftinfo(Gift giftinfo) {
        this.giftinfo = giftinfo;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
