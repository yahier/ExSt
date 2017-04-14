package yahier.exst.model;

import java.io.Serializable;

public class GoodsCount implements Serializable{

	int salecount; //总销量
	int commentcount;//评论数
	int collectcount;//收藏数
	int sharecount;//分享数
	int stockcount;//商品库存数
	float commentscore;//商品评论分数

	public int getSalecount() {
		return salecount;
	}

	public void setSalecount(int salecount) {
		this.salecount = salecount;
	}

	public int getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(int commentcount) {
		this.commentcount = commentcount;
	}

	public int getCollectcount() {
		return collectcount;
	}

	public void setCollectcount(int collectcount) {
		this.collectcount = collectcount;
	}

	public int getSharecount() {
		return sharecount;
	}

	public void setSharecount(int sharecount) {
		this.sharecount = sharecount;
	}

	public int getStockcount() {
		return stockcount;
	}

	public void setStockcount(int stockcount) {
		this.stockcount = stockcount;
	}

	public float getCommentscore() {
		return commentscore;
	}

	public void setCommentscore(float commentscore) {
		this.commentscore = commentscore;
	}

}
