package yahier.exst.model;

import java.io.Serializable;

public class OrderProduct implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4360459018786910686L;
	long orderdetailid;
	long goodsid;
	String goodsname;
	String skuname;
	float price;
	int count;
	long skuid;
	String imgurl;

	public long getOrderdetailid() {
		return orderdetailid;
	}

	public void setOrderdetailid(long orderdetailid) {
		this.orderdetailid = orderdetailid;
	}
	
	public long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(long goodsid) {
		this.goodsid = goodsid;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public String getSkuname() {
		return skuname;
	}

	public void setSkuname(String skuname) {
		this.skuname = skuname;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getSkuid() {
		return skuid;
	}

	public void setSkuid(long skuid) {
		this.skuid = skuid;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

}
