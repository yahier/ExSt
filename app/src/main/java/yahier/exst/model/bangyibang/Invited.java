package yahier.exst.model.bangyibang;

import java.io.Serializable;
/**
 * 收到邀请的列表item
 * @author 李全青
 * @time 2015-12-18
 * */
public class Invited implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8876554994443031549L;

	private long publisheruserid;				//发布者id
	private String publisherusername;			//发布者昵称
	private String publisherimgminurl;			//发布者头像小图
	private String publisherimgmiddleurl;		//发布者头像大图
	private String issuetitle;					//帮一帮标题
	private String issuedescription;			//帮一帮描述
	private int	publishtime;					//帮一帮发布时间
	private String	publishTimeStr;				//帮一帮发布时间
	private String  issuetype;					//行业
	private int recommendid;					//推荐id
	private long recommenderuserid;				//推荐人id
	private String recommenderusername;			//推荐人昵称
	private String recommenderimgminurl;		//推荐人头像小图
	private String recommenderimgmiddleurl;		//推荐人头像大图
	private int	recommendtime;					//推荐时间
	private String	recommendTimeStr;			//推荐时间
	private String	sharereason;				//推荐说明
	private int	iscontacted;					//是否联系，0-否，1-是
	public final static int iscontactedNo=0;
	public final static int iscontactedYes=1;
	
	private int	contacttime;					//联系时间
	private String	contactTimeStr;				//联系时间
	private int	isclose;						//是否关闭，0-否，1-是
	private int	isselected;						//是否采纳，0-否，1-是
	
	
	public Invited() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getPublisheruserid() {
		return publisheruserid;
	}

	public void setPublisheruserid(long publisheruserid) {
		this.publisheruserid = publisheruserid;
	}

	public String getPublisherusername() {
		return publisherusername;
	}

	public void setPublisherusername(String publisherusername) {
		this.publisherusername = publisherusername;
	}

	public String getPublisherimgminurl() {
		return publisherimgminurl;
	}

	public void setPublisherimgminurl(String publisherimgminurl) {
		this.publisherimgminurl = publisherimgminurl;
	}

	public String getPublisherimgmiddleurl() {
		return publisherimgmiddleurl;
	}

	public void setPublisherimgmiddleurl(String publisherimgmiddleurl) {
		this.publisherimgmiddleurl = publisherimgmiddleurl;
	}

	public String getIssuetitle() {
		return issuetitle;
	}

	public void setIssuetitle(String issuetitle) {
		this.issuetitle = issuetitle;
	}

	public String getIssuedescription() {
		return issuedescription;
	}

	public void setIssuedescription(String issuedescription) {
		this.issuedescription = issuedescription;
	}

	public int getPublishtime() {
		return publishtime;
	}

	public void setPublishtime(int publishtime) {
		this.publishtime = publishtime;
	}

	public String getIssuetype() {
		return issuetype;
	}

	public void setIssuetype(String issuetype) {
		this.issuetype = issuetype;
	}

	public long getRecommenderuserid() {
		return recommenderuserid;
	}

	public void setRecommenderuserid(long recommenderuserid) {
		this.recommenderuserid = recommenderuserid;
	}

	public String getRecommenderusername() {
		return recommenderusername;
	}

	public void setRecommenderusername(String recommenderusername) {
		this.recommenderusername = recommenderusername;
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

	public String getSharereason() {
		return sharereason;
	}

	public void setSharereason(String sharereason) {
		this.sharereason = sharereason;
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

	public int getIsclose() {
		return isclose;
	}

	public void setIsclose(int isclose) {
		this.isclose = isclose;
	}

	public int getIsselected() {
		return isselected;
	}

	public void setIsselected(int isselected) {
		this.isselected = isselected;
	}


	public String getPublishTimeStr() {
		return publishTimeStr;
	}

	public void setPublishTimeStr(String publishTimeStr) {
		this.publishTimeStr = publishTimeStr;
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

	public int getRecommendid() {
		return recommendid;
	}

	public void setRecommendid(int recommendid) {
		this.recommendid = recommendid;
	}

	
}
