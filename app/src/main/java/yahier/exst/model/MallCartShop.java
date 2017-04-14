package yahier.exst.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车item
 * 
 * @author lenovo
 * 
 */

public class MallCartShop implements Serializable {

	long shopid;
	String shopname;
	String alias;
	List<MallCartGoods> cartgoods;
	// 以下是app端生成的值。
	boolean isSelected;
	float totalamount;
	float realamount;

	public MallCartShop(){

	}

	public MallCartShop(MallCartShop shop){
		if (shop != null){
			shopid = shop.getShopid();
			shopname = shop.getShopname();
			alias = shop.getAlias();
			cartgoods = new ArrayList<>();
			cartgoods.addAll(shop.cartgoods);
			isSelected = shop.isSelected();
			totalamount = shop.getTotalamount();
			realamount = shop.getRealamount();
		}
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public long getShopid() {
		return shopid;
	}

	public void setShopid(long shopid) {
		this.shopid = shopid;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public List<MallCartGoods> getCartgoods() {
		return cartgoods;
	}

	public void setCartgoods(List<MallCartGoods> cartgoods) {
		this.cartgoods = cartgoods;
	}

	public float getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(float totalamount) {
		this.totalamount = totalamount;
	}

	public float getRealamount() {
		return realamount;
	}

	public void setRealamount(float realamount) {
		this.realamount = realamount;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;

//		if(cartgoods == null || cartgoods.size() == 0)
//			return;

//		for(int i = 0 ; i < cartgoods.size() ; i++){
//			MallCartGoods info = cartgoods.get(i);
//			info.setSelected(isSelected);
//			cartgoods.set(i, info);
//		}
	}

}
