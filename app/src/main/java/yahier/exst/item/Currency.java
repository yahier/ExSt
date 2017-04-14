package yahier.exst.item;

import java.io.Serializable;

public class Currency implements Serializable {
	int id;
	String sysremark;
	float amount;
	long createtime;

	int optype;
	public final static int optypeGet = 0;// 收入
	public final static int optypeSent = 1;// 支出
	int currencytype;

	public final static int currencytypeRMB = 0;
	public final static int currencytypeJindou = 1;
	public final static int currencytypeLvdou = 2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSysremark() {
		return sysremark;
	}

	public void setSysremark(String sysremark) {
		this.sysremark = sysremark;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public int getOptype() {
		return optype;
	}

	public void setOptype(int optype) {
		this.optype = optype;
	}

	public int getCurrencytype() {
		return currencytype;
	}

	public void setCurrencytype(int currencytype) {
		this.currencytype = currencytype;
	}

}
