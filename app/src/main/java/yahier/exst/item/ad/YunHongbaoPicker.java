package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 * 云账户抢红包人员列表
 */

public class YunHongbaoPicker implements Serializable {
    String UserId;
    String Nickname;
    String Avatar;
    String Money;
    String DateTime;
    boolean IsMaxAmount;


    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
