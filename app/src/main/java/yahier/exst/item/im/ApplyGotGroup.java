package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

public class ApplyGotGroup implements Serializable {

	String groupname;
	int groupcount;
	List<ApplyGotItem> groupmembers;

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public int getGroupcount() {
		return groupcount;
	}

	public void setGroupcount(int groupcount) {
		this.groupcount = groupcount;
	}

	public List<ApplyGotItem> getGroupmembers() {
		return groupmembers;
	}

	public void setGroupmembers(List<ApplyGotItem> groupmembers) {
		this.groupmembers = groupmembers;
	}

}
