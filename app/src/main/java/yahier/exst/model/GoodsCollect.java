package yahier.exst.model;

import java.io.Serializable;

public class GoodsCollect implements Serializable {
	int collectioncount;
	int optype;
	public final static int optypeSave = 0;//收藏
	public final static int optypeCancel = 1;//取消收藏
	public int getCollectioncount() {
		return collectioncount;
	}

	public void setCollectioncount(int collectioncount) {
		this.collectioncount = collectioncount;
	}

	public int getOptype() {
		return optype;
	}

	public void setOptype(int optype) {
		this.optype = optype;
	}

}
