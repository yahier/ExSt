package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/3/13.
 * 保存注册时的信息
 */
public class RegisterInfo extends UserItem implements Serializable {
    String phonePrex;

    public void setPhonePrex(String phonePrex) {
        this.phonePrex = phonePrex;
    }

    public String getPhonePrex() {
        return phonePrex;
    }
}
