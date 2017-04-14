package yahier.exst.model;

import java.io.Serializable;
import java.util.List;

public class MallCart implements Serializable{

	long userid;
	List<MallCartShop> cartshops;

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public List<MallCartShop> getCartshops() {
		return cartshops;
	}

	public void setCartshops(List<MallCartShop> cartshops) {
		this.cartshops = cartshops;
	}

}
