package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

/**
 * 签到列表 页面的解析
 *
 * @author lenovo
 *
 */
public class SignListResult implements Serializable {

	int signin;
	public final static int issignNo = 0;
	public final static int issignYes = 1;

	int cursignnode;//当天的节点信息
	int signdays;//连续签到天数
	int chest;//是否打开宝箱
	public final static int chestNo = 0;
	public final static int chestYes = 1;

	List<Integer> signnode;
	String nextawardmsg;
	long nowtimestamp;
	String awardmsg;



	//1-已签到;9-未签到;10-未签到已过期;11-可打开的宝箱;12-已打开的宝箱;13-未打开的宝箱;

	public final static int stateSigned = 1;
	public final static int stateToSign = 9;
	public final static int stateExpire = 10;
	public final static int stateBoxOpened = 12;
	public final static int stateBoxToOpen = 11;
	public final static int stateBoxClose = 13;


	public int getSigndays() {
		return signdays;
	}

	public void setSigndays(int signdays) {
		this.signdays = signdays;
	}

	public List<Integer> getSignnode() {
		return signnode;
	}

	public void setSignnode(List<Integer> signnode) {
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


	public void setCursignnode(int cursignnode) {
		this.cursignnode = cursignnode;
	}

	public int getCursignnode() {
		return cursignnode;
	}

	public void setNowtimestamp(long nowtimestamp) {
		this.nowtimestamp = nowtimestamp;
	}

	public long getNowtimestamp() {
		return nowtimestamp;
	}
	public String getAwardmsg() {
		return awardmsg;
	}

	public void setAwardmsg(String awardmsg) {
		this.awardmsg = awardmsg;
	}
}
