package yahier.exst.item.redpacket;

import java.io.Serializable;

/**
 * 红包的领取列表实体类
 * Created by Alan Chueng on 2016/12/27 0027.
 */

public class RpReceiveDetailItem implements Serializable {

    /**
     * ismax : 0
     * redpacketid : test003
     * sortid : 2
     * amount : 7.53
     * userid : 1002004
     * username :
     * usericon :
     * tradetime : 1482315496
     */

    private int ismax; //是否手气最佳(0：否，1：是)
    public final static int ismaxYes = 1;
    public final static int ismaxNo = 0;

    private String redpacketid; //红包id
    private int sortid; //排序id
    private String amount; //领取金额
    private long userid; //用户id
    private String username; //用户昵称
    private String usericon; //用户头像
    private int tradetime; //领取时间

    public int getIsmax() {
        return ismax;
    }

    public void setIsmax(int ismax) {
        this.ismax = ismax;
    }

    public String getRedpacketid() {
        return redpacketid;
    }

    public void setRedpacketid(String redpacketid) {
        this.redpacketid = redpacketid;
    }

    public int getSortid() {
        return sortid;
    }

    public void setSortid(int sortid) {
        this.sortid = sortid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public int getTradetime() {
        return tradetime;
    }

    public void setTradetime(int tradetime) {
        this.tradetime = tradetime;
    }
}
