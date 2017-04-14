package yahier.exst.item;

import java.io.Serializable;

public class EventTypeWallet implements Serializable {
	int type;
	public final static int typeShitudou = 1;
	public final static int typeBance = 2;

	public EventTypeWallet(int type) {
		this.type = type;

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
