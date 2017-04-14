package yahier.exst.item;

import java.io.Serializable;

public class ThreadUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6737732333788936551L;
	long userid;
	String nickname;
	String imgurl;

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

}
