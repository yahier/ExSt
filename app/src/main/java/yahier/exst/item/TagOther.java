package yahier.exst.item;

import java.io.Serializable;

/**
 * 标签之其它标签
 * 
 * @author lenovo
 * 
 */
public class TagOther implements Serializable {
	int id;
	String title;
	int type;
	long createtime;

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

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

}
