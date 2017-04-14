package yahier.exst.api.pushServer.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/12 0012.
 */
public class LivePushInfo implements Serializable{
    private String username;//用户名
    private String imgurl;//用户头象
    private long userid;//用户id
    private String msg;//消息内容
    private int imPushEnum;
    private int msgtype;//1：弹幕 2：普通消息

    public LivePushInfo(){

    }

    public LivePushInfo(JSONObject json) throws JSONException{
        username = json.optString("username", "");
        imgurl = json.optString("imgurl", "");
        userid = json.optLong("userid", 0);
        msg = json.optString("msg", "");
        imPushEnum = json.optInt("imPushEnum", 0);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public int getImPushEnum() {
        return imPushEnum;
    }

    public void setImPushEnum(int imPushEnum) {
        this.imPushEnum = imPushEnum;
    }

    @Override
    public String toString() {
        return "LivePushInfo{" +
                "username='" + username + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", userid=" + userid +
                ", msg='" + msg + '\'' +
                ", msgtype=" + msgtype +
                '}';
    }
}
