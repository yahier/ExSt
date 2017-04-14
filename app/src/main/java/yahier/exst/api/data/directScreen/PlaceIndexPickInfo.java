package yahier.exst.api.data.directScreen;

import java.io.Serializable;
import java.util.List;

/**
 * Created by meteorshower on 16/3/11.
 */
public class PlaceIndexPickInfo implements Serializable {

    /**
     * roomid : 203022
     * placeindex : 1
     * status : 1
     * roominfo : {"modifytime":1459153101,"roomid":203022,"topic":"测试切麦位","desc":"嗨,大家好!我开启了语音直播室,快来和我一起畅聊话题吧!","isneedpwd":0,"userid":14556846509545,"nickname":"Jack","imgurl":"http://dev-img-static.stbl.cc//user/14556846509545/user/head/ori/1458703376541.jpg","imgurlsrc":"/user/14556846509545/user/head/###/1458703376541.jpg","micplaces":[{"placeindex":1,"placetype":0,"micstatus":1,"memberid":14571732172658,"nickname":"噜啦啦","imgurl":"http://dev-img-static.stbl.cc//user/14571732172658/user/head/ori/1458989425327.jpg","imgurlsrc":"/user/14571732172658/user/head/###/1458989425327.jpg"},{"placeindex":2,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":3,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":4,"placetype":0,"micstatus":1,"memberid":0}],"guestplaces":[{"placeindex":1,"placetype":1,"micstatus":1,"memberid":0}],"memebertotalcount":2,"pickmiccount":2,"isallowpopmsg":1}
     */

    private int roomid;
    private int placeindex;
    private int status;
    /**
     * modifytime : 1459153101
     * roomid : 203022
     * topic : 测试切麦位
     * desc : 嗨,大家好!我开启了语音直播室,快来和我一起畅聊话题吧!
     * isneedpwd : 0
     * userid : 14556846509545
     * nickname : Jack
     * imgurl : http://dev-img-static.stbl.cc//user/14556846509545/user/head/ori/1458703376541.jpg
     * imgurlsrc : /user/14556846509545/user/head/###/1458703376541.jpg
     * micplaces : [{"placeindex":1,"placetype":0,"micstatus":1,"memberid":14571732172658,"nickname":"噜啦啦","imgurl":"http://dev-img-static.stbl.cc//user/14571732172658/user/head/ori/1458989425327.jpg","imgurlsrc":"/user/14571732172658/user/head/###/1458989425327.jpg"},{"placeindex":2,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":3,"placetype":0,"micstatus":1,"memberid":0},{"placeindex":4,"placetype":0,"micstatus":1,"memberid":0}]
     * guestplaces : [{"placeindex":1,"placetype":1,"micstatus":1,"memberid":0}]
     * memebertotalcount : 2
     * pickmiccount : 2
     * isallowpopmsg : 1
     */

    private RoomInfo roominfo;

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setPlaceindex(int placeindex) {
        this.placeindex = placeindex;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRoominfo(RoomInfo roominfo) {
        this.roominfo = roominfo;
    }

    public int getRoomid() {
        return roomid;
    }

    public int getPlaceindex() {
        return placeindex;
    }

    public int getStatus() {
        return status;
    }

    public RoomInfo getRoominfo() {
        return roominfo;
    }

}
