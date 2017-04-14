package yahier.exst.item.im;

import java.io.Serializable;

public class DiscussionTeam implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 4593620497438395544L;
	long groupid;
	String groupname;
	int memberscount;
	long groupmasterid;
	long createTime;
	long jointime;
	long keeptime;
	int iscollect;

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

	public int getMemberscount() {
		return memberscount;
	}

	public void setMemberscount(int memberscount) {
		this.memberscount = memberscount;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getJointime() {
		return jointime;
	}

	public void setJointime(long jointime) {
		this.jointime = jointime;
	}

	public long getKeeptime() {
		return keeptime;
	}

	public void setKeeptime(long keeptime) {
		this.keeptime = keeptime;
	}

	public long getGroupmasterid() {
		return groupmasterid;
	}

	public void setGroupmasterid(long groupmasterid) {
		this.groupmasterid = groupmasterid;
	}

	public int getIscollect() {
		return iscollect;
	}

	public void setIscollect(int iscollect) {
		this.iscollect = iscollect;
	}
}
