package yahier.exst.model;

import java.io.Serializable;

/**
 * 商品评论
 * @author ruilin
 *
 */
public class Comment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2781482651835044136L;
    public int id; //评论id
	public long goodsid;
	public String goodsname;
    public String nickname;
    public String content;
    public long createtime;
    public int score;
    public long skuid;
    public String skuname;
    public long orderid;
    public CommentUser user;
    
    
}
