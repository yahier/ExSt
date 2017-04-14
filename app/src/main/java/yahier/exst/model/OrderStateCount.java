package yahier.exst.model;

import java.io.Serializable;

public class OrderStateCount implements Serializable {

	int waitpaycount;
	int waitreceipcount;
	int waitsendcount;
	int aftersalecount;
	int appraisecount;

	public int getWaitpaycount() {
		return waitpaycount;
	}

	public void setWaitpaycount(int waitpaycount) {
		this.waitpaycount = waitpaycount;
	}

	public int getWaitreceipcount() {
		return waitreceipcount;
	}

	public void setWaitreceipcount(int waitreceipcount) {
		this.waitreceipcount = waitreceipcount;
	}

	public int getWaitsendcount() {
		return waitsendcount;
	}

	public void setWaitsendcount(int waitsendcount) {
		this.waitsendcount = waitsendcount;
	}

	public int getAftersalecount() {
		return aftersalecount;
	}

	public void setAftersalecount(int aftersalecount) {
		this.aftersalecount = aftersalecount;
	}

	public int getAppraisecount() {
		return appraisecount;
	}

	public void setAppraisecount(int appraisecount) {
		this.appraisecount = appraisecount;
	}
}
