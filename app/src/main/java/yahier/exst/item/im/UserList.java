package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

import com.stbl.stbl.item.UserItem;

public class UserList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1652360469728477767L;
	List<UserItem> list;

	public List<UserItem> getList() {
		return list;
	}

	public void setList(List<UserItem> list) {
		this.list = list;
	}

}
