package yahier.exst.item.im;

/**
 * Created by lenovo on 2016/6/2.
 * 专为IM所有
 */
public class IMAccount {
    String userid;
    String imgurl;
    String nickname;// 登录之后的
    int certification;
    String alias;
    int type;
    int peopleNum;
    //int type, String targetId, String name, String imgUrl, int certification, String alias

    public IMAccount() {

    }

    public IMAccount(String userid, String nickname, String imgurl, int certification) {
        this.userid = userid;
        this.imgurl = imgurl;
        this.nickname = nickname;
        this.certification = certification;
    }

    public IMAccount(int type, String userid, String nickname, String imgurl, int certification, String alias) {
        this.type = type;
        this.userid = userid;
        this.imgurl = imgurl;
        this.nickname = nickname;
        this.certification = certification;
        this.alias = alias;
    }


    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public int getPeopleNum() {
        return peopleNum;
    }
}
