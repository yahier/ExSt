package yahier.exst.model.bangyibang;

import java.io.Serializable;
/**
 * 推荐的人对象
 * */
public class ShareUserInfo implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -8173918752284347156L;

	private long userid;
	private String nickname;
	private String imgminurl;
	private String imgmiddleurl;
	private int gender;
	private long birthday;
	private int age;
	private String cityname;

	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getImgminurl() {
		return imgminurl;
	}
	public void setImgminurl(String imgminurl) {
		this.imgminurl = imgminurl;
	}
	public String getImgmiddleurl() {
		return imgmiddleurl;
	}
	public void setImgmiddleurl(String imgmiddleurl) {
		this.imgmiddleurl = imgmiddleurl;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public long getBirthday() {
		return birthday;
	}
	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("[userid:"+userid+",");
		sb.append("nickname:"+nickname+",");
		sb.append("imgminurl:"+imgminurl+",");
		sb.append("imgmiddleurl:"+imgmiddleurl+",");
		sb.append("gender:"+gender+",");
		sb.append("birthday:"+birthday+",");
		sb.append("age:"+age+"]");

		return sb.toString();
	}


}
