package yahier.exst.model;

import com.stbl.stbl.item.UserInfo;
import com.stbl.stbl.model.express.ExpressInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 卖家订单详情
 * @author ruilin
 *
 */
public class SellerOrderInfo<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -966318983781702957L;
	
	public long orderid;
	public int orderstate;
	public String orderstatename; //订单状态名称
	public String orderstatedescribe; //订单描述
	public long expressid;		// 物流ID（用于查询物流信息，速度最优）
	public String expressno;	// 物流No
	public long buyerid;
	public Address addressinfo;
	public Expresslastinfo expresslastinfo;
	public ExpressInfo expressInfo;
	public String invoicetitle;
	public long shopid;
	public T products;
	public ArrayList<OrderProduct> productsList;
	public int invoicetype;
	public float totalamount;
	public float realpayamount;
	public long createtime;
	public long paytime;
	public long delivertime;
	public long receipttime;
	public String userremark;
	public ShopInfo shopinfoview;
	public UserInfo buyerinfoview;
	public int isappraise;		// 是否已经评价过
	public Refundinfo refundinfo;
	public int paytype; //订单支付类型：0-未支付 1-微信 2-支付宝 3-余额 4-金豆
	public String buyerclosemsg; //买家取消订单原因
	public String sellerclosemsg; //卖家关闭订单原因
	public String sellerunagreemsg; //卖家拒绝退货原因
	public String courierid; //一点公益信使号

	public boolean parseExpress() {
		expressInfo = new ExpressInfo();
		expressInfo.orderid = orderid;
		expressInfo.expressno = expressno;
		expressInfo.toStationList(expressInfo.express);
		return true;
	}
	
	/**
	 * 退货信息
	 */
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
        public ArrayList<String> phonearr; //图片列表url
	}

	public class Expresslastinfo implements Serializable{

		/**
		 * ftime : 2016-03-07 10:12:24
		 * context : 已签收,感谢使用顺丰,期待再次为您服务
		 */

		private String ftime;
		private String context;

		public String getFtime() {
			return ftime;
		}

		public void setFtime(String ftime) {
			this.ftime = ftime;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}
	}
	
}

