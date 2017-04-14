package yahier.exst.model;

import java.io.Serializable;
import java.util.List;

/**
 * 积分商城首页实体类
 * Created by Administrator on 2016/7/25 0025.
 */
public class MallIntegralAll implements Serializable {

    List<Banner> bannerview;//banner
    int stticketamount;//师徒票余额
    List<MallIntegralProduct> productview;//商品信息

    public List<MallIntegralProduct> getProductview() {
        return productview;
    }

    public void setProductview(List<MallIntegralProduct> productview) {
        this.productview = productview;
    }

    public int getStticketamount() {
        return stticketamount;
    }

    public void setStticketamount(int stticketamount) {
        this.stticketamount = stticketamount;
    }

    public List<Banner> getBannerview() {
        return bannerview;
    }

    public void setBannerview(List<Banner> bannerview) {
        this.bannerview = bannerview;
    }
}
