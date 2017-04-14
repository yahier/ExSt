package yahier.exst.item;

import java.io.Serializable;

public class Relation implements Serializable {
    int isattention;// 关注状态
    int isshield;// 屏蔽状态
    //int relationtype;// 这个字段的值表示的意思 参照UserItem
    int relationflag;
    int isfans;//是否关注了我
    int isconceal;// 是否不让该用户看我的动态

    public final static int isattention_yes = 1;
    public final static int isattention_no = 0;
    public final static int isshield_yes = 1;
    public final static int isshield_no = 0;
    public final static int isbeattention_yes = 1;//关注了我
    public final static int isbeattention_no = 0;//没有关注我
    public final static int isconceal_yes = 1;//不让他看我的动态
    public final static int isconceal_no = 0;//让我看我的动态

    //输出关系类型。仅作参考
//    public final static int relation_type_default = 0;  //无关系
//    public final static int relation_type_self = 1;     //自己
//    public final static int relation_type_friend = 2;   //好友
//    public final static int relation_type_student_noFriend = 4;  //徒弟非好友
//    public final static int relation_type_studentAndFriend = 6;  //徒弟且好友
//    public final static int relation_type_master_notFriend = 8;   //师傅非好友
//    public final static int relation_type_masterAndFriend = 10;   //师傅且好友
//    public final static int relation_type_zhanglaoNoFriend = 16; //长老非好友
//    public final static int relation_type_zhanglaoAndFriend = 18; //长老且好友
//    public final static int relation_type_qiuzhangNoFriend = 32; //酋长非好友
//    public final static int relation_type_qiuzhangAndFriend = 34; //酋长且好友
    //以下是基础权值说明、
    public final static int relation_type_default = 0;  //无关系
    public final static int relation_type_self = 1;     //自己
    public final static int relation_type_friend = 2;   //好友
    public final static int relation_type_student = 4;  //徒弟非好友
    public final static int relation_type_master = 8;   //师傅
    public final static int relation_type_zhanglao = 16; //长老
    public final static int relation_type_qiuzhang = 32; //酋长

    /**
     * 是否是师傅
     *
     * @param relationflag
     */
    public static boolean isMaster(int relationflag) {
        if ((relation_type_master & relationflag) == relation_type_master) {
            return true;
        }
        return false;
    }

    /**
     * 是否是徒弟
     *
     * @param relationflag
     */
    public static boolean isStu(int relationflag) {
        if ((relation_type_student & relationflag) == relation_type_student) {
            return true;
        }
        return false;
    }

    /**
     * 是否是师傅或者徒弟
     *
     * @param relationflag
     */
    public static boolean isMasterOrStu(int relationflag) {
        if (isMaster(relationflag) || isStu(relationflag)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是好友
     *
     * @param relationflag
     */
    public static boolean isFriend(int relationflag) {
        if ((relation_type_friend & relationflag) == relation_type_friend) {
            return true;
        }
        return false;
    }

    public static boolean isFocus(int isattention){
        return isattention == isattention_yes;
    }


    /**
     * 是否是自己
     *
     * @param relationflag
     */
    public static boolean isSelf(int relationflag) {
        if ((relation_type_self & relationflag) == relation_type_self) {
            return true;
        }
        return false;
    }


    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

    public int getIsshield() {
        return isshield;
    }

    public void setIsshield(int isshield) {
        this.isshield = isshield;
    }

    public int getIsfans() {
        return isfans;
    }

    public void setIsfans(int isfans) {
        this.isfans = isfans;
    }

    public int getIsconceal() {
        return isconceal;
    }

    public void setIsconceal(int isconceal) {
        this.isconceal = isconceal;
    }

    public int getRelationflag() {
        return relationflag;
    }

    public void setRelationflag(int relationflag) {
        this.relationflag = relationflag;
    }
}
