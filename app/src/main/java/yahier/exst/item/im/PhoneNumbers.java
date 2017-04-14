package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

import com.stbl.stbl.item.UserItem;

public class PhoneNumbers implements Serializable {
    
	List<UserItem> registeduserlist;//号码中已经注册app的列表
	List<PhoneContact> unregistedphonelist;//号码中没有注册的列表

	public List<UserItem> getRegisteduserlist() {
		return registeduserlist;
	}

	public void setRegisteduserlist(List<UserItem> registeduserlist) {
		this.registeduserlist = registeduserlist;
	}

	public List<PhoneContact> getUnregistedphonelist() {
		return unregistedphonelist;
	}

	public void setUnregistedphonelist(List<PhoneContact> unregistedphonelist) {
		this.unregistedphonelist = unregistedphonelist;
	}

}
