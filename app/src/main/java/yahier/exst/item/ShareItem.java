package yahier.exst.item;

import com.umeng.socialize.media.UMImage;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/3/21.
 */
public class ShareItem implements Serializable {
    public final static int sharedMiRegister = 10000;
    public final static int sharedMiApp = 10010;
    public final static int sharedMiGoods = 10020;
    public final static int sharedMiStatuses = 10030;
    public final static int sharedMiTribe = 10040;


    public String title;
    public String content;
    public String imgUrl;
    public String link;
    public int defaultIcon;

    public String nickname; //用于更多分享 - 短信

    public int getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(int defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    public UMImage umImage;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String toString() {
        return "title:" + title + "  content:" + content;
    }
}
