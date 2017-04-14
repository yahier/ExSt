package yahier.exst.api.data.directScreen;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public class GuestInviteInfo implements Serializable{
    private int pushmodeltype;
    private long userid;
    private String imgurl;
    private String username;
    private String topic; //主题
    private int roomid;
    private String roomgroupid;

    public int getPushmodeltype() {
        return pushmodeltype;
    }

    public void setPushmodeltype(int pushmodeltype) {
        this.pushmodeltype = pushmodeltype;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getRoomgroupid() {
        return roomgroupid;
    }

    public void setRoomgroupid(String roomgroupid) {
        this.roomgroupid = roomgroupid;
    }
}
