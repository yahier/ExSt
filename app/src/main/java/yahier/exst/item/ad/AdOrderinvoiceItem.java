package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告订单发票信息
 * Created by Administrator on 2016/9/27 0027.
 */

public class AdOrderinvoiceItem implements Serializable {
    /**
     * orderinvoicetitle : safd
     * orderinvoicecontent : abc
     * contactname : daf
     * contactphone : 1231
     * contactaddr : fasdfas
     */

    private String orderinvoicetitle; //发票抬头
    private String orderinvoicecontent; //发票内容
    private String contactname; //发票联系人
    private String contactphone; //发票联系人电话
    private String contactaddr; //发票联系人地址

    public String getOrderinvoicetitle() {
        return orderinvoicetitle;
    }

    public void setOrderinvoicetitle(String orderinvoicetitle) {
        this.orderinvoicetitle = orderinvoicetitle;
    }

    public String getOrderinvoicecontent() {
        return orderinvoicecontent;
    }

    public void setOrderinvoicecontent(String orderinvoicecontent) {
        this.orderinvoicecontent = orderinvoicecontent;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public String getContactphone() {
        return contactphone;
    }

    public void setContactphone(String contactphone) {
        this.contactphone = contactphone;
    }

    public String getContactaddr() {
        return contactaddr;
    }

    public void setContactaddr(String contactaddr) {
        this.contactaddr = contactaddr;
    }
}
