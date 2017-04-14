package yahier.exst.model;

import java.io.Serializable;

/**
 * 缩减的商品信息
 *
 */
public class SimpleGoodsInfo implements Serializable {
	public String imgUrl;
	public String name;
	public String type;
	public float price;
	public int count;
}
