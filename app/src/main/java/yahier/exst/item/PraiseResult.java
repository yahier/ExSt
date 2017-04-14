package yahier.exst.item;

import java.io.Serializable;

public class PraiseResult implements Serializable {

	int count;
	int type;

	public final static int type_add = 1;
	public final static int type_cancel = 0;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
