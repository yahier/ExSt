package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by tnitf on 2016/3/19.
 */
public class ExchargeRate implements Serializable {

    private float yue2rmb;
    private float jindou2rmb;
    private float jindou2yue;
    private float lvdou2rmb;
    private float lvdou2yue;
    private float lvdou2jindou;

    public float getYue2rmb() {
        return yue2rmb;
    }

    public void setYue2rmb(float yue2rmb) {
        this.yue2rmb = yue2rmb;
    }

    public float getJindou2rmb() {
        return jindou2rmb;
    }

    public void setJindou2rmb(float jindou2rmb) {
        this.jindou2rmb = jindou2rmb;
    }

    public float getJindou2yue() {
        return jindou2yue;
    }

    public void setJindou2yue(float jindou2yue) {
        this.jindou2yue = jindou2yue;
    }

    public float getLvdou2rmb() {
        return lvdou2rmb;
    }

    public void setLvdou2rmb(float lvdou2rmb) {
        this.lvdou2rmb = lvdou2rmb;
    }

    public float getLvdou2yue() {
        return lvdou2yue;
    }

    public void setLvdou2yue(float lvdou2yue) {
        this.lvdou2yue = lvdou2yue;
    }

    public float getLvdou2jindou() {
        return lvdou2jindou;
    }

    public void setLvdou2jindou(float lvdou2jindou) {
        this.lvdou2jindou = lvdou2jindou;
    }
}
