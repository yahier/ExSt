package yahier.exst.api.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/9 0009.
 */
public class GiftAnimInfo implements Serializable {
   private int res;//礼物图片资源id
    private int duration; //每帧间隔时间

    public GiftAnimInfo(){}
    public GiftAnimInfo(int res, int duration){
        this.res = res;
        this.duration = duration;
    }
    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
