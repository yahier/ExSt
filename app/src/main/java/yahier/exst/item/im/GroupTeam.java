package yahier.exst.item.im;

import java.io.Serializable;

public class GroupTeam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034100360671332936L;
	long groupid;
	String groupname;
	String groupdesc;
	int memberscount;
	int likecount;
	int ranking;
	long createtime;
	long groupmasterid;// 群主id
	String iconurl;
	String iconlarurl;
	String iconoriurl;

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

	public int getMemberscount() {
		return memberscount;
	}

	public void setMemberscount(int memberscount) {
		this.memberscount = memberscount;
	}

	public int getLikecount() {
		return likecount;
	}

	public void setLikecount(int likecount) {
		this.likecount = likecount;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public long getGroupmasterid() {
		return groupmasterid;
	}

	public void setGroupmasterid(long groupmasterid) {
		this.groupmasterid = groupmasterid;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getIconlarurl() {
		return iconlarurl;
	}

	public void setIconlarurl(String iconlarurl) {
		this.iconlarurl = iconlarurl;
	}

	public String getIconoriurl() {
		return iconoriurl;
	}

	public void setIconoriurl(String iconoriurl) {
		this.iconoriurl = iconoriurl;
	}

}
