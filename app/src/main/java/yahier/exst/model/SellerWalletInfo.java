package yahier.exst.model;

import java.io.Serializable;

public class SellerWalletInfo implements Serializable {

	private static final long serialVersionUID = -4340763345971425819L;
	
	private long userid;
	private float totalamount; //总金额
	private float canoutamount; //可提现金额
	private float outedamount; //已提现金额
	private float unsettledamount; //未结算金额

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public float getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(float totalamount) {
		this.totalamount = totalamount;
	}

	public float getCanoutamount() {
		return canoutamount;
	}

	public void setCanoutamount(float canoutamount) {
		this.canoutamount = canoutamount;
	}

	public float getOutedamount() {
		return outedamount;
	}

	public void setOutedamount(float outedamount) {
		this.outedamount = outedamount;
	}

	public float getUnsettledamount() {
		return unsettledamount;
	}

	public void setUnsettledamount(float unsettledamount) {
		this.unsettledamount = unsettledamount;
	}
}
