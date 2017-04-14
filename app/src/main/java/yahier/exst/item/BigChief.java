package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/4/27.
 * 大酋长。目前在部落user字段下
 */
public class BigChief implements Serializable {
    long bigchiefuserid;
    int zlevel;
    String imgurl;


    public long getBigchiefuserid() {
        return bigchiefuserid;
    }

    public void setBigchiefuserid(long bigchiefuserid) {
        this.bigchiefuserid = bigchiefuserid;
    }

    public int getZlevel() {
        return zlevel;
    }

    public void setZlevel(int zlevel) {
        this.zlevel = zlevel;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
