package yahier.exst.item;

import java.io.Serializable;

/**
 * 用户等级 人脉等级和 财富等级
 *
 * @author lenovo
 *
 */
public class Level implements Serializable {
	long userid;// 用户ID
	String levelrichname;// string 财富等级
	String levelrichtitle;// int 财富称号
	int levelrichscore;// int 财富分数
	int levelrichcur; // int 当前级别所得分数
	int levelrichnext; // int 距离下一级财富分数

	String levelcontactname;// string 人脉等级
	String levelcontacttitle; // int 人脉称号
	int levelcontactscore;// //int 人脉分数
	int levelcontactcur;// int 当前级别所得分数
	int levelcontactnext;// int 距离下一级人脉分数

	public final static int type_wealth = 1;// 财富等级
	public final static int type_people = 2;// 人脉等级
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getLevelrichname() {
		return levelrichname;
	}
	public void setLevelrichname(String levelrichname) {
		this.levelrichname = levelrichname;
	}
	public String getLevelrichtitle() {
		return levelrichtitle;
	}
	public void setLevelrichtitle(String levelrichtitle) {
		this.levelrichtitle = levelrichtitle;
	}
	public int getLevelrichscore() {
		return levelrichscore;
	}
	public void setLevelrichscore(int levelrichscore) {
		this.levelrichscore = levelrichscore;
	}
	public int getLevelrichcur() {
		return levelrichcur;
	}
	public void setLevelrichcur(int levelrichcur) {
		this.levelrichcur = levelrichcur;
	}
	public int getLevelrichnext() {
		return levelrichnext;
	}
	public void setLevelrichnext(int levelrichnext) {
		this.levelrichnext = levelrichnext;
	}
	public String getLevelcontactname() {
		return levelcontactname;
	}
	public void setLevelcontactname(String levelcontactname) {
		this.levelcontactname = levelcontactname;
	}
	public String getLevelcontacttitle() {
		return levelcontacttitle;
	}
	public void setLevelcontacttitle(String levelcontacttitle) {
		this.levelcontacttitle = levelcontacttitle;
	}
	public int getLevelcontactscore() {
		return levelcontactscore;
	}
	public void setLevelcontactscore(int levelcontactscore) {
		this.levelcontactscore = levelcontactscore;
	}
	public int getLevelcontactcur() {
		return levelcontactcur;
	}
	public void setLevelcontactcur(int levelcontactcur) {
		this.levelcontactcur = levelcontactcur;
	}
	public int getLevelcontactnext() {
		return levelcontactnext;
	}
	public void setLevelcontactnext(int levelcontactnext) {
		this.levelcontactnext = levelcontactnext;
	}





}
