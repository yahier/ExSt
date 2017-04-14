package yahier.exst.model;

import java.io.Serializable;

public class HomeLikeItem implements Serializable{
	long businessid;
	String title;
	int businesstype;
	String param;
	String extraparam;
	String imgurl;
	String imglarurl;
	String imgoriurl;
	//businesstype的种类
	public final static int typeWeb = 0;
	public final static int typeGoods = 1;
	public final static int typeShop = 2;
	public final static int typeModule = 3;
	public final static int typeTopic = 4;

	public long getBusinessid() {
		return businessid;
	}

	public void setBusinessid(long businessid) {
		this.businessid = businessid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBusinesstype() {
		return businesstype;
	}

	public void setBusinesstype(int businesstype) {
		this.businesstype = businesstype;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getExtraparam() {
		return extraparam;
	}

	public void setExtraparam(String extraparam) {
		this.extraparam = extraparam;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getImglarurl() {
		return imglarurl;
	}

	public void setImglarurl(String imglarurl) {
		this.imglarurl = imglarurl;
	}

	public String getImgoriurl() {
		return imgoriurl;
	}

	public void setImgoriurl(String imgoriurl) {
		this.imgoriurl = imgoriurl;
	}

}
