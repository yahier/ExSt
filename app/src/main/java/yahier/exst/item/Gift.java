package yahier.exst.item;

import java.io.Serializable;

public class Gift implements Serializable {
	int giftid;
	String giftname;
	String giftimg;
	int currencytype;
	int value;
	public final static int type_renminbi = 0;
	public final static int type_jindou = 1;
	public final static int type_lvdou = 2;

	public int getGiftid() {
		return giftid;
	}

	public void setGiftid(int giftid) {
		this.giftid = giftid;
	}

	public String getGiftname() {
		return giftname;
	}

	public void setGiftname(String giftname) {
		this.giftname = giftname;
	}

	public String getGiftimg() {
		return giftimg;
	}

	public void setGiftimg(String giftimg) {
		this.giftimg = giftimg;
	}

	public int getCurrencytype() {
		return currencytype;
	}

	public void setCurrencytype(int currencytype) {
		this.currencytype = currencytype;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
