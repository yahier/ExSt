package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/7/28.
 */
public class LinkInStatuses implements Serializable {
    long linksid;
    String title;
    String linksurl;
    String imgurl;

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public long getLinksid() {
        return linksid;
    }

    public void setLinksid(long linksid) {
        this.linksid = linksid;
    }

    public String getLinksurl() {
        return linksurl;
    }

    public void setLinksurl(String linksurl) {
        this.linksurl = linksurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
