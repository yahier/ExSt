package yahier.exst.item;

import java.io.Serializable;

public class PraiseItem implements Serializable {
	long StatusesId;
	int statusesagreeid;
	long praisetime;
	UserInfo user;

	public long getStatusesId() {
		return StatusesId;
	}

	public void setStatusesId(long statusesId) {
		StatusesId = statusesId;
	}

	public int getStatusesagreeid() {
		return statusesagreeid;
	}

	public void setStatusesagreeid(int statusesagreeid) {
		this.statusesagreeid = statusesagreeid;
	}

	public long getPraisetime() {
		return praisetime;
	}

	public void setPraisetime(long praisetime) {
		this.praisetime = praisetime;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

}
