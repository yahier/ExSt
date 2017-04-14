package yahier.exst.item.redpacket;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 * 红包领取列表的接口返回值
 */

public class RpPickersAll implements Serializable{
    int totalcount;
    long lastid;
    List<RpReceiveDetailItem> datalist;

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public List<RpReceiveDetailItem> getDatalist() {
        return datalist;
    }

    public void setDatalist(List<RpReceiveDetailItem> datalist) {
        this.datalist = datalist;
    }

    public long getLastid() {
        return lastid;
    }

    public void setLastid(long lastid) {
        this.lastid = lastid;
    }
}
