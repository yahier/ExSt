package yahier.exst.item.im;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/3/18.
 */
public class PickResultBig implements Serializable {
    long hongbaoid;
    int status;
    RedPacketPickResult pickedresult;

    public long getHongbaoid() {
        return hongbaoid;
    }

    public void setHongbaoid(long hongbaoid) {
        this.hongbaoid = hongbaoid;
    }

    public RedPacketPickResult getPickedresult() {
        return pickedresult;
    }

    public void setPickedresult(RedPacketPickResult pickedresult) {
        this.pickedresult = pickedresult;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
