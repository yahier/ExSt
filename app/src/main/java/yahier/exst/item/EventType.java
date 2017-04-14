package yahier.exst.item;

import java.io.Serializable;

public class EventType implements Serializable {
	public final static int TYPE_DEFAULT = 0;
	public final static int TYPE_CANCEL_ORDER = 1;		// 取消订单
	public final static int TYPE_MALL_NUM_CHANGE = 2;	//付款后 也会发送
	public final static int TYPE_CART_CHANGE = 3;		// 购物车数量改变
	public final static int TYPE_MONEY_CHANGE = 4;		// 钱包数量改变
	
	public final static int TYPE_REFRESH_ALL_ORDER_LIST = 5;//刷新全部订单列表
	public final static int TYPE_REFRESH_ORDER_5 = 6;	//刷新售后订单
	
	public final static int TYPE_REFRESH_ORDER_LIST = 7;	//根据条件刷新相应列表
	public final static int TYPE_BUY_addToCart = 8;	//在商品选择页面 MallTypeChoiceAct 购买或者加入购物车
	/**关闭商品详情页面*/
	public final static int TYPE_FINISH_GOODS_DETAIL = 9; //

	public final static int TYPE_REFRESH_INTEGRAL = 10; //刷新积分商城个人积分
	
	int type;
	int param;

	public EventType(int type) {
		this.type = type;
	}
	
	public EventType(int type, int param) {
		this.type = type;
		this.param = param;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setParam(int param) {
		this.param = param;
	}
	
	public int getParam() {
		return param;
	}
}
