package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/4/29.
 * 进入时的广告显示。
 */
public class EnterAd implements Serializable {
    String adimgurl;
    String adlinkurl;
    long duration;
    String adid;

    public String getAdimgurl() {
        return adimgurl;
    }

    public void setAdimgurl(String adimgurl) {
        this.adimgurl = adimgurl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAdlinkurl() {
        return adlinkurl;
    }

    public void setAdlinkurl(String adlinkurl) {
        this.adlinkurl = adlinkurl;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }
}
