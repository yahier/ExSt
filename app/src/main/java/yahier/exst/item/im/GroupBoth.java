package yahier.exst.item.im;

import com.stbl.stbl.item.UserItem;

import java.io.Serializable;

/**
 * 消息首页 我的帮去和师傅的帮群
 * 
 * @author lenovo
 * 
 */
public class GroupBoth implements Serializable {
	GroupTeam mygroup;
	GroupTeam mastergroup;
	UserItem assistant;

	public GroupTeam getMygroup() {
		return mygroup;
	}

	public void setMygroup(GroupTeam mygroup) {
		this.mygroup = mygroup;
	}

	public GroupTeam getMastergroup() {
		return mastergroup;
	}

	public void setMastergroup(GroupTeam mastergroup) {
		this.mastergroup = mastergroup;
	}

	public UserItem getAssistant() {
		return assistant;
	}

	public void setAssistant(UserItem assistant) {
		this.assistant = assistant;
	}

}
