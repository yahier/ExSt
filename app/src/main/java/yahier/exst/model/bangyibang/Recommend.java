package yahier.exst.model.bangyibang;

import java.io.Serializable;

/**
 * 收到推荐的列表item
 * 
 * @author 李全青
 * @time 2015-12-21
 * */
public class Recommend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8876554994443031549L;

	private String issuedescrition; // 问题描述
	private int rewardbean; //赏金
	private int isselected; // 是否采纳 0-否,1-是
	private int selecttime; // 采纳时间
	private String selectTimeStr; // 采纳时间
	private int createtime; // 帮一帮发布时间
	private String createTimeStr; // 帮一帮发布时间
	private int recommendid; // 推荐id
	private long recommenderuserid; // 推荐人id
	private String recommendernickname; // 推荐人昵称
	private String recommenderimgminurl; // 推荐人头像小图
	private String recommenderimgmiddleurl; // 推荐人头像大图
	private int recommendtime; // 发布推荐时间
	private String recommendTimeStr; // 发布推荐时间
	private int iscontacted; // 是否已联系，0-否,1-是
	private int contacttime; // 联系时间
	private String contactTimeStr; // 联系时间
	private String shareusername; // 推荐的人昵称
	private long shareuserid; // 推荐的人id
	private String shareimgminurl; // 推荐的人头像小图
	private String shareimgmiddleurl; // 推荐的人头像大图
	private int isclose; // 是否关闭，0-否，1-是
	private String sharereason; // 新增推荐理由

	public Recommend() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getIssuedescrition() {
		return issuedescrition;
	}

	public void setIssuedescrition(String issuedescrition) {
		this.issuedescrition = issuedescrition;
	}
	
	public int getRewardbean() {
		return rewardbean;
	}

	public void setRewardbean(int rewardbean) {
		this.rewardbean = rewardbean;
	}

	public int getIsselected() {
		return isselected;
	}

	public void setIsselected(int isselected) {
		this.isselected = isselected;
	}

	public int getSelecttime() {
		return selecttime;
	}

	public void setSelecttime(int selecttime) {
		this.selecttime = selecttime;
	}

	public String getSelectTimeStr() {
		return selectTimeStr;
	}

	public void setSelectTimeStr(String selectTimeStr) {
		this.selectTimeStr = selectTimeStr;
	}

	public int getCreatetime() {
		return createtime;
	}

	public void setCreatetime(int createtime) {
		this.createtime = createtime;
	}

	public int getRecommendid() {
		return recommendid;
	}

	public void setRecommendid(int recommendid) {
		this.recommendid = recommendid;
	}

	public long getRecommenderuserid() {
		return recommenderuserid;
	}

	public void setRecommenderuserid(long recommenderuserid) {
		this.recommenderuserid = recommenderuserid;
	}

	public String getRecommendernickname() {
		return recommendernickname;
	}

	public void setRecommendernickname(String recommendernickname) {
		this.recommendernickname = recommendernickname;
	}

	public String getRecommenderimgminurl() {
		return recommenderimgminurl;
	}

	public void setRecommenderimgminurl(String recommenderimgminurl) {
		this.recommenderimgminurl = recommenderimgminurl;
	}

	public String getRecommenderimgmiddleurl() {
		return recommenderimgmiddleurl;
	}

	public void setRecommenderimgmiddleurl(String recommenderimgmiddleurl) {
		this.recommenderimgmiddleurl = recommenderimgmiddleurl;
	}

	public int getRecommendtime() {
		return recommendtime;
	}

	public void setRecommendtime(int recommendtime) {
		this.recommendtime = recommendtime;
	}

	public int getIscontacted() {
		return iscontacted;
	}

	public void setIscontacted(int iscontacted) {
		this.iscontacted = iscontacted;
	}

	public int getContacttime() {
		return contacttime;
	}

	public void setContacttime(int contacttime) {
		this.contacttime = contacttime;
	}

	public String getShareusername() {
		return shareusername;
	}

	public void setShareusername(String shareusername) {
		this.shareusername = shareusername;
	}

	public long getShareuserid() {
		return shareuserid;
	}

	public void setShareuserid(long shareuserid) {
		this.shareuserid = shareuserid;
	}

	public String getShareimgminurl() {
		return shareimgminurl;
	}

	public void setShareimgminurl(String shareimgminurl) {
		this.shareimgminurl = shareimgminurl;
	}

	public String getShareimgmiddleurl() {
		return shareimgmiddleurl;
	}

	public void setShareimgmiddleurl(String shareimgmiddleurl) {
		this.shareimgmiddleurl = shareimgmiddleurl;
	}

	public int getIsclose() {
		return isclose;
	}

	public void setIsclose(int isclose) {
		this.isclose = isclose;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getRecommendTimeStr() {
		return recommendTimeStr;
	}

	public void setRecommendTimeStr(String recommendTimeStr) {
		this.recommendTimeStr = recommendTimeStr;
	}

	public String getContactTimeStr() {
		return contactTimeStr;
	}

	public void setContactTimeStr(String contactTimeStr) {
		this.contactTimeStr = contactTimeStr;
	}

	public String getSharereason() {
		return sharereason;
	}

	public void setSharereason(String sharereason) {
		this.sharereason = sharereason;
	}

}
