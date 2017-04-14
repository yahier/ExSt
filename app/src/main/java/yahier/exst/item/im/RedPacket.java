package yahier.exst.item.im;

import java.io.Serializable;

public class RedPacket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -401126338459387708L;
	long hongbaoid;
	int hongbaotype;
	String hongbaodesc;
	long createuserid;
	String createusername;
	String createusericonurl;
	long createtime;
	long expiretime;
	int status;

	public final static int typeRedPacket = 1;
	public final static int typeCastBean = 2;

	public long getHongbaoid() {
		return hongbaoid;
	}

	public void setHongbaoid(long hongbaoid) {
		this.hongbaoid = hongbaoid;
	}

	public int getHongbaotype() {
		return hongbaotype;
	}

	public void setHongbaotype(int hongbaotype) {
		this.hongbaotype = hongbaotype;
	}

	public String getHongbaodesc() {
		return hongbaodesc;
	}

	public void setHongbaodesc(String hongbaodesc) {
		this.hongbaodesc = hongbaodesc;
	}

	public long getCreateuserid() {
		return createuserid;
	}

	public void setCreateuserid(long createuserid) {
		this.createuserid = createuserid;
	}

	public String getCreateusername() {
		return createusername;
	}

	public void setCreateusername(String createusername) {
		this.createusername = createusername;
	}

	public String getCreateusericonurl() {
		return createusericonurl;
	}

	public void setCreateusericonurl(String createusericonurl) {
		this.createusericonurl = createusericonurl;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public long getExpiretime() {
		return expiretime;
	}

	public void setExpiretime(long expiretime) {
		this.expiretime = expiretime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
