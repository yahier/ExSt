package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class ThreadDetailItem implements Serializable {
	int comment_id;
	ThreadUser user;
	String content;
	int reply_count;
	boolean allow_destroy;
	long create_time;
	ImgUrl img_url;
	List<ThreadReplyItem> reply;

	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public ThreadUser getUser() {
		return user;
	}

	public void setUser(ThreadUser user) {
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

	public ImgUrl getImg_url() {
		return img_url;
	}

	public void setImg_url(ImgUrl img_url) {
		this.img_url = img_url;
	}

	public List<ThreadReplyItem> getReply() {
		return reply;
	}

	public void setReply(List<ThreadReplyItem> reply) {
		this.reply = reply;
	}

}
