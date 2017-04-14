package yahier.exst.model.bangyibang;

import java.io.Serializable;
/**
 * 推荐的信息
 * */
public class ShareInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5390320788046625700L;

	/**
	 * 
|    shareuserinfo  | object| 推荐的人对象|
|    isselected  | int| 是否采纳 0-否,1-是|
|    sharereason  | string| 推荐理由|
|    recommendtime  | int|推荐发布时间|
|    selecttime  | int|采纳时间|
	 */
	

	private ShareUserInfo shareuserinfo;// 推荐的人对象
    
    private int isselected;				//是否采纳 0-否,1-是
    private String sharereason;			//推荐理由
    private long recommendtime;			//推荐发布时间
    private long selecttime;			//采纳时间
    
	public ShareUserInfo getShareuserinfo() {
		return shareuserinfo;
	}
	public void setShareuserinfo(ShareUserInfo shareuserinfo) {
		this.shareuserinfo = shareuserinfo;
	}
	public int getIsselected() {
		return isselected;
	}
	public void setIsselected(int isselected) {
		this.isselected = isselected;
	}
	public String getSharereason() {
		return sharereason;
	}
	public void setSharereason(String sharereason) {
		this.sharereason = sharereason;
	}
	public long getRecommendtime() {
		return recommendtime;
	}
	public void setRecommendtime(long recommendtime) {
		this.recommendtime = recommendtime;
	}
	public long getSelecttime() {
		return selecttime;
	}
	public void setSelecttime(long selecttime) {
		this.selecttime = selecttime;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("[shareuserinfo:"+(shareuserinfo==null? "null" :shareuserinfo.toString())+",");
		sb.append("isselected:"+isselected+",");
		sb.append("sharereason:"+sharereason+",");
		sb.append("recommendtime:"+recommendtime+",");
		sb.append("selecttime:"+selecttime+"]");
		return sb.toString();
	}
    
    

}
