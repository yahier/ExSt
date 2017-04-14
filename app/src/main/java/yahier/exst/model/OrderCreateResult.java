package yahier.exst.model;

import java.io.Serializable;
import java.util.List;

public class OrderCreateResult implements Serializable {
	int orderstate;
	int paystate;// 预支付返回的结果
	public final static int orderstateFailed = 0;
	public final static int orderstateSucceed = 1;
	public final static int orderstateWaitingPay = 2;
	int ismergepay;
	long orderno;
	long ordertime;
	PrePaydata prepaydata;
	List<OrderCreateResultSuborder> suborders;
	String msg;
	
	long orderpayno;
	
	public long getOrderpayno() {
		return orderpayno;
	}

	public void setOrderpayno(long orderpayno) {
		this.orderpayno = orderpayno;
	}

	public int getOrderstate() {
		return orderstate;
	}

	public void setOrderstate(int orderstate) {
		this.orderstate = orderstate;
	}

	public int getIsmergepay() {
		return ismergepay;
	}

	public void setIsmergepay(int ismergepay) {
		this.ismergepay = ismergepay;
	}

	public long getOrderno() {
		return orderno;
	}

	public void setOrderno(long orderno) {
		this.orderno = orderno;
	}


	public long getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(long ordertime) {
		this.ordertime = ordertime;
	}

	public List<OrderCreateResultSuborder> getSuborders() {
		return suborders;
	}

	public void setSuborders(List<OrderCreateResultSuborder> suborders) {
		this.suborders = suborders;
	}

	public PrePaydata getPrepaydata() {
		return prepaydata;
	}

	public void setPrepaydata(PrePaydata prepaydata) {
		this.prepaydata = prepaydata;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getPaystate() {
		return paystate;
	}

	public void setPaystate(int paystate) {
		this.paystate = paystate;
	}

}
