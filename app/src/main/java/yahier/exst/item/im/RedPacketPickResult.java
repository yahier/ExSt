package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

/**
 * 不作为最大一级来解析
 */
public class RedPacketPickResult implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1584858011179364855L;
	long hongbaoid;
	String hongbaodesc;
	String amount;
	int qty;
	int pickqty;
	int currencytype;
	long createuserid;
	String createusername;
	String createusericonurl;
	long createtime;
	long expiretime;
	int status;

	// 可领取 = 1, 已领完 = 2, 已过期 = 3, 自己已领取=4
	public final static int status_avilable = 1;
	public final static int status_finished = 2;
	public final static int status_expire = 3;
	public final static int status_hasGot = 4;

	int hongbaotype;
	public final static int hongbaotype_redPacket = 1;// 红包
	public final static int hongbaotype_caseBean = 2;// 撒豆

	int ispicked;
	List<RedPacktPicker> pickusers;

	public long getHongbaoid() {
		return hongbaoid;
	}

	public void setHongbaoid(long hongbaoid) {
		this.hongbaoid = hongbaoid;
	}

	public String getHongbaodesc() {
		return hongbaodesc;
	}

	public void setHongbaodesc(String hongbaodesc) {
		this.hongbaodesc = hongbaodesc;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public int getPickqty() {
		return pickqty;
	}

	public void setPickqty(int pickqty) {
		this.pickqty = pickqty;
	}

	public int getCurrencytype() {
		return currencytype;
	}

	public void setCurrencytype(int currencytype) {
		this.currencytype = currencytype;
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

	public int getIspicked() {
		return ispicked;
	}

	public void setIspicked(int ispicked) {
		this.ispicked = ispicked;
	}

	public List<RedPacktPicker> getPickusers() {
		return pickusers;
	}

	public void setPickusers(List<RedPacktPicker> pickusers) {
		this.pickusers = pickusers;
	}

	public int getHongbaotype() {
		return hongbaotype;
	}

	public void setHongbaotype(int hongbaotype) {
		this.hongbaotype = hongbaotype;
	}

}
