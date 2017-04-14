package yahier.exst.item.redpacket;

/**
 * Created by Administrator on 2016/12/26.
 * 红包详情
 */

public class RedpacketDetail {
    String redpacketid;
    float takenamount;//当前用户领取金额
    int status;//（0：待支付，1：待抢，2：抢完，3：超时退款）
    public final static int status_waitingPay = 0;
    public final static int status_pickabel = 1;
    public final static int status_pickOver = 2;
    public final static int status_expire = 3;
    int type;
    public final static int type_normal = 0;
    public final static int type_random = 1;
    double amount;//红包总金额
    double balance;//红包余额
    int totalqty;//红包份数
    int restqty;//剩余份数
    String message;//祝福语
    long userid;//红包主id
    String username;//红包主昵称
    String usericon;//红包主头像
    int ReceiveTimelong;//领取红包总时长(秒)
    String ReceiveTimelongStr;//领取红包总时长

    public String getRedpacketid() {
        return redpacketid;
    }

    public void setRedpacketid(String redpacketid) {
        this.redpacketid = redpacketid;
    }

    public float getTakenamount() {
        return takenamount;
    }

    public void setTakenamount(float takenamount) {
        this.takenamount = takenamount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public static int getStatus_waitingPay() {
        return status_waitingPay;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTotalqty() {
        return totalqty;
    }

    public void setTotalqty(int totalqty) {
        this.totalqty = totalqty;
    }

    public int getRestqty() {
        return restqty;
    }

    public void setRestqty(int restqty) {
        this.restqty = restqty;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public int getReceiveTimelong() {
        return ReceiveTimelong;
    }

    public void setReceiveTimelong(int receiveTimelong) {
        ReceiveTimelong = receiveTimelong;
    }

    public String getReceiveTimelongStr() {
        return ReceiveTimelongStr;
    }

    public void setReceiveTimelongStr(String receiveTimelongStr) {
        ReceiveTimelongStr = receiveTimelongStr;
    }
}
