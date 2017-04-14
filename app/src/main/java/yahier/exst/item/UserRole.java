package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class UserRole implements Serializable {
    public static final int ROLEID_COMMON = -1;//游客
    public static final int ROLEID_NOT_MASTER = 0; //未拜师普通用户
    public static final int ROLEID_HAS_MASTER = 1; //已拜师普通用户
    public static final int ROLEID_SELLER = 2; //卖家
    public static final int ROLEID_MASTER = 4; //大酋长

//    -1	游客
//    0	未激活（未拜师用户
//    1	普通（已拜师）
//     3卖家
//    5	大酋长非卖家
//    7	大酋长且卖家


    public final static int roleFlagCommonHasMaster = 1;

    /**
     * 是否卖家
     * @param roleflag
     * @return
     */
    public static boolean isSeller(int roleflag){
        if ((roleflag & ROLEID_SELLER) == ROLEID_SELLER){
            return true;
        }
        return false;
    }

    /**
     * 是否临时游客
     * @param roleflag
     * @return
     */
    public static boolean isTemp(int roleflag){
        if (roleflag == ROLEID_COMMON){
            return true;
        }
        return false;
    }

    /**
     * 是否未拜师普通用户
     * @param roleflag
     * @return
     */
    public static boolean isNotMaster(int roleflag){
        if (roleflag == ROLEID_NOT_MASTER ){
            return true;
        }
        return false;
    }

    /**
     * 是否已拜师普通用户
     * @param roleflag
     * @return
     */
    public static boolean isHasMaster(int roleflag){
        if ((roleflag & ROLEID_HAS_MASTER) == ROLEID_HAS_MASTER){
            return true;
        }
        return false;
    }

    /**
     * 是否大酋长100    4
     * @param roleflag 111    7 / 101    5
     * @return
     */
    public static boolean isMaster(int roleflag){
        if ((roleflag & ROLEID_MASTER) == ROLEID_MASTER){
            return true;
        }
        return false;
    }

}
