package yahier.exst.item;

import java.io.Serializable;

public class Rank1Item implements Serializable {
	long busid;
	String name;
	String alias;
	int rankings;
	String points;
	String imgurl;
	/**
	 * 性别
	 */
	String ex1;
	public static final String genderBoy = "0";
	public static final String genderGirl = "1";
	/**
	 * 年龄
	 */
	String ex2;
	/**
	 * 城市
	 */
	String ex3;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public long getBusid() {
		return busid;
	}

	public void setBusid(long busid) {
		this.busid = busid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRankings() {
		return rankings;
	}

	public void setRankings(int rankings) {
		this.rankings = rankings;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getEx1() {
		return ex1;
	}

	public void setEx1(String ex1) {
		this.ex1 = ex1;
	}

	public String getEx2() {
		return ex2;
	}

	public void setEx2(String ex2) {
		this.ex2 = ex2;
	}

	public String getEx3() {
		return ex3;
	}

	public void setEx3(String ex3) {
		this.ex3 = ex3;
	}

}
