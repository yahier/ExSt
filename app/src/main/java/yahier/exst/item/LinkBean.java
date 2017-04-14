package yahier.exst.item;

import java.io.Serializable;

/**
 * 我的-精彩链接
 * @author Administrator
 *
 */
public class LinkBean implements Serializable{
	private int linkid;//链接id
	private String picminurl;//小图
	private String piclarurl;//大图
	private String picoriurl;//原图
	private String linktitle;//链接标题
	private String linkurl;//链接地址
	
	public int getLinkid() {
		return linkid;
	}
	public void setLinkid(int linkid) {
		this.linkid = linkid;
	}
	public String getPicminurl() {
		return picminurl;
	}
	public void setPicminurl(String picminurl) {
		this.picminurl = picminurl;
	}
	public String getPiclarurl() {
		return piclarurl;
	}
	public void setPiclarurl(String piclarurl) {
		this.piclarurl = piclarurl;
	}
	public String getPicoriurl() {
		return picoriurl;
	}
	public void setPicoriurl(String picoriurl) {
		this.picoriurl = picoriurl;
	}
	public String getLinktitle() {
		return linktitle;
	}
	public void setLinktitle(String linktitle) {
		this.linktitle = linktitle;
	}
	public String getLinkurl() {
		return linkurl;
	}
	public void setLinkurl(String linkurl) {
		this.linkurl = linkurl;
	}
	
	
}
