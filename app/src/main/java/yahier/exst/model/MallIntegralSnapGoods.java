package yahier.exst.model;

import java.io.Serializable;

/**
 * 兑换商品快照实体
 * Created by Administrator on 2016/8/3 0003.
 */
public class MallIntegralSnapGoods implements Serializable {

    private long goodsid; //商品Id
    private String goodsname;//商品名称
    private long skuid; //商品类型
    private String skuname;//商品名称
    private int qty;//购买数量
    private long price;//单价
    private long shopid;//商铺ID
    private String shopname;//店铺名称
    private String fimgurl;//商品主图片
    private String goodsinfourl;//商品信息静态html地址
    private long createtime; //快照时间

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

    public long getSkuid() {
        return skuid;
    }

    public void setSkuid(long skuid) {
        this.skuid = skuid;
    }

    public String getSkuname() {
        return skuname;
    }

    public void setSkuname(String skuname) {
        this.skuname = skuname;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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

    public String getFimgurl() {
        return fimgurl;
    }

    public void setFimgurl(String fimgurl) {
        this.fimgurl = fimgurl;
    }

    public String getGoodsinfourl() {
        return goodsinfourl;
    }

    public void setGoodsinfourl(String goodsinfourl) {
        this.goodsinfourl = goodsinfourl;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
