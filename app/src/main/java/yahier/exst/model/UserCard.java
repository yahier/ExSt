package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/1.
 */

public class UserCard implements Serializable {

    public UserBaseInfoView userbaseinfoview;
    public UserExView userexview;

    public BigChiefInfo bigchiefinfo;
    public TopBigChiefInfo topbigchiefinfo;
    public UserRelationView userrelationview;
    public CBigChiefInfo cbigchiefinfo;

    public static class UserBaseInfoView implements Serializable {
        public long userid;
        public String nickname;
        public String imgurl;
        public int gender;
        public long birthday;
        public int age;
        public String invitecode;
        public int roleflag;
        public int iscertification;
        public String signature;
        public String cityname;
    }

    public static class UserExView implements Serializable {
        public int familycount;
        public int tudicount;
    }

    public static class BigChiefInfo implements Serializable {
        public String bigchiefusername;
        public long bigchiefuserid;
        public int zlevel;
        public String imgurl;
    }

    public static class TopBigChiefInfo implements Serializable {
        public String bigchiefusername;
        public long bigchiefuserid;
        public int zlevel;
        public String imgurl;
    }

    public static class CBigChiefInfo implements Serializable {
        public String bigchiefusername;
        public long bigchiefuserid;
        public int zlevel;
        public String imgurl;
    }

    public static class UserRelationView implements Serializable {
        public MasterView masterview;
        public ElderView elderview;
        public HeadmenView headmenview;
    }

    public static class MasterView implements Serializable {
        public long userid;
        public String nickname;
        public String imgurl;
        public int certification;
    }

    public static class ElderView implements Serializable {
        public long userid;
        public String nickname;
        public String imgurl;
        public int certification;
    }

    public static class HeadmenView implements Serializable {
        public long userid;
        public String nickname;
        public String imgurl;
        public int certification;
    }


}
