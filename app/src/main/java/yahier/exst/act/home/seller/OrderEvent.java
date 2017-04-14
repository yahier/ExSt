package yahier.exst.act.home.seller;

/**
 * 订单事件
 */
public class OrderEvent {
	public final static int EVENT_UPDATE = 0;
	
	int type;
	int class1;
	int class2;
	
	public int getClass1() {
		return class1;
	}

	public void setClass1(int class1) {
		this.class1 = class1;
	}

	public int getClass2() {
		return class2;
	}

	public void setClass2(int class2) {
		this.class2 = class2;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	

}
