package yahier.exst.model;

import java.io.Serializable;

/**
 * 充值参数
 *
 * @author ruilin
 */
public class Recharge implements Serializable {

    long orderpayno;
    float payfee;
    int paytype;
    String weixinjsonparameters;

    public long getOrderpayno() {
        return orderpayno;
    }

    public void setOrderpayno(long payno) {
        this.orderpayno = payno;
    }

    public float getPayfee() {
        return payfee;
    }

    public void setPayfee(float payfee) {
        this.payfee = payfee;
    }

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public String getWeixinjsonparameters() {
        return weixinjsonparameters;
    }

    public void setWeixinjsonparameters(String weixinjsonparameters) {
        this.weixinjsonparameters = weixinjsonparameters;
    }


}
