package yahier.exst.act.im.rong;

/**
 * 邀请消息类型中的item
 */
public class ItemInvite {
	long userid;
	String message;
	long targetid;
	int conversationtype;
	long inviteid;

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTargetid() {
		return targetid;
	}

	public void setTargetid(long targetid) {
		this.targetid = targetid;
	}

	public int getConversationtype() {
		return conversationtype;
	}

	public void setConversationtype(int conversationtype) {
		this.conversationtype = conversationtype;
	}

	public long getInviteid() {
		return inviteid;
	}

	public void setInviteid(long inviteid) {
		this.inviteid = inviteid;
	}

}
