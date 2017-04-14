package yahier.exst.model;

import java.io.Serializable;

public class PrePaydata implements Serializable {
	long orderno;
	int orderstate;
//	long payno;   // 支付号取消？
	float payfee;
	String weixinjsonparameters;
	int paytype;
//	String msg;
	long orderpayno;

	public long getOrderno() {
		return orderno;
	}

	public void setOrderno(long orderno) {
		this.orderno = orderno;
	}

	public int getOrderstate() {
		return orderstate;
	}

	public void setOrderstate(int paystate) {
		this.orderstate = paystate;
	}

	public long getOrderpayno() {
		return orderpayno;
	}

	public void setOrderpayno(long payno) {
		this.orderpayno = payno;
	}

	public float getPayfee() {
		return payfee;
	}

	public void setPayfee(float payfee) {
		this.payfee = payfee;
	}

	public String getWeixinjsonparameters() {
		return weixinjsonparameters;
	}

	public void setWeixinjsonparameters(String weixinjsonparameters) {
		this.weixinjsonparameters = weixinjsonparameters;
	}

//	public String getMsg() {
//		return msg;
//	}
//
//	public void setMsg(String msg) {
//		this.msg = msg;
//	}
}
