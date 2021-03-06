package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * 话题跟帖
 * 
 * @author lenovo
 * 
 */
public class TopicThread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1763585095706893580L;
	long comment_id;
	UserItem user;
	String content;
	int reply_count;
	int allow_destroy;
	long create_time;
	// ImgUrl img_url;
	StatusesPic img_url;
	List<ThreadReplyItem> reply;

	public long getComment_id() {
		return comment_id;
	}

	public void setComment_id(long comment_id) {
		this.comment_id = comment_id;
	}

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getReply_count() {
		return reply_count;
	}

	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}

	public int getAllow_destroy() {
		return allow_destroy;
	}

	public void setAllow_destroy(int allow_destroy) {
		this.allow_destroy = allow_destroy;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public StatusesPic getImg_url() {
		return img_url;
	}

	public void setImg_url(StatusesPic img_url) {
		this.img_url = img_url;
	}

	public List<ThreadReplyItem> getReply() {
		return reply;
	}

	public void setReply(List<ThreadReplyItem> reply) {
		this.reply = reply;
	}

}
