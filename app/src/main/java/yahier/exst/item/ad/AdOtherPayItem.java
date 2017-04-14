package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告订单他人代付信息
 * Created by Administrator on 2016/9/27 0027.
 */

public class AdOtherPayItem implements Serializable {

    /**
     * otherpayno : 1231231321
     * otherpayusername : abc
     * otherpaytype : 1
     */

    private String message; //申请人留言
    private String otherpayno; //代付支付号
    private String paidusername; //代付支付人名称
    private int otherpaytype; //代付付款类型 1-微信支付，2-支付宝

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtherpayno() {
        return otherpayno;
    }

    public void setOtherpayno(String otherpayno) {
        this.otherpayno = otherpayno;
    }

    public String getPaidusername() {
        return paidusername;
    }

    public void setPaidusername(String paidusername) {
        this.paidusername = paidusername;
    }

    public int getOtherpaytype() {
        return otherpaytype;
    }

    public void setOtherpaytype(int otherpaytype) {
        this.otherpaytype = otherpaytype;
    }
}
