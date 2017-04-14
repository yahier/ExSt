package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告订单预支付信息
 * Created by Administrator on 2016/9/26 0026.
 */

public class AdPayPreData implements Serializable {

    /**
     * orderno : AD9510542967648
     * orderpayno : ADP195420495481420
     * payfee : 0.01
     * prepaystate : 1
     * weixinjsonparameters : {"appid":"wx2933b730046aeea9","noncestr":"90265022294b4f54a6124f9758ec06a8","package":"Sign=WXPay","partnerid":"1296844001","prepayid":"wx2016092621102024be19ea4f0532236208","sign":"CAAE1FCB302990F073458C9F50FF7C45","timestamp":"1474895421"}
     */

    private String orderno; //订单号
    private String orderpayno;//支付单号
    private float payfee;//支付金额
    private int prepaystate;//预支付状态0-失败，1-成功
    private String weixinjsonparameters;//采用微信支付，并且成功时返回
    private String msg; //信息

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getOrderpayno() {
        return orderpayno;
    }

    public void setOrderpayno(String orderpayno) {
        this.orderpayno = orderpayno;
    }

    public float getPayfee() {
        return payfee;
    }

    public void setPayfee(float payfee) {
        this.payfee = payfee;
    }

    public int getPrepaystate() {
        return prepaystate;
    }

    public void setPrepaystate(int prepaystate) {
        this.prepaystate = prepaystate;
    }

    public String getWeixinjsonparameters() {
        return weixinjsonparameters;
    }

    public void setWeixinjsonparameters(String weixinjsonparameters) {
        this.weixinjsonparameters = weixinjsonparameters;
    }
}
