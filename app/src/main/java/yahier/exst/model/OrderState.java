package yahier.exst.model;

import java.io.Serializable;

/**	订单状态  */
public class OrderState implements Serializable {

	public final static int MAKEING_ORDER = 20000;			//订单系统异常，弹出下单失败提示。不显示，订单将被删除（后台服务）
	public final static int WAIT_FOR_PAY = 21000;			//待支付			系统	
	public final static int PAYED_CONFIRM = 21100;			//支付确认		系统
	public final static int WAIT_FOR_SEND = 21200;			//待发货			系统
	public final static int WAIT_FOR_RECEIVE = 21300;		//待收货
	public final static int GET_TO_EXPRESS = 21400;			//物流已签收		系统
	public final static int FINISH = 21900;					//交易完成		买家	买家确定收货，可以申请退货
	public final static int RETURN_RECEIVE_REFUSE = 22400;	//退货拒签		系统
	public final static int FUND_TO_SELLER = 23000;			//打款确认中		退款时系统异常，未检测到退款成功，待服务处理或人工服务（后台服务）

	public final static int CANCEL = 21001;					//订单取消		买家	订卖家未发货，用户取消订单，或则卖家关闭订单
	public final static int RETURN_PAY_APPLY = 21201;		//申请退货中		卖家N天内不操作，默认同意（后台服务）
	public final static int RETURN_APPLY = 22100;			//退货待确认		买家	买家退货
	public final static int RETURN_WAITING = 22300;			//退货待收货		买家	买家已退货发货，待卖家确定收货
	public final static int APPLY_SERVICE = 22102;			//客服协商申请中(退款)	买家	买家申请客服介入
	public final static int APPLY_SERVICE2 = 22103;			//客服协商申请中(退货)	买家	买家申请客服介入

	public final static int RETURN_DISAGREE_AMOUNT = 21202;	//退款不同意		卖家
	public final static int RETURN_DISAGREE = 22101;		//退货不同意		卖家
	public final static int RETURN_WAIT_FOR_SEND = 22200;	//退货待发货		卖家	卖家同意退货，买家需要发货
	public final static int FAIL = 29997;					//交易失败		卖家	取消订单，系统关闭订单
	public final static int RETURN_FINISH = 29998;			//退款成功		卖家	关闭订单，退货成功
	public final static int CLOSE = 29999;					//订单关闭		卖家	交易完成，系统关闭订单

	
	private int orderId;
	private String state;
	private String remark;
	
	/**
	 * 仅作参考 不再调用
	 * @param orderId
	 */
	public OrderState(int orderId) {
		this.orderId = orderId;
		switch (orderId) {
		case CANCEL:
			state = "订单取消";
			remark = "订单取消，系统将关闭此订单，交易失败";
			break;
		case RETURN_PAY_APPLY:
			state = "退款待确认";
			remark = "等待确认";
			break;
		case RETURN_APPLY:
			state = "退货申请中";
			remark = "等待卖家确认";
			break;
		case RETURN_WAITING:
			state = "退货待收货";
			remark = "买家已退货发货，待卖家确定收货";
			break;
		case APPLY_SERVICE:
			state = "客服协商申请中";
			remark = "正在申请客服介入";
			break;
			
		case RETURN_DISAGREE_AMOUNT:
			state = "拒绝退款";
			remark = "卖家不同意退款";
			break;
		case RETURN_DISAGREE:
			state = "不同意退货";
			remark = "卖家不同意退货";			
			break;
		case RETURN_WAIT_FOR_SEND:
			state = "退货待发货";
			remark = "卖家同意退货，买家需要发货";			
			break;
		case FAIL:
			state = "交易失败";
			remark = "取消订单，系统关闭订单";
			break;
		case RETURN_FINISH:
			state = "退货成功";
			remark = "退货成功，系统关闭订单";
			break;
		case CLOSE:
			state = "订单关闭";
			remark = "交易完成，系统关闭订单";
			break;
		case FINISH:
			state = "交易成功";
			remark = "";
			break;
		}
	}
	
	public String getState() {
		return state;
	}
	
	public String getRemark() {
		return remark;
	}
}
