package yahier.exst.item;

import java.io.Serializable;

public class UserSign extends UserItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -194319391267818529L;
	long signtime;

	public long getSigntime() {
		return signtime;
	}

	public void setSigntime(long signtime) {
		this.signtime = signtime;
	}

}
