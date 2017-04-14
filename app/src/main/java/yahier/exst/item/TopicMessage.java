package yahier.exst.item;

import java.io.Serializable;

//话题的消息管理item。我的跟帖回复
public class TopicMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5203202905829591729L;
	Topic topic;
	TopicThread comment;
	ThreadReplyItem reply;

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public TopicThread getComment() {
		return comment;
	}

	public void setComment(TopicThread comment) {
		this.comment = comment;
	}

	public ThreadReplyItem getReply() {
		return reply;
	}

	public void setReply(ThreadReplyItem reply) {
		this.reply = reply;
	}

}
