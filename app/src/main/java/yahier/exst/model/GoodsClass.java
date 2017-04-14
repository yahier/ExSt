package yahier.exst.model;

import java.io.Serializable;

import com.stbl.stbl.item.ImgUrl;

public class GoodsClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int classid;
	public String classname;
	public GoodsClass[] sonclass;
	public String iconurl;

	public GoodsClass() {
	}

	public GoodsClass(int classid) {
		this.classid = classid;
	}

	public int getClassid() {
		return classid;
	}

	public void setClassid(int classid) {
		this.classid = classid;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public GoodsClass[] getSonclass() {
		return sonclass;
	}

	public void setSonclass(GoodsClass[] sonclass) {
		this.sonclass = sonclass;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

}