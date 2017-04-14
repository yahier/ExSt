package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by yahier on 17/1/23.
 */

public class QRCodeInfo implements Serializable {
    String qrurl;
    UserItem userinfo;

    public String getQrurl() {
        return qrurl;
    }

    public void setQrurl(String qrurl) {
        this.qrurl = qrurl;
    }

    public UserItem getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserItem userinfo) {
        this.userinfo = userinfo;
    }
}
