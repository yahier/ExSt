package yahier.exst.item.redpacket;

import java.io.Serializable;

/**
 * 个人红包统计（发出与收到）
 * Created by Alan Chueng on 2016/12/26 0026.
 */

public class RpSummaryItem implements Serializable {


    /**
     * year : 2016
     * userid : 1002001
     * usericon :
     * username :
     * totalamount : 38.50
     * totalcount : 1
     * totalmaxcount : 1
     */

    private int year;
    private long userid;
    private String usericon;
    private String username;
    private String totalamount;
    private int totalcount;
    private int totalmaxcount;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public int getTotalmaxcount() {
        return totalmaxcount;
    }

    public void setTotalmaxcount(int totalmaxcount) {
        this.totalmaxcount = totalmaxcount;
    }
}
