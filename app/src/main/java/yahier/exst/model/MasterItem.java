package yahier.exst.model;

import java.io.Serializable;

/**
 * 推荐师傅列表条目
 * @author ruilin
 *
 */
public class MasterItem implements Serializable {

	long userid;
	int sortid;
    long lastlogintime;
    
    public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public int getSortid() {
		return sortid;
	}
	public void setSortid(int sortid) {
		this.sortid = sortid;
	}
	public long getLastlogintime() {
		return lastlogintime;
	}
	public void setLastlogintime(long lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
    
}
