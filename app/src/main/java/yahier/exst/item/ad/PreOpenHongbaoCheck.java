package yahier.exst.item.ad;

/**
 * Created by Administrator on 2016/9/29.
 * 抢红包前的验证
 */

public class PreOpenHongbaoCheck {
    int canreceive;//可否抢红包 1-可,0-否
    int takencount;//当天已抢红包次数
    String message;//不可抢红包原因
    String denyreason;
    public final static String denyreasonNo = "0";//正常
    public final static String denyreasonNoFreeTime = "10";
    public final static String denyreasonRpExpire = "30";
    public final static String denyreasonPicked = "40";
    public final static String denyreasonRpOver = "50";
    public final static String denyreasonOthers = "99";
    public final static String denyreasonNoPermission = "-10";//无权限
    public final static String denyreasonUnlockMax = "21";//可解锁最大领取权限


    //不可抢原因（正常=0, 免费次数已用完 = 10,领取次数已用玩 = 20,红包已过期=30,红包已领取=40,红包已领取完了=50,其他=99）

    public final static int canreceiveYes = 1;
    public final static int canreceiveNo = 0;


    public int getCanreceive() {
        return canreceive;
    }

    public void setCanreceive(int canreceive) {
        this.canreceive = canreceive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTakencount() {
        return takencount;
    }

    public void setTakencount(int takencount) {
        this.takencount = takencount;
    }

    public String getDenyreason() {
        return denyreason;
    }

    public void setDenyreason(String denyreason) {
        this.denyreason = denyreason;
    }
}
