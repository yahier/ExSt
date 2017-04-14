package yahier.exst.api.data;

import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.utils.UIUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by meteorshower on 16/3/3.
 */
public class LiveRoomCreateInfo implements Serializable{

    /**
     * topic : 杀马特奔腾的浪花
     * roomid : 200029
     * desc : 嗨,大家好!我开启了语音直播室,快来和我一起畅聊话题吧!
     * micplaces : [{"micstatus":1,"placeindex":1,"memberid":0,"placetype":0},{"micstatus":1,"placeindex":2,"memberid":0,"placetype":0},{"micstatus":1,"placeindex":3,"memberid":0,"placetype":0},{"micstatus":1,"placeindex":4,"memberid":0,"placetype":0}]
     * userid : 14556846509545
     * guestplaces : [{"micstatus":1,"placeindex":1,"memberid":0,"placetype":1}]
     */

    private String topic;
    private int roomid;
    private String desc;
    private long userid;
    /**
     * micstatus : 1
     * placeindex : 1
     * memberid : 0
     * placetype : 0
     */

    private List<MicplacesEntity> micplaces;
    /**
     * micstatus : 1
     * placeindex : 1
     * memberid : 0
     * placetype : 1
     */

    private List<GuestplacesEntity> guestplaces;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public void setMicplaces(List<MicplacesEntity> micplaces) {
        this.micplaces = micplaces;
    }

    public void setGuestplaces(List<GuestplacesEntity> guestplaces) {
        this.guestplaces = guestplaces;
    }

    public String getTopic() {
        return topic;
    }

    public int getRoomid() {
        return roomid;
    }

    public String getDesc() {
        return desc;
    }

    public long getUserid() {
        return userid;
    }

    public List<MicplacesEntity> getMicplaces() {
        return micplaces;
    }

    public List<GuestplacesEntity> getGuestplaces() {
        return guestplaces;
    }

    public static class MicplacesEntity implements Serializable{
        private int micstatus;
        private int placeindex;
        private int memberid;
        private int placetype;

        public void setMicstatus(int micstatus) {
            this.micstatus = micstatus;
        }

        public void setPlaceindex(int placeindex) {
            this.placeindex = placeindex;
        }

        public void setMemberid(int memberid) {
            this.memberid = memberid;
        }

        public void setPlacetype(int placetype) {
            this.placetype = placetype;
        }

        public int getMicstatus() {
            return micstatus;
        }

        public int getPlaceindex() {
            return placeindex;
        }

        public int getMemberid() {
            return memberid;
        }

        public int getPlacetype() {
            return placetype;
        }
    }

    public static class GuestplacesEntity implements Serializable{
        private int micstatus;
        private int placeindex;
        private int memberid;
        private int placetype;

        public void setMicstatus(int micstatus) {
            this.micstatus = micstatus;
        }

        public void setPlaceindex(int placeindex) {
            this.placeindex = placeindex;
        }

        public void setMemberid(int memberid) {
            this.memberid = memberid;
        }

        public void setPlacetype(int placetype) {
            this.placetype = placetype;
        }

        public int getMicstatus() {
            return micstatus;
        }

        public int getPlaceindex() {
            return placeindex;
        }

        public int getMemberid() {
            return memberid;
        }

        public int getPlacetype() {
            return placetype;
        }
    }

    @Override
    public String toString() {
        return "LiveRoomCreateInfo{" +
                "desc='" + desc + '\'' +
                ", topic='" + topic + '\'' +
                ", roomid=" + roomid +
                ", userid=" + userid +
                ", micplaces=" + micplaces +
                ", guestplaces=" + guestplaces +
                '}';
    }
}
