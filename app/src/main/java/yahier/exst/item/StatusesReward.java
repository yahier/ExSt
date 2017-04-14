package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态点赞
 * @author lenovo
 *
 */
public class StatusesReward implements Serializable {
	
	long rewardtime;
	UserItem user;

	public long getRewardtime() {
		return rewardtime;
	}

	public void setRewardtime(long rewardtime) {
		this.rewardtime = rewardtime;
	}

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}
}
