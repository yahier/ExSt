package yahier.exst.item;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 动态配图
 *
 * @author lenovo
 *
 */
public class StatusesPic implements Serializable {
	private static final long serialVersionUID = 1116608790918935750L;
	String thumbpic;
	String middlepic;
	String originalpic;
	ArrayList<String> pics;// 只是图片名称。需要和上面三个字符串配合 组成完整字符串
	String defaultpic;// 长微博封面图
	String ex;//现在作为无图时么，被转发的显示图

	public String getThumbpic() {
		return thumbpic;
	}

	public void setThumbpic(String thumbpic) {
		this.thumbpic = thumbpic;
	}

	public String getMiddlepic() {
		return middlepic;
	}

	public void setMiddlepic(String middlepic) {
		this.middlepic = middlepic;
	}

	public String getOriginalpic() {
		return originalpic;
	}

	public void setOriginalpic(String originalpic) {
		this.originalpic = originalpic;
	}

	public ArrayList<String> getPics() {
		return pics;
	}

	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
	}

	public String getDefaultpic() {
		return defaultpic;
	}

	public void setDefaultpic(String defaultpic) {
		this.defaultpic = defaultpic;
	}


	public String getEx() {
		return ex;
	}

	public void setEx(String ex) {
		this.ex = ex;
	}
}
