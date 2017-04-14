package yahier.exst.model;

import java.io.Serializable;

/**
 * 积分商城商品item
 * Created by Administrator on 2016/7/25 0025.
 */
public class MallIntegralProduct implements Serializable {
    long goodsid;//商品id
    String goodsname;//商品名称
    long shopid; //卖家id
    String shopname; //卖家名称
    String imgurl;//商品图片
    String price;//兑换价格
    long createtime;//商品发布时间
    long clientid;//客户端编码

    public long getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(long goodsid) {
        this.goodsid = goodsid;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public long getShopid() {
        return shopid;
    }

    public void setShopid(long shopid) {
        this.shopid = shopid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getClientid() {
        return clientid;
    }

    public void setClientid(long clientid) {
        this.clientid = clientid;
    }
}
