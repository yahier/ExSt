package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class AdUserItem2 implements Serializable {
    int isattention;
    long aduserid;
    String adnickname;
    String adimgurl;
    int clicktype;
    String adurl;
    String adid;
    String iconurl;
    String icontitle;

    public final static String icontitleGold = "1";
    public final static String icontitleSilver = "2";
    public final static String icontitleCopper = "3";


    public final static int typeClickTribe = 1;
    public final static int typeClickAd = 2;

    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

    public int getClicktype() {
        return clicktype;
    }

    public void setClicktype(int clicktype) {
        this.clicktype = clicktype;
    }

    public String getAdimgurl() {
        return adimgurl;
    }

    public void setAdimgurl(String adimgurl) {
        this.adimgurl = adimgurl;
    }

    public String getAdnickname() {
        return adnickname;
    }

    public void setAdnickname(String adnickname) {
        this.adnickname = adnickname;
    }

    public long getAduserid() {
        return aduserid;
    }

    public void setAduserid(long aduserid) {
        this.aduserid = aduserid;
    }

    public String getAdurl() {
        return adurl;
    }

    public void setAdurl(String adurl) {
        this.adurl = adurl;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }


    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getIcontitle() {
        return icontitle;
    }

    public void setIcontitle(String icontitle) {
        this.icontitle = icontitle;
    }
}
