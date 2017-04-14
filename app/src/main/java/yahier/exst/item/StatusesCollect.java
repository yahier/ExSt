package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * 动态
 * 
 * @author lenovo
 * 
 */
public class StatusesCollect implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1110803338446979457L;
	int statusescollectid;
	long createtime;
	Statuses statuses;

	public int getStatusescollectid() {
		return statusescollectid;
	}

	public void setStatusescollectid(int statusescollectid) {
		this.statusescollectid = statusescollectid;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public Statuses getStatuses() {
		return statuses;
	}

	public void setStatuses(Statuses statuses) {
		this.statuses = statuses;
	}

}
