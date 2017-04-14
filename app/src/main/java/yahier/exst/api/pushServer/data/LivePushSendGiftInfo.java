package yahier.exst.api.pushServer.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 送礼推送实体
 * Created by Administrator on 2016/3/12 0012.
 */
public class LivePushSendGiftInfo implements Serializable{

    private String username; //用户名
    private String giftimgurl; //用户头象
    private long userid;//用户id
    private long touserid;//收到礼物的Userid
    private String tousername; //收到礼物的Username
    private int giftid;//礼物id
    private String giftname;//礼物名称

    public LivePushSendGiftInfo(){

    }

    public LivePushSendGiftInfo(JSONObject json) throws JSONException{
        username = json.optString("username","");
        giftimgurl = json.optString("giftimgurl","");
        userid = json.optLong("userid", 0);
        touserid = json.optLong("touserid", 0);
        tousername = json.optString("tousername", "");
        giftid = json.optInt("giftid", 0);
        giftname = json.optString("giftname", "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGiftimgurl() {
        return giftimgurl;
    }

    public void setGiftimgurl(String giftimgurl) {
        this.giftimgurl = giftimgurl;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getTouserid() {
        return touserid;
    }

    public void setTouserid(long touserid) {
        this.touserid = touserid;
    }

    public String getTousername() {
        return tousername;
    }

    public void setTousername(String tousername) {
        this.tousername = tousername;
    }

    public int getGiftid() {
        return giftid;
    }

    public void setGiftid(int giftid) {
        this.giftid = giftid;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    @Override
    public String toString() {
        return "LivePushSendGiftInfo{" +
                "username='" + username + '\'' +
                ", giftimgurl='" + giftimgurl + '\'' +
                ", userid=" + userid +
                ", touserid=" + touserid +
                ", giftid=" + giftid +
                ", giftname='" + giftname + '\'' +
                '}';
    }
}
