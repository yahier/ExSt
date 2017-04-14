package yahier.exst.item;

import java.io.Serializable;

public class SignResult implements Serializable {

	int signdays;
	String awardmsg;

	public int getSigndays() {
		return signdays;
	}

	public void setSigndays(int signdays) {
		this.signdays = signdays;
	}

	public String getAwardmsg() {
		return awardmsg;
	}

	public void setAwardmsg(String awardmsg) {
		this.awardmsg = awardmsg;
	}

}
