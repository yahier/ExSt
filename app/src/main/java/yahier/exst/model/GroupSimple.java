package yahier.exst.model;

import java.io.Serializable;

/**
 * 帮会简单信息
 * @author ruilin
 *
 */
public class GroupSimple implements Serializable {

	long groupid;
	String groupname;
    String groupdesc;
    int membercount;
    
    public long getGroupid() {
		return groupid;
	}
	public void setGroupid(long groupid) {
		this.groupid = groupid;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getGroupdesc() {
		return groupdesc;
	}
	public void setGroupdesc(String groupdesc) {
		this.groupdesc = groupdesc;
	}
	public int getMembercount() {
		return membercount;
	}
	public void setMembercount(int membercount) {
		this.membercount = membercount;
	}

}
