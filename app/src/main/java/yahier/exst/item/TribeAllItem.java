package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class TribeAllItem implements Serializable {
	TribeUserView userview;
	UserCount countview;
	List<TribeGift> giftsview;
	List<Photo> userphotoview;
	List<UserItem> proxysigninview;
	int issignin;
	int userlinksnum;
	int userstatusesnum;
	Statuses lateststatusesview;
	List<LinkBean> linksview;
	DynamicNew tribelatestview;
	int userphotocount;

	public DynamicNew getTribelatestview() {
		return tribelatestview;
	}

	public void setTribelatestview(DynamicNew tribelatestview) {
		this.tribelatestview = tribelatestview;
	}

	public TribeUserView getUserview() {
		return userview;
	}

	public void setUserview(TribeUserView userview) {
		this.userview = userview;
	}

	public UserCount getCountview() {
		return countview;
	}

	public void setCountview(UserCount countview) {
		this.countview = countview;
	}

	public List<TribeGift> getGiftsview() {
		return giftsview;
	}

	public void setGiftsview(List<TribeGift> giftsview) {
		this.giftsview = giftsview;
	}

	public int getUserlinksnum() {
		return userlinksnum;
	}

	public void setUserlinksnum(int userlinksnum) {
		this.userlinksnum = userlinksnum;
	}

	public int getUserstatusesnum() {
		return userstatusesnum;
	}

	public void setUserstatusesnum(int userstatusesnum) {
		this.userstatusesnum = userstatusesnum;
	}

	public List<UserItem> getProxysigninview() {
		return proxysigninview;
	}

	public void setProxysigninview(List<UserItem> proxysigninview) {
		this.proxysigninview = proxysigninview;
	}

	public List<Photo> getUserphotoview() {
		return userphotoview;
	}

	public void setUserphotoview(List<Photo> userphotoview) {
		this.userphotoview = userphotoview;
	}

	public Statuses getLateststatusesview() {
		return lateststatusesview;
	}

	public void setLateststatusesview(Statuses lateststatusesview) {
		this.lateststatusesview = lateststatusesview;
	}

	public List<LinkBean> getLinksview() {
		return linksview;
	}

	public void setLinksview(List<LinkBean> linksview) {
		this.linksview = linksview;
	}

	public int getIssignin() {
		return issignin;
	}

	public void setIssignin(int issignin) {
		this.issignin = issignin;
	}

	public int getUserphotocount() {
		return userphotocount;
	}

	public void setUserphotocount(int userphotocount) {
		this.userphotocount = userphotocount;
	}
}
