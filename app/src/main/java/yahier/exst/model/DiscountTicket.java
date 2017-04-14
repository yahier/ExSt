package yahier.exst.model;

import java.io.Serializable;

/**
 * 优惠券item
 * 
 * @author lenovo
 * 
 */
public class DiscountTicket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6913195968208790752L;
	
	String title;
	long id;
	int isused;// 是否使用
	int tickettype;// 有货泉类型 是系统的还是商家的
	String disaccount;// 金额
	long validtime;// 有效日期

	final int isused_notUsed = 0;
	final int isused_used = 1;

	final int type_system = 0;
	final int type_company = 1;

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIsused() {
		return isused;
	}

	public void setIsused(int isused) {
		this.isused = isused;
	}

	public int getTickettype() {
		return tickettype;
	}

	public void setTickettype(int tickettype) {
		this.tickettype = tickettype;
	}

	public String getDisaccount() {
		return disaccount;
	}

	public void setDisaccount(String disaccount) {
		this.disaccount = disaccount;
	}

	public long getValidtime() {
		return validtime;
	}

	public void setValidtime(long validtime) {
		this.validtime = validtime;
	}

}
