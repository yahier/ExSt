package yahier.exst.item;

import java.io.Serializable;

/**
 * 动态点赞
 * @author lenovo
 *
 */
public class StatusesPraise implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2906531042173495432L;
	int praisesid;
	String createtime;
	UserItem user;
	
	public int getPraisesid() {
		return praisesid;
	}
	public void setPraisesid(int praisesid) {
		this.praisesid = praisesid;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public UserItem getUser() {
		return user;
	}
	public void setUser(UserItem user) {
		this.user = user;
	}
	

	
	
	
}
