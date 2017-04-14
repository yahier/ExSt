package yahier.exst.model;

import java.io.Serializable;

/**
 * 第三方支付完成回调
 */
public class ExternalPayResult implements Serializable{
	public final static int TYPE_WEIXIN = 0;
	public final static int TYPE_ALIPAY = 1;
	
	int errCode;
	String transaction;
	public final static int errCodeSucceed = 0;
	public final static int errCodeFailed = 1;
	public final static int errCodeCancel = 2;

	int type;
	
	public ExternalPayResult(int type, int errCode, String transaction) {
		this.errCode = errCode;
		this.transaction = transaction;
		this.type = type;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}
	
	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		String str = "type=" + type
				+ ", errCode=" + errCode
				+ ", transaction=" + transaction;
		return str;
	}
}
