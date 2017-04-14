package yahier.exst.model;

import java.io.Serializable;

/**
 * 传递给支付类的参数，比如传给微信支付
 * 
 * @author lenovo
 * 
 */
public class PayParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7375764865320362236L;
	float totalFee;
	String goodsDesc;
	long payNo;

	public float getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(float totalFee) {
		this.totalFee = totalFee;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public long getPayNo() {
		return payNo;
	}

	public void setPayNo(long payNo) {
		this.payNo = payNo;
	}

}
