package yahier.exst.item;

/**
 * Created by Administrator on 2016/9/1.
 * 当前只用做了官方账号的接口
 */

public class User {

    long userid;
    String username;
    String iconurl;
    String iconlarurl;
    String iconoriurl;


    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getIconoriurl() {
        return iconoriurl;
    }

    public void setIconoriurl(String iconoriurl) {
        this.iconoriurl = iconoriurl;
    }

    public String getIconlarurl() {
        return iconlarurl;
    }

    public void setIconlarurl(String iconlarurl) {
        this.iconlarurl = iconlarurl;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
