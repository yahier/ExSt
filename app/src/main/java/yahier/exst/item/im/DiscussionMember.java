package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

import com.stbl.stbl.item.UserItem;

public class DiscussionMember implements Serializable{
	List<UserItem> members;

	public List<UserItem> getMembers() {
		return members;
	}

	public void setMembers(List<UserItem> members) {
		this.members = members;
	}

}
