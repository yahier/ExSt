package yahier.exst.model;

import java.io.Serializable;

/**
 * 余额或者金豆支付的返回结果
 * 
 * @author lenovo
 * 
 */
public class BalanceOrDouPayResult implements Serializable {
	long orderno;
	int paystate;
	String msg;
	float fee;
	long orderpayno;
	long transationno;

	public final static int paystateFailed = 0;
	public final static int paystateSeccueed = 1;
	public final static int paystatePwdRequired = 2;
	public final static int paystateWrongPwd = 3;

	public long getOrderno() {
		return orderno;
	}

	public void setOrderno(long orderno) {
		this.orderno = orderno;
	}

	public int getPaystate() {
		return paystate;
	}

	public void setPaystate(int paystate) {
		this.paystate = paystate;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public float getFee() {
		return fee;
	}

	public void setFee(float fee) {
		this.fee = fee;
	}

	public long getOrderpayno() {
		return orderpayno;
	}

	public void setOrderpayno(long orderpayno) {
		this.orderpayno = orderpayno;
	}

	public long getTransationno() {
		return transationno;
	}

	public void setTransationno(long transationno) {
		this.transationno = transationno;
	}

}
