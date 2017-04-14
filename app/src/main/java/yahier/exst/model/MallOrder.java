package yahier.exst.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.stbl.stbl.act.home.seller.SimpleGoodsAdapter;
import com.stbl.stbl.adapter.mall.OrderGoodsAdapter;

public class MallOrder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1814234520158531743L;
	int id;
	public long orderid;
	int orderstate;
	String orderstatename;
	ArrayList<OrderProduct> products;
	Address addressinfo;
	float realpayamount;
	String shopname;
	String shopalias;
	long createtime;
	long modifytime;
	int isappraise;
	int paytype; //订单支付类型：0-未支付 1-微信 2-支付宝 3-余额 4-金豆

	public OrderGoodsAdapter adapter;

	public final static int querytypeAll = 0;
	public final static int querytypeToBePaid = 1;
	public final static int querytypeToSend = 2;
	public final static int querytypeToReceive = 3;
	public final static int querytypeService = 4;

	// 全部 = 0,待付款 = 1,待发货 = 2,待收货 = 3,售后 = 4, 待评价 = 5)
	public final static int state_all = 0;
	public final static int state_toBePaid = 1;
	public final static int state_toBeDelivered = 2;
	public final static int state_toBeGet = 3;
	public final static int state_postService = 4;
	public final static int STATE_EVALUATE = 5;

	public OrderGoodsAdapter getAdapter(Activity act) {
		if (null == adapter)
			adapter = new OrderGoodsAdapter(act, products);
		return adapter;
	}

	//弃用。直接去orderstatename
//	public String getStateText() {
//		switch (orderstate) {
//		case 20000:
//			return "订单生成中";
//		case 21000:
//			return "未付款";
//		case 21100:
//			return "支付确认中";
//		case 21200:
//			return "待发货";
//		case 21300:
//			return "待收货";
//		case 21900:
//			return "交易完成";
//		case 21400:
//			return "物流已签收";
//		case 21201:
//			return "等待卖家退款";
//		case 21202:
//			return "卖家不同意退款";
//		case 22100:
//			return "退货待确认";
//		case 22101:
//			return "卖家退货不同意";
//		case 22102:
//			return "客服协商申请中";
//		case 22200:
//			return "退货待发货";
//		case 22300:
//			return "等待卖家收货";
//		case 22400:
//			return "退货中";
//		case 23000:
//			return "打款确认中";
//		case 21001:
//			return "取消订单";
//		case 29998:
//			return "退货成功";
//		case 29997:
//			return "交易失败";
//		case 29999:
//			return "交易关闭";
//		default:
//			return "交易异常";
//		}
//	}


	public String getShopalias() {
		return shopalias;
	}

	public void setShopalias(String shopalias) {
		this.shopalias = shopalias;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// public long getOrderid() {
	// return orderid;
	// }
	//
	// public void setOrderid(long orderid) {
	// this.orderid = orderid;
	// }

	public int getOrderstate() {
		return orderstate;
	}

	public String getOrderstatename() {
		return orderstatename;
	}

	public void setOrderstatename(String orderstatename) {
		this.orderstatename = orderstatename;
	}

	public void setOrderstate(int orderstate) {
		this.orderstate = orderstate;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public float getRealpayamount() {
		return realpayamount;
	}

	public void setRealpayamount(float realpayamount) {
		this.realpayamount = realpayamount;
	}

	public ArrayList<OrderProduct> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<OrderProduct> products) {
		this.products = products;
	}

	public Address getAddressinfo() {
		return addressinfo;
	}

	public void setAddressinfo(Address addressinfo) {
		this.addressinfo = addressinfo;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public long getModifytime() {
		return modifytime;
	}

	public void setModifytime(long modifytime) {
		this.modifytime = modifytime;
	}

	public int getIsappraise() {
		return isappraise;
	}

	public void setIsappraise(int isappraise) {
		this.isappraise = isappraise;
	}

	public int getPaytype() {
		return paytype;
	}

	public void setPaytype(int paytype) {
		this.paytype = paytype;
	}
}
