package yahier.exst.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.stbl.stbl.model.express.ExpressInfo;

/**
 * 买家订单详情
 * @author ruilin
 *
 * @param <T>
 */
public class MallOrderInfo<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4804737010994749602L;
	
	public long orderid;
	public int orderstate;
	public long expressid;		// 物流ID（用于查询物流信息，速度最优）
	public String expressno;	// 物流No
	public long buyerid;
	public Address addressinfo;
	public String expresslastinfo;
	public ExpressInfo expressInfo;
	public String invoicetitle;
	public long shopid;
	public T products;
	public ArrayList<OrderProduct> productsList;
	public int invoicetype;
	public float totalamount;
	public float realpayamount;
	public String createtime;
	public String paytime;
	public String delivertime;
	public String receipttime;
	public String userremark;
	
	public Refundinfo refundinfo;
	
	public boolean parseExpress() {
		if (null == expresslastinfo || expresslastinfo.equals("")) return false;
		expressInfo = new ExpressInfo();
		expressInfo.orderid = orderid;
		expressInfo.expressno = expressno;
//		expressInfo.stationList = expressInfo.parseStation(expresslastinfo);
		expressInfo.toStationList(expressInfo.express);
		return true;
	}
	
	public class Refundinfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2836154820067552103L;
		public long refundid;
        public int reason;
        public String reasonname;
        public String description;
        public String applytime;
        public String expresstime;
        public ArrayList<String> phonearr;
	}
}
