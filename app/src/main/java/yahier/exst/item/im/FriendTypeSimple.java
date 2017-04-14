package yahier.exst.item.im;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

public class FriendTypeSimple implements Serializable {
	UserItem masterview;
	int tudicount;
	int friendcount;

	public UserItem getMasterview() {
		return masterview;
	}

	public void setMasterview(UserItem masterview) {
		this.masterview = masterview;
	}

	public int getTudicount() {
		return tudicount;
	}

	public void setTudicount(int tudicount) {
		this.tudicount = tudicount;
	}

	public int getFriendcount() {
		return friendcount;
	}

	public void setFriendcount(int friendcount) {
		this.friendcount = friendcount;
	}

}
