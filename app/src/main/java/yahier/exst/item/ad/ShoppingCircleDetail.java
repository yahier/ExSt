package yahier.exst.item.ad;

import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 * 商圈详细
 */

public class ShoppingCircleDetail implements Serializable {
    int id; //用于分页
    String content;
    long createtime;
    RPImgInfo imginfo2;
    //此字段解析失败
    RedPacketInfo rpinfo;
    String squareid;
    long userid;
    AdUserItem2 userinfo;
    // List<StatusesComment> commentinfo;
    LinkStatuses linkinfo;// 链接内容


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSquareid() {
        return squareid;
    }

    public void setSquareid(String squareid) {
        this.squareid = squareid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkStatuses getLinkinfo() {
        return linkinfo;
    }

    public void setLinkinfo(LinkStatuses linkinfo) {
        this.linkinfo = linkinfo;
    }


    //    public List<StatusesComment> getCommentinfo() {
//        return commentinfo;
//    }
//
//    public void setCommentinfo(List<StatusesComment> commentinfo) {
//        this.commentinfo = commentinfo;
//    }

    public RedPacketInfo getRpinfo() {
        return rpinfo;
    }

    public void setRpinfo(RedPacketInfo rpinfo) {
        this.rpinfo = rpinfo;
    }

    public RPImgInfo getImginfo2() {
        return imginfo2;
    }

    public void setImginfo2(RPImgInfo imginfo2) {
        this.imginfo2 = imginfo2;
    }

    public AdUserItem2 getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(AdUserItem2 userinfo) {
        this.userinfo = userinfo;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
