package yahier.exst.item;

import com.stbl.stbl.api.data.LiveRoomToken;

import java.io.Serializable;

public class AuthToken implements Serializable {
	String oldaccesstoken;
	String accesstoken;
	String refreshtoken;
	String rongyuntoken;
	String userid;
	String roleid;
	String roleflag;
	long expiriestime;
	UserItem userinfo;
	LiveRoomToken liveroomtoken;

	public String getOldaccesstoken() {
		return oldaccesstoken;
	}

	public void setOldaccesstoken(String oldaccesstoken) {
		this.oldaccesstoken = oldaccesstoken;
	}

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public String getRefreshtoken() {
		return refreshtoken;
	}

	public void setRefreshtoken(String refreshtoken) {
		this.refreshtoken = refreshtoken;
	}

	public String getRongyuntoken() {
		return rongyuntoken;
	}

	public void setRongyuntoken(String rongyuntoken) {
		this.rongyuntoken = rongyuntoken;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRoleflag() {
		return roleflag;
	}

	public void setRoleflag(String roleflag) {
		this.roleflag = roleflag;
	}

	public long getExpiriestime() {
		return expiriestime;
	}

	public void setExpiriestime(long expiriestime) {
		this.expiriestime = expiriestime;
	}

	public UserItem getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(UserItem userinfo) {
		this.userinfo = userinfo;
	}

	public LiveRoomToken getLiveRoomToken() {
		return liveroomtoken;
	}

	public void setLiveRoomToken(LiveRoomToken liveRoomToken) {
		this.liveroomtoken = liveRoomToken;
	}
}
