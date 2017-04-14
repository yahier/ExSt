package yahier.exst.model;

import java.io.Serializable;

public class OrderCreateResultSuborder implements Serializable {
	long shopid;
	long orderid;
	float realamount;
	float totalamount;

	public long getShopid() {
		return shopid;
	}

	public void setShopid(long shopid) {
		this.shopid = shopid;
	}

	public long getOrderid() {
		return orderid;
	}

	public void setOrderid(long orderid) {
		this.orderid = orderid;
	}

	public float getRealamount() {
		return realamount;
	}

	public void setRealamount(float realamount) {
		this.realamount = realamount;
	}

	public float getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(float totalamount) {
		this.totalamount = totalamount;
	}

}
