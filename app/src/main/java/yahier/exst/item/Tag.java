package yahier.exst.item;

import java.io.Serializable;

/**
 * 标签
 * @author lenovo
 *
 */
public class Tag implements Serializable {

	int id;
	String title;
	int type;
	long ctreatetime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getCtreatetime() {
		return ctreatetime;
	}

	public void setCtreatetime(long ctreatetime) {
		this.ctreatetime = ctreatetime;
	}

}
