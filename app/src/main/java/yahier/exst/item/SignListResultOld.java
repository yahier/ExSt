package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * 签到列表 页面的解析
 *
 * @author lenovo
 *
 */
public class SignListResultOld implements Serializable {

	int signin;
	public final static int issignNo = 0;
	public final static int issignYes = 1;

	int signdays;
	List<SignAward> signnode;
	String nextawardmsg;
	List<UserItem> proxyusers;

	public int getSigndays() {
		return signdays;
	}

	public void setSigndays(int signdays) {
		this.signdays = signdays;
	}

	public List<SignAward> getSignnode() {
		return signnode;
	}

	public void setSignnode(List<SignAward> signnode) {
		this.signnode = signnode;
	}

	public int getSignin() {
		return signin;
	}

	public void setSignin(int signin) {
		this.signin = signin;
	}

	public String getNextawardmsg() {
		return nextawardmsg;
	}

	public void setNextawardmsg(String nextawardmsg) {
		this.nextawardmsg = nextawardmsg;
	}

	public List<UserItem> getProxyusers() {
		return proxyusers;
	}

	public void setProxyusers(List<UserItem> proxyusers) {
		this.proxyusers = proxyusers;
	}

}
