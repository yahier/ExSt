package yahier.exst.model;

import java.io.Serializable;

/**
 * 卖家订单统计信息
 * @author ruilin
 *
 */
public class SellerOrderStatistics implements Serializable {

	private static final long serialVersionUID = 22179617371857245L;
	
	public int waitpaycount;
    public int waitreceipcount;
    public int waitsendcount;
    public int aftersalecount;
}
