package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态的收藏与取消接口的返回
 */
public class Collect implements Serializable {
	int collectcount;

	public int getCollectcount() {
		return collectcount;
	}

	public void setCollectcount(int collectcount) {
		this.collectcount = collectcount;
	}

}
