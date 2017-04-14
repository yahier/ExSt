package yahier.exst.model.bangyibang;

import java.io.Serializable;

/**
 * 帮一帮 数据适配器的实体对象
 * @author 李全青
 * @time 2015-12-18
 * */
public class BangYiBangItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 39435830455091674L;
	/**
	 * 
	 */

	
	/**
	 * 
|    |  类型及范围 | 说明 |
| ----- |  ------- | ---------- |
|   issueid  | long|帮一帮id|
|   issuetitle  | string| 帮一帮标题|
|   issuedescription  | string| 帮一帮描述|
|   rewardbean  | int| 赏金|
|   issuetype  | string|行业|
|   publishtime  | int|发布时间|
|   isclose  | int|是否关闭 0-否，1-是|
|   publisher  | object| 帮一帮发布者对象|
|   isself  | int| 是否当前用户发布|0代表不是 1代表是
|   isallhandle   | int| 是否有新的推荐 0-是，1-否|
|   isin  | int|是否参与推荐，0-否,1-是|
|   shareinfo  | object| 推荐的信息|
|   recommendernamearray  | array|推荐人昵称数组 |
|   recommendcount  | int| 推荐人数量|
|   requestcount  | int| 收到的邀请数量 |
	 * */
	private long issueid;					//帮一帮id
	private String issuetitle;				//帮一帮标题
	private String issuedescription;			//帮一帮描述
	private int rewardbean;					//赏金数量
	private String issuetype;				//行业
	private long publishtime;				//发布时间
	private String publishTimeStr;
	private int isclose;					//需求是否关闭
	//public final static int iscloseYes
	private Publisher publisher ;			//帮一帮发布者对象
	private int isself;						//是否当前用户发布
	private int isallhandle;				//是否有新的推荐 0-是，1-否
	private int isin;						//是否参与推荐，0-否,1-是
	private ShareInfo shareinfo;			//推荐的信息
	private String[] recommendernamearray;	//推荐人昵称数组 
	private int recommendcount;				//推荐人数量|
	private int requestcount;				//收到的邀请数量 |
	private int issuestate;	                //帮一帮状态，0-已激活，10-已关闭，20-已完成
	private int userintype;                 //当前用户与帮一帮的关系，0-无关系，10-发布者，20-推荐无采纳，21-推荐已采纳，30-被推荐者
	
	private int hasundo;					//当前用户发起的帮一帮中是否有未处理推荐，0-否，1-是
	private boolean isShow = false;

	public long getIssueid() {
		return issueid;
	}


	public void setIssueid(long issueid) {
		this.issueid = issueid;
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


	public int getRewardbean() {
		return rewardbean;
	}


	public void setRewardbean(int rewardbean) {
		this.rewardbean = rewardbean;
	}


	public String getIssuetype() {
		return issuetype;
	}


	public void setIssuetype(String issuetype) {
		this.issuetype = issuetype;
	}

	public long getPublishtime() {
		return publishtime;
	}

	public void setPublishtime(long publishtime) {
		this.publishtime = publishtime;
	}

	public String getPublishTimeStr() {
		return publishTimeStr;
	}


	public void setPublishTimeStr(String publishTimeStr) {
		this.publishTimeStr = publishTimeStr;
	}


	public int getIsclose() {
		return isclose;
	}


	public void setIsclose(int isclose) {
		this.isclose = isclose;
	}


	public Publisher getPublisher() {
		return publisher;
	}


	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}


	public int getIsself() {
		return isself;
	}


	public void setIsself(int isself) {
		this.isself = isself;
	}


	public int getIsallhandle() {
		return isallhandle;
	}


	public void setIsallhandle(int isallhandle) {
		this.isallhandle = isallhandle;
	}


	public int getIsin() {
		return isin;
	}


	public void setIsin(int isin) {
		this.isin = isin;
	}


	public ShareInfo getShareinfo() {
		return shareinfo;
	}


	public void setShareinfo(ShareInfo shareinfo) {
		this.shareinfo = shareinfo;
	}


	public String[] getRecommendernamearray() {
		return recommendernamearray;
	}


	public void setRecommendernamearray(String[] recommendernamearray) {
		this.recommendernamearray = recommendernamearray;
	}


	public int getRecommendcount() {
		return recommendcount;
	}


	public void setRecommendcount(int recommendcount) {
		this.recommendcount = recommendcount;
	}


	public int getRequestcount() {
		return requestcount;
	}


	public void setRequestcount(int requestcount) {
		this.requestcount = requestcount;
	}
	
	public int getIssuestate() {
		return issuestate;
	}


	public void setIssuestate(int issuestate) {
		this.issuestate = issuestate;
	}


	public int getUserintype() {
		return userintype;
	}


	public void setUserintype(int userintype) {
		this.userintype = userintype;
	}


	public int getHasundo() {
		return hasundo;
	}


	public void setHasundo(int hasundo) {
		this.hasundo = hasundo;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("[issueid:"+issueid+",");
		sb.append("issuetitle:"+issuetitle+",");
		sb.append("issuedescription:"+issuedescription+",");
		sb.append("rewardbean:"+rewardbean+",");
		sb.append("issuetype:"+issuetype+",");
		sb.append("publishtime:"+publishtime+",");
		sb.append("isclose:"+isclose+",");
		sb.append("publisher:"+(publisher==null? "[]" : publisher.toString())+",");
		sb.append("isself:"+isself+",");
		sb.append("isallhandle:"+isallhandle+",");
		sb.append("isin:"+isin+",");
		sb.append("shareinfo:"+(shareinfo==null? "[]" : shareinfo.toString())+",");
		
		
		if (recommendernamearray==null||recommendernamearray.length<1) {
			sb.append("recommendernamearray:[],");
		}else {
			StringBuilder sb2 = new StringBuilder();
			sb2.append("[");
			for (int i = 0; i < recommendernamearray.length; i++) {
				sb2.append(recommendernamearray[i]+",");
			}
			sb2.append("]");
			sb.append("recommendernamearray:"+sb2.toString()+",");
			
		}
		sb.append("recommendcount:"+recommendcount+",");
		sb.append("requestcount:"+requestcount+"]");
		
		return sb.toString();
	}


	public boolean isShow() {
		return isShow;
	}


	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	
	
	

}
