package yahier.exst.item;

import java.io.Serializable;

public class Masters implements Serializable {
	// 师傅 长老 酋长 什么的
	UserItem masterview;
	UserItem elderview;
	UserItem headmenview;
	int isheadmen;
	public final static int isheadmenYes = 1;
	public final static int isheadmenNo = 0;

	public final static int qubaishiYes = 0;
	//不用拜师
	public final static int qubaishiNo = 1;

	public UserItem getMasterview() {
		return masterview;
	}

	public void setMasterview(UserItem masterview) {
		this.masterview = masterview;
	}

	public UserItem getElderview() {
		return elderview;
	}

	public void setElderview(UserItem elderview) {
		this.elderview = elderview;
	}

	public UserItem getHeadmenview() {
		return headmenview;
	}

	public void setHeadmenview(UserItem headmenview) {
		this.headmenview = headmenview;
	}

	public int getIsheadmen() {
		return isheadmen;
	}

	public void setIsheadmen(int isheadmen) {
		this.isheadmen = isheadmen;
	}

}
