package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class UserTag implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -2799245619135119794L;
	long userid;
	int tradesid;
	String tradesname;
	int professionsid;
	String professionsname;
	// int othersid;
	// String othersname;
	List<TagOther> others;

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public int getTradesid() {
		return tradesid;
	}

	public void setTradesid(int tradesid) {
		this.tradesid = tradesid;
	}

	public String getTradesname() {
		return tradesname;
	}

	public void setTradesname(String tradesname) {
		this.tradesname = tradesname;
	}

	public int getProfessionsid() {
		return professionsid;
	}

	public void setProfessionsid(int professionsid) {
		this.professionsid = professionsid;
	}

	public String getProfessionsname() {
		return professionsname;
	}

	public void setProfessionsname(String professionsname) {
		this.professionsname = professionsname;
	}

	public List<TagOther> getOthers() {
		return others;
	}

	public void setOthers(List<TagOther> others) {
		this.others = others;
	}

}
