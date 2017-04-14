package yahier.exst.model;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8240988683105387471L;
	long goodsid;
	long shopid;
	String goodsname;
	String imgurl;
	int salecount;
	float minprice;
	float maxprice;
	long createtime;
	long clientid;
	int firstclassid;
	String firstname;
	int secondclassid;
	String secondname;
	int brandid;
	String goodsinfo;
	String fimgurl;//
	int isdiscount;
	int statuts;
	int ispinkage;
	int iscollect;
	public final static int iscollectNo = 0;
	public final static int iscollectYes = 1;
	List<Sku> skulist;

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

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public int getSalecount() {
		return salecount;
	}

	public void setSalecount(int salecount) {
		this.salecount = salecount;
	}

	public float getMinprice() {
		return minprice;
	}

	public void setMinprice(float minprice) {
		this.minprice = minprice;
	}

	public float getMaxprice() {
		return maxprice;
	}

	public void setMaxprice(float maxprice) {
		this.maxprice = maxprice;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public long getClientid() {
		return clientid;
	}

	public void setClientid(long clientid) {
		this.clientid = clientid;
	}

	public long getShopid() {
		return shopid;
	}

	public void setShopid(long shopid) {
		this.shopid = shopid;
	}

	public int getFirstclassid() {
		return firstclassid;
	}

	public void setFirstclassid(int firstclassid) {
		this.firstclassid = firstclassid;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public int getSecondclassid() {
		return secondclassid;
	}

	public void setSecondclassid(int secondclassid) {
		this.secondclassid = secondclassid;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public int getBrandid() {
		return brandid;
	}

	public void setBrandid(int brandid) {
		this.brandid = brandid;
	}
 
	public String getGoodsinfo() {
		return goodsinfo;
	}

	public void setGoodsinfo(String goodsinfo) {
		this.goodsinfo = goodsinfo;
	}

	public String getFimgurl() {
		return fimgurl;
	}

	public void setFimgurl(String fimgurl) {
		this.fimgurl = fimgurl;
	}

	public int getIsdiscount() {
		return isdiscount;
	}

	public void setIsdiscount(int isdiscount) {
		this.isdiscount = isdiscount;
	}

	public int getStatuts() {
		return statuts;
	}

	public void setStatuts(int statuts) {
		this.statuts = statuts;
	}

	public List<Sku> getSkulist() {
		return skulist;
	}

	public void setSkulist(List<Sku> skulist) {
		this.skulist = skulist;
	}

	public int getIspinkage() {
		return ispinkage;
	}

	public void setIspinkage(int ispinkage) {
		this.ispinkage = ispinkage;
	}

	public int getIscollect() {
		return iscollect;
	}

	public void setIscollect(int iscollect) {
		this.iscollect = iscollect;
	}

}
