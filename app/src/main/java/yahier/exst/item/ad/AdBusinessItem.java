package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 商务合作列表item
 * Created by Administrator on 2016/10/1 0001.
 */

public class AdBusinessItem implements Serializable {

    private int id;//主键Id，用于获取分页
    private long userid;//申请人Id
    private int isfriend;//是否是好友
    private long applyuserid;//申请人用户id
    private String applynickname;//申请人用户名
    private String applyimgurl;//申请人头像
    private String applyname;//申请填写的姓名
    private String applyareacode;//申请号码区号
    private String applyphone;//申请电话
    private String applycontent;//申请内容
    private long createtime;//申请时间

    private String sourcead ; //来自
    private String cotype ; //合作类型
    private String cotypename  ; //合作类型
    private int applytype; //申请类型：1-站内，2-站外（站外不显示加好友，头像不可以点击跳转）

    public static final int type_app = 1; //1-站内

    public int getApplytype() {
        return applytype;
    }

    public void setApplytype(int applytype) {
        this.applytype = applytype;
    }

    public String getSourcead() {
        return sourcead;
    }

    public void setSourcead(String sourcead) {
        this.sourcead = sourcead;
    }

    public String getCotype() {
        return cotype;
    }

    public void setCotype(String cotype) {
        this.cotype = cotype;
    }

    public String getCotypename() {
        return cotypename;
    }

    public void setCotypename(String cotypename) {
        this.cotypename = cotypename;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(int isfriend) {
        this.isfriend = isfriend;
    }

    public long getApplyuserid() {
        return applyuserid;
    }

    public void setApplyuserid(long applyuserid) {
        this.applyuserid = applyuserid;
    }

    public String getApplynickname() {
        return applynickname;
    }

    public void setApplynickname(String applynickname) {
        this.applynickname = applynickname;
    }

    public String getApplyimgurl() {
        return applyimgurl;
    }

    public void setApplyimgurl(String applyimgurl) {
        this.applyimgurl = applyimgurl;
    }

    public String getApplyname() {
        return applyname;
    }

    public void setApplyname(String applyname) {
        this.applyname = applyname;
    }

    public String getApplyareacode() {
        return applyareacode;
    }

    public void setApplyareacode(String applyareacode) {
        this.applyareacode = applyareacode;
    }

    public String getApplyphone() {
        return applyphone;
    }

    public void setApplyphone(String applyphone) {
        this.applyphone = applyphone;
    }

    public String getApplycontent() {
        return applycontent;
    }

    public void setApplycontent(String applycontent) {
        this.applycontent = applycontent;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
