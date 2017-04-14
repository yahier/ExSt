package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

import com.stbl.stbl.item.UserItem;

public class DiscussionInviteShow implements Serializable {
	long inviteid;
	int status;
	public final static int status_inviting = 0;
	public final static int status_received = 1;
	public final static int status_rejected = 2;
	long handletime;
	long inviteuserid;// 邀请人
	String inviteusername;
	long groupid;
	String groupname;
	long groupcreatetime;
	List<UserItem> members;

	public long getInviteid() {
		return inviteid;
	}

	public void setInviteid(long inviteid) {
		this.inviteid = inviteid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getHandletime() {
		return handletime;
	}

	public void setHandletime(long handletime) {
		this.handletime = handletime;
	}

	public long getInviteuserid() {
		return inviteuserid;
	}

	public void setInviteuserid(long inviteuserid) {
		this.inviteuserid = inviteuserid;
	}

	public String getInviteusername() {
		return inviteusername;
	}

	public void setInviteusername(String inviteusername) {
		this.inviteusername = inviteusername;
	}

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

	public long getGroupcreatetime() {
		return groupcreatetime;
	}

	public void setGroupcreatetime(long groupcreatetime) {
		this.groupcreatetime = groupcreatetime;
	}

	public List<UserItem> getMembers() {
		return members;
	}

	public void setMembers(List<UserItem> members) {
		this.members = members;
	}

}
