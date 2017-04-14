package yahier.exst.model;

import java.io.Serializable;

public class MallInfo implements Serializable {
	int ordercount;
	int cartgoodscount;
	int ticketscount;
	float walletamount;
	float jindoucount;

	public int getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(int ordercount) {
		this.ordercount = ordercount;
	}

	public int getCartgoodscount() {
		return cartgoodscount;
	}

	public void setCartgoodscount(int cartgoodscount) {
		this.cartgoodscount = cartgoodscount;
	}

	public int getTicketscount() {
		return ticketscount;
	}

	public void setTicketscount(int ticketscount) {
		this.ticketscount = ticketscount;
	}

	public float getWalletamount() {
		return walletamount;
	}

	public void setWalletamount(float walletamount) {
		this.walletamount = walletamount;
	}

	public float getJindoucount() {
		return jindoucount;
	}

	public void setJindoucount(float jindoucount) {
		this.jindoucount = jindoucount;
	}
}
