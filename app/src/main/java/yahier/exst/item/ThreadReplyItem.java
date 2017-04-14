package yahier.exst.item;

import java.io.Serializable;

/**
 * 
 * @author lenovo
 * 跟帖的回复
 *
 */
public class ThreadReplyItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6529615680032033283L;
	int reply_id;
	String content;
	boolean allow_destroy;
	long create_time;
	ThreadUser user;

	public int getReply_id() {
		return reply_id;
	}

	public void setReply_id(int reply_id) {
		this.reply_id = reply_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isAllow_destroy() {
		return allow_destroy;
	}

	public void setAllow_destroy(boolean allow_destroy) {
		this.allow_destroy = allow_destroy;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public ThreadUser getUser() {
		return user;
	}

	public void setUser(ThreadUser user) {
		this.user = user;
	}

}
