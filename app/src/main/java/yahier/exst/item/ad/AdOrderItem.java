package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告订单详情
 * Created by Administrator on 2016/9/27 0027.
 */

public class AdOrderItem extends AdOrderBaseItem implements Serializable {

    private long paidtime; //订购时间
    private AdOrderinvoiceItem orderinvoice; //发票信息
    private AdUserItem user; //用户信息
    private AdOtherPayItem otherpayinfo;  //他人代付信息

    public long getPaidtime() {
        return paidtime;
    }

    public void setPaidtime(long paidtime) {
        this.paidtime = paidtime;
    }

    public AdOrderinvoiceItem getOrderinvoice() {
        return orderinvoice;
    }

    public void setOrderinvoice(AdOrderinvoiceItem orderinvoice) {
        this.orderinvoice = orderinvoice;
    }

    public AdUserItem getUser() {
        return user;
    }

    public void setUser(AdUserItem user) {
        this.user = user;
    }

    public AdOtherPayItem getOtherpayinfo() {
        return otherpayinfo;
    }

    public void setOtherpayinfo(AdOtherPayItem otherpayinfo) {
        this.otherpayinfo = otherpayinfo;
    }
}
