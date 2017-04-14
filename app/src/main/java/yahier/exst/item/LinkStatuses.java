package yahier.exst.item;

import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.Goods;

import java.io.Serializable;

/**
 * 动态 链接
 *
 * @author lenovo
 */
public class LinkStatuses implements Serializable {

    // 四种链接类型，分别是名片 愿望 动态和产品
    public final static int linkTypeNone = 0;
    public final static int linkTypeCrad = 1;
    public final static int linkTypeWish = 2;
    public final static int linkTypeStateses = 3;
    public final static int linkTypeProduct = 4;
    public final static int linkTypeLive = 5;
    public static final int linkTypeVideo = 6;
    public static final int linkTypeNiceLink = 7;
    public static final int linkTypeAd = 8;//在商圈显示
    public static final int linkTypeHongbao = 9;//在动态中显示
    private static final long serialVersionUID = -7431365237498287359L;
    int linktype;
    String linkid;
    Statuses statusesinfo;
    UserItem userinfo;
    Goods productinfo;
    String linktitle;// 现在用在了直播
    String linkurl;//直播发动态，用于解析房间是否需要密码等 格式：？isneedpwd = 1 & xxx=x
    String username; //只有直播用到
    String userhead; //只有直播用到
    LinkInStatuses userlinksinfo;
    Ad adinfo;

    //视频新加ideo
    String linkex2;//用作视频标志  "video"
    public final static String linkex2VideoTag = "video";
    String linkex;//用作视频封面图

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLinktype() {
        return linktype;
    }

    public void setLinktype(int linktype) {
        this.linktype = linktype;
    }

    public String getLinkid() {
        return linkid;
    }

    public void setLinkid(String linkid) {
        this.linkid = linkid;
    }

    public Statuses getStatusesinfo() {
        return statusesinfo;
    }

    public void setStatusesinfo(Statuses statusesinfo) {
        this.statusesinfo = statusesinfo;
    }

    public UserItem getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserItem userinfo) {
        this.userinfo = userinfo;
    }

    public Goods getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(Goods productinfo) {
        this.productinfo = productinfo;
    }

    public String getLinktitle() {
        return linktitle;
    }

    public void setLinktitle(String linktitle) {
        this.linktitle = linktitle;
    }

    public String getLinkex2() {
        return linkex2;
    }

    public void setLinkex2(String linkex2) {
        this.linkex2 = linkex2;
    }

    public String getLinkex() {
        return linkex;
    }

    public void setLinkex(String linkex) {
        this.linkex = linkex;
    }


    public LinkInStatuses getUserlinksinfo() {
        return userlinksinfo;
    }

    public void setUserlinksinfo(LinkInStatuses userlinksinfo) {
        this.userlinksinfo = userlinksinfo;
    }

    public Ad getAdinfo() {
        return adinfo;
    }

    public void setAdinfo(Ad adinfo) {
        this.adinfo = adinfo;
    }
}
