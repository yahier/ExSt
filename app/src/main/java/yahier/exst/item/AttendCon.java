package yahier.exst.item;

import java.io.Serializable;

/**
 * 关注列表数据的item
 * @author lenovo
 *
 */
public class AttendCon implements Serializable {

	UserItem user;
	int isshield;
	int isattention;

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public int getIsshield() {
		return isshield;
	}

	public void setIsshield(int isshield) {
		this.isshield = isshield;
	}

	public int getIsattention() {
		return isattention;
	}

	public void setIsattention(int isattention) {
		this.isattention = isattention;
	}
}
