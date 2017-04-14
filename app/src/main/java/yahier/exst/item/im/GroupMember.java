package yahier.exst.item.im;

import java.io.Serializable;

public class GroupMember implements Serializable {

	long userid;
	String username;
	String iconurl;
	int isinvited;

	public final static int isinvitedNo = 0;
	public final static int isinvitedYes = 1;
	int isshutup;

	public final static int isshutupNo = 0;
	public final static int isshutupYes = 1;

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public int getIsinvited() {
		return isinvited;
	}

	public void setIsinvited(int isinvited) {
		this.isinvited = isinvited;
	}

	public int getIsshutup() {
		return isshutup;
	}

	public void setIsshutup(int isshutup) {
		this.isshutup = isshutup;
	}

}
