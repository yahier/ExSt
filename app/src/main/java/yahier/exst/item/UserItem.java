package yahier.exst.item;

import java.io.Serializable;

public class UserItem implements Serializable {
    private static final long serialVersionUID = -7312975783515292207L;
    long userid;
    String imgurl;
    // String nick;//动态列表中的
    String nickname;// 登录之后的
    String alias;
    int gender;
    int accounttype;
    public final static int accountTypePerson = 0;
    public final static int accountTypeSystem = 1;
    public final static int accountTypeTeam = 2;


    public final static int gender_unknow = -1; //未知
    public final static int gender_boy = 0;
    public final static int gender_girl = 1;
    // 以下是用户关系
    int relationtype;
    int relationflag; //用这个
    int issendfriendapply;// 是否发送过好友申请
    public final static int issendfriendapplyYes = 1;
    public final static int issendfriendapplyNo = 0;


    public final static String relationTypeDefault = "";
    public final static String relationTypeSelf = "自己";
    public final static String relationTypeMaster = "师傅";
    public final static String relationTypeStudent = "徒弟";
    public final static String relationTypeZhanglao = "长老";
    public final static String relationTypeQiuzhang = "酋长";
    public final static String relationTypeNormal = "普通";
    public final static String relationTypeFriend = "好友";

    public final static int type_levelflag_yes = 1;// 包含等级
    public final static int type_levelflag_no = 2;// 不包含等级

    public final static int type_tag_yes = 1;// 包含标签
    public final static int type_tag_no = 2;// 不包含标签

    public final static int addRelationTypeTudi = 1;
    public final static int addRelationTypeFriend = 2;

    int hasmaster;
    int isshutup;

    public final static int isshutupNo = 0;
    public final static int isshutupYes = 1;

    long birthday;
    int age;
    int gradeid;
    // 以下来自登录结果
    long masterid;// 师傅id
    String imgminurl;
    String imgurloriginal;
    String cityid;
    String cityname;
    String areacode;
    String telphone;
    String signature;
    int isheadmen;// 是否是酋长
    int richesscore;// 财富分数
    int contactsscore;// 人脉分数
    int recordstatus;// 个人状态
    String createtime;
    String phonetic;// 语音文件地址
    String openid;
    String invitecode;//邀请码
    String opentype;// 文档中写 微信
    int otherauthtype; // 0：没有授权账号 1：微信 2：QQ
    public final static int otherauthtypeNone = 0;
    public final static int otherauthtypeWeixin = 1;

    int haspaypassword; // 是否有支付密码
    public final static int haspaypasswordYes = 1;//有
    public final static int haspaypasswordNo = 0;//没有

    int roleid; // 角色，1-买家，3-商城卖家 4-茅台商城卖家
    String roleflag;
    //
    String imgmiddleurl;
    int certification;
    //身份认证
    public final static int certificationYes = 1;
    public final static int certificationNo = 0;
    long bigchiefuserid;// 大酋长
    //注册时保存用到
    String phonePrex;
    String loginPwd;
    String unionid;
    /**
     * 没有师傅
     */
    public final static int MatserNone = 0;


    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getRoleflag() {
        return roleflag;
    }

    public void setRoleflag(String roleflag) {
        this.roleflag = roleflag;
    }

    public int getOtherauthtype() {
        return otherauthtype;
    }

    public void setOtherauthtype(int otherauthtype) {
        this.otherauthtype = otherauthtype;
    }

    public int getHaspaypassword() {
        return haspaypassword;
    }

    public void setHaspaypassword(int haspaypassword) {
        this.haspaypassword = haspaypassword;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
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

    public int getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(int relationtype) {
        this.relationtype = relationtype;
    }

    public int getRelationflag() {
        return relationflag;
    }

    public void setRelationflag(int relationflag) {
        this.relationflag = relationflag;
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

    public int getGradeid() {
        return gradeid;
    }

    public void setGradeid(int gradeid) {
        this.gradeid = gradeid;
    }

    public long getMasterid() {
        return masterid;
    }

    public void setMasterid(long masterid) {
        this.masterid = masterid;
    }

    public String getImgminurl() {
        return imgminurl;
    }

    public void setImgminurl(String imgminurl) {
        this.imgminurl = imgminurl;
    }

    public String getImgurloriginal() {
        return imgurloriginal;
    }

    public void setImgurloriginal(String imgurloriginal) {
        this.imgurloriginal = imgurloriginal;
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

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getIsheadmen() {
        return isheadmen;
    }

    public void setIsheadmen(int isheadmen) {
        this.isheadmen = isheadmen;
    }

    public int getRichesscore() {
        return richesscore;
    }

    public void setRichesscore(int richesscore) {
        this.richesscore = richesscore;
    }

    public int getContactsscore() {
        return contactsscore;
    }

    public void setContactsscore(int contactsscore) {
        this.contactsscore = contactsscore;
    }

    public int getRecordstatus() {
        return recordstatus;
    }

    public void setRecordstatus(int recordstatus) {
        this.recordstatus = recordstatus;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpentype() {
        return opentype;
    }

    public void setOpentype(String opentype) {
        this.opentype = opentype;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getImgmiddleurl() {
        return imgmiddleurl;
    }

    public void setImgmiddleurl(String imgmiddleurl) {
        this.imgmiddleurl = imgmiddleurl;
    }

    public int getIsshutup() {
        return isshutup;
    }

    public void setIsshutup(int isshutup) {
        this.isshutup = isshutup;
    }

    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }

    public long getBigchiefuserid() {
        return bigchiefuserid;
    }

    public void setBigchiefuserid(long bigchiefuserid) {
        this.bigchiefuserid = bigchiefuserid;
    }


    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getPhonePrex() {
        return phonePrex;
    }

    public void setPhonePrex(String phonePrex) {
        this.phonePrex = phonePrex;
    }


    public String getInvitecode() {
        return invitecode;
    }

    public void setInvitecode(String invitecode) {
        this.invitecode = invitecode;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public int getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(int accounttype) {
        this.accounttype = accounttype;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
