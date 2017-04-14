package yahier.exst.model;

import java.io.Serializable;
import java.util.List;

public class MallAll implements Serializable {
	List<Banner> bannerview;
	List<GoodsClass> mallclassview;
	List<HomeLikeItem> hotproductview;
	List<HomeLikeItem> likeproductview;
	MallInfo mallinfoview;

	public List<Banner> getBannerview() {
		return bannerview;
	}

	public void setBannerview(List<Banner> bannerview) {
		this.bannerview = bannerview;
	}

	public List<GoodsClass> getMallclassview() {
		return mallclassview;
	}

	public void setMallclassview(List<GoodsClass> mallclassview) {
		this.mallclassview = mallclassview;
	}

	public List<HomeLikeItem> getHotproductview() {
		return hotproductview;
	}

	public void setHotproductview(List<HomeLikeItem> hotproductview) {
		this.hotproductview = hotproductview;
	}

	public List<HomeLikeItem> getLikeproductview() {
		return likeproductview;
	}

	public void setLikeproductview(List<HomeLikeItem> likeproductview) {
		this.likeproductview = likeproductview;
	}

	public MallInfo getMallinfoview() {
		return mallinfoview;
	}

	public void setMallinfoview(MallInfo mallinfoview) {
		this.mallinfoview = mallinfoview;
	}

}
