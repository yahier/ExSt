package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告用户信息
 * Created by Administrator on 2016/9/27 0027.
 */

public class AdUserItem implements Serializable {

    /**
     * userid : 12313132
     * nickname : abc
     * imgurl : abc.cc
     * certification : 0
     */

    private long userid; //用户id
    private String nickname; //用户昵称
    private String imgurl; //用户头像
    private int certification; //是否认证用户，0-否，1-是
    private int isdefaultimgurl; //是否默认头像，0-否，1-是

    public static final int defaultImg = 1; //默认头像

    public int getIsdefaultimgurl() {
        return isdefaultimgurl;
    }

    public void setIsdefaultimgurl(int isdefaultimgurl) {
        this.isdefaultimgurl = isdefaultimgurl;
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

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }
}
