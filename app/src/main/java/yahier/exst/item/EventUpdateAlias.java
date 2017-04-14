package yahier.exst.item;

import java.io.Serializable;

/**
 * 修改备注通知
 * Created by Administrator on 2016/8/31 0031.
 */

public class EventUpdateAlias implements Serializable {
    String alias;//备注
    long userid;//用户id

    public EventUpdateAlias(String alias, long userid) {
        this.alias = alias;
        this.userid = userid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
