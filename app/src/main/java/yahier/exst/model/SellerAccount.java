package yahier.exst.model;

import java.io.Serializable;

public class SellerAccount implements Serializable {

	String orderId;
	String createtime; //创建时间
	String optype; //操作类型(1： 提现、2：支付、3：商品收入 4：返现)
	String remark; //流水备注
	float amount; //流水金额
	int inouttype; //收支类型（0-收入 1-支出）
	
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public int getInouttype() {
		return inouttype;
	}
	public void setInouttype(int inouttype) {
		this.inouttype = inouttype;
	}

}
