package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态评论
 * 
 * @author lenovo
 * 
 */
public class StatusesComment implements Serializable {
	private static final long serialVersionUID = -1380386169327509429L;
	int commentid;
	long createtime;
	String content;
	int praisecount;
	boolean ispraise;
	UserItem user;
	boolean candestroy;
	UserItem lastuser;
	long statusesid;

	public int getCommentid() {
		return commentid;
	}

	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPraisecount() {
		return praisecount;
	}

	public void setPraisecount(int praisecount) {
		this.praisecount = praisecount;
	}

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public boolean isIspraise() {
		return ispraise;
	}

	public void setIspraise(boolean ispraise) {
		this.ispraise = ispraise;
	}

	public boolean isCandestroy() {
		return candestroy;
	}

	public void setCandestroy(boolean candestroy) {
		this.candestroy = candestroy;
	}

	public UserItem getLastuser() {
		return lastuser;
	}

	public void setLastuser(UserItem lastuser) {
		this.lastuser = lastuser;
	}

	public long getStatusesid() {
		return statusesid;
	}

	public void setStatusesid(long statusesid) {
		this.statusesid = statusesid;
	}
}
