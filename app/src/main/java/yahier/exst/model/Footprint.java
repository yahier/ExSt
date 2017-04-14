package yahier.exst.model;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

/**
 * 足迹
 * @author ruilin
 *
 */
public class Footprint implements Serializable {

	long id;
	int moduletype;
    long businessid;
	String actionurl;
    String businessurl;
    String remark;
    long createtime;
    
    public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getModuletype() {
		return moduletype;
	}
	public void setModuletype(int moduletype) {
		this.moduletype = moduletype;
	}
	public long getBusinessid() {
		return businessid;
	}
	public void setBusinessid(long businessid) {
		this.businessid = businessid;
	}

	public String getActionurl() {
		return actionurl;
	}

	public void setActionurl(String actionurl) {
		this.actionurl = actionurl;
	}

	public String getBusinessurl() {
		return businessurl;
	}
	public void setBusinessurl(String businessurl) {
		this.businessurl = businessurl;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
}
