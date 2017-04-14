package yahier.exst.item.im;

import java.io.Serializable;
import java.util.List;

public class RedPacketOpenResult implements Serializable {
	/**
	 * 开红包结果
	 */
	private static final long serialVersionUID = 1584858011179364855L;
	long hongbaoid;
	int status;

	// 可领取 = 1, 已领完 = 2, 已过期 = 3, 自己已领取=4
	public final static int status_avilable = 1;
	public final static int status_finished = 2;
	public final static int status_expire = 3;
	public final static int status_hasGot = 4;

	public long getHongbaoid() {
		return hongbaoid;
	}

	public void setHongbaoid(long hongbaoid) {
		this.hongbaoid = hongbaoid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
