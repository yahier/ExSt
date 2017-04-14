package yahier.exst.item.ad;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 * 还在使用中
 */

public class RedPacketInfo implements Serializable {
    String rpid;
    String amount;//红包金额
    int status;
    String endtime;//红包多长时间被抢完（秒）
    int pickedcount;//红包已被抢数量
    String mypick;//我抢到的金额数
    List<UserItem> users;//抢红包人列表（用户id，昵称，头像）

    public final static int statusPickAvailable = 0;//可以抢红包
    public final static int statusPickOver = 1;//已经抢完了
    public final static int statusPickExpire = -1;//已经过期

    public String getRpid() {
        return rpid;
    }

    public void setRpid(String rpid) {
        this.rpid = rpid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getPickedcount() {
        return pickedcount;
    }

    public void setPickedcount(int pickedcount) {
        this.pickedcount = pickedcount;
    }

    public String getMypick() {
        return mypick;
    }

    public void setMypick(String mypick) {
        this.mypick = mypick;
    }

    public List<UserItem> getUsers() {
        return users;
    }

    public void setUsers(List<UserItem> users) {
        this.users = users;
    }
}
