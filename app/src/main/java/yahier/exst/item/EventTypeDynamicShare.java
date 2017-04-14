package yahier.exst.item;

import java.io.Serializable;

/**
 * 用于commonweb分享动态
 * Created by Administrator on 2017/2/28 0028.
 */

public class EventTypeDynamicShare implements Serializable {
    String url;
    boolean isShareToCircle;
    boolean isShareToQzone;
    boolean isShareTask;
    ShareItem shareItem;

    public EventTypeDynamicShare(String url, boolean isShareToCircle, boolean isShareToQzone, boolean isShareTask, ShareItem shareItem) {
        this.url = url;
        this.isShareToCircle = isShareToCircle;
        this.isShareToQzone = isShareToQzone;
        this.isShareTask = isShareTask;
        this.shareItem = shareItem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isShareToCircle() {
        return isShareToCircle;
    }

    public void setShareToCircle(boolean shareToCircle) {
        isShareToCircle = shareToCircle;
    }

    public boolean isShareToQzone() {
        return isShareToQzone;
    }

    public void setShareToQzone(boolean shareToQzone) {
        isShareToQzone = shareToQzone;
    }

    public boolean isShareTask() {
        return isShareTask;
    }

    public void setShareTask(boolean shareTask) {
        isShareTask = shareTask;
    }

    public ShareItem getShareItem() {
        return shareItem;
    }

    public void setShareItem(ShareItem shareItem) {
        this.shareItem = shareItem;
    }
}
