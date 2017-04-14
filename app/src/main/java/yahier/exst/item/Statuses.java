package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * 动态
 *
 * @author lenovo
 */
public class Statuses implements Serializable {
    private static final long serialVersionUID = -8071317898237923144L;
    public final static int type_short = 0;// 短动态
    public final static int type_long = 1;// 长动态
    public final static int type_public = 0;// 多人动态
    public final static int type_personal = 1;// 自己的动态
    public final static int requestCount = 15;// 请求的数目

    long createtime;// 创建时间
    long statusesid;// 动态id
    String content;
    String title;
    int statusestype;
    int isfavorited;
    public final static int isfavoritedYes = 1;
    public final static int isfavoritedNo = 0;
    int ispraised;

    int isforward;
    public final static int isforwardYes = 1;
    public final static int isforwardNo = 0;

    int istop;
    public final static int istopYes = 1;
    public final static int istopNo = 0;
    UserItem user;// 用户信息
    LinkStatuses links;// 链接内容
    int forwardcount;// 转发数
    int favorcount;// 收藏数
    int commentcount;// 评论数
    int praisecount;// 点赞数
    int rewardcount;
    List<StatusesComment> comments;// 六条评论
    List<StatusesPraise> praises;// 点赞明细
    List<StatusesReward> rewarduserlst;
    StatusesPic statusespic;// 动态配图
    int isattention;// 关注状态
    int isshield;// 屏蔽状态
    long publisheruserid;
    int ishot;
    public final static int ishot_yes = 1;
    public final static int ishot_no = 0;
    public final static int isattention_yes = 1;
    public final static int isattention_no = 0;
    public final static int isshield_yes = 1;
    public final static int isshield_no = 0;
    public final static int isbeattention_yes = 1;//关注了我
    public final static int isbeattention_no = 0;//没有关注我

    public final static int ispraisedYes = 1;
    public final static int ispraisedNo = 0;

    public final static int linkTypeCard = 1;
    public final static int linkTypeWish = 2;
    public final static int linkTypeStatuses = 3;
    public final static int linkTypeGoods = 4;
    public final static int linkTypeLive = 5;//直播
    public static final int linkTypeVideo = 6; //视频
    public static final int linkTypeNiceLink = 7;//精彩链接
    public static final int linkTypeAd = 8;//在商圈显示
    public static final int linkTypeHongbao = 9;//在动态中显示

    public long getStatusesid() {
        return statusesid;
    }

    public void setStatusesid(long statusesid) {
        this.statusesid = statusesid;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

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

    public int getStatusestype() {
        return statusestype;
    }

    public void setStatusestype(int statusestype) {
        this.statusestype = statusestype;
    }

    public int getIsfavorited() {
        return isfavorited;
    }

    public void setIsfavorited(int isfavorited) {
        this.isfavorited = isfavorited;
    }

    public int getIspraised() {
        return ispraised;
    }

    public void setIspraised(int ispraised) {
        this.ispraised = ispraised;
    }

    public int getIsforward() {
        return isforward;
    }

    public void setIsforward(int isforward) {
        this.isforward = isforward;
    }

    public int getIstop() {
        return istop;
    }

    public int isIstop() {
        return istop;
    }

    public void setIstop(int istop) {
        this.istop = istop;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

    public int getForwardcount() {
        return forwardcount;
    }

    public void setForwardcount(int forwardcount) {
        this.forwardcount = forwardcount;
    }

    public int getFavorcount() {
        return favorcount;
    }

    public void setFavorcount(int favorcount) {
        this.favorcount = favorcount;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public int getPraisecount() {
        return praisecount;
    }

    public void setPraisecount(int praisecount) {
        this.praisecount = praisecount;
    }

    public LinkStatuses getLinks() {
        return links;
    }

    public void setLinks(LinkStatuses links) {
        this.links = links;
    }

    public List<StatusesComment> getComments() {
        return comments;
    }

    public void setComments(List<StatusesComment> comments) {
        this.comments = comments;
    }

    public List<StatusesPraise> getPraises() {
        return praises;
    }

    public void setPraises(List<StatusesPraise> praises) {
        this.praises = praises;
    }

    public StatusesPic getStatusespic() {
        return statusespic;
    }

    public void setStatusespic(StatusesPic statusespic) {
        this.statusespic = statusespic;
    }

    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

    public int getIsshield() {
        return isshield;
    }

    public void setIsshield(int isshield) {
        this.isshield = isshield;
    }

    public int getRewardcount() {
        return rewardcount;
    }

    public void setRewardcount(int rewardcount) {
        this.rewardcount = rewardcount;
    }

    public long getPublisheruserid() {
        return publisheruserid;
    }

    public void setPublisheruserid(long publisheruserid) {
        this.publisheruserid = publisheruserid;
    }

    public List<StatusesReward> getRewarduserlst() {
        return rewarduserlst;
    }

    public void setRewarduserlst(List<StatusesReward> rewarduserlst) {
        this.rewarduserlst = rewarduserlst;
    }

    public int getIshot() {
        return ishot;
    }

    public void setIshot(int ishot) {
        this.ishot = ishot;
    }
}
