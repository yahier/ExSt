package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/4/27.
 */
public class TribeUserItem extends UserItem implements Serializable {
    BigChief bigchiefinfo;
    BigChief cbigchiefinfo;
    long bigchiefuserid;

    public BigChief getBigchiefinfo() {
        return bigchiefinfo;
    }

    public void setBigchiefinfo(BigChief bigchiefinfo) {
        this.bigchiefinfo = bigchiefinfo;
    }

    @Override
    public long getBigchiefuserid() {
        return bigchiefuserid;
    }

    @Override
    public void setBigchiefuserid(long bigchiefuserid) {
        this.bigchiefuserid = bigchiefuserid;
    }

    public BigChief getCbigchiefinfo() {
        return cbigchiefinfo;
    }

    public void setCbigchiefinfo(BigChief cbigchiefinfo) {
        this.cbigchiefinfo = cbigchiefinfo;
    }
}
