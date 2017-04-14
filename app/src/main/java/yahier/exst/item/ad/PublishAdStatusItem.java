package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * 广告投放状态
 * Created by Administrator on 2016/9/25 0025.
 */

public class PublishAdStatusItem implements Serializable {

    /**
     * status : 1 ;投放申请单状态，-1 无，0-待审核，1-同意，2-不同意
     * canapply : 0 ;能否申请投放，0-否，1-是
     * msg : 您投放的广告正在审核中，请耐心等待;canapply为0，则显示该消息
     */

    private int status;
    private int canapply;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCanapply() {
        return canapply;
    }

    public void setCanapply(int canapply) {
        this.canapply = canapply;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
