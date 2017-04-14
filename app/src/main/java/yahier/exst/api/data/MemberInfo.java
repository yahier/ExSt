package yahier.exst.api.data;

import java.io.Serializable;

/**
 * 直播间在线成员实体
 * Created by Administrator on 2016/3/8 0008.
 */
public class MemberInfo implements Serializable {
//    "memberid": 14537400264617,
//            "membertype": 2,
//            "nickname": "7701",
//            "imgurl": "http://120.25.150.37:10089//user/14537400264617/user/head/ori/1454417119363.jpg",
//            "gender": 0,
//            "cityid": "020",
//            "cityname": "广州市",
//            "age": 0,
//            "birthday": 1453740026,
//            "likecount": 2
    private long memberid;
    private int membertype; //成员类型 0：普通 1：嘉宾 2：房主
    private String nickname;
    private String imgurl;
    private int gender;
    private String cityid;
    private String cityname;
    private int age;
    private long birthday;
    private int likecount;

    public long getMemberid() {
        return memberid;
    }

    public void setMemberid(long memberid) {
        this.memberid = memberid;
    }

    public int getMembertype() {
        return membertype;
    }

    public void setMembertype(int membertype) {
        this.membertype = membertype;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }
}
