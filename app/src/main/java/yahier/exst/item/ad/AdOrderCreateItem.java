package yahier.exst.item.ad;

/**
 * 创建广告订单
 * Created by Administrator on 2016/9/26 0026.
 */

public class AdOrderCreateItem extends AdOrderBaseItem {

    private AdPayPreData orderprepayview; //预支付数据

    public AdPayPreData getOrderprepayview() {
        return orderprepayview;
    }

    public void setOrderprepayview(AdPayPreData orderprepayview) {
        this.orderprepayview = orderprepayview;
    }
}
