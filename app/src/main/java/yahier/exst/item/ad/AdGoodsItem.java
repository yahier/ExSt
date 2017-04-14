package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告服务商品信息
 * Created by Administrator on 2016/9/26 0026.
 */

public class AdGoodsItem implements Serializable {


    /**
     * goodsname : abcv
     * qty : 1
     * price : 12
     * totalamount : 12
     * servicetime : 12
     * deadline : 2017-08
     */

    private String goodsname;//商品名称
    private int qty;//数量
    private String price;//单价
    private String totalamount;//总价
    private String servicetime;//服务时间（单位月）
    private String deadline;//有效期（年-月）
    private String displaytitle;//标题描述，用于开通显示

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getServicetime() {
        return servicetime;
    }

    public void setServicetime(String servicetime) {
        this.servicetime = servicetime;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDisplaytitle() {
        return displaytitle;
    }

    public void setDisplaytitle(String displaytitle) {
        this.displaytitle = displaytitle;
    }
}
