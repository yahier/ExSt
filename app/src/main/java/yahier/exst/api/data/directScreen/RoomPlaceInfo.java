package yahier.exst.api.data.directScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by meteorshower on 16/3/11.
 */
public class RoomPlaceInfo implements Serializable{

    /**
     * placeindex : 1
     * placetype : 0
     * micstatus : 1
     * memberid : 14571732172658
     * nickname : 噜啦啦
     * imgurl : http://dev-img-static.stbl.cc//sys/default/user.png
     * imgurlsrc : /sys/default/user.png
     */

    private int placeindex;
    private int placetype;//位置类型 0:麦位 1:嘉宾位
    private int micstatus;
    private long memberid;
    private String nickname;
    private String imgurl;
    private String imgurlsrc;
    private int imPushEnum;

    public RoomPlaceInfo(){

    }

    public RoomPlaceInfo(JSONObject json) throws JSONException{

        placeindex = json.optInt("placeindex", 0);
        placetype = json.optInt("placetype", 0);
        micstatus = json.optInt("micstatus", 0);
        memberid = json.optLong("memberid", 0);
        imPushEnum = json.optInt("imPushEnum", 0);
        nickname = json.optString("nickname", "");
        imgurl = json.optString("imgurl", "");
        imgurlsrc = json.optString("imgurlsrc", "");
    }

    public void setPlaceindex(int placeindex) {
        this.placeindex = placeindex;
    }

    public void setPlacetype(int placetype) {
        this.placetype = placetype;
    }

    public void setMicstatus(int micstatus) {
        this.micstatus = micstatus;
    }

    public void setMemberid(long memberid) {
        this.memberid = memberid;
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

    public int getPlaceindex() {
        return placeindex;
    }

    public int getPlacetype() {
        return placetype;
    }

    public int getMicstatus() {
        return micstatus;
    }

    public long getMemberid() {
        return memberid;
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

    public int getImPushEnum() {
        return imPushEnum;
    }

    public void setImPushEnum(int imPushEnum) {
        this.imPushEnum = imPushEnum;
    }
}
