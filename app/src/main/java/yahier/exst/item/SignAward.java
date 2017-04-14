package yahier.exst.item;

import java.io.Serializable;

public class SignAward implements Serializable {
	int type;
	String desc;//描述当前节点信息

	//type：9-未签到;1-金豆;2-绿豆;11-可打开的宝箱（触发04开启宝箱.md）;12-已打开的宝箱;13-未打开的宝箱; 14-已过期的宝箱;
	public final static int TypeGoldBean = 1;
	public final static int TypeGreenBean = 2;
	public final static int TypeScore = 3;//积分
	public final static int TypeNotSign = 9;
	public final static int TypeBoxOpenable = 11;//可以打开的宝箱
	public final static int TypeBoxOpened = 12;//已经打开的宝箱
	public final static int TypeBoxCloseed = 13;//未打开的宝箱
	public final static int TypeBoxExpire = 14;//已经过期的宝箱

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
