package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class CurrentResult implements Serializable {

	UserItem user;
	List<Currency> currencys;

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public List<Currency> getCurrencys() {
		return currencys;
	}

	public void setCurrencys(List<Currency> currencys) {
		this.currencys = currencys;
	}

}
