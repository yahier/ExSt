package yahier.exst.api.data.directScreen;

import com.stbl.stbl.api.utils.preferences.STBLWession;

import java.io.Serializable;
import java.util.List;

/**
 * Created by meteorshower on 16/3/11.
 */
public class RoomDetailsInfo implements Serializable {


    /**
     * modifytime : 0
     * roomid : 200977
     * topic : 杀马特奔腾的浪花
     * desc : 嗨,大家好!我开启了语音直播室,快来和我一起畅聊话题吧!
     * userid : 14556846509545
     * nickname : Jack
     * imgurl : http://dev-img-static.stbl.cc//sys/default/user.png
     * imgurlsrc : /sys/default/user.png
     * micplaces : [{"placeindex":1,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":2,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":3,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":4,"placetype":0,"micstatus":1,"memberid":0}]
     * guestplaces : [{"placeindex":1,"placetype":1,"micstatus":1,"memberid":0}]
     * memebertotalcount : 0
     * pickmiccount : 0
     */

    private int modifytime;
    private int roomid;
    private String topic;
    private String desc;
    private long userid;
    private String nickname;
    private String imgurl;
    private String imgurlsrc;
    private int memebertotalcount;
    private int pickmiccount;
    /**
     * placeindex : 1
     * placetype : 0
     * micstatus : 1
     * memberid : 0
     */

    private List<RoomPlaceInfo> micplaces;
    /**
     * placeindex : 1
     * placetype : 1
     * micstatus : 1
     * memberid : 0
     */

    private List<RoomPlaceInfo> guestplaces;

    public void setModifytime(int modifytime) {
        this.modifytime = modifytime;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public void setImgurlsrc(String imgurlsrc) {
        this.imgurlsrc = imgurlsrc;
    }

    public void setMemebertotalcount(int memebertotalcount) {
        this.memebertotalcount = memebertotalcount;
    }

    public void setPickmiccount(int pickmiccount) {
        this.pickmiccount = pickmiccount;
    }

    public void setMicplaces(List<RoomPlaceInfo> micplaces) {
        this.micplaces = micplaces;
    }

    public void setGuestplaces(List<RoomPlaceInfo> guestplaces) {
        this.guestplaces = guestplaces;
    }

    public int getModifytime() {
        return modifytime;
    }

    public int getRoomid() {
        return roomid;
    }

    public String getTopic() {
        return topic;
    }

    public String getDesc() {
        return desc;
    }

    public long getUserid() {
        return userid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getImgurlsrc() {
        return imgurlsrc;
    }

    public int getMemebertotalcount() {
        return memebertotalcount;
    }

    public int getPickmiccount() {
        return pickmiccount;
    }

    public List<RoomPlaceInfo> getMicplaces() {
        return micplaces;
    }

    public List<RoomPlaceInfo> getGuestplaces() {
        return guestplaces;
    }

    /** 是否为房主 */
    public boolean isOwnerFlag(){
        return STBLWession.getInstance().readIdentifier().equals(String.valueOf(getUserid()));
    }
    @Override
    public String toString() {
        return "RoomDetailsInfo{" +
                "desc='" + desc + '\'' +
                ", modifytime=" + modifytime +
                ", roomid=" + roomid +
                ", topic='" + topic + '\'' +
                ", userid=" + userid +
                ", nickname='" + nickname + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", imgurlsrc='" + imgurlsrc + '\'' +
                ", memebertotalcount=" + memebertotalcount +
                ", pickmiccount=" + pickmiccount +
                ", micplaces=" + micplaces.size() +
                ", guestplaces=" + guestplaces.size() +
                '}';
    }
}
