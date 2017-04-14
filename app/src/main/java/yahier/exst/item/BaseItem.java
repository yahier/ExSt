package yahier.exst.item;

import java.io.Serializable;

/**
 * 解析所有的返回数据
 * 
 * @author lenovo
 */
public class BaseItem<T> implements Serializable {
	T result;// 对象或者数组.悲剧 反射没起到作用
	int issuccess;
	ServerError err;
	public final static int successTag = 1;// 返回结果的成功标致
	public final static int errorNoTaostTag = 101;// 网络不可用

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public ServerError getErr() {
		return err;
	}

	public void setErr(ServerError err) {
		this.err = err;
	}

	public int getIssuccess() {
		return issuccess;
	}

	public void setIssuccess(int issuccess) {
		this.issuccess = issuccess;
	}

	// public Error getErr() {
	// return err;
	// }
	//
	// public void setErr(Error err) {
	// this.err = err;
	// }

}
