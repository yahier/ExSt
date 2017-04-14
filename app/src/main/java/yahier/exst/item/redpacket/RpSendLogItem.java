package yahier.exst.item.redpacket;

import java.io.Serializable;

/**
 * 个人发出红包列表
 * Created by Alan Chueng on 2016/12/26 0026.
 */

public class RpSendLogItem implements Serializable {


    /**
     * redpacketid : RP16122316001231901544
     * amount : 300.00
     * sendtime : 2016-12
     * redpackettype : 1
     * redpacketstatus : 2
     * redpacketsize : 100
     * receivedsize : 100
     */

    private String redpacketid; //红包id
    private String amount; //金额
    private long sendtime; //发出时间
    private int redpackettype;//红包类型 0-普通红包，1-手气红包
    private int redpacketstatus; //红包状态 1-待抢，2-已抢完，3-过期退款
    private int redpacketsize;//红包总数量
    private int receivedsize;//红包已领数量

    public String getRedpacketid() {
        return redpacketid;
    }

    public void setRedpacketid(String redpacketid) {
        this.redpacketid = redpacketid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getSendtime() {
        return sendtime;
    }

    public void setSendtime(long sendtime) {
        this.sendtime = sendtime;
    }

    public int getRedpackettype() {
        return redpackettype;
    }

    public void setRedpackettype(int redpackettype) {
        this.redpackettype = redpackettype;
    }

    public int getRedpacketstatus() {
        return redpacketstatus;
    }

    public void setRedpacketstatus(int redpacketstatus) {
        this.redpacketstatus = redpacketstatus;
    }

    public int getRedpacketsize() {
        return redpacketsize;
    }

    public void setRedpacketsize(int redpacketsize) {
        this.redpacketsize = redpacketsize;
    }

    public int getReceivedsize() {
        return receivedsize;
    }

    public void setReceivedsize(int receivedsize) {
        this.receivedsize = receivedsize;
    }
}
