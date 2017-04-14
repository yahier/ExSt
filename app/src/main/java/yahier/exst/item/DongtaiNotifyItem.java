package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态新消息实体
 * Created by Administrator on 2016/6/16 0016.
 */
public class DongtaiNotifyItem implements Serializable {

    public static final int NOTIFY_TYPE_COMMENT = 1;//评论
    public static final int NOTIFY_TYPE_REPLY = 2;//回复
    public static final int NOTIFY_TYPE_REPOST = 3;//转发
    public static final int NOTIFY_TYPE_PRAISE = 4;//点赞

    private int id;//id
    private long statusesid;//动态id
    private int commentid;//评论id
    private long opuserid;//推送用户id
    private String opusername; //推送用户名称
    private String opuserimgurl;//推送用户头像
    private long reminduserid;//提醒用户id
    private String remindusername;//提醒用户名称
    private long remindtime;//提醒时间
    private int type;//推送类型
    private String typename;//推送类型名称
    private String content;//推送的内容
    private String imgurl;//显示图像（如果没有图像，就显示imgcontent ）
    private String imgcontent;//显示文字（如果没有文字，就显示imgurl ）

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStatusesid() {
        return statusesid;
    }

    public void setStatusesid(long statusesid) {
        this.statusesid = statusesid;
    }

    public int getCommentid() {
        return commentid;
    }

    public void setCommentid(int commentid) {
        this.commentid = commentid;
    }

    public long getOpuserid() {
        return opuserid;
    }

    public void setOpuserid(long opuserid) {
        this.opuserid = opuserid;
    }

    public String getOpusername() {
        return opusername;
    }

    public void setOpusername(String opusername) {
        this.opusername = opusername;
    }

    public String getOpuserimgurl() {
        return opuserimgurl;
    }

    public void setOpuserimgurl(String opuserimgurl) {
        this.opuserimgurl = opuserimgurl;
    }

    public long getReminduserid() {
        return reminduserid;
    }

    public void setReminduserid(long reminduserid) {
        this.reminduserid = reminduserid;
    }

    public String getRemindusername() {
        return remindusername;
    }

    public void setRemindusername(String remindusername) {
        this.remindusername = remindusername;
    }

    public long getRemindtime() {
        return remindtime;
    }

    public void setRemindtime(long remindtime) {
        this.remindtime = remindtime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getImgcontent() {
        return imgcontent;
    }

    public void setImgcontent(String imgcontent) {
        this.imgcontent = imgcontent;
    }
}
