package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/3/18.
 * 融云直播间邀请类型消息的item
 */
public class MessageLiveRoomInvite implements Serializable {
    long userid;
    String username;
    String topic;
    String imgurl;
    long roomid;


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

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getRoomid() {
        return roomid;
    }

    public void setRoomid(long roomid) {
        this.roomid = roomid;
    }
}
