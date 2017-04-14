package yahier.exst.item;

import java.io.Serializable;

//现在用在了快递公司列表
public class CommonItem implements Serializable {
	int value;
	String title;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
