package yahier.exst.model;

import java.io.Serializable;

public class Headmen implements Serializable{

	UserSimple userview;
	GroupSimple groupview;
	
	public UserSimple getUserview() {
		return userview;
	}
	public void setUserview(UserSimple userview) {
		this.userview = userview;
	}
	public GroupSimple getGroupview() {
		return groupview;
	}
	public void setGroupview(GroupSimple groupview) {
		this.groupview = groupview;
	}
    
}
