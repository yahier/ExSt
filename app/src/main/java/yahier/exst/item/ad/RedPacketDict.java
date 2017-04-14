package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */

public class RedPacketDict implements Serializable{

    int receivetimes;
    int freetimes;

    public int getReceivetimes() {
        return receivetimes;
    }

    public void setReceivetimes(int receivetimes) {
        this.receivetimes = receivetimes;
    }

    public int getFreetimes() {
        return freetimes;
    }

    public void setFreetimes(int freetimes) {
        this.freetimes = freetimes;
    }
}
