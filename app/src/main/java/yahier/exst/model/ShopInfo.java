package yahier.exst.model;

import java.io.Serializable;

public class ShopInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4709545764872160700L;
	long shopid;
	String shopimgurl;
	String shopname;
	String alias;//备注
	String introduction;
	float commentscore;
	long createtime;
	String contactshopid;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public long getShopid() {
		return shopid;
	}

	public void setShopid(long shopid) {
		this.shopid = shopid;
	}

	public String getShopimgurl() {
		return shopimgurl;
	}

	public void setShopimgurl(String shopimgurl) {
		this.shopimgurl = shopimgurl;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public float getCommentscore() {
		return commentscore;
	}

	public void setCommentscore(float commentscore) {
		this.commentscore = commentscore;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}


	public String getContactshopid() {
		return contactshopid;
	}

	public void setContactshopid(String contactshopid) {
		this.contactshopid = contactshopid;
	}
}
