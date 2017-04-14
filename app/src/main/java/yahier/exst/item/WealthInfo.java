package yahier.exst.item;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WealthInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6682186028315584614L;
	long userid;
	int jindou;
	int lvdou;
	float amountaddup;
	int jindouaddup;
	int lvdouaddup;
	float amounts;
	int jifen; //积分

	ExchargeRate exchargerate;
	
	public final static int payTypeWeiXin = 1;
	public final static int payTypeAlipay = 2;
	public final static int payTypeBalance = 3;
	public final static int payTypeGoldPea = 4;
	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public int getJindou() {
		return jindou;
	}

	public void setJindou(int jindou) {
		this.jindou = jindou;
	}

	public int getLvdou() {
		return lvdou;
	}

	public void setLvdou(int lvdou) {
		this.lvdou = lvdou;
	}

	public float getAmountaddup() {
		return amountaddup;
	}

	public void setAmountaddup(float amountaddup) {
		this.amountaddup = amountaddup;
	}

	public int getJindouaddup() {
		return jindouaddup;
	}

	public void setJindouaddup(int jindouaddup) {
		this.jindouaddup = jindouaddup;
	}

	public int getLvdouaddup() {
		return lvdouaddup;
	}

	public void setLvdouaddup(int lvdouaddup) {
		this.lvdouaddup = lvdouaddup;
	}

	public float getAmounts() {
		return amounts;
	}

	public void setAmounts(float amounts) {
		this.amounts = amounts;
	}

	public int getJifen() {
		return jifen;
	}

	public void setJifen(int jifen) {
		this.jifen = jifen;
	}

	public ExchargeRate getExchargerate() {
		return exchargerate;
	}

	public void setExchargerate(ExchargeRate exchargerate) {
		this.exchargerate = exchargerate;
	}
}
