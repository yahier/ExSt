package yahier.exst.api.pushServer.data;

import com.stbl.stbl.api.data.directScreen.RoomPlaceInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meteorshower on 16/3/12.
 */
public class RoomPushPlaceInfo implements Serializable{

    private int pushmodeltype;
    private int membertotalcount;
    private int pickmiccount;
    private int roomid;
    private List<RoomPlaceInfo> micplaces;
    private List<RoomPlaceInfo> guestplaces;
    private int isallowpopmsg; // 是否弹幕 1-允许 0-不允许

    public RoomPushPlaceInfo(JSONObject json) throws Exception{

        pushmodeltype = json.optInt("pushmodeltype", 0);
        membertotalcount = json.optInt("membertotalcount", 0);
        pickmiccount = json.optInt("pickmiccount", 0);
        roomid = json.optInt("roomid", 0);
        isallowpopmsg = json.optInt("isallowpopmsg",0);

        if(!json.isNull("micplaces")){
            JSONArray jsonArr = json.optJSONArray("micplaces");
            micplaces = new ArrayList<RoomPlaceInfo>();
            for(int i= 0 ; i < jsonArr.length(); i++){
                micplaces.add(new RoomPlaceInfo(jsonArr.optJSONObject(i)));
            }
        }

        if(!json.isNull("guestplaces")){
            JSONArray jsonArr = json.optJSONArray("guestplaces");
            guestplaces = new ArrayList<RoomPlaceInfo>();
            for(int i= 0 ; i < jsonArr.length(); i++){
                guestplaces.add(new RoomPlaceInfo(jsonArr.optJSONObject(i)));
            }
        }
    }

    public int getIsallowpopmsg() {
        return isallowpopmsg;
    }

    public void setIsallowpopmsg(int isallowpopmsg) {
        this.isallowpopmsg = isallowpopmsg;
    }

    public void setPushmodeltype(int pushmodeltype) {
        this.pushmodeltype = pushmodeltype;
    }

    public void setMembertotalcount(int membertotalcount) {
        this.membertotalcount = membertotalcount;
    }

    public void setPickmiccount(int pickmiccount) {
        this.pickmiccount = pickmiccount;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setMicplaces(List<RoomPlaceInfo> micplaces) {
        this.micplaces = micplaces;
    }

    public void setGuestplaces(List<RoomPlaceInfo> guestplaces) {
        this.guestplaces = guestplaces;
    }

    public int getPushmodeltype() {
        return pushmodeltype;
    }

    public int getMembertotalcount() {
        return membertotalcount;
    }

    public int getPickmiccount() {
        return pickmiccount;
    }

    public int getRoomid() {
        return roomid;
    }

    public List<RoomPlaceInfo> getMicplaces() {
        return micplaces;
    }

    public List<RoomPlaceInfo> getGuestplaces() {
        return guestplaces;
    }

}
