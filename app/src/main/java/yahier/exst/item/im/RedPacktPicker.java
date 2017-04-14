package yahier.exst.item.im;

import java.io.Serializable;

/**
 * 抢红包的人
 * 
 * @author lenovo
 * 
 */
public class RedPacktPicker implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3521784719462721465L;
	String pickamount;
	long pickuserid;
	String pickusername;
	String pickusericonurl;
	long picktime;
	int isbest;
	public final static int isbestYes = 1;
	public final static int isbestNo = 0;

	public String getPickamount() {
		return pickamount;
	}

	public void setPickamount(String pickamount) {
		this.pickamount = pickamount;
	}

	public long getPickuserid() {
		return pickuserid;
	}

	public void setPickuserid(long pickuserid) {
		this.pickuserid = pickuserid;
	}

	public String getPickusername() {
		return pickusername;
	}

	public void setPickusername(String pickusername) {
		this.pickusername = pickusername;
	}

	public String getPickusericonurl() {
		return pickusericonurl;
	}

	public void setPickusericonurl(String pickusericonurl) {
		this.pickusericonurl = pickusericonurl;
	}

	public long getPicktime() {
		return picktime;
	}

	public void setPicktime(long picktime) {
		this.picktime = picktime;
	}

	public int getIsbest() {
		return isbest;
	}

	public void setIsbest(int isbest) {
		this.isbest = isbest;
	}

}
