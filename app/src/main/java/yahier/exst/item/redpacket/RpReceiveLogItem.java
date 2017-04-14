package yahier.exst.item.redpacket;

import java.io.Serializable;

/**
 * 个人领取红包列表
 * Created by Alan Chueng on 2016/12/26 0026.
 */

public class RpReceiveLogItem implements Serializable {

    /**
     * redpacketid : test002
     * amount : 38.50
     * tradetime : 1482311655
     * rpusername : 1
     * rptype : 1
     */

    private String redpacketid;//红包id
    private String amount; //领取金额
    private long tradetime; //领取时间
    private String rpusername; //红包昵称
    private int rptype; //红包类型（0：普通，1：拼手气）

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

    public long getTradetime() {
        return tradetime;
    }

    public void setTradetime(long tradetime) {
        this.tradetime = tradetime;
    }

    public String getRpusername() {
        return rpusername;
    }

    public void setRpusername(String rpusername) {
        this.rpusername = rpusername;
    }

    public int getRptype() {
        return rptype;
    }

    public void setRptype(int rptype) {
        this.rptype = rptype;
    }
}
