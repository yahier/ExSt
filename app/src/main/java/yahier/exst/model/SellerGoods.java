package yahier.exst.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 卖家商品详细信息
 * @author ruilin
 */
public class SellerGoods<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 530820051186501415L;

	// private PopupWindow pop;

	public final static int STATE_OUT_SALE = 0;
	public final static int STATE_ON_SALE = 1;
	
	public long shopid;
	public String goodsid;
	public String fimgurl;
	public String goodsname; // 服务端拼写错误，暂时保持一致
	public String goodsinfourl;
	public int firstclassid;
	public int secondclassid;
	public int brandid;
	public int commentcount;
	public int isonshelf;
	// public int Recordflag;

	public int salecount;
	public int collectcount;
	public int stockcount;
	public String createtime;

	// ???
	public String goodsinfo;
	public String secondName;
	public String clientId;
	public float minPrice;
	public float maxPrice;
	public String qrimgurl; //二维码图片地址
	
	
	// 商品库存
	public T skulist;
	public ArrayList<GoodsSku> skuList;

	public ArrayList<String> infoimgurls;
	public ArrayList<String> bannerimgurls;
	public ArrayList<ImageDescriptionInfo> imginfos;
	
}
