package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class UserAd implements Serializable {

    public static final int STATE_NOT_ADER = -1;//非广告主
    public static final int STATE_NORMAL = 0; //广告正在投放中
    public static final int STATE_NONE = 1; // 1-没有投放广告
    public static final int STATE_REVIEWING = 2; //2-有广告订单在审核

    public int state;
    public Ad adview;
    public String msg;

}
