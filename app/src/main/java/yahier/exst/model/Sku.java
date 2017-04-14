package yahier.exst.model;

import java.io.Serializable;

public class Sku implements Serializable{
	long skuid;
	String skuname;
	float outprice;
	float realprice;
	int stockcount;

	public long getSkuid() {
		return skuid;
	}

	public void setSkuid(long skuid) {
		this.skuid = skuid;
	}

	public String getSkuname() {
		return skuname;
	}

	public void setSkuname(String skuname) {
		this.skuname = skuname;
	}

	public float getOutprice() {
		return outprice;
	}

	public void setOutprice(float outprice) {
		this.outprice = outprice;
	}

	public float getRealprice() {
		return realprice;
	}

	public void setRealprice(float realprice) {
		this.realprice = realprice;
	}

	public int getStockcount() {
		return stockcount;
	}

	public void setStockcount(int stockcount) {
		this.stockcount = stockcount;
	}

}
