package yahier.exst.model;

/**
 * Created by Administrator on 2016/12/27.
 */

public class RedpacketOrder {

    public String redpacketid;
    public PrePayView prepayview;

    public static class PrePayView {
        public int prepaystate;
        public String orderno;
        public String orderpayno;
        public String orderthreepartytxt;
        public double payfee;
        public String weixinjsonparameters;
        public String msg;
    }

}
