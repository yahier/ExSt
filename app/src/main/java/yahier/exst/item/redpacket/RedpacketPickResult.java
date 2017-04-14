package yahier.exst.item.redpacket;

import com.stbl.stbl.item.ad.PreOpenHongbaoCheck;

import java.io.Serializable;

/**
 * Created by yahier on 17/1/12.
 */

public class RedpacketPickResult implements Serializable{
    String redpacketid;
    float amount;
    String message;
    String userIcon;
    String username;
    PreOpenHongbaoCheck checkresult;

    public PreOpenHongbaoCheck getCheckresult() {
        return checkresult;
    }

    public void setCheckresult(PreOpenHongbaoCheck checkresult) {
        this.checkresult = checkresult;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getRedpacketid() {
        return redpacketid;
    }

    public void setRedpacketid(String redpacketid) {
        this.redpacketid = redpacketid;
    }
}
