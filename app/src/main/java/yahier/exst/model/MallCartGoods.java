package yahier.exst.model;

import java.io.Serializable;

/**
 * 购物车 商品item
 * 
 * @author lenovo
 * 
 */
public class MallCartGoods implements Serializable {
	int cartid;
	long goodsid;
	String goodsname;
	String imgurl;
	String skuname;
	float realprice;
	int stockcount;
	int goodscount;
	int isonshelf;
	long skuid;
	boolean isSelected;
	final static int isonshelf_yes = 1;
	final static int isonshelf_no = 0;

	public int getCartid() {
		return cartid;
	}

	public void setCartid(int cartid) {
		this.cartid = cartid;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getSkuname() {
		return skuname;
	}

	public void setSkuname(String skuname) {
		this.skuname = skuname;
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

	public int getGoodscount() {
		return goodscount;
	}

	public void setGoodscount(int goodscount) {
		this.goodscount = goodscount;
	}

	public int getIsonshelf() {
		return isonshelf;
	}

	public void setIsonshelf(int isonshelf) {
		this.isonshelf = isonshelf;
	}

	public long getSkuid() {
		return skuid;
	}

	public void setSkuid(long skuid) {
		this.skuid = skuid;
	}

	public long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(long goodsid) {
		this.goodsid = goodsid;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
