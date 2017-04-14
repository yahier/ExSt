package yahier.exst.item.im;

import java.io.Serializable;

public class PhoneContact implements Serializable {
	int recordid;
	String telphone;
	int isinvited;
	public final static int isInvitedNo = 0;
	public final static int isInvitedYes = 1;

	public int getRecordid() {
		return recordid;
	}

	public void setRecordid(int recordid) {
		this.recordid = recordid;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public int getIsinvited() {
		return isinvited;
	}

	public void setIsinvited(int isinvited) {
		this.isinvited = isinvited;
	}

}
