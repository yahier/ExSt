package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class YunHongbaoStatistics implements Serializable {
    int Total;
    int Taken;
    String Amount;
    String TakenAmount;
    String TimeLength;
    String MyAmount;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public String getMyAmount() {
        return MyAmount;
    }

    public void setMyAmount(String myAmount) {
        MyAmount = myAmount;
    }

    public String getTimeLength() {
        return TimeLength;
    }

    public void setTimeLength(String timeLength) {
        TimeLength = timeLength;
    }

    public String getTakenAmount() {
        return TakenAmount;
    }

    public void setTakenAmount(String takenAmount) {
        TakenAmount = takenAmount;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public int getTaken() {
        return Taken;
    }

    public void setTaken(int taken) {
        Taken = taken;
    }
}
