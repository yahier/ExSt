package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/29.
 */

public class WithdrawAccount implements Serializable {

    public static final int BIND_YES = 1;
    public static final int BIND_NO = 0;

    public static final int ACCOUNT_TYPE_WECHAT = 1;

    public String bindingid;
    public String userid;
    public int accounttype;
    public int isbound;
    public String displayname;

}
