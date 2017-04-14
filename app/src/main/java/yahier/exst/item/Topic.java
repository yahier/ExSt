package yahier.exst.item;

import java.io.Serializable;

public class Topic implements Serializable {
	long topic_id;
	String title;
	String content;
	int comment_count;
	int read_count;
	int attention_count;// 关注数
	int isattention;// 是否关注
	ImgUrl img_url;

	public final static int isattentionYes = 1;
	public final static int isattentionNo = 0;

	public long getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(long topic_id) {
		this.topic_id = topic_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public int getRead_count() {
		return read_count;
	}

	public void setRead_count(int read_count) {
		this.read_count = read_count;
	}

	public ImgUrl getImg_url() {
		return img_url;
	}

	public void setImg_url(ImgUrl img_url) {
		this.img_url = img_url;
	}

	public int getAttention_count() {
		return attention_count;
	}

	public void setAttention_count(int attention_count) {
		this.attention_count = attention_count;
	}

	public int getIsattention() {
		return isattention;
	}

	public void setIsattention(int isattention) {
		this.isattention = isattention;
	}

}
