package yahier.exst.item;

import java.io.Serializable;

/**
 * user字段包含者。
 * 
 * @author lenovo
 * 
 */
public class UserContainer implements Serializable {
	UserItem user;
	Level level;
	Relation relation;
	UserTag tags;

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public UserTag getTags() {
		return tags;
	}

	public void setTags(UserTag tags) {
		this.tags = tags;
	}

}
