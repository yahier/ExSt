package yahier.exst.item.ad;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/29.
 */

public class ShoppingCircleComment implements Serializable{
    int commentid;
    String squareid;
    long createtime;
    String content;
    AdUserItem2 user;
    AdUserItem2 lastuser;
    int candelete;


    public int getCommentid() {
        return commentid;
    }

    public void setCommentid(int commentid) {
        this.commentid = commentid;
    }

    public AdUserItem2 getLastuser() {
        return lastuser;
    }

    public void setLastuser(AdUserItem2 lastuser) {
        this.lastuser = lastuser;
    }

    public AdUserItem2 getUser() {
        return user;
    }

    public void setUser(AdUserItem2 user) {
        this.user = user;
    }

    public int getCandelete() {
        return candelete;
    }

    public void setCandelete(int candelete) {
        this.candelete = candelete;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getSquareid() {
        return squareid;
    }

    public void setSquareid(String squareid) {
        this.squareid = squareid;
    }
}
