package yahier.exst.item.ad;

import java.io.Serializable;
import java.util.List;

/**
 * 广告订单实体类基类
 * Created by Administrator on 2016/9/26 0026.
 */

public class AdOrderBaseItem implements Serializable {

    /**
     * orderno : “1231231231”
     * orderstate : 1
     * payfee : 113123
     * paytype : 1
     * ordertime : 1231
     * msg : abc
     */
    public static final int WaitPay = 1000;//1000-待支付
    public static final int HASPAY = 1200;//1200-已支付
    public static final int PAYCANCEL = 2;//2-已取消
    public static final int PAYTIMEOUT = 3;//3-订单超时
    public static final int PAYVERIFY = 1100;//支付确认中

    public static final int TYPE_WECHATPAY = 1;//1-微信支付
    public static final int TYPE_ALIPAY = 2;//2-支付宝
    public static final int TYPE_OTHER = 3;//3-他人代付

    protected String orderno; //订单号
    protected int orderstate; //订单状态，1000-待支付，1100-支付待确认，1200-已支付，2-已取消
    protected float payfee; //支付金额
    protected int paytype; //支付类型 ，1-微信支付，2-支付宝，3-他人代付
    protected long ordertime; //下单时间
    protected long restpaytime; //剩余支付时间
    protected String msg; //提示消息
    protected List<AdGoodsItem> goods;//商品信息

    public List<AdGoodsItem> getGoods() {
        return goods;
    }

    public void setGoods(List<AdGoodsItem> goods) {
        this.goods = goods;
    }

    public long getRestpaytime() {
        return restpaytime;
    }

    public void setRestpaytime(long restpaytime) {
        this.restpaytime = restpaytime;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public int getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(int orderstate) {
        this.orderstate = orderstate;
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

    public long getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(long ordertime) {
        this.ordertime = ordertime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
