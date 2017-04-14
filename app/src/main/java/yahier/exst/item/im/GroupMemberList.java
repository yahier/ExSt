package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

import com.stbl.stbl.item.UserItem;

public class GroupMemberList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7525299981335726113L;
	String groupname;
	int groupcount;
	List<UserItem> groupmembers;
	//分组类型，
	public final static int typeRequestRelation = 0;
	public final static int typeRequestCharacter = 1;
	public final static int typeRequestNoneGroup = 2;
	//是否包含自己
	public final static int hasselfYes = 1;
	public final static int hasselfNo = 0;

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

	public List<UserItem> getGroupmembers() {
		return groupmembers;
	}

	public void setGroupmembers(List<UserItem> groupmembers) {
		this.groupmembers = groupmembers;
	}

}
