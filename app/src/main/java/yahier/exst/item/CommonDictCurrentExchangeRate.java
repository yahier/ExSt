package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/7/15.
 */
public class CommonDictCurrentExchangeRate implements Serializable{
    String yue2rmb;
    String jindou2rmb;
    String jindou2yue;
    String lvdou2rmb;
    String lvdou2yue;
    String lvdou2jindou;


    public String getJindou2rmb() {
        return jindou2rmb;
    }

    public void setJindou2rmb(String jindou2rmb) {
        this.jindou2rmb = jindou2rmb;
    }

    public String getJindou2yue() {
        return jindou2yue;
    }

    public void setJindou2yue(String jindou2yue) {
        this.jindou2yue = jindou2yue;
    }

    public String getLvdou2jindou() {
        return lvdou2jindou;
    }

    public void setLvdou2jindou(String lvdou2jindou) {
        this.lvdou2jindou = lvdou2jindou;
    }

    public String getLvdou2rmb() {
        return lvdou2rmb;
    }

    public void setLvdou2rmb(String lvdou2rmb) {
        this.lvdou2rmb = lvdou2rmb;
    }

    public String getLvdou2yue() {
        return lvdou2yue;
    }

    public void setLvdou2yue(String lvdou2yue) {
        this.lvdou2yue = lvdou2yue;
    }

    public String getYue2rmb() {
        return yue2rmb;
    }

    public void setYue2rmb(String yue2rmb) {
        this.yue2rmb = yue2rmb;
    }
}
