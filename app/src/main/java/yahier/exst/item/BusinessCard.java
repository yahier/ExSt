package yahier.exst.item;

import java.io.Serializable;

public class BusinessCard implements Serializable {
	int id;
	UserItem user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

}
