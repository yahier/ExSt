package yahier.exst.item.ad;

/**
 * Created by Administrator on 2016/9/24.
 */

public class BrandInfo {
    long aduserid;
    String adnickname;
    String adimgurl;
    int clicktype;
    public final static int typeClickTribe = 1;
    public final static int typeClickAd = 2;

    public final static int typeisdeleteImgYes = 1;
    public final static int typeisdeleteImgNo = 0;

    public final static String typeUploadImgAd ="AdSys";//投放广告
    public final static String typeUploadImgBrand ="AdUser";//品牌设置个人头像


    public long getAduserid() {
        return aduserid;
    }

    public void setAduserid(long aduserid) {
        this.aduserid = aduserid;
    }

    public String getAdnickname() {
        return adnickname;
    }

    public void setAdnickname(String adnickname) {
        this.adnickname = adnickname;
    }

    public String getAdimgurl() {
        return adimgurl;
    }

    public void setAdimgurl(String adimgurl) {
        this.adimgurl = adimgurl;
    }

    public int getClicktype() {
        return clicktype;
    }

    public void setClicktype(int clicktype) {
        this.clicktype = clicktype;
    }
}
