package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/29.
 */

public class MoneyFlowItem implements Serializable {

    public String amount;
    public int id;
    public String remark;
    public String exremark;
    public long createtime;
    public int optype;
    public String currencytype;

}
