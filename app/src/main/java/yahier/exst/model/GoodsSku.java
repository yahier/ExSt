package yahier.exst.model;

import java.io.Serializable;

public class GoodsSku implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1757416473171063868L;
	public long skuid = 0;
	public String skuname;
	public float outprice;
	public float realprice;
	public int stockcount;
	public int recordflag = 0;
}
