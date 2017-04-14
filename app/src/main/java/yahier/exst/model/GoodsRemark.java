package yahier.exst.model;

import java.io.Serializable;

/**
 * 商品评论
 * 
 * @author lenovo
 * 
 */
public class GoodsRemark implements Serializable {
	String Goodsid;
	long Userid;
	String Nickname;
	String Content;
	long Createtime;
	int Score;
	long Skuid;
	long Orderid;

	public String getGoodsid() {
		return Goodsid;
	}

	public void setGoodsid(String goodsid) {
		Goodsid = goodsid;
	}

	public long getUserid() {
		return Userid;
	}

	public void setUserid(long userid) {
		Userid = userid;
	}

	public String getNickname() {
		return Nickname;
	}

	public void setNickname(String nickname) {
		Nickname = nickname;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public long getCreatetime() {
		return Createtime;
	}

	public void setCreatetime(long createtime) {
		Createtime = createtime;
	}

	public int getScore() {
		return Score;
	}

	public void setScore(int score) {
		Score = score;
	}

	public long getSkuid() {
		return Skuid;
	}

	public void setSkuid(long skuid) {
		Skuid = skuid;
	}

	public long getOrderid() {
		return Orderid;
	}

	public void setOrderid(long orderid) {
		Orderid = orderid;
	}

}
