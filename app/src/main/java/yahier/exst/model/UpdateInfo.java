package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by tnitf on 2016/3/21.
 */
public class UpdateInfo implements Serializable {

    private String versionname;
    private int versioncode;
    private String title;
    private String updatecontent;
    private String downloadurl;
    private int forceversioncode;
    private long createtime;
    private int showtips;//是否弹窗提示（1-弹窗，0-不弹窗）

    public static final int showDialog = 1;//1-弹窗
    public static final int notShowDialog = 0;//0-不弹窗

    public int getShowtips() {
        return showtips;
    }

    public void setShowtips(int showtips) {
        this.showtips = showtips;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatecontent() {
        return updatecontent;
    }

    public void setUpdatecontent(String updatecontent) {
        this.updatecontent = updatecontent;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public int getForceversioncode() {
        return forceversioncode;
    }

    public void setForceversioncode(int forceversioncode) {
        this.forceversioncode = forceversioncode;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
