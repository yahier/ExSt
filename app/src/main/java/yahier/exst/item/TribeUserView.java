package yahier.exst.item;

import java.io.Serializable;

public class TribeUserView implements Serializable {

	long id;
	TribeUserItem user;
	Relation relation;
	Level level;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TribeUserItem getUser() {
		return user;
	}

	public void setUser(TribeUserItem user) {
		this.user = user;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

}
