package yahier.exst.item.redpacket;

import java.io.Serializable;

/**
 * Created by yahier on 17/1/3.
 * 红包通用配置
 */

public class RedpacketCommon implements Serializable {
    int enable;//购物圈红包总开关,1-可用，0-不可用 暂时不用
    int paytimelimit;//红包支付时限//分钟
    int refundtimelimit;//红包退款时间间隔（单位分钟）
    int redpacketmsglen;//红包祝福语字符长度
    int paycallbackloadingtime; //支付回调loadding时间间隔 (单位 秒)

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getRefundtimelimit() {
        return refundtimelimit;
    }

    public void setRefundtimelimit(int refundtimelimit) {
        this.refundtimelimit = refundtimelimit;
    }

    public int getPaytimelimit() {
        return paytimelimit;
    }

    public void setPaytimelimit(int paytimelimit) {
        this.paytimelimit = paytimelimit;
    }

    public int getRedpacketmsglen() {
        return redpacketmsglen;
    }

    public void setRedpacketmsglen(int redpacketmsglen) {
        this.redpacketmsglen = redpacketmsglen;
    }

    public int getPaycallbackloadingtime() {
        return paycallbackloadingtime;
    }

    public void setPaycallbackloadingtime(int paycallbackloadingtime) {
        this.paycallbackloadingtime = paycallbackloadingtime;
    }
}
