package yahier.exst.item;

import java.io.Serializable;

/**
 * 直播间token
 * 
 * @author lenovo
 * 
 */
public class LiveRoomToken implements Serializable {
	String usersig;// 腾讯云签名
	String sdkappid;// 腾讯云sdkID
	String accounttype;// 帐号体系类型：3306-独立模式
	String identifier;// 用户id

	public String getUsersig() {
		return usersig;
	}

	public void setUsersig(String usersig) {
		this.usersig = usersig;
	}

	public String getSdkappid() {
		return sdkappid;
	}

	public void setSdkappid(String sdkappid) {
		this.sdkappid = sdkappid;
	}

	public String getAccounttype() {
		return accounttype;
	}

	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
