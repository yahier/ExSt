package yahier.exst.item;

import java.io.Serializable;

public class TribeGift implements Serializable {
	long rewardlogid;
	UserItem objuser;
	Gift giftinfo;

	public long getRewardlogid() {
		return rewardlogid;
	}

	public void setRewardlogid(long rewardlogid) {
		this.rewardlogid = rewardlogid;
	}

	public UserItem getObjuser() {
		return objuser;
	}

	public void setObjuser(UserItem objuser) {
		this.objuser = objuser;
	}

	public Gift getGiftinfo() {
		return giftinfo;
	}

	public void setGiftinfo(Gift giftinfo) {
		this.giftinfo = giftinfo;
	}

}
